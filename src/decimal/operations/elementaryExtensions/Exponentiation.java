package decimal.operations.elementaryExtensions;

import java.math.MathContext;

import decimal.Decimal;

public class Exponentiation {
	
	private static final Decimal ZERO = new Decimal(0);
	private static final Decimal ONE = new Decimal(1);

	public static Decimal exp(Decimal exponent, MathContext context) {
		if (exponent.equals(ZERO))
				return ONE;
		if (exponent.lessThan(ZERO))
			return ONE.divide(exp(exponent.negate(), context), context);
		if (exponent.isInteger()) {
			integerExponentiation(e(context), exponent, context);
		}
	}
	
	private static void integerExponentiation(Decimal e, Decimal exponent, MathContext context) {
		// TODO Auto-generated method stub
		
	}

	private static Decimal e(MathContext context) {
		// TODO Auto-generated method stub
		return null;
	}

}
