package kdk.filemanager;

public class Debugger {
	private boolean isEnabled;
	private Log logger = new Log();
	
	public Debugger() {
		this(false);
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
	
	public void writeln(Object obj, String message, boolean force) {
		if(force) {
			System.out.println(System.currentTimeMillis() + " [DBG] " + parseClassName(obj) + ": " + message);
			logger.logln(System.currentTimeMillis() + " [DBG] " + parseClassName(obj) + ": " + message);
		} else {
			writeln(obj, message);
		}
	}
	
	public void writeln(Object obj, String message) {
		if(this.isEnabled) {
			System.out.println(System.currentTimeMillis() + " [DBG] " + parseClassName(obj) + ": " + message);
			logger.logln(System.currentTimeMillis() + " [DBG] " + parseClassName(obj) + ": " + message);
		}
	}
	
	public void writeln(String message) {
		writeln(getCallerClassName(), message);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getCallerClassName() {
		StackTraceElement[] stEles = Thread.currentThread().getStackTrace();
		for(int i =1; i<stEles.length; i++) {
			StackTraceElement ste = stEles[i];
			if(!ste.getClassName().equals(Debugger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
				return ste.getClassName();
			}
		}
		return null;
	}
	
	public static String getCallerCallerClassName() {
		StackTraceElement[] stEles = Thread.currentThread().getStackTrace();
		String callerClassName = null;
		for(int i=1; i <stEles.length; i++) {
			StackTraceElement ste = stEles[i];
			if(!ste.getClassName().equals(Debugger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") !=0) {
				if(callerClassName==null) {
					callerClassName = ste.getClassName();
				} else if(!callerClassName.equals(ste.getClassName())) {
					return ste.getClassName();
				}
			}
		}
		return null;
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
