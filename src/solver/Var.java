package solver;

public class Var extends Expr {
    
    private String name = "var" + numVars++;
    private static int numVars = 0;
    
    private double val = 0.0;
    
    public Var() {}
    
    public Var(String name) { this.name = name; }
    
    public Var(float val) { this.val = val; }
    
    public Var(String name, float val) {
        this.name = name;
        this.val = val;
    }
    
    public String getName() { return name; }
    
    public double getVal() { return val; }
    
    @Override
    public String toString() { return name + ":" + val; }
}