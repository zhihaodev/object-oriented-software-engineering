package edu.jhu.cs.zcao8.oose;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;



import java.util.Vector;

import edu.jhu.cs.oose.fall2014.shuffletter.iface.Position;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterIllegalMoveEvent;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterModel;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterModelListener;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterTile;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterTileMovedEvent;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterTilePlayedEvent;



/**
 * A class implementing ShuffletterModel interface, serves as underlying model for Shuffletter GUI.
 *<p>
 * This is intended for OOSE assignment 1 part I.
 * 
 * @author	Zhihao Cao
 */
public class MyShuffletterModel implements ShuffletterModel{
	//													 'A' 'B''C''D''E' 'F''G''H''I' 'J''K''L''M''N'
	private static final int[] INITIAL_LETTERTILES_SET = {13, 3, 3, 6, 18, 3, 4, 3, 12, 2, 2, 5, 3, 8, 
	// 'O' 'P''Q''R''S''T''U''V''W''X''Y''Z'wild
		11, 3, 2, 9, 6, 9, 6, 3, 3, 2, 3, 2, 1};
	private static final int INITIAL_SUPPLY_NUMBER = 21;
	private String AUTHORNAME = "Zhihao Cao"; 

	private int bagCount;
	private List<ShuffletterTile> bagContents ;
	private Set<String> legalWords;
	private Vector<ShuffletterModelListener> listenersList;
	private List<ShuffletterTile> supplyContents;
	private HashMap<Position, ShuffletterTile> tilePositions;
	private boolean isGameEnded;
	private boolean IfMoveCorrectly;
	int labelUsed;

