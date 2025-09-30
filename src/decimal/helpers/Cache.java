package decimal.helpers;

import decimal.Decimal;

/**
 * Simple fixed-size cache for storing recent {@link Decimal} values.
 *
 * <p>This cache is primarily used to detect repeated values during
 * iterative algorithms (such as Newton–Raphson) to prevent infinite loops.</p>
 *
 * <p>The cache maintains the most recent values in a FIFO-like manner:
 * <ul>
 *   <li>New values are inserted at the front.</li>
 *   <li>Older values are shifted back.</li>
 *   <li>Equality is determined using {@code Decimal.equals()}.</li>
 * </ul>
 * </p>
 */
public class Cache {

	/**
	 * Internal storage array holding the cached {@link Decimal} values.
	 *
	 * <p>The array maintains a fixed size determined at construction,
	 * with newer values placed at the front and older ones shifted back.</p>
	 */
	private Decimal[] cache;

	public Cache(int size, Decimal initialValue) {
		cache = new Decimal[size];
		for (int i = 0; i < size; i++) {
			cache[i] = initialValue;
		}
	}

	public boolean contains(Decimal value) {
		for (Decimal x : cache) {
			if (x.equals(value)) return true;
		}
		return false;
	}

	public void update(Decimal value) {
		for (int i = cache.length - 1; i > 0; i--)
			cache[i] = cache[i-1];
		cache[0] = value;
	}

}
