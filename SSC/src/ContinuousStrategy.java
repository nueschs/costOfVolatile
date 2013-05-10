public class ContinuousStrategy implements KeyDistributionStrategy {

	@Override
	public int[] getWritePositions(int pos, int numTotalKeys, int numProducers, float contentionPercentage) {

		/*
		 * If percentage smaller than number of writing / reading threads -> use
		 * uncontended allocation
		 */
		int totalAssignedKeys = numProducers;
		if (contentionPercentage == 0 || contentionPercentage * numProducers <= 1) {
			totalAssignedKeys = numTotalKeys;
		} else {
			totalAssignedKeys = Math.round(numProducers * contentionPercentage * numTotalKeys);
		}

		int numKeys = totalAssignedKeys / numProducers;
		int additional = pos < (totalAssignedKeys % numProducers) ? 1 : 0;
		int start = ((pos * numKeys) + (additional * pos) + ((1 - additional) * (totalAssignedKeys % numProducers))) % numTotalKeys;

		int[] keys = new int[numKeys + additional];
		int nextPosition = start;
		for (int count = 0; count < (numKeys + additional); count++) {
			keys[count] = nextPosition;
			nextPosition = (nextPosition + 1) % numTotalKeys;
		}
		return keys;
	}
}
