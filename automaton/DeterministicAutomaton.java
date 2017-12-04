package automaton;
import grammar.Rule;
import java.util.ArrayList;
import java.util.List;
import defines.Defines;
import globals.GlobalInfo;

public class DeterministicAutomaton {

	private List<Rule> rules = new ArrayList<Rule>();
	
	public DeterministicAutomaton(){}
	
	public  DeterministicAutomaton(List<Rule> rules){
				
		
		Boolean hasIndeterminism = false; 

		this.rules.addAll(rules);
		
		do{

			List<Rule> createdRules = new ArrayList<Rule>();

			hasIndeterminism = false; 

			for(Rule rule : this.rules){
				
				if(rule.hasIndeterminism() == false)
					continue;
				
				hasIndeterminism = true;

				for(String key : rule.getProductions().keySet()){
					
					List<String> productions = rule.getProductions().get(key);
					
					if(rule.getProductions().get(key).size() <= 1)
						continue;
									
					Rule newRule = createRule(rules, productions);
					
					if(newRule != null)
						createdRules.add(newRule);
				}
			}
			
			this.rules.addAll(createdRules);
			
			for(Rule rule : this.rules){
				
				if(rule.hasIndeterminism() == false)
					continue;

				hasIndeterminism = true;
				
				for(String key : rule.getProductions().keySet()){
					
					List<String> productions = rule.getProductions().get(key);
					
					if(rule.getProductions().get(key).size() <= 1)
						continue;
					
					String hash = joinProductionsNamesHash(productions);
					
					
					for(Rule createdRule : createdRules){
											
						if(createdRule.getRule().equals(hash) == false)
							continue;
						
						List<String> listWithHashProduction = new ArrayList<String>();
						listWithHashProduction.add(hash);
						 
						rule.productions.put(key, listWithHashProduction);
					}
				}
			}

		}while(hasIndeterminism);
	}
	
	
	private Rule createRule(List<Rule> rules, List<String> productions){
		
		Rule ruleToInsert = new Rule();
		String hash = joinProductionsNamesHash(productions);
		

		if(GlobalInfo.getInstance().hasState(hash))
			return null;
		
		GlobalInfo.getInstance().addState(hash);		
		ruleToInsert.setRule(hash);
		
		for(String production : productions){

			for(Rule rule : rules){
				
				if(rule.getRule().equals(production) == false)
					continue;
				
				ruleToInsert.productions.putAll(rule.getProductions());
			}
		}
		
		for(String key : ruleToInsert.productions.keySet()){
			if(ruleToInsert.productions.get(key).size() == 0 || ruleToInsert.productions.get(key).get(0).equals(Defines.NULL)){
				GlobalInfo.getInstance().addFinalState(ruleToInsert.getRule());
				break;
			}
		}
				
		return ruleToInsert;			
	}

	private String joinProductionsNamesHash(List<String> productions) {
		return "{" + String.join(",", productions) + "}";
	}
	
	public List<Rule> getRules(){
		return this.rules;
	}
	
	public void addRule(Rule rule){
		this.rules.add(rule);
	}
	
	public void printTable(){
		
		System.out.print("\t|");
		for(String letter : GlobalInfo.getInstance().getLetters())
			System.out.print(letter + "\t|");
		System.out.print("\n");
		
		for(Rule rule : this.rules){
			
			System.out.print(rule.getRule() + "\t|");
			
			for(String letter : GlobalInfo.getInstance().getLetters()){
				
				if(rule.getProductions().containsKey(letter) == false )
					System.out.print("ERRO\t|");
				else{
					List<String> productions = rule.getProductions().get(letter);
					
					if(productions.get(0) == Defines.NULL){
						System.out.print("ERRO\t|");
						continue;
					}
					
					System.out.print(String.join(",", productions) + "\t|");
				}				
			}
			
			System.out.print("\n");
		}
	}

	public Rule getRuleFromToken(String token){

		for(Rule rule : this.rules){
			if(rule.getRule().equals(token))
				return rule;
		}

		return null;
	}	
}
