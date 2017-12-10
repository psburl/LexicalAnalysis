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
import java.io.*;

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
				//System.out.println("rule " + recognizer.getRule() + " recognize '" + token + "'");
			}
		}
	}

	private static List<Metadata> lexicalAnalisys() throws Exception{
		
		System.out.println("Starting lexical analisys...");

		DeterministicAutomaton automaton = getAutomaton();
		fillReconizerStates(automaton);

		List<String> errors = new ArrayList<String>();
		List<Metadata> queue = new ArrayList<Metadata>();

		String font = String.join("\n",SingletonInput.readFile("font.txt"));
		font = font.replace(":", " : ")
		.replace(";", " ; ")
		.replace("!=", " != ")
		.replace("=", " = ")
		.replace("\t", "")
		.replace("  ", " ")
		.replace("	", " ");// this is different by the last!!


		System.out.println("Starting recognizing tokens..");
		for(String line : font.split("\n")){
			for (String word : line.split(" ")){

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

				if(rule != null && GlobalInfo.getInstance().isFinalState(rule.getRule()))
					queue.add(new Metadata(rule.getRule(), word));
				else
					errors.add("token '" + word + "' cannot be recognized");
			}

			queue.add(new Metadata("\n", "\n"));
		}

		if(errors.size() > 0){
			System.out.println("Error: ");
			for(String error : errors)
				System.out.println(error);
			return null;
		}
		else{
			System.out.println("OK! ");
			/*
			for (Metadata meta : queue){
				System.out.println("type: " + meta.type);
				if(!meta.lexval.equals(meta.type))
					System.out.println("lexval: " + meta.lexval);
				System.out.println("---------------------");
			}
			*/
			for (Metadata meta : queue)
				System.out.print(meta.type + " ");
			System.out.println("");
		}

		return queue;
	}

	public static void main(String[] args) throws Exception {
		List<Metadata>  queue = lexicalAnalisys();
		HashMap<String,Metadata> ts = new HashMap<String, Metadata>();


		String xml = "";
		if(queue != null){
			xml = "<root>\n";
			for(Metadata data : queue){
				
				xml += "\t<metadata>\n\t";
				xml += "\t<type><![CDATA[" + data.type + "]]></type>\n\t";
				xml += "\t<lexval><![CDATA[" + data.lexval + "]]></lexval>\n";
				xml += "\t</metadata>\n";
			}
			xml += "</root>\n";
		}
		else{
			xml = "<root></root>\n";
		}

	  	BufferedWriter output = null;
        try {
            File file = new File("lexical.xml");
            output = new BufferedWriter(new FileWriter(file));
            output.write(xml);
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
          if ( output != null ) {
            output.close();
          }
        }
	}
}
