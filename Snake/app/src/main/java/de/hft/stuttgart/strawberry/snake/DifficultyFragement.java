package de.hft.stuttgart.strawberry.snake;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by Juliano on 26.03.2015.
 *
 */


public class DifficultyFragement extends DialogFragment {

    // View zum Fragment
    private View myInflatedView;

    // Ausgeählte Geschwindigkeit als int
    private int geschwindigkeit;

    // Konstante zur Schwierigkeitsübergabe an das Spiel
    public static final String BUNDLE_DIFFICULTY = "difficulty";

    // Spielen Button zum Starten des Spiels
    private Button spielen_btn;

    // RadioButton Group zur Schwierigkeitsauswahl
    RadioGroup schwierigkeit;

    /*@Nullable*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Verbindet View mit .xml-file
        myInflatedView = inflater.inflate(R.layout.fragment_difficulty, container, false);

        // Dialogüberschrift
        getDialog().setTitle("Schwierigkeitsgrad");

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

    private void initWidgets() {
        // RadioButtonGroup
        schwierigkeit = (RadioGroup) myInflatedView.findViewById(R.id.schwierigkeitsgrad);

        // Spielen Button
        spielen_btn = (Button) myInflatedView.findViewById(R.id.spielen_btn);
    }

    private void initWidgetHandlers() {

        spielen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Holt die Id des ausgewählten RadioButtons
                int checkedId = schwierigkeit.getCheckedRadioButtonId();

                if (checkedId == R.id.einfach) {
                    geschwindigkeit = 1;
                    Toast.makeText(getActivity(), "Leicht", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.mittel) {
                    geschwindigkeit = 2;
                    Toast.makeText(getActivity(), "Mittel", Toast.LENGTH_SHORT).show();

                } else if (checkedId == R.id.schwer) {
                    geschwindigkeit = 3;
                    Toast.makeText(getActivity(), "Schwer", Toast.LENGTH_SHORT).show();
                }

                // Wenn keine Schwierigkeit gewählt wurde
                if (geschwindigkeit==0) {
                     Toast.makeText(getActivity(), "Schwierigkeitsgrad auswählen", Toast.LENGTH_SHORT).show();
                 } else {
                     Bundle bundle = new Bundle();
                     bundle.putInt(BUNDLE_DIFFICULTY, geschwindigkeit);
                     Intent intent = new Intent(getActivity(), GPSingleActivity.class);
                     intent.putExtras(bundle);
                     startActivity(intent);
                 }


            }
        });
    }
}

