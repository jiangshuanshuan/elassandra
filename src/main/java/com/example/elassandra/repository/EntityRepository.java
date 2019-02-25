package com.example.elassandra.repository;

import com.example.elassandra.model.Entity;
import io.searchbox.client.JestResult;
import io.searchbox.core.SearchResult;

import java.util.List;

public interface EntityRepository {
	void saveEntity(Entity entity);

	void saveEntity(List<Entity> entityList);

	Object searchEntity(String searchContent);

	/**
	 * 更新Document
	 * @param index ：文档在哪存放
	 * @param type ： 文档表示的对象类别
	 * @param id ：文档唯一标识
	 */
	public JestResult updateDocument(String script , String index, String type, String id);
	public Object updateDocumentByEntity(String script , String index, String type, Entity entity);
	/**
	 * 删除Document
	 * @param index ：文档在哪存放
	 * @param type ： 文档表示的对象类别
	 * @param id ：文档唯一标识
	 * @return
	 */
	public JestResult deleteDocument(String index,String type,String id) ;
	/**
	 * 根据条件删除
	 * @param index
	 * @param type
	 * @param params
	 */
	public JestResult deleteDocumentByQuery(String index, String type, String params);
	/**
	 * 搜索
	 * @param keyWord ：搜索关键字
	 * @return
	 */
	public <T> List<SearchResult.Hit<T,Void>> createSearch(String keyWord , String type , T o , String... fields) ;
}
