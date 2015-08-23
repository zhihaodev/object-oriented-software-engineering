package edu.jhu.cs.zcao8.oose;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterModel;
/**
 * A class that draws the supply of Shuffletter.
 * 
 * @author zhihao Cao
 *
 */
public class MyShuffletterSupply extends JPanel{
	private static final int SUPPLY_COLS = 2;
	private static final int SUPPLY_ROWS = 16;
	private ShuffletterModel model;
	private int tileClicked;
	private boolean isclicked;
	private Color tileColor;
	private Color supplyBackgroundColor;
	private Color highlightColor;
	private Color letterColor;
	/**
	 * Initialize all variables.
	 * 
	 * @param model			the underlying model of this GUI
	 */
	public MyShuffletterSupply(ShuffletterModel model) {
		super();
		this.model = model;
		isclicked = false;
		tileClicked = -1;
		letterColor = Color.WHITE;
		tileColor = new Color(100, 0, 0);
		supplyBackgroundColor = new Color(139, 134, 130);
		highlightColor = new Color(209, 238, 238);
	}
	/**
	 * Paint everything on the supply.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//Draw background
		g.setColor(supplyBackgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		int width = getWidth() / SUPPLY_COLS;
		int height = getHeight() / SUPPLY_ROWS;
		drawLineForSupply(g);
		drawSupply(g, width, height);
		if (isclicked)
			highLightClickedTile(g, width, height);
	}
	
	/**
	 * Return the preferred size of the supply.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 800);
	}

	/**
	 * Draw lines on the supply.
	 * 
	 * @param g		the Graphics we draw on
	 */
	private void drawLineForSupply(Graphics g) {
		
		int width = getWidth() / SUPPLY_COLS;
		int height = getHeight() / SUPPLY_ROWS;
		g.setColor(Color.BLACK);
		for (int i = 0; i <= SUPPLY_ROWS; i++) {
			
			g.drawLine(0, i * height, getWidth(), i * height);
		}
		g.drawLine(0, 0, 0, getHeight());
		g.drawLine(width, 0, width, getHeight() / SUPPLY_ROWS * SUPPLY_ROWS);
	}
	/**
	 * Determine which tile was clicked last time.
	 * 
	 * @return		index of the clicked tile
	 */
	public int getTileClicked() {
		return tileClicked;
	}

	/**
	 * Determine if any tile was clicked.
	 * 
	 * @return		whether any tile was clicked
	 */
	public boolean isClicked() {
		return this.isclicked;
	}

	/**
	 * Clear records of last click.
	 */
	public void clearClicked() {
		this.tileClicked = -1;
		this.isclicked = false;
	}
	
	/**
	 * record this click information or clear last one.
	 * 
	 * @param mouseX	the x coordinate of mouse click location
	 * @param mouseY	the y coordinate of mouse click location
	 */
	public void supplyClicked(int mouseX, int mouseY) {
		
		int width = getWidth() / SUPPLY_COLS;
		int height = getHeight() / SUPPLY_ROWS;
		int nowClicked = mouseX / width * SUPPLY_ROWS + mouseY / height;
		if (nowClicked != getTileClicked() && nowClicked < model.getSupplyContents().size()) {
			this.tileClicked = nowClicked;
			this.isclicked = true;
		}
		else {
			this.clearClicked();
		}
	}
	
	/**
	 * Highlight the selected tile.
	 * 
	 * @param g			the Graphics we draw on
	 * @param width		width of a tile on the supply
	 * @param height	height of a tile on the supply
	 */
	private void highLightClickedTile(Graphics g, int width, int height) {
		BasicStroke stroke = new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		g.setColor(highlightColor);
		Graphics2D g2d=(Graphics2D) g;
		g2d.setStroke(stroke);
		g2d.drawRect(tileClicked / SUPPLY_ROWS * width + 1, tileClicked % SUPPLY_ROWS * height + 1, width - 1, height - 1);
	}
	
	/**
	 * Draw tiles and their letters.
	 * 
	 * @param g			the Graphics we draw on
	 * @param width		width of a tile on the supply
	 * @param height	height of a tile on the supply
	 */
	private void drawSupply(Graphics g, int width, int height) {
		
		for (int i = 0; i < model.getSupplyContents().size(); i++) {
			char letter = model.getSupplyContents().get(i).getLetter();
			if (model.getSupplyContents().get(i).isWild()) letter = ' ';
			g.setColor(tileColor);
			g.fillRect(i / SUPPLY_ROWS * width + 1, i % SUPPLY_ROWS * height + 1, width - 1, height - 1);
			drawLetter(g,letter, i / SUPPLY_ROWS * width + 1, i % SUPPLY_ROWS * height + 1, width - 1, height - 1);
		}

	}
	
	/**
	 * Draw a letter in the middle of the grid.
	 * 
	 * @param g			the Graphics we draw on
	 * @param letter	the letter we wish to draw	
	 * @param x			upper left corner's x coordinate
	 * @param y			upper left corner's y coordinate
	 * @param width		width of a tile on the supply
	 * @param height	height of a tile on the supply
	 */
	private void drawLetter(Graphics g, char letter, int x, int y, int width, int height){
		g.setColor(letterColor);
        Font font = new Font("Helvetica", Font.PLAIN, 20);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);  
        int textWidth = fm.stringWidth(letter +"");
        int offSetX = (width - textWidth) / 2;
        int offSetY = (height - (fm.getAscent() + fm.getDescent())) / 2 + fm.getAscent();
        g.drawString(letter + "", x + offSetX, y + offSetY);
	}
	
 }
