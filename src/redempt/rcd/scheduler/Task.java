package redempt.rcd.scheduler;

public class Task {
	
	public final boolean repeat;
	private Thread thread;
	public final Cooldown cooldown;
	private boolean cancelled = false;
	
	public Task(Runnable action, boolean repeat, long time) {
		cooldown = new Cooldown(time);
		this.repeat = repeat;
		thread = new Thread() {
			
			@Override
			public void run() {
				while (true) {
					cooldown.start();
					cooldown.join();
					if (cancelled) {
						break;
					}
					action.run();
					if (!repeat) {
						break;
					}
				}
			}
			
		};
	}
	
	public void start() {
		thread.start();
	}
	
	public void stop() {
		cancelled = true;
	}
	
	public void setSpeed(long time) {
		cooldown.setLength(time);
	}
	
}