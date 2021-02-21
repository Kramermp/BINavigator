package binavigator.backend.sql;

import binavigator.backend.ArrayUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class SqlHelper {

	//Used for Number Checking
	private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static final String[] keyWordArray = new String[]{"SELECT", "FROM", "CREATE", "INSERT", "INTO", "ALTER",
			"ADD", "DISTINCT", "UNIQUE", "UPDATE", "SET", "DELETE", "TRUNCATE", "AS", "ORDER", "BY", "ASC", "DESC",
			"BETWEEN", "OR", "NOT", "LIMIT", "IS", "NULL", "DROP", "COLUMN", "TABLE", "DATABASE", "GROUP", "HAVING",
			"IN", "JOIN", "UNION", "EXISTS", "LIKE", "CASE", "WHERE", "ALL"};

	public static final String[] secondaryWordArray = new String[] {"AND", "OR"};

	public static final String[] miscWordArray = new String[] {"=", "<>", ">", "<", ">=" , "<="};

	private static boolean isKeyWord(String toCheck) {
		return ArrayUtils.containsIgnoreCase(keyWordArray, toCheck);

	}

	public static WordType getWordType(String toCheck) {
		if (toCheck == null) {
			return WordType.NONE;
		}

		if(ArrayUtils.containsIgnoreCase(keyWordArray, toCheck)) {
			return WordType.KEY;
		} else if (ArrayUtils.containsIgnoreCase(secondaryWordArray, toCheck)) {
			return  WordType.SECONDAY;
		} else if (ArrayUtils.containsIgnoreCase(miscWordArray, toCheck)){
			return WordType.MISC;
		} else if (pattern.matcher(toCheck).matches()) {
			return WordType.NUMBER;
		}
		return WordType.NONE;
	}

	public static void parseSql(String sqlToParse) {
		StringBuilder build = new StringBuilder(sqlToParse);

		//Check for Semi Colons to split statements

	}


}
