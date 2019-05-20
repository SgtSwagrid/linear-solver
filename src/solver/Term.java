package solver;

public class Term extends Expr {
    
    private Var var;
    
    private double coeff = 1.0;
    
    public Term(Var var) { this.var = var; }
    
    public Term(Var var, double coeff) {
        this.var = var;
        this.coeff = coeff;
    }
    
    public Var getVar() { return var; }
    
    public double getCoeff() { return coeff; }
    
    @Override
    public String toString() { return coeff + "*" + var; }
}