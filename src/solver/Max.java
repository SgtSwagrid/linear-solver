package solver;

public class Max extends Expr {
    
    private Expr[] expr;
    
    public Max(Expr... expr) { this.expr = expr.clone(); }
    
    public Expr[] getExpr() { return expr.clone(); }
    
    @Override
    public String toString() {
        String str = "max(";
        for(Expr e : expr) str += e + ", ";
        return str.substring(0, str.length() - 2) + ")";
    }
}