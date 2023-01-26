package com.mordechay.shabbatassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class mangerActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnEnableApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manger);

btnEnableApp = findViewById(R.id.manger_btn_enable_app);
btnEnableApp.setOnClickListener(this);

    }

    public void enableAPP(){
        String[] list = readFile();
        sendCommand(list);
    }

    private String[] readFile() {
        String[] str = null;
        try {
            FileInputStream fis = new FileInputStream(new File(getFilesDir(),"file.txt"));
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
        Process p = null;
        DataOutputStream os = null;

        try {
            p = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(p.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < list.length; i++) {
            try {

                os.writeBytes("pm " + "enable " + list[i] + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnEnableApp)
            enableAPP();
    }
}