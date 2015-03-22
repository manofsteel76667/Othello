/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * 
 * @author Anderson
 */
public class MainForm extends javax.swing.JFrame implements
		OthelloGameEventListener {

	private static final long serialVersionUID = 1L;
	final Label scoreLabel;
	final Label playerLabel;
	final OthelloCanvas canvas;
	final OthelloMenu menu;
	final OthelloGame game;

	public static void Run() {
		new MainForm("Othello");
	}

	MainForm(String Title) {
		super(Title);
		game = new OthelloGame();
		this.setLayout(new FlowLayout());
		scoreLabel = new Label();
		playerLabel = new Label();
		canvas = new OthelloCanvas(game);
		game.addGameEventListener(canvas);
		game.addGameEventListener(this);
		game.setWhitePlayer(new OthelloPlayer("White", OthelloAIType.Human,
				true));
		game.setBlackPlayer(new OthelloPlayer("Black", OthelloAIType.Hard,
				false));
		menu = new OthelloMenu(this);
		this.setJMenuBar(menu);
		game.startNewGame();
		add(scoreLabel);
		add(playerLabel);
		add(canvas);
		pack();
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		validate();
		setVisible(true);
	}

	public void updateScreenData() {
		String scoreFormat = "%s Score: %s: %d / %s: %d";
		scoreLabel.setText(String.format(scoreFormat, game.isOver() ? "Final" : "Current",
				game.getWhitePlayer().getName(),
				game.getBoard().getPlayerScore(game.getWhitePlayer().isWhite),
				game.getBlackPlayer().getName(),
				game.getBoard().getPlayerScore(game.getBlackPlayer().isWhite)));
		playerLabel.setText(game.isOver() ? "Game Over" : 
			String.format("%s's turn", game.getCurrentPlayer().getName()));
	}

	public void handleTileClick(Object source, OthelloTile tile) {
		game.handleTilePlacement(tile.x, tile.y);
	}

	@Override
	public void handleGameEvent(OthelloBoard board) {
		updateScreenData();
	}
}

class OthelloMenu extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JMenu gamemenu, player1, player2;
	JMenuItem newgame;
	ButtonGroup player1ai, player2ai;

	public OthelloMenu(final MainForm owner) {
		gamemenu = new JMenu("Game");
		gamemenu.setMnemonic(KeyEvent.VK_A);
		gamemenu.getAccessibleContext().setAccessibleDescription("Main Menu");
		this.add(gamemenu);
		newgame = new JMenuItem("New Game");
		newgame.setMnemonic(KeyEvent.VK_N);
		newgame.getAccessibleContext().setAccessibleDescription(
				"Start a new game");
		newgame.setActionCommand("newgame");
		newgame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				owner.game.startNewGame();
			}

		});
		gamemenu.add(newgame);
		player1 = new JMenu("Player 1");
		player2 = new JMenu("Player 2");
		player1ai = new ButtonGroup();
		player2ai = new ButtonGroup();
		for (final OthelloAIType type : OthelloAIType.values()) {
			JRadioButtonMenuItem p1 = new JRadioButtonMenuItem(type.toString());
			JRadioButtonMenuItem p2 = new JRadioButtonMenuItem(type.toString());
			if (type == owner.game.whitePlayer.getAIType())
				p1.setSelected(true);
			if (type == owner.game.blackPlayer.getAIType())
				p2.setSelected(true);
			p1.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					owner.game.getWhitePlayer().setAIType(type);
				}

			});
			p2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					owner.game.getBlackPlayer().setAIType(type);
				}

			});
			player1ai.add(p1);
			player1.add(p1);
			player2ai.add(p2);
			player2.add(p2);
			if (type == OthelloAIType.Human) {
				player1.addSeparator();
				player2.addSeparator();
			}
		}
		gamemenu.add(player1);
		gamemenu.add(player2);
	}
}
