package org.postronic.h3.needlog.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.postronic.h3.needlog.textlines.TextLine;
import org.postronic.h3.needlog.textlines.TextLines;

public class ScrollableTextPane extends JScrollPane {
	private static final long serialVersionUID = -433110405031728504L;
	
	private JPanel textJPanel;
	private TextLines searchResults;
	
	public ScrollableTextPane() {
		super();
		this.setDoubleBuffered(true);
		this.getVerticalScrollBar().setUnitIncrement(19);
		textJPanel = new JPanel() {
			private static final long serialVersionUID = 5208340199716254201L;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				TextLines searchResults;
		        synchronized (this) {
		        	searchResults = ScrollableTextPane.this.searchResults;
		        }
		        
		        if (searchResults != null && searchResults.getTextLineCount() > 0) {
				
					Rectangle viewRect = ScrollableTextPane.this.getViewport().getViewRect();
					FontMetrics fontMetrics = getFontMetrics(getFont());
					int lineHeight = fontMetrics.getHeight();
					int startLine = viewRect.y / lineHeight;
					int endLine = (viewRect.y + viewRect.height) / lineHeight;
					if (startLine >= searchResults.getTextLineCount()) { startLine = searchResults.getTextLineCount() - 1; }
					if (endLine >= searchResults.getTextLineCount()) { endLine = searchResults.getTextLineCount() - 1; }
					
					Graphics2D g2 = (Graphics2D) g;
			        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			        Color defaultForeground = this.getForeground();
			        
					for (int i = startLine; i <= endLine; i++) {
						TextLine textLine = searchResults.getTextLine(i);
						String lineNumber = Integer.toString(textLine.getLine());
						g2.setColor(Color.GRAY);
						g2.drawString(lineNumber, fontMetrics.getMaxAdvance() * 7 - 5 - fontMetrics.stringWidth(lineNumber), (1 + i) * lineHeight);
						g2.setColor(defaultForeground);
						g2.drawString(textLine.getText(), 2 + fontMetrics.getMaxAdvance() * 7, (1 + i) * fontMetrics.getHeight());
					}
		        }
			}
			
		};
		this.setViewportView(textJPanel);
	}
	
	public void setSearchResults(TextLines searchResults) {
		synchronized (this) {
			this.searchResults = searchResults;
		}
		FontMetrics fontMetrics = getFontMetrics(getFont());
		Dimension dimension = new Dimension(100, (1 + searchResults.getTextLineCount()) * fontMetrics.getHeight());
		textJPanel.setPreferredSize(dimension);
		textJPanel.setSize(dimension);
		textJPanel.repaint();
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (textJPanel != null) {
			textJPanel.setFont(font);
		}
	}
	
}

