package dev.deploy4j.jdemo;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileSystemService {

    public record FileNode(String name, String path, String type, List<FileNode> children) {}

    public FileNode getFileTree(String rootPath) throws IOException {
        Path root = Paths.get(rootPath).normalize();
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            throw new IllegalArgumentException("Invalid repository path: " + rootPath);
        }
        return buildFileTree(root, root);
    }

    private FileNode buildFileTree(Path root, Path current) throws IOException {
        String name = current.equals(root) ? root.getFileName() != null ? root.getFileName().toString() : "/" : current.getFileName().toString();
        String relativePath = root.relativize(current).toString();
        if (relativePath.isEmpty()) {
            relativePath = "/";
        }

        if (Files.isDirectory(current)) {
            List<FileNode> children = new ArrayList<>();
            try (Stream<Path> paths = Files.list(current)) {
                paths.sorted((p1, p2) -> {
                    boolean d1 = Files.isDirectory(p1);
                    boolean d2 = Files.isDirectory(p2);
                    if (d1 && !d2) return -1;
                    if (!d1 && d2) return 1;
                    return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
                }).forEach(path -> {
                    try {
                        // Skip hidden files and common ignored directories
                        String fileName = path.getFileName().toString();
                        if (!fileName.startsWith(".") && 
                            !fileName.equals("node_modules") && 
                            !fileName.equals("target") &&
                            !fileName.equals("build")) {
                            children.add(buildFileTree(root, path));
                        }
                    } catch (IOException e) {
                        // Skip files that can't be read
                    }
                });
            }
            return new FileNode(name, relativePath, "directory", children);
        } else {
            return new FileNode(name, relativePath, "file", null);
        }
    }

    public String getFileContent(String rootPath, String relativePath) throws IOException {
        Path root = Paths.get(rootPath).normalize();
        Path filePath = root.resolve(relativePath).normalize();
        
        // Security check: ensure the file is within the root directory
        if (!filePath.startsWith(root)) {
            throw new SecurityException("Access denied: path outside repository root");
        }
        
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("File not found: " + relativePath);
        }
        
        // Read file content as string
        return Files.readString(filePath);
    }
}
