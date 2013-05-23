import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.unibnf.ssc.vol.ContinuousStrategy;

public class ContinuousStrategyTest {

	private ContinuousStrategy strategy;

	@Before
	public void setup() {
		this.strategy = new ContinuousStrategy();
	}

	@Test
	public void testFullContention() {
		int[] expected10_8_0 = { 0 };
		Assert.assertArrayEquals(expected10_8_0, this.strategy.getKeyPositions(0, 10, 8, 1));
		Assert.assertArrayEquals(expected10_8_0, this.strategy.getKeyPositions(3, 10, 8, 1));
		Assert.assertArrayEquals(expected10_8_0, this.strategy.getKeyPositions(7, 10, 8, 1));

		int[] expected13_7_0 = { 0 };
		Assert.assertArrayEquals(expected13_7_0, this.strategy.getKeyPositions(0, 13, 7, 1));

		int[] expected100_13_0 = { 0, 1, 2, 3, 4, 5, 6 };
		Assert.assertArrayEquals(expected100_13_0, this.strategy.getKeyPositions(0, 100, 13, 1));
		Assert.assertArrayEquals(expected100_13_0, this.strategy.getKeyPositions(3, 100, 13, 1));
		Assert.assertArrayEquals(expected100_13_0, this.strategy.getKeyPositions(12, 100, 13, 1));
	}

	@Test
	public void testNoContention() {
		int[] expected10_8_0 = { 0 };
		int[] expected10_8_1 = { 1 };
		int[] expected10_8_7 = { 7 };
		Assert.assertArrayEquals(expected10_8_0, this.strategy.getKeyPositions(0, 10, 8, 0));
		Assert.assertArrayEquals(expected10_8_1, this.strategy.getKeyPositions(1, 10, 8, 0));
		Assert.assertArrayEquals(expected10_8_7, this.strategy.getKeyPositions(7, 10, 8, 0));

		int[] expected100_13_0 = { 0, 1, 2, 3, 4, 5, 6 };
		int[] expected100_13_1 = { 7, 8, 9, 10, 11, 12, 13 };
		int[] expected100_13_12 = { 84, 85, 86, 87, 88, 89, 90 };
		Assert.assertArrayEquals(expected100_13_0, this.strategy.getKeyPositions(0, 100, 13, 0));
		Assert.assertArrayEquals(expected100_13_1, this.strategy.getKeyPositions(1, 100, 13, 0));
		Assert.assertArrayEquals(expected100_13_12, this.strategy.getKeyPositions(12, 100, 13, 0));
	}

	@Test
	public void testVariousContentionLevels() {
		int[] expected10_8_0_50 = { 0 };
		Assert.assertArrayEquals(expected10_8_0_50, this.strategy.getKeyPositions(0, 10, 8, 0.5f));

		int[] expected100_10_7_30 = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57};
		Assert.assertArrayEquals(expected100_10_7_30, this.strategy.getKeyPositions(7, 100, 10, 0.3f));

		int[] expected10_8_7_50 = { 3 };
		Assert.assertArrayEquals(expected10_8_7_50, this.strategy.getKeyPositions(7, 10, 8, 0.5f));
	}

}
