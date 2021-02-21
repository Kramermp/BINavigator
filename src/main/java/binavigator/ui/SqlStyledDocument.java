package binavigator.ui;

import binavigator.backend.sql.SqlHelper;
import binavigator.ui.colortheme.Monokai;
import binavigator.ui.colortheme.TextColorTheme;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType;

import javax.swing.*;
import javax.swing.text.*;
import javax.xml.soap.Text;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SqlStyledDocument extends DefaultStyledDocument {
	private JComponent parent;
	StyleContext cont = StyleContext.getDefaultStyleContext();

	TextColorTheme textColorTheme = null;

	ArrayList<Integer> blockCommentStarts = new ArrayList<Integer>();

	public SqlStyledDocument(TextColorTheme textColorTheme) {
		this.textColorTheme = textColorTheme;
		updateStyles();
	}

	private void updateStyles() {

	}

	public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offset, str, a);
		String body = this.getText(0,  getLength());

		boolean foundLineStart = false;
		int startSearchPosition = offset;
		int lineStartIndex = -1;
		boolean foundLineEnd = false;
		int endSearchPosition = offset;
		int lineEndIndex = -1;

		while(!foundLineStart || !foundLineEnd) {
			if(!foundLineStart && (startSearchPosition <= 0 || body.charAt(startSearchPosition) == 10 )) {
				lineStartIndex = startSearchPosition;
				foundLineStart = true;
			}

			if( !foundLineEnd && (endSearchPosition >= body.length() - 1 || body.charAt(endSearchPosition) == 10)) {
				lineEndIndex = endSearchPosition + 1;
				foundLineEnd = true;
			}
			startSearchPosition--;
			endSearchPosition++;
		}

		System.out.println("Current Line Start at: " + lineStartIndex);
		System.out.println("Current Line End at: " + lineEndIndex);
		update(lineStartIndex, lineEndIndex);
	}

	public synchronized void update(int lineStartIndex, int lineEndIndex) throws BadLocationException {
		int segementStart = 0;
		System.out.println(0);

		String currentLine = getText(0, getLength());
		currentLine = currentLine.substring(lineStartIndex, lineEndIndex);

		for(int i = 0; i < currentLine.length(); i++) {

			//Detect Start of String and Process it
			if(currentLine.charAt(i) == '\"') {
				//Need to deal with letters currently in stack
				evaluateSegement(currentLine, lineStartIndex, segementStart, i + 1);
				segementStart = i;
				i = evaluateString(currentLine, lineStartIndex, segementStart);
				segementStart = i;

			//Detect Start of Line Comment and Process it
			} else if (i > 0 && currentLine.charAt(i) == '-' && currentLine.charAt(i -1) == '-') {
				//Need to deal with letters currently in stack
				evaluateSegement(currentLine, lineStartIndex, segementStart, i + 1);
				segementStart = i;

				commentSegement(segementStart - 1 + lineStartIndex, currentLine.length()); //Need -1 to hit first '-'
				i = currentLine.length();
				segementStart = i;

			//If it is not the start of the comment or string we move along
			} else {
				System.out.println("Current Segement Start: " + segementStart);
				evaluateSegement(currentLine, lineStartIndex, segementStart, i + 1);
				//Checking For Word Breaks
				if (currentLine.charAt(i) == ' ' || currentLine.charAt(i) == 10 || i == currentLine.length() - 1
						 || Character.isWhitespace(currentLine.charAt(i))) {
					segementStart = i + 1;
				}
			}

		}
	}

	private synchronized void evaluateSegement(String currentLine, int lineStartIndex, int segementStart, int segementEnd) {
		System.out.println("Segement Start:" + segementStart);
		System.out.println("Segement End:" + segementEnd);

		String currentSegment = currentLine.substring(segementStart, segementEnd);
		System.out.println("Checking word:" + currentSegment);

//		if (SqlHelper.isKeyWord(currentSegment)) {
//			System.out.println("isKeyword");
//			setCharacterAttributes(segementStart + lineStartIndex, segementEnd + lineStartIndex, textColorTheme.getKeyWordSyle(), true);
//		} else {
//			System.out.println("Not Keyword");
//			setCharacterAttributes(segementStart + lineStartIndex, segementEnd + lineStartIndex, textColorTheme.getDefaultStyle(), true);
//		}

		switch (SqlHelper.getWordType(currentSegment)) {
			case KEY:
				setCharacterAttributes(segementStart + lineStartIndex, segementEnd + lineStartIndex, textColorTheme.getKeyWordSyle(), true);
				break;
			case SECONDAY:
				setCharacterAttributes(segementStart + lineStartIndex, segementEnd + lineStartIndex, textColorTheme.getSecondaryStyle(), true);
				break;
			case MISC:
				setCharacterAttributes(segementStart + lineStartIndex, segementEnd + lineStartIndex, textColorTheme.getMiscStyle(), true);
				break;
			case NUMBER:
				setCharacterAttributes(segementStart + lineStartIndex, segementEnd + lineStartIndex, textColorTheme.getNumberStyle(), true);
				break;
			case NONE:
				setCharacterAttributes(segementStart + lineStartIndex, segementEnd + lineStartIndex, textColorTheme.getDefaultStyle(), true);
				break;
		}
	}

	private void commentSegement(int startPos, int endPos) {
		setCharacterAttributes(startPos, endPos, textColorTheme.getCommentStyle(), true);
	}

	private int evaluateString(String currentLine, int lineStartIndex, int startPos) {
		boolean stringTerminated = false;
		int endPosition = startPos + 1;
		while (endPosition < currentLine.length() && !stringTerminated) {
			//Check for String termination
			if(currentLine.charAt(endPosition) == '\"') {
				stringTerminated = true;
			}
			endPosition++;
		}
		System.out.println("String detected as " + currentLine.substring(startPos, endPosition));
		setCharacterAttributes(startPos + lineStartIndex, endPosition + lineStartIndex, textColorTheme.getStringStyle(), true);
		return endPosition;
	}

