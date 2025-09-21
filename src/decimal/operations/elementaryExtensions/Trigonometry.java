package decimal.operations.elementaryExtensions;

import static decimal.Decimal.*;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import decimal.Decimal;
import decimal.helpers.FactorialSupplier;
import decimal.helpers.Summation;

public class Trigonometry {
	
	private Trigonometry() {
		// TODO Auto-generated constructor stub
		throw new AssertionError("No instances for you!");
	}
	
	private static Decimal D(String value) {
		return new Decimal(value);
	}
	
	static class Pi {
		
		static class Chudovsky { //fast but imprecise
			private static final Decimal D6 = D("10005");
			private static final Decimal D5 = D("640320");
			private static final Decimal D4 = D("3");
			private static final Decimal D3 = D("545140134");
			private static final Decimal D2 = D("13591409");
			private static final Decimal D = D("4270934400");

			private static Decimal pi(MathContext context) {
				Decimal multiplier = D6.sqrt(context).divide(D, context);
				
				FactorialSupplier factorialSupplier = new FactorialSupplier(0);
				FactorialSupplier factorialSupplier2 = new FactorialSupplier(0);
				FactorialSupplier factorialSupplier3 = new FactorialSupplier(0);
				Summation summation = new Summation(k -> {
					Decimal numerator = factorialSupplier.nextPre(6).multiply(D2.add(D3.multiply(k, context), context), context);
					Decimal denominator = factorialSupplier2.nextPre(3).multiply(factorialSupplier3.nextPre().pow(D4, context), context).multiply(D5.negate().pow(D4.multiply(k, context), context), context);
					System.out.println(k);
					return numerator.divide(denominator, context);
				});
				
				return ONE.divide(multiplier.multiply(summation.sumInfinite(0, context), context), context);
			}
		}
		
		static class BBP { //slower but insanely precise
			
			private static final Decimal D5 = D("6");
			private static final Decimal D4 = D("5");
			private static final Decimal D3 = D("8");
			private static final Decimal D2 = D("4");
			private static final Decimal D = D("16");

			private static Decimal pi(MathContext context) {
				Summation summation = new Summation(k -> {
					Decimal multiplier = ONE.divide(D.pow(k, context), context);
					Decimal term = D2.divide(D3.multiply(k, context).add(ONE, context), context);
					Decimal term2 = TWO.divide(D3.multiply(k, context).add(D2, context), context);
					Decimal term3 = ONE.divide(D3.multiply(k, context).add(D4, context), context);
					Decimal term4 = ONE.divide(D3.multiply(k, context).add(D5, context), context);
					return multiplier.multiply(term.subtract(term2, context).subtract(term3, context).subtract(term4, context), context);
				});
				return summation.sumInfinite(0, context);
			}
			
		}
		
	}
	
	public static Decimal pi(MathContext context) {
		return Pi.BBP.pi(context);
	}
	
	static class Sin {
		
		private static Decimal maclaurin(Decimal angle, MathContext context) {
			Decimal reduced = angle.mod(TWO.multiply(pi(context), context), context);
			FactorialSupplier factorialSupplier = new FactorialSupplier(1);
			Summation summation = new Summation(n -> {
				Decimal numerator = ONE.negate().pow(n, context);
				Decimal denominator = factorialSupplier.nextPre(2);
				Decimal multiplier = reduced.pow(TWO.multiply(n, context).add(ONE, context), context);
				Decimal result = multiplier.multiply(numerator, context).divide(denominator, context);
				return result;
			});
			return summation.sumInfinite(0, context);
		}
	}
	
	public static Decimal sin(Decimal angle, MathContext context) {
		return null;
		
	}
	
	public static void main(String[] args) {
		MathContext context = new MathContext(100);
		Decimal pi = Pi.BBP.pi(context);
		System.out.println(Sin.maclaurin(pi.add(D("12334"), context), context));
	}

}
