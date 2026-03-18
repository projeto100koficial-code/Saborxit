package com.sabor.xit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import rikka.shizuku.Shizuku;
import java.io.FileOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnInjetar = findViewById(R.id.btnInjetar);
        RadioButton rbAlta = findViewById(R.id.rbAlta);
        RadioButton rbMedia = findViewById(R.id.rbMedia);

        // Lógica Visual: Muda a cor do texto ao selecionar
        rbAlta.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                rbAlta.setTextColor(Color.BLACK);
                rbMedia.setTextColor(Color.WHITE);
            }
        });
        rbMedia.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                rbMedia.setTextColor(Color.BLACK);
                rbAlta.setTextColor(Color.WHITE);
            }
        });

        btnInjetar.setOnClickListener(v -> {
            if (verificarShizuku()) {
                injetarComShizuku();
            }
        });
    }

    private boolean verificarShizuku() {
        if (Shizuku.isPreV11()) return false;
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            Shizuku.requestPermission(101);
            return false;
        }
    }

    private void injetarComShizuku() {
        try {
            // No Android 15 com Shizuku, acessamos o caminho via Shell
            String pastaFF = "/sdcard/Android/data/com.dts.freefireth/files/";
            String nomeArquivo = "sabor_xit_v2.cfg";
            
            RadioButton rbAlta = findViewById(R.id.rbAlta);
            String config = (rbAlta.isChecked()) ? 
                "Aim_Force: 1.0\nLock_On: Head\nSmooth: 0.01\nFOV: 360\nNo_Recoil: True" : 
                "Aim_Force: 0.50\nLock_On: Chest\nSmooth: 0.60\nFOV: 90";

            // Comando Shell para criar o arquivo direto na pasta protegida
            String comando = "echo \"" + config + "\" > " + pastaFF + nomeArquivo;
            
            Shizuku.newProcess(new String[]{"sh", "-c", comando}, null, null);

            Toast.makeText(this, "INJETADO VIA SHIZUKU!", Toast.LENGTH_SHORT).show();
            
            // Abre o jogo
            Intent it = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
            if (it != null) startActivity(it);

        } catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
