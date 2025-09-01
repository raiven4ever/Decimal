package decimal.operations;

import java.math.BigDecimal;
import java.math.MathContext;

import decimal.Decimal;

public class ArithmeticBasics {
	
	public static Decimal addition(Decimal firstOperand, Decimal secondOperand, MathContext context) {
		return new Decimal(firstOperand.toBigDecimal().add(secondOperand.toBigDecimal(), context));
	}
	
	public static Decimal subtraction(Decimal firstOperand, Decimal secondOperand, MathContext context) {
		return new Decimal(firstOperand.toBigDecimal().subtract(secondOperand.toBigDecimal(), context));
	}
	
	public static Decimal multiplication(Decimal firstOperand, Decimal secondOperand, MathContext context) {
		return new Decimal(firstOperand.toBigDecimal().multiply(secondOperand.toBigDecimal(), context));
	}
	
	public static Decimal division(Decimal firstOperand, Decimal secondOperand, MathContext context) {
		return new Decimal(firstOperand.toBigDecimal().divide(secondOperand.toBigDecimal(), context));
	}
}
