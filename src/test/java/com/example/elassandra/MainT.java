package com.example.elassandra;

import com.example.elassandra.model.Entity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MainT {
	public static void main(String[] args) {
		Entity entity = new Entity();
		entity.setId(11L);
		entity.setName("南京啊");
		System.out.println(new Gson().toJson(entity));
		Map<String,Entity> map = new HashMap<>();
		map.put("doc",entity);
		System.out.println(new Gson().toJson(map));
	}
}
