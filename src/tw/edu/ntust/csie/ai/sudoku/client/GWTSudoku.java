package tw.edu.ntust.csie.ai.sudoku.client;

import java.util.HashMap;
import java.util.Set;

import tw.edu.ntust.csie.ai.sudoku.client.data.Cell;
import tw.edu.ntust.csie.ai.sudoku.client.data.FreeCell;
import tw.edu.ntust.csie.ai.sudoku.client.data.ProblemSet;
import tw.edu.ntust.csie.ai.sudoku.client.data.SudokuBoard;
import tw.edu.ntust.csie.ai.sudoku.client.logic.Algorithm;
import tw.edu.ntust.csie.ai.sudoku.client.logic.AlgorithmStateListener;
import tw.edu.ntust.csie.ai.sudoku.client.logic.AlgorithmTrace;
import tw.edu.ntust.csie.ai.sudoku.client.logic.HillClimbingAlgorithm;
import tw.edu.ntust.csie.ai.sudoku.client.logic.Traceable;
import tw.edu.ntust.csie.ai.sudoku.client.logic.TraceableAStarAlgorithm;
import tw.edu.ntust.csie.ai.sudoku.client.logic.TraceableDFSAlgorithm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTSudoku implements EntryPoint {
	Grid grid = new Grid(9,9);
	Label[][] cells = new Label[9][9];
	Button solveButton = new Button("Solve");
	Button loadButton = new Button("Read the Problem");
	Button solveAllButton = new Button("Solve All Problems");
	
	ListBox algorithmList = new ListBox();
	ListBox problemList = new ListBox();
	
	CheckBox stepBox = new CheckBox("Stepwise");
	
	RootPanel boardPanel = RootPanel.get("gridSpace");
	RootPanel buttons = RootPanel.get("button");
	RootPanel algorithmPanel = RootPanel.get("algorithmPanel");
	RootPanel problemPanel = RootPanel.get("problemPanel");
	RootPanel stepPanel = RootPanel.get("stepPanel");
	RootPanel bulletin = RootPanel.get("bulletin");
	
	HashMap<String, Algorithm> algorithms = new HashMap<String, Algorithm>();
	
	InnerClickHandler handler = new InnerClickHandler();
	InnerStateListener stateListener = new InnerStateListener();
	InnerChangeListener changeListener = new InnerChangeListener();

	static int forward;
	static int backtrack;
	static boolean solutionFound = false;
	static long time;
	static int solutionCount;
	
	Timer timer;
	
    SudokuBoard board;
	
	public GWTSudoku(){
		
		/** 演算法請在這裡註冊！ */
		algorithms.put("DFS", new TraceableDFSAlgorithm());
		algorithms.put("A*",  new TraceableAStarAlgorithm());
		algorithms.put("Hill Climbing", new HillClimbingAlgorithm());
		//algorithms.put("A*", new TraceableAStarAlgorithm());
		solveButton.addStyleName("button");
		solveButton.addClickHandler(handler);
		solveButton.setEnabled(false);
		loadButton.addStyleName("button");
		loadButton.addClickHandler(handler);
		solveAllButton.addStyleName("button");
		solveAllButton.addClickHandler(handler);
		algorithmPanel.add(algorithmList);
		algorithmList.addChangeHandler(changeListener);
		problemList.addChangeHandler(changeListener);
		problemPanel.add(problemList);
		stepPanel.add(stepBox);
		grid.addStyleName("board");
		grid.setBorderWidth(1);
		for(int i = 0; i < cells.length ; i++){
			for(int j = 0; j < cells[i].length; j++){
				cells[i][j] = new Label();
				cells[i][j].addStyleName("cell");
				grid.setWidget(i, j , cells[i][j]);
			}
		}

		Set<String> keys = algorithms.keySet();
		for( String name : keys){
			algorithmList.addItem( name );
		}
		
		for(Algorithm algo : algorithms.values())
			algo.addStateListener(stateListener);
		
		for(int i = 1; i <= ProblemSet.problems.length ; i++)
			problemList.addItem(String.valueOf(i));
	}
	
	public void onModuleLoad() {
		buttons.clear();
		buttons.add(loadButton);
		buttons.add(solveButton);
		buttons.add(solveAllButton);
		boardPanel.clear();
		boardPanel.add(grid);
	}
	
	private void showBoard(SudokuBoard board){
		
		Cell[][] cellsData= board.getCells();
		for(int i = 0; i < cells.length ; i++){
			for(int j = 0; j < cells[i].length; j++){
				int sol = cellsData[i][j].getAnswer();
				cells[i][j].setText(String.valueOf(sol));
				if(sol == 0){
					cells[i][j].removeStyleName("empty-cell");
					cells[i][j].addStyleName("free-cell");
				}
				else{
					if(cells[i][j].getStyleName().contains("free-cell")){
						cells[i][j].removeStyleName("free-cell");
						cells[i][j].addStyleName("empty-cell");
					}
				}
			}
		}
	}
	
	private class InnerChangeListener implements ChangeHandler{

		@Override
		public void onChange(ChangeEvent event) {
			if(event.getSource() == algorithmList){
				String name = algorithmList.getItemText(algorithmList.getSelectedIndex());
				Algorithm algo = algorithms.get(name);
				if(algo instanceof Traceable){
					stepBox.setVisible(true);
				}
				else{
					stepBox.setValue(false);
					stepBox.setVisible(false);
				}
			}
			else if(event.getSource() == problemList){
				
			}
		}
	}

	private static void initializeRecord(){
		GWTSudoku.forward = 0;
		GWTSudoku.backtrack = 0;
		GWTSudoku.solutionFound = false;
		GWTSudoku.solutionCount = 0;
	}
	
	
	private class InnerClickHandler implements ClickHandler{

		public void onClick(ClickEvent event) {
			if( event.getSource() == loadButton ){
				bulletin.clear();
				int index = Integer.parseInt(problemList.getValue(problemList.getSelectedIndex()))-1;
				board = new SudokuBoard(ProblemSet.problems[index]);

				for(int i = 0; i < cells.length; i++){
					for(int j = 0; j < cells[i].length; j++){
						cells[i][j].removeStyleName("empty-cell");
					}
				}

				for(FreeCell fc:board.getFreeCells()){
					cells[fc.getRow()][fc.getColumn()].addStyleName("empty-cell");
				}
				showBoard(board);
				solveButton.setEnabled(true);
				if(timer != null)
					timer.cancel();
			}
			else if(event.getSource() == solveButton){
				initializeRecord();
				solveButton.setEnabled(false);
				String name = algorithmList.getItemText(algorithmList.getSelectedIndex());
				final Algorithm algo = algorithms.get(name);
				if( algo instanceof Traceable && stepBox.getValue()){
					Traceable t = (Traceable)algo;
					final AlgorithmTrace trace = t.newTraceInstance(board);
					timer = new Timer(){
						@Override
						public void run() {
							if(trace.hasNextStep()){
								trace.nextStep();
							}
							else{
								this.cancel();
								GWTSudoku.time = System.currentTimeMillis() - GWTSudoku.time;
								displayPerformance();
							}
							showBoard(board);
						}
					};
					GWTSudoku.time = System.currentTimeMillis();
					timer.scheduleRepeating(100);
				}
				else{
					GWTSudoku.time = System.currentTimeMillis();
					algo.solve(board);
					GWTSudoku.time = System.currentTimeMillis() - GWTSudoku.time;
					displayPerformance();
					showBoard(board);
				}
			}
			else if(event.getSource() == solveAllButton){
				initializeRecord();
				GWTSudoku.time = System.currentTimeMillis();
				String name = algorithmList.getItemText(algorithmList.getSelectedIndex());
				Algorithm algo = algorithms.get(name);
				
				for(int i = 0; i < ProblemSet.problems.length ; i++){
					int[][] cells = ProblemSet.problems[i];
					board = new SudokuBoard(cells);
					algo.solve(board);
					if(solutionFound)
						solutionCount++;
					solutionFound = false;
				}
				GWTSudoku.time = System.currentTimeMillis() - GWTSudoku.time;
				displayAllPerformance();
			}
		}
	}
	
	private class InnerStateListener implements AlgorithmStateListener{
		@Override
		public void stateChanged(Cell[][] data, int row, int column, int state) {
			
			if(state == Algorithm.INITIAL){
			}
			else if(state == Algorithm.BACKTRACK  ){
				GWTSudoku.backtrack++;
			}
			else if( state == Algorithm.FORWARD ){
				GWTSudoku.forward++;
			}
			else if(state == Algorithm.SOLUTION_FOUND){
				GWTSudoku.solutionFound = true;
			}
			else if(state == Algorithm.COMPLETE){
				
			}
		}
	}
	
	private void displayAllPerformance(){
		bulletin.clear();
		Label forwardLabel = new Label("Trial: "+GWTSudoku.forward);
		Label backtrackLabel = new Label("Backtrack: "+GWTSudoku.backtrack);
		Label timeLabel = new Label("Cost Time: "+GWTSudoku.time+" ms ");
		Label solvedLabel = new Label("In "+ProblemSet.problems.length+" problems,"+solutionCount+ " among them are solved。");
		bulletin.add(forwardLabel);
		bulletin.add(backtrackLabel);
		bulletin.add(timeLabel);
		bulletin.add(solvedLabel);
	}
	
	private void displayPerformance(){
		bulletin.clear();
		Label forwardLabel = new Label("Trial: "+GWTSudoku.forward);
		Label backtrackLabel = new Label("Backtrack: "+GWTSudoku.backtrack);
		Label timeLabel = new Label("Cost Time: "+GWTSudoku.time+" ms ");
		Label solvedLabel = new Label( "Problem "+(GWTSudoku.solutionFound?"is":"isn't") + " solved.");
		bulletin.add(forwardLabel);
		bulletin.add(backtrackLabel);
		bulletin.add(timeLabel);
		bulletin.add(solvedLabel);
	}
}
