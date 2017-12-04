package minimization;

import java.util.ArrayList;
import java.util.List;

import automaton.DeterministicAutomaton;
import globals.GlobalInfo;
import grammar.Rule;

public class UnreachableRemotion {
	
	DeterministicAutomaton automaton;
	
	public UnreachableRemotion(DeterministicAutomaton automaton){
		this.automaton = automaton;
	}
	
	public DeterministicAutomaton Remove(){
		
		DeterministicAutomaton automatonWithNonUnreachableStates = new DeterministicAutomaton();
				
		for(Rule rule : automaton.getRules()){
			
			if(rule.getRule().equals(GlobalInfo.getInstance().getInitialState())){
				automatonWithNonUnreachableStates.addRule(rule);
				continue;
			}
			
			if(find(rule.getRule()))
				automatonWithNonUnreachableStates.addRule(rule);	
		}
		
		return automatonWithNonUnreachableStates;
	}
	
	public boolean find(String findKey){
		
		List<String> alreadyPassed = new ArrayList<String>();
		
		String initialStateForAllSearches = GlobalInfo.getInstance().getInitialState();
		alreadyPassed.add(initialStateForAllSearches);
		
		if(findKey.equals(initialStateForAllSearches))
			return true;
		
		return recursion(findKey, initialStateForAllSearches, alreadyPassed);
	}
	
	public boolean recursion(String findKey, String initialStateForSearch, List<String> alreadyPassed){
						
		Rule rule = getReferenciedInitialState(initialStateForSearch);

		for(String key : rule.productions.keySet()){

			for(String nonTerminalToCompare : rule.productions.get(key)){

				if(nonTerminalToCompare.equals(findKey))
					return true;
				
				if(alreadyPassed.contains(nonTerminalToCompare) == false){
					alreadyPassed.add(nonTerminalToCompare);
					if(recursion(findKey, nonTerminalToCompare,alreadyPassed))
						return true;
				}
			}
		}
		
		return false;
	}

	private Rule getReferenciedInitialState(String initialStateForSearch) {
		
		Rule referenciedRule = new Rule();
		for(Rule rule : automaton.getRules())
			if(rule.getRule().equals(initialStateForSearch))
				referenciedRule = rule;
		return referenciedRule;
		
	}
}
