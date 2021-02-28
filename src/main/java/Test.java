import binavigator.backend.sql.WordMap;
import binavigator.backend.sql.WordType;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;

public class Test {

	public static void main (String args[]) {
		WordMap testMap = new WordMap();
		testMap.put("test".toCharArray(), WordType.KEY);

		System.out.println(testMap.getWordType("test".toCharArray()));
		System.out.println(testMap.size());
	}
}