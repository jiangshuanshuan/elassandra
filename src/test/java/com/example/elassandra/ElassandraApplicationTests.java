package com.example.elassandra;

import com.example.elassandra.model.Entity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElassandraApplicationTests {

	@Test
	public void contextLoads() throws JsonProcessingException {
		Entity entity = new Entity();
		entity.setId(11L);
		entity.setName("南京啊");
		System.out.println(new Gson().toJson(entity));
		System.out.println(new Gson().toJson(new HashMap<>().put("doc",entity)));
	}

}
