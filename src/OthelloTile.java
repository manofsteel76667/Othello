import java.util.HashMap;

public final class OthelloTile {

	//fields
	public boolean isClaimed;
	/**
	 * false = black, true = white
	 */
	public boolean color;
	public static final boolean white = true;
	public static final boolean black = false;
	public boolean isCorner;
	public boolean isEdge;
	public boolean isSafe;
	public boolean isPermaSafe;
	public int x;
	public int y;
	public HashMap<TileDirection, OthelloTile> neighbors;
	
	// Constructor
	public OthelloTile(int _x, int _y) {
		x = _x;
		y = _y;
		neighbors = new HashMap<TileDirection, OthelloTile>();
	}
	public OthelloTile getCopy(){
		OthelloTile ret = new OthelloTile(this.x, this.y);
		ret.isClaimed = this.isClaimed;
		ret.color = this.color;
		return ret;
	}
	public OthelloTile getNeighbor(TileDirection dir){
		return neighbors.get(dir);
	}
	public void setNeighbor(TileDirection dir, OthelloTile tile) {
		neighbors.put(dir, tile);
	}
	/**
	 * Whether or not tiles in the given direction can be legally flipped to the
	 * color of the tile.  This happens when there is another tile of the same color
	 * somewhere in the direction given and there are nothing but opponent tiles in between.
	 * @param dir
	 * Direction to check in
	 * @return
	 */
	public boolean CanFlipInDir(TileDirection dir) {
		//If this tile is unclaimed or if the next tile in that direction
		//is null, unclaimed, or the same color, you cannot flip.
		if (!isClaimed)
			return false;
		OthelloTile nexttile = getNeighbor(dir);
		if (nexttile == null)
			return false;
		if (nexttile.color == color || !nexttile.isClaimed)
			return false;
		// The first tile is an opponent.  Keep going in that direction until you
		// get to the first non-opponent tile.  If it's unclaimed or null, you can't flip.
		// If it's the same color as you, you can flip.
		nexttile = nexttile.getNeighbor(dir);
		while (nexttile != null) {
			if (!nexttile.isClaimed)
				return false;
			if (nexttile.color == color)
				return true;
			nexttile = nexttile.getNeighbor(dir);
		}
		return false;
	}

	/**
	 * Returns true if this tile can be claimed during the given player's turn.
	 * This is determined by whether claiming the tile will result in any of the
	 * opponent's tiles being flipped.
	 * 
	 * @return boolean
	 */
	public boolean CanBeClaimedBy(boolean _color) {
		if (isClaimed)
			return false;
		color = _color;
		isClaimed = true;
		for (TileDirection dir : TileDirection.values())
			if (CanFlipInDir(dir)) {
				isClaimed = false;
				return true;
			}
		isClaimed = false;
		return false;
	}
	public int getHashCode(){
		if (!isClaimed)
			return 0;
		else
			return color ? 2 : 1;
	}
}
