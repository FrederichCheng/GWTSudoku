package tw.edu.ntust.csie.ai.sudoku.client.logic;

import java.util.List;

import tw.edu.ntust.csie.ai.sudoku.client.data.Cell;
import tw.edu.ntust.csie.ai.sudoku.client.data.FreeCell;
import tw.edu.ntust.csie.ai.sudoku.client.data.SudokuBoard;

/**
 * 以遞迴方式實做深度優先演算法，能找出數獨盤面的所有解或單一解。
 * */

public class DFSAlgorithm extends AbstractAlgorithm{

	/** 是否只找一組解*/
	private boolean onlyOneSolution;

	/**
	 * 能決定該演算法是否只找一組解的建構子。
	 * @param onlySolution 是否只找一組解
	 * */
	public DFSAlgorithm(boolean onlySolution){
		this.onlyOneSolution = onlySolution;
	}
	
	/**
	 * 預設只找一組解的建構子。
	 * */
	public DFSAlgorithm(){
		this(true);
	}
	
	/**
	 * 設定是否只找一組解。
	 * @param b 是否只找一組解
	 * */
	public void setOnlyOneSolution(boolean b){
		onlyOneSolution = b;
	}
	
	@Override
	public boolean findOnlyOneSolution(){
		return onlyOneSolution;
	}
	
	/**
	 * 解決盤面的驅動方法。
	 * @param board 數獨盤面
	 * @return 找到的解答。
	 * */
	@Override
	public List<int[][]> solve(SudokuBoard board) {
		solutions.clear();		// 將先前找到的解答清除
		FreeCell[] freeCells = board.getFreeCells();
		Cell[][] cells = board.getCells();
		FreeCell cell = freeCells[0];
		notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.INITIAL);		// 初始狀態
		solve(freeCells, cells, 0);
		cell = freeCells[freeCells.length-1];
		notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.COMPLETE);	// 結束狀態
		return solutions;
	  }
	
	/**
	 * 以遞迴解決盤面的遞迴方法。
	 * @param freeCells 空格子陣列
	 * @param cells 盤面二維陣列
	 * @param index 要解決的空格子陣列索引值
	 * 
	 * @return 是否已找到解答。(若{@link #findOnlyOneAnswer()}為 true，則永遠回傳false。)
	 * */
	private boolean solve(FreeCell[] freeCells,Cell[][] cells, int index){
    	FreeCell cell = freeCells[index];		//取得目前要解決的空格子
    	SudokuBoard.constructPossibleAnswers(cell, cells);	//建立可能的解答。
    	boolean[] possibleSolutions = cell.getPossibleAnswers();
    	/** 若無可能解答，則判斷是無解還是需要backtrack。*/
    	if(possibleSolutions.length == 0){
    		/** 若不是第一個空格，則backtrack */
    		if(index > 0){
    			notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.BACKTRACK);
    			return false;
    		}
    		else{
    			/** 完成但無解。 */
    			notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.COMPLETE);
    			return false;
    		}
    	}
    	/** 若該格子為最後一個格子，則找到解答 */
    	if(index == freeCells.length -1){
			/** 找出最後空格的解答 */
    		for(int i = 0; i < possibleSolutions.length; i++){
    			if(possibleSolutions[i]){
    				cell.setAnswer(i+1);
    				break;
    			}
    		}
    		storeSolution(cells);	//儲存解答
    		notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.SOLUTION_FOUND);
            if(!onlyOneSolution){
                cell.eraseAnswer();		//還原該格子為空格
            	notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.BACKTRACK);
            }
    		return onlyOneSolution;			// 若只找一組解，則搜尋會在此行回傳true後結束。
    	}
    	
    	/** 嘗試該空格所有可能的解。 */
    	for(int i = 0; i < possibleSolutions.length; i++){
	    	if(possibleSolutions[i]){
		    	cell.setAnswer(i+1);		// 填入可能的解
		    	notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.FORWARD);
		    	if(solve(freeCells, cells, index+1)){					// 嘗試填入下一個空格
		    		return true;		// 若只找一組解，其他可能的解就不需要嘗試。
		    	}
	    	}
    	}
    	cell.eraseAnswer();		// 還原該空格
    	if(index > 0)
    		notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.BACKTRACK);
    	return false;
	}
   
}
