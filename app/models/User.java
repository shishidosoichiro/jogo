package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.binding.*;
import play.data.validation.*;
import play.libs.Codec;

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
	public String trackKey;
	
	public User(String username) {
		this.username = username;
		this.trackKey = Codec.UUID();
	}
	public boolean equals(User user) {
		if ( user == null ) return false;
		return this.id == user.id;
	}

	/**
		Get user by username.
	*/
	public static User get(String username) {
		return User.find("byUsername", username).first();
	}

	/**
		Get user by username.
	*/
	public static User getByTrackKey(String tracKey) {
		return User.find("byTrackKey", tracKey).first();
	}

}