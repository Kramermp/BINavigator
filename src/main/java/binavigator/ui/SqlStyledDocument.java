package binavigator.ui;

import binavigator.backend.sql.SqlHelper;
import binavigator.ui.colortheme.Monokai;
import binavigator.ui.colortheme.TextColorTheme;

import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SqlStyledDocument extends DefaultStyledDocument {
	final StyleContext cont = StyleContext.getDefaultStyleContext();

	final TextColorTheme textColorTheme = new Monokai();
	
	final AttributeSet keyWordSyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, textColorTheme.getKeyWordColor());
	final AttributeSet defaultStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, textColorTheme.getTextColor());

	public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offset, str, a);

		refreshDocument();
	}

	private synchronized void refreshDocument() throws BadLocationException {
		String text = getText(0, getLength());

		String[] lines = text.split("\n");
		List<HiliteWord> wordList;
		int lineStart = 0;
		for(String currentLine : lines) {
			System.out.println(currentLine);
			wordList = processWords(currentLine);
			for (HiliteWord word : wordList) {
				int p0 = word._position + lineStart;
				setCharacterAttributes(p0, word._word.length(), keyWordSyle, true);
			}
			lineStart+=currentLine.length() + 1;
		}

	}

	public void remove (int offs, int len) throws BadLocationException {
		super.remove(offs, len);

		refreshDocument();
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

		int _position;
		String _word;

		public HiliteWord(String word, int position) {
			_position = position;
			_word = word;
		}
	}
}
