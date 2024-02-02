package it.uniba.dib.sms2324_16;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Welcome extends AppCompatActivity {
    private Button buttonLogopedista;
    private Button buttonGenitore;
    private Button buttonBambino;
    private Button scegliLingua;
    private Button buttonGuest;  // Aggiunto pulsante per l'utente ospite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        buttonLogopedista = findViewById(R.id.buttonLogopedista);
        buttonGenitore = findViewById(R.id.buttonGenitore);
        buttonBambino = findViewById(R.id.buttonBambino);
        scegliLingua = findViewById(R.id.buttonChooseLanguage);
        buttonGuest = findViewById(R.id.buttonGuestNavigate);  // Inizializzato il pulsante per l'utente ospite

        buttonLogopedista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHomePage("logopedista");
            }
        });

        buttonGenitore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHomePage("genitore");
            }
        });

        buttonBambino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHomePage("bambino");
            }
        });

        // Modificato l'azione per il pulsante Guest
        buttonGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserTypeSelectionDialog();
            }
        });

        scegliLingua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });
    }

    private void showUserTypeSelectionDialog() {
        final String[] userTypes = {getString(R.string.logopedist), getString(R.string.parent), getString(R.string.child)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_user_type));

        builder.setItems(userTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedUserType = userTypes[which];

                // Avvia la HomePage corrispondente al tipo di utente selezionato
                Intent intent;
                switch (selectedUserType) {
                    case "Logopedista":
                        intent = new Intent(Welcome.this, HomePageLogopedista.class);
                        intent.putExtra("user_type", getString(R.string.logopedist));
                        break;
                    case "Genitore":
                        intent = new Intent(Welcome.this, HomePageGenitore.class);
                        intent.putExtra("user_type", getString(R.string.parent));
                        break;
                    case "Bambino":
                        intent = new Intent(Welcome.this, HomePageBambino.class);
                        intent.putExtra("user_type", getString(R.string.child));
                        break;
                    default:
                        intent = new Intent(Welcome.this, Welcome.class);  // Fall back su Welcome
                        break;
                }

                intent.putExtra("tipoUtente", "ospite");
                startActivity(intent);
            }
        });

        builder.show();
    }


    private void navigateToHomePage(String userType) {
        Intent intent = new Intent(Welcome.this, HomePage.class);
        intent.putExtra("tipoUtente", userType);
        startActivity(intent);
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Italian", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Welcome.this);
        mBuilder.setTitle("Scegli la lingua di preferenza: ");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocale("it");
                    recreate();
                } else if (i == 1) {
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
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }
}
