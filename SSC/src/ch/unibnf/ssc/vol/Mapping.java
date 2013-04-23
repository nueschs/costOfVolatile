package ch.unibnf.ssc.vol;

import java.util.Random;

public class Mapping {

	/**
	 * @param args
	 *            args[0] = KeySet to Use, args[1] = Number of producer threads,
	 *            args[2] = number of consumer threads, args[3] = number of
	 *            write cycles for each writer thread, args[4] = number of read
	 *            cycles
	 * 
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		final Mapping mapping = new Mapping(args);
		System.out.println("Running with parameters " + args[0] + " " + args[1]
				+ " " + args[2] + " " + args[3] + " " + args[4]);
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

	public final String[] KEYS;
	public final int NUM_PRODUCERS;
	public final int NUM_CONSUMERS;
	public final int NUM_WRITES;

	public final int NUM_READS;

	private final Pair[] pairs;

	public Mapping(String[] args) {
		int keys = Integer.parseInt(args[0]);
		if (keys > 1 || keys < 0) {
			throw new IllegalArgumentException(
					"Keys supports values 0 and 1 only");
		}

		this.KEYS = keys == 0 ? Config.KEYS_1 : Config.KEYS_64;

		this.NUM_PRODUCERS = Integer.parseInt(args[1]);
		this.NUM_CONSUMERS = Integer.parseInt(args[2]);
		this.NUM_WRITES = Integer.parseInt(args[3]);
		this.NUM_READS = Integer.parseInt(args[4]);

		this.pairs = new Pair[this.KEYS.length];

		for (int i = 0; i < this.KEYS.length; i++) {
			this.pairs[i] = new Pair(this.KEYS[i]);
		}
	};

	private void execute() throws InterruptedException {

		final Thread[] threads = new Thread[this.NUM_CONSUMERS
				+ this.NUM_PRODUCERS];
		final int len = this.KEYS.length / this.NUM_PRODUCERS;

		// starting producer threads
		for (int i = 0; i < this.NUM_PRODUCERS; i++) {
			final int localCount = i;
			threads[i] = new Thread() {

				int[] writePositions = this.getWritePositions();

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

				/*
				 * Distribute keys evenly to Threads (e.g. 10 Keys, 4 Threads:
				 * {0,4,8},{1,5,9},{2,6},{3,7})
				 */
				private int[] getWritePositions() {
					int additional = localCount < (Mapping.this.KEYS.length % Mapping.this.NUM_PRODUCERS) ? 1
							: 0;
					int[] writePositions = new int[len + additional];
					int count = 0;
					for (int i = 0; i < Mapping.this.KEYS.length; i++) {
						if (localCount == i % Mapping.this.NUM_PRODUCERS) {
							writePositions[count] = i;
							count++;
						}
					}
					return writePositions;
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
