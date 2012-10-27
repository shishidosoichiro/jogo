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

public class TestAuthentication extends Controller {

  public static void authenticate() {
		Authentication.beforeAuthenticate();
		User user = User.get("shishidohinata");
		Authentication.onAuthenticated(user, true);
	}
}