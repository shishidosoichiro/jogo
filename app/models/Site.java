package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.binding.*;
import play.data.validation.*;

import play.db.jpa.*;
 
@Entity
@Table(name="site")
public class Site extends Model {

	@ManyToOne
	public User user;
	
	public String name;
	public String url;

	public Site(User user, String name) {
		this.user = user;
		this.name = name;
	}
}