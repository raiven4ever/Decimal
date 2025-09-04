package decimal.operations;

import java.math.MathContext;

import decimal.Decimal;

/**
 * Utility class providing basic arithmetic operations for {@link Decimal}.
 *
 * <p>This class defines the fundamental four operations
 * (addition, subtraction, multiplication, and division) as
 * {@code static} methods.</p>
 *
 * <p><strong>Developer notes:</strong></p>
 * <ul>
 *   <li>This is the first in a planned series of operation helper classes.</li>
 *   <li>Keeping these as static methods avoids bloating {@code Decimal}
 *       with low-level arithmetic details.</li>
 *   <li>Each method delegates directly to the corresponding
 *       {@link BigDecimal} operation, wrapped back into a {@code Decimal}.</li>
 * </ul>
 *
 * <p>All methods return new immutable {@code Decimal} instances.</p>
 *
 * <p>This class cannot be instantiated.</p>
 */
public class ArithmeticBasics {

	/**
	 * Private constructor to prevent instantiation.
	 *
	 * @throws AssertionError always, since this class is not meant to be instantiated
	 */
	private ArithmeticBasics() {
		throw new AssertionError("No instances for you!");
	}

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

