package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class User extends Model {
    
    public String email;
    public String password;
    public String fullname;
    public boolean isAdmin;
    
    public User(String email, String password, String fullname)
    {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    } 

    /**
     * get user with email and password
     * 
     * @param email
     * @param password
     * @return
     *
     */
    public static User connect(String email, String password)
    {
        return find("byEmailAndPassword", email, password).first();
    }
}

