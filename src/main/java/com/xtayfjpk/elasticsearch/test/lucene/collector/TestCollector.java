package com.xtayfjpk.elasticsearch.test.lucene.collector;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Scorer;

public class TestCollector extends Collector {
	private Scorer scorer;
	private BinaryDocValues fullpaths;

	@Override
	public void setScorer(Scorer scorer) throws IOException {
		System.out.println("set scorer");
		this.scorer = scorer;
	}

	@Override
	public void collect(int doc) throws IOException {
		System.out.println(fullpaths.get(doc).utf8ToString() + "," + scorer.score());
	}

	@Override
	public void setNextReader(AtomicReaderContext context) throws IOException {
		this.fullpaths = FieldCache.DEFAULT.getTerms(context.reader(), "filename", true);
		System.out.println(context.docBase);
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;
	}

}
