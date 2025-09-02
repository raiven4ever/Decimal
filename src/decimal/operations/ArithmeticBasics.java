package decimal.operations;

import java.math.MathContext;

import decimal.Decimal;

/**
 * Utility class providing basic arithmetic operations for {@link Decimal}.
 *
 * <p><strong>Developer notes:</strong>
 * <ul>
 *   <li>This class is the first in a series
 *       of operation helpers extracted into separate utility classes.</li>
 *   <li>Operation implementations are placed here as static methods
 *       for now, rather than as instance methods, to keep {@code Decimal}
 *       itself lightweight.</li>
 * </ul>
 *
 * <p>All methods return a new {@code Decimal} instance wrapping the result
 * of the corresponding {@link BigDecimal} operation with the given
 * {@link MathContext}.
 */
public class ArithmeticBasics {

	/**
	 * Returns a new {@code Decimal} whose value is the sum of
	 * {@code firstOperand} and {@code secondOperand}, using the supplied
	 * {@link MathContext}.
	 *
	 * @param firstOperand  the first addend
	 * @param secondOperand the second addend
	 * @param context       the {@link MathContext} specifying precision and rounding
	 * @return a {@code Decimal} representing {@code firstOperand + secondOperand}
	 */
	public static Decimal addition(Decimal firstOperand, Decimal secondOperand, MathContext context) {
		return new Decimal(firstOperand.toBigDecimal().add(secondOperand.toBigDecimal(), context));
	}

	/**
	 * Returns a new {@code Decimal} whose value is the difference of
	 * {@code firstOperand} and {@code secondOperand}, using the supplied
	 * {@link MathContext}.
	 *
	 * @param firstOperand  the minuend
	 * @param secondOperand the subtrahend
	 * @param context       the {@link MathContext} specifying precision and rounding
	 * @return a {@code Decimal} representing {@code firstOperand - secondOperand}
	 */
	public static Decimal subtraction(Decimal firstOperand, Decimal secondOperand, MathContext context) {
		return new Decimal(firstOperand.toBigDecimal().subtract(secondOperand.toBigDecimal(), context));
	}

	/**
	 * Returns a new {@code Decimal} whose value is the product of
	 * {@code firstOperand} and {@code secondOperand}, using the supplied
	 * {@link MathContext}.
	 *
	 * @param firstOperand  the multiplicand
	 * @param secondOperand the multiplier
	 * @param context       the {@link MathContext} specifying precision and rounding
	 * @return a {@code Decimal} representing {@code firstOperand × secondOperand}
	 */
	public static Decimal multiplication(Decimal firstOperand, Decimal secondOperand, MathContext context) {
		return new Decimal(firstOperand.toBigDecimal().multiply(secondOperand.toBigDecimal(), context));
	}

	/**
	 * Returns a new {@code Decimal} whose value is the quotient of
	 * {@code firstOperand} divided by {@code secondOperand}, using the supplied
	 * {@link MathContext}.
	 *
	 * @param firstOperand  the dividend
	 * @param secondOperand the divisor
	 * @param context       the {@link MathContext} specifying precision and rounding
	 * @return a {@code Decimal} representing {@code firstOperand ÷ secondOperand}
	 * @throws ArithmeticException if {@code secondOperand} is zero
	 */
	public static Decimal division(Decimal firstOperand, Decimal secondOperand, MathContext context) {
		return new Decimal(firstOperand.toBigDecimal().divide(secondOperand.toBigDecimal(), context));
	}
}

