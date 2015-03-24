import java.util.ArrayList;
import java.util.List;

class Move {
	/**
	 * Used for Undo function and MiniMax calculation
	 */
	public Move previousMove;
	/**
	 * Used for redo, useless in MiniMax
	 */
	public Move nextMove;
	public OthelloPlayer player;
	/**
	 * Tile that was place on this move
	 */
	public OthelloTile tile;
	/**
	 * State of the board AFTER the move was made. Check lastMove.finalState for
	 * initial board state. State is stored as a serialized board.
	 */
	public String finalState;
	/**
	 * Turn on which the move was made
	 */
	public int turn;
	public int moveQuality = 0;

	public Move(Move previousMove, OthelloPlayer player, OthelloTile tile,
			String boardState, int turn) {
		this.player = player;
		this.tile = tile;
		this.turn = turn;
		this.previousMove = previousMove;
		finalState = boardState;
		nextMove = null;
	}

	public String toString() {
		if (turn == 0)
			return "No tiles playet.";
		else
			return String.format("Turn %d: %s played tile %s", turn,
					player.getName(), tile.toString());
	}
}

public class OthelloGame implements OthelloGameEventSource {

	public List<Move> moves;
	public int turn;

	List<OthelloGameEventListener> eventListeners;
	Move previousMove;
	Move nextMove;
	OthelloBoard board;
	OthelloPlayer whitePlayer;
	OthelloPlayer blackPlayer;
	OthelloPlayer currentPlayer;

	public OthelloGame() {
		board = new OthelloBoard(8, 8);
		eventListeners = new ArrayList<OthelloGameEventListener>();
		moves = new ArrayList<Move>();
		previousMove = new Move(null, currentPlayer, null, board.serialize(), 0);
		nextMove = null;
	}

	public OthelloBoard getBoard() {
		return board;
	}

	public OthelloPlayer getWhitePlayer() {
		return whitePlayer;
	}

	public void setWhitePlayer(OthelloPlayer player) {
		whitePlayer = player;
	}

	public OthelloPlayer getBlackPlayer() {
		return blackPlayer;
	}

	public void setBlackPlayer(OthelloPlayer player) {
		blackPlayer = player;
	}

	public OthelloPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	public void startNewGame() {
		board.startnewgame();
		currentPlayer = whitePlayer;
		notifyListeners();
		turn = 1;
		moves.clear();
		previousMove = new Move(null, currentPlayer, null, board.serialize(), 0);
	}

	public boolean isOver() {
		return board.isGameOver();
	}

	public void handleTilePlacement(int x, int y) {
		if (getCurrentPlayer().getAIType() == OthelloAIType.Human)
			if (!board.isGameOver())
				if (board.MakeMove(x, y, getCurrentPlayer().isWhite)) {
					logMove(x, y);
					handleEndOfTurn();
				}
	}

	void logMove(int x, int y) {
		Move m = new Move(previousMove, currentPlayer, board.getTile(x, y),
				board.serialize(), turn);
		while (moves.size() >= turn)
			moves.remove(moves.size() - 1);
		moves.add(m);
		turn++;
		previousMove.nextMove = m;
		previousMove = m;
	}

	/**
	 * Resets game state to the turn following the given move
	 * 
	 * @param move
	 */
	public void setToMove(Move move) {
		if (move.turn > 0) {
			currentPlayer = move.player;
			turn = move.turn;
			board.overwriteFromString(move.previousMove.finalState);
			previousMove = move.previousMove;
		}
	}

	public void undoMove() {
		if (turn > 1) {
			setToMove(previousMove);
			notifyListeners();
		}
	}

	public void redoMove() {
		if (previousMove.nextMove != null){
				Move move = previousMove.nextMove;
				if (board.getLegalMoves(!move.player.isWhite).size() > 0)
					currentPlayer = move.player == whitePlayer ? blackPlayer
							: whitePlayer;
				turn = move.turn + 1;
				board.overwriteFromString(move.finalState);
				previousMove = move;
				notifyListeners();
			}
	}

	private void handleEndOfTurn() {
		// Switch players if the next player has a move. If the next player is
		// an AI, handle his turn.
		if (board.getLegalMoves(!currentPlayer.isWhite).size() > 0)
			currentPlayer = currentPlayer == whitePlayer ? blackPlayer
					: whitePlayer;
		notifyListeners();
		if (currentPlayer.getAIType() != OthelloAIType.Human && !isOver())
			handleAITurn();
	}

	private void handleAITurn() {
		long minTime = System.currentTimeMillis() + 1000;
		OthelloTile tile = currentPlayer.getNextMove(this);
		if (!board.MakeMove(tile.x, tile.y, currentPlayer.isWhite))
			throw new RuntimeException("AI came up with an illegal move!");
		logMove(tile.x, tile.y);
		while (System.currentTimeMillis() < minTime)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		notifyListeners();
		handleEndOfTurn();
	}

	public OthelloGame getCopy() {
		OthelloGame ret = new OthelloGame();
		ret.currentPlayer = currentPlayer;
		ret.whitePlayer = whitePlayer;
		ret.blackPlayer = blackPlayer;
		ret.board = board.getCopy();
		ret.turn = turn;
		return ret;
	}

	public Move getLastMove() {
		return previousMove;
	}

	@Override
	public void addGameEventListener(OthelloGameEventListener listener) {
		if (!eventListeners.contains(listener))
			eventListeners.add(listener);
	}

	@Override
	public void removeGameEventListener(OthelloGameEventListener listener) {
		if (eventListeners.contains(listener))
			eventListeners.remove(listener);

	}

	void notifyListeners() {
		for (OthelloGameEventListener listener : eventListeners)
			listener.handleGameEvent(board);
	}
}
