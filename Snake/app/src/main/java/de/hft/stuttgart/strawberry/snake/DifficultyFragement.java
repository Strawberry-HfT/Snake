package de.hft.stuttgart.strawberry.snake;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by Juliano on 26.03.2015.
 *
 */


public class DifficultyFragement extends DialogFragment {
    View myInflatedView;
    RadioButton einfach,mittel,schwer;
    Button spielen_btn;
    RadioGroup schwierigkeit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
              myInflatedView = inflater.inflate(R.layout.fragment_difficulty, container, false);

        getDialog().setTitle("Schwierigkeitsgrad");
        initWidgets();

        return  myInflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initWidgets() {
       schwierigkeit = (RadioGroup) myInflatedView.findViewById(R.id.schwierigkeitsgrad);

    }
}

