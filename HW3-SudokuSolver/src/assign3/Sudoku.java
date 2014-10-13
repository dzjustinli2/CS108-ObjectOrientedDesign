package assign3;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	
	/* "Spot" inner class that represents a single spot
	 * on the grid of the Sudoku game.
	 */
	public class Spot implements Comparable<Spot> {
		
		/* Properties/fields of each individual Spot */
		private int row, col;
		private int value;
		private int part;
		
		/* Stores all possible values for this empty Spot if
		 * according to the rules of the game */
		private HashSet<Integer> possibleValues;
		
		Spot(int x, int y, int val) {
			row = x;
			col = y;
			value = val;
			part = getPart(x, y);
		
			possibleValues = new HashSet<>();
		}
		
		Spot(Spot s) {
			this(s.row, s.col, s.value);
			part = s.part;
			possibleValues = new HashSet<>(s.possibleValues);
		}
		
		/* Sets the value for this Spot */
		void setValue(int val) {
			value = val;
		}
		
		/* Returns the value of this Spot */
		int getValue() {
			return value;
		}
		
		/* Returns the part of the grid where this Spot belongs */
		int getPartForSpot() {
			return part;
		}
		
		/* Returns true iff this Spot is not filled */
		boolean isEmpty() {
			return value == 0;
		}
		
		/* Returns a HashSet of all legal values that can be 
		 * filled in this Spot.
		 */
		HashSet<Integer> getPossibleValues() {
			if (value != 0) return null;

			/* temporarily assign all 9 numbers */
			for (int i = 1; i <= Sudoku.SIZE; i++)
				possibleValues.add(i);
			
			/* Remove all the values that cannot be placed at this Spot */
			possibleValues.removeAll(valInRows.get(row));
			possibleValues.removeAll(valInCols.get(col));
			possibleValues.removeAll(valInParts.get(part));
			return possibleValues;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}
		
		@Override
		public int compareTo(Spot that) {
			return this.possibleValues.size() - that.possibleValues.size();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof Spot)) return false;
			
			Spot that = (Spot) o;
			return this.row == that.row && this.col == that.col;
		}
		
		@Override
		public int hashCode() {
			return possibleValues.size() * 25;
		}
		
		@Override
		public String toString() {
			return value + "";
		}
		
		/* Helper method that returns the Part in which the 
		 * coordinates x and y belong on the grid. 
		 */
		private int getPart(int x, int y) {
			if (x < 3) {
				if (y < 3) return PART1;
				else if (y < 6) return PART4;
				else return PART7;
			}
			if (x < 6) {
				if (y < 3) return PART2;
				else if (y < 6) return PART5;
				else return PART8;
			}
			else {
				if (y < 3) return PART3;
				else if (y < 6) return PART6;
				else return PART9;
			}	
		}
	}
	
	
	private Spot[][] puzzleGrid;
	private Spot[][] solutionGrid;
	
	/* List of all the solutions represented as an ArrayList of all
	 * solved Spots */
	private List<ArrayList<Spot>> solutions;
	
	/* The ivars to store the state of the grid.
	 * valInRows:- has a HashSet at each index that stores all the filled
	 * in values for that particular row
	 * valInCols:- Same as valInRows but for the columns
	 * valInParts:- For the 3x3 parts of the grid */
	private ArrayList<HashSet<Integer>> valInRows, valInCols, valInParts;
	
	/* Parts of the grid each of size 3x3. Counting from the
	 * top left to top right then the next row below.
	 * 0 1 2
	 * 3 4 5
	 * 6 7 8
	 */
	private static final int PART1 = 0;
	private static final int PART2 = 1;
	private static final int PART3 = 2;
	private static final int PART4 = 3;
	private static final int PART5 = 4;
	private static final int PART6 = 5;
	private static final int PART7 = 6;
	private static final int PART8 = 7;
	private static final int PART9 = 8;
		
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	public static final int[][] hardGrid2 = Sudoku.stringsToGrid(
			"3 0 0 0 0 0 0 8 0",
			"0 0 1 0 9 3 0 0 0",
			"0 4 0 7 8 0 0 0 3",
			"0 9 3 8 0 0 0 1 2",
			"0 0 0 0 4 0 0 0 0",
			"5 2 0 0 0 6 7 9 0",
			"6 0 0 0 2 1 0 4 0",
			"0 0 0 5 3 0 9 0 0",
			"0 3 0 0 0 0 0 5 1");

	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}


	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		puzzleGrid = new Spot[SIZE][SIZE];
		solutionGrid = new Spot[SIZE][SIZE];
		
		solutions = new ArrayList<>();
		
		valInRows = new ArrayList<HashSet<Integer>>(SIZE);
		valInCols = new ArrayList<HashSet<Integer>>(SIZE);
		valInParts = new ArrayList<HashSet<Integer>>(SIZE);
		
		/* Initializing all the HashSets */
		for (int i = 0; i < SIZE; i++) {
			valInRows.add(new HashSet<Integer>());
			valInCols.add(new HashSet<Integer>());
			valInParts.add(new HashSet<Integer>());
		}
		
		/* Setting up the Sudoku puzzle grid with the appropriate Spots.
		 * And adding all the values in the relevant Rows, Cols and Parts */
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				int val = ints[i][j];
				Spot newSpot = new Spot(i, j, val);
				puzzleGrid[i][j] = newSpot;
				
				if (!newSpot.isEmpty()) {
					valInRows.get(i).add(val);
					valInCols.get(j).add(val);
					valInParts.get(newSpot.getPartForSpot()).add(val);
				}
			}
		}
	}

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		ArrayList<Spot> emptySpots = getEmptySpotsList();
		ArrayList<Spot> solvedSpots = new ArrayList<>();
		
		solveSudoku(emptySpots, solvedSpots, 0);
		
		if (solutions.size() == 0)
			return 0;
		
		fillSolutionGrid();
		
		return solutions.size();
	}


	private void solveSudoku(ArrayList<Spot> emptySpots,
			ArrayList<Spot> solvedSpots, int index) {
		
		if (index >= emptySpots.size()) {
			solutions.add(new ArrayList<>(solvedSpots));
			return;
		}
		
		Spot currentSpot = new Spot(emptySpots.get(index));
		for (int value : currentSpot.possibleValues) {
			if (valueIsValid(value, currentSpot)) {
				currentSpot.setValue(value);
				updateGridStateWithValue(value, currentSpot);
				
				solvedSpots.add(currentSpot);
				
				int newIndex = index + 1;
				solveSudoku(emptySpots, solvedSpots, newIndex);
				
				/* Backtrack when the method above returns */
				emptySpots.get(index).setValue(0);
				solvedSpots.remove(currentSpot);
				updateGridStateWithValue(value, currentSpot);
			}
		}
	}

	
	private void fillSolutionGrid() {
		ArrayList<Spot> solvedSpots = solutions.get(0);
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				solutionGrid[i][j] = new Spot(puzzleGrid[i][j]);
			}
		}
		
		for (Spot thisSpot : solvedSpots)
			solutionGrid[thisSpot.getRow()][thisSpot.getCol()].value = thisSpot.value;
	}

	
	private boolean valueIsValid(int value, Spot currentSpot) {
		int row = currentSpot.getRow();
		int col = currentSpot.getCol();
		int part = currentSpot.getPartForSpot();
		
		return (!valInRows.get(row).contains(value)) &&
				(!valInCols.get(col).contains(value)) &&
				(!valInParts.get(part).contains(value));
	}


	private void updateGridStateWithValue(int value, Spot currentSpot) {
		HashSet<Integer> valsInCurrentRow = valInRows.get(currentSpot.getRow());
		HashSet<Integer> valsInCurrentCol = valInCols.get(currentSpot.getCol());
		HashSet<Integer> valsInCurrentPart = valInParts.get(currentSpot.getPartForSpot());
		
		if (valsInCurrentRow.contains(value))
			valsInCurrentRow.remove(value);
		else 
			valsInCurrentRow.add(value);
		
		if (valsInCurrentCol.contains(value))
			valsInCurrentCol.remove(value);
		else 
			valsInCurrentCol.add(value);
		
		if (valsInCurrentPart.contains(value))
			valsInCurrentPart.remove(value);
		else 
			valsInCurrentPart.add(value);
		
	}


	/* Helper method to compute the possible values for each empty spot and
	 * return the spots as an ArrayList sorted by the number of possible values
	 * from low to high.
	 */
	public ArrayList<Spot> getEmptySpotsList() {
		ArrayList<Spot> result = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				Spot thisSpot = puzzleGrid[i][j];
				if (thisSpot.isEmpty()) {
					thisSpot.getPossibleValues();
					result.add(thisSpot);
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public String getSolutionText() {
		if (solutions.size() == 0) return "No Solutions";
		String result = "";
		for (Spot[] sArr : solutionGrid) {
			result += "[";
			for (Spot s : sArr)
				result += s.toString() + ", ";
			result += "] \n";
		}
		return result;
	}
	
	public long getElapsed() {
		return 0; // YOUR CODE HERE
	}
	
	@Override
	public String toString() {
		String result = "";
		for (Spot[] sArr : puzzleGrid) {
			result += "[";
			for (Spot s : sArr)
				result += s.toString() + ", ";
			result += "] \n";
		}
		return result;
	}
	

	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}

}
