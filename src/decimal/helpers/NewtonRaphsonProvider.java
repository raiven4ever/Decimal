package decimal.helpers;

import java.math.MathContext;
import java.util.function.Function;

import decimal.Decimal;

/**
 * Utility class for solving equations using the Newton–Raphson method
 * with arbitrary-precision {@link Decimal} values.
 *
 * <p>This class supports both basic and extended configurations:
 * <ul>
 *   <li>Providing only the target function {@code f} and its derivative
 *       {@code f'} for root-finding.</li>
 *   <li>Optionally supplying a clamping mechanism with bounds
 *       to constrain iteration results within a given interval.</li>
 * </ul>
 * </p>
 *
 * <p>To improve stability, the solver caches recent guesses and stops
 * iterating if results converge or start repeating.</p>
 *
 * @see Decimal
 * @see java.math.MathContext
 */
public class NewtonRaphsonProvider {

	private Function<Decimal, Decimal> f;
	private Function<Decimal, Decimal> fPrime;
	private Function<Decimal, Decimal> clampingMechanism;
	private Decimal min;
	private Decimal max;

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
