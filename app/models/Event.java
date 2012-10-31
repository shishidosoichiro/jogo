package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.binding.*;
import play.data.validation.*;

import play.db.jpa.*;
 
@Entity
@Table(name="event")
public class Event extends Model {

	@ManyToOne
	public Site site;

	@ManyToOne
	public Testcase testcase;
	
	public String name;
	public String value;

	@Column(name="session_id")
	public String sessionId;

	@As("yyyy-MM-dd HH:mm:ss.S Z") 
	@Column(name="created_at")
	public Date createdAt;
	
	public Event(Site site, Testcase testcase, String sessionId, String name, String value) {
		this.site = site;
		this.testcase = testcase;
		this.sessionId = sessionId;
		this.name = name;
		this.value = value;
		this.createdAt = new Date();
	}
}