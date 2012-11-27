package game;

import java.util.ArrayList;


public class Board implements Cloneable {

	
	private ArrayList<int[][]> moves;
	private int turn;
	/**
	 * class for the cells of the board.
	 * a cell knows who is next to it and if there's a stone from player 1 or 2 or if its
	 * empty.
	 * 0 = cell is empty
	 * 1 = stone from player 1
	 * 2 = stone from player 2
	 * -1 = border
	 */
	public class Cell {

		Cell[] neighbours;
		int value, x, y;

		public Cell(int value) {
			neighbours = null;
			this.value = value;
		}

		public Cell[] getNeighbours() {
			return neighbours;
		}

		public int getValue() {
			return this.value;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}
	private int size, nPieces;
	private Cell[][] board;
	private Cell border;
	private Cell[] capturedCells;

	/**
	 * Creates a new board with specified dimensions.
	 * border cell is used to determine when cell's link is a border.
	 * @param dimensions the size of the board
	 */
	public Board() {
		moves = new ArrayList<int[][]>();
		turn = 0;
		int[][] cells = 
			{ { -1, -1, -1, 0, 0, 0, -1, -1, -1 },
				{   -1, 0, 0, 0, 0, 0, 0, 0, -1 }, 
				{   0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{   0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{   0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{   0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{   0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{   -1, -1, 0, 0, 0, 0, 0, -1, -1 },
				{   -1, -1, -1, -1, 0, -1, -1, -1, -1 }};

		size = cells.length;
		board = new Cell[size][size];

		border = new Cell(-1);
		border.x = -1;
		border.y = -1;
		border.neighbours = null;


		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = new Cell(cells[i][j]);
				board[i][j].x = j;
				board[i][j].y = i;

			}
		}

		findNeighbourCells();
	}
	/**
	 * this method connects all cells with their neighbouring cells
	 */
	private void findNeighbourCells() {
		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				Cell cell = board[j][i];
				if(cell.getValue() == 0) {
					cell.neighbours = new Cell[6];
					int top = i-1;
					int bottom = i+1;
					int left = j-1;
					int right = j+1;

					if(right < size) {
						cell.neighbours[2] = board[right][i];
					} else {
						cell.neighbours[2] = border;
					}

					if(left > -1) {
						cell.neighbours[5] = board[left][i];
					} else {
						cell.neighbours[5] = border;
					}

					if(i % 2 == 0) { // even row
						if(top > -1) {
							if(left > -1) {
								cell.neighbours[0] = board[left][top];
							} else {
								cell.neighbours[0] = border;
							}
							cell.neighbours[1] = board[j][top];
						} else {
							cell.neighbours[1] = border;
							cell.neighbours[0] = border;
						}
					if(bottom < size) {
						if(left > -1) {
							cell.neighbours[4] = board[left][bottom];
						} else {
							cell.neighbours[4] = border;
						}
						cell.neighbours[3] = board[j][bottom];
					} else {	
						cell.neighbours[4] = border;
						cell.neighbours[3] = border;
					}

					} else { // odd row
						if(top > -1) {
							if(right < size) {
								cell.neighbours[1] = board[right][top];
							} else {
								cell.neighbours[1] = border;
							}
							cell.neighbours[0] = board[j][top];	
						} else {
							cell.neighbours[0] = border;
							cell.neighbours[1] = border;
							
						}
						if(bottom < size) {
							if(right < size) {
								cell.neighbours[3] = board[right][bottom];
							} else {
								cell.neighbours[3] = border;
							}
							cell.neighbours[4] = board[j][bottom];
						} else {
							cell.neighbours[3] = border;
							cell.neighbours[4] = border;
							
						}
					}
				}
			}
		}
	}
	
	private int[] lastPiece;

	/**
	 * Puts a piece on the board.
	 * counter is used to keep a record of the amount of moves in a game
	 * Checks for capturing and captures cells as well.
	 * @param x the x coordinate on the board
	 * @param y the y coordinate on the board
	 * @param player the player the piece belongs to(1 or 2)
	 */
	public void setPiece(int x, int y, int player) {
		
		if (board[x][y].value == 0) {
			board[x][y].value = player;
			nPieces++;
			turn++;
			lastPiece = new int[]{x, y};
		}
		if(isCapturing(lastPiece[0], lastPiece[1])) {
			capture();
		}
		
		int[] added = new int[]{x,y};
		int[] deleted = new int[capturedCells.length*2];

		for(int i = 0 ; i < capturedCells.length ; i++) {
			if(player == 1) { // add 100 to make sure the deleted pieces were black ones
				deleted[2*i] = capturedCells[i].x + 100;
				deleted[2*i+1] = capturedCells[i].y + 100;
			} else {
				deleted[2*i] = capturedCells[i].x;
				deleted[2*i+1] = capturedCells[i].y;
			}
		}
		
		int[][] move = new int[][]{added, deleted};
		moves.add(move);
		capturedCells = null;
	}
	
	/**
	 * Adds a piece on the board.
	 * counter is used to keep a record of the amount of moves in a game
	 * @param x the x coordinate on the board
	 * @param y the y coordinate on the board
	 * @param player the player the piece belongs to(1 or 2)
	 */
	public void addPiece(int x, int y, int player) {
		while(x >= 100) {
			x-=100;
		}
		while(y >= 100) {
			y-=100;
		}
		if (board[x][y].value == 0) {
			board[x][y].value = player;
			nPieces++;
			lastPiece = new int[]{x, y};
		}
	}
	
	/**
	 * Remove a piece from the board
	 * @param x the x coordinate from the piece to be removed
	 * @param y the y coordinate from the piece to be removed
	 */
	public void removePiece(int x, int y) {
		board[x][y].value = 0;
		nPieces--;
	}

	/**
	 * Returns the last piece which was placed on the board
	 * @return the last piece which was placed on the board
	 */
	public int[] getLastPiece() {
		return lastPiece;
	}

	
	public void undoMove() {
		int[][] move = moves.remove(turn-1);
		turn--;
		int[] toDel = move[0];
		int[] toAdd = move[1];
		removePiece(toDel[0], toDel[1]);
		if(turn > 0)
			lastPiece = moves.get(turn-1)[0];
		for(int i = 0; i < toAdd.length; i++) {
			if(toAdd[i] > 100) {
				toAdd[i] -= 100;
				toAdd[i+1] -= 100;
				addPiece(toAdd[i+1], toAdd[i], 2);
			} else {
				addPiece(toAdd[i+1], toAdd[i], 1);
			}
			i++;
		}
		
	}
	/**
	 * Swaps the stone of the players if there's only one piece on the board.
	 */
	public void swapSides() {
		if (nPieces == 1) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (board[i][j].value == 1) {
						board[i][j].value = 2;

					} else if (board[i][j].value == 2) {
						board[i][j].value = 1;
					}
				}
			}
		}
	}

	/**
	 * Returns the size of the board.
	 * @return the size of one side of the board
	 */
	public int getDimensions() {
		return size;
	}

	/**
	 * Returns the number of pieces on the board.
	 * @return the size of one side of the board
	 */
	public int getPieceCount() {
		return nPieces;
	}

	/**
	 * Returns the player id that the piece belongs to or 0 if the field is
	 * empty.
	 * @param x the x coordinate on the board
	 * @param y the y coordinate on the board
	 * @return returns player id of the piece or 0 if the field is blank
	 */
	public int getField(int x, int y) {
		return board[x][y].value;
	}

	/**
	 * returns a specific cell
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return the specific cell
	 */
	public Cell getCell(int x, int y) {
		return board[x][y];
	}
	
	private boolean[][] checked;
	private boolean[][] freedomChecked;

	public boolean isSuicideMove(int x, int y, int player) {
		addPiece(x,y,player);
		freedomChecked = new boolean[board.length][board[0].length];
		if(hasFreedom(x, y)) {
			removePiece(x, y);
			return false;
		}
		removePiece(x, y);
		return true;
	}
	
	public boolean isCapturing(int x, int y) {
		ArrayList<Cell> captured = new ArrayList<Cell>();
		ArrayList<Cell> candidates = new ArrayList<Cell>();
		Cell current = board[x][y];
		int capVal = current.value % 2 + 1; // value of the cells to be captured by the current move
		System.out.println(capVal);
		checked = new boolean[board.length][board[0].length];
		freedomChecked = new boolean[board.length][board[0].length];
		for(int i = 0 ; i < current.neighbours.length ; i++) {
			Cell n = current.neighbours[i];
			//System.out.println("Checking neighbour");
			if(n.value == capVal && !checked[n.x][n.y] && !freedomChecked[n.x][n.y]) {
				checked[n.x][n.y] = true;
				candidates.add(n);
				if(hasFreedom(n, capVal, candidates)) {
					System.out.println("freedom for ("+n.x+", "+n.y+"), value = "+n.value);
					freedomChecked[n.x][n.y] = true;
					candidates.clear();
				} else{
					System.out.println("No freedom for ("+n.x+", "+n.y+")");
					captured.add(n);
					//System.out.println("Capture?");
				}
			}
		}
		capturedCells = null;
		capturedCells = new Cell[candidates.size()+captured.size()];
		for(int i = 0; i < candidates.size() ; i++) {
			capturedCells[i] = candidates.get(i);
		}
		for(int i = candidates.size(); i < candidates.size() + captured.size() ; i++) {
			capturedCells[i] = captured.get(i-candidates.size());
		}
		captured.clear();
		return (capturedCells.length > 0);
	}
	
	private boolean hasFreedom(Cell current, int player, ArrayList<Cell> candidates) {
		//freedomChecked[current.x][current.y] = true;
		boolean free = false;
		if(freedomChecked[current.x][current.y]) {
			return true;
		}
		checked[current.x][current.y] = true;
		for(Cell n : current.neighbours) {
			
			if(n.value == 0) {
				free = true;
			}
			if(n.value == player && !freedomChecked[n.x][n.y] && !checked[n.x][n.y]) {
				System.out.println("Checking: ("+n.x+", "+n.y+"), has value: "+n.value);
				
				checked[n.x][n.y] = true;
				candidates.add(n);
				if(hasFreedom(n, player, candidates)) {
					freedomChecked[n.x][n.y] = true;
					free = true;
				}
			}
		}
		return free;
	}
	
	private boolean hasFreedom(int x, int y) {
		Cell current = board[x][y];
		int player = current.value;
		freedomChecked[x][y] = true;
		if(current.neighbours != null) {
			for(Cell n : current.neighbours) {
				if(n.value == 0) {
					return true;
				}
				if(n.value == player && !freedomChecked[n.x][n.y]) {
					freedomChecked[n.x][n.y] = true;
					if(hasFreedom(n.x, n.y)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public void capture() {
		for(Cell c : capturedCells) {
			removePiece(c.y, c.x);
		}
	}
	
	/**
	 * Checks whether there are 5 stones connected in a straight line for a player. In
	 * that case the game ends.
	 * @return 1 if player one wins, 2 if player two wins, 0 otherwise.
	 */
	public int checkEnd() {

		return checkPath(5);
	}
	
	public int checkPath(int length) {
		int win = board[lastPiece[0]][lastPiece[1]].value;
		Cell current = board[lastPiece[0]][lastPiece[1]];
		if((check(current, 0, win, 0) + check(current, 3, win, 1)) == length 
				||(check(current, 1, win, 0) + check(current, 4, win, 1)) == length 
				||(check(current, 2, win, 0) + check(current, 5, win, 1)) == length)
			return win;
		return 0;
	}
	
	
	public int check(Cell current, int dir, int win, int length) {
		if(current.neighbours[dir] == null)
			return length;
		Cell n = current.neighbours[dir];
		if(n.value == win) {
			length++;
			length += check(n, dir, win, 0);
		}
		return length;
	}
		
	/**
	 *
	 * @return a string representation of the board
	 */
	@Override
	public String toString() {
		String string = "";
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				string += "{" + board[j][i].value + "}";
			}

			string += "\n";

			for (int k = -1; k < i; k++) {
				string += "  ";
			}
		}

		return string;
	}

	/**
	 * returns the board in a integer array representation
	 * @return the integer array
	 */
	public int[][] getBoard() {
		int[][] iBoard = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				iBoard[i][j] = board[i][j].value;
			}
		}
		return iBoard;
	}

	/**
	 * clones the board
	 * @return the cloned board
	 */
	@Override
	public Board clone() {
		Board clone = new Board();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].value != 0) {
					clone.setPiece(i, j, board[i][j].value);
				}
			}
		}

		return clone;
	}

	/*
	 * @return a Lite board representation of the board
	 *//*
    public LiteBoard getLiteBoard() {
    	int dimensions = getDimensions();
    	byte[][] boardArray = new byte[dimensions][dimensions];



    	for (int i = 0; i < dimensions; i++) {
			for (int j = 0; j < dimensions; j++) {
				boardArray[i][j] = (byte)getField(i, j);
			}
		}

    	return new LiteBoard(boardArray);
    }//*/
}
