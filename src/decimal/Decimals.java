package decimal;

import java.math.BigDecimal;

/**
 * Utility class for {@link Decimal}-related helper methods.
 *
 * <p>This class collects operations that are inconvenient or undesirable
 * to include directly in the {@code Decimal} class itself.</p>
 *
 * <p><strong>Developer notes:</strong></p>
 * <ul>
 *   <li>Acts as a companion class to {@code Decimal}, similar to how
 *       {@link java.util.Objects} supplements {@link Object}.</li>
 *   <li>Useful for validation and precondition checks.</li>
 * </ul>
 */
public class Decimals {

	/**
	 * Ensures that the given {@code Decimal} is an integer.
	 *
	 * <p>If the value is not an integer, throws an
	 * {@link IllegalArgumentException}. Otherwise, returns the same
	 * {@code Decimal} instance unchanged.</p>
	 *
	 * @param decimal the value to check
	 * @return the same {@code Decimal} if it is an integer
	 * @throws IllegalArgumentException if {@code decimal} is not an integer
	 */
	public static Decimal requireInteger(Decimal decimal) {
		if (!decimal.isInteger())
			throw new IllegalArgumentException(decimal.toString() + " must be an integer");
		return decimal;
	}

}

