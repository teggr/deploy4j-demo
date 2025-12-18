package dev.deploy4j.jdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/explore")
@RequiredArgsConstructor
public class FileExplorerController {

    private final Applications applications;
    private final FileSystemService fileSystemService;

    @GetMapping("/{id}")
    public String explore(@PathVariable Long id, Model model) {
        Applications.ApplicationRecord app = applications.getApplication(id);
        model.addAttribute("application", app);
        return "explore";
    }

    @GetMapping("/api/files/{id}")
    @ResponseBody
    public ResponseEntity<?> getFileTree(@PathVariable Long id) {
        try {
            Applications.ApplicationRecord app = applications.getApplication(id);
            if (app.repositoryPath() == null || app.repositoryPath().isEmpty()) {
                return ResponseEntity.badRequest().body("Repository path not configured");
            }
            FileSystemService.FileNode tree = fileSystemService.getFileTree(app.repositoryPath());
            return ResponseEntity.ok(tree);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error reading repository: " + e.getMessage());
        }
    }

    @GetMapping("/api/file-content/{id}")
    @ResponseBody
    public ResponseEntity<?> getFileContent(@PathVariable Long id, @RequestParam String path) {
        try {
            Applications.ApplicationRecord app = applications.getApplication(id);
            if (app.repositoryPath() == null || app.repositoryPath().isEmpty()) {
                return ResponseEntity.badRequest().body("Repository path not configured");
            }
            String content = fileSystemService.getFileContent(app.repositoryPath(), path);
            return ResponseEntity.ok(new FileContentResponse(content, path));
        } catch (IOException | SecurityException e) {
            return ResponseEntity.badRequest().body("Error reading file: " + e.getMessage());
        }
    }

    public record FileContentResponse(String content, String path) {}
}
