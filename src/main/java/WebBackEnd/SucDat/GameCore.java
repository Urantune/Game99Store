package WebBackEnd.SucDat;

import WebBackEnd.model.Entity.*;
import WebBackEnd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameCore {

    @Autowired
    private UserService userService;

    public static String[] imageLinkGame(String linkTotal){
        String link[] = linkTotal.split("\\|\\|");
        return link;
    }

    public static String[] deceptionGame(String deception){
        String deceptions[] = deception.split("\\|\\|");
        return deceptions;
    }

    public String getUserName(UUID userId){
        return userService.findById(userId).getUsername();
    }

    public void payMent(User user,Game game, Vouncher vouncher){

    }

}
