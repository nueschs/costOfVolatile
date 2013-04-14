package ch.unibnf.ssc.vol;

public class Bench1 {
	public static void main(String[] args) {
		Bench1 bench = new Bench1();
		for (int i = 0; i < 110; i++) {
			bench.runTest();
		}
	}

	public void runTest() {

		Runnable runnable = new Runnable() {
			long start = System.nanoTime();

			@Override
			public void run() {
				final Record rec = new Record();

				Thread reader = new Thread(new Runnable() {
					@Override
					public void run() {
						int sum = 0;
						while (!rec.signal) {
							sum += rec.v1;
							sum += rec.v1 + rec.v2;
							rec.v1 *= 2;
						}
						sum += rec.v1 + rec.v2;
						System.out.format("Reader has terminated, v1=%d, "
								+ "sum=%d\n", rec.v1, sum);
					}
				});

				Thread writer = new Thread(new Runnable() {
					@Override
					public void run() {
						int sum = 0;
						for (int i = 0; i < 10 * 1000 * 1000; i++) {
							sum += rec.v1;
							rec.v1 = i;

						}
						rec.signal = true;
						System.out.println("Writer has terminated. sum=" + sum);
					}
				});

				reader.start();
				writer.start();

				try {
					reader.join();
					writer.join();
					System.out.println(System.nanoTime() - this.start);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			class Record {
				int v1;
				int v2;
				volatile boolean signal;
			}
		};

		new Thread(runnable).start();
	}
}
