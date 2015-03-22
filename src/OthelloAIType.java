/**
 * 
 * @author Anderson
 */
public enum OthelloAIType {

	Human(0, "Human"), 
	Silly(0, "Silly"), 
	Easy(1, "Easy"), 
	Average(2, "Average"), 
	Hard(3, "Hard"), 
	Cyborg(4, "Cyborg");
	
	OthelloAIType(int _lookahead, String _description){
		lookahead = _lookahead;
		description = _description;
	}
	public final int lookahead;
	public final String description;
}
