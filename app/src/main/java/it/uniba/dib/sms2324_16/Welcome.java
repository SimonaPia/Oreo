package it.uniba.dib.sms2324_16;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Welcome extends AppCompatActivity {
    private Button buttonLogopedista;
    private Button buttonGenitore;
    private Button buttonBambino;
    private Button scegliLingua;
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
        scegliLingua=findViewById(R.id.buttonChooseLanguage);
        scegliLingua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });

    }
    private void showChangeLanguageDialog(){
        final String[] listItems = {"Italian", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Welcome.this);
        mBuilder.setTitle("Scegli la lingua di preferenza: ");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocale("it");
                    recreate();
                }
                if (i == 1) {
                    setLocale("en");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
AlertDialog mDialog = mBuilder.create();
mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
    }
}
