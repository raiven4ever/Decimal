package decimal.operations.elementaryExtensions;

import static decimal.Decimal.*;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import decimal.Decimal;
import decimal.helpers.FactorialSupplier;
import decimal.helpers.NewtonRaphsonProvider;
import decimal.helpers.Summation;

/**
 * Utility class providing trigonometric functions for {@link decimal.Decimal}.
 *
 * <p>This class implements a wide range of trigonometric and inverse
 * trigonometric functions (e.g., {@code sin}, {@code cos}, {@code tan},
 * {@code arcsin}, {@code arccos}, {@code arctan}, etc.) under arbitrary
 * precision, controlled by a {@link java.math.MathContext}.</p>
 *
 * <p>Constants such as {@code π} are computed internally using series
 * expansions (e.g., BBP and Chudnovsky formulas), and functions are
 * evaluated through Maclaurin expansions combined with range-reduction
 * techniques for stability.</p>
 *
 * <h2>Design Notes</h2>
 * <ul>
 *   <li>All methods are {@code static}; this class cannot be instantiated.</li>
 *   <li>Range reduction is applied to angles so that series approximations
 *       converge quickly.</li>
 *   <li>Both primary (sin, cos, tan) and reciprocal (csc, sec, cot)
 *       functions are included.</li>
 *   <li>Inverse trigonometric functions use iterative solvers and
 *       series-based identities to ensure precision.</li>
 * </ul>
 *
 * @see decimal.Decimal
 * @see java.math.MathContext
 */
public class Trigonometry {

	/**
	 * Constant representing the value {@code 3}.
	 */
	private static final Decimal THREE = D(3);

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 *
	 * @throws AssertionError always, since instantiation is not allowed
	 */
	private Trigonometry() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * Creates a {@link Decimal} from the given value.
	 *
	 * <p>Accepted types include:
	 * <ul>
	 *   <li>{@link String}</li>
	 *   <li>{@link Long}</li>
	 *   <li>{@link Integer}</li>
	 *   <li>{@link Double}</li>
	 * </ul>
	 * </p>
	 *
	 * @param value the value to convert
	 * @return a {@code Decimal} representing {@code value}
	 * @throws IllegalArgumentException if the type of {@code value} is not supported
	 */
	static Decimal D(Object value) {
		return switch(value) {
		case String string -> new Decimal(string);
		case Long _long -> new Decimal(_long);
		case Integer _int -> new Decimal(_int);
		case Double _double -> new Decimal(_double);
		default -> throw new IllegalArgumentException("Unexpected value: " + value);
		};
	}

	/**
	 * Provides algorithms for computing the constant π with arbitrary precision.
	 *
	 * <p>Two different approaches are included:
	 * <ul>
	 *   <li>{@link Chudovsky} — fast but less precise.</li>
	 *   <li>{@link BBP} — slower but extremely precise.</li>
	 * </ul>
	 * </p>
	 */
	private static class Pi {

		/**
		 * Computes π using the Chudnovsky formula.
		 *
		 * <p>This method converges quickly and is efficient,
		 * but the results may be less accurate at higher precisions.</p>
		 */
		static class Chudovsky { //fast but imprecise
			/**
			 * Constant {@code 10005}, used as part of the multiplier in the
			 * Chudnovsky series for π.
			 */
			private static final Decimal D6 = D(10005);

			/**
			 * Constant {@code 640320}, a key parameter in the Chudnovsky formula
			 * affecting the convergence rate.
			 */
			private static final Decimal D5 = D(640320);

			/**
			 * Constant {@code 3}, representing the exponent applied to factorial
			 * and polynomial terms in the series.
			 */
			private static final Decimal D4 = THREE;

			/**
			 * Constant {@code 545140134}, the coefficient applied to the series
			 * index in the numerator.
			 */
			private static final Decimal D3 = D(545140134);

			/**
			 * Constant {@code 13591409}, the base term added to the numerator
			 * in each series iteration.
			 */
			private static final Decimal D2 = D(13591409);

			/**
			 * Constant {@code 4270934400}, used in the denominator of the
			 * multiplier for the Chudnovsky series.
			 */
			private static final Decimal D = D("4270934400");

