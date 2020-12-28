package in.ashprog.assignmentwriter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static in.ashprog.assignmentwriter.AppPermission.createFolder;
import static in.ashprog.assignmentwriter.AppPermission.getPermission;
import static in.ashprog.assignmentwriter.AppPermission.hasPermission;

public class GetHandwritingActivity extends AppCompatActivity {

    public static final String WRITER_EMAIL = "assignment.writer@aol.com";
    public static final String CALLIGRAPHR_URL = "https://www.calligraphr.com/en/docs/tutorial1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_handwriting);

        ((TextView) findViewById(R.id.emailTV)).setText(WRITER_EMAIL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 102 && hasPermission(this)) {
            try {
                createFolder();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void downloadTemplate(View view) {
        if (hasPermission(this)) {
            try {
                createFolder();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            File templateFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Writer/template.pdf");
            try {
                AssetManager assetManager = getAssets();

                FileOutputStream fos = new FileOutputStream(templateFile);
                InputStream fis = assetManager.open("template.pdf");

                openFileOutput(templateFile.getName(), Context.MODE_PRIVATE);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }

                fis.close();
                fos.close();

                Snackbar.make(view, "Template downloaded at " + templateFile.getPath(), BaseTransientBottomBar.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            getPermission(this);
        }
    }

    public void copyEmail(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Writer. Email", WRITER_EMAIL);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Email copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    public void openCalligraphr(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CALLIGRAPHR_URL)));
    }

    public void back(View view) {
        finish();
    }
}