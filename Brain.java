
public interface Brain {
	/* Given a piece and a board, returns a best move int
	 	public static final int ROTATE = 0;
		public static final int LEFT = 1;
		public static final int RIGHT = 2;
		public static final int DROP = 3;
		public static final int DOWN = 4;
		*/
	int bestMove(Board board, Piece piece, int pieceX, int pieceY, int limitHeight);
	
}