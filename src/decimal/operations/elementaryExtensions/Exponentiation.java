package decimal.operations.elementaryExtensions;

import static decimal.Decimal.ONE;
import static decimal.Decimal.TWO;
import static decimal.Decimal.ZERO;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.function.BiFunction;

import decimal.Decimal;
import decimal.Decimal.BoundType;
import decimal.helpers.FactorialSupplier;
import decimal.helpers.Summation;

//utility class for exponentiation belonging to elementary extensions
public class Exponentiation {

	private Exponentiation() {
		throw new AssertionError("No instances for you!");
	}
	
	//wont be exposed
	private static Decimal integerExponentiation(Decimal base, Decimal exponent, MathContext context) {
		if (exponent.lessThanOrEqualTo(Decimal.INTEGER_MAX_VALUE)) {
			return new Decimal(base.toBigDecimal().pow(exponent.toInt(), context));
		} else {
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
	
	//might expose this later, but it is better to move this in its own utility class in the future, maybe
	private static Decimal exp(Decimal exponent, MathContext context) {
		Decimal ln2 = ln2(context);

		if (exponent.greaterThan(ln2.divide(TWO, context))) {
			Decimal k = exponent.divide(ln2, context).round();
			Decimal r = exponent.subtract(k.multiply(ln2, context), context);
			return integerExponentiation(TWO, k, context).multiply(exp(r, context), context);
		}

		FactorialSupplier nFactorial = new FactorialSupplier(0);
		Summation powerSeries =
				new Summation(n -> integerExponentiation(exponent, n, context).divide(nFactorial.nextPre(), context));
		return powerSeries.sumInfinite(0, context); // currently implemented via Taylor series
	}
	
	private static Decimal _3 = new Decimal(3);
	private static Decimal _9 = new Decimal(9);
	private static Decimal ln2(MathContext context) {
		return new Summation(
				k -> TWO.divide(
						_3.multiply(
								TWO.multiply(k, context).add(ONE, context),
								context
								).multiply(
										integerExponentiation(_9, k, context),
										context
										),
						context
						)
				).sumInfinite(0, context);
	}

	//it's here for now but wont be exposed until it is moved into it's own utility class
	private static Decimal ln(Decimal antiLogarithm, MathContext context) {
		long k = 0;
		while (!antiLogarithm.inInterval(ONE, TWO, BoundType.INCLUSIVE, BoundType.EXCLUSIVE)) {
			if (antiLogarithm.lessThan(ONE)) {
				antiLogarithm = antiLogarithm.multiply(TWO, context);
				k--;
			} else if (antiLogarithm.greaterThanOrEqualTo(TWO)) {
				antiLogarithm = antiLogarithm.divide(TWO, context);
				k++;
			}
		}

		if (k != 0) {
			return new Decimal(k)
					.multiply(ln2(context), context)
					.add(ln(antiLogarithm, context), context);
		}

		Decimal t = antiLogarithm.subtract(ONE, context)
				.divide(antiLogarithm.add(ONE, context), context);

		Summation atanh = new Summation(j -> {
			Decimal denominator = TWO.multiply(j, context).add(ONE, context);
			return integerExponentiation(t, denominator, context)
					.divide(denominator, context);
		}); // atanh method could be extracted in the future

		return TWO.multiply(atanh.sumInfinite(0, context), context);
	}
	
	//for all purposes, this will be the only method that will be exposed
	//so it will contain all the guardrails
	public static Decimal exponentiation(Decimal base, Decimal exponent, MathContext context) {
		if (exponent.isInteger()) {
			if (exponent.equals(ZERO) && base.notEqual(ZERO))
				return ONE;
			else if (exponent.greaterThan(ZERO))
				return integerExponentiation(base, exponent, context);
			else if (exponent.lessThan(ZERO) && base.notEqual(ZERO))
				ONE.divide(integerExponentiation(base, exponent.negate(), context), context);
		} else if (base.greaterThan(ZERO))
			return exp(exponent.multiply(ln(base, context)), context);
		throw new IllegalArgumentException(String.format("undefined for base %s, exponent %s", base, exponent));
	}

}
