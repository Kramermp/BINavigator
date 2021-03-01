package binavigator.ui.texteditor;

import binavigator.backend.BINavController;
import binavigator.backend.sql.SqlHelper;
import binavigator.backend.texteditor.DocMap;
import binavigator.backend.texteditor.TextEditorController;
import binavigator.backend.utils.CharArrayUtil;
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

	private DocMap commentMap = new DocMap();

	public SqlStyledDocument(TextEditorController textEditorController) {
		super();
		this.textEditorController = textEditorController;
	}

	public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offset, str, a);
		long insertStartTime = System.currentTimeMillis();

		paintDocument(0, getLength());

		long insertEndTime = System.currentTimeMillis();

		System.out.println("Insert Completed in " + ((insertEndTime - insertStartTime) / 1000) + "s");
	}

	private char[] addCharToBuffer(char[] buffer, char charToAdd) {
		buffer = Arrays.copyOf(buffer, buffer.length + 1);

		buffer[buffer.length - 1] = charToAdd;
		return buffer;
	}

	private void paintSegment(int start, int length, SegmentType type) {
		switch (type) {
			case STRING:
				//System.out.println("Printing " + start + " - " + end + " as a string");
				setCharacterAttributes(start, length,  textEditorController.getTextColorTheme().getStringStyle(), true);
				return;
			case TEXT:
				//System.out.println("Printing " + start + " - " + end + " as a text");
				setCharacterAttributes(start, length, textEditorController.getTextColorTheme().getDefaultStyle(), false);
				return;
			case MISC:
				setCharacterAttributes(start, length, textEditorController.getTextColorTheme().getMiscStyle(), false);
				return;
			case NUMBER:
				setCharacterAttributes(start, length, textEditorController.getTextColorTheme().getNumberStyle(), false);
				return;
			case KEYWORD:
				setCharacterAttributes(start, length, textEditorController.getTextColorTheme().getKeyWordSyle(), false);
				return;
			case SECONDARY:
				setCharacterAttributes(start, length, textEditorController.getTextColorTheme().getSecondaryStyle(), false);
				return;
			case COMMENT:
				setCharacterAttributes(start, length, textEditorController.getTextColorTheme().getCommentStyle(), false);
		}
	}

	private void processSegment(char[] charBuffer, int segmentStart) {
//		System.out.println("Processing Segment " + segmentStart + " - " + (segmentStart + charBuffer.length));
//		String test = new String(charBuffer);
//		System.out.println(test);

		switch (SqlHelper.getWordType(charBuffer)) {
			case NUMBER:
				paintSegment(segmentStart, charBuffer.length, SegmentType.NUMBER);
				return;
			case KEY:
				paintSegment(segmentStart, charBuffer.length, SegmentType.KEYWORD);
				return;
			case MISC:
				paintSegment(segmentStart, charBuffer.length, SegmentType.MISC);
			case SECONDAY:
				paintSegment(segmentStart, charBuffer.length, SegmentType.SECONDARY);
				return;
			case NONE:
				paintSegment(segmentStart, charBuffer.length, SegmentType.TEXT);
		}
	}

	public void paintDocument(int startIndex, int endIndex) {

		commentMap = new DocMap();
		char[] searchArray = new char[0];

		try {
			searchArray = getText(0, getLength()).toCharArray();
		} catch (BadLocationException e) {
			//This should never happen
		}

		int i = 0;
		int segmentStart = startIndex;

		char[] charBuffer = new char[0];
		for(i = i; i < searchArray.length; i++) {

			if (searchArray[i] == '\'') {
				processSegment(charBuffer, segmentStart);
				segmentStart = i;
				charBuffer = emptyArray;
				charBuffer = CharArrayUtil.findStringEnd(searchArray, charBuffer, i);
				paintSegment(segmentStart, charBuffer.length, SegmentType.STRING);
				i += charBuffer.length -1;
				segmentStart = i + 1;
				charBuffer = emptyArray;
			} else if (searchArray[i] == '-' && i != searchArray.length && searchArray[i + 1] == '-') {
				processSegment(charBuffer, segmentStart);
				segmentStart = i;
				charBuffer = emptyArray;
				charBuffer = CharArrayUtil.findLineCommentEnd(searchArray, charBuffer, i);
				paintSegment(segmentStart, charBuffer.length, SegmentType.COMMENT);
				commentMap.add(i, (i + charBuffer.length - 1));
				i += charBuffer.length - 1;
				segmentStart = i + 1;
				charBuffer = emptyArray;
			} else if(searchArray[i] == '/' && i!= searchArray.length && searchArray[i + 1] == '*') {
				processSegment(charBuffer, segmentStart);
				segmentStart = i;
				charBuffer = emptyArray;
				charBuffer = CharArrayUtil.findBlockCommentEnd(searchArray, charBuffer, i);
				paintSegment(segmentStart, charBuffer.length, SegmentType.COMMENT);
				commentMap.add(i, (i + charBuffer.length - 1));
				i += charBuffer.length -1;
				segmentStart = i + 1;
//				try {
//					System.out.println("Segment Start: " + segmentStart + " " + getText(0, getLength()).charAt(segmentStart));
//				} catch (BadLocationException e) {
//					e.printStackTrace();
//				}
				charBuffer = emptyArray;
			} else if(searchArray[i] == ' ' || searchArray[i] == '\n' || searchArray[i] == '\t') {
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

//		System.out.println(commentMap.size() + " comments in this doc.");
//		for(CommentInstance commentInstance : commentMap.getComments() ) {
//			System.out.println(commentInstance.toString());
//		}
	}

	public void remove (int offs, int len) throws BadLocationException {
		super.remove(offs, len);

		paintDocument(0, getLength());
	}

	public DocMap getCommentMap() {
		return commentMap;
	}

	public enum SegmentType {
		STRING,
		NUMBER, KEYWORD, MISC, SECONDARY, TEXT, COMMENT
	}

}
