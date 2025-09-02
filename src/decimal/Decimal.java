package decimal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import decimal.operations.ArithmeticBasics;

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
	    this.value = value;
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
	    this.value = new BigDecimal(value);
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
	        this.value = new BigDecimal(value);
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
	    this.value = new BigDecimal(value);
	}

	/**
	 * Creates a new {@code Decimal} from the specified {@code int} value.
	 *
	 * <p>Equivalent to calling {@code new BigDecimal(int)}.</p>
	 *
	 * @param value the {@code int} value to wrap
	 */
	public Decimal(int value) {
	    this.value = new BigDecimal(value);
	}

	/**
	 * Creates a new {@code Decimal} from the specified {@code long} value.
	 *
	 * <p>Equivalent to calling {@code new BigDecimal(long)}.</p>
	 *
	 * @param value the {@code long} value to wrap
	 */
	public Decimal(long value) {
	    this.value = new BigDecimal(value);
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
	        case DEFAULT, STRIPPED -> value.stripTrailingZeros().toPlainString();
	        case PLAIN -> value.toPlainString();
	        case ENGINEERING -> value.toEngineeringString();
	        case SCIENTIFIC -> toScientificString();
	        case PRESERVE_SCALE -> value.toString();
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
	 * Returns a new {@code Decimal} whose value is this {@code Decimal}
	 * rounded according to the supplied {@link MathContext}.
	 *
	 * <p>The result is equivalent to calling
	 * {@link BigDecimal#round(MathContext)} on the underlying value.</p>
	 *
	 * @param context the {@link MathContext} specifying precision and rounding mode
	 * @return a {@code Decimal} representing this value rounded according to {@code context}
	 */
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
	 * @return {@code true} if this value < 0
	 */
	public boolean isNegative() {
	    return signum() < 0;
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
	    if (low.greaterThan(high)) return ONE;
	    if (low.equals(high)) return low;
	    Decimal mid = low.average(high, context).floor();
	    Decimal left = factorialHelper(low, mid, context);
	    Decimal right = factorialHelper(mid.add(ONE, context), high, context);
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
	private BigInteger factorialHelper(BigInteger low, BigInteger high) {
	    if (low.compareTo(high) > 0) return BigInteger.ONE;
	    if (low.compareTo(high) == 0) return low;
	    BigInteger mid = low.add(high).shiftRight(1);
	    BigInteger left = factorialHelper(low, mid);
	    BigInteger right = factorialHelper(mid.add(BigInteger.ONE), high);
	    return left.multiply(right);
	}

}
