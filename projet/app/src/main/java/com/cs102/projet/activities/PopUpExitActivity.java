package com.cs102.projet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.cs102.projet.R;

public class PopUpExitActivity extends Activity {
    Button YesExitPop, NoExitPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_exit);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // setting pop up height and width
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.5),(int)(height*.2));

        // setting position of pop up window

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        getWindow().setAttributes(params);

        // setting buttons

        YesExitPop = findViewById(R.id.YesExitPop);
        NoExitPop = findViewById(R.id.NoExitPop);

        YesExitPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TO-DO
            }
        });

        NoExitPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}