package WebBackEnd.SucDat;

import WebBackEnd.Entity.Game;
import WebBackEnd.Entity.User;
import WebBackEnd.Entity.UserGame;
import WebBackEnd.Entity.Vouncher;
import WebBackEnd.repository.GameRepository;
import WebBackEnd.repository.UserGameRepository;
import WebBackEnd.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class GameCore {

    @Autowired
    UserService userService;

    @Autowired
    UserGameRepository userGameRepository;

    @Autowired
    GameRepository gameRepository;

    private static final Path STATIC_ROOT = Paths.get("src/main/resources/static");

    @Value("${gamestore.base-path:/home/urantune/gamestore}")
    private String basePathStr;

    Path BASE_PATH;

    @PostConstruct
    void initBasePath() {
        BASE_PATH = Paths.get(basePathStr).normalize();
        try { Files.createDirectories(BASE_PATH); }
        catch (Exception e) { throw new RuntimeException("Base path error"); }
    }

    public static String[] imageLinkGame(String s){ return s==null? new String[0] : s.split("\\|\\|"); }
    public static String[] deceptionGame(String s){ return s==null? new String[0] : s.split("\\|\\|"); }

    public String getUserName(UUID id){ return userService.findById(id).getUsername(); }

    public void payMent(User user, Game game, Vouncher v){}

    private String sanitizeName(String s){
        if(s==null) return "unnamed";
        String x = Paths.get(s).getFileName().toString();
        x = x.replaceAll("\\s+","_").replaceAll("[^a-zA-Z0-9._-]","");
        return x.isBlank()? "unnamed" : x;
    }

    private String sanitizeFolder(String s){
        if(s==null) return "unnamed";
        String x = s.trim().replaceAll("\\s+","-").replaceAll("[^a-zA-Z0-9._-]","");
        return x.isBlank()? "unnamed" : x;
    }

    public String saveToFolderKeepName(MultipartFile f, String folder){
        try{
            String safe = sanitizeName(f.getOriginalFilename());
            Path dir = STATIC_ROOT.resolve(folder);
            Files.createDirectories(dir);
            Path t = dir.resolve(safe);
            Files.write(t, f.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return folder+"/"+safe;
        }catch (Exception e){ throw new RuntimeException("Upload error"); }
    }

    public void deleteOldIfLocal(String link){
        if(link==null || link.isBlank()) return;
        String rel = link.startsWith("/")? link.substring(1): link;
        if(!(rel.startsWith("static/img/") || rel.startsWith("videos/"))) return;
        try{
            Path p = STATIC_ROOT.resolve(rel).normalize();
            if(p.startsWith(STATIC_ROOT)) Files.deleteIfExists(p);
        }catch (Exception ignored){}
    }

    public String handleMediaReplace(String oldLink, MultipartFile f, String url, String folder){
        try{
            if(f!=null && !f.isEmpty()){
                deleteOldIfLocal(oldLink);
                return saveToFolderKeepName(f, folder);
            }
            if(url!=null && !url.trim().isEmpty()) return url.trim();
            return oldLink;
        }catch (Exception e){ throw new RuntimeException("Media error"); }
    }

    private void cleanGamefileDir(Path dir){
        if(Files.isDirectory(dir)){
            try(Stream<Path> p = Files.list(dir)){
                p.forEach(x->{ try{Files.deleteIfExists(x);}catch(Exception ignored){} });
            }catch (Exception ignored){}
        }
    }

    public String saveGamePackage(MultipartFile f, String category, String gameName){
        try{
            if (f == null || f.isEmpty()) throw new IllegalArgumentException("Empty");

            String fn = f.getOriginalFilename() == null ? "" : f.getOriginalFilename().toLowerCase();

            boolean ok = fn.endsWith(".zip") || fn.endsWith(".rar") || fn.endsWith(".7z")
                    || fn.endsWith(".iso") || fn.endsWith(".jar");
            if (!ok) throw new IllegalArgumentException("Invalid format (.zip/.rar/.7z/.iso/.jar)");

            String cat = sanitizeFolder(category);
            String gname = sanitizeFolder(gameName);
            String safe = sanitizeName(f.getOriginalFilename());

            Path gamefile = BASE_PATH.resolve(cat).resolve(gname).resolve("gamefile").normalize();
            Files.createDirectories(gamefile);

            cleanGamefileDir(gamefile);

            Path t = gamefile.resolve(safe);
            Files.write(t, f.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return cat + "/" + gname + "/gamefile/" + safe;
        }catch (Exception e){
            throw new RuntimeException("Save package error");
        }
    }


    @RestController
    @RequestMapping("/api/files")
    public static class DownloadGame {

        @Autowired GameCore gameCore;
        @Autowired UserService userService;
        @Autowired UserGameRepository userGameRepository;
        @Autowired GameRepository gameRepository;

        @GetMapping("/download")
        public ResponseEntity<Resource> download(@RequestParam("path") String path,
                                                 @RequestParam("gameId") UUID gameId,
                                                 HttpSession session) {
            try {
                UUID userId = (UUID) session.getAttribute("id");
                if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

                var opt = gameRepository.findById(gameId);
                if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                Game game = opt.get();

                String reqPath = path == null ? "" : path.replaceFirst("^/","");
                String dbPath  = game.getLocate_game() == null ? "" : game.getLocate_game().replaceFirst("^/","");

                if (!dbPath.equals(reqPath)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

                User user = userService.findById(userId);
                if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

                UserGame ug = userGameRepository.findByGameAndUser(game, user);
                if (ug == null || ug.getStatus() != 1) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

                Path file = gameCore.BASE_PATH.resolve(reqPath).normalize();
                if (!file.startsWith(gameCore.BASE_PATH)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                if (!Files.exists(file)) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

                Resource r = new UrlResource(file.toUri());
                String fname = file.getFileName().toString();
                String encoded = URLEncoder.encode(fname, StandardCharsets.UTF_8).replace("+","%20");
                String type = Files.probeContentType(file);
                if (type == null) type = MediaType.APPLICATION_OCTET_STREAM_VALUE;

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''"+encoded)
                        .contentType(MediaType.parseMediaType(type))
                        .contentLength(Files.size(file))
                        .body(r);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
    }

}
