package WebBackEnd.SucDat;

import WebBackEnd.Entity.Game;
import WebBackEnd.Entity.User;
import WebBackEnd.Entity.Vouncher;
import WebBackEnd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
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

    public void payMent(User user, Game game, Vouncher vouncher){

    }
    private static final Path STATIC_ROOT = Paths.get("src/main/resources/static");


    private String sanitizeName(String original) {
        if (original == null) return "unnamed";
        String base = Paths.get(original).getFileName().toString();
        base = base.replaceAll("\\s+", "_");                        // space -> _
        base = base.replaceAll("[^a-zA-Z0-9._-]", "");              // lọc ký tự lạ
        if (base.isBlank()) base = "unnamed";
        return base;
    }

     public String saveToFolderKeepName(MultipartFile file, String subFolder) {
        try {
            String safeName = sanitizeName(file.getOriginalFilename());
            Path targetDir = STATIC_ROOT.resolve(subFolder);
            Files.createDirectories(targetDir);

            Path target = targetDir.resolve(safeName);


            Files.write(target, file.getBytes(), java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);


            return subFolder + "/" + safeName;
        } catch (Exception e) {
            throw new RuntimeException("Upload error: " + e.getMessage(), e);
        }
    }


    public void deleteOldIfLocal(String oldLink) {
        if (oldLink == null || oldLink.isBlank()) return;

        String rel = oldLink.startsWith("/") ? oldLink.substring(1) : oldLink;
        if (!(rel.startsWith("img/") || rel.startsWith("videos/"))) return;

        try {
            Path p = STATIC_ROOT.resolve(rel).normalize();
            if (p.startsWith(STATIC_ROOT)) {
                Files.deleteIfExists(p);
            }
        } catch (Exception ignore) {}
    }


    public String handleMediaReplace(String oldLink, MultipartFile file, String imageUrl, String subFolder) {
        try {
            if (file != null && !file.isEmpty()) {
                deleteOldIfLocal(oldLink);
                return saveToFolderKeepName(file, subFolder);
            }
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                return imageUrl.trim();
            }
            return oldLink;
        } catch (Exception e) {
            throw new RuntimeException("Media error: " + e.getMessage(), e);
        }
    }
}








