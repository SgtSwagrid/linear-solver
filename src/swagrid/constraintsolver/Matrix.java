package swagrid.constraintsolver;

/**
 * Matrix type for Swagrid's linear constraint solver.
 * Represents a matrix backed by a 2D array of doubles.
 * Provides functionality for solving a set of simultaneous equations.
 * Not intended for use in public API.
 * 
 * @author Alec Dorrington
 */
class Matrix {
    
    /** Tolerance for considering values to be equal. */
<<<<<<< HEAD
    private static final double TOLERANCE = 0.000001;
=======
    private static final double TOLERANCE = 0.0000001;
>>>>>>> branch 'master' of https://github.com/SgtSwagrid/constraint-solver.git
    
    /** Array backing for this matrix. */
    private double[][] matrix;
    
    /** Number of columns and rows in this matrix. */
    private int width, height;
    
    /**
     * Construct a new matrix with the given array backing.
     * The array should be of the form [column][row].
     * @param matrix the array backing for this matrix.
     */
    Matrix(double[][] matrix) {
        this.matrix = matrix;
        width = matrix.length;
        height = matrix[0].length;
    }
    
    /**
     * Solves for variables implicit in this matrix as if
     * each row were an augmented-matrix-style equation.
     * Converts this matrix to reduced row echelon form in the process.
     * Values are returned in an order consistent with the columns of the matrix.
     * The matrix should in reduced row echelon form before this is called.
     * @return an array of solutions to the variables in this matrix.
     */
    double[] solve() {
        
        //Array of variables for which to solve.
        double[] vars = new double[width];
        
        //For each row in the matrix, starting at the bottom.
        for(int row = height - 1; row >= 0; row--) {
            
            //Skip rows that consist only of zeroes.
            if(allZeroes(row)) continue;
            
            //Determine the index of the leading value in this row.
            int leadColumn = 0;
            while(leadColumn < width && Math.abs(matrix[leadColumn][row])
                    < TOLERANCE) leadColumn++;
            
            //The value is initially equal to the sum value in the final column.
            vars[leadColumn] = matrix[width - 1][row];
            
            //Subtract the value of each previously calculated variable
            //from this value, multiplied by its matrix coefficient.
            for(int column = leadColumn + 1; column < width; column++) {
                vars[leadColumn] -= matrix[column][row] * vars[column];
            }
            //Divide the value by this variables own coefficient.
            vars[leadColumn] /= matrix[leadColumn][row];
        }
        return vars;
    }
    
