//public class VariableContentionStrategy implements KeyDistributionStrategy {
//
//	/**
//	 * The percentage of threads reading from / writing to each key
//	 */
//	private final float contentionPercentage;
//
//	public VariableContentionStrategy(float contentionPercentage) {
//		this.contentionPercentage = contentionPercentage;
//	}
//
//	@Override
//	public int[] getWritePositions(int pos, int numTotalKeys, int numProucers) {
//		KeyDistributionStrategy noContStrategy = new ContinuousStrategy();
//
//		/*
//		 * If percentage smaller than number of writing / reading threads -> use
//		 * continuous allocation
//		 */
//		if (this.contentionPercentage == 0
//				|| this.contentionPercentage * numProucers <= 1) {
//			return new ContinuousStrategy().getWritePositions(pos,
//					numTotalKeys, numProucers);
//		}
//
//		float newProdF = Math.round(numProucers * this.contentionPercentage);
//		System.out.println(newProdF);
//		int newProd = numProucers / (int) newProdF;
//
//		int newPos = pos % newProd;
//		System.out.println("newPos: " + newPos + " newProd: " + newProd
//				+ " numProd: " + numProucers);
//
//		return noContStrategy.getWritePositions(newPos, numTotalKeys, newProd);
//	}
//
// }
