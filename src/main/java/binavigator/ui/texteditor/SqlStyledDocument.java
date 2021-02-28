package binavigator.ui.texteditor;

import binavigator.backend.BINavController;
import binavigator.backend.sql.SqlHelper;
import binavigator.backend.texteditor.TextEditorController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.text.*;

import java.util.*;

public class SqlStyledDocument extends DefaultStyledDocument {
	Log log = LogFactory.getLog(this.getClass());

	private BINavController parentController;
	private TextEditorController textEditorController;

	private final char[] emptyArray = new char[0];

	ArrayList<Integer> blockCommentStarts = new ArrayList<Integer>();

	public SqlStyledDocument(TextEditorController textEditorController) {
		super();
		this.textEditorController = textEditorController;
	}

	public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offset, str, a);
		long insertStartTime = System.currentTimeMillis();

		String[] lines = str.split("\n", -1);

		char[] searchArray = getText(0, getLength()).toCharArray();

		int lineStart = findLineStart(searchArray, offset);
//		System.out.println("LineStart: " + lineStart);



//		System.out.println("Char at Start: " + searchArray[segmentStart]);
//		System.out.println(getText(0, getLength()));
		SegmentType type = SegmentType.TEXT;
//		StringBuilder sb = new StringBuilder();

		int i = 0;
//		int i = lineStart;
//		ArrayList<Character> charList = new ArrayList<Character>();
//		charList.add('e');

		int segmentStart = lineStart;
		int segmentEnd = lineStart;
		char[] charBuffer = new char[0];
		for(i = i; i < searchArray.length; i++) {

			if (searchArray[i] == '\'') {
				processSegment(charBuffer, segmentStart);
				segmentStart = i;
				charBuffer = emptyArray;
				charBuffer = findStringEnd(searchArray, charBuffer, i);
				paintSegment(segmentStart, segmentStart + charBuffer.length, SegmentType.STRING);
				i += charBuffer.length;
				segmentStart = i + 1;
				charBuffer = emptyArray;
			} else if (searchArray[i] == '-' && i != 0 && searchArray[i - 1] == '-') {
				processSegment(charBuffer, segmentStart);
				segmentStart = i - 1;
				charBuffer = emptyArray;
				charBuffer = findLineCommentEnd(searchArray, charBuffer, i - 1);
				paintSegment(segmentStart, segmentStart + charBuffer.length, SegmentType.COMMENT);
				i += charBuffer.length - 1; //-1 because of the backwards includes of open '-'
				segmentStart = i + 1;
				charBuffer = emptyArray;
			} else if(searchArray[i] == '*' && i!= 0 && searchArray[i - 1] == '/') {
				processSegment(charBuffer, segmentStart);
				segmentStart = i - 1;
				charBuffer = emptyArray;
				charBuffer = findBlockCommentEnd(searchArray, charBuffer, i - 1);
				paintSegment(segmentStart, segmentStart + charBuffer.length, SegmentType.COMMENT);
				i += charBuffer.length;
				segmentStart = i + 1;
				charBuffer = emptyArray;
			} else
				if(searchArray[i] == ' ' || searchArray[i] == '\n') {
				processSegment(charBuffer, segmentStart);
				charBuffer = emptyArray;
				segmentStart = i + 1;
			} else {
				charBuffer = addCharToBuffer(charBuffer, searchArray[i]);
			}
		}

		if(charBuffer != emptyArray) {
			processSegment(charBuffer, segmentStart);
		}

//		System.out.println("Checking Last Segment");
//		processSegment(searchArray, segmentStart, (searchArray.length - 1));


