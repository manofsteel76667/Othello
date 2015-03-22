/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anderson
 */
public class OthelloBoard {
	OthelloTile[][] boardarray;
	List<OthelloTile> AllTiles;
	public final int BoardWidth;
	public final int BoardHeight;

	// Constructor
	public OthelloBoard(int width, int height) {
		BoardWidth = width;
		BoardHeight = height;
		initializeBoard();
	}
	public OthelloTile getTile(int x, int y) {
		return boardarray[x][y];
	}
	public List<OthelloTile> Corners;
	public List<OthelloTile> ClaimedTiles;
	public List<OthelloTile> UnclaimedTiles;
	public int whitetiles;
	public int blacktiles;

	protected void initializeBoard() {
		ClaimedTiles = new ArrayList<OthelloTile>();
		UnclaimedTiles = new ArrayList<OthelloTile>();
		AllTiles = new ArrayList<OthelloTile>();
		Corners = new ArrayList<OthelloTile>();
		boardarray = new OthelloTile[BoardWidth][BoardHeight];
		for (int x = 0; x < BoardWidth; x++)
			for (int y = 0; y < BoardHeight; y++) {
				boardarray[x][y] = new OthelloTile(x, y);
				UnclaimedTiles.add(boardarray[x][y]);
				AllTiles.add(boardarray[x][y]);
			}
		//Mark corners
		Corners.add(getTile(0, 0));
		Corners.add(getTile(0, BoardHeight - 1));
		Corners.add(getTile(BoardWidth - 1, 0));
		Corners.add(getTile(BoardWidth - 1, BoardHeight - 1));
		//Build tile connections
		for (OthelloTile tile : AllTiles)
			for (TileDirection dir : TileDirection.values()) {
				int x = tile.x + dir.xdiff;
				int y = tile.y + dir.ydiff;
				if (x >= 0 && x < BoardWidth && y >= 0 && y < BoardHeight)
					tile.setNeighbor(dir, getTile(x, y));
			}
	}

	public boolean isGameOver() {
		/**
		 * * The game is over when one of the following conditions is met: 1)
		 * Neither player has a valid move (typically due to all board tiles
		 * being filled) 2) One player has no pieces left
		 */
		return (getLegalMoves(true).isEmpty() && getLegalMoves(false).isEmpty())
				|| whitetiles == 0 || blacktiles == 0;
	}

	public OthelloBoard getCopy() {
		OthelloBoard ret = new OthelloBoard(BoardHeight, BoardWidth);
		for (OthelloTile tile : ClaimedTiles) {
			OthelloTile newtile = ret.getTile(tile.x, tile.y);
			newtile.color = tile.color;
			newtile.isClaimed = tile.isClaimed;
			ret.ClaimedTiles.add(newtile);
			ret.UnclaimedTiles.remove(newtile);
		}
		ret.updateCollections();
		return ret;
	}

	public boolean MakeMove(int x, int y, boolean player) {
		return AddPiece(x, y, player, false);
	}

	private boolean AddPiece(int x, int y, boolean isWhite, boolean NoValidate) {
		OthelloTile thistile = boardarray[x][y];
		if (!thistile.CanBeClaimedBy(isWhite) && NoValidate == false)
			return false;
		thistile.isClaimed = true;
		thistile.color = isWhite;
		ClaimedTiles.add(thistile);
		UnclaimedTiles.remove(thistile);
		FlipTilesFrom(thistile);
		updateCollections();
		return true;
	}

	private void updateCollections() {
		whitetiles = 0;
		blacktiles = 0;
		for (OthelloTile tile : ClaimedTiles) {
			if (tile.color)
				whitetiles++;
			else
				blacktiles++;
		}
	}

	public List<OthelloTile> getLegalMoves(boolean forWhite) {
		ArrayList<OthelloTile> ret = new ArrayList<OthelloTile>();
		for (OthelloTile tile : UnclaimedTiles)
			if (tile.CanBeClaimedBy(forWhite))
				ret.add(tile);
		return ret;
	}

	public int getPlayerScore(boolean forWhite) {
		if (forWhite)
			return whitetiles;
		else
			return blacktiles;
	}

	public void startnewgame() {
		UnclaimedTiles.clear();
		ClaimedTiles.clear();
		for (OthelloTile tile : AllTiles) {
			tile.isClaimed = false;
			UnclaimedTiles.add(tile);
		}
		AddPiece(3, 3, true, true);
		AddPiece(3, 4, false, true);
		AddPiece(4, 4, true, true);
		AddPiece(4, 3, false, true);
	}

	private void FlipTilesFrom(OthelloTile tile) {
		for (TileDirection dir : TileDirection.values())
			if (tile.CanFlipInDir(dir)) {
				OthelloTile nexttile = tile.getNeighbor(dir);
				while (nexttile.color == !tile.color) {
					nexttile.color = !nexttile.color;
					//ModifiedTiles.add(nexttile);
					nexttile = nexttile.getNeighbor(dir);
				}
			}
	}

	public ArrayList<String> getClaimedTiles() {
		ArrayList<String> ret = new ArrayList<String>();
		for (OthelloTile tile : ClaimedTiles)
			ret.add(tile.toString());
		return ret;
	}

	public String serialize() {
		StringBuilder sb = new StringBuilder();
		for(OthelloTile t : AllTiles)
			sb.append(t.isClaimed ? "U" : t.color ? "W" : "B");
		return sb.toString();
	}
	
	public void overwriteFromString(String s) {
		UnclaimedTiles = new ArrayList<OthelloTile>();
		ClaimedTiles = new ArrayList<OthelloTile>();
		blacktiles = 0;
		whitetiles = 0;
		if (s.length() != 64)
			return;
		for (int i = 0; i < s.length(); i++) {
			OthelloTile t = AllTiles.get(i); 
			if (s.charAt(i) == 'U') {
				t.isClaimed = false;
				UnclaimedTiles.add(t);
			}
			if (s.charAt(i) == 'W') {
				t.isClaimed = true;
				t.color = true;
				ClaimedTiles.add(t);
				whitetiles++;
			}
			if (s.charAt(i) == 'B') {
				t.isClaimed = true;
				t.color = false;
				ClaimedTiles.add(t);
				blacktiles++;
			}
		}
			
	}
}
