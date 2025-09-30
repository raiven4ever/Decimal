package decimal.helpers;

import java.math.MathContext;
import java.util.function.Function;

import decimal.Decimal;

/**
 * Utility class for solving equations using the Newton–Raphson method
 * with arbitrary-precision {@link Decimal} values.
 *
 * <p>This implementation is intentionally lightweight and does not
 * enforce advanced guardrails. The caller is responsible for ensuring
 * that the provided derivative {@code f'} actually corresponds to the
 * target function {@code f}. No validation or consistency checks are
 * performed.</p>
 *
 * <p>Two usage modes are supported:
 * <ul>
 *   <li>Basic: provide {@code f} and {@code f'} only.</li>
 *   <li>Extended: also provide an optional clamping mechanism and
 *       interval bounds to constrain iteration results.</li>
 * </ul>
 * </p>
 *
 * <p>Iteration stops when successive guesses converge or repeat,
 * as tracked by a small cache of previous values.</p>
 *
 * @see Decimal
 * @see java.math.MathContext
 */
public class NewtonRaphsonProvider {

	/**
	 * The target function {@code f(x)} whose root is to be solved.
	 */
	private Function<Decimal, Decimal> f;

	/**
	 * The derivative of the target function {@code f′(x)}.
	 */
	private Function<Decimal, Decimal> fPrime;

	/**
	 * Optional clamping mechanism applied to each iteration step
	 * to enforce custom constraints on candidate values.
	 */
	private Function<Decimal, Decimal> clampingMechanism;

	/**
	 * Optional lower bound for valid iteration values.
	 */
	private Decimal min;

	/**
	 * Optional upper bound for valid iteration values.
	 */
	private Decimal max;

	/**
	 * Creates a Newton–Raphson provider with the given function and its derivative.
	 *
	 * <p>No validation is performed to confirm that {@code fPrime} is the
	 * derivative of {@code f}; correctness is the caller’s responsibility.</p>
	 *
	 * @param f      the target function {@code f(x)}
	 * @param fPrime the derivative function {@code f′(x)}
	 */
	public NewtonRaphsonProvider(Function<Decimal, Decimal> f, Function<Decimal, Decimal> fPrime) {
		this.f = f;
		this.fPrime = fPrime;
	}

	public NewtonRaphsonProvider(Function<Decimal, Decimal> f, Function<Decimal, Decimal> fPrime,
			Function<Decimal, Decimal> clampingMechanism, Decimal min, Decimal max) {
		super();
		this.f = f;
		this.fPrime = fPrime;
		this.clampingMechanism = clampingMechanism;
		this.min = min;
		this.max = max;
	}

	public Decimal solve(Decimal start, MathContext context) {
		Decimal result = start;
		Cache cache = new Cache(2, start);
		while (true) {
			Decimal guess = clamp(result.subtract(f.apply(result).divide(fPrime.apply(result), context), context));
			if (guess.equals(result) || cache.contains(guess)) break;
			cache.update(guess);
			result = guess;
		}
		return clamp(result);
	}

	private Decimal clamp(Decimal value) {
		return 
				min != null && max != null && clampingMechanism != null ? clampingMechanism.apply(value) : 
					min != null && max != null ? value.clamp(min, max) :
						value;
	}

}
