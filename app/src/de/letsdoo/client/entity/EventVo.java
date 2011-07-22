package de.letsdoo.client.entity;


public class EventVo implements java.io.Serializable {
	private Integer id = null;
	private UserVo owner = null;
	private String name = null;
	private String description = null;
    private Long eventtime;
	private Integer state = null;
	private UserVo[] users = null;
	
	public EventVo() {
	}
	
	public EventVo(String name) {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public UserVo[] getUsers() {
		return users;
	}

	public void setUsers(UserVo[] users) {
		this.users = users;
	}

	public UserVo getOwner() {
		return owner;
	}

	public void setOwner(UserVo owner) {
		this.owner = owner;
	}

	public Long getEventtime() {
		return eventtime;
	}

	public void setEventtime(Long eventtime) {
		this.eventtime = eventtime;
	}
}
