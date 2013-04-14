public class Bench2 {

	public static int MAX;

	public static void main(String[] args) {
		MAX = Integer.parseInt(args[0]);
		for (int i = 0; i < 10; i++){
			new Bench2().execute();
			System.out.println();
		}
	}

	private long l1;
	private long p1,p2,p3,p4,p5,p6,p7;
	private long l2;

	public Bench2() {
		this.l1 = 12345L;
		this.l2 = 54321L;
	}

	public void execute() {
		System.out.println(System.nanoTime());
		Thread t1 = new Thread() {

			@Override
			public void run() {
				for (int i = 0; i < MAX; i++) {
					long temp = Bench2.this.l1;
					Bench2.this.l2++;
				}
			}

		};

		Thread t2 = new Thread() {

			@Override
			public void run() {
				for (int i = 0; i < MAX; i++) {
					Bench2.this.l1++;
					long temp = Bench2.this.l2;
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
