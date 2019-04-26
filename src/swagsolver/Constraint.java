package swagsolver;

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
    private static final double TOLERANCE = 0.000001;
    
    /** The Solver to which this constraint belongs. */
    private Solver solver;
    
    /** A list of variables and their coefficients in this equation. */
    private Map<Variable, Double> terms = new LinkedHashMap<>();
    
    /** The value to which the terms must sum. */
    private double sum = 0.0;
    
    /**
     * Construct a new Constraint object under the given Solver.
     * @param solver the solver to which this constraint belongs.
     */
    private Constraint(Solver solver) {
        this.solver = solver;
    }
    
    /**
     * Creates a new ConstraintBuilder under the given solver.
     * Make a subsequent call to build() to finish creating the constraint,
     * following calls to var() and sum() to set up the constraint equation.
     * @param solver the solver to which this constraint belongs.
     * @return a ConstraintBuilder for creating a new constraint.
     */
    public static ConstraintBuilder create(Solver solver) {
        return new ConstraintBuilder(solver);
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
    
    /**
     * ConstraintBuilder type for use in creating a constraint.
     * Use var() and sum() to set up the constraint equation,
     * followed by build() to create the actual constraint.
     */
    public static class ConstraintBuilder {
        
        /** The constraint in the process of being built. */
        private Constraint constr;
        
        /**
         * Construct a new ConstraintBuilder object for
         * building a Constraint under the given solver.
         * @param solver solver to which the created constraint will belong.
         */
        private ConstraintBuilder(Solver solver) {
            constr = new Constraint(solver);
        }
        
        /**
         * Add a new variable (v) to the constraint equation, with a particular coefficient (c).
         * Constraints are of the form c1v1 + c2v2 + ... + cnvn = x,
         * where c1 ... cn, x are constants and v1 ... vn are variables.
         * If the variable has already been added, its coefficient will be replaced.
         * @param var the variable to add to the equation (v).
         * @param coefficient the coefficient of the variable (c).
         * @return this ConstraintBuilder instance.
         */
        public ConstraintBuilder var(Variable var, double coefficient) {
            constr.terms.put(var, coefficient);
            return this;
        }
        
        /**
         * Set the value to which the variables must sum (x).
         * Constraints are of the form c1v1 + c2v2 + ... + cnvn = x,
         * where c1 ... cn, x are constants and v1 ... vn are variables.
         * @param sum the value to which the variables must sum (x).
         * @return this ConstraintBuilder instance.
         */
        public ConstraintBuilder sum(double sum) {
            constr.sum = sum;
            return this;
        }
        
        /**
         * Create a Constraint object from this ConstraintBuilder.
         * This must be called if the constraint is to take effect.
         * Will automatically update relevant variables if auto-solve is enabled.
         * @return the relevant Constraint object.
         */
        public Constraint build() {
            constr.solver.getConstraints().add(constr);
            if(constr.solver.isAutoSolveEnabled()) constr.solver.solve();
            return constr;
        }
    }
}