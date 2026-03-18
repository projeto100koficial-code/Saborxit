package com.sabor.xit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SAF = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnInjetar = findViewById(R.id.btnInjetar);
        
        btnInjetar.setOnClickListener(v -> {
            // Explica ao usuário o que fazer
            Toast.makeText(this, "Clique em 'USAR ESTA PASTA' na próxima tela", Toast.LENGTH_LONG).show();
            
            abrirSeletorPastas();
        });
    }

    private void abrirSeletorPastas() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        // Tenta abrir diretamente na pasta do Free Fire para facilitar
        String folderPath = "primary:Android/data/com.dts.freefireth";
        Uri uri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", folderPath);
        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        
        startActivityForResult(intent, REQUEST_CODE_SAF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SAF && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            
            // Salva a permissão para não pedir de novo
            getContentResolver().takePersistableUriPermission(treeUri, 
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            
            processarInjecao(treeUri);
        }
    }

    private void processarInjecao(Uri treeUri) {
        try {
            DocumentFile root = DocumentFile.fromTreeUri(this, treeUri);
            if (root == null) return;

            // Deleta arquivo antigo se existir
            DocumentFile fileExistente = root.findFile("sabor_xit_v2.cfg");
            if (fileExistente != null) fileExistente.delete();

            // Cria o novo arquivo
            DocumentFile novoArquivo = root.createFile("application/octet-stream", "sabor_xit_v2.cfg");
            
            if (novoArquivo != null) {
                OutputStream out = getContentResolver().openOutputStream(novoArquivo.getUri());
                
                // Pega a seleção do seu XML
                RadioButton rbAlta = findViewById(R.id.rbAlta);
                String config;

                if (rbAlta != null && rbAlta.isChecked()) {
                    // CONFIGURAÇÃO ALTO (100% CAPA)
                    config = "Aim_Force: 1.0\nLock_On: Head\nSmooth: 0.01\nFOV: 360\nNo_Recoil: True";
                } else {
                    // CONFIGURAÇÃO NORMAL (LEGIT)
                    config = "Aim_Force: 0.50\nLock_On: Chest\nSmooth: 0.60\nFOV: 90\nNo_Recoil: False";
                }

                out.write(config.getBytes());
                out.close();

                Toast.makeText(this, "INJETADO COM SUCESSO!", Toast.LENGTH_SHORT).show();
                abrirJogo();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void abrirJogo() {
        Intent it = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
        if (it != null) {
            startActivity(it);
        } else {
            Toast.makeText(this, "Free Fire não encontrado!", Toast.LENGTH_SHORT).show();
        }
    }
}
