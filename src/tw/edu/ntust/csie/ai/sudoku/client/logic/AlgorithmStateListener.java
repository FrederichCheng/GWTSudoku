package tw.edu.ntust.csie.ai.sudoku.client.logic;

import tw.edu.ntust.csie.ai.sudoku.client.data.Cell;

/**
 * 觀察解題演算法的監聽器
 * */
public interface AlgorithmStateListener {
	/**
	 * 當解題演算法狀態改變時會被呼叫的方法。
	 * 
	 * @param cells 目前盤面
	 * @param row 行
	 * @param column 列
	 * @param state 演算法目前狀態
	 * */
	void stateChanged(Cell[][] cells, int row, int column, int state);
}
