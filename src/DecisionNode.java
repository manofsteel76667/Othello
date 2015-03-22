/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * 
 * @author Anderson
 */
public class DecisionNode implements Comparable<DecisionNode> {
	// Fields

	private OthelloTile LastMove;
	public int id;
	public int layerid;
	public ArrayList<Integer> layerlengths;
	public DecisionNode parent;
	private DecisionNode root;
	private int size = -1;
	private OthelloGame board;
	private LinkedList<DecisionNode> NextTurn;
	private TreeSet<DecisionNode> states;
	private int LookAhead;
	private boolean forWhite;
	protected int MoveQuality = 0;

	// Static method to build and use decision tree
	public static OthelloTile getBestMove(OthelloGame board,
			OthelloPlayer forPlayer) {
		DecisionNode root = new DecisionNode(board, forPlayer, forPlayer
				.getAIType().getLookAhead());
		root.createDecisionTree();
		root.pruneDecisionTree();
		root.evaluateDecisionTree();
		int best = Integer.MIN_VALUE;
		// Return the best move, or one of the best moves at random if there are
		// more than one of equal quality.
		for (DecisionNode node : root.NextTurn)
			if (node.MoveQuality > best)
				best = node.MoveQuality;
		int i = 0;
		while (i < root.NextTurn.size())
			if (root.NextTurn.get(i).MoveQuality < best)
				root.NextTurn.remove(i);
			else
				i++;
		java.util.Random rnd = new java.util.Random();
		i = rnd.nextInt(root.NextTurn.size());
		OthelloTile ret = root.NextTurn.get(i).LastMove;
		root.destroy();
		root = null;
		return ret;
	}

	// Constructors
	protected DecisionNode(OthelloGame _board) {
		// Base constructor. Does nothing but copy the board and create the next
		// layer of the tree.
		this.board = _board.getCopy();
		this.NextTurn = new LinkedList<DecisionNode>();
	}

	public DecisionNode(OthelloGame _game, OthelloPlayer forPlayer,
			int lookahead) {
		// Create the root node
		this(_game);
		this.root = this;
		this.forWhite = forPlayer.isWhite;
		this.LookAhead = lookahead;
		this.states = new TreeSet<DecisionNode>();
		layerlengths = new ArrayList<Integer>();
		layerid = getNextLayerId(layer());
	}

	protected DecisionNode(DecisionNode laststate, OthelloTile move) {
		// Create a child node
		this(laststate.board);
		this.root = laststate.root;
		this.parent = laststate;
		this.forWhite = root.forWhite;
		this.LastMove = move;
		this.board.handleTilePlacement(move.x, move.y);
		this.LookAhead = laststate.LookAhead - 1;
		id = getNextId();
		layerid = getNextLayerId(layer());
	}

	public int hashCode() {
		return 100000 * layer() + layerid;
	}

	protected boolean getIsMyTurn() {
		/*
		 * Bizarre little boolean equality tester found online, left here to see
		 * if it makes a performance diff
		 */
		return this.board.getCurrentPlayer().isWhite ? root.board.getCurrentPlayer().isWhite
				: !root.board.getCurrentPlayer().isWhite;
	}

	protected void destroy() {
		for (DecisionNode node : NextTurn) {
			if (node.NextTurn.size() == 0)
				node = null;
			else
				node.destroy();
		}
	}

	protected int layer() {
		return root.LookAhead - LookAhead;
	}

	protected int getNextId() {
		root.size++;
		return root.size;
	}

	protected int getNextLayerId(int layer) {
		while (root.layerlengths.size() < layer + 1)
			root.layerlengths.add(-1);
		int ret = root.layerlengths.get(layer);
		ret++;
		return ret;
	}

	private void createDecisionTree() {
		// If this is an endpoint, do nothing
		if (this.LookAhead == 0 || this.board.isOver())
			return;
		// If not, create the next level in the tree
		for (OthelloTile tile : this.board.board
				.getLegalMoves(this.board.getCurrentPlayer().isWhite))
			this.NextTurn.add(new DecisionNode(this, tile));
		for (DecisionNode node : this.NextTurn)
			node.createDecisionTree();
		// If the next move is the last one, add this to the list of endpoints
		if (this.LookAhead == 1)
			addFinalState(this);
	}

	protected void pruneDecisionTree() {
		/*
		 * Handle cases where a move would result in a player having to skip his
		 * turn. If the player is not the one making the initial move, this is
		 * good; prune all the other moves. Otherwise, prune the move that
		 * resulted in the skip unless it's the only move left.
		 */
		Iterator<DecisionNode> i = NextTurn.iterator();
		while (i.hasNext() && this.NextTurn.size() > 1) {
			DecisionNode node = i.next();
			if (node.board.getCurrentPlayer().isWhite == this.board.getCurrentPlayer().isWhite)
				if (this.getIsMyTurn()) {
					this.NextTurn.clear();
					this.NextTurn.add(node);
				} else
					this.NextTurn.remove(i);
		}
		for (DecisionNode node : this.NextTurn)
			node.pruneDecisionTree();
	}

