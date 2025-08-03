package svm;

import hog.FeatureSetBuilder;
import hog.FeatureSetBuilder.DataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Clasă care antrenează un clasificator SVM pentru o persoană specifică,
 * folosind imagini din folderul `data/collected`. Setul de date este construit
 * astfel încât persoana dată este etichetată cu +1, iar restul cu -1.
 * La final, modelul este salvat ca fișier `.model` pentru a fi utilizat la recunoaștere.
 */
public class PersonTrainer {
	
	/**
	 * Punctul de intrare în aplicație. Primește ca argument pseudonimul unei persoane,
	 * încarcă toate imaginile din `data/collected`, extrage vectorii HOG,
	 * antrenează un clasificator SVM pentru această persoană și salvează modelul.
	 *
	 * @param args un singur parametru: pseudonimul persoanei (ex: alex)
	 * @throws Exception dacă apar erori la citirea datelor sau la salvarea modelului
	 */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Folosire: java svm.PersonTrainer <pseudonim>");
            return;
        }

        String pseudonim = args[0];
        File rootDir = new File("data/collected");

        System.out.println("Construim set de date pentru: " + pseudonim);
        DataSet ds = FeatureSetBuilder.loadForTraining(rootDir, pseudonim);

        System.out.println("Antrenăm SVM...");
        KernelFunction kernel = new SigmoidKernel(0.01, 0.0);
        SVMClassifier svm = new SVMClassifier(ds.X, ds.y, kernel);
        SMOTrainer.train(svm, 1000, 1.0, 1e-3);

        // Salvează modelul
        new File("data/models").mkdirs();
        FileOutputStream fos = new FileOutputStream("data/models/model_" + pseudonim + ".model");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(svm);
        oos.close();

        System.out.println("Model salvat: data/models/model_" + pseudonim + ".model");
    }
}
