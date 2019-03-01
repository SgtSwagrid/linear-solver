package swagrid.constraintsolver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Constraint type for Swagrid's linear constraint solver.
 * Represents a linear equation the solver is expected to uphold.
 * Constraints are of the form c1v1 + c2v2 + ... + cnvn = x,
 * where c1 ... cn, x are constants and v1 ... vn are variables.
 * Use in conjunction with Variable objects to develop relations between variables.
 * The solver will automatically keep variables in compliance with relevant constraints.
 * 
 * @author Alec Dorrington
 */
public class Constraint {
	
	/** Tolerance for considering values to be equal. */
	private static final double TOLERANCE = 0.0000001;
	
	/** The Solver to which this constraint belongs. */
	private Solver solver;
	
	/** A list of variables and their coefficients in this equation. */
	private Map<Variable, Double> terms = new LinkedHashMap<>();
	
	/** The value to which the terms must sum. */
	private double sum = 0.0;
	
	/**
	 * Construct a new Constraint object of the given Solver.
	 * @param solver the solver to which this constraint belongs.
	 */
	public Constraint(Solver solver) {
		this.solver = solver;
		solver.getConstraints().add(this);
	}
	
	/**
	 * Adds a new variable to this constraint equation.
	 * If the variable already exists, this method will ADD TO its coefficient.
	 * @param var the variable for which to update the coefficient.
	 * @param coefficient the amount to change the coefficient by.
	 * @return this constraint instance.
	 */
	public Constraint addVar(Variable var, double coefficient) {
		
		if(!containsVariable(var)) {
			//Create the variable if it doesn't exist.
			addVar(var, coefficient);
		} else {
			//Add to its coefficient if it does.
			terms.put(var, terms.get(var) + coefficient);
		}
		return this;
	}
	
	/**
	 * Adds a new variable to this constraint equation.
	 * If the variable already exists, this method will REPLACE its coefficient.
	 * @param var the variable for which to update the coefficient.
	 * @param coefficient the new coefficient for the variable.
	 * @return this constraint instance.
	 */
	public Constraint setVar(Variable var, double coefficient) {
		terms.put(var, coefficient);
		if(solver.isAutoSolveEnabled()) solver.solve();
		return this;
	}
	
	/**
	 * Removes the given variable from this constraint equation.
	 * Equivalent to setVariable(var, 0.0).
	 * @param var the variable to be removed.
	 * @return this constraint instance.
	 */
	public Constraint removeVar(Variable var) {
		terms.remove(var);
		return this;
	}
	
	/**
	 * @param sum the value to which the variables in this constraint are required to sum.
	 * @return this constraint instance.
	 */
	public Constraint setSum(double sum) {
		this.sum = sum;
		if(solver.isAutoSolveEnabled()) solver.solve();
		return this;
	}
	
	/**
	 * Determines the coefficient associated with the given variable in this constraint equation.
	 * Constraints are of the form c1v1 + c2v2 + ... + cnvn = x,
	 * where c1 ... cn, x are constants and v1 ... vn are variables.
	 * In other words, for a given vk, this method returns the associated ck.
	 * @param var the variable to test.
	 * @return the coefficient associated with var.
	 */
	public double getCoefficient(Variable var) {
		return terms.containsKey(var) ? terms.get(var) : 0.0;
	}
	
	/**
	 * Determines whether this constraint contains the given variable, or rather
	 * whether the variable has a non-zero coefficient in this constraint equation.
	 * @param var the variable to test.
	 * @return whether var is included in this constraint.
	 */
	public boolean containsVariable(Variable var) {
		return Math.abs(getCoefficient(var)) > TOLERANCE;
	}
	
	/**
	 * @return the value to which the variables in this constraint are required to sum.
	 */
	public double getSum() { return sum; }
	
	/**
	 * Delete this constraint.
	 * Does not update the solver, but will prevent satisfaction
	 * of this constraint to be required in the future.
	 */
	public void delete() { solver.getConstraints().remove(this); }
	
	@Override
	public String toString() {
		
		String str = "";
		for(Entry<Variable, Double> var : terms.entrySet()) {
			//Add each variable and coefficient to the string.
			str += var.getValue() + " * " + var.getKey().getName() + " + ";
		}
		//Add the sum constant to the string.
		str = str.substring(0, str.length() - 3) + " = " + sum;
		return str;
	}
}