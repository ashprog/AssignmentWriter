package in.ashprog.assignmentwriter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

import static in.ashprog.assignmentwriter.AppPermission.createFolder;
import static in.ashprog.assignmentwriter.AppPermission.getPermission;
import static in.ashprog.assignmentwriter.AppPermission.hasPermission;

public class PdfConverterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button createButton;
    CheckBox selectAllCB;

    ArrayList<File> imagesList;
    ImagesListAdapter imagesListAdapter;
    ArrayList<File> checkedImages;

    boolean permissionDialogShown = false;

    void initializeList() throws Exception {
        createFolder();

        File imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Writer/Images");
        final File[] images = imageFolder.listFiles();

        imagesList.clear();
        checkedImages.clear();
        imagesListAdapter.clearCheckedList();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (images != null) {
                    for (File image : images) {
                        if (image.getPath().endsWith(".png")) {
                            imagesList.add(image);
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imagesListAdapter.notifyDataSetChanged();
                        selectAllCB.setChecked(false);
                        if (imagesList.size() > 0) selectAllCB.setEnabled(true);
                        else selectAllCB.setEnabled(false);
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_converter);

        recyclerView = findViewById(R.id.recyclerView);
        createButton = findViewById(R.id.createButton);
        selectAllCB = findViewById(R.id.selectAllCB);

        imagesList = new ArrayList<>();
        checkedImages = new ArrayList<>();

        imagesListAdapter = new ImagesListAdapter(this, imagesList);
        imagesListAdapter.setSelectAllCBStateChangeListener(new ImagesListAdapter.SelectAllCBStateChanger() {
            @Override
            public void changeSelectAllCBState() {
                if (imagesListAdapter.getCheckedPositions().size() == imagesList.size())
                    selectAllCB.setChecked(true);
                else selectAllCB.setChecked(false);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(imagesListAdapter);

        selectAllCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) imagesListAdapter.selectAll();
                else imagesListAdapter.unSelectAll();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermission(this)) {
            try {
                initializeList();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else if (!permissionDialogShown)
            getPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 102)
            permissionDialogShown = true;
    }

    public void createPdf(View view) {
        if (hasPermission(this)) {
            try {
                createFolder();

                checkedImages.clear();

                for (int i = 0; i < imagesListAdapter.getCheckedPositions().size(); i++) {
                    checkedImages.add(imagesList.get(imagesListAdapter.getCheckedPositions().get(i)));
                }

                if (checkedImages.size() > 0)
                    new CreatePdfFileTask(this, checkedImages).execute();
                else
                    Toast.makeText(this, "Nothing selected.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            getPermission(this);
        }
    }

    public void back(View view) {
        finish();
    }
}