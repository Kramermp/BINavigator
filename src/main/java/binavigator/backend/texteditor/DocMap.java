package binavigator.backend.texteditor;

import java.util.Arrays;

public class DocMap {
	int[][] ignoreSegments = new int[0][0];


	public DocMap() {

	}

	public void add(int start, int end) {
		ignoreSegments = Arrays.copyOf(ignoreSegments, ignoreSegments.length + 1);
		ignoreSegments[ignoreSegments.length - 1] = new int[] {start, end};
	}

	public boolean isInIgnore(int indexToCheck) {
		for (int i = 0; i < ignoreSegments.length; i++) {
			if (indexToCheck >= ignoreSegments[i][0] && indexToCheck <= ignoreSegments[i][1]) {
				return true;
			}
		}

		return false;
	}

	public int getSearchDecrement(int indexToCheck) {
		for(int i = 0; i < ignoreSegments.length; i++) {
			if(indexToCheck > ignoreSegments[i][0]  && indexToCheck < ignoreSegments[i][1]) {
				return (indexToCheck - ignoreSegments[i][0]);
			}
		}

		return 1;
	}

	public int getSearchIncrement(int indexToCheck) {
		for(int i = 0; i < ignoreSegments.length; i++) {
			if(indexToCheck  > ignoreSegments[i][0]  && indexToCheck < ignoreSegments[i][1]) {
				return (ignoreSegments[i][1] - indexToCheck);
			}
		}

		return 1;
	}

	public int size() {
		return  ignoreSegments.length;
	}
}
