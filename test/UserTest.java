import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class UserTest extends UnitTest {

	@Test
	public void userTest() {
		// 
		User user = new User("shishido");
		assertEquals("shishido", user.username);

		// save
		user.save();
		User saved = User.get("shishido");
		assertNotNull(saved);
		assertEquals(user.username, saved.username);

		// save
		User notSaeved = User.get("yamada");
		assertNull(notSaeved);
	}

	@Test
	public void trackKeyTest() {
		User user = new User("shishidotadashi");
		assertNotNull(user.trackKey);
		user.save();
		//System.out.println(user.trackKey);
		
		User saved = User.getByTrackKey(user.trackKey);
		assertNotNull(saved);
		assertEquals(user.username, saved.username);
	}

}
