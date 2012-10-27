package controllers;

import play.*;
import play.db.jpa.*;
import play.cache.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Main extends Controller {

	static User loginuser;

	/**
	 Set the connected user and its settings, before a process.
	 */
	@Before
	@Transactional(readOnly=true)
	static void before() {
		User loginuser = Authentication.getConnectedUser();
		if ( loginuser == null ) Application.index(); 
		renderArgs.put("loginuser", loginuser);
		
		// keep url in session.
		session.put("url", "GET".equals(request.method) ? request.url : "/");
	}
}