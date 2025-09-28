package decimal.operations.elementaryExtensions;

import java.math.MathContext;
import java.util.Scanner;

import decimal.Decimal;
import static decimal.operations.elementaryExtensions.Trigonometry.*;
import static decimal.Decimal.*;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MathContext context = new MathContext(100);
//		Decimal pi = pi(context);
//		for (double i = -Math.PI*2; i <= Math.PI*2; i+= 0.05) {
//			Decimal angle = D(Double.toString(i));
//			Decimal object = csc(angle, context);
////			System.out.println(String.format("%s\t%s", Math.round(i*1000)/1000d, object));
//			System.out.println(object);
//		}
		
		for (double i = -10; i <= 10.05; i += 0.05) {
			try {
				Decimal value = D(i);
				Decimal object = arccot(value, context);
//				System.out.println(String.format("%s\t%s", value, object));
				object.echo();
			}
			catch(Exception e) {
				System.out.println();
			}
		};
		
	}

}
