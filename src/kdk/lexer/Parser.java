package kdk.lexer;

import java.util.ArrayList;

public class Parser {
	public static ArrayList<DictionaryItem> dictionary;
	
	static {
		dictionary.add(new DictionaryItem("cmd", 1));
		dictionary.add(new DictionaryItem("args", 0, 1));
		dictionary.add(new DictionaryItem("cntr", 1));
		dictionary.add(new DictionaryItem("cntr++", 1));
		dictionary.add(new DictionaryItem("cntr--", 1));
		dictionary.add(new DictionaryItem("rnd", 0, 2));
		dictionary.add(new DictionaryItem("chanuser", 0, 1));
		dictionary.add(new DictionaryItem("upper", 1));
		dictionary.add(new DictionaryItem("lower", 1));
		dictionary.add(new DictionaryItem("leet", 1));
		dictionary.add(new DictionaryItem("flip", 1));
		dictionary.add(new DictionaryItem("reverse", 1));
		dictionary.add(new DictionaryItem("hash", 2));
		dictionary.add(new DictionaryItem("pick", 1, Integer.MAX_VALUE));
		dictionary.add(new DictionaryItem("pagetitle", 1));
		dictionary.add(new DictionaryItem("yturl", 1));
		dictionary.add(new DictionaryItem("url", 1));
		dictionary.add(new DictionaryItem("join", 1, Integer.MAX_VALUE));
		dictionary.add(new DictionaryItem("math", 1));
		dictionary.add(new DictionaryItem("replace", 3));
		dictionary.add(new DictionaryItem("ytviews", 1));
		dictionary.add(new DictionaryItem("game", 1));
		dictionary.add(new DictionaryItem("title", 1));
		dictionary.add(new DictionaryItem("wfm", 1));
		dictionary.add(new DictionaryItem("wfs", 3));
		dictionary.add(new DictionaryItem("wfn", 1));
		dictionary.add(new DictionaryItem("wfnf", 1));
		dictionary.add(new DictionaryItem("wfa", 1));
		dictionary.add(new DictionaryItem("wfso", 1));
		dictionary.add(new DictionaryItem("wfsy", 1));
		dictionary.add(new DictionaryItem("wfv", 1));
		dictionary.add(new DictionaryItem("wfss", 1));
		dictionary.add(new DictionaryItem("wfi", 1));
		dictionary.add(new DictionaryItem("wfbad", 1));
		dictionary.add(new DictionaryItem("wfb", 1));
		dictionary.add(new DictionaryItem("wfd", 1));
		dictionary.add(new DictionaryItem("wfdr", 1));
	}
	
	private static final int ATOM_NOT_STARTED = 0;
	private static final int ATOM_STARTED = 1;
	private static final int ATOM_DATA_ENDED = ATOM_NOT_STARTED;
	private static final int ATOM_READING_ARGS = 2;
	
	public static void parse(String toParse) {
		int ch = 0; // Current character index
		final char atomStartChar = '(';
		final char atomEndChar = ':';
		final char atomVarEnd = ')';
		
		int atomState = 0;
		int atomOpened = 0;
		String readAtom = "";
		String readAtomArgs = "";
		
		while(ch < toParse.length()) {
			if(atomState == ATOM_READING_ARGS) {
				System.out.println("DBG: atomState: " + atomState + ", atomOpened: " + atomOpened);
				if(toParse.charAt(ch) == atomVarEnd && atomOpened == 1) {
					atomState = ATOM_DATA_ENDED; // We've ended the data, we can print info acquired
					System.out.println("Atom: " + readAtom + ", Args: " + readAtomArgs);
					readAtom = "";
					readAtomArgs = "";
				} else if(toParse.charAt(ch) == atomVarEnd && atomOpened > 1) {
					System.out.println("DBG: atomVarEnd found, atomOpened: " + atomOpened);
					readAtomArgs += toParse.charAt(ch);
					atomOpened--;
					if(atomOpened > 0) {
						parse(readAtomArgs);
					}
				} else {
					readAtomArgs += toParse.charAt(ch);
					if(toParse.charAt(ch) == atomStartChar) {
						atomOpened++;
					}
				}
			} else if(atomState == ATOM_STARTED) {
				System.out.println("DBG: atomState: " + atomState + ", atomOpened: " + atomOpened);
				if(toParse.charAt(ch) == atomEndChar) {
					atomState = ATOM_READING_ARGS;
				} else if(toParse.charAt(ch) == atomVarEnd) {
					atomState = 0; // We've ended the data, we can print info acquired
					System.out.println("Atom: " + readAtom + ", Args: " + readAtomArgs);
					readAtom = "";
					readAtomArgs = "";
				} else {
					readAtom += toParse.charAt(ch);
				}
			} else if(toParse.charAt(ch) == atomStartChar) {
				System.out.println("DBG: atomState: " + atomState + ", atomOpened: " + atomOpened);
				atomState = ATOM_STARTED;
				atomOpened++;
			}
			
			ch++; // Advance character index by one
		}
	}
}
