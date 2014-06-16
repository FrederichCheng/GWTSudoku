package tw.edu.ntust.csie.ai.sudoku.client.logic;

import tw.edu.ntust.csie.ai.sudoku.client.data.SudokuBoard;

/**
 * 表示演算法有提供逐步執行功能的介面。
 * */
public interface Traceable {
	
	/**
	 * 取得逐步執行物件。
	 * @param board 數獨盤面
	 * @return 逐步執行物件。
	 * */
	AlgorithmTrace newTraceInstance(SudokuBoard board);
}
