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
    View myInflatedView;
    Bundle bundle;


    public static final String BUNDLE_DIFFICULTY = "difficulty";

    //Layout
    Button spielen_btn;
    RadioGroup schwierigkeit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
              myInflatedView = inflater.inflate(R.layout.fragment_difficulty, container, false);

        getDialog().setTitle("Schwierigkeitsgrad");
        initWidgets();
        initWidgetHandlers();

        return  myInflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initWidgets() {
       schwierigkeit = (RadioGroup) myInflatedView.findViewById(R.id.schwierigkeitsgrad);
       spielen_btn = (Button) myInflatedView.findViewById(R.id.spielen_btn);
    }

    private void initWidgetHandlers() {
        schwierigkeit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               /* if (checkedId == R.id.einfach) {
                    bundle.putInt(BUNDLE_DIFFICULTY, 1);

                } else if (checkedId == R.id.mittel) {
                    bundle.putInt(BUNDLE_DIFFICULTY, 2);

                } else if (checkedId == R.id.schwer) {
                    bundle.putInt(BUNDLE_DIFFICULTY, 3);
                }*/
            }
        });

        spielen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedId = schwierigkeit.getCheckedRadioButtonId();
                if (checkedId == R.id.einfach) {
                    //bundle.putInt(BUNDLE_DIFFICULTY, 1);
                    Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.mittel) {
                    Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();

                } else if (checkedId == R.id.schwer) {
                    Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Schwierigkeitsgrad auswählen", Toast.LENGTH_SHORT).show();
                }
               /* Intent intent = new Intent(getActivity(), GPSingleActivity.class);
                startActivity(intent);*/

            }
        });
    }
}

