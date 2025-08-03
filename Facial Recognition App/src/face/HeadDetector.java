package face;

import svm.SVMClassifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.awt.Rectangle;
import java.util.*;

/**
 * Clasă responsabilă pentru detecția zonelor care conțin capuri în imagini,
 * folosind un clasificator SVM binar antrenat anterior.
 * Oferă metode pentru detectarea unui singur cap sau a mai multor capuri,
 * folosind sliding window și filtrare după scor SVM.
 */
public class HeadDetector {
    private final SVMClassifier svm;
	
	/**
	 * Încarcă modelul SVM pentru detecția capului dintr-un fișier serializat.
	 *
	 * @param modelPath calea către fișierul .model cu clasificatorul antrenat
	 * @throws Exception dacă fișierul nu poate fi citit sau modelul nu poate fi deserializat
	 */
    public HeadDetector(String modelPath) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath));
        this.svm = (SVMClassifier) ois.readObject();
        ois.close();
    }
	
	/**
	 * Caută în imagine cel mai bun patch care reprezintă un cap,
	 * pe baza scorului SVM maxim.
	 *
	 * @param image Imaginea de intrare (BufferedImage)
	 * @return O subimagine 128x128 (BufferedImage) decupată din regiunea detectată
	 */
    public BufferedImage detectBestHead(BufferedImage image) {
        int winSize = 128;
        int step = 32;

        double bestScore = Double.NEGATIVE_INFINITY;
        int bestX = 0, bestY = 0;

        for (int y = 0; y <= image.getHeight() - winSize; y += step) {
            for (int x = 0; x <= image.getWidth() - winSize; x += step) {
                BufferedImage patch = image.getSubimage(x, y, winSize, winSize);
                double[] vec = imageToVector(patch);
                double score = svm.predict(vec);

                if (score > bestScore) {
                    bestScore = score;
                    bestX = x;
                    bestY = y;
                }
            }
        }

        return image.getSubimage(bestX, bestY, winSize, winSize);
    }
	
	/**
	 * Convertește o imagine într-un vector de intensități normalizate (0–1).
	 * Folosit pentru a pregăti patch-urile înainte de clasificare.
	 *
	 * @param img Imaginea de intrare (BufferedImage)
	 * @return Vectorul dublu rezultat din imagine
	 */
	private double[] imageToVector(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        double[] vec = new double[w * h];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int gray = (rgb >> 16) & 0xff;
                vec[y * w + x] = gray / 255.0;
            }
        return vec;
    }

	/**
	 * Caută zona din imagine cu cel mai mare scor SVM pentru a fi considerată un cap.
	 * Returnează pătratul cu scor maxim.
	 *
	 * @param image Imaginea de intrare
	 * @return Un obiect Rectangle ce definește zona considerată drept cap
	 */
    public Rectangle detectHeadRectangle(BufferedImage image) {
        int[] sizes = {128};
        int step = 24;

        double bestScore = Double.NEGATIVE_INFINITY;
        int bestX = 0, bestY = 0, bestSize = 128;

        for (int winSize : sizes) {
            for (int y = 0; y <= image.getHeight() - winSize; y += step) {
                for (int x = 0; x <= image.getWidth() - winSize; x += step) {
                    BufferedImage patch = image.getSubimage(x, y, winSize, winSize);
                    BufferedImage scaled = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                    scaled.getGraphics().drawImage(patch, 0, 0, 128, 128, null);
                    double[] vec = imageToVector(scaled);
                    double score = svm.predict(vec);

                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestY = y;
                        bestSize = winSize;
                    }
                }
            }
        }

        return new Rectangle(bestX, bestY, bestSize, bestSize);
    }

	/**
	 * Caută toate pătratele cap în imagine care au un scor SVM ≥ minScore.
	 * Aplică Non-Maximum Suppression pentru a evita pătratele suprapuse.
	 *
	 * @param image Imaginea de intrare
	 * @param minScore Pragul minim de scor SVM pentru a accepta o zonă ca posibil cap
	 * @return Listă de obiecte Rectangle ce reprezintă capuri detectate
	 */
    public List<Rectangle> detectAllHeads(BufferedImage image, double minScore) {
        int[] sizes = {128};
        int step = 24;
        List<ScoredRect> scoredRects = new ArrayList<>();

        for (int winSize : sizes) {
            for (int y = 0; y <= image.getHeight() - winSize; y += step) {
                for (int x = 0; x <= image.getWidth() - winSize; x += step) {
                    BufferedImage patch = image.getSubimage(x, y, winSize, winSize);
                    BufferedImage scaled = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                    scaled.getGraphics().drawImage(patch, 0, 0, 128, 128, null);
                    double[] vec = imageToVector(scaled);
                    double score = svm.predict(vec);

                    if (score >= minScore) {
                        scoredRects.add(new ScoredRect(new Rectangle(x, y, winSize, winSize), score));
                    }
                }
            }
        }

        scoredRects.sort((a, b) -> Double.compare(b.score, a.score));
        List<Rectangle> finalRects = new ArrayList<>();

        for (ScoredRect candidate : scoredRects) {
            boolean overlaps = false;
            for (Rectangle existing : finalRects) {
                if (computeIoU(existing, candidate.rect) > 0.3) {
                    overlaps = true;
                    break;
                }
            }
            if (!overlaps) finalRects.add(candidate.rect);
        }

        return finalRects;
    }

	/**
	 * Calculează scorul de suprapunere (Intersection over Union) dintre două pătrate.
	 * Folosit pentru eliminarea pătratelor suprapuse în NMS.
	 *
	 * @param r1 primul pătrat
	 * @param r2 al doilea pătrat
	 * @return scorul de suprapunere (valoare între 0 și 1)
	 */
    private double computeIoU(Rectangle r1, Rectangle r2) {
        int x1 = Math.max(r1.x, r2.x);
        int y1 = Math.max(r1.y, r2.y);
        int x2 = Math.min(r1.x + r1.width, r2.x + r2.width);
        int y2 = Math.min(r1.y + r1.height, r2.y + r2.height);

        int intersectionArea = Math.max(0, x2 - x1) * Math.max(0, y2 - y1);
        int unionArea = r1.width * r1.height + r2.width * r2.height - intersectionArea;

        return unionArea == 0 ? 0 : (double) intersectionArea / unionArea;
    }
	
	/**
	 * Clasă internă care asociază un pătrat (Rectangle) cu scorul său SVM.
	 * Utilizată pentru sortare și filtrare în procesul de detecție.
	 */
    private static class ScoredRect {
        Rectangle rect;
        double score;

        ScoredRect(Rectangle rect, double score) {
            this.rect = rect;
            this.score = score;
        }
    }
}
