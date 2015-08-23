package edu.jhu.cs.zcao8.oose;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;

import edu.jhu.cs.oose.fall2014.shuffletter.iface.ShuffletterModel;

/**
 * main method for the whole Shuffletter GUI.
 * 
 * @author zhihao Cao
 *
 */
public class MyShuffletterMain {
	   public static void main(String[] args) throws IOException {
	        ShuffletterModel model = new edu.jhu.cs.oose.fall2014.shuffletter.model.StandardShuffletterModel();
	        MyShuffletterFrame gui = new MyShuffletterFrame(model);
			gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//			Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
//			gui.setSize(screenSize.height,screenSize.height);
	        gui.setLocationRelativeTo(null);
	        gui.setVisible(true);
	    }
}
