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
        setContentView(R.layout.activity_main);
        
        Button btnInjetar = findViewById(R.id.btnInjetar);
        if (btnInjetar != null) {
            btnInjetar.setOnClickListener(v -> {
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
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            if (pickedDir == null) return;

            DocumentFile file = pickedDir.createFile("text/plain", "sabor_xit_v2.cfg");
            if (file == null) throw new Exception("Erro ao criar arquivo");

            OutputStream out = getContentResolver().openOutputStream(file.getUri());

            RadioButton rbAlta = findViewById(R.id.rbAlta);
            RadioButton rbMedia = findViewById(R.id.rbMedia);
            
            String config;
            if (rbAlta != null && rbAlta.isChecked()) {
                // MODO 100% CAPA: FOCO EM CURTA/MÉDIA DISTÂNCIA
                config = "Aim_Force: 1.0\n" +
                         "Lock_On: Head\n" +
                         "Smooth: 0.01\n" +
                         "FOV: 360\n" +
                         "No_Recoil: True\n" +
                         "Auto_Headshot: True\n" +
                         "Aim_Assist: True\n" +
                         "Range_Ignore: True";
            } else if (rbMedia != null && rbMedia.isChecked()) {
                config = "Aim_Force: 0.75\nLock_On: Head\nSmooth: 0.25\nFOV: 120";
            } else {
                config = "Aim_Force: 0.50\nLock_On: Chest\nSmooth: 0.50\nFOV: 90";
            }

            if (out != null) {
                out.write(config.getBytes());
                out.close();
            }

            Toast.makeText(this, "100% CAPA ATIVADO!", Toast.LENGTH_LONG).show();
            
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro na Injeção!", Toast.LENGTH_SHORT).show();
        }
    }
                  }
