package com.feeder.audacity.feeder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.FitWindowsViewGroup;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FeedbackForm extends AppCompatActivity {
    View lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);
        lay = findViewById(R.id.activity_feedback_form);
        TextView titleView = new TextView(this);
        titleView.setText("Hallo Welt!");
        ((LinearLayout) lay).addView(titleView);

    }
}
