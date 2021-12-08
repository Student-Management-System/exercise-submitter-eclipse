package net.ssehub.teaching.exercise_submitter.eclipse.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This class has some FileUtils in it.
 * 
 * @author lukas
 * @author Adam
 */
public class FileUtils {

    /**
     * No instances.
     */
    private FileUtils() {
    }
    
    /**
     * Deletes the content of a folder.
     * 
     * @param folder The folder to delete.
     * 
     * @throws IOException If deleting a file fails.
     */
    public static void deleteContentInFolder(Path folder) throws IOException {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
}
