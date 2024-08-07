package uk.ac.core.services.web.recommender.services.ctr;

/**
 *
 * @author mc26486
 */
public enum CTRSource {

    UNKNOWN(1),
    DASHBOARD(2),
    DISPLAY(3),
    EXTERNAL(4);

    private int value;

    private CTRSource(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static CTRSource fromRecType(String recType){
        switch(recType){
            case "dashboard":
                return CTRSource.DASHBOARD;
            case "email":
                return CTRSource.EXTERNAL;
            case "core-display":
                return CTRSource.DISPLAY;
            default:
                return CTRSource.UNKNOWN;
        }
    }
}
