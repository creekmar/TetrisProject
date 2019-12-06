
public class MingBrain implements Brain {

	/**
	 Given a piece and a board, returns a move object that represents
	 the best play for that piece, or returns null if no play is possible.
	 See the Brain interface for details.
	*/
	
		@Override
		public int bestMove(Board board, Piece piece, int pieceX, int pieceY, int limitHeight)  {

			double bestScore = 1e20;
			int bestX = 0;
			int bestY = 0;
			Piece bestPiece = null;
			Piece current = piece;
			
			// loop through all the rotations
			while (true) {
				final int yBound = limitHeight - current.getHeight()+1;
				final int xBound = board.getWidth() - current.getWidth()+1;
				
				// For current rotation, try all the possible columns
				for (int x = 0; x<xBound; x++) {
					int y = board.dropHeight(current, x);
					if (y<yBound) {	// piece does not stick up too far
						int result = board.place(current, x, y);
						if (result <= Board.PLACE_ROW_FILLED) {
							if (result == Board.PLACE_ROW_FILLED) board.clearRows();
							
							double score = rateBoard(board, x, y, current.getWidth(), current.getHeight());
							
							if (score<bestScore) {
								bestScore = score;
								bestX = x;
								bestY = y;
								bestPiece = current;
							}
						}
						
						board.undo();	// back out that play, loop around for the next
					}
				}
				
				current = current.nextRotation();
				if (current == piece) break;	// break if back to original rotation
			}
			
			if (bestPiece == null) return(JTetris.DOWN);	// could not find a play at all!

			if(!piece.equals(bestPiece))
				return JTetris.ROTATE;
			if(bestX == pieceX)
				return JTetris.DOWN;
			if(bestX < pieceX)
				return JTetris.LEFT;
			else
				return JTetris.RIGHT;

		}
		
		/*
		 A simple brain function.
		 Given a board, produce a number that rates
		 that board position -- larger numbers for worse boards.
		 This version just counts the height
		 and the number of "holes" in the board.
		 See Tetris-Architecture.html for brain ideas.
		*/
		public double rateBoard(Board board, int j, int h, int pwidth, int pheight) {
			final int width = board.getWidth();
			final int maxHeight = board.getMaxHeight();
			
			int sumHeight = 0;
			int holes = 0;
			int lhole = 0;
			int rhole = 0;
			int close = 0;
			
			// Count the holes, and sum up the heights
			for (int x=0; x<width; x++) {
				final int colHeight = board.getColumnHeight(x);
				sumHeight += colHeight;
				
				int y = colHeight - 2;	// addr of first possible hole
				
				while (y>=0) {
					if  (!board.getGrid(x,y)) {
						if((x>=j-2 && x<=j+pwidth+2) && (y>=j-2 && y<=j+pheight+2))
							holes++;
						holes++;
						if(x<5)
							lhole++;
						else
							rhole++;
					}
					if(board.getRowWidth(y)>7);
						close++;
					y--;
				}
			}
			
			double avgHeight = ((double)sumHeight)/width;
		//	8*maxHeight + 40*avgHeight + 
			// Add up the counts to make an overall score
			// The weights, 8, 40, etc., are just made up numbers that appear to work
			return (10*maxHeight + 40*avgHeight + holes + .5*Math.abs(rhole-lhole)-2*close);	
		}






}
