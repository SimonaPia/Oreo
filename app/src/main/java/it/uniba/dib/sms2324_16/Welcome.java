package it.uniba.dib.sms2324_16;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    private Button buttonLogopedista;
    private Button buttonGenitore;
    private Button buttonBambino;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        buttonLogopedista = findViewById(R.id.buttonLogopedista);

        buttonLogopedista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, HomePage.class);
                intent.putExtra("tipoUtente", "logopedista");
                startActivity(intent);
            }
        });

        buttonGenitore = findViewById(R.id.buttonGenitore);

        buttonGenitore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, HomePage.class);
                intent.putExtra("tipoUtente", "genitore");
                startActivity(intent);
            }
        });

        buttonBambino = findViewById(R.id.buttonBambino);

        buttonBambino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, HomePage.class);
                intent.putExtra("tipoUtente", "bambino");
                startActivity(intent);
            }
        });
    }
}
