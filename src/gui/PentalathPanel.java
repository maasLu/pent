package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

import Players.*;

import game.*;
import game.Board.Cell;
import gui.com.rush.HexGridCell;

public class PentalathPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Game game;
	private Player activePlayer;
	private Player PLAYER_1, PLAYER_2;
	private Board board;
	private int mouseX, mouseY, mouseI, mouseJ, pressedX, pressedY, pressedI, pressedJ;
	private Polygon clicked;
	private boolean drawneighbours, drawindeces;
	private boolean swapSides;
	private JLabel status;
	private static final int B_WIDTH = 9, B_HEIGHT = 9;
	private static final int NUM_HEX_CORNERS = 6;
	private static final int CELL_R = 30;
	public static Color cellColor = new Color(201, 135, 58);
	public static Color bgColor = new Color(28, 19, 8);
	public static Color highlightColor = new Color(145, 97, 42);
	// @formatter:off
	private int[][] cells = 
		{ { -1, -1, -1, 0, 0, 0, -1, -1, -1 },
			{   -1, 0, 0, 0, 0, 0, 0, 0, -1 }, 
			{   0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{   0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
			{   0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{   0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
			{   0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{   -1, -1, 0, 0, 0, 0, 0, -1, -1 },
			{   -1, -1, -1, -1, 0, -1, -1, -1, -1 }};
	// @formatter:on
	private int[] mCornersY = new int[NUM_HEX_CORNERS];
	private int[] mCornersX = new int[NUM_HEX_CORNERS];

	private static HexGridCell mCellMetrics = new HexGridCell(CELL_R);

	public PentalathPanel() {


		drawneighbours = false;
		drawindeces = true;
		this.setBackground(bgColor);
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				mouseX = e.getX();
				mouseY = e.getY();
				revalidate();
				repaint();
				revalidate();
			}

		});
		this.addMouseListener(new MouseListener () {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				pressedX = e.getX();
				pressedY = e.getY();
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(clicked.contains(new Point2D.Double(e.getX(), e.getY())) && activePlayer != null && activePlayer.getClass().getName() == "Players.HumanPlayer") {

					if(swapSides && board.getField(pressedJ, pressedI) > 0) {
						swapSides = false;
						HumanPlayer human = (HumanPlayer)activePlayer;
						if (human.swapSides())
							activePlayer = null;
					} else if(!board.isSuicideMove(pressedJ, pressedI, activePlayer.getPlayerId())){
						System.out.println("No suicide");
						int[] move = new int[]{pressedJ, pressedI};
						HumanPlayer human = (HumanPlayer)activePlayer;
						if (human.setNextMove(move))
							activePlayer = null;
					} else {
						System.out.println("Suicide!!");
					}
				}
			}


		});

	}

	public void undo() {	
		HumanPlayer human = (HumanPlayer)activePlayer;
		if (human.undoMove())
			activePlayer = null;
	}
	
	@Override
	public void paint(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int j = 0; j < B_HEIGHT; j++) {
			for (int i = 0; i < B_WIDTH; i++) {
				mCellMetrics.setCellIndex(i, j);
				//
				if (cells[j][i] != -1) {
					mCellMetrics.computeCorners(mCornersY, mCornersX);
					Polygon p = new Polygon(mCornersX, mCornersY, NUM_HEX_CORNERS);
					if (p.contains(new Point2D.Double(pressedX, pressedY))) {
						pressedX = pressedY = -1;
						pressedI = i; pressedJ = j;
						clicked = p;
					}
					if (p.contains(new Point2D.Double(mouseX, mouseY))) {
						mouseI = i;
						mouseJ = j;
						g2d.setColor(highlightColor);
					} else {
						g2d.setColor(cellColor);
					}
					//Drawing the neighbours for testing
					if(board.getCell(mouseJ, mouseI).getNeighbours() != null && drawneighbours) {
						Cell[] neighbours = board.getCell(mouseJ, mouseI).getNeighbours();
						for(int k = 0 ; k < neighbours.length ; k++) {
							if(neighbours[k].getValue() != -1) {
								if(neighbours[k].getX() == i && neighbours[k].getY() == j) {
									g2d.setColor(Color.green);
								}
							}
						} //*/
					}
					g2d.fillPolygon(mCornersX, mCornersY, NUM_HEX_CORNERS);
					g2d.setColor(Color.BLACK);
					g2d.drawPolygon(mCornersX, mCornersY, NUM_HEX_CORNERS);

					// stones on the board
					// g2d.setColor(Color.RED);
					int x = mCornersX[3] - (mCornersX[3] - mCornersX[0]) / 2;
					int y = mCornersY[3] - (mCornersY[3] - mCornersY[0]) / 2;

					
					if (board.getField(j, i) == 1) {
						g2d.setColor(Color.WHITE);
						g2d.fillOval(x - 20, y - 20, 40, 40);
					} else if (board.getField(j, i) == 2) {
						g2d.setColor(Color.BLACK);
						g2d.fillOval(x - 20, y - 20, 40, 40);
					}
					//small circle indicating the last stone on the board;
					if(board.getPieceCount() > 0) {
						if(board.getLastPiece()[0] == j && board.getLastPiece()[1] == i) {
							if (board.getField(j, i) == 1) {
								g2d.setColor(Color.BLACK);
								g2d.fillOval(x - 5, y - 5, 10, 10);
							} else if (board.getField(j, i) == 2) {
								g2d.setColor(Color.WHITE);
								g2d.fillOval(x - 5, y - 5, 10, 10);
							}
						}
					}
					if(drawindeces) {
						g2d.setColor(Color.GRAY);
						g2d.drawString("("+j+", "+i+")", x, y);
					}
				}
			}
		}
	}


	public void setActivePlayer(Player p) {
		activePlayer = p;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public Game getGame() {
		return game;
	}

	public void waitingForMove(Player aPlayer) {
		this.activePlayer = aPlayer;
		if (activePlayer.getPlayerId() == Game.PLAYER_ONE)
			if (activePlayer.getClass().getName() == "Players.HumanPlayer")
				setStatusText("It is White's move.");
			else
				setStatusText("Waiting for White's move.");

		else if (activePlayer.getPlayerId() == Game.PLAYER_TWO)
			if (activePlayer.getClass().getName() == "Players.HumanPlayer")
				setStatusText("It is Black's move.");
			else
				setStatusText("Waiting for Black's move.");
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setStatusText(String s) {
		status.setText(s);
	}

	public void setStatusLabel(JLabel l) {
		status = l;
	}

	public void gameHasEnded(int winner) {
		if (winner == 1) {
			setStatusText("White wins!");
		} else if (winner == 2) {
			setStatusText("Black wins!");
		}

	}

	public void setCanSwapSides(boolean state) {
		if (state && activePlayer.getClass().getName().equals("Players.HumanPlayer")) {
			swapSides = true;
			if (activePlayer.getPlayerId() == Game.PLAYER_ONE)
				setStatusText("Click the piece to swap if you want to!");
			else if (activePlayer.getPlayerId() == Game.PLAYER_TWO)
				setStatusText("Click the piece to swap if you want to!");
		} 
	}
}
