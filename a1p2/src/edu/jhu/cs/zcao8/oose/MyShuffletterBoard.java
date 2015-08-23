package edu.jhu.cs.zcao8.oose;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import edu.jhu.cs.oose.fall2014.shuffletter.iface.Position;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterModel;


/**
 * A class that draws the board of Shuffletter.
 * 
 * @author zhihao Cao
 *
 */
public class MyShuffletterBoard extends JComponent{
	private static final int GRID_SIDE_LENGTH = 50;
	private ShuffletterModel model;
	private int boardRows;
	private int boardCols;
	private boolean isClicked;
	private int firstClickedX;
	private int firstClickedY;
	private int colsAddedFormLeft;
	private int rowsAddedFormTop;
	private Color tileColor;
	private Color letterColor;
	private Color highlightColor;
	private Color lineColor;
	
	/**
	 * Initialize all variables.
	 * 
	 * @param model
	 */
	public MyShuffletterBoard(ShuffletterModel model) {
		super();
		boardRows = 16;
		boardCols = 16;
		this.model = model;
		isClicked = false;
		firstClickedX = -1;
		firstClickedY = -1;
		colsAddedFormLeft = 0;
		rowsAddedFormTop = 0;
		tileColor = new Color(100, 0, 0);
		letterColor = Color.WHITE;
		highlightColor = new Color(209, 238, 238);
		lineColor = new Color(82, 82, 82);
	}

	/**
	 * Paint everything on the board.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//draw background
		g.setColor(Color.BLACK);
	    g.fillRect(0, 0, getWidth(), getHeight());
	    
	    ImageIcon icon = new ImageIcon("files" + File.separator + "board_background.jpg"); 
		icon.setImage(icon.getImage().getScaledInstance(800, 800, Image.SCALE_DEFAULT));
		g.drawImage(icon.getImage(), 0, 0, this);
	    
		drawLineForBoard(g);
		
		//draw tiles on the board
		for (Position pos : model.getTilePositions()) {
			char letter = model.getTile(pos).getLetter();
			if (letter == '?') letter = ' ';
			int x = pos.getX() + colsAddedFormLeft;
			int y = - pos.getY() + rowsAddedFormTop;
			g.setColor(tileColor);
			g.fillRect(x * GRID_SIDE_LENGTH + 1, y * GRID_SIDE_LENGTH + 1, GRID_SIDE_LENGTH - 1, GRID_SIDE_LENGTH - 1);
			drawLetter(g, letter, x * GRID_SIDE_LENGTH + 1, y * GRID_SIDE_LENGTH + 1, GRID_SIDE_LENGTH - 1, GRID_SIDE_LENGTH - 1);
		}
		
		hightlightClickedTile(g);
		
			
	}
	
	/**
	 * Return the preferred size of the board.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(boardCols * GRID_SIDE_LENGTH, boardRows * GRID_SIDE_LENGTH);
//		return new Dimension(800, 800);
	}
	
	/**
	 * Draw lines on the board.
	 * @param g		the Graphics we draw on
	 */
	private void drawLineForBoard(Graphics g) {
		g.setColor(lineColor);
		
		for (int i = 0; i < Math.max(boardCols, boardRows); i++) {
			g.drawLine(i * GRID_SIDE_LENGTH, 0, i * GRID_SIDE_LENGTH, boardRows * GRID_SIDE_LENGTH);
			g.drawLine(0 , i * GRID_SIDE_LENGTH, boardCols * GRID_SIDE_LENGTH, i * GRID_SIDE_LENGTH);
		}
		
		
		g.drawLine(boardCols * GRID_SIDE_LENGTH - 1, 0, boardCols * GRID_SIDE_LENGTH - 1, boardRows * GRID_SIDE_LENGTH);
		g.drawLine(0, boardRows * GRID_SIDE_LENGTH - 1, boardCols * GRID_SIDE_LENGTH, boardRows * GRID_SIDE_LENGTH - 1);
	}
	
	

	/**
	 * Draw a letter in the middle of the grid.
	 * 
	 * @param g			the Graphics we draw on
	 * @param letter	the letter we wish to draw	
	 * @param x			upper left corner's x coordinate
	 * @param y			upper left corner's y coordinate
	 * @param width		width of a tile on the board
	 * @param height	height of a tile on the board
	 */
	private void drawLetter(Graphics g, char letter, int x, int y, int width, int height){
		g.setColor(letterColor);
        Font font = new Font("Helvetica", Font.BOLD, 24);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);  
        int textWidth = fm.stringWidth(letter +"");
        int x1 = (width - textWidth) / 2;
        int y1 = (height - (fm.getAscent() + fm.getDescent())) / 2 + fm.getAscent();
        g.drawString(letter + "", x + x1, y + y1);
	}
	
	/**
	 * Return the side length of a grid
	 * @return
	 */
	public int getGirdSideLength() {
		return GRID_SIDE_LENGTH;
	}
	
	/**
	 * Record first click information.
	 * 
	 * @param mouseX
	 * @param mouseY
	 */
	public void setFirstClicked(int mouseX, int mouseY) {
		firstClickedX = mouseX / GRID_SIDE_LENGTH;
		firstClickedY = mouseY / GRID_SIDE_LENGTH;
		isClicked = true;
	}
	
	/**
	 * Determine whether the board is clicked
	 * @return
	 */
	public boolean isClicked() {
		return isClicked;
	}
	
	/**
	 * Return the x coordinate of first clicked grid.
	 * @return
	 */
	public int getFirstClickedX() {
		return firstClickedX;
	}
	
	/**
	 * Return the y coordinate of first clicked grid.
	 * @return
	 */
	public int getFirstClickedY() {
		return firstClickedY;
	}
	/**
	 * Get the number of cols added from the left
	 * @return
	 */
	public int getColsAddedFromLeft() {
		return colsAddedFormLeft;
	}
	/**
	 * Get the number of rows added from the top
	 * @return
	 */
	public int getRowsAddedFromTop() {
		return rowsAddedFormTop;
	}
	
	/**
	 * Extend the board if needed.
	 * 
	 * @param boardX		x coordinate on the board
	 * @param boardY		y coordinate on the board
	 */
	public void extendBoard(int boardX, int boardY) {
		int width = boardCols * GRID_SIDE_LENGTH;
		int height = boardRows * GRID_SIDE_LENGTH;
		if (boardX == 0) {
			boardCols ++;
			colsAddedFormLeft ++;
			setSize(new Dimension(width, height));
			
		} else if (boardX == boardCols - 1) {
			boardCols ++;
			setSize(new Dimension(width, height));
			
		}
		if (boardY == 0) {
			boardRows ++;
			rowsAddedFormTop ++;
			setSize(new Dimension(width, height));
		} else if (boardY == boardRows - 1) {
			boardRows ++;
			setSize(new Dimension(width, height));
		}
		revalidate();
		
	}
	
	/**
	 * Highlight the selected tile.
	 * 
	 * @param g		the Graphics we draw on
	 */
	private void hightlightClickedTile(Graphics g) {
		g.setColor(highlightColor);
		BasicStroke stroke = new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		Graphics2D g2d=(Graphics2D) g;
		g2d.setStroke(stroke);
		g2d.drawRect(firstClickedX * GRID_SIDE_LENGTH + 1, firstClickedY * GRID_SIDE_LENGTH + 1, GRID_SIDE_LENGTH - 1, GRID_SIDE_LENGTH - 1);
	}
	/**
	 * Clear records of last click.
	 */
	public void clearClicked() {
		isClicked = false;
		firstClickedX = -1;
		firstClickedY = -1;
	}
}
