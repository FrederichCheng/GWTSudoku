package tw.edu.ntust.csie.ai.sudoku.client.logic;

import java.util.List;

import tw.edu.ntust.csie.ai.sudoku.client.data.SudokuBoard;
/**
 * 表示解決數獨盤面演算法的界面。
 * */
public interface Algorithm {
	/** 初始狀態 */
	int INITIAL = 0;	
	/** 演算法完成狀態 */
	int COMPLETE = 1;
	/** 向前測試(相對於回溯)狀態 */
	int FORWARD = 2;
	/** 回溯狀態 */
	int BACKTRACK = 3;
	/** 找到一組解狀態 */
	int SOLUTION_FOUND = 4;
	
	/**
	 * 解決數獨盤面。
	 * @param board 數獨盤面
	 * @return 找到的解答。
	 * */
	List<int[][]> solve(SudokuBoard board);
	
	
	/**
	 * 此演算法是否只找一組解。
	 * @return 是否只找一組解。
	 * */
	public boolean findOnlyOneSolution();
	
	/**
	 * 新增狀態監聽器。
	 * @param listener 監聽器
	 * */
	void addStateListener(AlgorithmStateListener listener);
	
	/**
	 * 移除狀態監聽器。
	 * @param listener 監聽器
	 * */
	void removeStateListener(AlgorithmStateListener listener);
}
