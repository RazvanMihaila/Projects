package face;

import hog.HOGExtractor;
import svm.SVMClassifier;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * Clasă responsabilă pentru recunoașterea facială live pe baza camerelor
 * și clasificatoarelor antrenate. Detectează fețele din imagine folosind
 * un detector de cap și identifică persoanele recunoscute prin clasificatoare SVM.
 */
public class LiveRecognizer {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

	/**
	 * Punctul de intrare al aplicației.
	 * Încarcă toate clasificatoarele SVM pentru persoane, inițializează camera web
	 * și pornește bucla de recunoaștere live. Pentru fiecare cadru:
	 * - detectează zonele „cap”
	 * - clasifică fiecare zonă cu toți clasificatorii disponibili
	 * - desenează un pătrat și numele persoanei recunoscute (o singură dată per persoană)
	 *
	 * @param args neutilizat
	 * @throws Exception dacă apar erori la accesarea fișierelor, cameră sau întreruperi
	 */
    public static void main(String[] args) throws Exception {
        System.out.println("Se încarcă modele...");
        Map<String, SVMClassifier> classifiers = new HashMap<>();

        File modelDir = new File("data/models");
        for (File f : modelDir.listFiles((dir, name) -> name.endsWith(".model"))) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            SVMClassifier svm = (SVMClassifier) ois.readObject();
            ois.close();

            String name = f.getName().replace("model_", "").replace(".model", "");
            classifiers.put(name, svm);
        }

        HeadDetector detector = new HeadDetector("data/head_detector.model");
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Camera nu a putut fi pornită.");
            return;
        }

        JFrame frame = new JFrame("Recunoaștere persoane");
        JLabel label = new JLabel();
        frame.setContentPane(label);
        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Mat mat = new Mat();
        while (true) {
            if (camera.read(mat)) {
                BufferedImage bimg = matToBufferedImage(mat);
                Graphics2D g = bimg.createGraphics();
                g.setColor(Color.GREEN);

                java.util.List<Rectangle> rects = detector.detectAllHeads(bimg, 0.95);
                Map<String, Rectangle> bestMatchPerPerson = new HashMap<>();

                for (Rectangle rect : rects) {
                    if (rect.x >= 0 && rect.y >= 0 &&
                            rect.x + rect.width <= bimg.getWidth() &&
                            rect.y + rect.height <= bimg.getHeight()) {

                        BufferedImage head = bimg.getSubimage(rect.x, rect.y, rect.width, rect.height);
                        BufferedImage scaled = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                        scaled.getGraphics().drawImage(head, 0, 0, 128, 128, null);

                        double[] vec = HOGExtractor.extract(scaled);

                        for (Map.Entry<String, SVMClassifier> entry : classifiers.entrySet()) {
                            if (entry.getValue().classify(vec) == 1) {
                                String name = entry.getKey();
                                if (!bestMatchPerPerson.containsKey(name)) {
                                    bestMatchPerPerson.put(name, rect);
                                }
                            }
                        }
                    }
                }

                for (Map.Entry<String, Rectangle> match : bestMatchPerPerson.entrySet()) {
                    Rectangle rect = match.getValue();
                    g.drawRect(rect.x, rect.y, rect.width, rect.height);
                    g.drawString(match.getKey(), rect.x, rect.y - 5);
                }

                g.dispose();
                label.setIcon(new ImageIcon(bimg));
                label.repaint();
            }

            Thread.sleep(100);
        }
    }

	/**
	 * Conversie din formatul OpenCV `Mat` într-un `BufferedImage` compatibil cu Swing.
	 * Această metodă permite afișarea în ferestrele grafice Java a imaginilor video
	 * provenite de la cameră.
	 *
	 * @param mat obiectul capturat de la cameră, în format OpenCV
	 * @return imaginea convertită ca `BufferedImage`
	 */
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
}
