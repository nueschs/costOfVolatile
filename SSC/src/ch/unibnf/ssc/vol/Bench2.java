package ch.unibnf.ssc.vol;

public class Bench2 {

	public static int MAX = 300000;

	public static void main(String[] args) {
		new Bench2().execute();
	}

	private volatile long l1;

	public Bench2() {
		this.l1 = 12345L;
	}

	public void execute() {
		System.out.println(System.nanoTime());
		Thread t1 = new Thread() {

			@Override
			public void run() {
				for (int i = 0; i < MAX; i++) {
					long temp = Bench2.this.l1;
				}
			}

		};

		Thread t2 = new Thread() {

			@Override
			public void run() {
				for (int i = 0; i < MAX; i++) {
					Bench2.this.l1++;
				}
			}

		};

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(System.nanoTime());
	}
}
