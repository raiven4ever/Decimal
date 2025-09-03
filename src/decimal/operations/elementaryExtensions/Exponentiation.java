package decimal.operations.elementaryExtensions;

import java.math.MathContext;

import decimal.Decimal;
import decimal.helpers.FactorialSupplier;
import decimal.helpers.Summation;

public class Exponentiation {

	public static Decimal exp(Decimal exponent, MathContext context) {
		if (exponent.equals(Decimal.ZERO))
				return Decimal.ONE;
		if (exponent.lessThan(Decimal.ZERO))
			return Decimal.ONE.divide(exp(exponent.negate(), context), context);
		if (exponent.isInteger()) {
			integerExponentiation(e(context), exponent, context);
		}
		return null;
	}
	
	private static void integerExponentiation(Decimal e, Decimal exponent, MathContext context) {
		// TODO Auto-generated method stub
		
	}

	public static Decimal e(MathContext context) {
		// i'm like, aware of better methods like binary splitting or something
		// but im choosing it to do the old way so i can be limited by the precision allowed by the math context
		// instead of arbitrarily choosing a specific arbitrary number of terms or choosing an arbitrary precision
		FactorialSupplier nFactorial = new FactorialSupplier(0);
		Summation e = new Summation(n -> Decimal.ONE.divide(nFactorial.nextPre(), context));
		return e.sumInfinite(0, context);
	}

}
