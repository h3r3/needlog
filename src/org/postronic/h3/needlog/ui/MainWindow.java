package org.postronic.h3.needlog.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.postronic.h3.needlog.Needlog;
import org.postronic.h3.needlog.textlines.TextLines;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = -7567060145020700775L;
	
	private Needlog needlog;
	private QueryField queryField;
	private ScrollableTextPane scrollableTextPane;
	private StatusBar statusBar;
	private QueryRunnable queryRunnable;
	private Thread queryThread;
	private String lastQueryText = "";
	
	private class QueryRunnable implements Runnable {
		private final String queryText;
		private final boolean refreshing;
		private volatile boolean aborted = false;
		public QueryRunnable(String queryText, boolean refreshing) {
			this.queryText = queryText;
			this.refreshing = refreshing;
		}
		public void abort() {
			aborted = true;
		}
		public void run() {
			if (!refreshing) { queryField.setBackground(Color.YELLOW); }
			try {
				TextLines searchResults = needlog.search(queryText);
				if (!aborted) {
					scrollableTextPane.setSearchResults(searchResults);
					statusBar.setTextLines(searchResults);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!refreshing) { queryField.setBackground(Color.WHITE); } 
		}
	}

	public MainWindow(Needlog needlog) {
		super("Needlog " + Needlog.VERSION);
		this.needlog = needlog;
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResource("Needlog.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setIconImage(image);
		
		Font font = new Font("Consolas", 0, 15);
				
		scrollableTextPane = new ScrollableTextPane();
		scrollableTextPane.setFont(font);
		
		statusBar = new StatusBar();
		
		queryField = new QueryField(this);
		queryField.setFont(font);

		this.getContentPane().add(queryField, BorderLayout.NORTH);
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);
		this.getContentPane().add(scrollableTextPane, BorderLayout.CENTER);
		
		this.setSize(780, 550);
		this.setLocationByPlatform(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void setPercentIndexing(float percentIndexing) {
		statusBar.setPercentIndexing(percentIndexing);
	}
	
	public void query(String queryText) {
		query(queryText, false);
	}
	
	private void query(String queryText, boolean refreshing) {
		synchronized (this) {
			if (!refreshing) { lastQueryText = queryText; }
			if (queryThread != null && queryThread.isAlive()) {
				queryRunnable.abort();
				//queryThread.interrupt();
				queryField.setBackground(Color.WHITE);
			}
			queryRunnable = new QueryRunnable(queryText, refreshing);
			queryThread = new Thread(queryRunnable, "Search");
			queryThread.setDaemon(true);
			queryThread.start();
		}
	}
	
	public void queryRefresh() {
		query(lastQueryText, true);
	}

}
