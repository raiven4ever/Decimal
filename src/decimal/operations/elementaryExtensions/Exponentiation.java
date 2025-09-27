package decimal.operations.elementaryExtensions;

import static decimal.Decimal.ONE;
import static decimal.Decimal.TWO;
import static decimal.Decimal.ZERO;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.function.BiFunction;

import decimal.Decimal;
import decimal.Decimal.BoundType;
import decimal.helpers.FactorialSupplier;
import decimal.helpers.Summation;

/**
 * Utility class providing methods for exponentiation within the
 * {@code decimal.operations.elementaryExtensions} package.
 * <p>
 * This class implements various forms of exponentiation for the
 * {@link decimal.Decimal} type, including:
 * <ul>
 *   <li>Integer exponentiation using fast exponentiation (square-and-multiply)</li>
 *   <li>Real exponentiation via {@code exp} and {@code ln} functions,
 *       implemented with Taylor series and atanh-based logarithm expansion</li>
 *   <li>Range reduction techniques using {@code ln(2)} for stability</li>
 * </ul>
 * </p>
 *
 * <p>
 * The primary entry point is {@link #exponentiation(Decimal, Decimal, MathContext)},
 * which applies guardrails to handle defined and undefined cases:
 * <ul>
 *   <li>For integer exponents: supports zero, positive, and negative values</li>
 *   <li>For real exponents: only defined when the base is positive</li>
 *   <li>Throws {@link IllegalArgumentException} for undefined inputs
 *       (e.g. negative base with non-integer exponent, or 0⁰)</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class cannot be instantiated.
 * </p>
 *
 * @implNote
 * Internal helper methods such as {@code exp}, {@code ln}, and {@code ln2}
 * are not currently exposed and may be refactored into their own utility
 * classes in the future.
 *
 * @see decimal.Decimal
 * @see java.math.MathContext
 */
