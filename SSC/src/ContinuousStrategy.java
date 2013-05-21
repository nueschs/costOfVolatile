public class ContinuousStrategy {

	public int[] getKeyPositions(int pos, int numTotalKeys, int numThreads, float contentionPercentage) {

		int totalAssignedKeys = numTotalKeys / numThreads;
		int offset = (int) Math.round(Math.floor(pos * totalAssignedKeys * (1 - contentionPercentage)));
		int[] keys = new int[totalAssignedKeys];

		for (int i = 0; i < totalAssignedKeys; i++) {
			keys[i] = offset + i;
		}

		return keys;
	}
}
