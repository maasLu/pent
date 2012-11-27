package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import Players.*;
import gui.PentalathPanel;

/**
 * This class is a controller for the game. Please keep it simple and think
 * twice before writing something. Comments are always appreciated and don't
 * forget JavaDocs too. Cheers and good luck!
 *
 */
public class Game implements Runnable {

    private Thread thread;
    private boolean end;
	private PentalathPanel boardView;
	private Board board;
	private Player[] players;

	/**
	 * Represents the active player, the one that has the turn.
	 */
	private int playerTurn;

	/**
	 * Represents the number of the turn.
	 */
	private int turn;

	/**
	 * Initializes a game and sets the board.
	 */



    public Game() {
        this(5, new HumanPlayer(), new HumanPlayer());
    }

	
	public Game(int boardSize, Player playerOne, Player playerTwo) {
		board = new Board();
		
		players = new Player[2];
		
		playerOne.setGame(this);
		playerOne.setPlayerId(PLAYER_ONE);
		players[0] = playerOne;
		
		playerTwo.setGame(this);
		playerTwo.setPlayerId(PLAYER_TWO);
		players[1] = playerTwo;
		endGameListenners = new ArrayList<ActionListener>();
	}

	/**
	 * Check whether there is a board and AI set up if there are any and start a
	 * game.
	 */
	public void run()
	{

                thread = Thread.currentThread();

		playerTurn = PLAYER_ONE;
		turn = 0;

		while(!end) {
			this.doTurn();
		}
	}
	
	private int gameWinner;

	public void undoTurn() {
		board.undoMove();
		if (playerTurn == PLAYER_ONE)
			playerTurn = PLAYER_TWO;
		else if (playerTurn == PLAYER_TWO)
			playerTurn = PLAYER_ONE;
		turn--;
		
	}
	
	/**
	 * Carries out all the  operations for a turn to happen 
	 */
	private void doTurn() {		
		// Lets get the active player
		Player activePlayer = this.getPlayer(playerTurn);
		// Lets inform board that we are waiting for a move 
		if (boardView != null)
			boardView.waitingForMove(activePlayer);
		// If it's the first turn the player can swap sides
		if (turn == 1)
		{
			activePlayer.setCanSwapSides(true);
			if (boardView != null)
				boardView.setCanSwapSides(true);
		}
		else
		{
			activePlayer.setCanSwapSides(false);
			if (boardView != null)
				boardView.setCanSwapSides(false);
		}
				
		// Lets ask the move from the player
		int[] move = activePlayer.getNextMove();

                if (move == null) {
                    return;
                }

		// If it's the first turn of the game player might have wanted to swap
		// the board.
		if (turn == 1 && move[0] == -1 && move[1] == -1)
			board.swapSides();
		else if(move[0] == -2 && move[1] == -2) {
			boardView.waitingForMove(activePlayer);
			board.undoMove();
			turn-=2;
			activePlayer = this.getPlayer(playerTurn);
			if (boardView != null)
				boardView.waitingForMove(activePlayer);
			boardView.repaint();

		} else
			board.setPiece(move[0], move[1], activePlayer.getPlayerId());

		if (boardView != null)
			boardView.repaint();
		
		// Lets check whether the game has ended
		int endState = board.checkEnd();
		if (endState != 0)
		{
			end = true;
			if (boardView != null)
				boardView.gameHasEnded(endState);
			gameWinner = endState;
			performEndGameListeners();
			return;
		}

		// Lets have a move from the other player
		if (playerTurn == PLAYER_ONE)
			playerTurn = PLAYER_TWO;
		else if (playerTurn == PLAYER_TWO)
			playerTurn = PLAYER_ONE;

		turn++;
//		this.doTurn();
		
	}

	/**
	 * Returns a player by its identifier
	 * @param playerId numeric player identifier
	 * @return the player
	 */
    private Player getPlayer(int playerId)
	{
		for (Player player : this.players) {
			if (player.getPlayerId() == playerId) {
				return player;
			}
		}
		return null;
	}
    
    public int getGameWinner() {
    	return gameWinner;
    }

    /**
     * Sets the reference for the visual representation of the game.
     * @param boardView reference to the board view
     */
	public void setBoardView(PentalathPanel boardView)
	{
		this.boardView = boardView;
		this.boardView.setGame(this);
		this.boardView.setBoard(board);

	}

	/**
	 * Returns the game board at its current state
	 * @return game board
	 */
	public Board getBoard()
	{
		return this.board;
	}

	private ArrayList<ActionListener> endGameListenners;
	
	public void addEndGameListener(ActionListener aListener)
	{
		endGameListenners.add(aListener);
	}
	
	private void performEndGameListeners() {
		for (ActionListener listener : endGameListenners) {
			listener.actionPerformed(new ActionEvent(this, 1, "End Game"));
		}
	}

        public synchronized void stop() {
            boardView = null;
            thread.interrupt();
        }
	
	/**
	 * A constant that represents player one
	 */
	static public final int PLAYER_ONE = 1;

	/**
	 * A constant the represents player two;
	 */
	static public final int PLAYER_TWO = 2;
}
