package in.ashprog.assignmentwriter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static in.ashprog.assignmentwriter.AppPermission.createFolder;

public class GetHandwritingActivity extends AppCompatActivity {

    private static final String TAG = "GetHandwritingActivity";
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

        if (requestCode == 102) {
            try {
                createFolder(this);
            } catch (Exception e) {
                Log.e(TAG, "Error creating folder", e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void downloadTemplate(View view) {
        try {
            createFolder(this);
        } catch (Exception e) {
            Log.e(TAG, "Error creating folder", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        File baseDir = getExternalFilesDir(null);
        if (baseDir == null) return;
        File writerDir = new File(baseDir, "Writer");
        if (!writerDir.exists() && !writerDir.mkdirs()) return;
        File templateFile = new File(writerDir, "template.pdf");
        try (InputStream fis = getAssets().open("template.pdf");
             FileOutputStream fos = new FileOutputStream(templateFile)) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }

            Snackbar.make(view, getString(R.string.template_downloaded, templateFile.getPath()), BaseTransientBottomBar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error downloading template", e);
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void copyEmail(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Writer. Email", WRITER_EMAIL);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.email_copied, Toast.LENGTH_SHORT).show();
        }
    }

    public void openCalligraphr(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CALLIGRAPHR_URL)));
    }

    public void back(View view) {
        finish();
    }
}