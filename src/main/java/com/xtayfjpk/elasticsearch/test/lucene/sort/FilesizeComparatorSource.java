package com.xtayfjpk.elasticsearch.test.lucene.sort;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.Longs;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;

public class FilesizeComparatorSource extends FieldComparatorSource {

	@Override
	public FieldComparator<?> newComparator(final String fieldname, final int numHits,
			int sortPos, boolean reversed) throws IOException {
		
		return new FieldComparator<Long>() {
			private long[] values = new long[numHits];
			private long bottom;
			private long top;
			private Longs filesizes;

			@Override
			public int compare(int slot1, int slot2) {
				long value1 = values[slot1], value2 = values[slot2];
				return value1>value2 ? 1 : value1<value2 ? -1 : 0;
			}

			@Override
			public void setBottom(int slot) {
				this.bottom = values[slot];				
			}

			@Override
			public void setTopValue(Long value) {
				this.top = value;		
			}

			@Override
			public int compareBottom(int docId) throws IOException {
				long filesize = getFilesize(docId);
				return filesize > bottom ? 1 : filesize<bottom ? -1 : 0;
			}

			@Override
			public int compareTop(int docId) throws IOException {
				long filesize = getFilesize(docId);
				return filesize > top ? 1 : filesize<top ? -1 : 0;
			}

			@Override
			public void copy(int slot, int docId) throws IOException {
				values[slot] = getFilesize(docId);				
			}

			@Override
			public FieldComparator<Long> setNextReader(AtomicReaderContext context) throws IOException {
				this.filesizes = FieldCache.DEFAULT.getLongs(context.reader(), fieldname, true);
				return this;
			}

			@Override
			public Long value(int slot) {
				return values[slot];
			}
			
			private Long getFilesize(int docId) {
				return filesizes.get(docId);
			}
			
		};
		
	}

}
