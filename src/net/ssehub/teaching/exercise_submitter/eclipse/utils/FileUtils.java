package net.ssehub.teaching.exercise_submitter.eclipse.utils;

import java.io.File;

/**
 * This class has some FileUtils in it.
 * @author lukas
 *
 */
public class FileUtils {
    /**
     * Deletes the contant of a folder.
     * @param folder
     */
    public static void deleteContentInFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { 
            for (File f: files) {
                if (f.isDirectory()) {
                    deleteContentInFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
    
}
