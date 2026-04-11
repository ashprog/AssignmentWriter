package in.ashprog.assignmentwriter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static in.ashprog.assignmentwriter.App.sharedPreferences;

class CreatePdfFileTask {

    private final Context context;
    private final ArrayList<File> imagesList;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onPreExecute();
        void onProgressUpdate(int progress, int total);
        void onPostExecute(File file);
    }

    private final Callback callback;

    public CreatePdfFileTask(Context context, ArrayList<File> imagesList, Callback callback) {
        this.context = context;
        this.imagesList = imagesList;
        this.callback = callback;
    }

    public void execute() {
        if (callback != null) callback.onPreExecute();

        executorService.execute(() -> {
            File baseDir = context.getExternalFilesDir(null);
            if (baseDir == null) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onPostExecute(null);
                });
                return;
            }

            File pdfFile = new File(baseDir, "Writer/Pdf/pdfFile (" + getCount() + ") .pdf");

            Document document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);
            try {
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();

                for (int i = 0; i < imagesList.size(); i++) {
                    Image image = Image.getInstance(imagesList.get(i).getAbsolutePath());

                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                            - document.rightMargin()) / image.getWidth()) * 100;
                    image.scalePercent(scaler);
                    image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    image.setAbsolutePosition((document.getPageSize().getWidth() - image.getScaledWidth()) / 1.0f,
                            (document.getPageSize().getHeight() - image.getScaledHeight()) / 1.0f);

                    document.add(image);
                    document.newPage();

                    int finalI = i;
                    mainHandler.post(() -> {
                        if (callback != null) callback.onProgressUpdate(finalI + 1, imagesList.size());
                    });
                }

                document.close();

                mainHandler.post(() -> {
                    if (callback != null) callback.onPostExecute(pdfFile);
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    if (callback != null) callback.onPostExecute(null);
                });
            }
        });
    }

    private int getCount() {
        int count = sharedPreferences.getInt("pdfCount", 1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pdfCount", count + 1);
        editor.apply();

        return count;
    }
}