	/**
	 * Read in words from wordlist.txt.
	 */
	private void prepareLegalWords() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("files" + File.separator + "wordlist.txt"));
			String line = "";
			legalWords = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				line = line.toUpperCase();
				legalWords.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load tiles according to the initial distribution, and randomly pick certain number of tiles to the supply.
	 */
	private void loadTilesAndSupply() {
		bagCount = 0;
		for (int i = 0; i < INITIAL_LETTERTILES_SET.length; i++) {
			for (int j = 0; j < INITIAL_LETTERTILES_SET[i]; j++) {
				char letter = i != INITIAL_LETTERTILES_SET.length - 1 ? (char)(i + 'A'): ' ';
				ShuffletterTile tile = new MyShuffletterTile(letter);
				bagContents.add(tile);
			}
			bagCount += INITIAL_LETTERTILES_SET[i];

		}
		//pick tiles at random to the supply
		Random r = new Random();
		for (int i = 0; i < INITIAL_SUPPLY_NUMBER; i++) {
			int tilePicked = r.nextInt(bagCount);
			supplyContents.add(bagContents.get(tilePicked));
			bagContents.remove(tilePicked);
			bagCount --;
		}
	}
	/**
	 * Given a tile, get its label according to its adjacent tiles in following rules(similar to determining connected components in computer vision):
	 * <ui>
	 * <li> If the tile is not connected to any labeled tiles, it receives a new label;
	 * <li> If the tile is connected to tiles with same label, it receives that label;
	 * <li> If the tile is connected to tiles with multiple labels, labels it with one of them and records their equivalent relationship.
	 * </ui>
	 * 
	 * @param i					the x-coordinate of the tile				
	 * @param j					the y-coordinate of the tile
	 * @param xMin				the minimum of x of all tiles
	 * @param yMin				the minimum of y of all tiles
	 * @param xMax				the maximum of x of all tiles
	 * @param yMax				the maximum of y of all tiles
	 * @param tilePos			the position of the tile
	 * @param positionLabel		the records of tiles' positions and labels
	 * @param equivalentLabel	the records of equivalent labels
	 */
	private void getLabelFromAjacentTiles(int i, int j, int xMin, int yMin, int xMax, int yMax, Position tilePos,HashMap<Position, Integer> positionLabel,
			ArrayList<Set<Integer>> equivalentLabel) {
		ArrayList<Position> AdjacentTiles = new ArrayList<Position>();
		if (j + 1 <= yMax) AdjacentTiles.add(new Position(i, j + 1));
		if (j - 1 >= yMin) AdjacentTiles.add(new Position(i, j - 1));
		if (i - 1 >= xMin) AdjacentTiles.add(new Position(i - 1, j));
		if (i + 1 <= xMax) AdjacentTiles.add(new Position(i + 1, j));
		int label1 = 0;
		int label2 = 0;
		int labelCount = 0;
		
		
		for (Position pos : AdjacentTiles) {
			if (pos != null && positionLabel.containsKey(pos)) {
				label1 = positionLabel.get(pos);
				if (label1 != label2) {
					labelCount ++;
					//record that label1 is equivalent to label2
					if (labelCount > 1) {
						if (equivalentLabel.isEmpty()) {
							Set<Integer> labelSet = new HashSet<Integer>();
							labelSet.add(label1);
							labelSet.add(label2);
							equivalentLabel.add(labelSet);
						} else {
							for (int k = 0; k < equivalentLabel.size(); k++) {
								Set<Integer> labelSet = equivalentLabel.get(k);
								//if label1 or label2 is contained in the sets
								if (labelSet.contains(label1) || labelSet.contains(label2)) {
									labelSet.add(label1);
									labelSet.add(label2);
								} else {
									//if both label1 and label2 are not contained in the sets
									Set<Integer> newLabelSet = new HashSet<Integer>();
									newLabelSet.add(label1);
									newLabelSet.add(label2);
									equivalentLabel.add(newLabelSet);
								}
								int l = k + 1;
								//merge these sets if they have same elements
								while (l < equivalentLabel.size()) {
									Set<Integer> labelSetNext = equivalentLabel.get(l);
									if (labelSet.removeAll(labelSetNext)) {
										labelSet.addAll(labelSetNext);
										equivalentLabel.remove(l);
									}
									l ++;
								}
							}
						}

					}
				}
				label2 = label1;
			}
		}
		//label the tile
		if (labelCount >= 1) {
			positionLabel.put(tilePos, label2); 
		}else {
			positionLabel.put(tilePos, labelUsed + 1); 
			labelUsed ++; 

		}
	}

	/**
	 * Check all the words' correctness. If a word contains a Wild(' '), leave it to later process.
	 * 
	 * @param word					the word represented by tiles
	 * @param wordUsed				the records of used words
	 * @param wordsWithWildLetter	the records of words containing ' '
	 * @param illegalMoveEvent		the event of ShuffletterTilePlayedEvent, representing certain illegal move
	 * @return						updated version of illegalMoveEvent	
	 */
	private ShuffletterIllegalMoveEvent checkWords(String word, ArrayList<String> wordUsed, ArrayList<String>wordsWithWildLetter, ShuffletterIllegalMoveEvent illegalMoveEvent) {
			if (!word.contains(" ")) {
				if (!legalWords.contains(word) || wordUsed.contains(word)) {
					illegalMoveEvent = new ShuffletterIllegalMoveEvent("Illigal word: " + word);
				} else {
					wordUsed.add(word);
				}
			} else {
				wordsWithWildLetter.add(word);
			}		
		return illegalMoveEvent;
	}
	
	/**
	 * Check all the words with ' ', determine whether the ' ' leads to illegal words.
	 * 
	 * @param wordsWithWildLetter	the records of words containing ' '
	 * @param wordUsed				the records of used words
	 * @param illegalMoveEvent		the event of ShuffletterTilePlayedEvent, representing certain illegal move
	 * @return						updated version of illegalMoveEvent
	 */
	private ShuffletterIllegalMoveEvent checkWordsWithWildLetter(ArrayList<String>wordsWithWildLetter, ArrayList<String> wordUsed,
			ShuffletterIllegalMoveEvent illegalMoveEvent) {
		if (wordsWithWildLetter != null) {
			boolean isWildLetterFit = true;
			ArrayList<String> wildWordUsed = new ArrayList<String>();
			//replace ' ' with a letter from 'A' to 'Z'
			for (char letter = 'A'; letter <= 'Z'; letter ++) {
				isWildLetterFit = true;
				wildWordUsed.clear();
				for (String word : wordsWithWildLetter) {
					word = word.replace(' ', letter);
					if (!legalWords.contains(word) || wordUsed.contains(word) || wildWordUsed.contains(word)) {
						isWildLetterFit = false;
					} else {
						wildWordUsed.add(word);
					}
				}
				if (isWildLetterFit) break;

			}
			//give warning to users
			if(!isWildLetterFit) {
				String message = "Illegal word(s): ";
				for (String string : wordsWithWildLetter) {
					string = string.replace(' ', '?');
					message += string + "  ";
				}
				illegalMoveEvent = new ShuffletterIllegalMoveEvent(message);
			}
		}
		return illegalMoveEvent;
	}

	/**
	 * Check whether all the words are connected.
	 * 
	 * @param equivalentLabel		the records of equivalent labels
	 * @param illegalMoveEvent		the event of ShuffletterTilePlayedEvent, representing certain illegal move
	 * @return						updated version of illegalMoveEvent
	 */
	private ShuffletterIllegalMoveEvent checkWordsConnectivity(ArrayList<Set<Integer>> equivalentLabel, ShuffletterIllegalMoveEvent illegalMoveEvent) {
		//All words are connected when there's only one set containing all labels in the equivalentLabel.
		if (equivalentLabel.size() != 1 || equivalentLabel.size() == 1 && equivalentLabel.get(0).size() < labelUsed)
			illegalMoveEvent = new ShuffletterIllegalMoveEvent("Tiles are not connected.");
		
		return illegalMoveEvent;
	}
	
	/**
	 * Determine whether we should end the round or the game, or warn the user about the illegal moves.
	 * 
	 * @param illegalMoveEvent		the event of ShuffletterTilePlayedEvent, representing certain illegal move
	 */
	private void endRoundOrGame(ShuffletterIllegalMoveEvent illegalMoveEvent) {
		IfMoveCorrectly = illegalMoveEvent == null;
		if (!IfMoveCorrectly) {
			for (ShuffletterModelListener listener : listenersList)
				listener.illegalMoveMade(illegalMoveEvent);
		} else {
			if (!isGameOver()) {
				//end this round, prepares for next round
				Random r = new Random();
				int tilePicked = r.nextInt(bagCount);
				supplyContents.add(bagContents.get(tilePicked));
				bagContents.remove(tilePicked);
				bagCount --;
				for (ShuffletterModelListener listener : listenersList)
					listener.roundEnded();
			} else {
				//end the game
				isGameEnded = true;
				for (ShuffletterModelListener listener : listenersList)
					listener.gameEnded();
			} 
		}
		

	}
	
	/**
	 * Scan the tiles from left to right, check horizontal words' correctness and label the tiles.
	 * 
	 * @param xMin					the minimum of x of all tiles
	 * @param yMin					the minimum of y of all tiles
	 * @param xMax					the maximum of x of all tiles
	 * @param yMax					the maximum of y of all tiles
	 * @param positionLabel			the records of tiles' positions and labels
	 * @param equivalentLabel		the records of equivalent labels
	 * @param wordUsed				the records of used words
	 * @param wordsWithWildLetter	the records of words containing ' '
	 * @param illegalMoveEvent		the event of ShuffletterTilePlayedEvent, representing certain illegal move
	 * @return						updated version of illegalMoveEvent
	 */
	private ShuffletterIllegalMoveEvent scanFromLeftToRight(int xMin, int yMin, int xMax, int yMax, HashMap<Position, Integer> positionLabel,
			ArrayList<Set<Integer>> equivalentLabel, ArrayList<String> wordUsed, ArrayList<String>wordsWithWildLetter,
			ShuffletterIllegalMoveEvent illegalMoveEvent) { 
		String word;
		
		////put label '1' into a set
		Set<Integer> equivalentSet = new HashSet<Integer>();
		equivalentSet.add(1);
		equivalentLabel.add(equivalentSet);
		
		
		for (int j = yMax; j >= yMin; j--) {
			word = "";
			for (int i = xMin; i <= xMax; i++) {
				Position tilePos = new Position(i, j);
				ShuffletterTile tile = tilePositions.get(tilePos);
				if (tile != null) {
					word += tile.getLetter();
					//check words before moving to next line
					if (word.length() > 1 && i == xMax) {
						illegalMoveEvent = checkWords(word, wordUsed, wordsWithWildLetter, illegalMoveEvent);
					}
					getLabelFromAjacentTiles(i, j, xMin, yMin, xMax, yMax, tilePos, positionLabel, equivalentLabel);
				} else 	{
					//check words when we meet a position without tiles
					if (word.length() > 1) {
						illegalMoveEvent = checkWords(word, wordUsed, wordsWithWildLetter, illegalMoveEvent);
					}
					word = "";
				}
			}
		}
		return illegalMoveEvent;
	}

	/**
	 * Scan the tiles from top to bottom, check vertical  words' correctness.
	 * 
	 * @param xMin					the minimum of x of all tiles
	 * @param yMin					the minimum of y of all tiles
	 * @param xMax					the maximum of x of all tiles
	 * @param yMax					the maximum of y of all tiles
	 * @param positionLabel			the records of tiles' positions and labels
	 * @param equivalentLabel		the records of equivalent labels
	 * @param wordUsed				the records of used words
	 * @param wordsWithWildLetter	the records of words containing ' '
	 * @param illegalMoveEvent		the event of ShuffletterTilePlayedEvent, representing certain illegal move
	 * @return						updated version of illegalMoveEvent
	 */
	private ShuffletterIllegalMoveEvent scanFromTopToBottom(int xMin, int yMin, int xMax, int yMax, HashMap<Position, Integer> positionLabel,
			ArrayList<Set<Integer>> equivalentLabel, ArrayList<String> wordUsed, ArrayList<String>wordsWithWildLetter,
			ShuffletterIllegalMoveEvent illegalMoveEvent) {
		String word;
		for(int i = xMin; i <= xMax; i++) {
			word = "";
			for (int j = yMax; j >= yMin; j--) {
				Position tilePos = new Position(i, j);
				ShuffletterTile tile = tilePositions.get(tilePos);
				if (tile != null) {
					word += tile.getLetter();
					//check words before moving to next line
					if (word.length() > 1 && j == yMin) {
						illegalMoveEvent = checkWords(word, wordUsed, wordsWithWildLetter, illegalMoveEvent);
					}
				} else {
					//check words when we meet a position without tiles
					if (word.length() > 1) {
						illegalMoveEvent = checkWords(word, wordUsed, wordsWithWildLetter, illegalMoveEvent);
					}
					word = "";
				}
			}
		}
		return illegalMoveEvent;
	}

	/**
	 * Initialize variables, read in words and load tiles.
	 */
	public MyShuffletterModel() {
		bagContents = new ArrayList<ShuffletterTile>();
		legalWords = new HashSet<String>();
		listenersList = new Vector<ShuffletterModelListener>();
		supplyContents = new ArrayList<ShuffletterTile>();
		tilePositions = new HashMap<Position, ShuffletterTile>();		
		isGameEnded = false;
		IfMoveCorrectly = true;
		labelUsed = 1;
		prepareLegalWords();
		loadTilesAndSupply();
	}

	/**
	 * Add an event listener to this model.
	 */
	@Override
	public void addListener(ShuffletterModelListener listener) {
		listenersList.add(listener);
	}

	/**
	 * Use to indicate that the player wishes to end the current round. 
	 * Whether the move is legal or illegal, an appropriate event is raised. 
	 * Ending the round is illegal, for instance, when the tiles are not touching or when words on the board are not correctly spelled.
	 */
	@Override
	public void endRound() {
		ShuffletterIllegalMoveEvent illegalMoveEvent = null;
		ArrayList<String> wordUsed = new ArrayList<String>();
		ArrayList<String> wordsWithWildLetter = new ArrayList<String>();
		HashMap<Position, Integer> positionLabel = new HashMap<Position, Integer>();
		ArrayList<Set<Integer>> equivalentLabel = new ArrayList<Set<Integer>>();
		
		labelUsed = 0;

		if (supplyContents.isEmpty()) {
			
			//determine the maximums and the minimums of x, y respectively
			Collection<Position> positions = getTilePositions();
			Iterator iter = positions.iterator();
			Position p = (Position) iter.next();
			int xMin = p.getX();
			int yMin = p.getY();
			int xMax = xMin;
			int yMax = yMin;
			while (iter.hasNext()) {
				p = (Position) iter.next();
				int pX = p.getX();
				int pY = p.getY();
				xMin = Math.min(xMin, pX);
				xMax = Math.max(xMax, pX);
				yMin = Math.min(yMin, pY);
				yMax = Math.max(yMax, pY);
			}
			
			//retrieve illegalMoveEvent if present
			illegalMoveEvent = scanFromLeftToRight(xMin, yMin, xMax, yMax, positionLabel, equivalentLabel, wordUsed, 
					wordsWithWildLetter, illegalMoveEvent);
			illegalMoveEvent = scanFromTopToBottom(xMin, yMin, xMax, yMax, positionLabel, equivalentLabel, wordUsed, 
					wordsWithWildLetter, illegalMoveEvent);			
			illegalMoveEvent = checkWordsWithWildLetter(wordsWithWildLetter, wordUsed, illegalMoveEvent);
			illegalMoveEvent = checkWordsConnectivity(equivalentLabel, illegalMoveEvent);

		} else {
			illegalMoveEvent = new ShuffletterIllegalMoveEvent("Supply is not empty.");
		}
		endRoundOrGame(illegalMoveEvent);

	}

	/**
	 * Retrieve the name of the author of this model.
	 */
	@Override
	public String getAuthorName() {
		return AUTHORNAME;
	}

	/**
	 * Determine the number of tiles in the bag.
	 */
	@Override
	public int getBagCount() {
		return bagCount;
	}

	/**
	 * Retrieve the set of legal words that this model is using.
	 */
	@Override
	public Set<String> getLegalWords() {
		return legalWords;
	}

	/**
	 * Retrieve the collection of tiles currently in the supply.
	 */
	@Override
	public List<ShuffletterTile> getSupplyContents() {
		return supplyContents;
	}

	/**
	 * Retrieve the tile at the provided position in the playing grid.
	 */
	@Override
	public ShuffletterTile getTile(Position p) {
		return tilePositions.get(p);
	}

	/**
	 * Obtain a collection of positions at which letter pieces appear on the playing grid.
	 */
	@Override
	public Collection<Position> getTilePositions() {
		return tilePositions.keySet();
	}

	/**
	 * Determine whether or not the game is over.
	 */
	@Override
	public boolean isGameOver() {

		return bagCount == 0 && supplyContents.isEmpty() && IfMoveCorrectly;
	}

	/**
	 * Use to indicate that a tile should be moved from one location on the playing grid to another.
	 */
	@Override
	public void move(Position source, Position target) throws IllegalArgumentException{
		if (!isGameEnded) {
			ShuffletterTile sourceTile = getTile(source);
			ShuffletterTile targetTile = getTile(target);
			if (sourceTile == null) {
				System.out.println("No tile at source position!");
				throw new IllegalArgumentException();
			} else {
				ShuffletterTileMovedEvent movedEvent = new ShuffletterTileMovedEvent(source, target, sourceTile);
				tilePositions.remove(source);
				//swap the tiles
				if(getTile(target) != null) {
					tilePositions.remove(target);
					tilePositions.put(source, targetTile);
				}
				tilePositions.put(target, sourceTile);

				for (ShuffletterModelListener listener : listenersList)
					listener.tileMoved(movedEvent);

			}
		}
	}

	/**
	 * Use to indicate that a tile in the supply should be moved to the playing grid.
	 */
	@Override
	public void play(ShuffletterTile tile, Position target) throws IllegalArgumentException{
		if (supplyContents.contains(tile)) {
			if (getTile(target) == null) {
				ShuffletterTilePlayedEvent playedEvent = new ShuffletterTilePlayedEvent(target, tile);
				tilePositions.put(target, tile);
				for (ShuffletterModelListener listener : listenersList)
					listener.tilePlayed(playedEvent);
				supplyContents.remove(tile);
			} else {
				ShuffletterIllegalMoveEvent illegalMovedEvent = new ShuffletterIllegalMoveEvent("There is already a piece in that location.");
				for (ShuffletterModelListener listener : listenersList)
					listener.illegalMoveMade(illegalMovedEvent);
			}
		} else {
			System.out.println("The tile doesn't exist in the supply!");
			throw new IllegalArgumentException();
		}

	}

	/**
	 * Remove an event listener from this model.
	 */
	@Override
	public void removeListener(ShuffletterModelListener listener) {
		listenersList.remove(listener);
	}

}
