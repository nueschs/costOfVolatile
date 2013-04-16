package ch.unibnf.ssc.vol;

import java.util.Random;

public class Mapping {

	public static final String[] KEYS = Config.KEYS_1;
	public static final int NUM_PRODUCERS = 8;
	public static final int NUM_CONSUMERS = 8;
	public static final int NUM_WRITES = 8;
	public static final int NUM_READS = 100;

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Mapping mapping = new Mapping();

		System.out.println(">>> Warm-up pass");
		for (int i = 0; i < 10; i++) {
			mapping.execute();
		}
		System.out.println("<<< Warm-up done\n\n");

		System.out.println(">>> Run benchmark");
		for (int i = 0; i < 100; i++) {
			mapping.execute();
		}
		System.out.println("<<< Run benchmark");
	}

	private final Pair[] pairs;

	public Mapping() {
		this.pairs = new Pair[KEYS.length];
		this.createPairs();
	}

	private void createPairs() {
		for (int i = 0; i < KEYS.length; i++) {
			this.pairs[i] = new Pair(KEYS[i]);
		}
	}

	private void execute() throws InterruptedException {

		final Thread[] threads = new Thread[NUM_CONSUMERS + NUM_PRODUCERS];
		// starting producer threads
		long start = System.nanoTime();

		final int len = KEYS.length / NUM_PRODUCERS;
		for (int i = 0; i < NUM_PRODUCERS; i++) {
			final int localCount = i;
			threads[i] = new Thread() {

				Random r = new Random();
				int start = (len * localCount);

				@Override
				public void run() {
					for (int j = 0; j < len; j++) {
						for (int k = 0; k < NUM_WRITES; k++) {
							Mapping.this.pairs[len + this.start].setData(this.r
									.nextLong());
						}
					}
				}

			};
			threads[i].start();
		}

		// starting consumer threads
		for (int i = 0; i < NUM_CONSUMERS; i++) {
			final int localCount = i;
			threads[i + NUM_PRODUCERS] = new Thread() {
				@Override
				public void run() {
					for (int j = 0; j < NUM_READS; j++) {
						long temp = Mapping.this.pairs[KEYS.length
								/ NUM_CONSUMERS + localCount].getData();
					}
				}

			};
			threads[i + NUM_PRODUCERS].start();
		}

		for (Thread thread : threads) {
			thread.join();
		}
		long end = System.nanoTime();
		System.out.println(end - start);
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
