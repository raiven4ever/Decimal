package decimal.operations.elementaryExtensions;

import static decimal.Decimal.ONE;
import static decimal.Decimal.TWO;
import static decimal.Decimal.ZERO;

import java.math.BigInteger;
import java.math.MathContext;

import decimal.Decimal;
import decimal.helpers.Summation;

public class Exponentiation {
	
	private static Decimal _3 = new Decimal(3);
	private static Decimal _9 = new Decimal(9);
	//this is basically this bad boy
	/*	∑k=0∞2/3(2k+1)⋅9k
	 * */
	//if i wasnt less lazy, i wouldve done a machin-like implementation
	//but that requires arctanh implementation
	//and bbp is too many terms
	private static Decimal ln2(MathContext context) {
		//you know, this wouldn't be a pain in this ass
		//if i had an interpreter that interprets string of a basic mathematical expression
		//and spits out a function
		return new Summation(k -> TWO.divide(_3.multiply(TWO.multiply(k, context).add(ONE, context), context).multiply(integerExponentiation(_9, k, context), context), context)).sumInfinite(0, context);
	}
	
	public static void main(String[] args) {
		System.out.println(ln2(new MathContext(1000)));
	}
	
	//i need the context in case base is not an integer
	public static Decimal integerExponentiation(Decimal base, Decimal exponent, MathContext context) {
		if (!exponent.isInteger()) throw new IllegalArgumentException("exponent must be an integer");
		if (exponent.equals(ZERO)) return ONE;
		if (exponent.lessThan(ZERO)) {
			if (base.equals(ZERO))
				throw new ArithmeticException("division by zero");
			return ONE.divide(integerExponentiation(base, exponent.negate(), context), context);
		}
		if (exponent.lessThanOrEqualTo(new Decimal(Integer.MAX_VALUE))) {
			return new Decimal(base.toBigDecimal().pow(exponent.toInt(), context)); //piggyback on this hoe
		}
		else {
			Decimal result = ONE;
		    while (exponent.greaterThan(ZERO)) {
		        if (exponent.isOdd())
		        	result = result.multiply(base, context);
		        base = base.multiply(base, context);
		        exponent = new Decimal(exponent.toBigInteger().shiftRight(1));
		    }
			return result;
		}
	}
	
	public static Decimal exp(Decimal exponent, MathContext context) {
		if (exponent.equals(ZERO)) return ONE;
		if (exponent.lessThan(ZERO)) return ONE.divide(exp(exponent.negate(), context), context);
		Decimal ln2 = ln2(context);
		if (exponent.greaterThan(ln2.divide(TWO, context))) {
			Decimal k = exponent.divide(ln2, context).round();
			Decimal r = exponent.subtract(k.multiply(ln2, context), context);
			return integerExponentiation(TWO, k, context).multiply(exp(r, context), context);
		}
	}
	
}
