package WebBackEnd.model.Entity;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "games")
public class Game {


    @Id
    @UuidGenerator
    @Column(name = "game_id", length = 36, nullable = false, updatable = false)
    private String gameId ;
    @Column(name = "gameName")
    private String game_name;
    @Column(name = "price")
    private double game_price;
    @Column(name = "version")
    private String game_version;
    @Column(name = "status")
    private String game_status;
    @Column(name = "category")
    private String game_category;
    @Column(name ="locateGame")
    private String locate_game;

    public Game(String locate_game, String game_category, String game_status, String game_version, double game_price, String game_name, String gameId) {
        this.locate_game = locate_game;
        this.game_category = game_category;
        this.game_status = game_status;
        this.game_version = game_version;
        this.game_price = game_price;
        this.game_name = game_name;
        this.gameId = gameId;
    }

    public Game() {

    }

    public String getId() {
        return gameId;
    }

    public void setId(String gameId) {
        this.gameId = gameId;
    }


    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public double getGame_price() {
        return game_price;
    }

    public void setGame_price(double game_price) {
        this.game_price = game_price;
    }

    public String getGame_version() {
        return game_version;
    }

    public void setGame_version(String game_version) {
        this.game_version = game_version;
    }

    public String getGame_status() {
        return game_status;
    }

    public void setGame_status(String game_status) {
        this.game_status = game_status;
    }

    public String getGame_category() {
        return game_category;
    }

    public void setGame_category(String game_category) {
        this.game_category = game_category;
    }

    public String getLocate_game() {
        return locate_game;
    }

    public void setLocate_game(String locate_game) {
        this.locate_game = locate_game;
    }
}
