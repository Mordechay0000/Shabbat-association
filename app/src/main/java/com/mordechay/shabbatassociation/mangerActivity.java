package com.mordechay.shabbatassociation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class mangerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtStatus;
    private Button btnEnableApp;
    private Button btnImportFile;
    private Button btnExportFile;
    private TextView txtViewFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manger);

        txtStatus = findViewById(R.id.manger_txt_status);
        btnEnableApp = findViewById(R.id.manger_btn_enable_app);
        btnEnableApp.setOnClickListener(this);
        btnImportFile = findViewById(R.id.manger_btn_import_file);
        btnImportFile.setOnClickListener(this);
        btnExportFile = findViewById(R.id.manger_btn_export_file);
        btnExportFile.setOnClickListener(this);
        txtViewFile = findViewById(R.id.manger_txt_file_view);
        loadingFileView();
    }

    private void loadingFileView() {
        txtStatus.setText(txtStatus.getText()+ "\n" + "טוען תצוגת קובץ...");
        String[] txtFile = readFileForEnableApp();
        if(txtFile != null) {
            for (String listApp : txtFile) {
                txtViewFile.setText(txtViewFile.getText() + listApp + "\n");
            }
            txtStatus.setText(txtStatus.getText()+ "\n" + "הקובץ נטען");
        }else{
            txtViewFile.setText("לא נמצא קובץ.");
            txtStatus.setText(txtStatus.getText()+ "\n" + "לא נמצא קובץ");
        }
    }

    public void enableAPP(){
        txtStatus.setText(txtStatus.getText()+ "\n" + "קורא קובץ");
        String[] list = readFileForEnableApp();
        if(list != null) {
            txtStatus.setText(txtStatus.getText()+ "\n" + "שולח פקודות לשחרור אפליקציות");
            sendCommand(list);
        }else{
            txtStatus.setText(txtStatus.getText()+ "\n" + "לא נמצא קובץ לשחרור האפליקציות");
        }
    }

    private String[] readFileForEnableApp() {
        String[] str = null;
        try {
            FileInputStream fis = new FileInputStream(new File(getFilesDir(),Data.FILE_NAME));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            fis.close();
            str = sb.toString().split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void sendCommand(String[] list) {
        Process p;
        DataOutputStream os = null;

        try {
            p = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(p.getOutputStream());
            txtStatus.setText(txtStatus.getText()+ "\n" + "התקבל הרשאת root (רוט/שורש)");
        } catch (IOException e) {
            e.printStackTrace();
            txtStatus.setText(txtStatus.getText()+ "\n" + "שגיאה בקבלת הרשאת root (רוט/שורש)");
        }
        txtStatus.setText(txtStatus.getText()+ "\n" + "משחרר אפליקציות");
        for (String app : list) {
            try {

                assert os != null;
                os.writeBytes("pm " + "enable " + app + "\n");
            } catch (IOException e) {
                txtStatus.setText(txtStatus.getText()+ "\n" + "שגיאה בשליחת פקודה לשחרור האפליקציות, הפקודה: " + "pm " + "enable " + app);
                throw new RuntimeException(e);
            }
        }
        try {
            assert os != null;
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            txtStatus.setText(txtStatus.getText()+ "\n" + "שגיאה ביציאה מהטרמינל");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnEnableApp) {
            txtStatus.setText(txtStatus.getText()+ "\n" + "מתחיל בשרור אפליקציות");
            enableAPP();
        } else if (v == btnImportFile) {
            txtStatus.setText(txtStatus.getText()+ "\n" + "בחירת קובץ לייבוא");
            importFile();
        } else if (v == btnExportFile) {
            txtStatus.setText(txtStatus.getText()+ "\n" + "בחירת נתיב לשמירת הקובץ המיוצא");
            exportFile();
        }
    }





    public void importFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        openActivityForImportFile.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> openActivityForImportFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Uri uri;
                    if (data != null) {
                        uri = data.getData();
                        saveFileToInternalStorage(uri);
                    }else {
                        txtStatus.setText(txtStatus.getText()+ "\n" + "שגיאה בקבלת נתיב הקובץ");
                    }
                }else {
                    txtStatus.setText(txtStatus.getText()+ "\n" + "לא נבחר קובץ");
                }
            });


    private void saveFileToInternalStorage(Uri fileUri) {
        txtStatus.setText(txtStatus.getText()+ "\n" + "מייבא קובץ");
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), Data.FILE_NAME));
            byte[] buffer = new byte[1024];
            int bufferLength;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();
            inputStream.close();
            txtStatus.setText(txtStatus.getText()+ "\n" + "הקובץ יובא בהצלחה");
        } catch (Exception e) {
            txtStatus.setText(txtStatus.getText()+ "\n" + "שגיאה בייבוא קובץ");
            e.printStackTrace();
        }
        loadingFileView();
    }

    public void exportFile() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date());
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TITLE, "list app - " + currentDate + ".mdy");
        openActivityForExportFile.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> openActivityForExportFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Uri uri;
                    if (data != null) {
                        uri = data.getData();
                        saveFileToExternalStorage(uri);
                    } else {
                        txtStatus.setText(txtStatus.getText() + "\n" + "שגיאה בקבלת נתיב הקובץ");
                    }
                } else {
                    txtStatus.setText(txtStatus.getText()+ "\n" + "לא נבחר קובץ");
            }}
    );

    private void saveFileToExternalStorage(Uri fileUri) {
        txtStatus.setText(txtStatus.getText()+ "\n" + "מייצא קובץ");
        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(getFilesDir(), Data.FILE_NAME)));
            OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
            byte[] buffer = new byte[1024];
            int bufferLength;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bufferLength);
            }
            outputStream.close();
            inputStream.close();
            txtStatus.setText(txtStatus.getText()+ "\n" + "הקובץ יוצא בהצלחה");
        } catch (Exception e) {
            txtStatus.setText(txtStatus.getText()+ "\n" + "שגיאה בייצוא קובץ");
            e.printStackTrace();
        }
    }

}