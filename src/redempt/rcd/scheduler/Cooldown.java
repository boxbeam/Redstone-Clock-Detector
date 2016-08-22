package redempt.rcd.scheduler;

public class Cooldown {
	
	private long start;
	private long time;
	
	public Cooldown(long time) {
		this.time = time;
		start = System.currentTimeMillis();
	}
	
	public boolean isOver() {
		return System.currentTimeMillis() - start >= time;
	}
	
	public void start() {
		start = System.currentTimeMillis();
	}
	
	public void join() {
		while (!isOver()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setLength(long time) {
		this.time = time;
	}
	
}
