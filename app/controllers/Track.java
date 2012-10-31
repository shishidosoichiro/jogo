package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Track extends Controller {

	public static void put(
		String trackKey,
		String sitename,
		String sessionId,
		String casename,
		String eventname,
		String value
	) {
		
		// No user, return.
		User user = User.getByTrackKey(trackKey);
		if (user == null) forbidden();
		
		// No site, regist.
		Site site = Site.find("byName", sitename).first();
		if (site == null) site = new Site(user, sitename).save();

		// No testcase, regist.
		Testcase testcase = Testcase.find("byName", casename).first();
		if (testcase == null) testcase = new Testcase(site, casename).save();

		// put the event.
		new Event(site, testcase, sessionId, eventname, value).save();
		
		// successful.
		render();
	}

}