package models.com.gamecode_share.models;

import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;

@Entity
@Table(name = "gamecode")
public class GameCode {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long Id;

    @Column(name="code")
    @ColumnTransformer(
            read="decrypt(code)",
            write="encrypt(?)")
    private String code;

    @Column(nullable = false)
    private String gameName;

    public GameCode() { }


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
