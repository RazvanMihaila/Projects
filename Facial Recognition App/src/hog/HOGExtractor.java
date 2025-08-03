package hog;

import java.awt.image.BufferedImage;

/**
 * Clasă responsabilă pentru extragerea descriptorului HOG (Histogram of Oriented Gradients)
 * dintr-o imagine. Acest vector de trăsături este utilizat ulterior pentru clasificarea SVM.
 */
public class HOGExtractor {
	
	/**
	 * Extrage vectorul HOG dintr-o imagine 128x128. Imaginea este împărțită în celule 8x8,
	 * pentru fiecare celulă fiind calculat un histogramă cu 9 binuri bazată pe orientările gradientului.
	 *
	 * @param image imaginea de intrare (se presupune că are dimensiunea 128x128)
	 * @return vectorul de trăsături HOG rezultat (dimensiune fixă: 14 x 14 x 9 = 1764)
	 */
    public static double[] extract(BufferedImage image) {
        int cellSize = 8;
        int numBins = 9;

        int width = image.getWidth();
        int height = image.getHeight();
        int cellsX = width / cellSize;
        int cellsY = height / cellSize;

        double[][] magnitude = new double[height][width];
        double[][] angle = new double[height][width];

        // gradient pe axele X și Y (Sobel simplificat)
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = gray(image, x + 1, y) - gray(image, x - 1, y);
                int gy = gray(image, x, y + 1) - gray(image, x, y - 1);

                magnitude[y][x] = Math.sqrt(gx * gx + gy * gy);
                angle[y][x] = Math.toDegrees(Math.atan2(gy, gx));
                if (angle[y][x] < 0) angle[y][x] += 180;
            }
        }

        double[] feature = new double[cellsX * cellsY * numBins];
        int idx = 0;

        for (int cy = 0; cy < cellsY; cy++) {
            for (int cx = 0; cx < cellsX; cx++) {
                double[] hist = new double[numBins];
                for (int y = 0; y < cellSize; y++) {
                    for (int x = 0; x < cellSize; x++) {
                        int px = cx * cellSize + x;
                        int py = cy * cellSize + y;
                        double mag = magnitude[py][px];
                        double ang = angle[py][px];
                        int bin = (int) (ang / (180.0 / numBins));
                        if (bin >= numBins) bin = numBins - 1;
                        hist[bin] += mag;
                    }
                }
                for (int b = 0; b < numBins; b++) {
                    feature[idx++] = hist[b];
                }
            }
        }

        return feature;
    }

	/**
	 * Convertește un pixel color la o intensitate de gri folosind doar canalul roșu.
	 * Se presupune că imaginea este deja în tonuri de gri sau simplificată.
	 *
	 * @param img imaginea de intrare
	 * @param x coordonata X a pixelului
	 * @param y coordonata Y a pixelului
	 * @return valoarea de intensitate a pixelului (0–255)
	 */
    private static int gray(BufferedImage img, int x, int y) {
        int rgb = img.getRGB(x, y);
        return (rgb >> 16) & 0xff; // doar roșu (echivalent gri)
    }
}
