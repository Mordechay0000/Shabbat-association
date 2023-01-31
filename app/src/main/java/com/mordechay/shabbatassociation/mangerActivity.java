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

    private Button btnEnableApp;
    private Button btnImportFile;
    private Button btnExportFile;
    private TextView txtViewFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manger);

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
        String[] txtFile = readFileForEnableApp();
        if(txtFile != null) {
            for (String listApp : txtFile) {
                txtViewFile.setText(txtViewFile.getText() + listApp + "\n");
            }
        }else{
            txtViewFile.setText("לא נמצא קובץ.");
        }
    }

    public void enableAPP(){
        String[] list = readFileForEnableApp();
        sendCommand(list);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String app : list) {
            try {

                assert os != null;
                os.writeBytes("pm " + "enable " + app + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            assert os != null;
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnEnableApp) {
            enableAPP();
        } else if (v == btnImportFile) {
            importFile();
        } else if (v == btnExportFile) {
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
                    }
                }
            });


    private void saveFileToInternalStorage(Uri fileUri) {
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
        } catch (Exception e) {
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
                    }
                }
            });

    private void saveFileToExternalStorage(Uri fileUri) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}