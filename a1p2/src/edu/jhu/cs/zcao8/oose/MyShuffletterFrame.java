package edu.jhu.cs.zcao8.oose;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.jhu.cs.oose.fall2014.shuffletter.iface.NoOpShuffletterModelListener;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.Position;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterIllegalMoveEvent;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterModel;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterTileMovedEvent;
import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterTilePlayedEvent;

/**
 * A class that serves as main GUI for Shuffletter.
 * 
 *<p>
 * This is intended for OOSE assignment 1 part II.
 * 
 * @author zhihao Cao
 *
 */
public class MyShuffletterFrame extends JFrame {
	private ShuffletterModel model;
	private boolean haveShownCongrads;
	
	/**
	 * Create components and arranges them properly. Listen to mouse actions or model events in order to repaint correctly.
	 * 
	 * @param model			the underlying model of this GUI
	 */
	public MyShuffletterFrame(final ShuffletterModel model) {
		super();
		this.model = model;
		haveShownCongrads = false;
		
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		JMenuItem jMenuItemExit = new JMenuItem("Exit");
		
		//end the game if "Exit" is clicked
		jMenuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		file.add(jMenuItemExit);
		
		JMenuItem jMenuItemAbout = new JMenuItem("About");
		//show infos  if "About" is clicked
		jMenuItemAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageIcon icon = new ImageIcon("files" + File.separator + "about.jpg"); 
				icon.setImage(icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
				JOptionPane.showMessageDialog(null, "--> GUI Author: Zhihao Cao\nModel Author: " + model.getAuthorName(), 
						"About Shuffletter", JOptionPane.INFORMATION_MESSAGE, icon);
			}
		});
		help.add(jMenuItemAbout);
		
		menuBar.add(file);
		menuBar.add(help);
		this.setJMenuBar(menuBar);
		
		final JLabel messageLabel = new JLabel("");
		JButton endRoundButton = new JButton("<html><strong>End Round<strong>");
		final MyShuffletterBoard board = new MyShuffletterBoard(model);
		JScrollPane scrollPane = new JScrollPane(board);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(messageLabel, BorderLayout.CENTER);
		bottomPanel.add(endRoundButton, BorderLayout.EAST);
		
		this.getContentPane().setLayout(new BorderLayout());
		final MyShuffletterSupply supply = new MyShuffletterSupply(model);
		this.getContentPane().add(supply, BorderLayout.EAST);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		this.pack();

		
		//Perform endRound() if the button is clicked
		endRoundButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.endRound();
			}
		});
		
		//Update messages shown in the bottom
		model.addListener(new NoOpShuffletterModelListener() {
			@Override
			public void illegalMoveMade(ShuffletterIllegalMoveEvent event) {
				messageLabel.setText("  " + event.getMessage());
				messageLabel.setForeground(Color.RED);
			}
			@Override
			public void tilePlayed(ShuffletterTilePlayedEvent event) {
				updateBagCountInMessageLabel(messageLabel);
			}
			@Override
			public void tileMoved(ShuffletterTileMovedEvent event) {
				updateBagCountInMessageLabel(messageLabel);
			}
			@Override
			public void gameEnded() {
				messageLabel.setText("  You Win!");
				messageLabel.setForeground(Color.BLUE);
				if(!haveShownCongrads) {
				JOptionPane.showMessageDialog(null, "<html><font color=red>CONGRATULATIONS!</font></html>", 
						"You Win!", JOptionPane.INFORMATION_MESSAGE);
				haveShownCongrads = true;
				}
			}
			@Override
			public void roundEnded() {
				updateBagCountInMessageLabel(messageLabel);
			}
		});
	
		
		//Repaint whenever the model is changed
		model.addListener(new NoOpShuffletterModelListener() {
			@Override
			public void tileMoved(ShuffletterTileMovedEvent event) {
				repaint();
				
			}
			@Override
			public void tilePlayed(ShuffletterTilePlayedEvent event) {
				repaint();
			}
			@Override
			public void roundEnded() {
				repaint();
			}
			@Override
			public void illegalMoveMade(ShuffletterIllegalMoveEvent arg0) {
				repaint();
			}
			@Override
			public void gameEnded() {
				repaint();
			}
			
		});
		
		//Listen to mouse clicks on the supply. Mark down the tile clicked or clear last click information
		supply.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mouseX = e.getX();
				int mouseY = e.getY();
				if (!board.isClicked()) {
					supply.supplyClicked(mouseX, mouseY);
				} else {
					board.clearClicked();
					supply.clearClicked();
				}
				
				repaint();
			}
		});
		
		//Listen to mouse clicks on the board. Determine whether the model should move() or play()
		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//coordinate transformation
				int mouseX = e.getX();
				int mouseY = e.getY();
				int boardX = mouseX / board.getGirdSideLength();
				int boardY = mouseY / board.getGirdSideLength();
				int positionX = boardX - board.getColsAddedFromLeft();
				int positionY = - boardY + board.getRowsAddedFromTop();
				if (supply.isClicked()) {
					//Move the tile from the supply to the board
					model.play(model.getSupplyContents().get(supply.getTileClicked()), new Position(positionX, positionY));
					supply.clearClicked();
					//extend the board if needed
					board.extendBoard(boardX, boardY);
					
				} else  {
					if (!board.isClicked() && model.getTile(new Position(positionX, positionY))!= null) {
						board.setFirstClicked(mouseX, mouseY);
					} else {
						//coordinate transformation
						int firstClickedX = board.getFirstClickedX();
						int firstClickedY = board.getFirstClickedY();
						int positionFirstClickedX = firstClickedX - board.getColsAddedFromLeft();
						int positionFirstClickedY =	- firstClickedY + board.getRowsAddedFromTop();	
						
						if (model.getTile(new Position(positionFirstClickedX, positionFirstClickedY)) != null) {
							//move the tiles within the board
							model.move(new Position(positionFirstClickedX, positionFirstClickedY), new Position(positionX, positionY));
							//extend the board if needed
							board.extendBoard(boardX, boardY);
						}
						board.clearClicked();
					}
				}
				repaint();
			}
		});
	}

	/**
	 * Update the bag count displayed in the message label
	 * @param messageLabel		a label intended for displaying message
	 */
	private void updateBagCountInMessageLabel(JLabel messageLabel) {
		messageLabel.setText("<html>&nbsp;&nbsp<strong>" + model.getBagCount() + "</strong> tiles left in the bag.</html> ");
		messageLabel.setForeground(Color.BLUE);
	}
}

