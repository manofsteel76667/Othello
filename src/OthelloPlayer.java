/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author Anderson
 */
public class OthelloPlayer {

	private OthelloAIType aiType;
	//private OthelloTileState side;
	private String name;

	public OthelloPlayer(String _name, OthelloAIType _type, boolean _isWhite) {
		setName(_name);
		setAIType(_type);
		isWhite = _isWhite;
	}

	public OthelloTile getNextMove(OthelloGame game) {
		return DecisionNode.getBestMove(game, this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public OthelloAIType getAIType() {
		return aiType;
	}

	public void setAIType(OthelloAIType AItype) {
		this.aiType = AItype;
	}

	//public OthelloTileState getSide() {
	//	return side;
	//}

	//public void setSide(OthelloTileState side) {
	//	this.side = side;
	//}
	
	public boolean isWhite;
}
