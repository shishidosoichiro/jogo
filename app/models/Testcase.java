package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.binding.*;
import play.data.validation.*;

import play.db.jpa.*;
 
@Entity
@Table(name="testcase")
public class Testcase extends Model {

	@ManyToOne
	public Site site;
	
	public String name;

	public Testcase(Site site, String name) {
		this.site = site;
		this.name = name;
	}
}