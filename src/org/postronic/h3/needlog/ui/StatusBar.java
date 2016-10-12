package org.postronic.h3.needlog.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.postronic.h3.needlog.textlines.SearchResultsTextLines;
import org.postronic.h3.needlog.textlines.TextLines;

public class StatusBar extends JPanel {
	private static final long serialVersionUID = -1515200305816417422L;

	
	private JLabel percentIndexingJLabel;
	private JLabel displayStatusJLabel;

	public StatusBar() {
		super(new BorderLayout());
		
		percentIndexingJLabel = new JLabel("Loading...");
		displayStatusJLabel = new JLabel();
		
		add(percentIndexingJLabel, BorderLayout.WEST);
		add(displayStatusJLabel, BorderLayout.EAST);
		
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}
	
	public void setTextLines(TextLines textLines) {
		if (textLines.getTotalResults() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(textLines.getTextLineCount());
			if (textLines.getTextLineCount() != textLines.getTotalResults()) {
				sb.append(" of ");
				sb.append(textLines.getTotalResults());
			}
			if (textLines instanceof SearchResultsTextLines) {
				sb.append(" results");
			} else {
				sb.append(" lines");
			}
			displayStatusJLabel.setText(sb.toString());
		} else {
			displayStatusJLabel.setText("");
		}
	}
	
	public void setPercentIndexing(float percentIndexing) {
		if (percentIndexing < 100.0f) {
			DecimalFormat df = new DecimalFormat("##0.00'%'");
			percentIndexingJLabel.setText("Loading: " + df.format(percentIndexing));
		} else {
			percentIndexingJLabel.setText("");
		}
	}

}
