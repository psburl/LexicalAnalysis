package metadata;

import java.util.List;
import automaton.DeterministicAutomaton;
import grammar.Rule;
import io.SingletonInput;
import minimization.UnreachableRemotion;
import java.util.ArrayList;
import java.util.Arrays;
import globals.GlobalInfo;
import java.io.IOException;

public class Metadata {

    public Metadata(String type, String lexval){
        this.type = type;
        this.lexval = lexval;
    }

    public String type = "";
    public String lexval = "";
}