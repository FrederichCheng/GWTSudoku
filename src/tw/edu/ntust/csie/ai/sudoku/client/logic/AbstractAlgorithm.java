package tw.edu.ntust.csie.ai.sudoku.client.logic;

import java.util.LinkedList;
import java.util.List;

import tw.edu.ntust.csie.ai.sudoku.client.data.Cell;
import tw.edu.ntust.csie.ai.sudoku.client.data.SudokuBoard;

/**
 * 提供{@link Algorithm} 類別預設實作的抽象類別。
 * 此類別提供新增、移除監聽器的實作。
 * */
public abstract class AbstractAlgorithm implements Algorithm{
	
	/** 儲存所有解答的List*/
	protected List<int[][]> solutions = new LinkedList<int[][]>();
	
	/** 監聽器串列 */
	private List<AlgorithmStateListener> listeners = new LinkedList<AlgorithmStateListener>();
	
	public void addStateListener(AlgorithmStateListener listener){
		listeners.add(listener);
	}
	
	public void removeStateListener(AlgorithmStateListener listener){
		listeners.remove(listener);
	}

	/**
	 * 通知所有監聽器。
	 * 
	 * @param cells 目前盤面
	 * @param row 行
	 * @param column 列
	 * @param state 演算法目前狀態
	 * */
	protected void notifyListeners(Cell[][] cells, int row, int column, int state){
		for(AlgorithmStateListener listener : listeners){
			listener.stateChanged(cells, row, column, state);
		}
	}
	
	/**
	 * 儲存解答。
	 * @param cells 表示目前解答的盤面二維陣列。
	 * */
	protected void storeSolution(Cell[][] cells){
		/** 拷貝陣列。*/
		int[][] array = new int[SudokuBoard.BOARD_BOUND][SudokuBoard.BOARD_BOUND];
		for(int i = 0; i < SudokuBoard.BOARD_BOUND ; i++){
			for(int j = 0; j < SudokuBoard.BOARD_BOUND; j++){
				array[i][j] = cells[i][j].getAnswer();
			}
		}
        solutions.add(array);
	}
}
