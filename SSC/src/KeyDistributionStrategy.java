public interface KeyDistributionStrategy {
	public int[] getWritePositions(int pos, int numTotalKeys, int numProducers,
			float contentionPercentage);
}
