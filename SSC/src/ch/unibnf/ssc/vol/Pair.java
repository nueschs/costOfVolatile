package ch.unibnf.ssc.vol;


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