package face;

import svm.*;
import utils.TrainingLoader;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Clasă care antrenează un clasificator SVM pentru detecția de capuri în imagini.
 * Încarcă imagini pozitive și negative din directoare predefinite, le convertește în vectori
 * și folosește algoritmul SMO pentru a învăța modelul. La final, salvează modelul rezultat
 * în fișierul `data/head_detector.model`.
 */
public class HeadDetectorTrainer {
	
	/**
	 * Punctul de intrare al aplicației. Încarcă imaginile de antrenare,
	 * construiește setul de date X și y, antrenează SVM-ul folosind SMO,
	 * apoi salvează modelul rezultat într-un fișier binar.
	 *
	 * @param args neutilizat
	 * @throws Exception dacă apar erori la citirea imaginilor sau scrierea fișierului .model
	 */
    public static void main(String[] args) throws Exception {
        double[][] pos = TrainingLoader.loadImages("train_images/positive");
        double[][] neg = TrainingLoader.loadImages("train_images/negative");

        System.out.println("Pozitive: " + pos.length + " | Negative: " + neg.length);

        int m = pos.length + neg.length;
        double[][] X = new double[m][];
        int[] y = new int[m];

        int idx = 0;
        for (double[] ex : pos) {
            X[idx] = ex;
            y[idx++] = 1;
        }
        for (double[] ex : neg) {
            X[idx] = ex;
            y[idx++] = -1;
        }

        KernelFunction kernel = new SigmoidKernel(0.01, 0.0);
        SVMClassifier svm = new SVMClassifier(X, y, kernel);

        System.out.println("Antrenare SVM...");
        long start = System.currentTimeMillis();
        SMOTrainer.train(svm, 1000, 1.0, 1e-3);
        long end = System.currentTimeMillis();
        System.out.printf("Durata antrenării: %.2f secunde%n", (end - start) / 1000.0);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/head_detector.model"));
        oos.writeObject(svm);
        oos.close();

        System.out.println("Antrenare finalizată și modelul salvat.");
    }
}