//	if (body.charAt(i) == '/' && i != body.length() - 1 && body.charAt(i + 1) == '*') {
//		inBlockComment = true;
//	} else if (body.charAt(i) == '/' && i != body.length() - 1 && body.charAt(i - 1) == '*') {
//		inBlockComment = false;
//		styleForChar = commentStyle;
//		pickedStyle = true;
//	} else if (body.charAt(i) == '-' && body.charAt(i - 1) ==  '-' && !inBlockComment && !inString) {
//		inLineComment = true;
//		setCharacterAttributes(i - 1, i, commentStyle, true);
//		pickedStyle = true;
//	} else if (inLineComment && body.charAt(i) == 10) {
//		inLineComment = false;
//		pickedStyle = true;
//	}
//
//			if(!inBlockComment && !inBlockComment && !inLineComment && !Character.isWhitespace(body.charAt(i))) {
//		runningString.append(body.charAt(i));
//	} else if(!inBlockComment && !inBlockComment && !inLineComment && Character.isWhitespace(body.charAt(i))) {
//		currentString = runningString.toString();
//		runningString.setLength(0);
//		if(SqlHelper.isKeyWord(currentString)) {
//			setCharacterAttributes(i - currentString.length(), i, keyWordSyle, true);
//		} else {
//			System.out.println(currentString +  " is not a keyword.");
//		}
//	}

//	currentLine = lines[i];
//	commentIndex = currentLine.indexOf("--");
//			if (editFound == false && lineStart + currentLine.length() >= offset) {
////				System.out.println("Reseting current line");
//		setCharacterAttributes(lineStart, lineStart + currentLine.length(), defaultStyle, true);
//		editFound = true;
//	}
//
//
//			if(commentIndex >= 0) {
////				System.out.println("Line Contains a Comment at index: " + commentIndex);
//		setCharacterAttributes(lineStart + commentIndex, lineStart + currentLine.length(), commentStyle, true);
//	}
////			System.out.println("No Comment " + commentIndex);
//	wordList = processWords((currentLine));
//
//
//	int wordStart = 0;
//	int wordEnd = 0;
//
//			for (int j = 0; j <  wordList.size(); j++) {
//		wordStart = wordList.get(0).wordStart + lineStart;
//		wordEnd = wordStart + wordList.get(0).word.length();
//		if (wordEnd >= commentIndex) {
//			// After Comment means it is part of the comment
//			// Do Nothing
//		} else {
//			setCharacterAttributes(wordStart, wordEnd, keyWordSyle, true);
//		}
//	}

	public void remove (int offs, int len) throws BadLocationException {
		super.remove(offs, len);

	}

	private static  List<HiliteWord> processWords(String content) {
//		content += " ";
		List<HiliteWord> hiliteWords = new ArrayList<HiliteWord>();
//		int lastWhitespacePosition = 0;
//		String word = "";
//		char[] data = content.toCharArray();
//
//		for(int index=0; index < data.length; index++) {
//			char ch = data[index];
//			if(!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) {
//				lastWhitespacePosition = index;
//				if(word.length() > 0) {
//					if(SqlHelper.isKeyWord(word)) {
//						hiliteWords.add(new HiliteWord(word,(lastWhitespacePosition - word.length())));
//					}
//					word="";
//				}
//			}
//			else {
//				word += ch;
//			}
//		}
		return hiliteWords;
	}

	private int findLastNonWordChar (String text, int index) {
		while (--index >= 0) {
			if (String.valueOf(text.charAt(index)).matches("\\W")) {
				break;
			}
		}
		return index;
	}

	private int findFirstNonWordChar (String text, int index) {
		while (index < text.length()) {
			if (String.valueOf(text.charAt(index)).matches("\\W")) {
				break;
			}
			index++;
		}
		return index;
	}

	public static class HiliteWord {

		int wordStart;
		String word;

		public HiliteWord(String word, int position) {
			wordStart = position;
			this.word = word;
		}
	}
}
