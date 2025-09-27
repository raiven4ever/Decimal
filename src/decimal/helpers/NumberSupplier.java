package decimal.helpers;

import decimal.Decimal;

public interface NumberSupplier {

	Decimal currentValue();

	Decimal currentN();

	Decimal nextPre();

	Decimal nextPre(int steps);

	Decimal nextPost();

	Decimal nextPost(int steps);

}
