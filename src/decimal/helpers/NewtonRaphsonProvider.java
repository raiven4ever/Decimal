package decimal.helpers;

import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.function.Function;

import static decimal.Decimal.*;

import decimal.Decimal;

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
