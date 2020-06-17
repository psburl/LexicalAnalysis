package metadata;

public class Metadata {

    public Metadata() {
    }

    public Metadata(final String type, final String lexval) {
        this.type = type;
        this.lexval = lexval;
    }

    public String type = "";
    public String lexval = "";
}