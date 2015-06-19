package com.xtayfjpk.elasticsearch.test;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

public class ClientTest {
	private static Settings settings = ImmutableSettings.settingsBuilder()
			.put("cluster.name", "my_elasticsearch_cluster")
			.put("client.transport.sniff", true)
			.build();
	
	
	@Test
	public void testPrepareGet() throws Exception {
		TransportClient client = getClient();
		System.out.println(client.connectedNodes().get(0).getName());
		String index = "bank";
		String type = "account";
		String id = "1";
		GetResponse response = client.prepareGet(index, type, id).setOperationThreaded(false).execute().actionGet();
		if(response!=null) {
			System.out.println(response.getSourceAsString());
		}
	}
	
	@Test
	public void testPrepareSearch() throws Exception {
		Client client = getClient();
		String index = "bank";
		String source = "{\"query\": { \"match_all\": {} }, \"highlight\" : {\"fields\" : {\"firstname\" : {}}}}";
		SearchResponse response = client.prepareSearch(index).setExtraSource(source).execute().actionGet();
		System.out.println(response);
	}
	
	@Test
	public void testPrepareSearchWithBuilder() throws Exception {
		Client client = getClient();
		String index = "bank";
		//设置了查询高亮才会有效
		SearchResponse response = client.prepareSearch(index).setQuery(QueryBuilders.matchQuery("firstname", "test Alyce")).addHighlightedField("firstname")
				.setHighlighterEncoder("html").setHighlighterPreTags("<em>").setHighlighterPostTags("</em>").execute().actionGet();
		System.out.println(response);
		for(SearchHit searchHit : response.getHits()) {
			System.out.println(searchHit.getHighlightFields());
		}
		
		SearchScrollRequestBuilder builder = new SearchScrollRequestBuilder(client);
		client.searchScroll(builder.request());
		
	}


	@SuppressWarnings("resource")
	public static TransportClient getClient() {
		return new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("192.168.0.202", 9300));
	}
}
