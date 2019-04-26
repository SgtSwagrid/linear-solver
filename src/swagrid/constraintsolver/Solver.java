package swagrid.constraintsolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    
    /** Whether the solver being over-constrained throws an exception. */
    private boolean errorOnOverConstrained = true;
    
    /**
     * Manually triggers the solving for all variables once.
     * This need not be called provided auto-solve is enabled.
     */
    public void solve() {
        
        //Convert the list of constraints to matrix representation.
        Matrix matrix = getMatrix();
        
        //Convert the matrix to reduced row echelon form.
        matrix.rref();
        
        //Throw an error if the matrix is over-constrained.
        if(errorOnOverConstrained && matrix.isOverConstrained()) {
            throw new IllegalStateException(
                    "Solver is over-constrained and no solutions exist.");
        }
        
        //Solve for all variables in the matrix.
        double[] vars = matrix.solve();
        
        //Set of variables whose value has been changed.
        Set<Variable> changed = new HashSet<>();
        
        //Update the values of all variables.
        for(int i = 0; i < var.size(); i++) {
            if(var.get(i).updateValue(vars[i])) {
                changed.add(var.get(i));
            }
        }
        
        //Trigger the update function for each modified variable.
        changed.forEach(v -> v.getUpdateFunction().accept(v.getValue()));
    }
    
    /**
     * @return whether this solver is over-constrained such that no solutions exist.
     */
    public boolean isOverConstrained() {
        Matrix matrix = getMatrix();
        matrix.rref();
        return matrix.isOverConstrained();
    }
    
    /**
     * @return whether this solver is under-constrained such that multiple solutions exist.
     */
    public boolean isUnderConstrained() {
        Matrix matrix = getMatrix();
        matrix.rref();
        return matrix.isUnderConstrained();
    }
    
    /**
     * @param error whether an exception is thrown if the solver is over-constrained.
     */
    public void setErrorOnOverConstrained(boolean error) {
        errorOnOverConstrained = error;
    }
    
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