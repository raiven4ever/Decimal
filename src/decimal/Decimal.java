package decimal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;

import decimal.operations.ArithmeticBasics;
import decimal.operations.elementaryExtensions.Exponentiation;
import decimal.operations.elementaryExtensions.RootExtraction;

/**
 * Immutable wrapper class around {@link java.math.BigDecimal}.
 * <p>
 * This class provides a higher-level API for precise decimal arithmetic
 * while enforcing consistent semantics, default precision, and
 * serialization compatibility. Instances of {@code Decimal} are
 * immutable and thread-safe.
 */
public class Decimal implements Comparable<Decimal>, Serializable{

	/**
	 * Constant representing the decimal value {@code 0}.
	 */
	public static final Decimal ZERO = new Decimal(0);

	/**
	 * Constant representing the decimal value {@code 0.5}.
	 *
	 * <p>Useful for midpoint calculations and averaging
	 * (e.g., {@link #average(Decimal, MathContext)}).</p>
	 */
	public static final Decimal HALF = new Decimal(0.5);

	/**
	 * Constant representing the decimal value {@code 1}.
	 */
	public static final Decimal ONE = new Decimal(1);

	/**
	 * Constant representing the decimal value {@code 2}.
	 */
	public static final Decimal TWO = new Decimal(2);

	/**
	 * Constant representing the maximum finite {@code double} value
	 * as a {@code Decimal}.
	 *
	 * <p>Equivalent to {@link Double#MAX_VALUE} wrapped in a {@code Decimal}.</p>
	 */
	public static final Decimal DOUBLE_MAX_VALUE = new Decimal(Double.MAX_VALUE);

	/**
	 * Constant representing the maximum {@code int} value
	 * as a {@code Decimal}.
	 *
	 * <p>Equivalent to {@link Integer#MAX_VALUE} wrapped in a {@code Decimal}.</p>
	 */
	public static final Decimal INTEGER_MAX_VALUE = new Decimal(Integer.MAX_VALUE);

	/**
	 * Constant representing the maximum {@code long} value
	 * as a {@code Decimal}.
	 *
	 * <p>Equivalent to {@link Long#MAX_VALUE} wrapped in a {@code Decimal}.</p>
	 */
	public static final Decimal LONG_MAX_VALUE = new Decimal(Long.MAX_VALUE);


	/**
	 * Default {@link MathContext} to be used when no explicit
	 * context is provided by the caller.
	 * <p>
	 * The default is {@link MathContext#DECIMAL128}, providing
	 * 34 digits of precision with the IEEE 754R Decimal128 format.
	 */
	private static final MathContext DEFAULT_CONTEXT = MathContext.DECIMAL128;

	/**
	 * Serial version identifier used during deserialization
	 * to verify that the sender and receiver of a serialized
	 * object maintain binary compatibility.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The underlying {@link BigDecimal} value that this {@code Decimal}
	 * instance wraps.
	 * <p>
	 * This field is immutable and never {@code null}.
	 */
	private final BigDecimal value;

	/**
	 * Creates a new {@code Decimal} instance wrapping the specified
	 * {@link BigDecimal} value.
	 *
	 * <p>This constructor does not alter the supplied value. If you want
	 * to normalize numbers such as {@code 1.0} and {@code 1.00} so that
	 * they compare as equal via {@link #equals(Object)}, consider calling
	 * {@link BigDecimal#stripTrailingZeros()} before assigning.</p>
	 *
	 * @param value the {@link BigDecimal} to wrap (must not be {@code null})
	 */
	public Decimal(BigDecimal value) {
		this.value = Objects.requireNonNull(value, "value must not be null");
	}

	/**
	 * Creates a new {@code Decimal} from the specified {@link BigInteger}.
	 *
	 * <p>The value is converted directly via
	 * {@code new BigDecimal(BigInteger)}.</p>
	 *
	 * @param value the {@code BigInteger} to wrap (must not be {@code null})
	 */
	public Decimal(BigInteger value) {
		this.value = new BigDecimal(Objects.requireNonNull(value, "value must not be null"));
	}

	/**
	 * Creates a new {@code Decimal} from the specified string
	 * representation.
	 *
	 * <p>The input string must conform to the syntax accepted by
	 * {@link BigDecimal#BigDecimal(String)}. Leading and trailing
	 * whitespace is permitted and will be ignored.</p>
	 *
	 * <p><strong>Developer note:</strong> If the string cannot be parsed,
	 * a {@link NumberFormatException} is thrown. This constructor
	 * re-throws the exception so that the stack trace originates
	 * from {@code Decimal}, rather than {@code BigDecimal}, making
	 * the wrapper feel more self-contained.</p>
	 *
	 * @param value the string representation of the decimal value
	 * @throws NumberFormatException if {@code value} is not a valid
	 *         representation of a {@code BigDecimal}
	 */
	public Decimal(String value) {
		try {
			this.value = new BigDecimal(Objects.requireNonNull(value, "value must not be null"));
		} catch (NumberFormatException e) {
			// Re-throw so the exception appears to come from Decimal
			throw new NumberFormatException(e.toString());
		}
	}

	/**
	 * Creates a new {@code Decimal} from the specified {@code double} value.
	 *
	 * <p>This constructor internally delegates to
	 * {@link BigDecimal#BigDecimal(double)}, which may produce unexpected
	 * results due to the binary floating-point representation of {@code double}.
	 * For example, {@code new Decimal(0.1)} will not represent exactly 0.1.
	 * If exact decimal values are required, prefer the
	 * {@link #Decimal(String)} constructor or {@link BigDecimal#valueOf(double)}.</p>
	 *
	 * @param value the {@code double} value to wrap
	 * @throws NumberFormatException if {@code value} is {@code NaN},
	 *         positive infinity, or negative infinity
	 */
	public Decimal(double value) {
		this.value = new BigDecimal(Objects.requireNonNull(value, "value must not be null"));
	}

