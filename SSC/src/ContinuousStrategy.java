public class ContinuousStrategy implements KeyDistributionStrategy {

	/*
	 * distribute keys continuously (e.g. 10 keys, 4 threads:
	 * {0,1,2},{3,4,5},{6,7},{8,9ry})
	 */
	@Override
	public int[] getWritePositions(int pos, int numTotalKeys, int numProucers) {
		int additional = pos < (numTotalKeys % numProucers) ? pos
				: numTotalKeys % numProucers;
		int numKeys = numTotalKeys / numProucers;
		int numLocalKeys = numKeys
				+ (pos < (numTotalKeys % numProucers) ? 1 : 0);
		int offset = (pos * numKeys) + additional;
		int[] writePositions = new int[numLocalKeys];
		int count = 0;
		for (int i = offset; i < offset + numLocalKeys; i++) {
			writePositions[count] = i;
			count++;
		}

		return writePositions;
	}
}
