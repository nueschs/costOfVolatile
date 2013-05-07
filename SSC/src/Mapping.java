import java.util.Random;

public class Mapping {

	/**
	 * @param args
	 *            args[0] number of keys, args[1] = key length, args[2] key
	 *            distribution strategy, args[3] = Number of producer threads,
	 *            args[4] = number of consumer threads, args[5] = number of
	 *            write cycles for each writer thread, args[6] = number of read
	 *            cycles, args[7] number of benchmark cycles
	 * 
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		final int NUM_CYCLES = Integer.parseInt(args[8]);

		final Mapping mapping = new Mapping(args);
		for (int i = 0; i < 10; i++) {
			mapping.execute(false);
		}

		for (int i = 0; i < NUM_CYCLES; i++) {
			mapping.execute(true);
		}
	}

	private final String[] KEYS;
	private final int NUM_PRODUCERS;
	private final int NUM_CONSUMERS;
	private final int NUM_WRITES;
	private final KeyDistributionStrategy KEY_STRATEGY;
	private final int NUM_READS;

	private final Pair[] pairs;

	public Mapping(String[] args) {
		int strat = Integer.parseInt(args[0]);
		switch (strat) {
		case 0:
			this.KEY_STRATEGY = new ContinuousStrategy();
			break;
		case 1:
			this.KEY_STRATEGY = new DistributedStrategy();
			break;
		case 2:
			float perc = Float.parseFloat(args[1]);
			this.KEY_STRATEGY = new VariableContentionStrategy(perc);
			break;
		default:
			throw new IllegalArgumentException(
					"Only 0 and 1 are allowed values for key strategy");
		}

		int numKeys = Integer.parseInt(args[2]);
		int keyLength = Integer.parseInt(args[3]);
		this.KEYS = KeySet.generateKeySet(keyLength, numKeys);
		this.NUM_PRODUCERS = Integer.parseInt(args[4]);
		this.NUM_CONSUMERS = Integer.parseInt(args[5]);
		this.NUM_WRITES = Integer.parseInt(args[6]);
		this.NUM_READS = Integer.parseInt(args[7]);

		this.pairs = new Pair[this.KEYS.length];

		for (int i = 0; i < this.KEYS.length; i++) {
			this.pairs[i] = new Pair(this.KEYS[i]);
		}
	}

	private void execute(boolean output) throws InterruptedException {

		final Thread[] threads = new Thread[this.NUM_CONSUMERS
				+ this.NUM_PRODUCERS];

		// starting producer threads
		for (int i = 0; i < this.NUM_PRODUCERS; i++) {
			final int localCount = i;
			threads[i] = new Thread() {

				int[] writePositions = Mapping.this.KEY_STRATEGY
						.getWritePositions(localCount,
								Mapping.this.KEYS.length,
								Mapping.this.NUM_PRODUCERS);

				Random r = new Random();

				@Override
				public void run() {
					for (int k = 0; k < Mapping.this.NUM_WRITES; k++) {
						for (int j = 0; j < this.writePositions.length; j++) {
							Mapping.this.pairs[this.writePositions[j]]
									.setData(this.r.nextLong());
						}
					}
				}
			};
		}

		// starting consumer threads
		for (int i = 0; i < this.NUM_CONSUMERS; i++) {
			final Random r = new Random();
			threads[i + this.NUM_PRODUCERS] = new Thread() {
				@Override
				public void run() {
					for (int j = 0; j < Mapping.this.NUM_READS; j++) {
						long temp = Mapping.this.pairs[r
								.nextInt(Mapping.this.KEYS.length)].getData();
					}
				}

			};
		}

		// starttime measured after Thread creation
		long start = System.nanoTime();

		for (Thread thread : threads) {
			thread.start();
		}

		for (Thread thread : threads) {
			thread.join();
		}
		long end = System.nanoTime();
		if (output) {
			System.out.println(end - start);
		}
	}

	public class Pair {
		private volatile long data;
		private final String key;

		public Pair(String key) {
			this.key = key;
			this.data = -1;
		}

		public long getData() {
			return this.data;
		}

		public String getKey() {
			return this.key;
		}

		public void setData(long data) {
			this.data = data;
		}
	}

}
