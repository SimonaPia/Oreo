package it.uniba.dib.sms2324_16;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PercorsoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PercorsoFragment extends Fragment implements View.OnTouchListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GameThread gameThread;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Bitmap layer1Image;
    private Bitmap layer2Image;
    public PercorsoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageBambinoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PercorsoFragment newInstance(String param1, String param2) {
        PercorsoFragment fragment = new PercorsoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_percorso, container, false);

        surfaceView = view.findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceView.setOnTouchListener(this);

        gameThread = new GameThread(surfaceHolder, requireContext());
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (gameThread.getState() == Thread.State.NEW) {
                    gameThread.setCanvasSize(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
                    gameThread.start();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Handle surface changes if needed
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                gameThread.setRunning(false);
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        // Verifica se il tocco è avvenuto all'interno di uno degli ovali
        for (int i = 0; i < gameThread.getZigzagPath().size(); i++) {
            Point ovalCenter = gameThread.getZigzagPath().get(i);
            float ovalLeft = ovalCenter.x - 30;
            float ovalTop = ovalCenter.y - 50;
            float ovalRight = ovalCenter.x + 30;
            float ovalBottom = ovalCenter.y + 10;

            if (touchX >= ovalLeft && touchX <= ovalRight && touchY >= ovalTop && touchY <= ovalBottom) {
                // Il tocco è avvenuto all'interno dell'ovale, esegui l'azione desiderata
                // Ad esempio, visualizza un messaggio o avvia un'attività
                Toast.makeText(requireContext(), "Hai toccato l'ovale " + (i + 1), Toast.LENGTH_SHORT).show();
                return true; // Consuma l'evento di tocco
            }
        }

        // Il tocco non è avvenuto all'interno di nessun ovale
        return false;
    }
}