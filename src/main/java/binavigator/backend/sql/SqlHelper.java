package binavigator.backend.sql;

import binavigator.backend.utils.ArrayUtils;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SqlHelper {

	//Used for Number Checking
	private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static final char[][] keyWordArray = new char[][]{"SELECT".toCharArray(), "FROM".toCharArray(),
			"CREATE".toCharArray(), "INSERT".toCharArray(), "INTO".toCharArray(), "ALTER".toCharArray(),
			"ADD".toCharArray(), "DISTINCT".toCharArray(), "UNIQUE".toCharArray(), "UPDATE".toCharArray(),
			"SET".toCharArray(), "DELETE".toCharArray(), "TRUNCATE".toCharArray(), "AS".toCharArray(),
			"ORDER".toCharArray(), "BY".toCharArray(), "ASC".toCharArray(), "DESC".toCharArray(),
			"BETWEEN".toCharArray(), "OR".toCharArray(), "NOT".toCharArray(), "LIMIT".toCharArray(),
			"IS".toCharArray(), "NULL".toCharArray(), "DROP".toCharArray(), "COLUMN".toCharArray(),
			"TABLE".toCharArray(), "DATABASE".toCharArray(), "GROUP".toCharArray(), "HAVING".toCharArray(),
			"IN".toCharArray(), "JOIN".toCharArray(), "UNION".toCharArray(),
			"EXISTS".toCharArray(), "LIKE".toCharArray(), "CASE".toCharArray(),
			"WHERE".toCharArray(), "ALL".toCharArray()};

	public static final char[][] secondaryWordArray = new char[][]  {"AND".toCharArray(), "OR".toCharArray()};

	public static final char[][]  miscWordArray = new char[][] {"=".toCharArray(), "<>".toCharArray(), ">".toCharArray(), "<".toCharArray(), ">=" .toCharArray(), "<=".toCharArray()};

	public static final WordMap wordMap = initialize();

	private static WordMap initialize() {
		WordMap tempMap = new WordMap();

		for(int i = 0; i < keyWordArray.length; i++) {
			tempMap.put(keyWordArray[i], WordType.KEY);
		}

		for(int i = 0; i < secondaryWordArray.length; i++) {
			tempMap.put(secondaryWordArray[i], WordType.SECONDAY);
		}

		for(int i = 0; i < miscWordArray.length; i++) {
			tempMap.put(miscWordArray[i], WordType.MISC);
		}

		return tempMap;
	}

	public static WordType getWordType(char[] wordToCheck) {
		WordType wt = wordMap.getWordType(wordToCheck);
		if(wt != WordType.NONE) {
			return wt;
		} else {
			for(int i  = 0; i < wordToCheck.length; i++) {
				if(!Character.isDigit(wordToCheck[i])) {
					if(i == 0 && wordToCheck[i] == '-') {
						//Do Nothing aka allow a - in 0 index for negative numbers
					} else {
						return WordType.NONE;
					}
				}
			}
			return WordType.NUMBER;
		}
	}


	public static void parseSql(String sqlToParse) {
		StringBuilder build = new StringBuilder(sqlToParse);

		//Check for Semi Colons to split statements

	}


}
