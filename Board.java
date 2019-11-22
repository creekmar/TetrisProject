import java.awt.*;
import java.util.*;


/**
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearning.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Intead,
 just represents the abtsract 2-d board.
  See Tetris-Architecture.html for an overview.
  
 This is the starter file version -- a few simple things are filled in already
  
 @author	Nick Parlante
 @version	1.0, Mar 1, 2001
*/
public final class Board  {
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean[][] backupGrid;
	
	private boolean committed;
		
	private static final boolean DEBUG = true;
	
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int aWidth, int aHeight) {
		width = aWidth;
		height = aHeight;
		
		committed = true;

		grid = new boolean[width][height];
		backupGrid = new boolean[width][height];
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	
		int max = 0;
		for(int i = 0; i < width; i++) {
			int colHeight = getColumnHeight(i);
			if(colHeight > max)
				max = colHeight;
		}
		return max;
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			//TODO
			// consistency check the board state
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		
		int max = 0;
		
		for(int i = 0; i < skirt.length;i++) {
			int num = getColumnHeight(i+x) - skirt[i];
			if(num > max) {
				max = num;
			}
		}
		
		return max;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
	    for(int i = grid[x].length - 1; i >= 0; i--)
	    {
	      if(grid[x][i])
	        return i+1;
	    }
		return 0;
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		int sum = 0;
		for(int i = 0; i < grid.length; i++) {
			if(grid[i][y])
				sum++;
		}
		return sum;
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public final boolean getGrid(int x, int y) {
		return grid[x][y];
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_OF_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 If part of the piece would fall out of bounds, the placement
	 does not change the board at all, and PLACE_OUT_BOUNDS is returned.
	 If the placement is "bad" --interfering with existing blocks in the grid --
	 then the placement is halted partially complete and PLACE_BAD is returned.
	 An undo() will remove the bad placement.
	*/
	public int place(Piece piece, int x, int y) {
		committed = false;
		for(int i = 0; i < backupGrid.length; i++) {
			System.arraycopy(grid[i], 0, backupGrid[i], 0, grid[i].length);
		}
		
		
		Point[] body = piece.getBody();
		
		if(x < 0 || y < 0 || x + piece.getWidth() > width || y + piece.getHeight() > height) {
			return PLACE_OUT_OF_BOUNDS;
		}
		for(Point p: body) {
			int dx = p.x;
			int dy = p.y;
			int newX = p.x + x;
			int newY = p.y + y;
			if (grid[newX][newY]) {
				return PLACE_BAD;
			}
			else
				grid[newX][newY] = true;
		}
		
		for(Point p: body)
			if(getRowWidth(y + p.y) == width) {
				return PLACE_ROW_FILLED;
			}
		return PLACE_OK;
	}

	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns true if any row clearing happened.
	 
	 <p>Implementation: This is complicated.
	 Ideally, you want to copy each row down
	 to its correct location in one pass.
	 Note that more than one row may be filled.
	*/
	public boolean clearRows() {
	    boolean removed = false;
			int toRow = 0;
	    int fromRow = 0;
	    while(toRow < height){
	      while(fromRow < height && getRowWidth(fromRow) == width)
	        fromRow++;

	      if(fromRow >= height){
	        for(int i = 0; i < width; i++){
	          grid[i][toRow] = false;
	        }
	        removed = true;
	      }else{
	        for(int i = 0; i < width; i++){
	          grid[i][toRow] = grid[i][fromRow];
	        }
	      }
	      toRow++;
	      fromRow++;
	    }

	    return removed;
	}



	/**
	 If a place() happens, optionally followed by a clearRows(),
	 a subsequent undo() reverts the board to its state before
	 the place(). If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if(!committed)
		{
			boolean[][] temp = grid;
			grid = backupGrid;
			backupGrid = temp;
			committed = !committed;
		}
	}
	
	
	/**
	 Puts the board in the committed state.
	 See the overview docs.
	*/
	public void commit() {
		committed = true;
	}
}