			/**
			 * Computes π using the Chudnovsky series expansion.
			 *
			 * <p>This implementation uses factorial-based summation
			 * and converges rapidly, making it suitable when speed
			 * is prioritized over ultimate precision.</p>
			 *
			 * @param context the {@link MathContext} specifying precision and rounding
			 * @return an approximation of π at the given precision
			 */
			private static Decimal pi(MathContext context) {
				Decimal multiplier = D6.sqrt(context).divide(D, context);

				FactorialSupplier factorialSupplier = new FactorialSupplier(0);
				FactorialSupplier factorialSupplier2 = new FactorialSupplier(0);
				FactorialSupplier factorialSupplier3 = new FactorialSupplier(0);
				Summation summation = new Summation(k -> {
					Decimal numerator = factorialSupplier.nextPre(6).multiply(D2.add(D3.multiply(k, context), context), context);
					Decimal denominator = factorialSupplier2.nextPre(3).multiply(factorialSupplier3.nextPre().pow(D4, context), context).multiply(D5.negate().pow(D4.multiply(k, context), context), context);
					System.out.println(k);
					return numerator.divide(denominator, context);
				});

				return ONE.divide(multiplier.multiply(summation.sumInfinite(0, context), context), context);
			}
		}

		/**
		 * Computes π using the Bailey–Borwein–Plouffe (BBP) formula.
		 *
		 * <p>This method converges more slowly but provides
		 * very high precision, making it suitable for contexts
		 * where accuracy is more important than speed.</p>
		 */
		static class BBP { //slower but insanely precise

			/**
			 * Constant {@code 6}, used in the denominator terms of the BBP series.
			 */
			private static final Decimal D5 = D(6);

			/**
			 * Constant {@code 5}, used in the denominator terms of the BBP series.
			 */
			private static final Decimal D4 = D(5);

			/**
			 * Constant {@code 8}, representing the common factor multiplied with
			 * the summation index in the BBP formula.
			 */
			private static final Decimal D3 = D(8);

			/**
			 * Constant {@code 4}, used in the denominator terms of the BBP series.
			 */
			private static final Decimal D2 = D(4);

			/**
			 * Constant {@code 16}, the base of the power series in the BBP formula.
			 */
			private static final Decimal D = D(16);

			/**
			 * Computes π using the Bailey–Borwein–Plouffe (BBP) series expansion.
			 *
			 * <p>The BBP formula allows direct computation of hexadecimal (base-16)
			 * digits of π without requiring all preceding digits. This implementation
			 * is slower than Chudnovsky’s method but provides extremely high precision.</p>
			 *
			 * @param context the {@link MathContext} specifying precision and rounding
			 * @return an approximation of π at the given precision
			 */
			private static Decimal pi(MathContext context) {
				Summation summation = new Summation(k -> {
					Decimal multiplier = ONE.divide(D.pow(k, context), context);
					Decimal term = D2.divide(D3.multiply(k, context).add(ONE, context), context);
					Decimal term2 = TWO.divide(D3.multiply(k, context).add(D2, context), context);
					Decimal term3 = ONE.divide(D3.multiply(k, context).add(D4, context), context);
					Decimal term4 = ONE.divide(D3.multiply(k, context).add(D5, context), context);
					return multiplier.multiply(term.subtract(term2, context).subtract(term3, context).subtract(term4, context), context);
				});
				return summation.sumInfinite(0, context);
			}

		}

	}

	/**
	 * Provides implementations of the sine function.
	 *
	 * <p>Methods in this class approximate {@code sin(x)} using
	 * series expansions.</p>
	 */
	private static class Sin {

		/**
		 * Computes {@code sin(angle)} using the Maclaurin series expansion.
		 *
		 * <p>The series is defined as:
		 * <pre>
		 *     sin(x) = Σ [(-1)^n * x^(2n+1)] / (2n+1)!
		 * </pre>
		 * and converges for all real values of {@code x}.</p>
		 *
		 * @param angle   the angle in radians
		 * @param context the {@link MathContext} specifying precision and rounding
		 * @return the sine of {@code angle} with the given precision
		 */
		private static Decimal maclaurin(Decimal angle, MathContext context) {
			FactorialSupplier factorialSupplier = new FactorialSupplier(1);
			Summation summation = new Summation(n -> {
				Decimal numerator = ONE.negate().pow(n, context);
				Decimal denominator = factorialSupplier.nextPre(2);
				Decimal multiplier = angle.pow(TWO.multiply(n, context).add(ONE, context), context);
				Decimal result = multiplier.multiply(numerator, context).divide(denominator, context);
				return result;
			});
			return summation.sumInfinite(0, context);
		}
	}

