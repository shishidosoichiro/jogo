package controllers;

import play.*;
import play.cache.*;
import play.libs.*;
import play.libs.OAuth.ServiceInfo;
import play.mvc.*;
import play.utils.*;

import java.util.*;
import com.google.gson.*;

import models.*;

public class Twitter extends Controller {

	static final private ServiceInfo SERVICE_INFO = new ServiceInfo(
			"http://twitter.com/oauth/request_token",
			"http://twitter.com/oauth/access_token",
			"http://twitter.com/oauth/authorize",
			"15QSax0yFALB9bAEp82pwQ",
			"OkJdTbsKqDTk1Y9gkHXLahlQmoe9EfUn6NhbARbLkw"
	);

  public static void authenticate() {
		Authentication.beforeAuthenticate();

    	// check authentication
		if ( ! OAuth.isVerifierResponse() ) {
			Logger.debug("is NOT Verifier Response");
			requestAccessToken();
		}
    	// after verified, get token and secret.
		Logger.debug("is Verifier Response");
		TwitterAccount pair = getAccount();
		if ( pair == null ) {
			redirect("/");
		}
 
 		// get account info from twiter.
		JsonObject json = Twitter.getAccountInfo(pair);
		pair.twitterId = json.get("id").getAsString();
		pair.screenName = json.get("screen_name").getAsString();
		pair.name = json.get("name").getAsString();
		pair.profileImageUrl = json.get("profile_image_url").getAsString();
		pair.description = json.get("description").getAsString();
		
		// if twitter account doesn't exists yet,
		// create user and save twitter account.
		Logger.debug("twitterId:" + pair.twitterId);
		TwitterAccount account = TwitterAccount.find("twitterId", pair.twitterId).first();
		User user;
		if ( account == null ) {
			Logger.debug("create account.");
			pair.user = new User(pair.screenName);
		}
		// if twitter account already exists,
		// change old account to new.
		else {
			Logger.debug("change account.");
			pair.user = account.user;
			account.user = null;
			pair.user.username = pair.screenName;
		}
		
		// set properties.
		pair.user.fullname = pair.name;
		pair.user.iconUrl = pair.profileImageUrl;
		pair.user.profile = pair.description;
		pair.user.save();
		pair.save();

		// redirect 
		Authentication.onAuthenticated(pair.user, true);
	}

	static private void requestAccessToken() {
		OAuth service = OAuth.service(SERVICE_INFO);
		OAuth.Response oauthResponse = service.retrieveRequestToken();
		if (oauthResponse.error != null) {
			Logger.error("Error connecting to twitter: " + oauthResponse.error);
			redirect("/");
		}

		Cache.set(session.getId() + "-token", oauthResponse.token);
		Cache.set(session.getId() + "-secret", oauthResponse.secret);
		redirect(service.redirectUrl(oauthResponse.token));

	}

	static private TwitterAccount getAccount() {
		String token = Cache.get(session.getId() + "-token", String.class);
		String secret = Cache.get(session.getId() + "-secret", String.class);
		// We got the verifier; now get the access token, store it and back to index
		OAuth.Response oauthResponse = OAuth.service(Twitter.SERVICE_INFO).retrieveAccessToken(token, secret);
		if (oauthResponse.error != null) {
			Logger.error("Error connecting to twitter: " + oauthResponse.error);
			return null;
		}

		Logger.debug("token:" + token);
		TwitterAccount account = new TwitterAccount();
		account.token = oauthResponse.token;
		account.secret = oauthResponse.secret;
		return account;
	}

	static private JsonObject getAccountInfo(TwitterAccount account) {
		// get username.
		String url = "http://api.twitter.com/1/account/verify_credentials.json";
		return WS.url(url).oauth(Twitter.SERVICE_INFO, account.token, account.secret).get().getJson().getAsJsonObject();
	}
}