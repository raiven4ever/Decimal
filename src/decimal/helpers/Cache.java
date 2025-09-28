package decimal.helpers;

import java.util.Arrays;

import decimal.Decimal;

public class Cache {
	
	private Decimal[] cache;
	
	public Cache(int size, Decimal initialValue) {
		cache = new Decimal[size];
		for (int i = 0; i < size; i++) {
			cache[i] = initialValue;
		}
	}
	
	public boolean contains(Decimal value) {
		for (Decimal x : cache) {
			if (x.equals(value)) return true;
		}
		return false;
	}
	
	public void update(Decimal value) {
		for (int i = cache.length - 1; i > 0; i--)
			cache[i] = cache[i-1];
		cache[0] = value;
	}

}
