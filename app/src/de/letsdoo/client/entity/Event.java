package de.letsdoo.client.entity;

public class Event {
	private Integer id = null;
	private String name = null;
	
	public Event() {
	}
	
	public Event(String name) {
		this.name = name;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString() {
		return name;
	}
}
