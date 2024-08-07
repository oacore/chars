package uk.ac.core.common.model.legacy;

/**
 *
 * @author la4227 <lucas.anastasiou@open.ac.uk>
 */
public enum AlgorithmType {

    // algorithms for pdf downloading    
    /**
     * Depth First Search
     */
    DFS("DFS", "Depth First Search", 1),
    /**
     * Breadth First Search
     */
    BFS("BFS", "Breadth First Search", 2);
    
    private String name;
    private String text;
    private int dbFlag;

    AlgorithmType(String name, String text, int dbFlag) {
        this.name = name;
        this.text = text;
        this.dbFlag = dbFlag;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getName(){
        return name;
    }
    
    public int getDbFlag() {
        return dbFlag;
    }
    
    static public AlgorithmType fromDbFlag(int dbFlag) {
        for (AlgorithmType type : AlgorithmType.values()) {
            if (type.getDbFlag()==dbFlag) {
                return type;
            }
        }
        return null;
    }
    
    static public AlgorithmType fromName(String name) {
        for (AlgorithmType type : AlgorithmType.values()) {
            if (type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }
}
