package org.postronic.h3.needlog.textlines;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class SearchResultsTextLines implements TextLines {
	
	private IndexSearcher indexSearcher;
	private TopDocs topDocs;
	
	public SearchResultsTextLines(IndexSearcher indexSearcher, TopDocs topDocs) {
		this.indexSearcher = indexSearcher;
		this.topDocs = topDocs;
	}
	
	/* (non-Javadoc)
	 * @see org.postronic.h3.needlog.SearchLines#getTextLine(int)
	 */
	public TextLine getTextLine(int index) {
	    ScoreDoc scoreDoc = topDocs.scoreDocs[index];
    	Document doc;
		try {
			doc = indexSearcher.doc(scoreDoc.doc);
		} catch (CorruptIndexException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	    return new TextLine(Integer.parseInt(doc.get("line")), doc.get("text"));
	}
	
	/* (non-Javadoc)
	 * @see org.postronic.h3.needlog.SearchLines#getTextLineCount()
	 */
	public int getTextLineCount() {
		return topDocs.scoreDocs.length;
	}
	
	/* (non-Javadoc)
	 * @see org.postronic.h3.needlog.SearchLines#getTotalResults()
	 */
	public int getTotalResults() {
		return topDocs.totalHits;
	}

}
