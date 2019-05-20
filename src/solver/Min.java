package solver;

public class Min extends Expr {
    
    private Expr[] expr;
    
    public Min(Expr... expr) { this.expr = expr.clone(); }
    
    public Expr[] getExpr() { return expr.clone(); }
    
    @Override
    public String toString() {
        String str = "min(";
        for(Expr e : expr) str += e + ", ";
        return str.substring(0, str.length() - 2) + ")";
    }
}