public class Exponentiation {

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 * <p>
	 * Calling this constructor will always result in an
	 * {@link AssertionError}, ensuring the class cannot be instantiated.
	 * </p>
	 *
	 * @throws AssertionError always, since this class should not be instantiated
	 */
	private Exponentiation() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * Computes {@code base^exponent} where the exponent is an integer.
	 * <p>
	 * This method uses two strategies depending on the size of the exponent:
	 * <ul>
	 *   <li>If the exponent is within {@link Decimal#INTEGER_MAX_VALUE}, it delegates
	 *       to {@link java.math.BigDecimal#pow(int, MathContext)} for efficiency.</li>
	 *   <li>If the exponent exceeds that range, it falls back to an iterative
	 *       square-and-multiply algorithm using bit-shifting and odd/even checks.</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * This method is marked private and is not exposed publicly. It is intended
	 * as a helper for higher-level exponentiation routines.
	 * </p>
	 *
	 * @param base the {@link Decimal} base
	 * @param exponent the exponent, required to be an integer value
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the result of raising {@code base} to the given {@code exponent}
	 * @throws ArithmeticException if {@code exponent} is negative or non-integral
	 *                             (behavior undefined for non-integer inputs)
	 */
	private static Decimal integerExponentiation(Decimal base, Decimal exponent, MathContext context) {
		if (exponent.lessThanOrEqualTo(Decimal.INTEGER_MAX_VALUE)) {
			return new Decimal(base.toBigDecimal().pow(exponent.toInt(), context));
		} else {
			Decimal result = ONE;
			while (exponent.greaterThan(ZERO)) {
				if (exponent.isOdd())
					result = result.multiply(base, context);
				base = base.multiply(base, context);
				exponent = exponent.shiftRight(1);
			}
			return result;
		}
	}

	/**
	 * Computes the exponential function {@code e^exponent} for a {@link Decimal} value.
	 * <p>
	 * The implementation uses range reduction with {@code ln(2)} to keep the
	 * argument within a numerically stable interval. Specifically:
	 * <ul>
	 *   <li>If {@code exponent} is larger than {@code ln(2)/2}, the method
	 *       reduces it via {@code exponent = k * ln(2) + r} and computes
	 *       {@code 2^k * e^r} recursively.</li>
	 *   <li>For small arguments, it evaluates the Taylor series expansion
	 *       of {@code e^x} around 0 using a factorial supplier and summation.</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * This method is currently private and not part of the public API. It may
	 * be moved to a dedicated utility class in the future.
	 * </p>
	 *
	 * @param exponent the {@link Decimal} exponent to raise {@code e} to
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the value of {@code e^exponent}
	 * @implNote
	 *   The series evaluation is done via a {@code Summation} object and
	 *   {@code FactorialSupplier}, both of which generate the Taylor expansion
	 *   terms on demand. The method relies on {@link #integerExponentiation(Decimal, Decimal, MathContext)}
	 *   for efficient power computation of the reduced terms.
	 */
	private static Decimal exp(Decimal exponent, MathContext context) {
		Decimal ln2 = ln2(context);

		if (exponent.greaterThan(ln2.divide(TWO, context))) {
			Decimal k = exponent.divide(ln2, context).round();
			Decimal r = exponent.subtract(k.multiply(ln2, context), context);
			return integerExponentiation(TWO, k, context).multiply(exp(r, context), context);
		}

		FactorialSupplier nFactorial = new FactorialSupplier(0);
		Summation powerSeries =
				new Summation(n -> integerExponentiation(exponent, n, context).divide(nFactorial.nextPre(), context));
		return powerSeries.sumInfinite(0, context); // currently implemented via Taylor series
	}

	/**
	 * Computes the natural logarithm of 2 ({@code ln(2)}).
	 * <p>
	 * The implementation uses an infinite series expansion based on
	 * the Gregory–Leibniz / arctanh-derived identity:
	 * <pre>
	 *   ln(2) = Σ ( 2 / [ (2k + 1) * 3 * 9^k ] ),  for k = 0, 1, 2, ...
	 * </pre>
	 * This series is evaluated using a {@link Summation} object, with each term
	 * generated by {@link #integerExponentiation(Decimal, Decimal, MathContext)}.
	 * </p>
	 *
	 * <p>
	 * The method is private and intended as a helper for range reduction in
	 * exponential and logarithmic functions.
	 * </p>
	 *
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the value of {@code ln(2)} to the given precision
	 */
	private static Decimal _3 = new Decimal(3);
	private static Decimal _9 = new Decimal(9);
	private static Decimal ln2(MathContext context) {
		return new Summation(
				k -> TWO.divide(
						_3.multiply(
								TWO.multiply(k, context).add(ONE, context),
								context
								).multiply(
										integerExponentiation(_9, k, context),
										context
										),
						context
						)
				).sumInfinite(0, context);
	}

	/**
	 * Computes the natural logarithm ({@code ln(x)}) of a {@link Decimal} value.
	 * <p>
	 * The implementation first applies range reduction to bring the argument
	 * {@code antiLogarithm} into the interval [1, 2), adjusting by powers of 2
	 * and tracking the exponent {@code k}. The identity
	 * <pre>
	 *   ln(x) = k * ln(2) + ln(reducedValue)
	 * </pre>
	 * is then used to reconstruct the result.
	 * </p>
	 *
	 * <p>
	 * For values in [1, 2), the function evaluates
	 * <pre>
	 *   ln(x) = 2 * atanh( (x - 1) / (x + 1) )
	 * </pre>
	 * where the {@code atanh} series expansion is computed as
	 * <pre>
	 *   atanh(t) = Σ ( t^(2j+1) / (2j+1) ),  for j = 0, 1, 2, ...
	 * </pre>
	 * using a {@link Summation} helper. Internally, powers are computed via
	 * {@link #integerExponentiation(Decimal, Decimal, MathContext)}.
	 * </p>
	 *
	 * <p>
	 * This method is private and not part of the public API. It may be moved
	 * into a dedicated logarithm utility class in the future.
	 * </p>
	 *
	 * @param antiLogarithm the argument {@code x}, required to be positive
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the natural logarithm of {@code antiLogarithm}
	 * @throws ArithmeticException if {@code antiLogarithm} is non-positive
	 */
	private static Decimal ln(Decimal antiLogarithm, MathContext context) {
		long k = 0;
		while (!antiLogarithm.inInterval(ONE, TWO, BoundType.INCLUSIVE, BoundType.EXCLUSIVE)) {
			if (antiLogarithm.lessThan(ONE)) {
				antiLogarithm = antiLogarithm.multiply(TWO, context);
				k--;
			} else if (antiLogarithm.greaterThanOrEqualTo(TWO)) {
				antiLogarithm = antiLogarithm.divide(TWO, context);
				k++;
			}
		}

		if (k != 0) {
			return new Decimal(k)
					.multiply(ln2(context), context)
					.add(ln(antiLogarithm, context), context);
		}

		Decimal t = antiLogarithm.subtract(ONE, context)
				.divide(antiLogarithm.add(ONE, context), context);

		Summation atanh = new Summation(j -> {
			Decimal denominator = TWO.multiply(j, context).add(ONE, context);
			return integerExponentiation(t, denominator, context)
					.divide(denominator, context);
		}); // atanh method could be extracted in the future

		return TWO.multiply(atanh.sumInfinite(0, context), context);
	}

	/**
	 * Computes {@code base} raised to the power of {@code exponent} with the given {@link MathContext}.
	 * <p>
	 * This method supports both integer and non-integer exponents:
	 * <ul>
	 *   <li>If {@code exponent} is an integer:
	 *     <ul>
	 *       <li>{@code exponent == 0} returns {@code ONE}.</li>
	 *       <li>{@code exponent > 0} delegates to {@code integerExponentiation(base, exponent, context)}.</li>
	 *       <li>{@code exponent < 0} computes the reciprocal of the corresponding positive power.</li>
	 *     </ul>
	 *   </li>
	 *   <li>If {@code exponent} is non-integer:
	 *     <ul>
	 *       <li>The result is defined as {@code exp(exponent * ln(base))} and is evaluated with the
	 *           provided precision.</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 * <p>
	 * No explicit guardrails are applied for special cases such as {@code base <= 0} with non-integer
	 * exponents or division by zero. If such conditions arise, they will fail naturally in the
	 * underlying operations ({@code ln}, {@code divide}, etc.), rather than being intercepted here.
	 *
	 * @param base     the value to raise
	 * @param exponent the power to raise {@code base} to
	 * @param context  the {@link MathContext} used for intermediate and final precision
	 * @return {@code base} raised to {@code exponent}, computed with the given precision
	 */
	public static Decimal exponentiation(Decimal base, Decimal exponent, MathContext context) {
	    if (exponent.isInteger()) {
	        if (exponent.equals(ZERO)) return ONE;
	        if (exponent.greaterThan(ZERO)) return integerExponentiation(base, exponent, context);
	        return ONE.divide(integerExponentiation(base, exponent.negate(), context), context);
	    }
	    return exp(exponent.multiply(ln(base, context), context), context);
	}

}
