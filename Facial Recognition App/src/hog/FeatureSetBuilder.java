package hog;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Clasă care construiește setul de date pentru antrenarea clasificatorului SVM al unei persoane.
 * Încarcă imaginile pentru toate persoanele, extrage descriptorii HOG și etichetează
 * cu +1 pentru persoana țintă și -1 pentru toate celelalte.
 */
public class FeatureSetBuilder {
	
	/**
	 * Structură simplă care conține:
	 * - matricea X: vectorii de trăsături extrași din imagini
	 * - vectorul y: etichete +1 (persoană țintă), -1 (alte persoane)
	 */
    public static class DataSet implements Serializable {
        public double[][] X; // vectori HOG
        public int[] y;      // etichete
    }

	/**
	 * Construiește setul de date de antrenare pentru o persoană anume.
	 * Încarcă toate imaginile din subfolderele directorului dat,
	 * extrage descriptorii HOG și setează etichete corespunzătoare.
	 *
	 * @param rootDir directorul principal care conține câte un subfolder per persoană
	 * @param targetPerson numele persoanei țintă (va fi etichetată cu +1)
	 * @return obiect DataSet cu vectorii de trăsături și etichete
	 * @throws IOException dacă nu există imagini valide sau folderul nu poate fi accesat
	 */
    public static DataSet loadForTraining(File rootDir, String targetPerson) throws IOException {
        List<double[]> Xlist = new ArrayList<>();
        List<Integer> ylist = new ArrayList<>();

        File[] persons = rootDir.listFiles(File::isDirectory);
        if (persons == null) throw new IOException("Nu există subfoldere în " + rootDir);

        for (File personDir : persons) {
            String personName = personDir.getName();
            int label = personName.equals(targetPerson) ? +1 : -1;

            File[] images = personDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));
            if (images == null) continue;
            Arrays.sort(images); // sortare stabilă

            for (File imgFile : images) {
                BufferedImage img = ImageIO.read(imgFile);
                if (img == null || img.getWidth() != 128 || img.getHeight() != 128) {
                    System.out.println("Imagine ignorată (nu este 128x128): " + imgFile.getName());
                    continue;
                }
                double[] features = HOGExtractor.extract(img);
                Xlist.add(features);
                ylist.add(label);
            }
        }

        if (Xlist.isEmpty()) {
            throw new IOException("Nu s-au găsit imagini valide în " + rootDir.getPath());
        }

        DataSet dataset = new DataSet();
        dataset.X = Xlist.toArray(new double[0][]);
        dataset.y = ylist.stream().mapToInt(Integer::intValue).toArray();
        return dataset;
    }

	/**
	 * Demonstrație de utilizare pentru metoda `loadForTraining`.
	 * Încarcă imaginile pentru o persoană, extrage datele și afișează statistici.
	 *
	 * @param args neutilizat
	 * @throws Exception dacă apar erori la citirea imaginilor
	 */
    public static void main(String[] args) throws Exception {
        File root = new File("data/collected");
        String persoana = "alex";

        DataSet ds = loadForTraining(root, persoana);

        System.out.println("Total imagini: " + ds.X.length);
        System.out.println("Dimensiune vector HOG: " + ds.X[0].length);
        System.out.println("Etichete: " + Arrays.toString(Arrays.copyOf(ds.y, Math.min(10, ds.y.length))));
    }
}
