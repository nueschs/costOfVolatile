
public class VariableContentionStrategy implements KeyDistributionStrategy {
	
	/**
	 * The percentage of threads reading from / writing to each key
	 */
	private float contentionPercentage;

	public VariableContentionStrategy(float contentionPercentage){
		this.contentionPercentage = contentionPercentage;
	}

	@Override
	public int[] getWritePositions(int pos, int numTotalKeys, int numProucers) {
		KeyDistributionStrategy noContStrategy = new ContinuousStrategy();
		
		/*
		 * If percentage smaller than number of writing / reading threads -> use continuous allocation 
		 */
		if (contentionPercentage == 0 || contentionPercentage*numTotalKeys <= 1){
			return new ContinuousStrategy().getWritePositions(pos, numTotalKeys, numProucers);
		}
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
