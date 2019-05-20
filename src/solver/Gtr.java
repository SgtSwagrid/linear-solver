package solver;

public class Gtr extends Constr {
    
    private Expr expr1, expr2;
    
    public Gtr(Expr expr1, Expr expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    public Expr getExpr1() { return expr1; }
    
    public Expr getExpr2() { return expr2; }
    
    @Override
    public String toString() { return expr1 + " >= " + expr2; }
}