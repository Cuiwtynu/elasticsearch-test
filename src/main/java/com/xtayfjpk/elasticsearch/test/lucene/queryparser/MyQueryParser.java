package com.xtayfjpk.elasticsearch.test.lucene.queryparser;

import java.text.SimpleDateFormat;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

public class MyQueryParser extends QueryParser {

	public MyQueryParser(String f, Analyzer a) {
		super(f, a);
	}

	@Override
	protected org.apache.lucene.search.Query getFuzzyQuery(String field,
			String termStr, float minSimilarity) throws ParseException {
		throw new ParseException("fuzzy query is forbidden.");
	}
	
	@Override
	protected org.apache.lucene.search.Query getWildcardQuery(String field,
			String termStr) throws ParseException {
		throw new ParseException("wildcard query is forbidden.");
	}
	
	@Override
	protected org.apache.lucene.search.Query getRangeQuery(String field,
			String part1, String part2, boolean startInclusive,
			boolean endInclusive) throws ParseException {
		if(field.equals("filesize")) {
			return NumericRangeQuery.newLongRange(field, Long.valueOf(part1), Long.valueOf(part2), startInclusive, endInclusive);
		}
		if(field.equals("lastModified")) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Long start = Long.valueOf(dateFormat.parse(part1).getTime());
				Long end = Long.valueOf(dateFormat.parse(part2).getTime());
				return NumericRangeQuery.newLongRange(field, start, end, startInclusive, endInclusive);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}
		return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
	}
	
	@Override
	protected org.apache.lucene.search.Query getFieldQuery(String field,
			String queryText, int slop) throws ParseException {
		org.apache.lucene.search.Query query = super.getFieldQuery(field, queryText, slop);
		if(!(query instanceof PhraseQuery)) {
			return query;
		}
		
		PhraseQuery phraseQuery = (PhraseQuery) query;
		org.apache.lucene.index.Term[] terms = phraseQuery.getTerms();
		SpanQuery[] clauses = new SpanQuery[terms.length];
		for(int i=0; i<terms.length; i++) {
			clauses[i] = new SpanTermQuery(terms[i]);
		}
		SpanNearQuery nearQuery = new SpanNearQuery(clauses, slop, true);
		return nearQuery;
	}
}
