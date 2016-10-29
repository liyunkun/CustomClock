package org.liyunkun.customclockmain;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.liyunkun.customclock.CustomClock;

public class MainActivity extends AppCompatActivity {

    private CustomClock clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
