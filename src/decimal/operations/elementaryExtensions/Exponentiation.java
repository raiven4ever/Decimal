package decimal.operations.elementaryExtensions;

import static decimal.Decimal.ONE;
import static decimal.Decimal.TWO;
import static decimal.Decimal.ZERO;

import java.math.BigInteger;
import java.math.MathContext;

import decimal.Decimal;
import decimal.Decimal.BoundType;
import decimal.helpers.FactorialSupplier;
import decimal.helpers.Summation;

//for anything that has to do with exponentiation
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
		//you know, i wouldn't be in so much pain
		//if i had an interpreter that interprets string of a basic mathematical expression
		//and spits out a function
		return new Summation(k -> TWO.divide(_3.multiply(TWO.multiply(k, context).add(ONE, context), context).multiply(integerExponentiation(_9, k, context), context), context)).sumInfinite(0, context);
	}
	
	//i need the context in case base is not an integer
	public static Decimal integerExponentiation(Decimal base, Decimal exponent, MathContext context) {
		if (!exponent.isInteger()) throw new IllegalArgumentException("exponent must be an integer");
		if (base.equals(ZERO)) {
			if (exponent.greaterThan(ZERO)) return ZERO;
			if (exponent.equals(ZERO)) return ONE; //screw it, we're not doing stuff analytically anyway. this is numerical exponentiation
			if (exponent.lessThan(ZERO)) throw new ArithmeticException("division by zero");
		}
		if (base.equals(ONE)) return ONE;
		if (base.equals(ONE.negate())) {
			if (exponent.isInteger()) return exponent.isEven() ? ONE : ONE.negate();
			else throw new IllegalArgumentException("not defined in R");
		}
		if (exponent.equals(ZERO)) return ONE;
		if (exponent.lessThan(ZERO)) {
			if (base.equals(ZERO))
				throw new ArithmeticException("division by zero");
			return ONE.divide(integerExponentiation(base, exponent.negate(), context), context);
		}
		if (exponent.lessThanOrEqualTo(Decimal.INTEGER_MAX_VALUE)) {
			return new Decimal(base.toBigDecimal().pow(exponent.toInt(), context)); //piggyback on this hoe
		}
		else {
			Decimal result = ONE;
		    while (exponent.greaterThan(ZERO)) {
		        if (exponent.isOdd())
		        	result = result.multiply(base, context);
		        base = base.multiply(base, context);
		        exponent = exponent.shiftRight(1);
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
		FactorialSupplier nFactorial = new FactorialSupplier(0);
		Summation powerSeries = new Summation(n -> integerExponentiation(exponent, n, context).divide(nFactorial.nextPre(), context));
		return powerSeries.sumInfinite(0, context);
	}
	
	//if your antiLogarithm = m * 2 ^ k where k > Long.MAX_VALUE or k < Long.MIN_VALUE, you have bigger problems
	public static Decimal ln(Decimal antiLogarithm, MathContext context) {
		if (!antiLogarithm.greaterThan(ZERO)) throw new IllegalArgumentException("antiLogarithm must be greater than 0");
		
		long k = 0;
		while (!antiLogarithm.inInterval(ONE, TWO, BoundType.INCLUSIVE, BoundType.EXCLUSIVE)) {
			if (antiLogarithm.lessThan(ONE)) {
				antiLogarithm = antiLogarithm.multiply(TWO, context);
				k--;
			}
			else if (antiLogarithm.greaterThanOrEqualTo(TWO)) {
				antiLogarithm = antiLogarithm.divide(TWO, context);
				k++;
			};
		}
		if (k != 0) return new Decimal(k).multiply(ln2(context), context).add(ln(antiLogarithm, context), context);
		
		Decimal t = antiLogarithm.subtract(ONE, context).divide(antiLogarithm.add(ONE, context), context);
		
		Summation atanh = new Summation(j -> {
			Decimal denominator = TWO.multiply(j, context).add(ONE, context);
			return integerExponentiation(t, denominator, context)
					.divide(denominator, context);
		}); //dedicated atanh function somewhere in the future
		
		return TWO.multiply(atanh.sumInfinite(0, context), context);
	}
	
	//this thing can't distinguish between rational exponents and irrational exponents
	//it's not a thing in the decimal class system
	public static Decimal exponentiation(Decimal base, Decimal exponent, MathContext context) {
		if (exponent.isInteger()) return integerExponentiation(base, exponent, context);
		if (base.equals(ZERO)) {
			if (exponent.greaterThan(ZERO)) return ZERO;
			if (exponent.equals(ZERO)) return ONE; //screw it, we're not doing stuff analytically anyway. this is numerical exponentiation
			if (exponent.lessThan(ZERO)) throw new ArithmeticException("division by zero");
		}
		if (base.equals(ONE)) return ONE;
		if (base.equals(ONE.negate())) {
			if (exponent.isInteger()) return exponent.isEven() ? ONE : ONE.negate();
			else throw new IllegalArgumentException("not defined in R");
		}
		if (exponent.equals(ZERO)) return ONE;
		//not gonna bother catching base < 0, exponent not an integer since ln(base) will catch it for me anyway
		//i'm also not gonna bother defining exponent < 0 for base > 0 since the other two methods handle it for me as well
		return exp(exponent.multiply(ln(base, context)), context);
	}

}
