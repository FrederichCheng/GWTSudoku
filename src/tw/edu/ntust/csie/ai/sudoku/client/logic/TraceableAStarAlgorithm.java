package tw.edu.ntust.csie.ai.sudoku.client.logic;

import java.util.List;
import java.util.Stack;

import tw.edu.ntust.csie.ai.sudoku.client.data.Cell;
import tw.edu.ntust.csie.ai.sudoku.client.data.FreeCell;
import tw.edu.ntust.csie.ai.sudoku.client.data.SudokuBoard;

/**
 * 可逐步執行的深度優先搜尋演算法。
 * 此類別可以用{@linkplain Algorithm#solve(SudokuBoard)}來一次解題，
 * 也可以使用{@linkplain Traceable#newTraceInstance(SudokuBoard)}來取得{@link AlgorithmTrace}物件，以逐步執行。
 * 
 * */
public class TraceableAStarAlgorithm extends AbstractAlgorithm implements Traceable{
	
	/**
	 * 藉由{@link AlgorithmTrace} 物件，以迴圈將所有步驟執行，解決盤面。
	 * 此方法實作上與{@link DFSAlgorithm}稍有不同，效率上稍差。
	 * 
	 * @param board 數獨盤面
	 * @return 是否已找到解答。(若{@link #findOnlyOneSolution()}為 true，則永遠回傳false。)
	 */
	@Override
	public List<int[][]> solve(SudokuBoard board) {
		solutions.clear();
		AStarTrace trace = new AStarTrace(board);
		while(trace.hasNextStep())
			trace.nextStep();
		Cell[][] cells = board.getCells();
		storeSolution(cells);
		return solutions;
	}
	
	@Override
	public boolean findOnlyOneSolution(){
		return true;
	}
	
	@Override
	public AlgorithmTrace newTraceInstance(SudokuBoard board) {
		return new AStarTrace(board);
	}

	/**
	 * 提供深度優先搜尋演算法逐步執行功能的類別。
	 * */
	private class AStarTrace implements AlgorithmTrace{
		/**儲存已加入closeList的名單*/
		private boolean[] closeList;
		/**儲存每個freeCell的Heuristics function*/
		private int[]  Hfunction;
		/**儲存從第幾個答案開始嘗試*/
		private int[] answer;
		/** 儲存每次 forward 與 backtrack時，從第幾個答案開始嘗試的堆疊，以取代遞迴呼叫的 Stack frame*/
		private Stack<Integer> sequence = new Stack<Integer>();
		/** 儲存目前搜尋到的空格索引 */
		private int currentIndex;
		/** 儲存盤面的陣列 */
		private Cell[][] cells;
		/** 儲存空格的陣列 */
		private FreeCell[] freeCells;
		/** 是否已完成 */
		private boolean complete;
		
		/**
		 * 以數獨盤面建構逐步執行物件。
		 * @param board 數獨盤面
		 * */
		AStarTrace(SudokuBoard board){
			
			this.cells = board.getCells();
			this.freeCells = board.getFreeCells();
			closeList = new boolean[freeCells.length];
			Hfunction = new int[freeCells.length];
			answer = new int[freeCells.length];
			//sequence.push(0);	//將第一個Index存入stack
			//answers.push(0);			//先將第一步驟開始嘗試的答案索引放入堆疊
			
			for(int i = 0; i < freeCells.length; i++){
				closeList[i] = false;
				Hfunction[i] = -1;
				answer[i] = 0;
			}
		}
		
		@Override
		public boolean hasNextStep() {
			return !complete;
		}

		@Override
		public int nextStep() {
		 	FreeCell cell = freeCells[currentIndex];	//取得目前要解決的空格
		 	closeList[currentIndex] = true;
		 	SudokuBoard.constructPossibleAnswers(cell, cells);			//建立可能的解答
	    	boolean[] possibleSolutions = cell.getPossibleAnswers();
	    	
	    	/** 若無可能解答，則判斷是無解還是需要backtrack。*/
	    	if(possibleSolutions.length == 0){
	    		/** 若目前空格子不為第一個，則backtrack */
	    		if(currentIndex > 0){
	    			//answers.pop();		// 將嘗試的答案索引消除
	    			closeList[currentIndex] = false;
	        		answer[currentIndex] = 0;
	    			return backtrack(cell);
	    		}
	    		else{
	    			complete(cell);
	    		}
	    	}
	    	
	    	/** 若該格子為最後一個格子，則找到解答 */
	    	if(IsAllInCloseList()){
				/** 找出最後空格的解答 */
	    		for(int i = answer[currentIndex]; i < possibleSolutions.length; i++){
	    			if(possibleSolutions[i]){
	    				cell.setAnswer(i+1);
	    				break;
	    			}
	    		}

	    		notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.SOLUTION_FOUND);
	    		return complete(cell);
	    	}
	    	
	    	/** 嘗試該空格所有可能的解。 */
	    	for(int i = answer[currentIndex]; i < possibleSolutions.length; i++){
		    	if(possibleSolutions[i]){
			    	cell.setAnswer(i+1);		// 填入可能的解
			    	answer[currentIndex] = (i+1);
			    	//answers.push(i+1);		// 若回溯到本空格，則從第 i+1個答案開始嘗試
			    	//answers.push(0);			// 設定下一個空格從第0個答案開始嘗試
			    	return forward(cell);
		    	}
	    	}
	    	
	    	/**所有答案嘗試完皆不符合 */
	    	
	    	/** 若非第一個空格，則backtrack */
	    	if(currentIndex > 0){
	    		closeList[currentIndex] = false;
	    		answer[currentIndex] = 0;
	    		return backtrack(cell);
	    	}
	    	else{
	    		return complete(cell);
	    	}
		}
		
		/** 嘗試下一格 */
		private int forward(FreeCell cell){
	    	notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.FORWARD);
	    	//back = currentIndex;
	    	sequence.push(currentIndex);
	    	currentIndex = findNextCell();		// 將要嘗試的空格索引遞增
	    	//sequence.push(currentIndex);
	    	return  Algorithm.FORWARD;
		}
		
		/** 回溯 */
		private int backtrack(FreeCell cell){
	    	cell.eraseAnswer();		// 還原該空格
    		notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.BACKTRACK);
    		//currentIndex = back;
    		currentIndex = sequence.pop();
    		//System.out.println(currentIndex);
    		return Algorithm.BACKTRACK;
		}
		
		/** 完成 */
		private int complete(FreeCell cell){
    		notifyListeners(cells, cell.getRow(), cell.getColumn(), Algorithm.COMPLETE);
    		complete = true;
    		return Algorithm.COMPLETE;	    		
		}
		
		private boolean IsAllInCloseList(){
			for(boolean c:closeList)
				if(!c)
					return false;
			
			return true;	
		}
		
		private int findNextCell() {
			int next = currentIndex;
			//flush
			for(int f = 0; f < freeCells.length; f++){
				SudokuBoard.constructPossibleAnswers(freeCells[f], cells);	//建立可能的解答。
		    	boolean[] possible = freeCells[f].getPossibleAnswers();
		    	
				//算有幾個可能答案
				int count = 0;
				for(int a = 0; a < possible.length; a++)
					if(possible[a])
						count++;
				
				//判別是否要更新open list 節點的H
				if(Hfunction[f] > count || (Hfunction[f] == -1)){
					Hfunction[f] = count;
				}					
			}		
			for(int i = 0; i < closeList.length; i++){
				if(closeList[next] && !closeList[i])
					next = i;
				if(!closeList[i]){
					if(Hfunction[i] < Hfunction[next])
						next = i;
				}
			}	
			return next;
		}
	}
}
