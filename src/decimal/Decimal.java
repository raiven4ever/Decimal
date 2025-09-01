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
     * Serial version identifier used during deserialization
     * to verify that the sender and receiver of a serialized
     * object maintain binary compatibility.
     */
	private static final long serialVersionUID = 1L;
	
	/**
     * Default {@link MathContext} to be used when no explicit
     * context is provided by the caller.
     * <p>
     * The default is {@link MathContext#DECIMAL128}, providing
     * 34 digits of precision with the IEEE 754R Decimal128 format.
     */
	private static final MathContext DEFAULT_CONTEXT = MathContext.DECIMAL128;
	
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
	 * Returns {@code true} if this {@code Decimal} has no fractional part.
	 *
	 * @return {@code true} if this value is an integer, {@code false} otherwise
	 */
	public boolean isInteger() {
	    return this.equals(floor());
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

}
