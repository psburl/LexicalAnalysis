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

    public Metadata(String id, String type, String value){
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public String id = "";
    public String type = "";
    public String value = "";
    public String lexVal = "";
}