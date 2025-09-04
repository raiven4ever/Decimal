package decimal.operations.elementaryExtensions;

import static decimal.Decimal.ONE;
import static decimal.Decimal.TWO;
import static decimal.Decimal.ZERO;

import java.math.BigInteger;
import java.math.MathContext;

import decimal.Decimal;
import decimal.Decimal.BoundType;
import decimal.helpers.FactorialSupplier;
import decimal.helpers.Summation;

/**
 * Utility class for exponentiation-related operations.
 *
 * <p>This class provides implementations and placeholders for
 * exponentiation, logarithms, and exponential functions on {@link Decimal}.
 * It is intended as a companion to {@link Decimal} for advanced
 * mathematical operations.</p>
 *
 * <p><strong>Developer notes:</strong></p>
 * <ul>
 *   <li>Constants such as 3 and 9 are cached internally for convenience.</li>
 *   <li>The eventual goal is to support multiple strategies (e.g., Machin-like
 *       formulas, BBP-type series, iterative methods) for logarithms and
 *       exponentials.</li>
 *   <li>Currently, some methods are placeholders awaiting proper
 *       implementation.</li>
 * </ul>
 */
public class Exponentiation {

	/** Constant 3, cached as a {@code Decimal}. */
	private static Decimal _3 = new Decimal(3);

	/** Constant 9, cached as a {@code Decimal}. */
	private static Decimal _9 = new Decimal(9);

