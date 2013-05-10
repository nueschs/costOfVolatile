/**
 * 
 * @author Stefan Nüesch
 * 
 */
public class DistributedStrategy implements KeyDistributionStrategy {

	/**
	 * TODO currently, this is not working as supposed. fix as soon as possible
	 */
	@Override
	public int[] getWritePositions(int pos, int numTotalKeys, int numProducers, float contentionPercentage) {

		/*
		 * If percentage smaller than number of writing / reading threads -> use
		 * uncontended allocation
		 */
		int totalAlignedKeys;
		if (contentionPercentage == 0 || contentionPercentage * numProducers <= 1) {
			totalAlignedKeys = numTotalKeys;
		} else {
			totalAlignedKeys = Math.round(numProducers * contentionPercentage * numTotalKeys);
		}
		int additional = (totalAlignedKeys % numProducers) < pos ? 1 : 0;
		int numKeys = (totalAlignedKeys / numProducers) + additional;
		int nextPosition = pos;
		int[] keys = new int[numKeys];

		for (int count = 0; count < numKeys; count++) {
			keys[count] = nextPosition;
			nextPosition = (nextPosition + numProducers) % numTotalKeys;
		}

		return keys;
	}
}
