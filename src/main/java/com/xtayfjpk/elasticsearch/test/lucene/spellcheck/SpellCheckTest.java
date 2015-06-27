package com.xtayfjpk.elasticsearch.test.lucene.spellcheck;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class SpellCheckTest {
	
	@Test
	public void testGenSpellIndex() throws Exception {
		String spellIndexDir = "lucene-spell-index";
		String indexDir = "lucene-index";
		String indexField = "contents";
		FSDirectory spellIndex = FSDirectory.open(new File(spellIndexDir));
		SpellChecker spellChecker = new SpellChecker(spellIndex);
		
		FSDirectory index = FSDirectory.open(new File(indexDir));
		IndexReader indexReader = DirectoryReader.open(index);
		
		Analyzer analyzer = new StandardAnalyzer();
		
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		spellChecker.indexDictionary(new LuceneDictionary(indexReader, indexField), config, true);
		spellChecker.close();
		index.close();
		spellIndex.close();
		
	}
	
	@Test
	public void testGenPromptList() throws Exception {
		String spellIndexDir = "lucene-spell-index";
		String wordToRespell = "letuce";
		FSDirectory spellIndex = FSDirectory.open(new File(spellIndexDir));
		SpellChecker spellChecker = new SpellChecker(spellIndex);
		spellChecker.setStringDistance(new LevensteinDistance());
		String[] suggestions = spellChecker.suggestSimilar(wordToRespell, 5);
		
		for(String suggestion : suggestions) {
			System.out.println(suggestion);
		}
		spellChecker.close();
	}
}