	/**
	 * Computes an approximation of {@code ln(2)} using a series expansion.
	 *
	 * <p>The series implemented is:</p>
	 * <pre>
	 *   ∑ (from k = 0 to ∞)  2 / (3(2k + 1) * 9^k)
	 * </pre>
	 *
	 * <p><strong>Developer notes:</strong></p>
	 * <ul>
	 *   <li>This approach uses a direct series expansion. A Machin-like
	 *       formula would be more efficient but requires {@code arctanh}.</li>
	 *   <li>The BBP (Bailey–Borwein–Plouffe) formula is another alternative,
	 *       but it involves too many terms to be practical in this context.</li>
	 *   <li>The implementation uses {@link Summation#sumInfinite(long, MathContext)}
	 *       to handle convergence detection automatically.</li>
	 *   <li>Having a general-purpose interpreter for mathematical expression
	 *       strings would simplify this implementation significantly.</li>
	 * </ul>
	 *
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return an approximation of {@code ln(2)} at the given precision
	 */
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
	 * Computes {@code base^exponent} where {@code exponent} must be an integer.
	 *
	 * <p>This method handles a variety of special cases:</p>
	 * <ul>
	 *   <li>If {@code exponent} is not an integer, throws {@link IllegalArgumentException}.</li>
	 *   <li>If {@code base = 0}:
	 *     <ul>
	 *       <li>and {@code exponent > 0}, returns {@code 0}.</li>
	 *       <li>and {@code exponent = 0}, returns {@code 1} (numerical convention, not analytical).</li>
	 *       <li>and {@code exponent < 0}, throws {@link ArithmeticException} (division by zero).</li>
	 *     </ul>
	 *   </li>
	 *   <li>If {@code base = 1}, always returns {@code 1}.</li>
	 *   <li>If {@code base = -1}, returns {@code 1} if exponent is even, and {@code -1} if exponent is odd.</li>
	 *   <li>If {@code exponent = 0}, returns {@code 1}.</li>
	 *   <li>If {@code exponent < 0}, computes {@code 1 / base^|exponent|}.</li>
	 * </ul>
	 *
	 * <p>For performance, two strategies are used:</p>
	 * <ul>
	 *   <li>If {@code exponent ≤ Integer.MAX_VALUE}, delegates to
	 *       {@link BigDecimal#pow(int, MathContext)}.</li>
	 *   <li>Otherwise, uses binary (exponentiation by squaring) with
	 *       repeated squaring and multiplication.</li>
	 * </ul>
	 *
	 * <p><strong>Developer note:</strong> A {@link MathContext} is required
	 * even for integer exponents because the base may be non-integer, and
	 * precision/rounding rules still apply.</p>
	 *
	 * @param base     the base value
	 * @param exponent the exponent value (must be an integer)
	 * @param context  the math context specifying precision and rounding
	 * @return {@code base^exponent}
	 * @throws IllegalArgumentException if {@code exponent} is not an integer
	 * @throws ArithmeticException if the operation is undefined (e.g., division by zero)
	 */
	public static Decimal integerExponentiation(Decimal base, Decimal exponent, MathContext context) {
		if (!exponent.isInteger()) throw new IllegalArgumentException("exponent must be an integer");

		if (base.equals(ZERO)) {
			if (exponent.greaterThan(ZERO)) return ZERO;
			if (exponent.equals(ZERO)) return ONE; // numerical convention: 0^0 = 1
			if (exponent.lessThan(ZERO)) throw new ArithmeticException("division by zero");
		}

		if (base.equals(ONE)) return ONE;

		if (base.equals(ONE.negate())) {
			if (exponent.isInteger()) return exponent.isEven() ? ONE : ONE.negate();
			else throw new IllegalArgumentException("not defined in R");
		}

		if (exponent.equals(ZERO)) return ONE;

		if (exponent.lessThan(ZERO)) {
			if (base.equals(ZERO))
				throw new ArithmeticException("division by zero");
			return ONE.divide(integerExponentiation(base, exponent.negate(), context), context);
		}

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
	 * Computes the exponential function {@code e^exponent}.
	 *
	 * <p>This implementation handles several cases:</p>
	 * <ul>
	 *   <li>If {@code exponent = 0}, returns {@code 1}.</li>
	 *   <li>If {@code exponent < 0}, computes {@code 1 / e^(-exponent)}.</li>
	 *   <li>If {@code exponent} is larger than {@code ln(2)/2}, performs
	 *       a range reduction:
	 *       <ul>
	 *         <li>Computes {@code k = round(exponent / ln(2))}.</li>
	 *         <li>Computes {@code r = exponent - k * ln(2)}.</li>
	 *         <li>Then returns {@code 2^k * e^r}.</li>
	 *       </ul>
	 *   </li>
	 *   <li>For smaller values of {@code exponent}, uses the Taylor series
	 *       expansion of {@code e^x}:
	 *       <pre>
	 *         e^x = ∑ (from n=0 to ∞) x^n / n!
	 *       </pre>
	 *   </li>
	 * </ul>
	 *
	 * <p><strong>Developer notes:</strong></p>
	 * <ul>
	 *   <li>The Taylor series is implemented using a {@link FactorialSupplier}
	 *       for efficient computation of successive factorials.</li>
	 *   <li>A {@link Summation} is used to accumulate the series terms until
	 *       convergence under the provided {@link MathContext}.</li>
	 *   <li>This is a straightforward implementation; more sophisticated
	 *       algorithms (e.g., Padé approximants or continued fractions)
	 *       could improve performance and accuracy for large exponents.</li>
	 * </ul>
	 *
	 * @param exponent the exponent value
	 * @param context  the {@link MathContext} specifying precision and rounding
	 * @return the computed value of {@code e^exponent}
	 */
	public static Decimal exp(Decimal exponent, MathContext context) {
		if (exponent.equals(ZERO)) return ONE;
		if (exponent.lessThan(ZERO)) return ONE.divide(exp(exponent.negate(), context), context);

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
	 * Computes the natural logarithm {@code ln(antiLogarithm)}.
	 *
	 * <p>This implementation uses a two-step process:</p>
	 * <ol>
	 *   <li><strong>Range reduction:</strong> The input is repeatedly
	 *       scaled by powers of 2 until it falls into the interval
	 *       {@code [1, 2)}. A counter {@code k} tracks the number of
	 *       scaling steps performed.
	 *       <ul>
	 *         <li>If {@code antiLogarithm < 1}, multiply by 2 and decrement {@code k}.</li>
	 *         <li>If {@code antiLogarithm ≥ 2}, divide by 2 and increment {@code k}.</li>
	 *       </ul>
	 *       Once reduced, the logarithm is expressed as:
	 *       <pre>
	 *         ln(x) = k * ln(2) + ln(reducedValue)
	 *       </pre>
	 *   </li>
	 *   <li><strong>Series expansion:</strong> For the reduced value,
	 *       the following identity is used:
	 *       <pre>
	 *         ln(x) = 2 * atanh( (x - 1) / (x + 1) )
	 *       </pre>
	 *       where atanh is computed via the Taylor series:
	 *       <pre>
	 *         atanh(t) = ∑ (from j=0 to ∞) t^(2j+1) / (2j+1)
	 *       </pre>
	 *   </li>
	 * </ol>
	 *
	 * <p><strong>Developer notes:</strong></p>
	 * <ul>
	 *   <li>If {@code antiLogarithm ≤ 0}, throws {@link IllegalArgumentException},
	 *       since the natural logarithm is undefined for non-positive values.</li>
	 *   <li>If {@code antiLogarithm = m * 2^k} with {@code k} outside the range
	 *       {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE}, the algorithm will fail,
	 *       but such inputs are considered out of scope.</li>
	 *   <li>Currently uses an inline {@link Summation} for the atanh expansion;
	 *       a dedicated atanh method could be implemented in the future.</li>
	 * </ul>
	 *
	 * @param antiLogarithm the input value, must be greater than zero
	 * @param context       the {@link MathContext} specifying precision and rounding
	 * @return the natural logarithm of {@code antiLogarithm}
	 * @throws IllegalArgumentException if {@code antiLogarithm ≤ 0}
	 */
	public static Decimal ln(Decimal antiLogarithm, MathContext context) {
		if (!antiLogarithm.greaterThan(ZERO))
			throw new IllegalArgumentException("antiLogarithm must be greater than 0");

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
	 * Computes {@code base^exponent} for arbitrary real exponents.
	 *
	 * <p>This method dispatches to different strategies depending on the type of
	 * {@code exponent} and the value of {@code base}:</p>
	 *
	 * <ul>
	 *   <li>If {@code exponent} is an integer, delegates to
	 *       {@link #integerExponentiation(Decimal, Decimal, MathContext)}.</li>
	 *   <li>If {@code base = 0}:
	 *     <ul>
	 *       <li>and {@code exponent > 0}, returns {@code 0}.</li>
	 *       <li>and {@code exponent = 0}, returns {@code 1}
	 *           (numerical convention, not analytical).</li>
	 *       <li>and {@code exponent < 0}, throws {@link ArithmeticException}
	 *           (division by zero).</li>
	 *     </ul>
	 *   </li>
	 *   <li>If {@code base = 1}, always returns {@code 1}.</li>
	 *   <li>If {@code base = -1}:
	 *     <ul>
	 *       <li>and {@code exponent} is an integer, returns {@code 1} if even,
	 *           {@code -1} if odd.</li>
	 *       <li>and {@code exponent} is not an integer, throws
	 *           {@link IllegalArgumentException}, since {@code (-1)^r}
	 *           is undefined in the reals.</li>
	 *     </ul>
	 *   </li>
	 *   <li>If {@code exponent = 0}, returns {@code 1}.</li>
	 *   <li>Otherwise, computes the general case using the identity:
	 *     <pre>
	 *       base^exponent = exp(exponent * ln(base))
	 *     </pre>
	 *     which is valid for {@code base > 0}.</li>
	 * </ul>
	 *
	 * <p><strong>Developer notes:</strong></p>
	 * <ul>
	 *   <li>This method does not distinguish between rational and irrational
	 *       exponents; the {@code Decimal} class system treats all reals
	 *       uniformly.</li>
	 *   <li>Negative bases with non-integer exponents are implicitly handled
	 *       by {@link #ln(Decimal, MathContext)}, which will throw for invalid input.</li>
	 *   <li>Negative exponents with positive bases are also handled naturally
	 *       by the logarithm + exponential identity or by integer exponentiation.</li>
	 * </ul>
	 *
	 * @param base     the base value
	 * @param exponent the exponent value
	 * @param context  the {@link MathContext} specifying precision and rounding
	 * @return {@code base^exponent}
	 * @throws IllegalArgumentException if the operation is undefined in the reals
	 * @throws ArithmeticException if division by zero occurs
	 */
	public static Decimal exponentiation(Decimal base, Decimal exponent, MathContext context) {
		if (exponent.isInteger()) return integerExponentiation(base, exponent, context);

		if (base.equals(ZERO)) {
			if (exponent.greaterThan(ZERO)) return ZERO;
			if (exponent.equals(ZERO)) return ONE; // convention: 0^0 = 1
			if (exponent.lessThan(ZERO)) throw new ArithmeticException("division by zero");
		}

		if (base.equals(ONE)) return ONE;

		if (base.equals(ONE.negate())) {
			if (exponent.isInteger()) return exponent.isEven() ? ONE : ONE.negate();
			else throw new IllegalArgumentException("(-1)^r is not defined in R for non-integer r");
		}

		if (exponent.equals(ZERO)) return ONE;

		// For general case: base^exponent = exp(exponent * ln(base))
		return exp(exponent.multiply(ln(base, context)), context);
	}

}