	/**
	 * Provides implementations of the cosine function.
	 *
	 * <p>Methods in this class approximate {@code cos(x)} using
	 * series expansions.</p>
	 */
	private static class Cos {

		/**
		 * Computes {@code cos(angle)} using the Maclaurin series expansion.
		 *
		 * <p>The series is defined as:
		 * <pre>
		 *     cos(x) = Σ [(-1)^n * x^(2n)] / (2n)!
		 * </pre>
		 * and converges for all real values of {@code x}.</p>
		 *
		 * @param angle   the angle in radians
		 * @param context the {@link MathContext} specifying precision and rounding
		 * @return the cosine of {@code angle} with the given precision
		 */
		private static Decimal maclaurin(Decimal angle, MathContext context) {
			FactorialSupplier factorialSupplier = new FactorialSupplier(0);
			Summation summation = new Summation(n -> {
				Decimal numerator = ONE.negate().pow(n, context);
				Decimal denominator = factorialSupplier.nextPre(2);
				Decimal multiplier = angle.pow(TWO.multiply(n, context), context);
				Decimal result = multiplier.multiply(numerator, context).divide(denominator, context);
				return result;
			});
			return summation.sumInfinite(0, context);
		}

	}

    /**
     * Computes the constant π with arbitrary precision.
     *
     * <p>Currently uses the Bailey–Borwein–Plouffe (BBP) series by default,
     * which converges slowly but yields extremely high accuracy. The
     * Chudnovsky algorithm is available but disabled here.</p>
     *
     * @param context the {@link MathContext} specifying precision and rounding
     * @return an approximation of π at the given precision
     */
	public static Decimal pi(MathContext context) {
//		return Pi.Chudovsky.pi(context);
		return Pi.BBP.pi(context);
	}

    /**
     * Computes the sine of the given angle.
     *
     * <p>The input is range-reduced using multiples of π/2, and the
     * appropriate sine or cosine Maclaurin expansion is applied
     * depending on the quadrant.</p>
     *
     * @param angle   the angle in radians
     * @param context the {@link MathContext} specifying precision and rounding
     * @return the sine of {@code angle} with the given precision
     */
	public static Decimal sin(Decimal angle, MathContext context) {
		Decimal pi = pi(context);
		Decimal n = TWO.multiply(angle, context).divide(pi, context).round();
		angle = angle.subtract(n.multiply(pi, context).multiply(HALF, context), context);

		n = n.and(THREE);
		if (n.equals(ZERO))
			return Sin.maclaurin(angle, context);
		else if (n.equals(ONE))
			return Cos.maclaurin(angle, context);
		else if (n.equals(TWO))
			return Sin.maclaurin(angle, context).negate();
		else
			return Cos.maclaurin(angle, context).negate();
	}

    /**
     * Computes the cosine of the given angle.
     *
     * <p>The input is range-reduced using multiples of π/2, and the
     * appropriate cosine or sine Maclaurin expansion is applied
     * depending on the quadrant.</p>
     *
     * @param angle   the angle in radians
     * @param context the {@link MathContext} specifying precision and rounding
     * @return the cosine of {@code angle} with the given precision
     */
	public static Decimal cos(Decimal angle, MathContext context) {
		Decimal pi = pi(context);
		Decimal n = TWO.multiply(angle, context).divide(pi, context).round();
		angle = angle.subtract(n.multiply(pi, context).multiply(HALF, context), context);

		n = n.and(THREE);
		if (n.equals(ZERO))
			return Cos.maclaurin(angle, context);
		else if (n.equals(ONE))
			return Sin.maclaurin(angle, context).negate();
		else if (n.equals(TWO))
			return Cos.maclaurin(angle, context).negate();
		else
			return Sin.maclaurin(angle, context);
	}

