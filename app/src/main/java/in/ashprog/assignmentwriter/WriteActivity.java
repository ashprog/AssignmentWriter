package in.ashprog.assignmentwriter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static in.ashprog.assignmentwriter.App.sharedPreferences;
import static in.ashprog.assignmentwriter.AppPermission.createFolder;
import static in.ashprog.assignmentwriter.AppPermission.getPermission;
import static in.ashprog.assignmentwriter.AppPermission.hasPermission;

public class WriteActivity extends AppCompatActivity implements TextWatcher, View.OnTouchListener {

    EditText editText;
    TextView previewTV;
    RelativeLayout previewLayout;

    boolean permissionDialogShown = false;

    void initialize() {
        editText = findViewById(R.id.editText);
        previewTV = findViewById(R.id.previewTV);
        previewLayout = findViewById(R.id.previewLayout);
    }

    void initializeFormat() throws Exception {
        createFolder(this);

        File baseDir = getExternalFilesDir(null);
        if (baseDir == null) throw new Exception("External storage not available");

        //Setting Font
        File fontFile = new File(baseDir, "Writer/Font/myWriting.otf");
        if (fontFile.exists())
            previewTV.setTypeface(Typeface.createFromFile(fontFile));
        else {
            int font = sharedPreferences.getInt("font", 0);
            if (font == 0) {
                previewTV.setTypeface(ResourcesCompat.getFont(this, R.font.ashprog));
            } else if (font == 1) {
                previewTV.setTypeface(ResourcesCompat.getFont(this, R.font.vinay));
            }
        }

        //Setting Font Size
        previewTV.setTextSize(sharedPreferences.getFloat("fontSize", 24f));

        //Setting Line Space Multiplier
        previewTV.setLineSpacing(sharedPreferences.getFloat("lineSpace", -12), 1.0f);

        //Setting Font Color
        int color = sharedPreferences.getInt("color", 0);
        if (color == 0) {
            previewTV.setTextColor(Color.parseColor("#414A82"));
        } else if (color == 1) {
            previewTV.setTextColor(Color.parseColor("#000000"));
        }

        //Setting Page Background
        File pageFile = new File(baseDir, "Writer/Background/page_bg.png");
        if (pageFile.exists()) {
            previewLayout.setBackground(Drawable.createFromPath(pageFile.getPath()));
        } else {
            int page = sharedPreferences.getInt("page", 0);
            if (page == 0) {
                previewLayout.setBackgroundResource(R.drawable.page_bg_1);
            } else if (page == 1) {
                previewLayout.setBackgroundResource(R.drawable.page_bg_2);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        initialize();

        editText.addTextChangedListener(this);
        editText.setOnTouchListener(this);

        setPreviewLayout();

        editText.setHint("Use <Q> </Q> tag for adding question text.\nFor eg. <Q> Your text </Q>");
        previewTV.setText(Html.fromHtml(formatText("\nUse \"Q\" tag for adding question text.\n\nFor eg. <Q> Your text </Q>"), Html.FROM_HTML_MODE_COMPACT),
                TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermission(this)) {
            try {
                initializeFormat();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (!permissionDialogShown) {
            getPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 102)
            permissionDialogShown = true;

    }

    public void downloadPage(View view) {
        if (hasPermission(this)) {
            try {
                createFolder(this);

                File baseDir = getExternalFilesDir(null);
                if (baseDir == null) return;
                if (previewLayout.getWidth() <= 0 || previewLayout.getHeight() <= 0) {
                    Toast.makeText(this, "Preview not ready yet", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bitmap = Bitmap.createBitmap(previewLayout.getWidth(), previewLayout.getHeight(), Bitmap.Config.ARGB_8888);
                android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
                previewLayout.draw(canvas);
                File pageFile = new File(baseDir, "Writer/Images/page" + getCount() + ".png");
                try (FileOutputStream out = new FileOutputStream(pageFile)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    Toast.makeText(this, "Page saved at " + pageFile.getPath(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            getPermission(this);
        }
    }

    public void openSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void openGetHandwriting(View view) {
        startActivity(new Intent(this, GetHandwritingActivity.class));
    }

    public void openPdfConverter(View view) {
        startActivity(new Intent(this, PdfConverterActivity.class));
    }

    public void openCredits(View view) {
        CreditDialog creditDialog = new CreditDialog();
        creditDialog.showDialog(this);
    }

    public void clearAll(View view) {
        editText.setText("");
        Snackbar.make(view, "Input cleared.", BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    void setPreviewLayout() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density - 16f;

        if (dpWidth < 330) {
            previewLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (1.41 * displayMetrics.widthPixels)));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = s.toString();

        text = formatText(text);

        previewTV.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.editText) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    int getCount() {
        int count = sharedPreferences.getInt("imagesCount", 1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("imagesCount", count + 1);
        editor.commit();

        return count;
    }

    String patternMatcher(String text, String regex, String replacement) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        text = m.replaceAll(replacement);

        return text;
    }

    String formatText(String text) {
        text = patternMatcher(text, "<", "&lt;");
        text = patternMatcher(text, ">", "&gt;");
        text = patternMatcher(text, "\n", "<br/>");
        text = patternMatcher(text, "  ", "&nbsp;&nbsp;");
        text = patternMatcher(text, "&lt;Q&gt;", "<font color='#000000'>");
        text = patternMatcher(text, "&lt;/Q&gt;", "</font>");

        return text;
    }
}