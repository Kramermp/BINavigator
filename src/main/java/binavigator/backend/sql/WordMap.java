package binavigator.backend.sql;

import java.util.Arrays;

public class WordMap {

	char[][] words = new char[0][0];
	WordType[] wordTypes = new WordType[0];

	public WordMap() {
		// Do Nothing
	}

	public void put(char[] wordToAdd, WordType wordType) {
		int index = findWord(wordToAdd);
		if(index == -1) {
			expand();
			index = words.length - 1;
		}

		words[index] = wordToAdd;
		wordTypes[index] = wordType;
	}

	public int findWord(char[] wordToCheck) {
		for(int i = 0; i < words.length; i++) {
			if(Arrays.equals(words[i], wordToCheck)) {
				return i;
			}
		}

		return -1;
	}

	private void expand(){
		words = Arrays.copyOf(words, words.length + 1);
		wordTypes = Arrays.copyOf(wordTypes, wordTypes.length + 1);
	}

	public int size() {
		return words.length;
	}

	public WordType getWordType(char[] wordToCheck) {
		for(int i  = 0; i < words.length; i++) {
			if(compareArrays(words[i], wordToCheck)) {
				return wordTypes[i];
			}
		}

		return WordType.NONE;
	}

	private boolean compareArrays(char[] sourceWord, char[] wordToCheck) {
		if (sourceWord == wordToCheck) {
			return true;
		} else if (sourceWord != null && wordToCheck != null) {
			if (wordToCheck.length != sourceWord.length) {
				return false;
			} else {
				for(int i = 0; i < sourceWord.length; ++i) {
					if (sourceWord[i] != Character.toUpperCase(wordToCheck[i])) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}
}