    /**
     * Computes the tangent of the given angle.
     *
     * <p>This implementation currently evaluates {@code tan(x)} as
     * {@code sin(x) / cos(x)}. A dedicated implementation based on
     * series expansions (e.g., using Bernoulli or Euler up/down numbers)
     * may replace this in the future.</p>
     *
     * @param angle   the angle in radians
     * @param context the {@link MathContext} specifying precision and rounding
     * @return the tangent of {@code angle} with the given precision
     */
	public static Decimal tan(Decimal angle, MathContext context) {
		return sin(angle, context).divide(cos(angle, context), context);
	}

    /**
     * Computes the cosecant of the given angle.
     *
     * <p>Defined as {@code csc(x) = 1 / sin(x)}.</p>
     *
     * @param angle   the angle in radians
     * @param context the {@link MathContext} specifying precision and rounding
     * @return the cosecant of {@code angle} with the given precision
     */
	public static Decimal csc(Decimal angle, MathContext context) {
		return ONE.divide(sin(angle, context), context);
	}
	
    /**
     * Computes the secant of the given angle.
     *
     * <p>Defined as {@code sec(x) = 1 / cos(x)}.</p>
     *
     * @param angle   the angle in radians
     * @param context the {@link MathContext} specifying precision and rounding
     * @return the secant of {@code angle} with the given precision
     */
	public static Decimal sec(Decimal angle, MathContext context) {
		return ONE.divide(cos(angle, context), context);
	}

    /**
     * Computes the cotangent of the given angle.
     *
     * <p>Defined as {@code cot(x) = cos(x) / sin(x)}.</p>
     *
     * @param angle   the angle in radians
     * @param context the {@link MathContext} specifying precision and rounding
     * @return the cotangent of {@code angle} with the given precision
     */
	public static Decimal cot(Decimal angle, MathContext context) {
		return cos(angle, context).divide(sin(angle, context), context);
	}

	public static Decimal arcsin(Decimal x, MathContext context) {
		if (x.inInterval(ONE.negate(), ONE, BoundType.INCLUSIVE, BoundType.INCLUSIVE)) {
			NewtonRaphsonProvider provider = 
					new NewtonRaphsonProvider(
							y -> sin(y, context).subtract(x, context), 
							y -> cos(y, context)
							);
			return provider.solve(ZERO, context);
		}
		throw new IllegalArgumentException(String.format("%s is outside of the domain of this function", x));
	}

	public static Decimal arccos(Decimal x, MathContext context) {
		if (x.inInterval(ONE.negate(), ONE, BoundType.INCLUSIVE, BoundType.INCLUSIVE)) {
			NewtonRaphsonProvider provider = 
					new NewtonRaphsonProvider(
							y -> cos(y, context).subtract(x, context), 
							y -> sin(y, context).negate()
							);
			return provider.solve(ONE, context);
		}
		throw new IllegalArgumentException(String.format("%s is outside of the domain of this function", x));
	}

	public static Decimal arctan(Decimal x, MathContext context) {
		Decimal pi = pi(context);
		if (x.abs().greaterThan(ONE))
			return pi.multiply(HALF, context).multiply(D(x.signum()), context).subtract(arctan(x.reciprocal(context), context), context);

		Decimal max = pi.multiply(HALF, context);
		Decimal min = max.negate();
		NewtonRaphsonProvider provider = 
				new NewtonRaphsonProvider(
						y -> tan(y, context).subtract(x, context),
						y -> sec(y, context).squared(context),
						y -> y.subtract(pi.multiply(y.divide(pi, context).round(), context), context),
						min,
						max
						);
		Decimal solve = provider.solve(ZERO, context);
		return solve;
	}

	public static Decimal arccsc(Decimal x, MathContext context) {
		return arcsin(x.reciprocal(context), context);
	}

	public static Decimal arcsec(Decimal x, MathContext context) {
		return arccos(x.reciprocal(context), context);
	}

	public static Decimal arccot(Decimal x, MathContext context) {
		return x.isPositive() ? arctan(x.reciprocal(context), context) : arctan(x.reciprocal(context), context).add(pi(context), context);
	}

}
