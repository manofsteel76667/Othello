/**
 * 
 * @author Anderson
 */
public enum TileDirection {

	Left(-1, 0), UpLeft(-1, -1), Up(0, -1), UpRight(1, -1), Right(1, 0), DownRight(
			1, 1), Down(0, 1), DownLeft(-1, 1);

	TileDirection(int _xdiff, int _ydiff) {
		xdiff = _xdiff;
		ydiff = _ydiff;
	}

	public final int ydiff;
	public final int xdiff;
}
