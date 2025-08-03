package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasă utilitară pentru încărcarea imaginilor de antrenament
 * și transformarea acestora în vectori de intensitate normalizată.
 * Este folosită pentru pregătirea setului de date SVM.
 */
public class TrainingLoader {
	
	/**
	 * Încarcă toate imaginile cu extensia `.jpg` sau `.png` dintr-un folder
	 * și le convertește în vectori de trăsături normalizate (0–1).
	 *
	 * @param folder calea către directorul cu imagini
	 * @return matrice de vectori (un vector pentru fiecare imagine)
	 * @throws Exception dacă imaginile nu pot fi citite sau folderul nu există
	 */
    public static double[][] loadImages(String folder) throws Exception {
        File dir = new File(folder);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
        List<double[]> data = new ArrayList<>();

        for (File file : files) {
            BufferedImage img = ImageIO.read(file);
            double[] vec = imageToVector(img);
            data.add(vec);
        }

        return data.toArray(new double[0][]);
    }

	/**
	 * Convertește o imagine într-un vector de intensitate grayscale normalizat.
	 * Este utilizat intern pentru a transforma fiecare imagine într-o reprezentare numerică.
	 *
	 * @param img imaginea de intrare
	 * @return vectorul rezultat cu valori între 0 și 1
	 */
    private static double[] imageToVector(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        double[] vec = new double[w * h];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int gray = (rgb >> 16) & 0xff; // doar roșu, simplificat
                vec[y * w + x] = gray / 255.0; // normalizare
            }
        return vec;
    }
}
