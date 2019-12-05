
public class BadBrain implements Brain {
/*
		public static final int ROTATE = 0;
		public static final int LEFT = 1;
		public static final int RIGHT = 2;
		public static final int DROP = 3;
		public static final int DOWN = 4;
	*/

	private int which;

	public BadBrain()
	{
		super();
		which = 0;
	}

	@Override
	public int bestMove(Board board, Piece piece, int pieceX, int pieceY, int limitHeight) {
		which++;

		if(which % 2 == 0)
			return JTetris.RIGHT;
		return JTetris.LEFT;
	}

}
