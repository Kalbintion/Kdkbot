package kdkbot.lexer;

public class DictionaryItem implements IDictionaryItem {
	private String name;
	private int argMinCount;
	private int argMaxCount;
	
	public DictionaryItem(String name, int argMinCount, int argMaxCount) {
		this.name = name;
		this.argMinCount = argMinCount;
		this.argMaxCount = argMaxCount;
	}
	
	public DictionaryItem(String name, int argMinCount) {
		this(name, argMinCount, argMinCount);
	}
	
	public DictionaryItem(String name) {
		this(name, 0);
	}
	
	public boolean equals(String name, int argCount) {
		if(this.name.equalsIgnoreCase(name) && (argCount >= argMinCount || argCount <= argMaxCount))
			return true;
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public int getArgMinCount() {
		return argMinCount;
	}
	
	public int getArgMaxCount() {
		return argMaxCount;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setArgMinCount(int argMinCount) {
		this.argMinCount = argMinCount;
	}
	
	public void setArgMaxCount(int argMaxCount) {
		this.argMaxCount = argMaxCount;
	}
	
	public void setArgCounts(int argMinCount, int argMaxCount) {
		setArgMinCount(argMinCount);
		setArgMaxCount(argMaxCount);
	}
	
	public void handler(String message, String... data) {} ;
}
