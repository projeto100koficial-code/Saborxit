package com.sabor.xit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Garanta que o nome do layout seja activity_main.xml
        setContentView(R.layout.activity_main);

        Button btnInjetar = findViewById(R.id.btnInjetar);
        
        if (btnInjetar != null) {
            btnInjetar.setOnClickListener(v -> {
                // Abre o seletor de pastas do Android (SAF)
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 42);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && resultCode == RESULT_OK && data != null) {
            injetar(data.getData());
        }
    }

    private void injetar(Uri treeUri) {
        try {
            // Acessa a pasta selecionada pelo usuário
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            if (pickedDir == null) return;

            // Cria ou sobrescreve o arquivo de configuração
            DocumentFile file = pickedDir.createFile("text/plain", "sabor_xit_v2.cfg");
            if (file == null) throw new Exception("Não foi possível criar o arquivo");

            OutputStream out = getContentResolver().openOutputStream(file.getUri());

            RadioButton rbAlta = findViewById(R.id.rbAlta);
            
            String config;
            // Se a opção ALTA estiver marcada, injeta o 100% CAPA
            if (rbAlta != null && rbAlta.isChecked()) {
                config = "Aim_Force: 1.0\n" +
                         "Lock_On: Head\n" +
                         "Smooth: 0.01\n" + // Mira instantânea
                         "FOV: 360\n" +    // Puxa de qualquer lado
                         "No_Recoil: True\n" +
                         "Auto_Headshot: True\n" +
                         "Aim_Assist: True\n" +
                         "Range_Ignore: True"; // Ignora a distância
            } else {
                // Opção NORMAL (Legit)
                config = "Aim_Force: 0.50\n" +
                         "Lock_On: Chest\n" +
                         "Smooth: 0.60\n" +
                         "FOV: 90";
            }

            if (out != null) {
                out.write(config.getBytes());
                out.close();
            }

            Toast.makeText(this, "INJETADO COM SUCESSO!", Toast.LENGTH_LONG).show();

            // ABRE O FREE FIRE AUTOMATICAMENTE
            Intent intentFF = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
            if (intentFF != null) {
                startActivity(intentFF);
            } else {
                Toast.makeText(this, "Free Fire não encontrado!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro na Injeção: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
}
