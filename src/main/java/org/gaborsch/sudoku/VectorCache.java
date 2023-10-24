package org.gaborsch.sudoku;

public class VectorCache {
	
	private static VectorProvider fetchByMapping = new VectorProvider();
	private static VectorProvider drawSummary = new VectorProvider();
	private static MatrixProvider floatingsByRowAndBox = new MatrixProvider();
	private static MatrixProvider floatingsByColAndBox = new MatrixProvider();

	private static VectorProvider checkExclusion = new VectorProvider();
	private static VectorProvider partitionFloatings = new VectorProvider();
	private static VectorProvider partitionPositionsCleared = new VectorProvider();
	
	public static int[] fetchByMappingVector(int size) {
		return fetchByMapping.get(size);
	}
	
	public static int[] drawSummaryVector() {
		return drawSummary.get(10);
	}

	public static int[][] floatingsByRowAndBoxMatrix() {
		return floatingsByRowAndBox.get(9,9);
	}

	public static int[][] floatingsByColAndBoxMatrix() {
		return floatingsByColAndBox.get(9,9);
	}
	
	public static int[] checkExclusionVector() {
		return checkExclusion.get(3);
	}

	public static int[] partitionFloatingsVector(int size) {
		return partitionFloatings.get(size);
	}
	
	public static int[] partitionPositionsClearedVector(int size) {
		return partitionPositionsCleared.get(size);
	}
	
	
	private static class VectorProvider {
		private int[] vector;
		private int size = -1;
		
		public int[] get(int size) {
			if (this.size < 0) {
				this.size = size;
				this.vector = new int[size];
			} else if (this.size != size) {
				throw new RuntimeException("illegal vector size, asked "+size+", have "+this.size);
			}
			for (int i = 0; i < vector.length; i++) {
				vector[i] = 0;
			}
			return this.vector;
		}
	}

	private static class MatrixProvider {
		private int[][] matrix;
		private int size1 = -1;
		private int size2;
		
		public int[][] get(int size1, int size2) {
			if (this.size1 < 0) {
				this.size1 = size1;
				this.size2 = size2;
				this.matrix = new int[size1][size2];
			} else if (this.size1 != size1 || this.size2 != size2) {
				throw new RuntimeException("illegal matrix size, asked "+size1+"x"+size2+", have "+this.size1+"x"+this.size2);
			}
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					matrix[i][j]=0;
				}
			}
			return this.matrix;
		}
	}

}
