package decimal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

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
	
	// =======================
	// Constructors
	// =======================

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
	 * <p>The string must conform to the syntax accepted by
	 * {@link BigDecimal#BigDecimal(String)}. Leading and trailing
	 * whitespace is permitted and will be ignored.</p>
	 *
	 * @param value the string representation of the decimal value
	 * @throws NumberFormatException if {@code value} is not a valid
	 *         representation of a {@code BigDecimal}
	 */
	public Decimal(String value) {
	    this.value = new BigDecimal(value);
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
	
	//Class conversions
	
	//this just gives the value
	public BigDecimal toBigDecimal() {
		return value;
	}
	
	//uses BigDecimal toBigInteger()
	public BigInteger toBigInteger() {
		return value.toBigInteger();
	}
	
	/*if this BigDecimal has too great a magnitude represent as a double, it will be converted to Double.NEGATIVE_INFINITY or Double.POSITIVE_INFINITY as appropriate.
	 * (verbatim from BigDecimal doubleValue() documentation)
	 * */
	public double toDouble() {
		return value.doubleValue();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return value.stripTrailingZeros().toString();
	}
	
	//helper enum class just for format method
	public static enum DecimalStringFormat{
		DEFAULT,
		PLAIN,
		ENGINEERING,
		SCIENTIFIC,
		PRESERVE_SCALE,
		STRIPPED,
	}
	
	//gives string representation of this decimal in a given format
	public String format(DecimalStringFormat format) {
		return switch (format) {
			case DEFAULT, STRIPPED -> value.stripTrailingZeros().toPlainString();
			case PLAIN -> value.toPlainString();
			case ENGINEERING -> value.toEngineeringString();
			case SCIENTIFIC -> toScientificString();
			case PRESERVE_SCALE -> value.toString();
		};
	}
	
	//honestly thinking that this method should be public, even though it is a helper method of the format method
	private String toScientificString() {
		//rough implementation
		
		//making the coefficient
		BigDecimal stripTrailingZeros = value.stripTrailingZeros();
		BigInteger unscaledValue = stripTrailingZeros.unscaledValue();
		StringBuilder builder = new StringBuilder(unscaledValue.toString());
		builder.insert(1, '.').toString();
		
		//making the exponent
		int exponent = unscaledValue.toString().length() - 1 - stripTrailingZeros.scale();
		builder.append('E');
		builder.append(exponent < 0 ? exponent : "+" + String.valueOf(exponent)); 
		
		return builder.toString();
	}

	// =======================
	// Arithmetic Basics
	// =======================

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


	// =======================
	// Arithmetic Basics (default context)
	// =======================

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

	@Override
	public int compareTo(Decimal other) {
		// TODO Auto-generated method stub
		return value.compareTo(other.value);
	}
	
	//Object niceties
	@Override
	public boolean equals(Object other) {
		// TODO Auto-generated method stub
		return value.equals(other);
	}

}
