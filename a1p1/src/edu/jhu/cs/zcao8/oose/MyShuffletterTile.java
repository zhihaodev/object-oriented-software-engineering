package edu.jhu.cs.zcao8.oose;

import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterTile;

/**
 * A class implementing ShuffletterTile interface.
 *<p>
 * This is intended for OOSE assignment 1 part I.
 * 
 * @author Zhihao Cao 
 */
public class MyShuffletterTile implements ShuffletterTile {
	private char letter;
	
	/**
	 * Initialize the tile's letter with the input letter.
	 */
	public MyShuffletterTile(char tileLetter) {
		letter = tileLetter;
	}
	
	/**
	 * Get the letter of the tile.
	 */
	@Override
	public char getLetter() {
		return letter;
	}

	/**
	 * Determine if the tile's letter is Wild(' ').
	 */
	@Override
	public boolean isWild() {
		return letter == ' ';
	}

}
