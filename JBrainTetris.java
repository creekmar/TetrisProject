
public class JBrainTetris extends JTetris {
	
	private Brain brain;
	
	JBrainTetris(int width, int height) {
		super(width, height);
		brain = new LameBrain();
	}
	public void tick(int verb)
	{
		if (currentPiece != null) {
			board.undo();	// remove the piece from its old position
		}

		Brain.Move move = brain.bestMove(board, currentPiece, HEIGHT + TOP_SPACE, null);

		if(!currentPiece.equals(move.piece))
			super.tick(ROTATE);
		if(move.x < currentX)
			super.tick(LEFT);
		else if(move.x > currentX)
			super.tick(RIGHT);
		super.tick(DOWN);
	}
	
}
