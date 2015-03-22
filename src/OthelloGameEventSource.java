
public interface OthelloGameEventSource {
	void addGameEventListener(OthelloGameEventListener listener);
	void removeGameEventListener(OthelloGameEventListener listener);
}
