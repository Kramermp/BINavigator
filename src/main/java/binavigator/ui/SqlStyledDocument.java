package binavigator.ui;

import binavigator.backend.sql.SqlHelper;
import binavigator.ui.colortheme.Monokai;
import binavigator.ui.colortheme.TextColorTheme;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SqlStyledDocument extends DefaultStyledDocument {
	private JComponent parent;
	final StyleContext cont = StyleContext.getDefaultStyleContext();

	final TextColorTheme textColorTheme = new Monokai();
	
	final AttributeSet keyWordSyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, textColorTheme.getKeyWordColor());
	final AttributeSet defaultStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, textColorTheme.getTextColor());
	final AttributeSet commentStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, textColorTheme.getCommentColor());
	final AttributeSet stringStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, textColorTheme.getStringColor());

	ArrayList<Integer> blockCommentStarts = new ArrayList<Integer>();

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
				lineEndIndex = endSearchPosition;
				foundLineEnd = true;
			}
			startSearchPosition--;
			endSearchPosition++;
		}

		System.out.println("Current Line Start at: " + lineStartIndex);
		System.out.println("Current Line End at: " + lineEndIndex);
		refreshLine(lineStartIndex, lineEndIndex);
	}

	public void refreshLine(int lineStartIndex, int lineEndIndex) throws BadLocationException {
		System.out.println("Refreshing Line: " + lineStartIndex + lineEndIndex);

		String currentLine = getText(0, getLength());
		currentLine = currentLine.substring(lineStartIndex, lineEndIndex + 1);
		System.out.println("Current Line: " + currentLine);

		boolean inLineComment = false;
		boolean inString = false;
		boolean inBlockComment = false;

		boolean stringTerminated = false;

		int segementStart = 0;
		for(int i = 0; i < currentLine.length(); i++) {

			//Detects the start of a lineComement
			if(i != 0 && currentLine.charAt(i) == '-' && currentLine.charAt(i - 1) == '-' && !inString && !inLineComment){
				int commentStart = lineStartIndex + i - 1;
				int commentEnd = lineEndIndex;
				System.out.println("Found Line Comment; Start " + commentStart + " : End " + commentEnd);
				setCharacterAttributes(commentStart, commentEnd, commentStyle, true);
				i = currentLine.length(); //Exit the for loop
			} else {

				//If starting a line comment of in one already we need to check for string
				if(currentLine.charAt(i) == '\"') {
					stringTerminated = false;
					segementStart = i;
					i++;
					while (i < currentLine.length() && !stringTerminated) {
						if(currentLine.charAt(i) == '\"') {
							stringTerminated = true;
						}
						i+=1;
					}
					System.out.println("String detected as " + currentLine.substring(segementStart, i));
					setCharacterAttributes(segementStart, i, stringStyle, true);
					segementStart = i;
				} else if ((Character.isWhitespace(currentLine.charAt(i)) || currentLine.charAt(i) == 10 || i == currentLine.length() - 1)) {
					String currentWord = currentLine.substring(segementStart, i + 1).trim();
					System.out.println("Checking word:" + currentWord);
					if(SqlHelper.isKeyWord(currentWord)) {
						System.out.println("isKeyword");
						setCharacterAttributes(segementStart + lineStartIndex , i + lineStartIndex, keyWordSyle, true);
					} else {
						System.out.println("Not Keyword");
						setCharacterAttributes(segementStart +lineStartIndex, i + lineStartIndex, defaultStyle, true);
					}
					segementStart = i + 1;
				}
			}
		}
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
		content += " ";
		List<HiliteWord> hiliteWords = new ArrayList<HiliteWord>();
		int lastWhitespacePosition = 0;
		String word = "";
		char[] data = content.toCharArray();

		for(int index=0; index < data.length; index++) {
			char ch = data[index];
			if(!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) {
				lastWhitespacePosition = index;
				if(word.length() > 0) {
					if(SqlHelper.isKeyWord(word)) {
						hiliteWords.add(new HiliteWord(word,(lastWhitespacePosition - word.length())));
					}
					word="";
				}
			}
			else {
				word += ch;
			}
		}
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
