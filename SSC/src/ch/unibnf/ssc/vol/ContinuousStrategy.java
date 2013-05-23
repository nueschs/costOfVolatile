package ch.unibnf.ssc.vol;
public class ContinuousStrategy {

	public int[] getKeyPositions(int pos, int numTotalKeys, int numThreads, double oVERLAPPING) {

		int totalAssignedKeys = numTotalKeys / numThreads;
		int offset = (int) Math.round(Math.floor(pos * totalAssignedKeys * (1 - oVERLAPPING)));
		int[] keys = new int[totalAssignedKeys];

		for (int i = 0; i < totalAssignedKeys; i++) {
			keys[i] = offset + i;
		}

		return keys;
	}
}
