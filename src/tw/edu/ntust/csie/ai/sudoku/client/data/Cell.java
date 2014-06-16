package tw.edu.ntust.csie.ai.sudoku.client.data;


/**
 * 表示數獨盤面的一個格子的類別。
 * Cell包含Row, Column與答案{@link Cell#getAnswer()}。
 * 若{@link Cell#getAnswer()}的值為0，表示答案未填，則{@link Cell#isSolved()}}為false，否則為true。
 * */

public class Cell {
	protected int row;
	protected int column;
	protected int answer;
	
	/**
	 * 以Row 、Column與答案  建立格子。
	 * @param row 行
	 * @param column 列
	 * @param answer 答案
	 * */
	public Cell(int row, int column,int answer){
		this.row = row;
		this.column = column;
		this.answer = answer;
	}

	/**
	 * 傳回答案。
	 * @return 答案。
	 * */
	public int getAnswer(){
		return answer;
	}
	
	/**
	 * 傳回答案是否已填，solution > 0則傳回true。
	 * @return 答案是否已填。
	 * */
	public boolean isSolved(){
		return answer > 0;
	}
	
	/**
	 * 傳回行。
	 * @return 行。
	 * */
	public int getRow(){
		return row;
	}
	
	/**
	 * 傳回列。
	 * @return  列。
	 * */
	public int getColumn(){
		return column;
	}
}
