package com.mordechay.shabbatassociation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private ProgressBar prg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prg = findViewById(R.id.main_progress);

        String[] list = readFile();
        sendCommand(list);
        ImageView img = findViewById(R.id.main_img);
        prg.setVisibility(View.GONE);
        img.setVisibility(View.VISIBLE);
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

                os.writeBytes("pm " + "disable " + list[i] + "\n");
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
}

