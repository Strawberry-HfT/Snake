package de.hft.stuttgart.strawberry.snake;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;

/**
 * Von hier aus wird das Game Play des
 * Singleplayermodus ausgefuehrt.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(new DrawGPView(this));
    }


}