//		if (lines.length > 1) {
//			for (int i = 0; i < lines.length; i++) {
////				System.out.println("Detected Line: " + lines[i]);
//				processLine(lines[i], offset);
//				offset += lines[i].length() + 1; //Need magic +1 to account for the linebreak that was removed by split command
//			}
//		} else {
//			int rowStart = textEditorController.getRowStart(offset);
//			int rowEnd = textEditorController.getRowEnd(offset);
//
//			String currentLine = this.getText(rowStart, rowEnd - rowStart);
//			processLine(currentLine, rowStart);
//		}

		long insertEndTime = System.currentTimeMillis();

		System.out.println("Insert Completed in " + ((insertEndTime - insertStartTime) / 1000) + "s");

	}

	private char[] findLineCommentEnd(char[] searchArray, char[] charBuffer, int startIndex) {
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

	private char[] findBlockCommentEnd(char[] searchArray, char[] charBuffer, int startIndex) {
		charBuffer =  addCharToBuffer(charBuffer, searchArray[startIndex]);

		for(int i = startIndex + 1; i < searchArray.length; i++){
			if(searchArray[i] == '/' && i != 0 && searchArray[i - 1] == '*'){
				break;
			} else {
				charBuffer = addCharToBuffer(charBuffer, searchArray[i]);
			}
		}

		return charBuffer;
	}

	private char[] addCharToBuffer(char[] buffer, char charToAdd) {
		buffer = Arrays.copyOf(buffer, buffer.length + 1);

		buffer[buffer.length - 1] = charToAdd;
		return buffer;
	}

	private int findLineStart(char[] searchArray, int offset) {
		for(int i = offset; i > 0; i--) {
			if(searchArray[i] == '\n') {
				return i;
			}
		}
		return 0;
	}

	private char[] findStringEnd(char[] searchArray, char[] charBuffer, int startIndex) {

		charBuffer =  addCharToBuffer(charBuffer, searchArray[startIndex]);

		for(int i = startIndex + 1; i < searchArray.length; i++){
			if(searchArray[i] == '\'' || searchArray[i] == '\n' || searchArray[i] == '\''){
				break;
			} else {
				charBuffer = addCharToBuffer(charBuffer, searchArray[i]);
			}
		}

//		System.out.println("Found String to be " + new String(charBuffer));
		return charBuffer;
	}

	private void paintSegment(int start, int end, SegmentType type) {
		switch (type) {
			case STRING:
				//System.out.println("Printing " + start + " - " + end + " as a string");
				setCharacterAttributes(start, end - start,  textEditorController.getTextColorTheme().getStringStyle(), true);
				return;
			case TEXT:
				//System.out.println("Printing " + start + " - " + end + " as a text");
				setCharacterAttributes(start, end - start, textEditorController.getTextColorTheme().getDefaultStyle(), false);
				return;
			case MISC:
				setCharacterAttributes(start, end - start, textEditorController.getTextColorTheme().getMiscStyle(), false);
				return;
			case NUMBER:
				setCharacterAttributes(start, end - start, textEditorController.getTextColorTheme().getNumberStyle(), false);
				return;
			case KEYWORD:
				setCharacterAttributes(start, end - start, textEditorController.getTextColorTheme().getKeyWordSyle(), false);
				return;
			case SECONDARY:
				setCharacterAttributes(start, end - start, textEditorController.getTextColorTheme().getSecondaryStyle(), false);
				return;
			case COMMENT:
				setCharacterAttributes(start, end - start, textEditorController.getTextColorTheme().getCommentStyle(), false);
		}
	}

	private void processSegment(char[] charBuffer, int segmentStart) {
//		System.out.println("Processing Segment " + segmentStart + " - " + (segmentStart + charBuffer.length));
//		String test = new String(charBuffer);
//		System.out.println(test);

		switch (SqlHelper.getWordType(charBuffer)) {
			case NUMBER:
				paintSegment(segmentStart, segmentStart + charBuffer.length, SegmentType.NUMBER);
				return;
			case KEY:
				paintSegment(segmentStart, segmentStart + charBuffer.length, SegmentType.KEYWORD);
				return;
			case MISC:
				paintSegment(segmentStart, segmentStart + charBuffer.length, SegmentType.MISC);
			case SECONDAY:
				paintSegment(segmentStart, segmentStart + charBuffer.length, SegmentType.SECONDARY);
				return;
			case NONE:
				paintSegment(segmentStart, segmentStart + charBuffer.length, SegmentType.TEXT);
		}
//
	}

//	private void processLine(String line, int offset) throws BadLocationException {
////		System.out.println("Processing Line: " + line);
//		String body = this.getText(0,  getLength());
//
//		updateSegment(offset, offset + line.length());
//	}

//	public void updateSegment(int lineStartIndex, int lineEndIndex) throws BadLocationException {
//		int segementStart = 0;
//
//
//		String currentLine = getText(0, getLength());
//		currentLine = currentLine.substring(lineStartIndex, lineEndIndex);
//		char[] searchArray  = currentLine.toCharArray();
////		System.out.println("Updating Segement: " + lineStartIndex + " - " + lineEndIndex + " : " + currentLine);
//		for(int i = 0; i < currentLine.length(); i++) {
//
//			//Detect Start of String and Process it
//			if(searchArray[i] == '\"') {
//				//Need to deal with letters currently in stack
//				evaluateSegment(currentLine, searchArray, lineStartIndex, segementStart, i + 1);
//				segementStart = i;
//				//After Dealing with remaining characters it deals with the string
//				i = evaluateString(searchArray, lineStartIndex, segementStart);
//				segementStart = i;
//
//			//Detect Start of Line Comment and Process it
//			} else if (i < currentLine.length() - 1 && searchArray[i] == '-' && searchArray[i + 1] == '-') {
//				//Need to deal with letters currently in stack
//				evaluateSegment(currentLine, searchArray, lineStartIndex, segementStart, i + 1);
//				segementStart = i;
//
//				commentSegment(segementStart + lineStartIndex, currentLine.length() - segementStart); //Need -1 to hit first '-'
//				i = currentLine.length();
//				segementStart = i;
//
//			//If it is not the start of the comment or string we move along
//			} else {
//				//System.out.println("Current Segment Start: " + segmentStart);
//				evaluateSegment(currentLine, searchArray, lineStartIndex, segementStart, i + 1);
//				//Checking For Word Breaks
//				if (searchArray[i] == ' ' || searchArray[i] == 10 || i == currentLine.length() - 1
//						 || Character.isWhitespace(searchArray[i])) {
//					segementStart = i + 1;
//				}
//			}
//
//		}
//	}

	public void paintDocument(int startIndex, int endIndex) {
//		System.out.println("Printing Documenting");
//		try {
//			String text = getText(0, getLength());
//			String[] lines = text.split("\n", -1);
//
//			int offset = 0;
//			for(int i = 0; i < lines.length; i++) {
//				processLine(lines[i], offset);
//				offset+=lines[i].length();
//			}
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}

	}


	private synchronized void evaluateSegment(String currentLine, char[] searchArray, int lineStartIndex, int segementStart, int segementEnd) {
//		System.out.println("Segement Start:" + segementStart);
//		System.out.println("Segement End:" + segementEnd);

//		//TODO: This just looks bad Feel like there is a better way for this
//		if(searchArray[segementEnd - 1] == ' ') {
//			segementEnd-=2;
//		}


//		String currentSegment = currentLine.substring(segementStart, segementEnd);

//		System.out.println("Current Segement: " + currentSegment);

//		switch (SqlHelper.getWordType(currentSegment)) {
//			case KEY:
//				setCharacterAttributes(segementStart + lineStartIndex, segementEnd - segementStart, textEditorController.getTextColorTheme().getKeyWordSyle(), true);
//				break;
//			case SECONDAY:
//				setCharacterAttributes(segementStart + lineStartIndex, segementEnd - segementStart , textEditorController.getTextColorTheme().getSecondaryStyle(), true);
//				break;
//			case MISC:
//				setCharacterAttributes(segementStart + lineStartIndex, segementEnd - segementStart , textEditorController.getTextColorTheme().getMiscStyle(), true);
//				break;
//			case NUMBER:
//				setCharacterAttributes(segementStart + lineStartIndex, segementEnd - segementStart , textEditorController.getTextColorTheme().getNumberStyle(), true);
//				break;
//			case NONE:
//				setCharacterAttributes(segementStart + lineStartIndex, segementEnd - segementStart , textEditorController.getTextColorTheme().getDefaultStyle(), true);
//				break;
//		}
	}

	private void commentSegment(int startPos, int length) {
//		System.out.println("Commenting Segement: " + startPos + " " + length);
//		setCharacterAttributes(startPos, length, textEditorController.getTextColorTheme().getCommentStyle(), true);
	}

//	private int evaluateString(char[] searchArray, int lineStartIndex, int startPos) {
//		boolean stringTerminated = false;
//		int endPosition = startPos + 1;
//
//		while (endPosition < searchArray.length && !stringTerminated) {
//			//Check for String termination
//			if(searchArray[endPosition] == '\"') {
//				stringTerminated = true;
//			}
//			endPosition++;
//		}
////		String string = currentLine.substring(startPos, endPosition);
////		System.out.println("String detected as " + string);
//		setCharacterAttributes(startPos + lineStartIndex, endPosition - startPos,  textEditorController.getTextColorTheme().getStringStyle(), true);
//		return endPosition;
//	}

	public void remove (int offs, int len) throws BadLocationException {
		super.remove(offs, len);

	}

	public enum SegmentType {
		STRING,
		NUMBER, KEYWORD, MISC, SECONDARY, TEXT, COMMENT
	}
}
