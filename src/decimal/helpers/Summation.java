package decimal.helpers;

import java.math.MathContext;
import java.util.function.Function;

import decimal.Decimal;

/**
 * Utility class for performing summation of a given function over a range of values.
 *
 * <p>This class wraps a {@link Function} from {@code Decimal} to {@code Decimal}
 * and provides methods for finite and infinite summation.</p>
 *
 * <p><strong>Developer notes:</strong></p>
 * <ul>
 *   <li>This could have been designed as an interface extending {@code Function},
 *       but inheritance was intentionally avoided in favor of a simple wrapper.</li>
 *   <li>In effect, this class is a lightweight wrapper around a function, consistent
 *       with the general wrapper pattern used elsewhere in this library.</li>
 *   <li>Summation assumes numeric stability: for infinite sums, convergence is
 *       detected when two successive partial sums are equal under the given
 *       {@link MathContext}.</li>
 * </ul>
 */
public class Summation {

	/**
	 * The function to be summed.
	 */
	private Function<Decimal, Decimal> function;

	/**
	 * Creates a new {@code Summation} wrapper for the given function.
	 *
	 * @param function the function to be summed
	 */
	public Summation(Function<Decimal, Decimal> function) {
		this.function = function;
	}

	/**
	 * Computes an "infinite" summation of the wrapped function starting at
	 * the given index, under the supplied {@link MathContext}.
	 *
	 * <p>The summation continues until convergence is detected, i.e. when
	 * two successive partial sums are equal under the given precision.</p>
	 *
	 * <p><strong>Developer note:</strong> Although convergence is assumed,
	 * divergence will result in an endless loop. Use with caution.</p>
	 *
	 * @param start   the starting index (may be negative or non-negative)
	 * @param context the math context specifying precision and rounding
	 * @return the approximated sum of the series
	 */
	public Decimal sumInfinite(long start, MathContext context) {
		Decimal result = Decimal.ZERO;
		for (long i = start; ; i++) { // safe until 2^63 - 1 terms
			Decimal newResult = result.add(function.apply(new Decimal(i)), context);
			if (result.equals(newResult)) // assumes convergence
				break;
			result = newResult;
		}
		return result;
	}

	/**
	 * Computes the finite summation of the wrapped function from {@code start}
	 * to {@code end}, inclusive, under the supplied {@link MathContext}.
	 *
	 * <p>If {@code end < start}, the loop runs zero times and the result is
	 * {@link Decimal#ZERO}.</p>
	 *
	 * @param start   the starting index
	 * @param end     the ending index (inclusive)
	 * @param context the math context specifying precision and rounding
	 * @return the sum of the function applied over the range [start, end]
	 */
	public Decimal sum(long start, long end, MathContext context) {
		Decimal result = Decimal.ZERO;
		for (long i = start; i <= end; i++) { // safe until 2^63 - 1 terms
			result = result.add(function.apply(new Decimal(i)), context);
		}
		return result;
	}
}

