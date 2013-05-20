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

		final int NUM_CYCLES = Integer.parseInt(args[10]);

		final Mapping mapping = new Mapping(args);
		for (int i = 0; i < 10; i++) {
			mapping.execute(false);
		}

		for (int i = 0; i < NUM_CYCLES; i++) {
			mapping.execute(true);
		}
	}

	private final String[] KEYS;

	private final int NUM_WRITERS;
	private final int NUM_WRITES;
	private final KeyDistributionStrategy WRITER_KEY_STRATEGY;
	private final float WRITER_CONTENTION_PRECENTAGE;

	private final int NUM_READERS;
	private final int NUM_READS;
	private final KeyDistributionStrategy READER_KEY_STRATEGY;
	private final float READER_CONTENTION_PRECENTAGE;

	private final Pair[] pairs;

	public Mapping(String[] args) {
		int numKeys = Integer.parseInt(args[0]);
		int keyLength = Integer.parseInt(args[1]);
		if ((Math.pow(16, keyLength)) < numKeys) {
			throw new IllegalArgumentException("Keys consist of hex digits, so the total number of keys must be larger than 16^key length");
		}
		this.KEYS = KeySet.generateKeySet(keyLength, numKeys);

		this.NUM_WRITERS = Integer.parseInt(args[2]);
		this.NUM_WRITES = Integer.parseInt(args[3]);
		int writeStrat = Integer.parseInt(args[4]);
		if (writeStrat < 0 || writeStrat > 1) {
			throw new IllegalArgumentException("Only 0 and 1 are allowed values for key strategy");
		}
		this.WRITER_KEY_STRATEGY = writeStrat == 0 ? new ContinuousStrategy() : new DistributedStrategy();
		this.WRITER_CONTENTION_PRECENTAGE = Float.parseFloat(args[5]);

		this.NUM_READERS = Integer.parseInt(args[6]);
		this.NUM_READS = Integer.parseInt(args[7]);
		int readStrat = Integer.parseInt(args[8]);
		if (readStrat < 0 || readStrat > 1) {
			throw new IllegalArgumentException("Only 0 and 1 are allowed values for key strategy");
		}
		this.READER_KEY_STRATEGY = readStrat == 0 ? new ContinuousStrategy() : new DistributedStrategy();
		this.READER_CONTENTION_PRECENTAGE = Float.parseFloat(args[9]);

		this.pairs = new Pair[this.KEYS.length];

		for (int i = 0; i < this.KEYS.length; i++) {
			this.pairs[i] = new Pair(this.KEYS[i]);
		}
	}

	private void execute(boolean output) throws InterruptedException {

		final Thread[] threads = new Thread[this.NUM_READERS + this.NUM_WRITERS];

		// starting producer threads
		for (int i = 0; i < this.NUM_WRITERS; i++) {
			final int localCount = i;
			threads[i] = new Thread() {

				int[] writePositions = Mapping.this.WRITER_KEY_STRATEGY.getWritePositions(localCount, Mapping.this.KEYS.length, Mapping.this.NUM_WRITERS,
						Mapping.this.WRITER_CONTENTION_PRECENTAGE);

				Random r = new Random();

				@Override
				public void run() {
					for (int k = 0; k < Mapping.this.NUM_WRITES; k++) {
						for (int j = 0; j < this.writePositions.length; j++) {
							Mapping.this.pairs[this.writePositions[j]].setData(this.r.nextLong());
						}
					}
				}
			};
		}

		// starting consumer threads
		for (int i = 0; i < this.NUM_READERS; i++) {
			final Random r = new Random();
			final int localCount = i;
			threads[i + this.NUM_WRITERS] = new Thread() {

				int[] readPositions = Mapping.this.READER_KEY_STRATEGY.getWritePositions(localCount, Mapping.this.KEYS.length, Mapping.this.NUM_READERS,
						Mapping.this.READER_CONTENTION_PRECENTAGE);

				@Override
				public void run() {
					for (int j = 0; j < Mapping.this.NUM_READS; j++) {
						for (int k = 0; k < this.readPositions.length; k++) {
							long temp = Mapping.this.pairs[this.readPositions[k]].getData();
						}
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
