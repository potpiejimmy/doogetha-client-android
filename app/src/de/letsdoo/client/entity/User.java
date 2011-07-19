package de.letsdoo.client.entity;

/**
 *
 */
public class User implements java.io.Serializable {
    
	/* User values */
	private Integer id;
    private String email;
    private String firstname;
    private String lastname;

    /* UserEvent values */
    private Integer state = null;

    public User() {
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String toString() {
    	return email;
    }

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
}
