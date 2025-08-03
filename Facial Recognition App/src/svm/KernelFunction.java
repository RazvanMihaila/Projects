package svm;

/**
 * Interfață pentru funcțiile de kernel utilizate în clasificatorul SVM.
 * Kernelurile definesc produsul scalar într-un spațiu transformabil pentru separabilitate nelineară.
 * Exemple de kernel: liniar, sigmoid, RBF etc.
 */
public interface KernelFunction {
	
	/**
	 * Calculează valoarea funcției de kernel între două vectori.
	 *
	 * @param x primul vector de intrare
	 * @param y al doilea vector de intrare
	 * @return rezultatul produsului scalar în spațiul kernelului
	 */
    double compute(double[] x, double[] y);
}
