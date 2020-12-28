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
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    static void getPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},102);
    }

    static void createFolder() throws Exception{
        try {
            //Writer (Root) folder
            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Writer");
            if (!folder.exists()) folder.mkdir();

            //Images Folder
            File imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Writer/Images");
            if (!imageFolder.exists()) imageFolder.mkdir();

            //Pdf Files Folder
            File pdfFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Writer/Pdf");
            if (!pdfFolder.exists()) pdfFolder.mkdir();

            //Font file folder
            File fontFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Writer/Font");
            if (!fontFolder.exists()) fontFolder.mkdir();

            //Background Page Folder
            File pageBgFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Writer/Background");
            if (!pageBgFolder.exists()) pageBgFolder.mkdir();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
