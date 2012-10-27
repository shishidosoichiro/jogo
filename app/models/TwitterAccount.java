package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.binding.*;
 
@Entity
@Table(name="twitter_account")
public class TwitterAccount extends Model {

	@OneToOne
	public User user;

	@Column(name="twitter_id")
	public String twitterId;

	@Column(name="screen_name")
	public String screenName;
	public String name;

	@Column(name="profile_image_url")
	public String profileImageUrl;
	
	@Column(name="description")
	public String description;
	
	public String token;
	public String secret;
	@As("yyyy-MM-dd HH:mm:ss.S Z") public Date savedAt;

	public TwitterAccount() {
		this.savedAt = new Date();
	}

	public TwitterAccount(User user) {
		this.user = user;
		this.savedAt = new Date();
	}

}