	/**
	 * Creates a new {@code Decimal} from the specified {@code int} value.
	 *
	 * <p>Equivalent to calling {@code new BigDecimal(int)}.</p>
	 *
	 * @param value the {@code int} value to wrap
	 */
	public Decimal(int value) {
		this.value = new BigDecimal(Objects.requireNonNull(value, "value must not be null"));
	}

	/**
	 * Creates a new {@code Decimal} from the specified {@code long} value.
	 *
	 * <p>Equivalent to calling {@code new BigDecimal(long)}.</p>
	 *
	 * @param value the {@code long} value to wrap
	 */
	public Decimal(long value) {
		this.value = new BigDecimal(Objects.requireNonNull(value, "value must not be null"));
	}


	/**
	 * Returns the underlying {@link BigDecimal} value wrapped by this
	 * {@code Decimal}.
	 *
	 * <p><strong>Developer note:</strong> This is a direct accessor for
	 * interoperability with APIs that require {@code BigDecimal}.
	 *
	 * @return the underlying {@code BigDecimal}
	 */
	public BigDecimal toBigDecimal() {
		return value;
	}

	/**
	 * Converts this {@code Decimal} to a {@link BigInteger}, using
	 * {@link BigDecimal#toBigInteger()} semantics.
	 *
	 * <p><strong>Developer note:</strong> This relies directly on
	 * {@code BigDecimal.toBigInteger()}.
	 *
	 * @return a {@code BigInteger} representation of this {@code Decimal}
	 */
	public BigInteger toBigInteger() {
		return value.toBigInteger();
	}

	/**
	 * Converts this {@code Decimal} to a {@code double}.
	 *
	 * <p>If this {@code Decimal} has too great a magnitude to be represented
	 * as a {@code double}, the result will be
	 * {@link Double#NEGATIVE_INFINITY} or {@link Double#POSITIVE_INFINITY}
	 * as appropriate.
	 *
	 * <p><strong>Developer note:</strong> This behavior is verbatim from
	 * {@link BigDecimal#doubleValue()} documentation.
	 *
	 * @return a {@code double} approximation of this {@code Decimal}
	 */
	public double toDouble() {
		return value.doubleValue();
	}

	/**
	 * Converts this {@code Decimal} to an {@code int}, throwing an exception
	 * if the value has a fractional part or is out of the {@code int} range.
	 *
	 * <p><strong>Developer note:</strong> Uses {@link BigDecimal#intValueExact()}.
	 * The {@link ArithmeticException} is rethrown so the stack trace originates
	 * from {@code Decimal} instead of {@code BigDecimal}.</p>
	 *
	 * @return this value as an {@code int}
	 * @throws ArithmeticException if the value cannot be represented as an exact {@code int}
	 */
	public int toInt() {
		try {
			return value.intValueExact();
		} catch (ArithmeticException e) {
			throw new ArithmeticException(e.toString());
		}
	}

	/**
	 * Converts this {@code Decimal} to a {@code long}, throwing an exception
	 * if the value has a fractional part or is out of the {@code long} range.
	 *
	 * <p><strong>Developer note:</strong> Uses {@link BigDecimal#longValueExact()}.
	 * The {@link ArithmeticException} is rethrown so the stack trace originates
	 * from {@code Decimal} instead of {@code BigDecimal}.</p>
	 *
	 * @return this value as a {@code long}
	 * @throws ArithmeticException if the value cannot be represented as an exact {@code long}
	 */
	public long toLong() {
		try {
			return value.longValueExact();
		} catch (ArithmeticException e) {
			throw new ArithmeticException(e.toString());
		}
	}

	/**
	 * Returns the string representation of this {@code Decimal} using
	 * the {@link DecimalStringFormat#DEFAULT DEFAULT} format.
	 *
	 * @return a string representation of this {@code Decimal}
	 */
	@Override
	public String toString() {
		return format(DecimalStringFormat.DEFAULT);
	}

	/**
	 * Enumeration of available string formatting styles for
	 * {@code Decimal} values.
	 *
	 * <ul>
	 *   <li>{@link #DEFAULT} – strips trailing zeros, plain string</li>
	 *   <li>{@link #PLAIN} – plain string, no exponent</li>
	 *   <li>{@link #ENGINEERING} – engineering notation (exponent multiple of 3)</li>
	 *   <li>{@link #SCIENTIFIC} – scientific notation</li>
	 *   <li>{@link #PRESERVE_SCALE} – preserves original scale, uses {@code BigDecimal.toString()}</li>
	 *   <li>{@link #STRIPPED} – strips trailing zeros, plain string (alias of DEFAULT)</li>
	 * </ul>
	 */
	public static enum DecimalStringFormat {
		DEFAULT,
		PLAIN,
		ENGINEERING,
		SCIENTIFIC,
		PRESERVE_SCALE,
		STRIPPED,
	}

	/**
	 * Returns a string representation of this {@code Decimal} in the
	 * specified {@link DecimalStringFormat}.
	 *
	 * @param format the desired string formatting style
	 * @return a string representation of this {@code Decimal} in the given format
	 */
	public String format(DecimalStringFormat format) {
		return switch (format) {
		case DEFAULT, STRIPPED 	-> value.stripTrailingZeros().toPlainString();
		case PLAIN 				-> value.toPlainString();
		case ENGINEERING 		-> value.toEngineeringString();
		case SCIENTIFIC 		-> toScientificString();
		case PRESERVE_SCALE 	-> value.toString();
		};
	}

