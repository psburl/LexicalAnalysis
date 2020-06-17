package grammar;

import globals.GlobalInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import defines.Defines;

public class Rule {

	private String rule = "";
	private String recognizedToken = "";
	public Map<String, List<String>> productions = new HashMap<String, List<String>>();

	public static List<Rule> mount(List<String> inputLines) throws Exception {
		List<Rule> rules = new ArrayList<Rule>();
		for (String input : inputLines) {
			Rule rule = new Rule(input);
			rules.add(rule);
			GlobalInfo.getInstance().addState(rule.getRule());
		}
		if (rules.size() > 0) {
			GlobalInfo.getInstance().addInitialState(rules.get(0).getRule());
		}
		return rules;
	}

	public Rule() {

	}

	public Rule(String input) {
		try {
			this.rule = getRuleFromInput(input);
			this.productions = getProductionsFromInput(input);
			for (String key : this.productions.keySet()) {
				if (this.productions.get(key).size() == 0 || this.productions.get(key).get(0).equals(Defines.NULL)) {
					GlobalInfo.getInstance().addFinalState(this.rule);
					break;
				}
			}
		} catch (Exception e) {

		}
	}

	private static String getRuleFromInput(String input) throws Exception {
		String rule = Arrays.asList(input.split("::=")).get(0);
		if (validateRule(rule) == false) {
			throw new Exception("Input:" + input + " has an invalid rule pattern");
		}
		rule = rule.trim().replace("<", "").replace(">", "");
		return rule;
	}

	private static boolean validateRule(String rule) {
		Pattern pattern = Pattern.compile("^([^<]?)+<[^>]+>([^:]?)+");
		Matcher matcher = pattern.matcher(rule);
		return matcher.find();
	}

	private static Map<String, List<String>> getProductionsFromInput(String input) {
		String productions = Arrays.asList(input.split("::=")).get(1);

		Map<String, List<String>> map = new HashMap<String, List<String>>();

		for (String production : Arrays.asList(productions.split("\\|"))) {
			List<String> terminals = Arrays.asList(production.split("<"));
			String terminal = terminals.get(0).trim();

			if (terminal.equals("") || terminal.equals(Defines.ESPSLON)) {
				terminal = Defines.ESPSLON;
			}

			if (map.containsKey(terminal) == false) {
				map.put(terminal, new ArrayList<String>());
			}

			if (terminals.size() == 1) {
				map.get(terminal).add(Defines.NULL);
				continue;
			}

			String nonTerminal = "";
			nonTerminal = terminals.get(1).trim().replace(">", "");
			GlobalInfo.getInstance().addLetter(terminal);
			map.get(terminal).add(nonTerminal);
		}
		return map;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public void setRecognizedToken(String token) {
		this.recognizedToken = token;
	}

	public String getRecognizedToken() {
		return this.recognizedToken;
	}

	public Map<String, List<String>> getProductions() {
		return productions;
	}

	public boolean hasIndeterminism() {
		for (String key : this.productions.keySet()) {
			if (this.productions.get(key).size() > 1) {
				return true;
			}
		}
		return false;
	}

	public void print() {
		System.out.print(this.rule + "::=");
		for (String key : this.productions.keySet()) {
			for (String pd : this.productions.get(key)) {
				System.out.print(key + "<" + pd + ">" + "|");
			}
		}
		System.out.print("\r\n");
	}
}
