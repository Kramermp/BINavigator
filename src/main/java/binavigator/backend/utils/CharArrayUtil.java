package binavigator.backend.utils;

import binavigator.backend.texteditor.DocMap;

import java.util.Arrays;

public class CharArrayUtil {

	public static int findOpenParentheses(int startIndex, char[] searchArray, DocMap commentMap) {
		int countNeeded = 1;
		int i;

		i = startIndex - 1;// Start Index is the original ')'

		while (i > 0 && countNeeded > 0) {
			if (searchArray[i] == ')') {
				countNeeded++;
			} else if(searchArray[i] == '(') {
				countNeeded--;
			}

			if (countNeeded == 0) {
				return i;
			}

			i-=commentMap.getSearchDecrement(i);

		}
		return -1;
	}

	public static int findCloseParentheses(int startIndex, char[] searchArray, DocMap commentMap) {
		int countNeeded = 1;
		int i = startIndex + 1; //Start Index is the original '('

		while(i < searchArray.length && countNeeded > 0) {


			if(searchArray[i] == ')') {
				countNeeded--;
			} else if (searchArray[i] == '(') {
				countNeeded++;
			}

			if (countNeeded == 0) {
				return i;
			}
			i+=commentMap.getSearchIncrement(i);
		}

		return i;
	}

	public static int findStringlength(int startIndex, char[] searchArray) {

//		charBuffer =  addCharToBuffer(charBuffer, searchArray[startIndex]);

		int length = 1;
		for(int i = startIndex + 1; i < searchArray.length; i++){
			length++;
			if(searchArray[i] == '\'' || searchArray[i] == '\n' || searchArray[i] == '\''){
				break;
			}

		}

		//System.out.println("Found String to be " + new String(charBuffer));
		return length;
	}

	public static  char[] findStringEnd(char[] searchArray, char[] charBuffer, int startIndex) {

		charBuffer =  addCharToBuffer(charBuffer, searchArray[startIndex]);

		for(int i = startIndex + 1; i < searchArray.length; i++){
			charBuffer = addCharToBuffer(charBuffer, searchArray[i]);
			if(searchArray[i] == '\'' || searchArray[i] == '\n' || searchArray[i] == '\''){
				break;
			}

		}

		//System.out.println("Found String to be " + new String(charBuffer));
		return charBuffer;
	}

	private static char[] addCharToBuffer(char[] buffer, char charToAdd) {
		buffer = Arrays.copyOf(buffer, buffer.length + 1);

		buffer[buffer.length - 1] = charToAdd;
		return buffer;
	}

	public static char[] findBlockCommentEnd(char[] searchArray, char[] charBuffer, int startIndex) {
		charBuffer =  addCharToBuffer(charBuffer, searchArray[startIndex]);

		for(int i = startIndex + 1; i < searchArray.length; i++){
			charBuffer = addCharToBuffer(charBuffer, searchArray[i]);
			if(searchArray[i] == '/' && i != 0 && searchArray[i - 1] == '*'){
				break;
			}
		}

//		System.out.println("Block Comment Detected to be:" + new String(charBuffer));
		return charBuffer;
	}

	public static char[] findLineCommentEnd(char[] searchArray, char[] charBuffer, int startIndex) {
		charBuffer =  addCharToBuffer(charBuffer, searchArray[startIndex]);

		for(int i = startIndex + 1; i < searchArray.length; i++){
			if(searchArray[i] == '\n'){
				break;
			} else {
				charBuffer = addCharToBuffer(charBuffer, searchArray[i]);
			}
		}

		return charBuffer;
	}
}
