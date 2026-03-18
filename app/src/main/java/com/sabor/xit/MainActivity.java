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
                // Orienta o usuário a encontrar a pasta correta
                Toast.makeText(this, "Selecione a pasta: Android > data > com.dts.freefireth", Toast.LENGTH_LONG).show();
                
                // Abre o seletor de pastas (SAF)
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                // Tenta pré-carregar o caminho da pasta data (ajuda em alguns Androids)
                intent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata"));
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
            // Permissão persistente para não precisar pedir toda vez
            getContentResolver().takePersistableUriPermission(treeUri, 
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            if (pickedDir == null) return;

            // Se o arquivo antigo existir, ele é deletado para aplicar a nova escolha
            DocumentFile existingFile = pickedDir.findFile("sabor_xit_v2.cfg");
            if (existingFile != null) existingFile.delete();

            // Cria o novo arquivo de configuração dentro da pasta selecionada
            DocumentFile file = pickedDir.createFile("application/octet-stream", "sabor_xit_v2.cfg");
            if (file == null) throw new Exception("Erro: Escolha a pasta 'com.dts.freefireth' corretamente!");

            OutputStream out = getContentResolver().openOutputStream(file.getUri());

            // Verifica qual RadioButton do seu XML está marcado
            RadioButton rbAlta = findViewById(R.id.rbAlta);
            
            String config;
            // Se o rbAlta estiver marcado, usa a config 100% CAPA, senão usa a Normal (rbMedia)
            if (rbAlta != null && rbAlta.isChecked()) {
                config = "Aim_Force: 1.0\n" +
                         "Lock_On: Head\n" +
                         "Smooth: 0.01\n" + 
                         "FOV: 360\n" +    
                         "No_Recoil: True\n" +
                         "Auto_Headshot: True"; 
            } else {
                config = "Aim_Force: 0.50\n" +
                         "Lock_On: Chest\n" +
                         "Smooth: 0.60\n" +
                         "FOV: 90\n" +
                         "No_Recoil: False";
            }

            if (out != null) {
                out.write(config.getBytes());
                out.close();
            }

            Toast.makeText(this, "CONFIGURAÇÃO APLICADA!", Toast.LENGTH_LONG).show();

            // Tenta abrir o jogo automaticamente após a injeção
            Intent intentFF = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
            if (intentFF != null) {
                startActivity(intentFF);
            } else {
                Toast.makeText(this, "Free Fire não instalado!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro na Injeção: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
