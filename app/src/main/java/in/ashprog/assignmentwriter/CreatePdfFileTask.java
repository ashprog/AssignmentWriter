package in.ashprog.assignmentwriter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static in.ashprog.assignmentwriter.App.sharedPreferences;

class CreatePdfFileTask extends AsyncTask<String, Integer, File> {

    Context context;
    ArrayList<File> imagesList;
    ProgressDialog progressDialog;

    public CreatePdfFileTask(Context context, ArrayList<File> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Creating pdf...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected File doInBackground(String... strings) {
        File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Writer/Pdf/pdfFile (" + getCount() + ") .pdf");

        Document document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            for (int i = 0; i < imagesList.size(); i++) {
                Image image = Image.getInstance(imagesList.get(i).getAbsolutePath());

                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                image.setAbsolutePosition((document.getPageSize().getWidth() - image.getScaledWidth()) / 1.0f,
                        (document.getPageSize().getHeight() - image.getScaledHeight()) / 1.0f);

                document.add(image);
                document.newPage();
                publishProgress(i);
            }

            document.close();

            return pdfFile;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        this.progressDialog.setProgress(((values[0] + 1) * 100) / imagesList.size());
        StringBuilder sb = new StringBuilder();
        sb.append("Processing images (");
        sb.append(values[0] + 1);
        sb.append("/");
        sb.append(imagesList.size());
        sb.append(")");
        progressDialog.setTitle(sb.toString());
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);

        progressDialog.dismiss();
        if (file != null)
            Toast.makeText(context, "Pdf file saved at " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Some error occurred.", Toast.LENGTH_SHORT).show();
    }

    int getCount() {
        int count = sharedPreferences.getInt("pdfCount", 1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pdfCount", count + 1);
        editor.commit();

        return count;
    }

}
