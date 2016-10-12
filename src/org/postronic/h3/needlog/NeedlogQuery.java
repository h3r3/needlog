package org.postronic.h3.needlog;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

public class NeedlogQuery extends BooleanQuery {
	private static final long serialVersionUID = 1608609591274464284L;

	public NeedlogQuery(String queryString) {
		super(false);
		String [] words = queryString.split(" ");
		for (String word : words) {
			word = word.trim();
			Occur occur = Occur.SHOULD;
			if (word.startsWith("+")) {
				occur = Occur.MUST;
				word = word.substring(1).trim();
			}
			if (word.startsWith("-")) {
				occur = Occur.MUST_NOT;
				word = word.substring(1).trim();
			}
			if (word.length() > 0) {
				add(new BooleanClause(new TermQuery(new Term("text", word)), occur));
			}
		}
	}

}
