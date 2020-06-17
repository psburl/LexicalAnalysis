package minimization;

import java.util.ArrayList;
import java.util.List;
import automaton.DeterministicAutomaton;
import globals.GlobalInfo;
import grammar.Rule;

public class UnreachableRemotion {

	DeterministicAutomaton automaton;

	public UnreachableRemotion(final DeterministicAutomaton automaton) {
		this.automaton = automaton;
	}

	public DeterministicAutomaton Remove() {
		final DeterministicAutomaton automatonWithNonUnreachableStates = new DeterministicAutomaton();
		for (final Rule rule : automaton.getRules()) {
			if (rule.getRule().equals(GlobalInfo.getInstance().getInitialState())) {
				automatonWithNonUnreachableStates.addRule(rule);
				continue;
			}
			if (find(rule.getRule())) {
				automatonWithNonUnreachableStates.addRule(rule);
			}
		}
		return automatonWithNonUnreachableStates;
	}

	public boolean find(final String findKey) {
		final List<String> alreadyPassed = new ArrayList<String>();
		final String initialStateForAllSearches = GlobalInfo.getInstance().getInitialState();
		alreadyPassed.add(initialStateForAllSearches);
		if (findKey.equals(initialStateForAllSearches)) {
			return true;
		}
		return recursion(findKey, initialStateForAllSearches, alreadyPassed);
	}

	public boolean recursion(final String findKey, final String initialStateForSearch,
			final List<String> alreadyPassed) {
		final Rule rule = getReferenciedInitialState(initialStateForSearch);
		for (final String key : rule.productions.keySet()) {
			for (final String nonTerminalToCompare : rule.productions.get(key)) {
				if (nonTerminalToCompare.equals(findKey)) {
					return true;
				}
				if (alreadyPassed.contains(nonTerminalToCompare) == false) {
					alreadyPassed.add(nonTerminalToCompare);
					if (recursion(findKey, nonTerminalToCompare, alreadyPassed)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Rule getReferenciedInitialState(final String initialStateForSearch) {
		Rule referenciedRule = new Rule();
		for (final Rule rule : automaton.getRules()) {
			if (rule.getRule().equals(initialStateForSearch)) {
				referenciedRule = rule;
			}
		}
		return referenciedRule;
	}
}
