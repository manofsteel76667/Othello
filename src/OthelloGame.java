import java.util.ArrayList;
import java.util.List;

class Move {
	public OthelloPlayer player;
	public OthelloTile tile;
	public String result;
	public int turn;

	public Move(OthelloPlayer player, OthelloTile tile, OthelloBoard board,
			int turn) {
		this.player = player;
		this.tile = tile;
		this.turn = turn;
		result = board.serialize();
	}

	public String toString() {
		return String.format("Turn %d: %s played tile %s", turn,
				player.getName(), tile.toString());
	}
}

public class OthelloGame implements OthelloGameEventSource {
	public OthelloGame() {
		board = new OthelloBoard(8, 8);
		eventListeners = new ArrayList<OthelloGameEventListener>();
		moves = new ArrayList<Move>();
	}

	List<OthelloGameEventListener> eventListeners;
	public List<Move> moves;
	Move lastMove;
	public int turn;
	OthelloBoard board;
	OthelloPlayer whitePlayer;
	OthelloPlayer blackPlayer;
	private OthelloPlayer currentPlayer;

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
		Move m = new Move(currentPlayer, board.getTile(x, y), board, turn);
		moves.add(m);
		turn++;
		lastMove = m;
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
		// TODO: log something if the AI comes up with an illegal move?
		if (!board.MakeMove(tile.x, tile.y, currentPlayer.isWhite))
			board.toString();
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
		return lastMove;
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
