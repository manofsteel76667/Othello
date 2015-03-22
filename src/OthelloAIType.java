/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.EnumMap;
import java.util.Map;

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
	private int lookahead;
	private String description;
	private static final Map<OthelloAIType, String> labels = new EnumMap<OthelloAIType, String>(
			OthelloAIType.class);

	static {
		for (OthelloAIType ai: OthelloAIType.values())
			labels.put(ai, ai.description);
	}

	public Map<OthelloAIType, String> getMap() {
		return labels;
	}

	public String getDescription() {
		return this.description;
	}

	public static final OthelloAIType fromDescription(String from) {
		for (Map.Entry<OthelloAIType, String> entry : labels.entrySet())
			if (entry.getValue().equals(from))
				return entry.getKey();
		return null;
	}

	public int getLookAhead() {
		return this.lookahead;
	}
}
