package WebBackEnd.model.Entity;


import WebBackEnd.SucDat.GameCore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue
    @Column(name = "game_id", columnDefinition = "uniqueidentifier")
    private UUID gameId;
    @Column(name = "gameName")
    private String gameName;
    @Column(name = "price")
    private double price;
    @Column(name = "version")
    private String game_version;
    @Column(name = "status")
    private String status;
    @Column(name = "category")
    private String game_category;
    @Column(name ="locateGame")
    private String locate_game;
    @Column(name = "deception")
    private String deception;
    @Column(name ="imageLinks")
    private String imageLinks;

    public Game(String locate_game, String game_category, String status, String game_version, double price, String gameName, UUID gameId) {
        this.locate_game = locate_game;
        this.game_category = game_category;
        this.status = status;
        this.game_version = game_version;
        this.price = price;
        this.gameName = gameName;
        this.gameId = gameId;
    }

    public Game() {

    }

    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public String getDeception() {
        return deception;
    }

    public void setDeception(String deception) {
        this.deception = deception;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String game_name) {
        this.gameName = game_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double game_price) {
        this.price = game_price;
    }

    public String getGame_version() {
        return game_version;
    }

    public void setGame_version(String game_version) {
        this.game_version = game_version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String game_status) {
        this.status = game_status;
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

    public String[] getDeceptions() {
        return GameCore.deceptionGame(deception);
    }

    public String[] getLinkImage(){
        return GameCore.imageLinkGame(imageLinks);
    }
}




