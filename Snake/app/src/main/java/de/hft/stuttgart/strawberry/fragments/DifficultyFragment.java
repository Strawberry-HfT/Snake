package de.hft.stuttgart.strawberry.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import de.hft.stuttgart.strawberry.activities.GPMultiActivity;
import de.hft.stuttgart.strawberry.activities.MainActivity;
import de.hft.stuttgart.strawberry.snake.R;

/**
 * Created by Juliano on 26.03.2015.
 *
 */
public class DifficultyFragment extends DialogFragment {

    // TAG für den Logger
    private static final String TAG = DifficultyFragment.class.getSimpleName();

    // Schwierigkeitsgrade
    private static final int NONE = 0;
    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;

    // View zum Fragment
    private View myInflatedView;

    // Ausgewählte Schwierigkeit als int
    private int selectedDifficulty;

    // Spielen Button zum Starten des Spiels
    private Button btnAccept;

    // RadioButton Group zur Schwierigkeitsauswahl
    private RadioGroup schwierigkeit;

    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Verbindet View mit .xml-file
        myInflatedView = inflater.inflate(R.layout.fragment_difficulty, container, false);

        // Dialogüberschrift
        getDialog().setTitle("Schwierigkeitsgrad");//TODO aus Strings holen

        // Initialisierung der Widgets
        initWidgets();

        // Initialisierung der Handler
        initWidgetHandlers();

        return  myInflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        Activity activity = getActivity();
        // hier wird die gewaehlte Geschiwndigkeit uebergeben
        if (activity instanceof MainActivity) {

            MainActivity mainActivity = (MainActivity) activity;

            mainActivity.setSelectedDifficulty(selectedDifficulty);
            mainActivity.setLevelSelected(true);
            mainActivity.onDismiss(dialog);
        } else if (activity instanceof GPMultiActivity) {

            GPMultiActivity multiActivity = (GPMultiActivity) activity;

            multiActivity.setSelectedDifficulty(selectedDifficulty);
            multiActivity.setLevelSelected(true);
            multiActivity.onDismiss(dialog);
        }
    }

    private void initWidgets() {
        // RadioButtonGroup
        schwierigkeit = (RadioGroup) myInflatedView.findViewById(R.id.schwierigkeitsgrad);

        // Spielen Button
        btnAccept = (Button) myInflatedView.findViewById(R.id.spielen_btn);
    }

    private void initWidgetHandlers() {

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Holt die Id des ausgewählten RadioButtons
                int checkedId = schwierigkeit.getCheckedRadioButtonId();

                //TODO besser als Switch-Case schreiben
                if (checkedId == R.id.einfach) {
                    selectedDifficulty = EASY;
//                    Toast.makeText(getActivity(), "Leicht", Toast.LENGTH_SHORT).show();//TODO aus Strings holen
                } else if (checkedId == R.id.mittel) {
                    selectedDifficulty = MEDIUM;
//                    Toast.makeText(getActivity(), "Mittel", Toast.LENGTH_SHORT).show();//TODO aus Strings holen

                } else if (checkedId == R.id.schwer) {
                    selectedDifficulty = HARD;
//                    Toast.makeText(getActivity(), "Schwer", Toast.LENGTH_SHORT).show();//TODO aus Strings holen
                }

                // Wenn keine Schwierigkeit gewählt wurde
                if (selectedDifficulty == NONE) {
                    Toast.makeText(getActivity(), "Schwierigkeitsgrad auswählen", Toast.LENGTH_SHORT).show();//TODO aus Strings holen
                } else {
                    dismiss();
                }
            }
        });
    }
}

