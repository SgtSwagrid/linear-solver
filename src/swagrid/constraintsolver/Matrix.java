package swagrid.constraintsolver;

class Matrix {
	
	private static final double TOLERANCE = 0.0000001;
	
	private double[][] matrix;
	
	Matrix(double[][] matrix) {
		this.matrix = matrix;
	}
	
	double[] solve() {
		
		int width = matrix.length, height = matrix[0].length;
		
		double[] vars = new double[width];
		
		for(int row = height - 1; row >= 0; row--) {
			
			if(matrix[width - 1][row] == 0.0) continue;
			
			int leadColumn = 0;
			while(leadColumn < width && Math.abs(matrix[leadColumn][row])
					< TOLERANCE) leadColumn++;
			
			vars[leadColumn] = matrix[width - 1][row];
			
			for(int column = leadColumn + 1; column < width; column++) {
				vars[leadColumn] -= matrix[column][row] * vars[column];
			}
			vars[leadColumn] /= matrix[leadColumn][row];
		}
		return vars;
	}
	
	void rref() {
		
		int width = matrix.length, height = matrix[0].length;
		
		for(int leadRow = 0, leadColumn = 0; leadColumn < width
				&& leadRow < height; leadColumn++) {
			
			int pivotRow = getPivotRow(leadColumn, leadRow);
			
			if(Math.abs(matrix[leadColumn][pivotRow]) > TOLERANCE) {
				swapRows(leadRow, pivotRow);
				applyPivot(leadColumn, leadRow);
				reduceRow(leadColumn, leadRow);
				leadRow++;
			}
		}
	}
	
	private int getPivotRow(int leadColumn, int leadRow) {
		
		int height = matrix[0].length;
		
		double maxValue = 0.0;
		int maxRow = 0;
		
		for(int row = leadRow; row < height; row++) {
			
			double absValue = Math.abs(matrix[leadColumn][row]);
			if(absValue >= maxValue) {
				maxValue = absValue;
				maxRow = row;
			}
		}
		return maxRow;
	}
	
	private void swapRows(int row1, int row2) {
		
		int width = matrix.length;
		
		for(int column = 0; column < width; column++) {
			
			double swap = matrix[column][row1];
			matrix[column][row1] = matrix[column][row2];
			matrix[column][row2] = swap;
		}
	}
	
	private void applyPivot(int pivotColumn, int pivotRow) {
		
		int width = matrix.length, height = matrix[0].length;
		
		for(int row = pivotRow + 1; row < height; row++) {
			
			double multiplier = matrix[pivotColumn][row]
					/ matrix[pivotColumn][pivotRow];
			
			for(int column = pivotColumn; column < width; column++) {
				matrix[column][row] -= multiplier * matrix[column][pivotRow];
			}
		}
	}
	
	private void reduceRow(int leadColumn, int row) {
		
		int width = matrix.length;
		
		for(int column = width - 1; column >= leadColumn; column--) {
			matrix[column][row] /= matrix[leadColumn][row];
		}
	}
}