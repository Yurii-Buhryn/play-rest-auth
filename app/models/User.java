package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

@Entity
public class User extends Model {

	private static final long serialVersionUID = 1L;

	@Id
    public Long id;

	@Column(unique = true)
    private String authToken;
    
    @Column(unique = true)
    public String activationToken;
    
    @Column(length = 256, unique = true, nullable = false)
    @Constraints.MaxLength(256)
    @Constraints.Required
    @Constraints.Email
    private String emailAddress;

    @Column(length = 64, nullable = false)
    private byte[] shaPassword;
    
    @Transient
    @Constraints.Required
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    public Boolean active = Boolean.FALSE;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        shaPassword = getSha512(password);
    }

    @Column(length = 256, nullable = false)
    @Constraints.Required
    @Constraints.MinLength(2)
    @Constraints.MaxLength(256)
    public String fullName;

    @Column(nullable = false)
    public Date creationDate;

    public String createToken() {
    	if(authToken == null){
    		authToken = UUID.randomUUID().toString() + id.toString();
    		save();    		
    	}
        return authToken;
    }
    
    public String generateActivationToken(){
    	if(activationToken == null){
    		activationToken = UUID.randomUUID().toString() + id.toString();
    		save();    		
    	}
        return activationToken;
    }

    public void deleteAuthToken() {
        authToken = null;
        save();
    }
    
    public User() {
        this.creationDate = new Date();
        this.active = false;
    }

    public User(String emailAddress, String password, String fullName) {
        setEmailAddress(emailAddress);
        setPassword(password);
        this.fullName = fullName;
        this.creationDate = new Date();
        this.active = false;
    }
    
    @Transactional
    public Boolean activate(String activationToken){
    	if(activationToken.equals(this.activationToken)){
    		active = true;
    		activationToken = null;
    		save();
    		return true;
    	} else {
    		return false;
    	}
    }


    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
    
    public static User findByAuthToken(String authToken) {
        if (authToken == null) {
            return null;
        }

        try  {
            return find.where().eq("authToken", authToken).findUnique();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static User findByActivateToken(String activationToken){
    	return find.where().eq("activationToken", activationToken).findUnique();
    }
    public static User findByEmailAddressAndPassword(String emailAddress, String password) {
        // todo: verify this query is correct.  Does it need an "and" statement?
        return find.where().eq("emailAddress", emailAddress.toLowerCase()).eq("shaPassword", getSha512(password)).findUnique();
    }

}
