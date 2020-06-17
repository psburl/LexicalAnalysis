package globals;

import java.util.ArrayList;
import java.util.List;

public class GlobalInfo {

	private static GlobalInfo instance = null;

	private GlobalInfo() {
	}

	private final List<String> letters = new ArrayList<String>();
	private final List<String> finalStates = new ArrayList<String>();
	private final List<String> states = new ArrayList<String>();
	private String initialState = "";
	private final List<String> tokens = new ArrayList<String>();

	public static GlobalInfo getInstance() {
		if (instance == null) {
			instance = new GlobalInfo();
		}
		return instance;
	}

	public List<String> getLetters() {
		return letters;
	}

	public void addLetter(final String letter) {
		if (instance.letters.contains(letter) == false) {
			instance.letters.add(letter);
		}
	}

	public void addToken(final String token) {
		if (instance.tokens.contains(token) == false) {
			instance.tokens.add(token);
		}
	}

	public List<String> getFinalStates() {
		return finalStates;
	}

	public boolean isFinalState(final String state) {
		return instance.finalStates.contains(state);
	}

	public List<String> getAllStates() {
		return states;
	}

	public List<String> getAllTokens() {
		return tokens;
	}

	public void addFinalState(final String finalStates) {
		if (instance.finalStates.contains(finalStates) == false) {
			instance.finalStates.add(finalStates);
		}
	}

	public void addInitialState(final String initialState) {
		instance.initialState = initialState;
	}

	public void addState(final String state) {
		if (instance.states.contains(state) == false) {
			instance.states.add(state);
		}
	}

	public boolean hasState(final String state) {
		return instance.states.contains(state);

	}

	public String getInitialState() {
		return initialState;
	}
}
