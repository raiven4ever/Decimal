package decimal.operations.elementaryExtensions;

import static decimal.Decimal.ONE;
import static decimal.Decimal.ZERO;
import static decimal.operations.elementaryExtensions.Exponentiation.*;

import java.math.MathContext;

import decimal.Decimal;

public class RootExtraction {
	
	private static Decimal integerRootExtraction(Decimal a, Decimal n, MathContext context) {
		Decimal subtract = n.subtract(ONE);
		Decimal add = n.add(ONE);
		
		Decimal result = ONE;
		while (true) {
			Decimal numerator = subtract.multiply(exponentiation(result, n, context)).add(add.multiply(a, context), context);
			Decimal denominator = add.multiply(exponentiation(result, n, context)).add(subtract.multiply(a, context), context);
			Decimal fraction = numerator.divide(denominator, context);
			Decimal guess = result.multiply(fraction, context);
			
			if (guess.equals(result)) break;
			result = guess;
		}
		return result;
	}
	
	private static Decimal realRootExtraction(Decimal a, Decimal n, MathContext context) {
		return exponentiation(a, ONE.divide(n, context), context);
	}
	
	public static Decimal rootExtraction(Decimal a, Decimal n, MathContext context) {
		if (a.equals(ZERO) && n.greaterThan(ZERO))
			return ZERO;
		else if (n.isInteger()) {
			if (a.greaterThan(ZERO)) {
				if (n.greaterThan(ZERO))
					return integerRootExtraction(a, n, context);
				else if (n.lessThan(ZERO))
					return ONE.divide(integerRootExtraction(a, n.negate(), context), context);
			} else if (a.lessThan(ZERO) && n.isOdd())
				return integerRootExtraction(a, n, context).negate();
		} 
		else if (a.greaterThan(ZERO))
			realRootExtraction(a, n, context);
		throw new IllegalArgumentException(String.format("undefined for radicand %s, degree %s", a, n));
	}

}
