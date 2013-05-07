import java.util.Arrays;

import org.junit.Test;

public class VariableContentionStrategyTest {

	@Test
	public void testGetWritePositions() {
		VariableContentionStrategy strat = new VariableContentionStrategy(0.5f);
		// System.out.println(Arrays.toString(new
		// DistributedStrategy().getWritePositions(0, 72, 8)));
		System.out.println(Arrays.toString(strat.getWritePositions(2, 15, 9)));
	}

}
