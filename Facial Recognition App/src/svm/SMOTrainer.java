package svm;

import java.util.Random;

/**
 * Clasă care implementează algoritmul Sequential Minimal Optimization (SMO)
 * pentru antrenarea unui clasificator SVM. Algoritmul optimizează perechi de variabile α
 * pentru a satisface condițiile KKT și a găsi frontul de decizie optim.
 */
public class SMOTrainer {

	/**
	 * Antrenează clasificatorul SVM furnizat folosind algoritmul SMO.
	 *
	 * @param svm obiectul SVMClassifier care conține datele de antrenare (X, y), kernelul și variabilele alpha
	 * @param maxIter numărul maxim de iterații fără modificări ale α până la oprirea algoritmului
	 * @param C parametru de regularizare (limita superioară pentru α)
	 * @param tol toleranța utilizată pentru a decide dacă o constrângere KKT este încălcată
	 */
    public static void train(SVMClassifier svm, int maxIter, double C, double tol) {
        int m = svm.X.length;
        double[] alpha = svm.alpha;
        double b = svm.b;
        int[] y = svm.y;
        double[][] X = svm.X;
        KernelFunction kernel = svm.kernel;

        double[] E = new double[m];
        for (int i = 0; i < m; i++) {
            E[i] = svm.predict(X[i]) - y[i];
        }

        int iter = 0;
        Random rand = new Random();

        while (iter < maxIter) {
            int alphaChanged = 0;
            for (int i = 0; i < m; i++) {
                double Ei = E[i];
                if ((y[i]*Ei < -tol && alpha[i] < C) || (y[i]*Ei > tol && alpha[i] > 0)) {
                    int j = i;
                    while (j == i) j = rand.nextInt(m);

                    double Ej = E[j];
                    double alpha_i_old = alpha[i];
                    double alpha_j_old = alpha[j];

                    double L, H;
                    if (y[i] != y[j]) {
                        L = Math.max(0, alpha[j] - alpha[i]);
                        H = Math.min(C, C + alpha[j] - alpha[i]);
                    } else {
                        L = Math.max(0, alpha[i] + alpha[j] - C);
                        H = Math.min(C, alpha[i] + alpha[j]);
                    }

                    if (L == H) continue;

                    double eta = 2 * kernel.compute(X[i], X[j]) - kernel.compute(X[i], X[i]) - kernel.compute(X[j], X[j]);
                    if (eta >= 0) continue;

                    alpha[j] -= y[j] * (Ei - Ej) / eta;
                    if (alpha[j] > H) alpha[j] = H;
                    else if (alpha[j] < L) alpha[j] = L;

                    if (Math.abs(alpha[j] - alpha_j_old) < 1e-5) continue;

                    alpha[i] += y[i]*y[j]*(alpha_j_old - alpha[j]);

                    double b1 = b - Ei
                        - y[i] * (alpha[i] - alpha_i_old) * kernel.compute(X[i], X[i])
                        - y[j] * (alpha[j] - alpha_j_old) * kernel.compute(X[i], X[j]);

                    double b2 = b - Ej
                        - y[i] * (alpha[i] - alpha_i_old) * kernel.compute(X[i], X[j])
                        - y[j] * (alpha[j] - alpha_j_old) * kernel.compute(X[j], X[j]);

                    if (0 < alpha[i] && alpha[i] < C) b = b1;
                    else if (0 < alpha[j] && alpha[j] < C) b = b2;
                    else b = (b1 + b2) / 2;

                    E[i] = svm.predict(X[i]) - y[i];
                    E[j] = svm.predict(X[j]) - y[j];

                    alphaChanged++;
                }
            }

            if (alphaChanged == 0)
                iter++;
            else
                iter = 0;
        }

        svm.b = b;
        svm.alpha = alpha;
    }
}
