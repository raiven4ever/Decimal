package decimal.helper;

import java.math.MathContext;

import decimal.Decimal;

/**
 * Utility class for supplying successive factorial values of {@link Decimal}.
 *
 * <p>This class maintains both the current {@code n} and its factorial {@code n!},
 * allowing iteration over factorial values without recomputing from scratch.
 * It supports both <em>pre-increment</em> and <em>post-increment</em> styles
 * of advancing.</p>
 *
 * <p><strong>Developer notes:</strong></p>
 * <ul>
 *   <li>If the start is 0, the initial value is {@code 0! = 1}.</li>
 *   <li>If the start is 3, the initial value is {@code 3! = 6}.</li>
 *   <li>Designed as a helper for computing series expansions that require
 *       successive factorial terms.</li>
 * </ul>
 */
public class FactorialSupplier {

	/**
	 * The current factorial value.
	 * <p>Initialized to {@code start!} in the constructor.</p>
	 */
	private Decimal value;

	/**
	 * Returns the current factorial value without advancing.
	 *
	 * @return the current factorial
	 */
	public Decimal currentFactorial() {
		return value;
	}

	/**
	 * The current {@code n} associated with {@code n!}.
	 */
	private Decimal n;

	/**
	 * Returns the current {@code n}.
	 *
	 * @return the current value of {@code n}
	 */
	public Decimal currentN() {
		return n;
	}

	/**
	 * The math context used for multiplications.
	 */
	private MathContext context;

	/**
	 * Creates a new {@code FactorialSupplier} starting at the given value.
	 *
	 * <p>If {@code start = 0}, the initial factorial is {@code 0! = 1}.
	 * Otherwise the initial factorial is {@code start!}.</p>
	 *
	 * @param start   the starting {@code n}
	 * @param context the math context to use for multiplications
	 */
	public FactorialSupplier(Decimal start, MathContext context) {
		n = start;
		this.context = context;
		value = n.factorial(); // uses Decimal.factorial()
	}

	/**
	 * Creates a new {@code FactorialSupplier} starting at the given value,
	 * using {@link MathContext#UNLIMITED} as the default context.
	 *
	 * <p>If {@code start = 0}, the initial factorial is {@code 0! = 1}.
	 * Otherwise the initial factorial is {@code start!}.</p>
	 *
	 * @param start the starting {@code n}
	 */
	public FactorialSupplier(Decimal start) {
		n = start;
		context = MathContext.UNLIMITED;
		value = n.factorial();
	}

	/**
	 * Increments {@code n} by one and multiplies {@code value} by the new {@code n}.
	 *
	 * <p><strong>Developer note:</strong> This helper method exists only to bundle
	 * the logic of advancing {@code n} and updating the factorial value. It is used
	 * internally by {@code nextPre} and {@code nextPost} methods.</p>
	 */
	private void factorialIncrement() {
		n = n.add(Decimal.ONE); // could be optimized with a dedicated increment
		value = value.multiply(n, context);
	}

	/**
	 * Returns the current factorial, then advances to the next factorial.
	 *
	 * <p>Equivalent to "post-return, pre-compute".</p>
	 *
	 * @return the current factorial before advancing
	 */
	public Decimal nextPre() {
		Decimal toReturn = value;
		factorialIncrement();
		return toReturn;
	}

	/**
	 * Returns the current factorial, then advances by the given number of steps.
	 *
	 * @param steps the number of factorial steps to advance
	 * @return the current factorial before advancing
	 */
	public Decimal nextPre(int steps) {
		Decimal toReturn = value;
		for (int i = 0; i < steps; i++) {
			factorialIncrement();
		}
		return toReturn;
	}

	/**
	 * Advances to the next factorial, then returns it.
	 *
	 * <p>Equivalent to "pre-compute, post-return".</p>
	 *
	 * @return the next factorial value
	 */
	public Decimal nextPost() {
		factorialIncrement();
		return value;
	}

	/**
	 * Advances by the given number of steps, then returns the resulting factorial.
	 *
	 * @param steps the number of factorial steps to advance
	 * @return the factorial after advancing
	 */
	public Decimal nextPost(int steps) {
		for (int i = 0; i < steps; i++) {
			factorialIncrement();
		}
		return value;
	}

}
