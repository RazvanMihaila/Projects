package camera;

import face.HeadDetector;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Clasă care capturează imagini ale unei persoane de la camera web,
 * folosind un detector de cap pentru a poziționa pătratul de selecție.
 * Imaginile detectate sunt salvate într-un folder asociat cu pseudonimul.
 */
public class ImageCollector {
    private final String name;
    private final int maxImages;
    private final Consumer<Integer> onProgress;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public ImageCollector(String name, int count, Consumer<Integer> onProgress) {
        this.name = name;
        this.maxImages = count;
        this.onProgress = onProgress;
    }

    public void run() throws Exception {
        String outputFolder = "data/collected/" + name;
        new File(outputFolder).mkdirs();

        HeadDetector detector = new HeadDetector("data/head_detector.model");
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            throw new IOException("Camera nu a putut fi pornită.");
        }

        Mat mat = new Mat();
        int count = 0;
        int frameCount = 0;

        while (count < maxImages) {
            if (camera.read(mat)) {
                BufferedImage bimg = matToBufferedImage(mat);

                Image scaledImage = bimg.getScaledInstance(320, 240, Image.SCALE_SMOOTH);
                BufferedImage resized = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
                resized.getGraphics().drawImage(scaledImage, 0, 0, null);

                Rectangle faceRect = null;
                if (frameCount % 30 == 0) {
                    faceRect = detector.detectHeadRectangle(resized);
                    if (faceRect != null) {
                        double scaleX = (double) bimg.getWidth() / resized.getWidth();
                        double scaleY = (double) bimg.getHeight() / resized.getHeight();
                        faceRect = new Rectangle(
                                (int) (faceRect.x * scaleX),
                                (int) (faceRect.y * scaleY),
                                (int) (faceRect.width * scaleX),
                                (int) (faceRect.height * scaleY)
                        );
                    }
                }

                if (faceRect != null) {
                    Imgproc.rectangle(mat,
                            new org.opencv.core.Point(faceRect.x, faceRect.y),
                            new org.opencv.core.Point(faceRect.x + faceRect.width, faceRect.y + faceRect.height),
                            new Scalar(0, 255, 0), 2);

                    if (faceRect.x >= 0 && faceRect.y >= 0 &&
                            faceRect.x + faceRect.width <= bimg.getWidth() &&
                            faceRect.y + faceRect.height <= bimg.getHeight()) {
                        BufferedImage head = bimg.getSubimage(faceRect.x, faceRect.y, faceRect.width, faceRect.height);
                        BufferedImage scaled = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                        scaled.getGraphics().drawImage(head, 0, 0, 128, 128, null);

                        String filename = outputFolder + "/" + name + "_" + count + ".jpg";
                        ImageIO.write(scaled, "jpg", new File(filename));
                        count++;
                        if (onProgress != null) onProgress.accept(count);

                        Thread.sleep(500);
                    }
                }

                HighGui.imshow("Captura Live", mat);
                if (HighGui.waitKey(1) == 27) break;
                frameCount++;
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
        System.out.println("Captura completă pentru: " + name);
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_3BYTE_BGR;
        if (mat.channels() == 1) type = BufferedImage.TYPE_BYTE_GRAY;

        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] b = new byte[bufferSize];
        mat.get(0, 0, b);
        BufferedImage img = new BufferedImage(mat.cols(), mat.rows(), type);
        img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), b);
        return img;
    }

    // Main doar pentru testare manuală (poate fi șters în aplicația finală)
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Folosire: java camera.ImageCollector <pseudonim> <numar_imagini>");
            return;
        }

        String pseudonim = args[0];
        int numar = Integer.parseInt(args[1]);

        ImageCollector collector = new ImageCollector(pseudonim, numar, (progress) -> {
            System.out.println("Progres: " + progress);
        });
        collector.run();
    }
}
