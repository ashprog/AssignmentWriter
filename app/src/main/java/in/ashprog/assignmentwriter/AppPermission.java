package in.ashprog.assignmentwriter;

import android.content.Context;

import java.io.File;

class AppPermission {
    @SuppressWarnings("unused")
    static boolean hasPermission(Context context) {
        return true;
    }

    @SuppressWarnings("unused")
    static void getPermission(android.app.Activity activity) {
    }

    static void createFolder(Context context) throws Exception {
        try {
            File baseDir = context.getExternalFilesDir(null);
            if (baseDir == null) throw new Exception("External storage not available");

            //Writer (Root) folder
            File folder = new File(baseDir, "Writer");
            if (!folder.exists()) {
                if (!folder.mkdirs()) throw new Exception("Could not create folder: " + folder.getPath());
            }

            //Images Folder
            File imageFolder = new File(folder, "Images");
            if (!imageFolder.exists()) {
                if (!imageFolder.mkdirs()) throw new Exception("Could not create folder: " + imageFolder.getPath());
            }

            //Pdf Files Folder
            File pdfFolder = new File(folder, "Pdf");
            if (!pdfFolder.exists()) {
                if (!pdfFolder.mkdirs()) throw new Exception("Could not create folder: " + pdfFolder.getPath());
            }

            //Font file folder
            File fontFolder = new File(folder, "Font");
            if (!fontFolder.exists()) {
                if (!fontFolder.mkdirs()) throw new Exception("Could not create folder: " + fontFolder.getPath());
            }

            //Background Page Folder
            File pageBgFolder = new File(folder, "Background");
            if (!pageBgFolder.exists()) {
                if (!pageBgFolder.mkdirs()) throw new Exception("Could not create folder: " + pageBgFolder.getPath());
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
