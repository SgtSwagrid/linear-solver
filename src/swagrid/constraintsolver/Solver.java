package swagrid.constraintsolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver type for Swagrid's linear constraint solver.
 * Represents a universal set of variables and the constraints between them.
 * A solver instance must be passed to variable and constraint constructors.
 * 
 * @author Alec
 */
public class Solver {
	
	/** The list of constraints associated with this solver. */
	private List<Constraint> constr = new ArrayList<>();
	
	/** The list of variables associated with this solver. */
	private List<Variable> var = new ArrayList<>();
	
	/** Whether auto-solve is enabled. */
	private boolean autoSolve = true;
	
	/**
	 * Sets whether auto-solve is enabled, which it is by default.
	 * Auto-solve will automatically re-solve for all variables every time something is updated.
	 * Disable for performance-critical applications.
	 * Without auto-solve, solve() must be called manually to update variables.
	 * @param autoSolve whether auto-solve should be enabled.
	 */
	public void setAutoSolve(boolean autoSolve) { this.autoSolve = autoSolve; }
	
	/**
	 * @return whether auto-solve is enabled.
	 */
	boolean isAutoSolveEnabled() { return autoSolve; }
	
	/**
	 * Manually triggers the solving for all variables once.
	 * This need not be called provided auto-solve is enabled.
	 */
	public void solve() {
		
		//Convert the list of constraints to matrix representation.
		Matrix matrix = getMatrix();
		//Convert matrix to reduced row echelon form.
		matrix.rref();
		//Solve for all variables in the matrix.
		double[] vars = matrix.solve();
		
		//Update the values of all variables.
		for(int i = 0; i < var.size(); i++) {
			var.get(i).updateValue(vars[i]);
		}
	}
	
	/**
	 * Converts the set of constraints to matrix form.
	 * @return the set of constraints, as a matrix.
	 */
	private Matrix getMatrix() {
		
		//The size of the matrix.
		int width = var.size() + 1, height = constr.size();
		
		//Array representation of the matrix.
		double[][] matrix = new double[var.size() + 1][constr.size()];
		
		//Create a row for each constraint.
		for(int row = 0; row < height; row++) {
			//Create an entry in the row for each variable.
			for(int column = 0; column < width - 1; column++) {
				
				//Put the variable coefficient in the matrix.
				matrix[column][row] = constr.get(row)
						.getCoefficient(var.get(column));
			}
			//Put the target sums from each constraint in the last column.
			matrix[width - 1][row] = constr.get(row).getSum();
		}
		return new Matrix(matrix);
	}
	
	/**
	 * @return the list of constraints associated with this solver.
	 */
	List<Constraint> getConstraints() { return constr; }
	
	/**
	 * @return the list of variables associated with this solve.
	 */
	List<Variable> getVariables() { return var; }
}