	/**
	 * Returns a string representation of this {@code Decimal} in
	 * scientific notation.
	 *
	 * <p><strong>Developer note:</strong> This method is currently private
	 * as it is a helper for {@link #format(DecimalStringFormat)}, but it
	 * may be promoted to {@code public} in the future if direct access
	 * is desirable.
	 *
	 * <p><strong>Implementation note:</strong> Builds the coefficient and
	 * exponent manually:
	 * <ul>
	 *   <li>Coefficient: {@code unscaledValue} with a decimal point
	 *       inserted after the first digit</li>
	 *   <li>Exponent: {@code (unscaledValue length - 1 - scale)}</li>
	 * </ul>
	 *
	 * @return a scientific-notation string representation of this {@code Decimal}
	 */
	private String toScientificString() {
		// rough implementation

		// making the coefficient
		BigDecimal stripTrailingZeros = value.stripTrailingZeros();
		BigInteger unscaledValue = stripTrailingZeros.unscaledValue();
		StringBuilder builder = new StringBuilder(unscaledValue.toString());
		builder.insert(1, '.').toString();

		// making the exponent
		int exponent = unscaledValue.toString().length() - 1 - stripTrailingZeros.scale();
		builder.append('E');
		builder.append(exponent < 0 ? exponent : "+" + String.valueOf(exponent));

		return builder.toString();
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * {@code (this + addend)}, using the supplied {@link MathContext}.
	 *
	 * @param addend  the value to be added to this {@code Decimal}
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return a {@code Decimal} representing {@code this + addend}
	 */
	public Decimal add(Decimal addend, MathContext context) {
		return ArithmeticBasics.addition(this, addend, context);
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * {@code (this - subtrahend)}, using the supplied {@link MathContext}.
	 *
	 * @param subtrahend the value to be subtracted from this {@code Decimal}
	 * @param context    the {@link MathContext} specifying precision and rounding
	 * @return a {@code Decimal} representing {@code this - subtrahend}
	 */
	public Decimal subtract(Decimal subtrahend, MathContext context) {
		return ArithmeticBasics.subtraction(this, subtrahend, context);
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * {@code (this × multiplicand)}, using the supplied {@link MathContext}.
	 *
	 * @param multiplicand the value to multiply this {@code Decimal} by
	 * @param context      the {@link MathContext} specifying precision and rounding
	 * @return a {@code Decimal} representing {@code this × multiplicand}
	 */
	public Decimal multiply(Decimal multiplicand, MathContext context) {
		return ArithmeticBasics.multiplication(this, multiplicand, context);
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * {@code (this ÷ divisor)}, using the supplied {@link MathContext}.
	 *
	 * @param divisor the value by which this {@code Decimal} is to be divided
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return a {@code Decimal} representing {@code this ÷ divisor}
	 * @throws ArithmeticException if {@code divisor} is zero
	 */
	public Decimal divide(Decimal divisor, MathContext context) {
		return ArithmeticBasics.division(this, divisor, context);
	}

	/**
	 * Returns the remainder of dividing this {@code Decimal} by the given divisor,
	 * using the specified {@link MathContext}.
	 *
	 * <p>This is equivalent to {@link BigDecimal#remainder(BigDecimal, MathContext)}
	 * on the underlying value.</p>
	 *
	 * @param divisor the value by which this {@code Decimal} is divided
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the remainder after division
	 * @throws ArithmeticException if {@code divisor} is zero
	 */
	public Decimal remainder(Decimal divisor, MathContext context) {
		return new Decimal(value.remainder(divisor.value, context));
	}

	/**
	 * Returns a new {@code Decimal} whose value is this {@code Decimal}
	 * rounded according to the supplied {@link MathContext}.
	 *
	 * <p>The result is equivalent to calling
	 * {@link BigDecimal#round(MathContext)} on the underlying value.</p>
	 *
	 * <p><strong>Developer note:</strong> This method is deprecated in favor of
	 * using {@link #setScale(int, RoundingMode)} for explicit scale and rounding
	 * control, or {@link #round()} for the common case of rounding to the nearest
	 * integer.</p>
	 *
	 * @param context the {@link MathContext} specifying precision and rounding mode
	 * @return a {@code Decimal} representing this value rounded according to {@code context}
	 * @deprecated use {@link #setScale(int, RoundingMode)} or {@link #round()} instead
	 */
	@Deprecated
	public Decimal round(MathContext context) {
		return new Decimal(value.round(context));
	}

	/**
	 * Returns a new {@code Decimal} whose value is this {@code Decimal}
	 * with its scale set to the specified value, using the given rounding mode
	 * if necessary.
	 *
	 * <p>This is equivalent to calling
	 * {@link BigDecimal#setScale(int, RoundingMode)} on the underlying
	 * {@link BigDecimal}.</p>
	 *
	 * <p><strong>Developer note:</strong> The {@link ArithmeticException}
	 * is rethrown so that the stack trace originates from {@code Decimal}
	 * instead of {@code BigDecimal}, keeping the wrapper API self-contained.</p>
	 *
	 * @param newScale the scale of the result (number of digits to the right of the decimal point)
	 * @param mode the rounding mode to apply if rounding is necessary
	 * @return a {@code Decimal} whose scale is set to {@code newScale}
	 * @throws ArithmeticException if rounding is required but the rounding
	 *         mode is {@link RoundingMode#UNNECESSARY}
	 */
	public Decimal setScale(int newScale, RoundingMode mode) {
		try {
			return new Decimal(value.setScale(newScale, mode));
		} catch (ArithmeticException e) {
			throw new ArithmeticException(e.toString());
		}
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * {@code (this + addend)}, using the default {@link MathContext}.
	 *
	 * @param addend the value to be added to this {@code Decimal}
	 * @return a {@code Decimal} representing {@code this + addend}
	 */
	public Decimal add(Decimal addend) {
		return add(addend, DEFAULT_CONTEXT);
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * {@code (this - subtrahend)}, using the default {@link MathContext}.
	 *
	 * @param subtrahend the value to be subtracted from this {@code Decimal}
	 * @return a {@code Decimal} representing {@code this - subtrahend}
	 */
	public Decimal subtract(Decimal subtrahend) {
		return subtract(subtrahend, DEFAULT_CONTEXT);
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * {@code (this × multiplicand)}, using the default {@link MathContext}.
	 *
	 * @param multiplicand the value to multiply this {@code Decimal} by
	 * @return a {@code Decimal} representing {@code this × multiplicand}
	 */
	public Decimal multiply(Decimal multiplicand) {
		return multiply(multiplicand, DEFAULT_CONTEXT);
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * {@code (this ÷ divisor)}, using the default {@link MathContext}.
	 *
	 * @param divisor the value by which this {@code Decimal} is to be divided
	 * @return a {@code Decimal} representing {@code this ÷ divisor}
	 * @throws ArithmeticException if {@code divisor} is zero
	 */
	public Decimal divide(Decimal divisor) {
		return divide(divisor, DEFAULT_CONTEXT);
	}

	/**
	 * Returns the remainder of dividing this {@code Decimal} by the given divisor,
	 * using exact precision (no {@link MathContext}).
	 *
	 * <p>This is equivalent to {@link BigDecimal#remainder(BigDecimal)}
	 * on the underlying value.</p>
	 *
	 * @param divisor the value by which this {@code Decimal} is divided
	 * @return the remainder after division
	 * @throws ArithmeticException if {@code divisor} is zero
	 */
	public Decimal remainder(Decimal divisor) {
		return new Decimal(value.remainder(divisor.value));
	}

	/**
	 * Returns a new {@code Decimal} whose value is the negation of this
	 * {@code Decimal}.
	 *
	 * <p>This is equivalent to multiplying the value by {@code -1}.</p>
	 *
	 * @return a {@code Decimal} representing {@code -this}
	 */
	public Decimal negate() {
		return new Decimal(value.negate());
	}

	/**
	 * Returns this {@code Decimal} unchanged.
	 *
	 * <p><strong>Developer note:</strong> This method exists mainly for
	 * symmetry with {@link #negate()}, so that unary plus and unary minus
	 * can both be expressed explicitly.</p>
	 *
	 * @return this {@code Decimal} instance
	 */
	public Decimal plus() { // it’s just here so that negate is not alone
		return this;
	}

	/**
	 * Compares this {@code Decimal} with the specified {@code Decimal}
	 * for order.
	 *
	 * <p>The result is identical to calling
	 * {@link BigDecimal#compareTo(BigDecimal)} on the underlying values.
	 *
	 * <p><strong>Developer note:</strong> This method is "just here honestly"
	 * for convenience and delegates directly to {@code BigDecimal.compareTo()}.
	 *
	 * @param other the {@code Decimal} to compare against
	 * @return a negative integer, zero, or a positive integer as this
	 *         {@code Decimal} is less than, equal to, or greater than {@code other}
	 */
	@Override
	public int compareTo(Decimal other) {
		return value.compareTo(other.value);
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is numerically equal
	 * to the specified {@code Decimal}.
	 *
	 * <p>This method uses {@link #compareTo(Decimal)} under the hood,
	 * meaning scale differences are ignored (e.g., 1.0 and 1.00 compare as equal).
	 *
	 * @param other the {@code Decimal} to compare against
	 * @return {@code true} if the two values are equal in numeric value
	 */
	public boolean equals(Decimal other) {
		return compareTo(other) == 0;
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is numerically
	 * not equal to the specified {@code Decimal}.
	 *
	 * @param other the {@code Decimal} to compare against
	 * @return {@code true} if the two values differ in numeric value
	 */
	public boolean notEqual(Decimal other) {
		return compareTo(other) != 0;
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is strictly less than
	 * the specified {@code Decimal}.
	 *
	 * @param other the {@code Decimal} to compare against
	 * @return {@code true} if this value is less than {@code other}
	 */
	public boolean lessThan(Decimal other) {
		return compareTo(other) < 0;
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is strictly greater than
	 * the specified {@code Decimal}.
	 *
	 * @param other the {@code Decimal} to compare against
	 * @return {@code true} if this value is greater than {@code other}
	 */
	public boolean greaterThan(Decimal other) {
		return compareTo(other) > 0;
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is less than or equal to
	 * the specified {@code Decimal}.
	 *
	 * @param other the {@code Decimal} to compare against
	 * @return {@code true} if this value is ≤ {@code other}
	 */
	public boolean lessThanOrEqualTo(Decimal other) {
		return compareTo(other) <= 0;
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is greater than or equal to
	 * the specified {@code Decimal}.
	 *
	 * @param other the {@code Decimal} to compare against
	 * @return {@code true} if this value is ≥ {@code other}
	 */
	public boolean greaterThanOrEqualTo(Decimal other) {
		return compareTo(other) >= 0;
	}

	/**
	 * Enumeration of bound types used for interval comparisons.
	 *
	 * <ul>
	 *   <li>{@link #EXCLUSIVE} – the boundary is excluded from the interval.</li>
	 *   <li>{@link #INCLUSIVE} – the boundary is included in the interval.</li>
	 * </ul>
	 */
	public static enum BoundType {
		EXCLUSIVE,
		INCLUSIVE
	}

	/**
	 * Checks whether this {@code Decimal} lies within the specified interval.
	 *
	 * <p>The interval is defined by a start value, an end value, and the
	 * inclusivity or exclusivity of each bound.</p>
	 *
	 * <p>Examples (using bracket/parenthesis notation):</p>
	 * <ul>
	 *   <li>{@code x.inInterval(a, b, INCLUSIVE, INCLUSIVE)} → interval {@code [a, b]} → checks if {@code a ≤ x ≤ b}</li>
	 *   <li>{@code x.inInterval(a, b, EXCLUSIVE, INCLUSIVE)} → interval {@code (a, b]} → checks if {@code a < x ≤ b}</li>
	 *   <li>{@code x.inInterval(a, b, INCLUSIVE, EXCLUSIVE)} → interval {@code [a, b)} → checks if {@code a ≤ x < b}</li>
	 *   <li>{@code x.inInterval(a, b, EXCLUSIVE, EXCLUSIVE)} → interval {@code (a, b)} → checks if {@code a < x < b}</li>
	 * </ul>
	 *
	 * @param start the lower bound of the interval
	 * @param end   the upper bound of the interval
	 * @param left  the bound type for the lower bound (inclusive or exclusive)
	 * @param right the bound type for the upper bound (inclusive or exclusive)
	 * @return {@code true} if this value is within the interval, {@code false} otherwise
	 */
	public boolean inInterval(Decimal start, Decimal end, BoundType left, BoundType right) {
		return switch (left) {
		case EXCLUSIVE -> greaterThan(start);
		case INCLUSIVE -> greaterThanOrEqualTo(start);
		} && switch (right) {
		case EXCLUSIVE -> lessThan(end);
		case INCLUSIVE -> lessThanOrEqualTo(end);
		};
	}

	/**
	 * Compares this {@code Decimal} with the specified object for equality.
	 *
	 * <p>This implementation delegates directly to
	 * {@link BigDecimal#equals(Object)}, which is sensitive to scale
	 * (e.g., {@code new BigDecimal("1.0").equals(new BigDecimal("1.00"))}
	 * is {@code false}).
	 *
	 * <p><strong>Developer note:</strong> This may become redundant if
	 * {@code Decimal} is changed to normalize (e.g., strip trailing zeros)
	 * on construction, since {@link #equals(Decimal)} already handles
	 * numeric equality.
	 *
	 * @param other the object to compare with
	 * @return {@code true} if the object is equal to this {@code Decimal}
	 */
	@Override
	public boolean equals(Object other) {
		return value.equals(other);
	}

	/**
	 * Returns the sign of this {@code Decimal}.
	 *
	 * <p>The result is {@code -1} if negative, {@code 0} if zero,
	 * and {@code 1} if positive.</p>
	 *
	 * @return the signum of this value
	 */
	public int signum() {
		return value.signum();
	}

	/**
	 * Returns {@code true} if this {@code Decimal} has no fractional part.
	 *
	 * @return {@code true} if this value is an integer, {@code false} otherwise
	 */
	public boolean isInteger() {
		return this.equals(floor());
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is zero or positive.
	 *
	 * @return {@code true} if this value ≥ 0
	 */
	public boolean isPositive() {
		return signum() >= 0;
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is strictly negative.
	 *
	 * @return {@code true} if this value {@literal <} 0
	 */
	public boolean isNegative() {
		return signum() < 0;
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is an even integer.
	 *
	 * <p>An even number is defined as an integer divisible by 2 with no remainder.
	 * If this value is not an integer, an {@link IllegalStateException} is thrown.</p>
	 *
	 * @return {@code true} if this value is even
	 * @throws IllegalStateException if this value is not an integer
	 */
	public boolean isEven() {
		if (!isInteger())
			throw new IllegalStateException("Non-integers are neither even or odd");
		return remainder(TWO).equals(ZERO);
	}

	/**
	 * Returns {@code true} if this {@code Decimal} is an odd integer.
	 *
	 * <p>An odd number is defined as an integer that is not divisible by 2
	 * without a remainder. If this value is not an integer, an
	 * {@link IllegalStateException} is thrown.</p>
	 *
	 * @return {@code true} if this value is odd
	 * @throws IllegalStateException if this value is not an integer
	 */
	public boolean isOdd() {
		return !isEven();
	}

	/**
	 * Returns a new {@code Decimal} whose value is this {@code Decimal}
	 * rounded to the nearest integer using {@link RoundingMode#HALF_UP}.
	 *
	 * <p>This is the conventional "schoolbook" rounding mode, where ties
	 * (e.g. 2.5) are rounded up to the next integer (3).</p>
	 *
	 * @return a {@code Decimal} representing this value rounded to the nearest integer
	 */
	public Decimal round() {
		return setScale(0, RoundingMode.HALF_UP);
	}

	/**
	 * Returns a new {@code Decimal} whose value is this {@code Decimal}
	 * rounded to the nearest integer value towards positive infinity.
	 *
	 * <p>This is equivalent to calling
	 * {@code setScale(0, RoundingMode.CEILING)}.</p>
	 *
	 * @return a {@code Decimal} rounded upward to the nearest integer
	 */
	public Decimal ceiling() {
		return setScale(0, RoundingMode.CEILING);
	}

	/**
	 * Returns a new {@code Decimal} whose value is this {@code Decimal}
	 * rounded to the nearest integer value towards negative infinity.
	 *
	 * <p>This is equivalent to calling
	 * {@code setScale(0, RoundingMode.FLOOR)}.</p>
	 *
	 * @return a {@code Decimal} rounded downward to the nearest integer
	 */
	public Decimal floor() {
		return setScale(0, RoundingMode.FLOOR);
	}

	/**
	 * Linearly interpolates between this {@code Decimal} and another.
	 *
	 * <p>{@code alpha} must be in the range [0, 1]. When {@code alpha = 0},
	 * the result is this value; when {@code alpha = 1}, the result is {@code other}.
	 * Intermediate values produce a weighted average.</p>
	 *
	 * <p><strong>Developer note:</strong> All {@code lerp} overloads
	 * delegate to this method.</p>
	 *
	 * @param other  the target value
	 * @param alpha  the interpolation parameter, between 0 and 1 inclusive
	 * @param context the math context specifying precision and rounding
	 * @return the interpolated value
	 * @throws IllegalArgumentException if {@code alpha} is outside [0, 1]
	 */
	public Decimal lerp(Decimal other, Decimal alpha, MathContext context) {
		if (alpha.greaterThanOrEqualTo(ZERO) && alpha.lessThanOrEqualTo(ONE))
			return (ONE.subtract(alpha, context))
					.multiply(this, context)
					.add(alpha.multiply(other, context), context);
		else
			throw new IllegalArgumentException("Alpha value must be between 0 inclusive and 1 inclusive");
	}

	/**
	 * Linearly interpolates between this {@code Decimal} and another
	 * using the default math context.
	 *
	 * @param other the target value
	 * @param alpha the interpolation parameter, between 0 and 1 inclusive
	 * @return the interpolated value
	 */
	public Decimal lerp(Decimal other, Decimal alpha) {
		return lerp(other, alpha, DEFAULT_CONTEXT);

	}

	/**
	 * Linearly interpolates between this {@code Decimal} and another
	 * using a primitive {@code double} alpha value.
	 *
	 * @param other  the target value
	 * @param alpha  the interpolation parameter, between 0 and 1 inclusive
	 * @param context the math context specifying precision and rounding
	 * @return the interpolated value
	 */
	public Decimal lerp(Decimal other, double alpha, MathContext context) {
		return lerp(other, new Decimal(alpha), context);
	}

	/**
	 * Linearly interpolates between this {@code Decimal} and another
	 * using a primitive {@code double} alpha value and the default math context.
	 *
	 * @param other the target value
	 * @param alpha the interpolation parameter, between 0 and 1 inclusive
	 * @return the interpolated value
	 */
	public Decimal lerp(Decimal other, double alpha) {
		return lerp(other, alpha, DEFAULT_CONTEXT);
	}

	/**
	 * Returns the average (arithmetic mean) of this {@code Decimal}
	 * and another, using the specified math context.
	 *
	 * <p>The result is computed as
	 * {@code (this + other) × 0.5}, using the provided
	 * {@link MathContext} for precision and rounding.</p>
	 *
	 * <p><strong>Implementation note:</strong> Although equivalent to
	 * calling {@code lerp(other, HALF, context)}, this method directly
	 * computes the mean via addition and multiplication for clarity.</p>
	 *
	 * @param other   the value to average with
	 * @param context the math context specifying precision and rounding
	 * @return the arithmetic mean of the two values
	 */
	public Decimal average(Decimal other, MathContext context) {
		//		return lerp(other, HALF, context);
		return add(other, context).multiply(HALF, context);
	}

	/**
	 * Returns the average (arithmetic mean) of this {@code Decimal}
	 * and another, using the default math context.
	 *
	 * @param other the value to average with
	 * @return the arithmetic mean of the two values
	 */
	public Decimal average(Decimal other) {
		return average(other, DEFAULT_CONTEXT);
	}

	/**
	 * Returns the factorial of this {@code Decimal}, using the supplied math context.
	 *
	 * <p>The value must be a non-negative integer. If the value is not an integer
	 * or is negative, an {@link ArithmeticException} is thrown.</p>
	 *
	 * <p><strong>Developer note:</strong> This implementation uses recursive
	 * divide-and-conquer multiplication via {@code factorialHelper}. Since
	 * {@link BigInteger} multiplications do not require a {@link MathContext},
	 * this method is less efficient and has been deprecated.</p>
	 *
	 * @param context the math context specifying precision and rounding
	 * @return the factorial of this value
	 * @throws ArithmeticException if the value is not a non-negative integer
	 * @deprecated use {@link #factorial()} instead, which uses a more efficient
	 *             {@link BigInteger}-based implementation
	 */
	@Deprecated
	public Decimal factorial(MathContext context) {
		if (!isInteger() || isNegative())
			throw new ArithmeticException("value must be a non-negative integer");
		if (lessThan(TWO)) return ONE;
		return factorialHelper(TWO, this, context);
	}

	/**
	 * Returns the factorial of this {@code Decimal}, using an internal
	 * {@link BigInteger}-based implementation.
	 *
	 * <p>The value must be a non-negative integer. If the value is not an integer
	 * or is negative, an {@link ArithmeticException} is thrown.</p>
	 *
	 * <p><strong>Developer note:</strong> This version avoids {@link MathContext}
	 * overhead by delegating to a pure {@link BigInteger} divide-and-conquer
	 * multiplication strategy, which is more efficient for large values.</p>
	 *
	 * @return the factorial of this value
	 * @throws ArithmeticException if the value is not a non-negative integer
	 */
	public Decimal factorial() {
		if (!isInteger() || isNegative())
			throw new ArithmeticException("value must be a non-negative integer");
		if (lessThan(TWO)) return ONE;
		return new Decimal(factorialHelper(BigInteger.TWO, this.toBigInteger()));
	}

	/**
	 * Helper method for computing factorial recursively using
	 * a divide-and-conquer approach, with {@code Decimal} arithmetic.
	 *
	 * <p><strong>Developer note:</strong> This method introduces significant
	 * overhead due to frequent conversions between {@code Decimal} and
	 * {@code BigDecimal}. It has been deprecated in favor of the
	 * {@link BigInteger}-based implementation.</p>
	 *
	 * @param low   the lower bound of the multiplication range
	 * @param high  the upper bound of the multiplication range
	 * @param context the math context specifying precision and rounding
	 * @return the product of all integers in the range [low, high]
	 * @deprecated use the {@link BigInteger}-based factorial helper instead
	 */
	@Deprecated
	private static Decimal factorialHelper(Decimal low, Decimal high, MathContext context) {
		if (low.greaterThan(high)) 	return ONE;
		if (low.equals(high)) 		return low;
		Decimal mid 	= low.average(high, context).floor();
		Decimal left 	= factorialHelper(low, mid, context);
		Decimal right 	= factorialHelper(mid.add(ONE, context), high, context);
		return left.multiply(right);
	}

	/**
	 * Helper method for computing factorial recursively using
	 * a divide-and-conquer approach, with {@link BigInteger} arithmetic.
	 *
	 * <p><strong>Developer note:</strong> This implementation avoids
	 * precision contexts entirely and is significantly faster and more
	 * memory-efficient than the {@code Decimal}-based version.</p>
	 *
	 * @param low  the lower bound of the multiplication range
	 * @param high the upper bound of the multiplication range
	 * @return the product of all integers in the range [low, high]
	 */
	private static BigInteger factorialHelper(BigInteger low, BigInteger high) {
		if (low.compareTo(high) > 0) 	return BigInteger.ONE;
		if (low.compareTo(high) == 0) 	return low;
		BigInteger mid 		= low.add(high).shiftRight(1);
		BigInteger left 	= factorialHelper(low, mid);
		BigInteger right 	= factorialHelper(mid.add(BigInteger.ONE), high);
		return left.multiply(right);
	}

	/**
	 * Returns a new {@code Decimal} whose value is this {@code Decimal}
	 * shifted left by the specified number of bits.
	 *
	 * <p>Equivalent to multiplying the integer value by {@code 2^n}.
	 * Only valid if this {@code Decimal} is an integer.</p>
	 *
	 * @param n the number of bits to shift
	 * @return a new {@code Decimal} equal to {@code this << n}
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public Decimal shiftLeft(int n) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger().shiftLeft(n));
	}

	/**
	 * Returns a new {@code Decimal} whose value is this {@code Decimal}
	 * shifted right by the specified number of bits.
	 *
	 * <p>Equivalent to dividing the integer value by {@code 2^n}.
	 * Only valid if this {@code Decimal} is an integer.</p>
	 *
	 * @param n the number of bits to shift
	 * @return a new {@code Decimal} equal to {@code this >> n}
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public Decimal shiftRight(int n) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger().shiftRight(n));
	}

	/**
	 * Returns the bitwise AND of this {@code Decimal} and the specified value.
	 *
	 * @param value the other operand
	 * @return a new {@code Decimal} representing {@code this & value}
	 * @throws IllegalArgumentException if either value is not an integer
	 */
	public Decimal and(Decimal value) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger()
				.and(Decimals.requireInteger(value).toBigInteger()));
	}

	/**
	 * Returns the bitwise OR of this {@code Decimal} and the specified value.
	 *
	 * @param value the other operand
	 * @return a new {@code Decimal} representing {@code this | value}
	 * @throws IllegalArgumentException if either value is not an integer
	 */
	public Decimal or(Decimal value) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger()
				.or(Decimals.requireInteger(value).toBigInteger()));
	}

	/**
	 * Returns the bitwise XOR of this {@code Decimal} and the specified value.
	 *
	 * @param value the other operand
	 * @return a new {@code Decimal} representing {@code this ^ value}
	 * @throws IllegalArgumentException if either value is not an integer
	 */
	public Decimal xor(Decimal value) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger()
				.xor(Decimals.requireInteger(value).toBigInteger()));
	}

	/**
	 * Returns the bitwise NOT of this {@code Decimal}.
	 *
	 * @return a new {@code Decimal} representing {@code ~this}
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public Decimal not() {
		return new Decimal(Decimals.requireInteger(this).toBigInteger().not());
	}

	/**
	 * Returns the bitwise AND-NOT of this {@code Decimal} and the specified value.
	 *
	 * @param value the other operand
	 * @return a new {@code Decimal} representing {@code this & ~value}
	 * @throws IllegalArgumentException if either value is not an integer
	 */
	public Decimal andNot(Decimal value) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger()
				.andNot(Decimals.requireInteger(value).toBigInteger()));
	}

	/**
	 * Returns the bitwise NAND of this {@code Decimal} and the specified value.
	 *
	 * @param value the other operand
	 * @return a new {@code Decimal} representing {@code ~(this & value)}
	 * @throws IllegalArgumentException if either value is not an integer
	 */
	public Decimal nand(Decimal value) {
		return and(value).not();
	}

	/**
	 * Returns the bitwise NOR of this {@code Decimal} and the specified value.
	 *
	 * @param value the other operand
	 * @return a new {@code Decimal} representing {@code ~(this | value)}
	 * @throws IllegalArgumentException if either value is not an integer
	 */
	public Decimal nor(Decimal value) {
		return or(value).not();
	}

	/**
	 * Returns the bitwise XNOR of this {@code Decimal} and the specified value.
	 *
	 * @param value the other operand
	 * @return a new {@code Decimal} representing {@code ~(this ^ value)}
	 * @throws IllegalArgumentException if either value is not an integer
	 */
	public Decimal xnor(Decimal value) {
		return xor(value).not();
	}

	/**
	 * Tests whether the bit at the specified index is set.
	 *
	 * @param n the bit index (0 is the least significant bit)
	 * @return {@code true} if the bit is set, {@code false} otherwise
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public boolean testBit(int n) {
		return Decimals.requireInteger(this).toBigInteger().testBit(n);
	}

	/**
	 * Returns a new {@code Decimal} with the bit at the specified index set.
	 *
	 * @param n the bit index
	 * @return a new {@code Decimal} with the specified bit set
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public Decimal setBit(int n) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger().setBit(n));
	}

	/**
	 * Returns a new {@code Decimal} with the bit at the specified index cleared.
	 *
	 * @param n the bit index
	 * @return a new {@code Decimal} with the specified bit cleared
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public Decimal clearBit(int n) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger().clearBit(n));
	}

	/**
	 * Returns a new {@code Decimal} with the bit at the specified index flipped.
	 *
	 * @param n the bit index
	 * @return a new {@code Decimal} with the specified bit flipped
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public Decimal flipBit(int n) {
		return new Decimal(Decimals.requireInteger(this).toBigInteger().flipBit(n));
	}

	/**
	 * Returns the number of bits in the minimal two's-complement representation
	 * of this {@code Decimal}.
	 *
	 * @return the bit length of this integer value
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public int bitLength() {
		return Decimals.requireInteger(this).toBigInteger().bitLength();
	}

	/**
	 * Returns the number of set bits (population count) in the two's-complement
	 * representation of this {@code Decimal}.
	 *
	 * @return the number of one-bits in this value
	 * @throws IllegalArgumentException if this value is not an integer
	 */
	public int bitCount() {
		return Decimals.requireInteger(this).toBigInteger().bitCount();
	}

	/**
	 * Computes the remainder of this value divided by the given {@code divisor},
	 * using floor division.
	 *
	 * @param divisor the divisor
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the remainder after division
	 */
	public Decimal mod(Decimal divisor, MathContext context) {
		return subtract(divisor.multiply(divide(divisor, context).floor(), context), context);
	}

	/**
	 * Computes the remainder of this value divided by the given {@code divisor},
	 * using the default math context.
	 *
	 * @param divisor the divisor
	 * @return the remainder after division
	 */
	public Decimal mod(Decimal divisor) {
		return mod(divisor, DEFAULT_CONTEXT);
	}

	/**
	 * Raises this value to the power of the given {@code exponent}.
	 *
	 * @param exponent the exponent
	 * @param context  the {@link MathContext} specifying precision and rounding
	 * @return the result of {@code this^exponent}
	 */
	public Decimal pow(Decimal exponent, MathContext context) {
		return Exponentiation.exponentiation(this, exponent, context);
	}

	/**
	 * Computes the square of this value.
	 *
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the result of {@code this^2}
	 */
	public Decimal squared(MathContext context) {
		if (isInteger()) {
			Decimal shiftLeft = shiftLeft(1);
			return shiftLeft.isNegative() ? shiftLeft.negate() : shiftLeft;
		} else
			return pow(TWO, context);
	}

	/**
	 * Extracts the n-th root of this value.
	 *
	 * @param degree  the degree of the root
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the result of {@code this^(1/degree)}
	 */
	public Decimal root(Decimal degree, MathContext context) {
		return RootExtraction.rootExtraction(this, degree, context);
	}

	/**
	 * Computes the square root of this value.
	 *
	 * @param context the {@link MathContext} specifying precision and rounding
	 * @return the square root of this value
	 */
	public Decimal sqrt(MathContext context) {
		return root(TWO, context);
	}

	/**
	 * Returns this value incremented by one.
	 *
	 * <p>Equivalent to {@code this + 1} with unlimited precision.</p>
	 *
	 * @return a new {@code Decimal} equal to {@code this + 1}
	 */
	public Decimal plusOne() {
		return add(ONE, MathContext.UNLIMITED);
	}

	public Decimal minusOne() {
		return subtract(ONE, MathContext.UNLIMITED);
	}

	public Decimal min(Decimal other) {
		return lessThan(other) ? this : other;
	}

	public Decimal max(Decimal other) {
		return greaterThan(other) ? this : other;
	}

	public Decimal reciprocal(MathContext context) {
		return ONE.divide(this, context);
	}

	public Decimal echo() {
		System.out.println(this);
		return this;
	}

	public Decimal echo(Function<Decimal, String> format) {
		System.out.println(format.apply(this));
		return this;
	}

	public Decimal abs() {
		return new Decimal(value.abs());
	}

	public Decimal clamp(Decimal min, Decimal max) {
		if (min.greaterThanOrEqualTo(max))
			throw new IllegalArgumentException(String.format("min: %s has to be strictly less than max: %s", min, max));
		return max(min).min(max);
	}

}
