package it.uniba.dib.sms2324_16;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GameThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private boolean running;
    // Immagini o oggetti per rappresentare i livelli del percorso
    private Bitmap sfondo, draggedImage;
    private Paint paint;
    private List<Point> zigzagPath;
    private Path zigzagPath1;
    private int zigzagHeight = 200;
    private int canvasWidth;
    private int canvasHeight;
    private Context context;
    private float draggedX, draggedY;
    private Bitmap personaggio1;
    private boolean movimento;
    private int eserciziAssegnati;

    public GameThread(SurfaceHolder holder, Context context) {
        surfaceHolder = holder;
        this.context = context;

        // Carica le immagini o inizializza gli oggetti dei livelli
        sfondo = BitmapFactory.decodeResource(context.getResources(), R.drawable.sfondo_input);

        // viene settato lo sfondo in base a ciò che è stato scelto dal genitore per il bambino (controllato con una query al db)
        setSfondo();

        // Inizializza l'elemento paint ed il vettore che conterrà il percorso
        paint = new Paint();
        zigzagPath = new ArrayList<>();
        setRunning(true);
    }

    private void setSfondo() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("SceltaScenario");

            // Esegui una query filtrata per ottenere i documenti associati all'utente attualmente loggato
            collectionReference.whereEqualTo("id_bambino", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String scenario = document.getString("SceltaScenario");

                                    if (scenario.equals("Savana"))
                                        sfondo = BitmapFactory.decodeResource(context.getResources(), R.drawable.scenario_savana);

                                    if (scenario.equals("Mondo incantato"))
                                        sfondo = BitmapFactory.decodeResource(context.getResources(), R.drawable.scenario_mondo_incantato);

                                    if (scenario.equals("Pirata"))
                                        sfondo = BitmapFactory.decodeResource(context.getResources(), R.drawable.scenario_piratesco);

                                    if (scenario.equals("Fiabesco"))
                                        sfondo = BitmapFactory.decodeResource(context.getResources(), R.drawable.scenario_fiabesco);
                                }
                            } else {
                                Log.e(TAG, "Error getting documents: ", task.getException());
                                PercorsoFragment percorsoFragment = new PercorsoFragment();
                                percorsoFragment.messaggio("Il tuo genitore non ha ancora scelto lo scenario per te!");
                                setRunning(false);
                            }
                        }
                    });
        }


    }

    public void setCanvasSize(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }

    private void getEserciziAssegnati() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("EserciziAssegnati");

            collectionReference.whereEqualTo("id_bambino", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            eserciziAssegnati = task.getResult().size();
                        }
                    });
        }
    }

    private void createZigzagPath() {
        // Usa le variabili canvasWidth e canvasHeight per calcolare il percorso zigzag
        int zigzagWidth = 500;  // Larghezza dello zigzag, adatta se necessario
        int startX = (canvasWidth - zigzagWidth) / 2;
        //int startY = (canvasHeight - zigzagHeight) / 2;
        int startY = 500;

        int endX = canvasWidth - startX;
        int endY = startY;

        //il 10 della condizione dipende dal numero di esercizi assegnati al bambino
        getEserciziAssegnati();

        for (int i=0; startY <= canvasHeight && i < eserciziAssegnati; i++) {
            zigzagPath.add(new Point(startX, startY));

            // Calcola la prossima posizione dello zigzag
            startY += zigzagHeight;

            // Inverti la direzione dello zigzag
            int tempX = startX;
            startX = endX;
            endX = tempX;
        }
    }


    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    // Update game logic
                    update();
                    if (zigzagPath.isEmpty() && canvasWidth > 0 && canvasHeight > 0) {
                        createZigzagPath();
                    }
                    // Render the game
                    render(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void update() {
        // Update your game logic here
    }

    private void render(Canvas canvas) {
        if(canvas == null){
            return;
        }
        if (sfondo != null)
        {
            // Ridimensiona l'immagine di sfondo alle dimensioni della SurfaceView
            Bitmap scaledBackground = Bitmap.createScaledBitmap(sfondo, canvasWidth, canvasHeight, true);
            // Disegna il background o altri elementi del gioco qui
            canvas.drawBitmap(scaledBackground, 0, 0, null);
        }

        paint.setColor(ContextCompat.getColor(context, R.color.celeste));

        // Imposta la larghezza della linea
        paint.setStrokeWidth(30);  // Imposta la larghezza desiderata

        // Disegna la linea zigzag
        for (int i = 0; i < zigzagPath.size() - 1; i++) {
            Point currentPoint = zigzagPath.get(i);
            Point nextPoint = zigzagPath.get(i + 1);

            // Disegna la linea tra i punti
            canvas.drawLine(currentPoint.x, currentPoint.y, nextPoint.x, nextPoint.y, paint);
        }

        // Disegna gli ovali sopra la linea zigzag
        paint.setColor(Color.WHITE); // Colore dello sfondo
        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < zigzagPath.size() - 1; i++) {
            Point currentPoint = zigzagPath.get(i);
            Point nextPoint = zigzagPath.get(i + 1);

            // Se il percorso cambia direzione, disegna l'ovale sopra la linea zigzag
            if (currentPoint.y != nextPoint.y) {
                float ovalLeft = nextPoint.x - 60; // Imposta la posizione orizzontale sinistra dell'ovale
                float ovalTop = nextPoint.y - 50; // Imposta la posizione verticale superiore dell'ovale
                float ovalRight = nextPoint.x + 60; // Imposta la posizione orizzontale destra dell'ovale
                float ovalBottom = nextPoint.y + 50; // Imposta la posizione verticale inferiore dell'ovale

                // Disegna uno sfondo ad ovale
                paint.setShadowLayer(10, 0, 0, Color.BLACK);
                canvas.drawOval(ovalLeft, ovalTop, ovalRight, ovalBottom, paint);
                paint.setShadowLayer(0, 0, 0, Color.BLACK);
            }
        }

        // Disegna i numeri al centro degli ovali
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);

        for (int i = 0; i < zigzagPath.size() - 1; i++) {
            Point currentPoint = zigzagPath.get(i);
            Point nextPoint = zigzagPath.get(i + 1);

            // Se il percorso cambia direzione, disegna il numero al centro dell'ovale
            if (currentPoint.y != nextPoint.y) {
                String number = Integer.toString((i + 2)); // Calcola il numero da visualizzare
                float textX = nextPoint.x - 10;
                float textY = nextPoint.y + 10; // Altezza del testo sopra l'ovale
                canvas.drawText(number, textX, textY, paint);
            }
        }

        // Disegna l'ovale e il numero all'inizio del percorso
        if (!zigzagPath.isEmpty()) {
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            Point startPoint = zigzagPath.get(0);
            float startOvalLeft = startPoint.x - 60;
            float startOvalTop = startPoint.y - 50;
            float startOvalRight = startPoint.x + 60;
            float startOvalBottom = startPoint.y + 50;

            // Disegna uno sfondo ad ovale all'inizio del percorso
            paint.setShadowLayer(10, 0, 0, Color.BLACK);
            canvas.drawOval(startOvalLeft, startOvalTop, startOvalRight, startOvalBottom, paint);
            paint.setShadowLayer(0, 0, 0, Color.BLACK);

            // Disegna il numero al centro dell'ovale
            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            String startNumber = "1"; // Numero iniziale
            float startTextX = startPoint.x - 10;
            float startTextY = startPoint.y + 10; // Altezza del testo sopra l'ovale
            canvas.drawText(startNumber, startTextX, startTextY, paint);
        }

        // Disegna l'immagine trascinata, se presente...
        if (draggedImage != null) {
            canvas.drawBitmap(draggedImage, draggedX, draggedY, null);
        }
    }

    public List<Point> getZigzagPath() {
        return zigzagPath;
    }

    public void setDraggedImage(ImageView imageView, float x, float y) {
        draggedImage = getBitmapFromImageView(imageView);
        draggedX = x;
        draggedY = y;
    }

    private Bitmap getBitmapFromImageView(ImageView imageView) {
        // Converte l'ImageView in un'immagine Bitmap
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            return drawable.getBitmap();
        } else {
            return null;
        }
    }
}

