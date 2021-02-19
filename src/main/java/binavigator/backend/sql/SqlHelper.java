package binavigator.backend.sql;

import binavigator.backend.ArrayUtils;

import java.util.Arrays;

public class SqlHelper {
	public static final String[] keyWordArray = new String[]{"SELECT", "FROM"};

	public static boolean isKeyWord(String toCheck) {
		return ArrayUtils.containsIgnoreCase(keyWordArray, toCheck);
	}


}
