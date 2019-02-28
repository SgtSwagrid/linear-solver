package swagrid.constraintsolver;

public class Test {

	public static void main(String[] args) {
		
		Solver s = new Solver();
		
		Variable v1 = new Variable(s);
		Variable v2 = new Variable(s);
		
		new Constraint(s).setVar(v1, 1.0).setVar(v2, 1.0).setSum(6.0); //v1+v2=6.0
		new Constraint(s).setVar(v1, 2.0).setVar(v2, 1.0).setSum(8.0); //2v1+v2=8.0
		
		System.out.println(v1 + "\n" + v2);
	}
}