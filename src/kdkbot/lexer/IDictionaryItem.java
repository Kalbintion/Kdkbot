package kdkbot.lexer;

public interface IDictionaryItem {
	String name = "";
	int argMinCount = 0;
	int argMaxCount = 0;

	public boolean equals(String name, int argCount);
	
	public void handler(String message, String... data);
}
