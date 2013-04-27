public class DistributedStrategy implements KeyDistributionStrategy {

	/*
	 * Distribute keys evenly to Threads (e.g. 10 Keys, 4 Threads:
	 * {0,4,8},{1,5,9},{2,6},{3,7})
	 */
	@Override
	public int[] getWritePositions(int pos, int numTotalKeys, int numProucers) {
		int additional = pos < (numTotalKeys % numProucers) ? 1 : 0;
		int numLocalKeys = numTotalKeys / numProucers;
		int[] writePositions = new int[numLocalKeys + additional];
		int count = 0;
		for (int i = 0; i < numTotalKeys; i++) {
			if (pos == i % numProucers) {
				writePositions[count] = i;
				count++;
			}
		}
		return writePositions;
	}
}
