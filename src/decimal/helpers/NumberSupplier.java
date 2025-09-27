package decimal.helpers;

import decimal.Decimal;

/**
 * A generic supplier of sequentially generated {@link Decimal} values,
 * parameterized by an index {@code n}.
 *
 * <p>The interface provides access to the current value and index, as well as
 * methods for advancing the sequence either before ({@code pre}) or after
 * returning a value ({@code post}).</p>
 */
public interface NumberSupplier {

	/**
	 * Returns the current value of the sequence without advancing.
	 *
	 * @return the current value
	 */
	Decimal currentValue();

	/**
	 * Returns the current index {@code n} associated with the sequence value.
	 *
	 * @return the current index
	 */
	Decimal currentN();

	/**
	 * Advances the sequence by one step and returns the new current value.
	 *
	 * @return the next value in the sequence
	 */
	Decimal nextPre();

	/**
	 * Advances the sequence by the specified number of steps and returns
	 * the new current value.
	 *
	 * @param steps the number of steps to advance
	 * @return the value at the advanced position
	 */
	Decimal nextPre(int steps);

	/**
	 * Returns the current value, then advances the sequence by one step.
	 *
	 * @return the current value before advancing
	 */
	Decimal nextPost();

	/**
	 * Returns the current value, then advances the sequence by the specified
	 * number of steps.
	 *
	 * @param steps the number of steps to advance
	 * @return the current value before advancing
	 */
	Decimal nextPost(int steps);
}

