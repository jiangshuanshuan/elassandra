package com.example.elassandra.controller;

import com.example.elassandra.model.Entity;
import com.example.elassandra.repository.EntityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.searchbox.client.JestResult;
import io.searchbox.core.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EntityController {

	@Autowired
	EntityRepository cityESService;

	//http://localhost:8888/save?id=44&name=南京南京
	@RequestMapping(value="/save", method= RequestMethod.GET)
	public String save(@RequestParam long id, @RequestParam String name) {
		System.out.println("save 接口");
		if(id>0 && StringUtils.isNotEmpty(name)) {
			Entity newEntity = new Entity(id,name);
			List<Entity> addList = new ArrayList<Entity>();
			addList.add(newEntity);
			cityESService.saveEntity(addList);
			return "OK";
		}else {
			return "Bad input value";
		}
	}
	@RequestMapping(value="/update", method= RequestMethod.PUT)
	public String update(@RequestBody Entity entity,@RequestParam String id) throws JsonProcessingException {
		/*http://localhost:8888/update?id=8QbyE2kBBHW5HyLqk2xr
		{
  "id" : 44,
  "age" : 330,
 "name":"南京中山陵44"}
		 */
		Map<String,Entity> map = new HashMap<>();
		map.put("doc",entity);
		String json = new Gson().toJson(map);
		System.out.println("json="+json);
		String script = "{" +
				"    \"doc\" : {" +
				"        \"id\" : "+entity.getId()+"," +
				"        \"age\" : "+entity.getAge()+"," +
				"        \"name\" : \""+entity.getName()+"\"" +
				"}" +
				"}";
		System.out.println("str="+script);
		JestResult jr = cityESService.updateDocument(json, Entity.INDEX_NAME, Entity.TYPE, id);//8QbyE2kBBHW5HyLqk2xr
		return jr.getJsonString();
	}

	@RequestMapping(value="/updateAll", method= RequestMethod.PUT)
	public Object updateAll(@RequestBody Entity entity) throws JsonProcessingException {
		/*http://localhost:8888/updateAll
		{
  "id" : 22,
 "name":"南京中山陵"}

		 */
		Map<String,Entity> map = new HashMap<>();
		map.put("doc",entity);
//		String json = new Gson().toJson(map);
//		System.out.println("json="+json);
		String script = "{" +
				"    \"doc\" : {" +
				"        \"id\" : "+entity.getId()+"," +
				"        \"age\" : "+entity.getAge()+"," +
				"        \"name\" : \""+entity.getName()+"\"" +
				"}" +
				"}";
		System.out.println("str="+script);
		Object jr = cityESService.updateDocumentByEntity(script, Entity.INDEX_NAME, Entity.TYPE, entity);//8QbyE2kBBHW5HyLqk2xr
		return jr;
	}

	//http://localhost:8888/delete?id=hpV9I2kBzU6g49Dbm371
	@RequestMapping(value="/delete", method=RequestMethod.DELETE)
	public Object delete(@RequestParam String id) {
		return cityESService.deleteDocument(Entity.INDEX_NAME,Entity.TYPE,id).getJsonString();
	}

	@RequestMapping(value="/deleteAll", method=RequestMethod.DELETE)
	public Object deleteAll(@RequestBody String params) {
		/*http://localhost:8888/deleteAll
		{
			"query": {
			"match": {
				"name": "你啊"
			}
		}
		}*/
		return cityESService.deleteDocumentByQuery(Entity.INDEX_NAME,Entity.TYPE,params).getJsonString();
	}

	//http://localhost:8888/search?name=你好啊
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public Object save(@RequestParam String name) {
		if(StringUtils.isNotEmpty(name)) {
			return cityESService.searchEntity(name);
		}
		return null;
	}

}
