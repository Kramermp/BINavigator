package binavigator.backend.texteditor;

import java.util.ArrayList;
import java.util.Arrays;

public class CommentMap {
	int[][] comments = new int[0][0];


	public CommentMap() {

	}

	public void add(int start, int end) {
		comments = Arrays.copyOf(comments, comments.length + 1);
		comments[comments.length - 1] = new int[] {start, end};
	}

	public boolean isInComment(int indexToCheck) {
		for (int i = 0; i < comments.length; i++) {
			if (indexToCheck >= comments[i][0] && indexToCheck <= comments[i][1]) {
				return true;
			}
		}

		return false;
	}

	public int getSearchDecrement(int indexToCheck) {
		for(int i = 0; i < comments.length; i++) {
			if(indexToCheck >= comments[i][0]  && indexToCheck <= comments[i][0]) {
				return (indexToCheck - comments[i][0]) + 1; // Magic +1 Guarantees a decrement so if you pass in start
			}
		}

		return 1;
	}

	public int getSearchIncrement(int indexToCheck) {
		for(int i = 0; i < comments.length; i++) {
			if(indexToCheck >= comments[i][0]  && indexToCheck <= comments[i][0]) {
				return (comments[i][1] - indexToCheck) + 1; // Magic +1 Guarantees a decrement so if you pass in start
			}
		}

		return 1;
	}

	public int size() {
		return  comments.length;
	}
}
