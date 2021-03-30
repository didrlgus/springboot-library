package gabia.library.domain;

public enum Destination {
    HEAD("본사"),
    SEOCHO("서초IDC"),
    GASAN("가산IDC"),
    WCENTER("가산W센터");

    Destination(String value) {
        this.value = value;
    }
    private final String value;
    public String value() { return value; }
}
