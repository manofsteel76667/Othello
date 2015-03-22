/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * 
 * @author Anderson
 */
public class OthelloCanvas extends Canvas implements OthelloGameEventListener {

	/**
	 * TODO: Fix tile flashing after change bugs
	 * TODO: clean up calls to board.
	 */	
	private static final long serialVersionUID = -6043771758228084859L;
	private int TileSize = 75;
	private int PieceSize = 50;
	private int Gutter = 10;
	public OthelloBoard myBoard;
	private java.awt.image.BufferedImage offscreenimage;

	public OthelloCanvas(final OthelloGame game) {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public
			void mouseClicked(MouseEvent e) {
				OthelloTile tile = GetTileAtPoint(e.getPoint());
				game.handleTilePlacement(tile.x, tile.y);
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		if (myBoard == null)
			return;
		Graphics2D offscreengraphic = (Graphics2D) offscreenimage.getGraphics();
		for (OthelloTile tile : myBoard.AllTiles) {
			Point start = GetTileStartPoint(tile);
			// Draw the square
			offscreengraphic.setColor(Color.GREEN);
			offscreengraphic.fillRect(start.x, start.y, TileSize, TileSize);
			// Black border around the square
			offscreengraphic.setColor(Color.BLACK);
			offscreengraphic.drawRect(start.x, start.y, TileSize, TileSize);
			// Any game piece on the tile
			if (tile.isClaimed) {
				offscreengraphic.setColor(tile.color ? java.awt.Color.WHITE : java.awt.Color.BLACK);
				Shape circle = new Ellipse2D.Double(start.x
						+ (TileSize - PieceSize) / 2, start.y
						+ (TileSize - PieceSize) / 2, PieceSize, PieceSize);
				offscreengraphic.draw(circle);
				offscreengraphic.fill(circle);
			}
		}
		g.drawImage(offscreenimage, 0, 0, null);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	private Point GetTileStartPoint(OthelloTile tile) {
		Point ret = new Point();
		ret.x = Gutter + tile.x * TileSize;
		ret.y = Gutter + tile.y * TileSize;
		return ret;
	}

	/**
	 * Returns the tile that contains the given point, or null if no tile
	 * contains that point (normally due to clicking on the gutter)
	 * 
	 * @param point
	 * @return OthelloTile
	 */
	public OthelloTile GetTileAtPoint(Point point) {
		int x = (int) Math.floor((point.x - Gutter) / TileSize);
		int y = (int) Math.floor((point.y - Gutter) / TileSize);
		return myBoard.getTile(x, y);
	}

	@Override
	public void handleGameEvent(OthelloBoard board) {
		myBoard = board;
		Dimension dim = new Dimension();
		dim.height = Gutter * 2 + myBoard.BoardHeight * TileSize;
		dim.width = Gutter * 2 + myBoard.BoardWidth * TileSize;
		offscreenimage = new java.awt.image.BufferedImage(dim.width,
				dim.height, BufferedImage.TYPE_INT_ARGB);
		setPreferredSize(dim);
		this.repaint();
	}
}
