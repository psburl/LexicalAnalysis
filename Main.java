import java.util.List;
import automaton.DeterministicAutomaton;
import grammar.Rule;
import io.SingletonInput;
import minimization.UnreachableRemotion;
import java.util.ArrayList;
import java.util.Arrays;
import globals.GlobalInfo;
import metadata.Metadata;
import java.io.IOException;
import java.util.HashMap;

public class Main {

	private static DeterministicAutomaton getAutomaton() throws Exception{

		List<String> input = SingletonInput.getInstance().getContentLines();
		List<String> grammar = new ArrayList<String>();

		for (String entry : input){
			if(entry.charAt(0) == '<')
				grammar.add(entry);
			else
				GlobalInfo.getInstance().addToken(entry);
		}

		List<Rule> rules = Rule.mount(grammar);
		
		DeterministicAutomaton automaton = new DeterministicAutomaton(rules);
		
		UnreachableRemotion remotion = new UnreachableRemotion(automaton);
		
		automaton = remotion.Remove();

		return automaton;
	}

	private static void fillReconizerStates(DeterministicAutomaton automaton) throws Exception{

		for (String token : GlobalInfo.getInstance().getAllTokens()){
			
			Rule rule = automaton.getRuleFromToken(GlobalInfo.getInstance().getInitialState());

			if(rule == null)
				throw new IOException("impossible to find initial state");
			
			String nextState = "";
			for (Character character : token.toCharArray()){
				
				if(rule == null || rule.productions == null)
					break;

				List<String> productions = rule.productions.get(character.toString());
				
				if(productions == null){
					System.out.println("token '" + token + "' cannot be recognized");
					nextState = null;
					break;
				}

				rule = automaton.getRuleFromToken(productions.get(0));
				nextState = productions.get(0);
			}

			if(nextState != null){
				Rule recognizer = automaton.getRuleFromToken(nextState);
				if(recognizer == null)
					break;
				rule.setRecognizedToken(token);	
				System.out.println("rule " + recognizer.getRule() + " recognize '" + token + "'");
			}
		}
	}

	public static void main(String[] args) throws Exception {
			
		DeterministicAutomaton automaton = getAutomaton();
		fillReconizerStates(automaton);
		automaton.printTable();

		List<String> errors = new ArrayList<String>();
		List<Metadata> queue = new ArrayList<Metadata>();
		HashMap<String,Metadata> ts = new HashMap<String, Metadata>();


		String font = "int a: \"0\"; if a=2 then print:a;";
		font = font.replace(":", " : ").replace(";", " ; ").replace("=", " = ").replace("  ", " ");
		for (String word : font.split(" ")){

			Rule rule = automaton.getRuleFromToken(GlobalInfo.getInstance().getInitialState());

			if(rule == null)
				throw new IOException("impossible to find initial state");

			for (Character character : word.toCharArray()){

				if(rule == null)
					break;

				List<String> productions = rule.productions.get(character.toString());
				
				if(productions == null){
					rule = null;
					break;
				}
				
				rule = automaton.getRuleFromToken(productions.get(0));
			}

			if(rule != null){
				queue.add(new Metadata("0", rule.getRule(), word));
			}
			else if(word.matches("-?\\d+(\\.\\d+)?")){  //match a number with optional '-' and decimal.)
				queue.add(new Metadata("1", "number", word));
			}
			else if(word.matches("-?\"[^\"]+\"?")){
				queue.add(new Metadata("2", "string", word));
			}
			else{
				if(word.matches("-?\\d[^.]+?"))
					errors.add("id " + word + " cannot start by number");
				else
					queue.add(new Metadata("3", "id", word));
			}
		}

		int total = queue.size();
		for(int i=0; i< total; i++){

			if(i + 3 >= total)
				continue;

			Metadata current = queue.get(i);

			if(!current.id.equals("0"))
				continue;
			
			if(! Arrays.asList("INT.,DBL.,CHR.,BOOL.".split(",")).contains(current.type))
				continue;
			
			if(queue.get(i+1).id != "3")
				continue;
			
			if(!queue.get(i+2).type.equals("TP"))
				continue;
			
			if(! Arrays.asList("1,2".split(",")).contains(queue.get(i+3).id))
				continue;

			queue.get(i+1).lexVal  = queue.get(i+3).value;
			System.out.println(queue.get(i+1).value + "=" + queue.get(i+3).value);
			
		}

		for (Metadata meta : queue)	{
			ts.put(meta.value, meta);
		}	

		for (Metadata meta : ts.values()){
			System.out.println("id: " + meta.id);
			System.out.println("type: " + meta.type);
			System.out.println("value: " + meta.value);
			System.out.println("---------------------");
		}
	}
}
