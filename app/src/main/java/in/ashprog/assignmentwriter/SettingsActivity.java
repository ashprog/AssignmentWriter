package in.ashprog.assignmentwriter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import static in.ashprog.assignmentwriter.App.sharedPreferences;
import static in.ashprog.assignmentwriter.AppPermission.createFolder;
import static in.ashprog.assignmentwriter.AppPermission.getPermission;
import static in.ashprog.assignmentwriter.AppPermission.hasPermission;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup colorRG, pageRG, fontRG;
    TextView demoTV, versionTV;
    EditText fontSizeET, lineSpaceET;

    boolean permissionDialogShown = false;

    void initialize() {
        colorRG = findViewById(R.id.colorRG);
        pageRG = findViewById(R.id.pageRG);
        fontRG = findViewById(R.id.fontRG);
        fontSizeET = findViewById(R.id.fontSizeET);
        lineSpaceET = findViewById(R.id.lineSpaceET);
        versionTV = findViewById(R.id.versionTV);
        demoTV = findViewById(R.id.demoTV);
    }

    void fetchFormat() {
        fontSizeET.setText(String.valueOf(sharedPreferences.getFloat("fontSize", 24f)));
        lineSpaceET.setText(String.valueOf(sharedPreferences.getFloat("lineSpace", -12f)));

        int color = sharedPreferences.getInt("color", 0);
        if (color == 0) {
            colorRG.check(R.id.blueRB);
        } else if (color == 1) {
            colorRG.check(R.id.blackRB);
        }

        int page = sharedPreferences.getInt("page", 0);
        if (page == 0) {
            pageRG.check(R.id.plainPageRB);
        } else if (page == 1) {
            pageRG.check(R.id.linedPageRB);
        }

        int font = sharedPreferences.getInt("font", 0);
        if (font == 0) {
            fontRG.check(R.id.ashprogRB);
            demoTV.setTypeface(ResourcesCompat.getFont(SettingsActivity.this, R.font.ashprog));
        } else if (font == 1) {
            fontRG.check(R.id.vinayRB);
            demoTV.setTypeface(ResourcesCompat.getFont(SettingsActivity.this, R.font.vinay));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialize();

        fetchFormat();

        setPageRGListener();

        setFontRGListener();

        showVersion();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermission(this)) {
            try {
                createFolder(this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (!permissionDialogShown) getPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 102)
            permissionDialogShown = true;

    }

    void setPageRGListener() {
        pageRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.plainPageRB) {
                    lineSpaceET.setText(String.valueOf(-12));
                } else if (checkedId == R.id.linedPageRB) {
                    lineSpaceET.setText(String.valueOf(-15));
                }
            }
        });
    }

    void setFontRGListener() {
        fontRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ashprogRB) {
                    fontSizeET.setText(String.valueOf(24));
                    demoTV.setTypeface(ResourcesCompat.getFont(SettingsActivity.this, R.font.ashprog));
                } else if (checkedId == R.id.vinayRB) {
                    fontSizeET.setText(String.valueOf(18));
                    demoTV.setTypeface(ResourcesCompat.getFont(SettingsActivity.this, R.font.vinay));
                }
            }
        });
    }

    void showVersion() {
        versionTV.setText(String.format("Version : %d.0", BuildConfig.VERSION_CODE));
    }

    public void save(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (fontSizeET.getText().length() > 0)
            editor.putFloat("fontSize", Float.parseFloat(fontSizeET.getText().toString()));

        if (lineSpaceET.getText().length() > 0)
            editor.putFloat("lineSpace", Float.parseFloat(lineSpaceET.getText().toString()));

        int colorCheckedId = colorRG.getCheckedRadioButtonId();
        if (colorCheckedId == R.id.blueRB) {
            editor.putInt("color", 0);
        } else if (colorCheckedId == R.id.blackRB) {
            editor.putInt("color", 1);
        }

        int pageCheckedId = pageRG.getCheckedRadioButtonId();
        if (pageCheckedId == R.id.plainPageRB) {
            editor.putInt("page", 0);
        } else if (pageCheckedId == R.id.linedPageRB) {
            editor.putInt("page", 1);
        }

        int fontCheckedId = fontRG.getCheckedRadioButtonId();
        if (fontCheckedId == R.id.ashprogRB) {
            editor.putInt("font", 0);
        } else if (fontCheckedId == R.id.vinayRB) {
            editor.putInt("font", 1);
        }

        editor.commit();

        Snackbar.make(view, "Format Saved !", BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    public void back(View view) {
        finish();
    }
}