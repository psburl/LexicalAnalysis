import java.util.List;
import automaton.DeterministicAutomaton;
import grammar.Rule;
import io.SingletonInput;
import minimization.UnreachableRemotion;
import java.util.ArrayList;
import globals.GlobalInfo;
import metadata.Metadata;
import java.io.IOException;
import java.io.*;

public class Main {

	static boolean showTable = false;

	private static DeterministicAutomaton getAutomaton() throws Exception {
		final List<String> input = SingletonInput.getInstance().getContentLines();
		final List<String> grammar = new ArrayList<String>();

		for (final String entry : input) {
			if (entry.charAt(0) == '<') {
				grammar.add(entry);
			} else {
				GlobalInfo.getInstance().addToken(entry);
			}
		}
		final List<Rule> rules = Rule.mount(grammar);
		DeterministicAutomaton automaton = new DeterministicAutomaton(rules);
		final UnreachableRemotion remotion = new UnreachableRemotion(automaton);
		automaton = remotion.Remove();
		return automaton;
	}

	private static void fillReconizerStates(final DeterministicAutomaton automaton) throws Exception {
		for (final String token : GlobalInfo.getInstance().getAllTokens()) {
			Rule rule = automaton.getRuleFromToken(GlobalInfo.getInstance().getInitialState());
			if (rule == null) {
				throw new IOException("impossible to find initial state");
			}
			String nextState = "";
			for (final Character character : token.toCharArray()) {
				if (rule == null || rule.productions == null) {
					break;
				}
				final List<String> productions = rule.productions.get(character.toString());

				if (productions == null) {
					System.out.println("token '" + token + "' cannot be recognized");
					nextState = null;
					break;
				}
				rule = automaton.getRuleFromToken(productions.get(0));
				nextState = productions.get(0);
			}
			if (nextState != null) {
				final Rule recognizer = automaton.getRuleFromToken(nextState);
				if (recognizer == null) {
					break;
				}
			}
		}
	}

	private static List<Metadata> lexicalAnalisys() throws Exception {
		System.out.println("Starting lexical analisys...");
		final DeterministicAutomaton automaton = getAutomaton();
		fillReconizerStates(automaton);
		
		final List<String> errors = new ArrayList<String>();
		final List<Metadata> queue = new ArrayList<Metadata>();
		String font = String.join("\n", SingletonInput.readFile("data/font.txt"));
		font = font.replace(":", " : ").replace(";", " ; ").replace("!=", " != ").replace("=", " = ").replace("\t", "")
				.replace("  ", " ").replace("	", " ");// this is different by the last!!
		
		System.out.println("Starting recognizing tokens..");
		int count = 0;
		for (final String line : font.split("\n")) {
			count++;
			for (final String word : line.split(" ")) {
				Rule rule = automaton.getRuleFromToken(GlobalInfo.getInstance().getInitialState());
				if (rule == null) {
					throw new IOException("impossible to find initial state");
				}
				for (final Character character : word.toCharArray()) {
					if (rule == null) {
						break;
					}
					final List<String> productions = rule.productions.get(character.toString());
					if (productions == null) {
						rule = null;
						break;
					}
					rule = automaton.getRuleFromToken(productions.get(0));
				}

				if (rule != null && GlobalInfo.getInstance().isFinalState(rule.getRule())) {
					queue.add(new Metadata(rule.getRule(), word));
				}
				else {
					errors.add("token '" + word + "' cannot be recognized. Line: " + count);
				}
			}
			queue.add(new Metadata("\n", "\n"));
		}

		if (errors.size() > 0) {
			System.out.println("Error: ");
			for (final String error : errors) {
				System.out.println(error);
			}
			return null;
		} else {
			System.out.println("OK! ");
			if (showTable) {
				for (final Metadata meta : queue) {
					if (meta.type.equals("\n")) {
						continue;
					}
					System.out.println("type: " + meta.type);
					if (!meta.lexval.equals(meta.type)) {
						System.out.println("lexval: " + meta.lexval);
					}
					System.out.println("---------------------");
				}
			}
			for (final Metadata meta : queue) {
				System.out.print(meta.type + " ");
			}
			System.out.println("");
		}
		return queue;
	}

	public static void main(final String[] args) throws Exception {
		if (args.length > 0) {
			if (args[0].equals("-t")) {
				showTable = true;
			}
		}
		final List<Metadata> queue = lexicalAnalisys();
		String xml = "";
		if (queue != null) {
			xml = "<root>\n";
			for (final Metadata data : queue) {
				xml += "\t<metadata>\n\t";
				xml += "\t<type><![CDATA[" + data.type + "]]></type>\n\t";
				xml += "\t<lexval><![CDATA[" + data.lexval + "]]></lexval>\n";
				xml += "\t</metadata>\n";
			}
			xml += "</root>\n";
		} else {
			xml = "<root></root>\n";
		}

		BufferedWriter output = null;
		try {
			final File file = new File("out/lexical.xml");
			output = new BufferedWriter(new FileWriter(file));
			output.write(xml);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}
}
