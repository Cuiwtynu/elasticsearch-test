package com.xtayfjpk.elasticsearch.test.lucene.filter;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.Longs;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.OpenBitSet;

/**
 * @author zj
 *
 */
public class MaxFilesizeFilter extends Filter {
	private long maxFilesize;
	
	public MaxFilesizeFilter(long maxFilesize) {
		this.maxFilesize = maxFilesize;
	}
	
	//各bit位位置和文档号相对应，值为1的bit位表示该位置上的文档可以被搜索到，而为0的bit位则表示与之对应的文档不在搜索之列
	//当有删除暂时删除的文档时acceptDocs不为null
	@Override
	public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
		System.out.println(acceptDocs);
		if(acceptDocs!=null) {
			System.out.println("acceptDocs:" + acceptDocs.length());
		}
		System.out.println("maxDoc:" + context.reader().maxDoc());
		OpenBitSet bits = new OpenBitSet(context.reader().maxDoc());
		Longs filesizes = FieldCache.DEFAULT.getLongs(context.reader(), "filesize", true);
		for(int i=0; i<context.reader().maxDoc(); i++) {
			if(filesizes.get(i) < maxFilesize) {
				bits.set(i);
			}
		}
		return bits;
	}

}
