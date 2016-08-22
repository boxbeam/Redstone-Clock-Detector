package redempt.rcd;


public class Page {
	
	public String[] entries = new String[10];
	int pos = 0;
	private boolean empty = true;
	
	public Page() {
	}
	
	public Page(String[] entries) {
		this.entries = entries;
	}
	
	public boolean add(String entry) {
		empty = false;
		if (pos >= 9) {
			return false;
		}
		entries[pos] = entry;
		pos++;
		return true;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
}
