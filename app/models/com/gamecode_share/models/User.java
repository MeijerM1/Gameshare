package models.com.gamecode_share.models;

import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;


    private String username;
    @Column(nullable = false)
    private byte[] salt;
    @Column(nullable = false)
    private byte[] hashedPassword;

    @Column(nullable = false)
    private int reputation = 0;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private boolean isVerified;

    @OneToMany( targetEntity=GameCode.class )
    private List<GameCode> codes= new ArrayList<>();

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
