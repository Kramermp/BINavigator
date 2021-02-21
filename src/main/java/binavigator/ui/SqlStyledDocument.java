package binavigator.ui;

import binavigator.backend.sql.SqlHelper;
import binavigator.ui.colortheme.TextColorTheme;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.text.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SqlStyledDocument extends DefaultStyledDocument {
	Log log = LogFactory.getLog(this.getClass());

	private TextEditorPanel parent;

	ArrayList<Integer> blockCommentStarts = new ArrayList<Integer>();

	public SqlStyledDocument(TextEditorPanel parent) {
		this.parent = parent;

	}

	public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offset, str, a);

		String[] lines = str.split("\n", -1);

		if (lines.length > 1) {
			for (int i = 0; i < lines.length; i++) {
//				System.out.println("Detected Line: " + lines[i]);
				processLine(lines[i], offset);
				offset += lines[i].length() + 1; //Need magic +1 to account for the linebreak that was removed by split command
			}
		} else {
			int rowStart = Utilities.getRowStart(parent.getTextPane(), offset);
			int rowEnd = Utilities.getRowEnd(parent.getTextPane(), offset);

			String currentLine = this.getText(rowStart, rowEnd - rowStart);
			processLine(currentLine, rowStart);
		}

	}

	private void processLine(String line, int offset) throws BadLocationException {
//		System.out.println("Processing Line: " + line);
		String body = this.getText(0,  getLength());

//		boolean foundLineStart = false;
//		int startSearchPosition = offset;
//		int lineStartIndex = -1;
//		boolean foundLineEnd = false;
//		int endSearchPosition = offset;


//		while(!foundLineStart || !foundLineEnd) {
//			if(!foundLineStart && (startSearchPosition <= 0 || body.charAt(startSearchPosition) == 10 )) {
//				lineStartIndex = startSearchPosition;
//				foundLineStart = true;
//			}
//
//			if( !foundLineEnd && (endSearchPosition >= body.length() - 1 || body.charAt(endSearchPosition) == 10)) {
//				lineEndIndex = endSearchPosition + 1;
//				foundLineEnd = true;
//			}
//			startSearchPosition--;
//			endSearchPosition++;
//		}

//		System.out.println("Current Line Start at: " + lineStartIndex);
//		System.out.println("Current Line End at: " + lineEndIndex);
		updateSegement(offset, offset + line.length());
	}

	public synchronized void updateSegement(int lineStartIndex, int lineEndIndex) throws BadLocationException {
		int segementStart = 0;


		String currentLine = getText(0, getLength());
		currentLine = currentLine.substring(lineStartIndex, lineEndIndex);
//		System.out.println("Updating Segement: " + lineStartIndex + " - " + lineEndIndex + " : " + currentLine);
		for(int i = 0; i < currentLine.length(); i++) {

			//Detect Start of String and Process it
			if(currentLine.charAt(i) == '\"') {
				//Need to deal with letters currently in stack
				evaluateSegement(currentLine, lineStartIndex, segementStart, i + 1);
				segementStart = i;
				i = evaluateString(currentLine, lineStartIndex, segementStart);
				segementStart = i;

			//Detect Start of Line Comment and Process it
			} else if (i < currentLine.length() - 1 && currentLine.charAt(i) == '-' && currentLine.charAt(i + 1) == '-') {
				//Need to deal with letters currently in stack
				evaluateSegement(currentLine, lineStartIndex, segementStart, i + 1);
				segementStart = i;

				commentSegement(segementStart + lineStartIndex, currentLine.length() - segementStart); //Need -1 to hit first '-'
				i = currentLine.length();
				segementStart = i;

			//If it is not the start of the comment or string we move along
			} else {
				//System.out.println("Current Segement Start: " + segementStart);
				evaluateSegement(currentLine, lineStartIndex, segementStart, i + 1);
				//Checking For Word Breaks
				if (currentLine.charAt(i) == ' ' || currentLine.charAt(i) == 10 || i == currentLine.length() - 1
						 || Character.isWhitespace(currentLine.charAt(i))) {
					segementStart = i + 1;
				}
			}

		}
	}

	public void paintDocument(int startIndex, int endIndex) {
//		System.out.println("Printing Documenting");
		try {
			String text = getText(0, getLength());
			String[] lines = text.split("\n", -1);

			int offset = 0;
			for(int i = 0; i < lines.length; i++) {
				processLine(lines[i], offset);
				offset+=lines[i].length();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}


	private synchronized void evaluateSegement(String currentLine, int lineStartIndex, int segementStart, int segementEnd) {
//		System.out.println("Segement Start:" + segementStart);
//		System.out.println("Segement End:" + segementEnd);

		String currentSegment = currentLine.substring(segementStart, segementEnd).trim();

//		System.out.println("Current Segement: " + currentSegment);

		switch (SqlHelper.getWordType(currentSegment)) {
			case KEY:
				setCharacterAttributes(segementStart + lineStartIndex, currentSegment.length(), parent.getTextColorTheme().getKeyWordSyle(), true);
				break;
			case SECONDAY:
				setCharacterAttributes(segementStart + lineStartIndex, currentSegment.length() , parent.getTextColorTheme().getSecondaryStyle(), true);
				break;
			case MISC:
				setCharacterAttributes(segementStart + lineStartIndex, currentSegment.length() , parent.getTextColorTheme().getMiscStyle(), true);
				break;
			case NUMBER:
				setCharacterAttributes(segementStart + lineStartIndex, currentSegment.length() , parent.getTextColorTheme().getNumberStyle(), true);
				break;
			case NONE:
				setCharacterAttributes(segementStart + lineStartIndex, currentSegment.length() , parent.getTextColorTheme().getDefaultStyle(), true);
				break;
		}
	}

	private void commentSegement(int startPos, int length) {
//		System.out.println("Commenting Segement: " + startPos + " " + length);
		setCharacterAttributes(startPos, length, parent.getTextColorTheme().getCommentStyle(), true);
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
		String string = currentLine.substring(startPos, endPosition);
//		System.out.println("String detected as " + string);
		setCharacterAttributes(startPos + lineStartIndex, string.length(),  parent.getTextColorTheme().getStringStyle(), true);
		return endPosition;
	}

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

	public Font getCurrentFont() {
		return this.getFont(parent.getTextColorTheme().getDefaultStyle());
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
