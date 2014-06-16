package tw.edu.ntust.csie.ai.sudoku.client.logic;

/**
 * 表示演算法逐步執行狀態的介面。
 * */
public interface AlgorithmTrace {
	/**
	 * 回傳是否還有下一步。
	 * @return 是否還有下一步。
	 * */
	boolean hasNextStep();
	
	/**
	 * 執行下一步。
	 * @return 目前狀態。
	 * */
	int nextStep();
}
