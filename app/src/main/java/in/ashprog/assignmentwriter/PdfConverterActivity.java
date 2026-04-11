package in.ashprog.assignmentwriter;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static in.ashprog.assignmentwriter.AppPermission.createFolder;
import static in.ashprog.assignmentwriter.AppPermission.getPermission;
import static in.ashprog.assignmentwriter.AppPermission.hasPermission;

public class PdfConverterActivity extends AppCompatActivity {

    private static final String TAG = "PdfConverterActivity";
    RecyclerView recyclerView;
    Button createButton;
    CheckBox selectAllCB;
    ProgressBar progressBar;
    TextView progressText;

    ArrayList<File> imagesList;
    ImagesListAdapter imagesListAdapter;
    ArrayList<File> checkedImages;

    boolean permissionDialogShown = false;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String[]> pickImagesLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenMultipleDocuments(),
            uris -> {
                if (uris != null && !uris.isEmpty()) {
                    copyUrisToInternalStorage(uris);
                }
            }
    );

    void initializeList() throws Exception {
        createFolder(this);

        File baseDir = getExternalFilesDir(null);
        if (baseDir == null) return;
        File imageFolder = new File(baseDir, "Writer/Images");
        final File[] images = imageFolder.listFiles();

        imagesList.clear();
        checkedImages.clear();
        imagesListAdapter.clearCheckedList();

        executorService.execute(() -> {
            if (images != null) {
                for (File image : images) {
                    if (image.getPath().endsWith(".png")) {
                        imagesList.add(image);
                    }
                }
            }

            runOnUiThread(() -> {
                imagesListAdapter.notifyDataSetChanged();
                selectAllCB.setChecked(false);
                selectAllCB.setEnabled(!imagesList.isEmpty());
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_converter);

        recyclerView = findViewById(R.id.recyclerView);
        createButton = findViewById(R.id.createButton);
        selectAllCB = findViewById(R.id.selectAllCB);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        imagesList = new ArrayList<>();
        checkedImages = new ArrayList<>();

        imagesListAdapter = new ImagesListAdapter(this, imagesList);
        imagesListAdapter.setSelectAllCBStateChangeListener(() -> {
            selectAllCB.setChecked(imagesListAdapter.getCheckedPositions().size() == imagesList.size());
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(imagesListAdapter);

        selectAllCB.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked()) imagesListAdapter.selectAll();
            else imagesListAdapter.unSelectAll();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermission(this)) {
            try {
                initializeList();
            } catch (Exception e) {
                Log.e(TAG, "Error initializing list", e);
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

    public void pickImages(View view) {
        pickImagesLauncher.launch(new String[]{"image/*"});
    }

    private void copyUrisToInternalStorage(List<Uri> uris) {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        progressBar.setMax(uris.size());
        progressBar.setProgress(0);
        progressText.setText("Copying images...");

        executorService.execute(() -> {
            File baseDir = getExternalFilesDir(null);
            if (baseDir == null) return;
            File imageFolder = new File(baseDir, "Writer/Images");
            if (!imageFolder.exists()) imageFolder.mkdirs();

            for (int i = 0; i < uris.size(); i++) {
                Uri uri = uris.get(i);
                File destFile = new File(imageFolder, "picked_image_" + System.currentTimeMillis() + "_" + i + ".png");
                try (InputStream in = getContentResolver().openInputStream(uri);
                     FileOutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error copying uri", e);
                }
                int progress = i + 1;
                runOnUiThread(() -> progressBar.setProgress(progress));
            }

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
                try {
                    initializeList();
                } catch (Exception e) {
                    Log.e(TAG, "Error refreshing list", e);
                }
            });
        });
    }

    private final ActivityResultLauncher<String> createPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("application/pdf"),
            uri -> {
                if (uri != null) {
                    generatePdfToUri(uri);
                }
            }
    );

    private void generatePdfToUri(Uri uri) {
        new CreatePdfFileTask(this, checkedImages, uri, new CreatePdfFileTask.Callback() {
            @Override
            public void onPreExecute() {
                createButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                progressBar.setMax(checkedImages.size());
                progressBar.setProgress(0);
                progressText.setText(R.string.creating_pdf);
            }

            @Override
            public void onProgressUpdate(int progress, int total) {
                progressBar.setProgress(progress);
                progressText.setText(getString(R.string.processing_images, progress, total));
            }

            @Override
            public void onPostExecute(boolean success) {
                createButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
                if (success) {
                    Toast.makeText(PdfConverterActivity.this, "Pdf file saved successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PdfConverterActivity.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    public void createPdf(View view) {
        try {
            checkedImages.clear();

            for (int i = 0; i < imagesListAdapter.getCheckedPositions().size(); i++) {
                checkedImages.add(imagesList.get(imagesListAdapter.getCheckedPositions().get(i)));
            }

            if (!checkedImages.isEmpty()) {
                createPdfLauncher.launch("Assignment_" + System.currentTimeMillis() + ".pdf");
            } else
                Toast.makeText(this, "Nothing selected.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error creating PDF", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        finish();
    }
}