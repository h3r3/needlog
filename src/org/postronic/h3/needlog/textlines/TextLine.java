package org.postronic.h3.needlog.textlines;

import java.io.Serializable;

public class TextLine implements Serializable {
	private static final long serialVersionUID = -1297332845148211315L;
	
	private int line;
	private String text;
	
	public TextLine(int line, String text) {
		this.line = line;
		this.text = text;
	}

	public int getLine() {
		return line;
	}

	public String getText() {
		return text;
	}

}
