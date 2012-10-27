package controllers;

import play.*;
import play.mvc.*;
import play.db.jpa.*;

import java.util.*;

import models.*;

public class Dashboard extends Main {

    public static void index() {
        render();
    }
}