package com.mordechay.shabbatassociation;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private ProgressBar prg;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prg = findViewById(R.id.main_progress);
        img = findViewById(R.id.main_img);

        String[] list = readFile();
        if (list != null) {
            sendCommand(list);
        }
    }

    private String[] readFile() {
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

        for (String appPackageName : list) {
            try {
                if (os != null) {
                    os.writeBytes("pm " + "disable " + appPackageName + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            if (os != null) {
                os.writeBytes("exit\n");
                os.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            // your task to be executed after 5 seconds
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                prg.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
            });
        }).start();
    }


}

