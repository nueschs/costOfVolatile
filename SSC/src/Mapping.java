import java.util.Random;

public class Mapping {

	/**
	 * @param args
	 *            args[0] number of keys, args[1] = key length, args[3] = Number
	 *            of producer threads, args[4] = number of consumer threads,
	 *            args[5] = number of write cycles for each writer thread,
	 *            args[6] = number of read cycles, args[7] number of benchmark
	 *            cycles
	 * 
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		final int NUM_CYCLES = Integer.parseInt(args[6]);

		final Mapping mapping = new Mapping(args);
		for (int i = 0; i < 10; i++) {
			mapping.execute(false);
		}

		for (int i = 0; i < NUM_CYCLES; i++) {
			mapping.execute(true);
		}
	}

	private final String[] KEYS;

	private final Pair[] pairs;
	private final int NUM_THREADS;

	private final int NUM_CYCLES;

	private final int RW_RATIO;

	private final double OVERLAPPING;

	private ContinuousStrategy distributionHelper;

	public Mapping(String[] args) {
		int numKeys = Integer.parseInt(args[0]);
		int keyLength = Integer.parseInt(args[1]);
		if ((Math.pow(16, keyLength)) < numKeys) {
			throw new IllegalArgumentException("Keys consist of hex digits, so the total number of keys must be larger than 16^key length");
		}
		this.KEYS = KeySet.generateKeySet(keyLength, numKeys);
		
		this.NUM_THREADS = Integer.parseInt(args[2]);
		this.NUM_CYCLES =  Integer.parseInt(args[3]); 
		this.RW_RATIO = Integer.parseInt(args[4]);
		this.OVERLAPPING = Double.parseDouble(args[5]);
		this.distributionHelper = new ContinuousStrategy();

		this.pairs = new Pair[this.KEYS.length];

		for (int i = 0; i < this.KEYS.length; i++) {
			this.pairs[i] = new Pair(this.KEYS[i]);
		}
	}

	private void execute(boolean output) throws InterruptedException {

		final Thread[] threads = new Thread[this.NUM_THREADS];
		
		for (int i = 0; i < NUM_THREADS; i++){
			final int localCount = i;
			threads[i] = new Thread(){
				
				int[] keyPositions = distributionHelper.getKeyPositions(localCount, KEYS.length, NUM_THREADS, OVERLAPPING);
				Random r = new Random();
				
				@Override
				public void run() {
					for(int j = 0; j < NUM_CYCLES; j++){
						int count = localCount*RW_RATIO;
						for (int k = 0; k < this.keyPositions.length; k++){
							if (count >= 100){
								pairs[keyPositions[k]].setData(this.r.nextLong());
								count = 0;
							} else {
								long temp = Mapping.this.pairs[this.keyPositions[k]].getData();
								count += RW_RATIO;
							}
						}
					}
					super.run();
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
