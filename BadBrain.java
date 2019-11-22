
public class BadBrain implements Brain {

	@Override
	public Move bestMove(Board board, Piece piece, int limitHeight, Move move) {
		Move m = new Move();
		if (Math.random() > .5)
			m.x = 0;
		else
			m.x = board.getWidth() - 1;
		m.y = board.getColumnHeight(m.x);
		m.piece = piece.nextRotation();
		m.score = 0;
		return m;
	}

}
