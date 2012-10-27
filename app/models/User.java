package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.binding.*;
import play.data.validation.*;

import play.db.jpa.*;
 
@Entity
@Table(name="user")
public class User extends Model {

	@Required
	@MinSize(6)
	@MaxSize(63)
  @Match(value="^\\w*$", message="validation.not.valid.username")
	public String username;

	public String fullname;
	public String iconUrl;
	public String profile;

	@As("yyyy-MM-dd HH:mm:ss.S Z") public Date savedAt;

	public User(String username) {
		this.username = username;
		this.savedAt = new Date();
	}
	public boolean equals(User user) {
		if ( user == null ) return false;
		return this.id == user.id;
	}

	/**
		Return username + savedAt
	*/
	public String toString() {
		return username + "(savedAt:" + this.savedAt.toString() + ")";
	}

	/**
		Get user by username.
	*/
	public static User get(String username) {
		return User.find("byUsername", username).first();
	}

}