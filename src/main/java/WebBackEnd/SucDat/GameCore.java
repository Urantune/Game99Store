package WebBackEnd.SucDat;

public class GameCore {


    public static String[] imageLinkGame(String linkTotal){
        String link[] = linkTotal.split("\\|\\|");
        return link;
    }

    public static String[] deceptionGame(String deception){
        String deceptions[] = deception.split("\\|\\|");
        return deceptions;
    }

}
