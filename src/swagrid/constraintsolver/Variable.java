package swagrid.constraintsolver;

import java.util.function.Consumer;

/**
 * Variable type for Swagrid's linear constraint solver.
 * Represents a value for which the satisfier should solve.
 * Use in conjunction with Constraint objects to develop relations between variables.
 * The solver will automatically keep variables in compliance with relevant constraints.
 * 
 * @author Alec Dorrington
 */
public class Variable {
    
    /** Tolerance for considering values to be equal. */
    private static final double TOLERANCE = 0.000001;
    
    /** The total number of nameless variables created. */
    private static int varNum = 0;
    
    /** The Solver to which this variable belongs. */
    private Solver solver;
    
    /** Human-recognizable name of this variable. */
    private String name;
    
    /** The current value of this variable. */
    private double value = 0.0;
    
    /** Constraint for locking the value of this variable. */
    private Constraint lock;
    
    /* Function to be called when the value of this variable is modified. */
    private Consumer<Double> updateFunction;
    
    /**
     * Construct a new Variable object under the given Solver.
     * @param solver the Solver to which this variable belongs.
     */
    public Variable(Solver solver) {
        this(solver, "var" + varNum++, 0.0);
    }
    
    /**
     * Construct a new Variable object under the given Solver and name.
     * @param solver the Solver to which this variable belongs.
     * @param name human-recognizable name of this variable.
     */
    public Variable(Solver solver, String name) {
        this(solver, name, 0.0);
    }
    
    /**
     * Construct a new Variable object under the given Solver, name and initial value.
     * @param solver the Solver to which this variable belongs.
     * @param name human-recognizable name of this variable.
     * @param value the initial value assigned to this variable.
     */
    public Variable(Solver solver, String name, double value) {
        this.name = name;
        this.value = value;
        this.solver = solver;
        solver.getVariables().add(this);
    }
    
    /**
     * @return the human-recognizable name of this variable.
     */
    public String getName() { return name; }
    
    /**
     * @return the current value of this variable.
     */
    public double getValue() { return value; }
    
    /**
     * Changes the value of this variable.
     * Will modify other variables as necessary to accommodate any constraints,
     * IFF auto-solve is enabled.
     * Does not permanently lock the variable to this value (see lock()).
     * This may still be called if the variable is locked, and will not unlock the variable.
     * @param value the new value.
     * @return this variable instance.
     */
    public Variable setValue(double value) {
        
         //Update the value.
        double oldValue = this.value;
        this.value = value;
        
        //If the new value is different from the original.
        if(Math.abs(value - oldValue) > TOLERANCE) {
            
            //Remove the lock constraint if there was one.
            boolean locked = lock != null;
            unlock();
            
            //Lock the value while the ConstraintSet is solved.
            lock();
            if(solver.isAutoSolveEnabled()) solver.solve();
            //If the variable was originally unlocked, make it so again.
            if(!locked) unlock();
            
            //Call the update function.
            if(updateFunction != null) updateFunction.accept(value);
        }
        return this;
    }
    
    /**
     * Locks the value of this constraint to the current value,
     * preventing it from being changed by other constraints.
     * This still allows the value to be changed manually using setValue().
     * Can be undone by use of unlock().
     * Equivalent to creating a Constraint object on only this variable.
     * All variables are unlocked by default.
     * @return this variable instance.
     */
    public Variable lock() {
        if(lock == null) {
            //Create a new lock constraint.
            double value = this.value;
            lock = Constraint.create(solver).var(this, 1.0).sum(value).build();
        }
        return this;
    }
    
    /**
     * Unlocks the value of this constraint,
     * allowing it to be changed by other constraints.
     * All variables are unlocked by default.
     * @return this variable instance.
     */
    public Variable unlock() {
        if(lock != null) {
            //Delete the lock constraint.
            lock.delete();
            lock = null;
        }
        return this;
    }
    
    /**
     * Defines a behavior for when the value of this variable is modified.
     * The given function is called whenever the value is changed,
     * which does not include calls to setValue() which don't actually change the value.
     * Only one behavior may be provided at a time for each variable.
     * Accepts functions which take a double parameter, which is the new value.
     * @param updateFunction the function to be called when the value it modified.
     * @return this variable instance.
     */
    public Variable onUpdate(Consumer<Double> updateFunction) {
        this.updateFunction = updateFunction;
        return this;
    }
    
    /**
     * Changes the value of this variable,
     * without attempting to re-solve for other variables.
     * Will not trigger the update function.
     * Not intended for use in public API.
     * @param value the new value.
     * @return whether the new value was different to the original.
     */
    boolean updateValue(double value) {
        //Determine whether the new value is different to the original.
        boolean different = Math.abs(value - this.value) > TOLERANCE;
        //Update the value.
        this.value = value;
        return different;
    }
    
    /**
     * @return the update function associated with this variable.
     */
    Consumer<Double> getUpdateFunction() { return updateFunction; }
    
    @Override
    public String toString() { return name + " = " + value; }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Variable)) return false;
        return Math.abs(((Variable) o).value - value) < TOLERANCE;
    }
}