	protected void evaluateDecisionTree() {
		/*
		 * Starting from the endpoints, run a minimax on each node, moving up
		 * the tree to the root. When the root node has been evaluated, we are
		 * done.
		 */
		for (DecisionNode node : root.states)
			node.MoveQuality = getAdvantage(node.board.board);
		Iterator<DecisionNode> i = root.states.iterator();
		while (i.hasNext()) {
			DecisionNode node = i.next();
			if (!node.getIsMyTurn())
				this.Maximize();
			else
				this.Minimize();
			root.states.remove(node);
			if (node.parent != null)
				addFinalState(node.parent);
		}
	}

	private void Maximize() {
		if (this.NextTurn.size() == 0)
			return;
		int bestvalue = Integer.MIN_VALUE;
		for (DecisionNode node : this.NextTurn)
			if (node.MoveQuality > bestvalue)
				bestvalue = node.MoveQuality;
		this.MoveQuality = bestvalue;
	}

	private void Minimize() {
		if (this.NextTurn.size() == 0)
			return;
		int bestvalue = Integer.MAX_VALUE;
		for (DecisionNode node : this.NextTurn)
			if (node.MoveQuality < bestvalue)
				bestvalue = node.MoveQuality;
		this.MoveQuality = bestvalue;
	}

	protected void addFinalState(DecisionNode node) {
		if (root.states.contains(node))
			return;
		root.states.add(node);
	}
	public int tilescore;
	public int safescore;
	public int cornerscore;
	
	protected int getAdvantage(OthelloBoard _board) {
		// Positive numbers indicate the caller has the advantage. negative
		// means the opponent
		if (root == null)
			return 0;
		int scoremultiplier = root.forWhite ? 1 : -1;
		int cornervalue = 20;
		int safevalue = 2;
		// Get the current score
		int ret = (_board.getPlayerScore(true) - _board
				.getPlayerScore(false)) * scoremultiplier;
		tilescore = ret;
		/* 
		 * If the game is over, return Max or Min value, depending on who was
		 * ahead when it ended.
		 */
		if (_board.isGameOver()) {
			if (ret != 0)
				if (ret > 0)
					return Integer.MAX_VALUE;
				else
					return Integer.MIN_VALUE;
			else
				return 0;
						
		}
		/*
		 * Bonus points for controlling edges and corners. More points for
		 * having tiles that are currently not takeable
		 */
		ret = 0;
		for (OthelloTile tile : board.board.ClaimedTiles) {
			if (!tile.CanBeClaimedBy(!root.forWhite)
					&& tile.color == root.forWhite)
				ret += safevalue;
			if (!tile.CanBeClaimedBy(root.forWhite)
					&& tile.color == !root.forWhite)
				ret -= safevalue;
			ret -= cornervalue;
		}
		safevalue = ret;
		ret = 0;
		for(OthelloTile tile : board.board.Corners)
			if (tile.isClaimed)
				if (tile.color == root.forWhite)
					ret += safetilesincorner(tile) * cornervalue;
				else
					ret -= safetilesincorner(tile) * cornervalue;	
		cornerscore = ret;
		return tilescore + safescore + cornerscore;
	}

	@Override
	public int compareTo(DecisionNode other) {
		return hashCode() - other.hashCode();
	}
	private Integer safetilesincorner(OthelloTile tile){
		int ret = 0;
		TileDirection ydir = tile.getNeighbor(TileDirection.Up) != null? TileDirection.Up : TileDirection.Down;
		TileDirection xdir = tile.getNeighbor(TileDirection.Left) != null? TileDirection.Left : TileDirection.Right;
		int ymax = 8;
		int ycount = 0;
		int yval = tile.y;
		int xval = tile.x;
		OthelloTile toConsider = tile;
		boolean xmatch;
		boolean ymatch;
		/* Move along the y-axis of the corner, adding 1 for each tile found of the same color.
		 *  When an unclaimed or opposite tile is found, the index becomes the edge tile for the 
		 *  next row's search.
		 */
		do{
			do {
				ymatch = toConsider.color == tile.color && toConsider.isClaimed;
				ycount++;
				if (ymatch)
					ret++;
				else
					ymax = yval;
				yval += ydir.ydiff;
				toConsider = board.board.getTile(xval , yval);
			} while (ymatch && ycount < ymax);
			xval += xdir.xdiff;	
			yval = tile.y;
			toConsider = board.board.getTile(xval , yval);
			xmatch = toConsider != null;
			if (xmatch)
				xmatch = toConsider.isClaimed && toConsider.color == tile.color;
			ycount = 0;
		} while (xmatch);
		return ret;
	}
}
