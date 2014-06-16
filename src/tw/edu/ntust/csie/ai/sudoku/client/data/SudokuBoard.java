package tw.edu.ntust.csie.ai.sudoku.client.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示數獨盤面的類別。
 * */
public class SudokuBoard {
	/** 盤面的長與寬，一般為9*/
	public final static int BOARD_BOUND = 9;
	/** 表示盤面的二維陣列 */
	private Cell[][] cells = new Cell[BOARD_BOUND][BOARD_BOUND];
	/** 儲存空格的List*/
	private List<FreeCell> freeCells = new ArrayList<FreeCell>(BOARD_BOUND*BOARD_BOUND/2);
	
	/**
	 * 以整數二維陣列建立盤面。 
	 * @param board 表示盤面的整數二維陣列，第一維為Row，第二維為Column( board[row][column] )。
	 *  */
	public SudokuBoard(int[][] board){
		for(int row = 0; row < board.length; row++){
			for(int col = 0; col < board[row].length ; col++){
					if(board[row][col] == 0){	//若該格為0
						FreeCell freeCell =  new FreeCell(row,col);
						cells[row][col] = freeCell;
						freeCells.add(freeCell);	//加入空格List
					}
					else{
						cells[row][col] = new Cell(row,col,board[row][col]);
					}
			}
		}
	}
	
	/**
	 * 以整數二維陣列建立盤面。 
	 * @param srcCells 表示盤面的Cell二維陣列，第一維為Row，第二維為Column( board[row][column] )。
	 *  */
	public SudokuBoard(Cell[][] srcCells){
		for(int row = 0; row < srcCells.length; row++){
			for(int col = 0; col < srcCells[row].length ; col++){
					if(srcCells[row][col].getAnswer() == 0){	//若該格為0
						FreeCell freeCell =  new FreeCell(row,col);
						cells[row][col] = freeCell;
						freeCells.add(freeCell);	//加入空格List
					}
					else{
						cells[row][col] = new Cell(row,col,srcCells[row][col].getAnswer());
					}
			}
		}
	}
	
	/**
	 * 傳回盤面二維陣列的副本。
	 * @return 表示盤面的{@link Cell}二維陣列。
	 * */
	public Cell[][] getCells(){
		Cell[][] copy = new Cell[cells.length][];
		for(int i = 0; i < cells.length; i++){
			copy[i] = new Cell[cells[i].length];
			for(int j = 0; j < cells[i].length ; j ++){
				copy[i][j] = cells[i][j];
			}
		}
		return copy;
	}
	
	/**
	 * 傳回空格陣列。
	 * @return 空格陣列。
	 * */
	public FreeCell[] getFreeCells(){
		FreeCell[] cells = new FreeCell[freeCells.size()];
		cells = freeCells.toArray(cells);
		return cells;
	}
	
	/**
	 * 建立單一空格的可能解答陣列。
	 * @param freeCell 要解決的空格
	 * @param cells 目前盤面的二維陣列
	 * */
	public static void constructPossibleAnswers(FreeCell freeCell, Cell[][] cells){
		int row = freeCell.getRow();
		int column = freeCell.getColumn();
		boolean[] array = freeCell.getPossibleAnswers();
		/** 先將所有數字都設為可能的解答 */
		for( int i = 0; i < SudokuBoard.BOARD_BOUND; i++ ){
			array[i] = true;
		}
		
		/** 將同一row與column和同block裡目前存在的數字過濾掉 */
		for( int i = 0; i < SudokuBoard.BOARD_BOUND; i++ ){
			Cell c1= cells[i][ column];	// 同一個column 的格子
			Cell c2 =  cells[row][ i];		//同一個row的格子
			Cell c3 = cells[ (row/3)*3 + i/3][(column/3)*3 + i%3 ];	// 同一個block的格子
			
			/** 若該格子已解決，則過濾掉格子內的答案。 */
			if(c1.isSolved())
				array[c1.getAnswer()-1] = false;
			if(c2.isSolved())
				array[c2.getAnswer()-1] = false;
			if(c3.isSolved())
				array[c3.getAnswer()-1] = false;
		}	
	}
	
	  /** Check whether grid[i][j] is valid in the grid */
	  public static boolean isValid(int i, int j, int[][] grid) {
	    // Check whether grid[i][j] is valid at the i's row
	    for (int column = 0; column < 9; column++)
	      if (column != j && grid[i][column] == grid[i][j])
	        return false;

	    // Check whether grid[i][j] is valid at the j's column
	    for (int row = 0; row < 9; row++)
	      if (row != i && grid[row][j] == grid[i][j])
	        return false;

	    // Check whether grid[i][j] is valid in the 3 by 3 box
	    for (int row = (i / 3) * 3; row < (i / 3) * 3 + 3; row++)
	      for (int col = (j / 3) * 3; col < (j / 3) * 3 + 3; col++)
	        if (row != i && col != j && grid[row][col] == grid[i][j])
	          return false;

	    return true; // The current value at grid[i][j] is valid
	  }

	  /** Check whether the fixed cells are valid in the grid */
	  public static boolean isValid(int[][] grid) {
	    for (int i = 0; i < 9; i++)
	      for (int j = 0; j < 9; j++)
	        if (grid[i][j] != 0 && !isValid(i, j, grid)) return false;

	    return true; // The fixed cells are valid
	  }
}
