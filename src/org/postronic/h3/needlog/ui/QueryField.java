package org.postronic.h3.needlog.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class QueryField extends JTextField implements KeyListener {
	private static final long serialVersionUID = 5851041639564248504L;
	
	private String previousText = "";
	private MainWindow mainWindow;
	
	public QueryField(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		addKeyListener(this);
	}
	
	public void keyPressed(KeyEvent keyEvent) { }
	
	public void keyTyped(KeyEvent keyEvent) { }

	public void keyReleased(KeyEvent keyEvent) {
		String text = getText();
		if (!text.equals(previousText)) {
			mainWindow.query(text);
			previousText = text;
		}
	}
	
}
