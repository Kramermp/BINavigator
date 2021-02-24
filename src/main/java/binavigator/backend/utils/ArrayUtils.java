package binavigator.backend.utils;

public class ArrayUtils {

	public static boolean containsIgnoreCase(String[] searchArray, String searchString) {
		for(int i = 0; i < searchArray.length; i++) {
			if(searchArray[i].equalsIgnoreCase(searchString)) {
				return true;
			}
		}
		return false;
	}
}
