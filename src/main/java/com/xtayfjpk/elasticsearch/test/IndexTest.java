package com.xtayfjpk.elasticsearch.test;

import java.util.Arrays;
import java.util.Map;

import net.sf.json.JSONObject;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.hppc.ObjectLookupContainer;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

public class IndexTest {
	
	@Test
	public void testCreateIndex() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("number_of_shards", 3)
				.put("number_of_replicas", 2).build();
		CreateIndexRequest request = new CreateIndexRequest("my_index", settings);
		
		ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
			
			@Override
			public void onResponse(CreateIndexResponse response) {
				System.out.println(response.isAcknowledged());			
			}
			
			@Override
			public void onFailure(Throwable e) {
				System.out.println("create index faulure");
				System.out.println(e);
			}
		};
		indicesAdminClient.create(request, listener);
		
		Thread.sleep(5000);
	}
	
	
	@Test
	public void testCreateIndexWithSource() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		CreateIndexRequest request = new CreateIndexRequest("my_index");
		JSONObject source = new JSONObject();
		JSONObject settings = new JSONObject();
		JSONObject index = new JSONObject();
		index.put("number_of_shards", 3);
		index.put("number_of_replicas", 2);
		settings.put("index", index);
		source.put("settings", settings);
		request.source(source.toString());
		
		ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
			
			@Override
			public void onResponse(CreateIndexResponse response) {
				System.out.println(response.isAcknowledged());			
			}
			
			@Override
			public void onFailure(Throwable e) {
				System.out.println("create index faulure");
				System.out.println(e);
			}
		};
		indicesAdminClient.create(request, listener);
		
		Thread.sleep(5000);
	}
	
	@Test
	public void testDeleteIndex() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		DeleteIndexRequest request = new DeleteIndexRequest("my_index");
		DeleteIndexResponse response = indicesAdminClient.delete(request).actionGet();
		System.out.println(response.isAcknowledged());
	}
	
	@Test
	public void testGetIndicesInfo() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		GetIndexRequest request = new GetIndexRequest();
		GetIndexResponse response = indicesAdminClient.getIndex(request).actionGet();
		System.out.println(Arrays.asList(response.getIndices()));
		ObjectLookupContainer<String> keys = response.getSettings().keys();
		for(Object key : keys.toArray()) {
			System.out.println(key + ":" + response.getSettings().get(key.toString()).getAsMap());
		}
	}
	
	@Test
	public void testCloseIndex() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		CloseIndexRequest request = new CloseIndexRequest("my_index");
		CloseIndexResponse response = indicesAdminClient.close(request).actionGet();
		System.out.println(response.isAcknowledged());
	}
	
	@Test
	public void testOpenIndex() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		OpenIndexRequest request = new OpenIndexRequest("my_index");
		OpenIndexResponse response = indicesAdminClient.open(request).actionGet();
		System.out.println(response.isAcknowledged());
	}
	
	@Test
	public void testGetMappings() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		GetMappingsRequest request = new GetMappingsRequest().indices("bank");
		GetMappingsResponse response = indicesAdminClient.getMappings(request).actionGet();
		ObjectLookupContainer<String> keys = response.mappings().keys();
		for(Object key : keys.toArray()) {
			for(Object key2 : response.mappings().get(key.toString()).keys().toArray()) {
				Map<String, Object> result = response.mappings().get(key.toString()).get(key2.toString()).sourceAsMap();
				System.out.println(key + "," + key2);
				System.out.println(result);
			}
		}
	}
	
	@Test
	public void testIndexExists() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		IndicesExistsRequest request = new IndicesExistsRequest("my_index");
		IndicesExistsResponse response = indicesAdminClient.exists(request).actionGet();
		System.out.println(response.isExists());
	}
	
	@Test
	public void testPutMapping() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		PutMappingRequest request = new PutMappingRequest("my_index");
		request.type("tweet");
		JSONObject mappingSource = new JSONObject();
		JSONObject tweet = new JSONObject();
		JSONObject properties = new JSONObject();
		JSONObject message = new JSONObject();
		message.put("type", "string");
		message.put("store", true);
		properties.put("message", message);
		tweet.put("properties", properties);
		mappingSource.put("tweet", tweet);
		request.source(mappingSource.toString());
		PutMappingResponse response = indicesAdminClient.putMapping(request).actionGet();
		System.out.println(response.isAcknowledged());
	}
	
	@Test
	public void testGetFieldMappings() throws Exception {
		IndicesAdminClient indicesAdminClient = ClientTest.getClient().admin().indices();
		GetFieldMappingsRequest request = new GetFieldMappingsRequest();
		request.indices("my_index");
		request.types("bank");
		GetFieldMappingsResponse response = indicesAdminClient.getFieldMappings(request).actionGet();
		System.out.println(response.fieldMappings("my_index", "bank", "account"));
		
	}
}
