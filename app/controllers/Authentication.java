package controllers;

import play.*;
import play.db.jpa.*;
import play.mvc.*;
import play.libs.*;
import play.utils.*;
import models.*;

public class Authentication extends Controller {

	/**
	 * This method set user's authentication info to session.
	 * @return
	 */
	static String setConnected(String username, boolean remember) {
		// Mark user as connected
		session.put("username", username);
		// Remember if needed
		if(remember) {
			response.setCookie("rememberme", Crypto.sign(username) + "-" + username, "30d");
		}
		return username;
	}

	/**
	 * This method returns the current connected username
	 * @return
	 */
	static String connected() {
		String username = session.get("username");
		if ( username != null ) return username;

		// if cookie contatins rememberme and rememberme is correct, ok.
		Http.Cookie remember = request.cookies.get("rememberme");
		if(remember != null && remember.value.indexOf("-") > 0) {
			String sign = remember.value.substring(0, remember.value.indexOf("-"));
			username = remember.value.substring(remember.value.indexOf("-") + 1);
			if(Crypto.sign(username).equals(sign)) {
				session.put("username", username);
				return username;
			}
		}
		return null;
	}

	/**
	 * Indicate if a user is currently connected
	 * @return  true if the user is connected
	 */
	static boolean isConnected() {
		return connected() != null;
	}

	@NoTransaction
	public static void logout() {
		session.clear();
		response.removeCookie("rememberme");
		flash.success("success.logout");
		redirect("/");
	}
	
	static void redirectToOriginalURL() {
		String url = session.get("url");
		session.remove("url");
		if(url == null) url = "/";
		else if("/login".equals(url)) url = "/";
		Logger.debug("redirect to " + url);
		redirect(url);
	}

	static User getConnectedUser() {
	  if( ! isConnected()) return null;
		return User.findById(new Long(connected()));
	}

	static void onAuthenticated(User user, boolean remember) {
		// login successful
		Logger.info("login successful. welcome " + user.username);
		flash.success("success.login");
		// Mark user as connected
		Authentication.setConnected(user.id.toString(), remember);
		// Redirect to the original URL (or /)
		Authentication.redirectToOriginalURL();
	}

	@Transactional(readOnly=true)
	static void beforeAuthenticate() {
	}
}
