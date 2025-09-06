package decimal.operations.elementaryExtensions;

import static decimal.Decimal.ONE;
import static decimal.Decimal.ZERO;
import static decimal.operations.elementaryExtensions.Exponentiation.*;

import java.math.MathContext;

import decimal.Decimal;

public class RootExtraction {
	
    /**
     * Private constructor to prevent instantiation of this utility class.
     * <p>
     * Since {@code RootExtraction} is a static utility holder, it should 
     * never be instantiated. Any attempt to do so will throw an 
     * {@link AssertionError}.
     * </p>
     *
     * @throws AssertionError always, since this constructor is not meant 
     *                        to be called
     */
	private RootExtraction() {
		throw new AssertionError("No instances for you!");
	}
	
    /**
     * Internal helper for computing integer-degree roots using Halley’s method.
     * <p>
     * This method is not part of the public API and is intended solely for use
     * within the root extraction routines of this class. It applies Halley’s
     * iterative method to approximate the n-th root of a given {@code radicand}
     * with quadratic convergence under the provided {@link MathContext}.
     * </p>
     *
     * @param radicand the value whose root is being extracted
     * @param degree   the integer degree of the root
     * @param context  the {@link MathContext} specifying precision and rounding
     * @return the computed root approximation
     */
	private static Decimal integerRootExtraction(Decimal radicand, Decimal degree, MathContext context) {
		Decimal subtract = degree.subtract(ONE);
		Decimal add = degree.add(ONE);
		
		Decimal result = ONE;
		while (true) {
			Decimal numerator = subtract.multiply(exponentiation(result, degree, context)).add(add.multiply(radicand, context), context);
			Decimal denominator = add.multiply(exponentiation(result, degree, context)).add(subtract.multiply(radicand, context), context);
			Decimal fraction = numerator.divide(denominator, context);
			Decimal guess = result.multiply(fraction, context);
			
			if (guess.equals(result)) break;
			result = guess;
		}
		return result;
	}
	
    /**
     * Internal helper for computing real (non-integer) roots by definition.
     * <p>
     * This method is not part of the public API and is intended only for use
     * inside the root extraction routines of this class. It calculates the
     * root of a given {@code radicand} with a real (non-integer) {@code degree}
     * using the definition {@code a^(1/n)}. Because it delegates to general
     * exponentiation, results may be less accurate than the iterative
     * integer-root method.
     * </p>
     *
     * @param radicand the value whose root is being extracted
     * @param degree   the real (non-integer) degree of the root
     * @param context  the {@link MathContext} specifying precision and rounding
     * @return the computed root approximation
     */
	private static Decimal realRootExtraction(Decimal radicand, Decimal degree, MathContext context) {
		return exponentiation(radicand, ONE.divide(degree, context), context);
	}
	
    /**
     * Computes the n-th root of a {@code radicand} with the given {@code degree}.
     * <p>
     * This is the only publicly exposed method of the class. When the degree
     * is an integer, it is generally preferred over direct exponentiation
     * since it uses an iterative method (Halley’s method) for improved
     * stability. For non-integer degrees, this method behaves the same as
     * calling exponentiation directly.
     * </p>
     *
     * <h2>Definition</h2>
     * <ul>
     *   <li>If {@code radicand = 0} and {@code degree > 0}, returns 0.</li>
     *   <li>If {@code degree} is an integer:
     *     <ul>
     *       <li>If {@code radicand > 0} and {@code degree > 0}, returns the integer root.</li>
     *       <li>If {@code radicand > 0} and {@code degree < 0}, returns the reciprocal of the positive-degree root.</li>
     *       <li>If {@code radicand < 0} and {@code degree} is odd, returns the corresponding negative root.</li>
     *     </ul>
     *   </li>
     *   <li>If {@code degree} is non-integer and {@code radicand > 0}, computes
     *       {@code radicand^(1/degree)} using exponentiation.</li>
     * </ul>
     *
     * <p>
     * In all other cases (e.g., even roots of negative numbers, zero to a
     * nonpositive degree, non-integer degree with negative radicand), an
     * {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param radicand the value whose root is to be extracted
     * @param degree   the degree of the root (integer or real)
     * @param context  the {@link MathContext} specifying precision and rounding
     * @return the computed root
     *
     * @throws IllegalArgumentException if the input combination is mathematically undefined
     */
	public static Decimal rootExtraction(Decimal radicand, Decimal degree, MathContext context) {
		if (radicand.equals(ZERO) && degree.greaterThan(ZERO))
			return ZERO;
		else if (degree.isInteger()) {
			if (radicand.greaterThan(ZERO)) {
				if (degree.greaterThan(ZERO))
					return integerRootExtraction(radicand, degree, context);
				else if (degree.lessThan(ZERO))
					return ONE.divide(integerRootExtraction(radicand, degree.negate(), context), context);
			} else if (radicand.lessThan(ZERO) && degree.isOdd())
				return integerRootExtraction(radicand, degree, context).negate();
		} 
		else if (radicand.greaterThan(ZERO))
			realRootExtraction(radicand, degree, context);
		throw new IllegalArgumentException(String.format("undefined for radicand %s, degree %s", radicand, degree));
	}

}