    /**
     * Determines if there are no possible solutions due to over-constraining.
     * The matrix should be in reduced row echelon form first.
     * @return whether the matrix is over-constrained.
     */
    boolean isOverConstrained() {
        
        //Check each row for an impossible equation.
        for(int row = 0; row < height; row++) {
            
            //The matrix is over-constrained if there exists
            //a row will zeroes in all but the last column.
            if(Math.abs(matrix[width - 1][row]) > TOLERANCE && allZeroes(row)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines whether there are multiple solutions due to under-constraining.
     * The matrix should be in reduced row echelon form first.
     * @return whether the matrix is under-constrained.
     */
    boolean isUnderConstrained() {
        
        //Count the number of equations.
        int equations = 0;
        for(int row = 0; row < height; row++) {
            
            if(!allZeroes(row)) equations++;
        }
        //The matrix is under-constrained if there are less equations than variables.
        return equations < width - 1;
    }
    
    /**
     * Converts this matrix to reduced row echelon form.
     * Modifies the existing matrix.
     */
    void rref() {
        
        /* Iterate over each column, looking for a non-zero value below the current leadRow in each.
         * Should such a value be found, swap its row with the current leadRow, modify all subsequent
         * rows to produce a column of zeroes below the leadRow, then increment the leadRow. */
        for(int leadRow = 0, leadColumn = 0; leadColumn < width
                && leadRow < height; leadColumn++) {
            
            //Determine the row to swap with the current leadRow.
            int pivotRow = getPivotRow(leadColumn, leadRow);
            
            //If the pivotRow has a non-zero value in the leadColumn.
            if(Math.abs(matrix[leadColumn][pivotRow]) > TOLERANCE) {
                //Swap the lead and pivot rows.
                swapRows(leadRow, pivotRow);
                //Subtract multiples of the new lead row from each subsequent
                //row so as to produce all zeroes in the leadColumn.
                subtractPivot(leadColumn, leadRow);
                //Reduce the row such that the leading term is a one.
                reduceRow(leadColumn, leadRow);
                leadRow++;
            }
        }
    }
    
    /**
     * Determines the index of the pivot row.
     * That is, the row after leadRow with the largest absolute value in leadColumn.
     * @param leadColumn the column in which to search the largest value.
     * @param leadRow the after which to search for a pivot row.
     * @return the index of the pivot row.
     */
    private int getPivotRow(int leadColumn, int leadRow) {
        
        /* The pivot row is the optimal row for swapping with the current leadRow.
         * In theory, all that is needed is any row with a non-zero value in the leadColumn,
         * however choosing the largest value is optimal for floating-point accuracy. */
        
        double maxValue = 0.0;
        int maxRow = 0;
        
        //Search all the rows following the lead row.
        for(int row = leadRow; row < height; row++) {
            
            double absValue = Math.abs(matrix[leadColumn][row]);
            //Set this as the new pivot row if it has a larger value.
            if(absValue >= maxValue) {
                maxValue = absValue;
                maxRow = row;
            }
        }
        return maxRow;
    }
    
    /**
     * Swap the two rows of the given indices.
     * @param row1 the first row to swap.
     * @param row2 the second row to swap.
     */
    private void swapRows(int row1, int row2) {
        
        //For each column.
        for(int column = 0; column < width; column++) {
            
            //Swap the values in each row for this column.
            double swap = matrix[column][row1];
            matrix[column][row1] = matrix[column][row2];
            matrix[column][row2] = swap;
        }
    }
    
    /**
     * Subtract a multiple of the pivot row from each subsequent row,
     * such that a zero is formed in the leadColumn of each row.
     * @param pivotColumn the row in which to form zeroes.
     * @param pivotRow the row to be subtracted from subsequent rows.
     */
    private void subtractPivot(int pivotColumn, int pivotRow) {
        
        //For each subsequent row in the matrix.
        for(int row = pivotRow + 1; row < height; row++) {
            
            //The value by which to multiply the pivot row so as to form
            //a 0 in the lead column of this row when it is subtracted.
            double multiplier = matrix[pivotColumn][row]
                    / matrix[pivotColumn][pivotRow];
            
            //Subtract this multiple of the appropriate value
            //from the pivot row from each value in this row.
            for(int column = pivotColumn; column < width; column++) {
                matrix[column][row] -= multiplier * matrix[column][pivotRow];
            }
        }
    }
    
    /**
     * Reduce the row of the given index,
     * such that the leading term is a 1.
     * @param leadColumn the index of the column at which the row starts.
     * Assumes the values in previous columns are all 0.
     * @param row the index of the row to reduce.
     */
    private void reduceRow(int leadColumn, int row) {
        
        //Divide each value in the row by the leading term.
        for(int column = width - 1; column >= leadColumn; column--) {
            matrix[column][row] /= matrix[leadColumn][row];
        }
    }
    
    /**
     * Determines if a row contains only zeroes,
     * except for the last column, which can be anything.
     * @param row the row to check.
     * @return whether the row contains only zeroes.
     */
    private boolean allZeroes(int row) {
        
        boolean allZero = true;
        //For each value in the row, check if it is zero.
        for(int column = 0; column < width - 1; column++) {
            allZero &= Math.abs(matrix[column][row]) < TOLERANCE;
        }
        return allZero;
    }
}