package solver;

public class Sum extends Expr {
    
    private Expr[] expr;
    
    public Sum(Expr... expr) { this.expr = expr.clone(); }
    
    public Expr[] getExpr() { return expr.clone(); }
    
    @Override
    public String toString() {
        String str = "";
        for(Expr e : expr) str += e + " + ";
        return str.substring(0, str.length() - 3);
    }
}