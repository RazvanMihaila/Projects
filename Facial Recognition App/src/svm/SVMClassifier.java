package svm;

import java.io.Serializable;

/**
 * Clasă care implementează un clasificator SVM binar antrenabil.
 * Acceptă orice funcție de kernel și memorează coeficienții alpha și bias-ul.
 * Poate fi folosit pentru clasificare și predicții scor.
 */
public class SVMClassifier implements Serializable {
    public double[][] X;
    public int[] y;
    public double[] alpha;
    public double b;
    public KernelFunction kernel;

	/**
	 * Creează un clasificator SVM inițializat cu datele de antrenare și kernelul dat.
	 *
	 * @param X matricea cu vectorii de antrenare (un vector per exemplu)
	 * @param y vectorul de etichete (+1 sau -1)
	 * @param kernel funcția de kernel care va fi folosită la predicție
	 */
    public SVMClassifier(double[][] X, int[] y, KernelFunction kernel) {
        this.X = X;
        this.y = y;
        this.kernel = kernel;
        this.alpha = new double[X.length];
        this.b = 0;
    }

	/**
	 * Calculează scorul SVM (valoarea brută a funcției de decizie) pentru un vector de intrare.
	 * Nu aplică nicio funcție de semn – doar suma ponderată a kernelului.
	 *
	 * @param x vectorul de intrare pentru care se face predicția
	 * @return valoarea reală a funcției SVM (poate fi pozitivă sau negativă)
	 */
    public double predict(double[] x) {
        double sum = 0;
        for (int i = 0; i < X.length; i++) {
            if (alpha[i] > 0) {
                sum += alpha[i] * y[i] * kernel.compute(X[i], x);
            }
        }
        return sum + b;
    }

	/**
	 * Clasifică un vector de intrare folosind pragul 0 aplicat funcției SVM.
	 *
	 * @param x vectorul de intrare
	 * @return +1 dacă scorul este ≥ 0, altfel -1
	 */
    public int classify(double[] x) {
        return predict(x) >= 0 ? 1 : -1;
    }
}
