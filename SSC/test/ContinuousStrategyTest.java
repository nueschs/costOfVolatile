import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ContinuousStrategyTest extends AbstractStrategyTest {

	@Before
	public void setup() {
		this.strategy = new ContinuousStrategy();
	}

	@Test
	public void testAllThreads() {
		int[] expected10_0 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		int[] expected10_3 = { 3, 4, 5, 6, 7, 8, 9, 0, 1, 2 };
		int[] expected10_7 = { 7, 8, 9, 0, 1, 2, 3, 4, 5, 6 };
		Assert.assertArrayEquals(expected10_0, this.strategy.getWritePositions(0, 10, 8, 1));
		Assert.assertArrayEquals(expected10_0, this.strategy.getWritePositions(3, 10, 8, 1));
		Assert.assertArrayEquals(expected10_0, this.strategy.getWritePositions(7, 10, 8, 1));

		int[] expected13 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		Assert.assertArrayEquals(expected13, this.strategy.getWritePositions(0, 13, 16, 1));
		Assert.assertArrayEquals(expected13, this.strategy.getWritePositions(7, 13, 16, 1));
		Assert.assertArrayEquals(expected13, this.strategy.getWritePositions(11, 13, 16, 1));
		Assert.assertArrayEquals(expected13, this.strategy.getWritePositions(15, 13, 16, 1));
	}

	@Test
	public void testDistribution() {
		int[] expected13_8_3_30 = { 12, 0, 1, 2 };
		int[] expected50_16_4_30 = { 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 };
		int[] expected27_6_0_25 = { 0, 1, 2, 3, 4, 5, 6 };

		Assert.assertArrayEquals(expected13_8_3_30, this.strategy.getWritePositions(3, 13, 8, 0.3f));
		Assert.assertArrayEquals(expected50_16_4_30, this.strategy.getWritePositions(4, 50, 16, 0.3f));
		Assert.assertArrayEquals(expected27_6_0_25, this.strategy.getWritePositions(0, 27, 6, 0.25f));
	}

	@Test
	public void testSingleThread() {
		int[] expected12_8_0 = { 0, 1 };
		int[] expected12_8_3 = { 6, 7 };
		int[] expected12_8_7 = { 11 };

		Assert.assertArrayEquals(expected12_8_0, this.strategy.getWritePositions(0, 12, 8, 0));
		Assert.assertArrayEquals(expected12_8_3, this.strategy.getWritePositions(3, 12, 8, 0));
		Assert.assertArrayEquals(expected12_8_7, this.strategy.getWritePositions(7, 12, 8, 0));

		int[] expected55_13_0 = { 0, 1, 2, 3, 4 };
		int[] expected55_13_5 = { 23, 24, 25, 26 };
		int[] expected55_13_9 = { 39, 40, 41, 42 };
		int[] expected55_13_12 = { 51, 52, 53, 54 };
		Assert.assertArrayEquals(expected55_13_0, this.strategy.getWritePositions(0, 55, 13, 0));
		Assert.assertArrayEquals(expected55_13_5, this.strategy.getWritePositions(5, 55, 13, 0));
		Assert.assertArrayEquals(expected55_13_9, this.strategy.getWritePositions(9, 55, 13, 0));
		Assert.assertArrayEquals(expected55_13_12, this.strategy.getWritePositions(12, 55, 13, 0));
	}

}
