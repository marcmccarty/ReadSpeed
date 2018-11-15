package com.mccarty.marc.readspeed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Timer;

public class MainActivity extends Activity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        Button beginButton = findViewById(R.id.begin_button);
        Button finishedButton = findViewById(R.id.finished_button);

        TextView paragraph = findViewById(R.id.paragraph);
        paragraph.setVisibility(View.GONE);

        beginButton.setBackgroundColor(Color.rgb(255,128,0));
        finishedButton.setBackgroundColor(Color.GRAY);

        beginButton.setOnClickListener(MainActivity.this);
        finishedButton.setOnClickListener(MainActivity.this);
        finishedButton.setEnabled(false);
    }

    @Override
    public void onClick(View v)
    {
        Button beginButton = findViewById(R.id.begin_button);
        Button finishedButton = findViewById(R.id.finished_button);
        TextView paragraph = findViewById(R.id.paragraph);

        long elapsedTime = 0;
        long startTime = 0;

        if (v != null)
        {
            int id = v.getId();

            if(id == R.id.begin_button)
            {
                finishedButton.setBackgroundColor(Color.rgb(255,128,0));
                //Show paragraph(s)
                //Start timer

                paragraph.setVisibility(View.VISIBLE);

                elapsedTime = 0;
                startTime = System.currentTimeMillis();

                finishedButton.setEnabled(true);
                beginButton.setEnabled(false);
                beginButton.setBackgroundColor(Color.GRAY);
            }
            else if(id == R.id.finished_button) //&&beginFlag
            {
                beginButton.setBackgroundColor(Color.rgb(255,128,0));
                //Stop timer
                //Hide paragraph -- maybe
                //Present WPM to user

                elapsedTime = System.currentTimeMillis() - startTime;
                int wordCount = 12;

                paragraph.setVisibility(View.GONE);

                Context context = getApplicationContext();
                CharSequence text = "WPM: " + (wordCount / ((elapsedTime / 1000)  / 60));
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                finishedButton.setEnabled(false);
                beginButton.setEnabled(true);
                finishedButton.setBackgroundColor(Color.GRAY);
            }
        }
    }
}
