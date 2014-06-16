package tw.edu.ntust.csie.ai.sudoku.client.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import tw.edu.ntust.csie.ai.sudoku.client.data.Cell;
import tw.edu.ntust.csie.ai.sudoku.client.data.FreeCell;
import tw.edu.ntust.csie.ai.sudoku.client.data.SudokuBoard;

/**
 * 爬山法之數獨實作，先找一組解。
 * */

public class HillClimbingAlgorithm extends AbstractAlgorithm{

	/** 是否只找一組解*/
	private boolean onlyOneSolution;

	/**
	 * 能決定該演算法是否只找一組解的建構子。
	 * @param 是否只找一組解
	 * */
	public HillClimbingAlgorithm(boolean onlySolution){
		this.onlyOneSolution = onlySolution;
	}
	
	/**
	 * 預設只找一組解的建構子。
	 * */
	public HillClimbingAlgorithm(){
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
	private List<Cell[][]> succesorFunction(Cell[][] currentCells){
		List<Cell[][]> newStates = new ArrayList<Cell[][]>();
		for(int row=0;row<SudokuBoard.BOARD_BOUND;row++){
			for(int column=0;column<SudokuBoard.BOARD_BOUND-1;column++){
				if(currentCells[row][column] instanceof FreeCell){
					for(int k=column+1;k<SudokuBoard.BOARD_BOUND;k++){
						if(currentCells[row][k] instanceof FreeCell){
							// 複製現在這個currentCells
							Cell[][] newStateCell = new Cell[SudokuBoard.BOARD_BOUND][SudokuBoard.BOARD_BOUND];
							for(int rw = 0; rw < newStateCell.length; rw++){
								for(int cl = 0; cl < newStateCell[rw].length ; cl++){
										if(currentCells[rw][cl] instanceof FreeCell){	//若該格為0
											FreeCell freeCell =  new FreeCell(rw,cl);
											freeCell.setAnswer(currentCells[rw][cl].getAnswer());
											newStateCell[rw][cl] = freeCell;
											//freeCells.add(freeCell);	//加入空格List
										}
										else{
											newStateCell[rw][cl] = new Cell(rw,cl,currentCells[rw][cl].getAnswer());
										}
								}
							}
							// 交換一次空格就將新狀態新增到newStates裡
							FreeCell c1 = (FreeCell)newStateCell[row][column];
							FreeCell c2= (FreeCell)newStateCell[row][k];
							int temp = c1.getAnswer();
							c1.setAnswer(newStateCell[row][k].getAnswer());
							c2.setAnswer(temp);

					        newStates.add(newStateCell);
							//break;
						}
					}
				}
			}
		}
		return newStates;
	}
	/**
	 * 。
	 * @param freeCells 空格子陣列
	 * @param cells 盤面二維陣列
	 * @param index 要解決的空格子陣列索引值
	 * 
	 * @return 是否已找到解答。(若{@link #findOnlyOneAnswer()}為 true，則永遠回傳false。)
	 * */
	private boolean solve(FreeCell[] freeCells,Cell[][] cells, int index){
		// 設定合理的重新啟動次數
		final int numberOfRandomRestart = 100;
		Cell[][] bestHeuristicsValueCells = null;
		float bestHeuristicsValue = Float.NEGATIVE_INFINITY;
		for(int restartCount=0;restartCount<numberOfRandomRestart;restartCount++){
		//do{
			// 建造盤面起始狀態，每次重新啟動後都不一樣
			Cell[][] startCells = cells;
			for(int row=0;row<SudokuBoard.BOARD_BOUND;row++){
				// 對每個空格計算候選答案
				for(int j=0;j<freeCells.length;j++){
					SudokuBoard.constructPossibleAnswers(freeCells[j],cells);
				}
				
				LinkedList<Integer> availableNumbersInRow = new LinkedList<Integer>();
				// 產生數字1到9並放到availableNumbersInRow中
				for(int j=1;j<10;j++){
					availableNumbersInRow.add(Integer.valueOf(j));
				}
				// 弄亂順序
				Random generator = new Random();
				for(int j=0;j< generator.nextInt(12);j++){
					int indexA = generator.nextInt(9);
					int indexB = generator.nextInt(9);
					Integer A = availableNumbersInRow.get(indexA);
					Integer B = availableNumbersInRow.get(indexB);
					if(A==B)
						continue;
					Integer temp = A;
					availableNumbersInRow.set(indexA, B);
					availableNumbersInRow.set(indexB, temp);
				}
				// 從availableNumbersInRow中刪掉一列中非空格格子的數字
				for(int column=0;column<SudokuBoard.BOARD_BOUND;column++){
					if(!(startCells[row][column] instanceof FreeCell)){			
						availableNumbersInRow.remove(Integer.valueOf(startCells[row][column].getAnswer()));
					}
				}
				// 將availableNumbersInRow中剩下數字填入該列空格中
				for(int column=0;column<SudokuBoard.BOARD_BOUND;column++){
					
					if(startCells[row][column] instanceof FreeCell){
						// 取出空格的可能解答，放到possibleSolutions中
						boolean[] pas = ((FreeCell)(startCells[row][column])).getPossibleAnswers();
						LinkedList<Integer> possibleSolutions = new LinkedList<Integer>();
						for(int i=0;i<pas.length;i++ ){
							if(pas[i]){
								possibleSolutions.add(i+1);
							}
						}
						// 從possibleSolutions中隨機選出一個並填入startCells
						int possibleSolutionsLength = possibleSolutions.size();
						if(possibleSolutionsLength>0){
							int indexPossibleSolution = generator.nextInt(possibleSolutionsLength);
							Integer possibleAnswer = possibleSolutions.get(indexPossibleSolution);
							if(availableNumbersInRow.contains(possibleAnswer)){
								FreeCell c = (FreeCell)startCells[row][column];
								c.setAnswer(possibleAnswer);
								availableNumbersInRow.remove(possibleAnswer);
							}
						}
						// 再次檢查startCells有無填入猜測答案
						if(!startCells[row][column].isSolved()){
							FreeCell c = (FreeCell)startCells[row][column];
							c.setAnswer(availableNumbersInRow.removeLast());
						}
						//Window.alert(row+","+column+"="+startCells[row][column].getAnswer());
					}
				}
			}
			
			Cell[][] currentCells = startCells;
			float currentHeuristicValue = evaluateHeuristic(startCells);
			
			for(;;){
				if(currentHeuristicValue==0){
					notifyListeners(currentCells, 0, 0, Algorithm.SOLUTION_FOUND);
					break;
				}
				List<Cell[][]> newStates = succesorFunction(currentCells);
				if(newStates.isEmpty()){
					if(currentHeuristicValue==0)
						notifyListeners(currentCells, 0, 0, Algorithm.SOLUTION_FOUND);
					else
						notifyListeners(currentCells, 0, 0, Algorithm.COMPLETE);
					break;
				}
				Iterator<Cell[][]> newStatesItr = newStates.iterator();
				float bestNewHeuristicValue = Float.NEGATIVE_INFINITY;
				Cell[][] bestNewState=null;
				float currentNewStateHeuristicValue;
				Cell[][] currentNewState=null;
				while(newStatesItr.hasNext()){
			    	currentNewState = newStatesItr.next();
			    	currentNewStateHeuristicValue = evaluateHeuristic(currentNewState);
			    	if(currentNewStateHeuristicValue >= bestNewHeuristicValue){
			    		bestNewHeuristicValue = currentNewStateHeuristicValue;
			    		bestNewState = currentNewState;
			    	}
			    }
			    // 沒有更好解，結束
			    if(bestNewHeuristicValue <= currentHeuristicValue){
			    	notifyListeners(currentCells, 0, 0, Algorithm.COMPLETE);
			    	break;
			    }
			    // 將更好的狀態更新成現在狀態
			    currentHeuristicValue=bestNewHeuristicValue;
			    currentCells=bestNewState;
			    notifyListeners(currentCells, 0, 0, Algorithm.FORWARD);
			}
			
			if(currentHeuristicValue > bestHeuristicsValue){
				bestHeuristicsValue = currentHeuristicValue;
				bestHeuristicsValueCells = currentCells;
			}
			if(bestHeuristicsValue==0){
				notifyListeners(bestHeuristicsValueCells, 0, 0, Algorithm.SOLUTION_FOUND);
				break;
			}
		//}while(bestHeuristicsValue<-5);
		}
		//String blah="";
		for(int i=0;i<freeCells.length;i++){
			int row = freeCells[i].getRow();
			int col = freeCells[i].getColumn();
			freeCells[i].setAnswer(bestHeuristicsValueCells[row][col].getAnswer());
			//blah = blah + "\n"+row +","+ col +"="+ freeCells[i].getAnswer();
		}
		//storeSolution(currentCells);
		return true;
	}
	/**
	 * 加總盤面中每一欄、每一宮格中的衝突數目
	 * @param cells 待計算的盤面狀態
	 * 
	 * @return 傳回經驗值
	 * */
	private int evaluateHeuristic(Cell[][] cells){
		int heuristicValue=0;
		for(int row=0;row<SudokuBoard.BOARD_BOUND;row++){
			for(int column=0;column<SudokuBoard.BOARD_BOUND-1;column++){
				for(int i=column+1;i<SudokuBoard.BOARD_BOUND;i++){
					// 加總同列衝突數
					if(cells[row][column].getAnswer()==cells[row][i].getAnswer())
						heuristicValue = heuristicValue + 1;
					// 加總同宮格衝突數
					if(cells[3*(row/3)+column/3][column%3+3*(row%3)].getAnswer()==cells[3*(row/3)+i/3][i%3+3*(row%3)].getAnswer())
						heuristicValue = heuristicValue + 1;
				}
			}
		}
		
		return -heuristicValue;
	}
	private int evaluateHeuristic(Cell[][] allCells, FreeCell[] freeCells){
		int heuristicValue=0;
		// 取得盤面所有空格候選答案的數量
		for(int i=1;i<freeCells.length;i++){
			SudokuBoard.constructPossibleAnswers(freeCells[i], allCells);	//為每個空格建立可能解答。
			heuristicValue=heuristicValue+freeCells[i].getNumberOfPossibleAnswers();
		}
		return -heuristicValue;
	}
   
}

