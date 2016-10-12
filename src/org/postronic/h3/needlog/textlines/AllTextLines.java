package org.postronic.h3.needlog.textlines;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

public class AllTextLines implements TextLines {
	
	private IndexReader indexReader;
	
	public AllTextLines(IndexReader indexReader) {
		this.indexReader = indexReader;
	}

	public TextLine getTextLine(int index) {
		Document document;
		try {
			document = indexReader.document(index);
		} catch (CorruptIndexException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return new TextLine(Integer.parseInt(document.get("line")), document.get("text"));
	}

	public int getTextLineCount() {
		return indexReader.numDocs();
	}

	public int getTotalResults() {
		return indexReader.numDocs();
	}

}
