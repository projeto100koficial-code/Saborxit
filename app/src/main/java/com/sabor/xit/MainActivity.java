package com.sabor.xit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// IMPORTANTE: Importa os recursos do seu pacote específico
import com.sabor.xit.R;

// Imports do Shizuku v13.1.0
import dev.rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {

    private static final int SHIZUKU_CODE = 101;

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
            } else {
                // Solicita permissão se não tiver
                try {
                    Shizuku.requestPermission(SHIZUKU_CODE);
                } catch (Exception e) {
                    Toast.makeText(this, "Shizuku não está rodando!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean verificarShizuku() {
        try {
            if (Shizuku.isPreV11()) return false;
            return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    private void injetarComShizuku() {
        try {
            // Caminho da pasta files do Free Fire
            String pastaFF = "/sdcard/Android/data/com.dts.freefireth/files/";
            String nomeArquivo = "sabor_xit_v2.cfg";
            
            RadioButton rbAlta = findViewById(R.id.rbAlta);
            
            // Configuração formatada
            String config = (rbAlta.isChecked()) ? 
                "Aim_Force:1.0;Lock_On:Head;Smooth:0.01;FOV:360;No_Recoil:True" : 
                "Aim_Force:0.50;Lock_On:Chest;Smooth:0.60;FOV:90";

            // Comando Shell via Shizuku
            String comando = "mkdir -p " + pastaFF + " && echo '" + config + "' > " + pastaFF + nomeArquivo;
            
            // Executa o comando
            Process process = Shizuku.newProcess(new String[]{"sh", "-c", comando}, null, null);
            process.waitFor();

            if (process.exitValue() == 0) {
                Toast.makeText(this, "INJETADO COM SUCESSO!", Toast.LENGTH_SHORT).show();
                
                // Abre o Free Fire
                Intent it = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
                if (it != null) {
                    startActivity(it);
                } else {
                    Toast.makeText(this, "Free Fire não encontrado!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Erro no comando Shell!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao injetar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
