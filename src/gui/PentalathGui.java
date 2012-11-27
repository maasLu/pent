package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import Players.HumanPlayer;
import Players.Player;

import game.Game;
import gui.com.rush.HexGridCell;

public class PentalathGui extends JFrame {
	private static final long serialVersionUID = -1921481286866231418L;
	private JPanel contentPane;
	private JMenuBar menubar;
	private JMenu gameMenu;
	private JMenuItem newGame, undoMove;
	private final PentalathPanel pentalathPanel;
	private Game game;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PentalathGui frame = new PentalathGui();
					frame.setTitle("Pentalath");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PentalathGui() {
		setName("Pentalath");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 486, 530);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		
		game = new Game();
		pentalathPanel = new PentalathPanel();
		game.setBoardView(pentalathPanel);
		
		JButton swapSidesButton = new JButton("Swap sides");
		swapSidesButton.setVisible(false);
		swapSidesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Player activePlayer = pentalathPanel.getActivePlayer();
				if (activePlayer != null
						&& activePlayer.getClass().getName() == "Players.HumanPlayer") {
					if (((HumanPlayer) activePlayer).swapSides())
						pentalathPanel.setActivePlayer(null);
				}
			}
		});
		this.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if(game.getBoard().getPieceCount() > 0) {
						pentalathPanel.undo();
					}
				}
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		JLabel status = new JLabel("Welcome to Pentalath.");
		status.setHorizontalAlignment(JLabel.CENTER);
		status.setForeground(PentalathPanel.cellColor);
		contentPane.add(status, BorderLayout.SOUTH);
		contentPane.setBackground(PentalathPanel.bgColor);
		pentalathPanel.setStatusLabel(status);


		contentPane.add(pentalathPanel, BorderLayout.CENTER);
		menuSetup();
		

		contentPane.add(menubar, BorderLayout.NORTH);
		this.setContentPane(contentPane);
		new Thread(game).start();

	}
	
	private void menuSetup() {
		menubar = new JMenuBar();

		gameMenu = new JMenu("Game");
		
		newGame = new JMenuItem("New game");
		final PentalathPanel tempBoardView = pentalathPanel; 
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
								game = pentalathPanel.getGame();
                                game.stop();
                                game = null;
                                pentalathPanel.setActivePlayer(null);
				Game newgame = new Game();
                                pentalathPanel.setBoard(newgame.getBoard());
				newgame.setBoardView(tempBoardView);
				new Thread(newgame).start();
                                pentalathPanel.setGame(newgame);
                                setSize(getSize().height + 1, getSize().width + 1);
                                setSize(getSize().height - 1, getSize().width - 1);
                                game = newgame;
			}
		});
		undoMove = new JMenuItem("Undo move");
		undoMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(game.getBoard().getPieceCount() > 0) {
					pentalathPanel.undo();
				}
			}
			
		});
		gameMenu.add(newGame);
		gameMenu.add(undoMove);
		menubar.add(gameMenu);
		menubar.setForeground(PentalathPanel.bgColor);
		menubar.setBorderPainted(false);
	}
}
