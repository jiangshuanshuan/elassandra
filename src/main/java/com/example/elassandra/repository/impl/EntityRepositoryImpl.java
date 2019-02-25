package com.example.elassandra.repository.impl;

import com.example.elassandra.model.Entity;
import com.example.elassandra.repository.EntityRepository;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntityRepositoryImpl implements EntityRepository {

	@Autowired
	private JestClient jestClient;

	@Override
	public void saveEntity(Entity entity) {
		Index index = new Index.Builder(entity).index(Entity.INDEX_NAME).type(Entity.TYPE).build();
		try {
			jestClient.execute(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量保存内容到ES
	 */
	@Override
	public void saveEntity(List<Entity> entityList) {
		Bulk.Builder bulk = new Bulk.Builder();
		for(Entity entity : entityList) {
			Index index = new Index.Builder(entity).index(Entity.INDEX_NAME).type(Entity.TYPE).build();
			bulk.addAction(index);
		}
		try {
			jestClient.execute(bulk.build());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在ES中搜索内容
	 */
	@Override
	public Object searchEntity(String searchContent){
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//searchSourceBuilder.query(QueryBuilders.queryStringQuery(searchContent));
		//searchSourceBuilder.field("name");
		searchSourceBuilder.query(QueryBuilders.matchQuery("name",searchContent));
		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex(Entity.INDEX_NAME).addType(Entity.TYPE).build();
		try {
			JestResult result = jestClient.execute(search);
			return result.getSourceAsObjectList(Entity.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JestResult updateDocument(String script, String index, String type, String id) {
		Update update = new Update.Builder(script).index(index).type(type).id(id).build();
		JestResult result = null ;
		try {
			result = jestClient.execute(update);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result ;
	}

	/**
	 * update all data by id(the type is int)
	 * @param script
	 * @param index
	 * @param type
	 * @param entity
	 * @return
	 */
	@Override
	public Object updateDocumentByEntity(String script, String index, String type, Entity entity) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("id",entity.getId()));
		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex(Entity.INDEX_NAME).addType(Entity.TYPE).build();
		List<String> objects = new ArrayList<>();
		try {
			SearchResult result = jestClient.execute(search);
			List<SearchResult.Hit<Object, Void>> ls = result.getHits(Object.class);
			ls.forEach(objectVoidHit -> objects.add(updateDocument(script,index,type,objectVoidHit.id).getJsonString()));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return objects;
	}

	@Override
	public JestResult deleteDocument(String index, String type, String id) {
		Delete delete = new Delete.Builder(id).index(index).type(type).build();
		JestResult result = null ;
		try {
			result = jestClient.execute(delete);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public JestResult deleteDocumentByQuery(String index, String type, String params) {
		DeleteByQuery db = new DeleteByQuery.Builder(params)
				.addIndex(index)
				.addType(type)
				.build();

		JestResult result = null ;
		try {
			result = jestClient.execute(db);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public <T> List<SearchResult.Hit<T, Void>> createSearch(String keyWord, String type, T o, String... fields) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.queryStringQuery(keyWord));
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		for(String field : fields){
			highlightBuilder.field(field);//高亮field
		}
		highlightBuilder.preTags("<em>").postTags("</em>");//高亮标签
		highlightBuilder.fragmentSize(200);//高亮内容长度
		searchSourceBuilder.highlighter(highlightBuilder);
		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(type).build();
		SearchResult result = null ;
		List<?> hits = null ;
		try {
			result = jestClient.execute(search);
			System.out.println("本次查询共查到："+result.getTotal()+"个结果！");

			hits = result.getHits(o.getClass());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return (List<SearchResult.Hit<T, Void>>) hits ;
	}
}
