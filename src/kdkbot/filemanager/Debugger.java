package kdkbot.filemanager;

public class Debugger {
	private boolean isEnabled;
	
	public Debugger() {
		isEnabled = false;
	}
	
	public Debugger(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public void enable() {
		this.isEnabled = true;
	}
	
	public void disable() {
		this.isEnabled = false;
	}
	
	public void writeln(Object obj, String message) {
		if(this.isEnabled) 
			System.out.println(System.currentTimeMillis() + " [DBG] " + parseClassName(obj) + ": " + message);
	}
	
	private String parseClassName(Object obj) {
		return parseClassName(obj, "[", "] ");
	}
	
	private String parseClassName(Object obj, String prefix, String suffix) {
		return parseClassName(obj, prefix, suffix, 0);
	}
	
	private String parseClassName(Object obj, String prefix, String suffix, int startIndex) {
		String[] parts =  obj.getClass().getName().split("\\.");
		StringBuilder partBuilder = new StringBuilder();
		
		for(int i = startIndex; i < parts.length; i++) {
			partBuilder.append(prefix + parts[i] + suffix);
		}
		
		return partBuilder.toString();
	}
}
