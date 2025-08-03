package svm;
import java.io.Serializable;

/**
 * Implementare a funcției de kernel sigmoid pentru clasificatorul SVM.
 * Această funcție are forma: K(x, y) = tanh(α * <x, y> + coef0)
 * Este utilizată pentru separabilitate nelineară în spațiul caracteristic.
 */
public class SigmoidKernel implements KernelFunction, Serializable {
    private double alpha;
    private double coef0;

	/**
	 * Creează un kernel sigmoid cu parametrii dați.
	 *
	 * @param alpha coeficientul de scalare aplicat produsului scalar <x, y>
	 * @param coef0 termenul de bias adăugat înainte de aplicarea funcției tangente hiperbolice
	 */
    public SigmoidKernel(double alpha, double coef0) {
        this.alpha = alpha;
        this.coef0 = coef0;
    }

	/**
	 * Calculează valoarea funcției de kernel sigmoid între doi vectori.
	 * Formula: tanh(α * <x, y> + coef0)
	 *
	 * @param x primul vector
	 * @param y al doilea vector
	 * @return valoarea scalară a kernelului sigmoid
	 */
    @Override
    public double compute(double[] x, double[] y) {
        double dot = 0;
        for (int i = 0; i < x.length; i++) dot += x[i] * y[i];
        return Math.tanh(alpha * dot + coef0);
    }
}
