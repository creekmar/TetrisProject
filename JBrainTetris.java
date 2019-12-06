import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JBrainTetris extends JTetris {

	private static final int movesPerTick = 1;
	private Brain brain;
	protected javax.swing.Timer brainTimer;

	JBrainTetris(int width, int height) {
		super(width, height);
		brain = new MingBrain();

		brainTimer = new javax.swing.Timer(DELAY/movesPerTick, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeBrainMove();
			}
		});
	}

	@Override
	public void startGame()
	{
		super.startGame();
		brainTimer.start();
	}

	@Override
	public void stopGame()
	{
		brainTimer.stop();
		super.stopGame();
	}

	@Override
	public void updateTimer()
	{
		super.updateTimer();
		double value = ((double)speed.getValue())/speed.getMaximum();
		brainTimer.setDelay((int)(DELAY - value*DELAY)/movesPerTick);
	}

	@Override
	public void tick(int verb)
	{
		moved = false;
		super.tick(verb);
	}

	public void executeBrainMove()
	{
		if (currentPiece != null) {
			board.undo();	// remove the piece from its old position
		}	
		int verb = brain.bestMove(board,currentPiece,currentX,currentY,HEIGHT);
		tick(verb);
	}

}







