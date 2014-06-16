package tw.edu.ntust.csie.ai.sudoku.client.data;

public class FreeCell extends Cell {
	/** 有可能的數字陣列，possibleAnswers[i]為 true表示 i+1是可能為答案的數字 */
	private boolean[] possibleAnswers = new boolean[SudokuBoard.BOARD_BOUND];
	
	/**
	 * 以行、列建構類別。
	 * @param row 行
	 * @param column 列
	 * */
	public FreeCell(int row, int column){
		super(row, column, 0);
	}

	/**
	 * 設定有可能的數字。
	 * @param sol 數字，不小於1，不超過 {@link SudokuBoard#BOARD_BOUND}。 
	 * */
	public void setPossibleAnswer(int sol){
		possibleAnswers[sol-1] = true;
	}
	
	/**
	 * 移除有可能的數字。
	 * @param sol 數字，不小於1，不超過 {@link SudokuBoard#BOARD_BOUND}。
	 * */
	public void removePossibleAnswer(int sol){
		possibleAnswers[sol-1] = false;
	}
	
	/**
	 * 取得可能的答案陣列。
	 * @return 可能的答案陣列，若array[i] 為 true，代表 i+1為有可能的答案。
	 * */
	public boolean[] getPossibleAnswers(){
		return possibleAnswers;
	}
	
	
	/**
	 * 設定答案。
	 * @param sol 答案
	 * */
	public void setAnswer(int sol){
		if(sol < 1)
			throw new IllegalArgumentException("答案不可小於0。");
		this.answer = sol;
	}
	
	/**
	 * 取得可能的答案的數量。
	 * @return 可能的答案的數量。
	 * */
	public int getNumberOfPossibleAnswers() {
		int numOfPossibleAnswers=0;
		for(int i=0;i<possibleAnswers.length;i++)
			if(possibleAnswers[i]==true)
				numOfPossibleAnswers=numOfPossibleAnswers+1;
		return numOfPossibleAnswers;
	} 
	/**
	 * 移除該格的答案。
	 * */
	public void eraseAnswer(){
		answer = 0;
	} 
	
	public boolean equals(Object o){
		if(!(o instanceof FreeCell))
			return false;
		FreeCell cell = (FreeCell)o;
		if(row == cell.getRow() && column == cell.getColumn() && answer == cell.getAnswer())
			return true;
		return false;
	}
	
}
