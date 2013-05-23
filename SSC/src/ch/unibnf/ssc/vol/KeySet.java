package ch.unibnf.ssc.vol;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class KeySet {

	/*
	 * Using a set to prevent equal keys (relevant mainly for 1 byte keys)
	 */
	public static String[] generateKeySet(int keyLength, int numKeys) {
		Set<String> keys = new HashSet<>();
		while (keys.size() < numKeys) {
			keys.add(generateKey(keyLength));
		}
		return keys.toArray(new String[numKeys]);
	}

	private static String generateKey(int keyLength) {
		String temp = "";
		do {
			temp += UUID.randomUUID();
		} while (temp.length() < keyLength);

		return temp.substring(0, keyLength);
	}

}
