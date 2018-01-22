package models.com.gamecode_share.models;

import models.com.gamecode_share.utility.StringGenerator;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private byte[] salt;
    @Column(nullable = false)
    private byte[] hashedPassword;

    @Column(nullable = false)
    private int reputation = 0;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private boolean isVerified;

    @Column(nullable = true)
    private String verificationCode;
    @Column(nullable = true)
    private Date verifyDate;

    @Column(nullable = false)
    private Date joinDate;

    private Role role;

    @OneToMany( targetEntity=GameCode.class )
    private List<GameCode> codes;

    public User() {
        role = Role.USER;
        codes = new ArrayList<>();
    }

    public User(String username)
    {
        role = Role.USER;
        codes = new ArrayList<>();
        this.username = username;
    }

    public void generateVerificationCode() {
        verifyDate = new Date();
        verificationCode = StringGenerator.generateRandom(50);
        isVerified = false;
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

    public List<GameCode> getCodes() {
        return codes;
    }

    public void setCodes(List<GameCode> codes) {
        this.codes = codes;
    }

    public void addCode(GameCode code) {
        codes.add(code);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Date getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
}
