package in.ashprog.assignmentwriter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

class AppPermission {
    static boolean hasPermission(Context context) {
        return true;
    }

    static void getPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},102);
    }

    static void createFolder(Context context) throws Exception {
        try {
            File baseDir = context.getExternalFilesDir(null);
            if (baseDir == null) throw new Exception("External storage not available");

            //Writer (Root) folder
            File folder = new File(baseDir, "Writer");
            if (!folder.exists()) folder.mkdirs();

            //Images Folder
            File imageFolder = new File(folder, "Images");
            if (!imageFolder.exists()) imageFolder.mkdirs();

            //Pdf Files Folder
            File pdfFolder = new File(folder, "Pdf");
            if (!pdfFolder.exists()) pdfFolder.mkdirs();

            //Font file folder
            File fontFolder = new File(folder, "Font");
            if (!fontFolder.exists()) fontFolder.mkdirs();

            //Background Page Folder
            File pageBgFolder = new File(folder, "Background");
            if (!pageBgFolder.exists()) pageBgFolder.mkdirs();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
