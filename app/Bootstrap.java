
import models.*;
import java.io.*;
import play.*;
import play.jobs.*;
import play.test.*;
import play.vfs.*; 

@OnApplicationStart
public class Bootstrap extends Job { 
 
    public void doJob() {
        // Check if the database is empty
        if(User.count() == 0) {
            Fixtures.deleteAll();
            Fixtures.loadModels("data.yml");
        }
    }
}