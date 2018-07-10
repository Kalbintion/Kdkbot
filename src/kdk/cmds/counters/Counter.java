package kdk.cmds.counters;

public class Counter {
	public int value;
	public String name;
	
	public Counter(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public void addValue(int value) {
		this.value += value;
	}
	
	public void addValue() {
		this.value += 1;
	}
	
	public void subtractValue(int value) {
		this.value -= value;
	}
	
	public void subtractValue() {
		this.value -= 1;
	}
	
	public void multiplyValue(int value) {
		this.value *= value;
	}
	
	public void divideValue(int value) {
		if(value != 0) {
			this.value /= value;
		}
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
