package de.hft.stuttgart.strawberry.snake;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Von hier aus wird das Game Play des
 * Singleplayermodus ausgefuehrt.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleActivity extends Activity {
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        int geschw = bundle.getInt(DifficultyFragement.BUNDLE_DIFFICULTY);

        this.setContentView(new DrawGPView(this));




    }

}
