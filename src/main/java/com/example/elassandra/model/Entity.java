package com.example.elassandra.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Entity implements Serializable{

	private static final long serialVersionUID = -763638353551774166L;
	public static final String INDEX_NAME = "index_entity";

	public static final String TYPE = "tstype";
	private Long id;

	private String name;
	private int age;
	public Entity() {
		super();
	}

	public Entity(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "{" +
				"id:" + id +
				", name:'" + name + '\'' +
				'}';
	}
}
