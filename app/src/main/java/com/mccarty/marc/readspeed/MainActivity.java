package com.mccarty.marc.readspeed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.StringTokenizer;

public class MainActivity extends Activity implements View.OnClickListener
{
    double elapsedTime = 0;
    double startTime = 0;

    class ParsePageTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            try
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                Document doc = Jsoup.connect(urls[0]).get();
                Element p = doc.select("p").first();
                String text = p.text();
                return text;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return "";
        }

        protected void onPostExecute(String result)
        {
            // process results
            ((TextView) findViewById(R.id.paragraph)).setText(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        Button beginButton = findViewById(R.id.begin_button);
        Button finishedButton = findViewById(R.id.finished_button);

        new ParsePageTask().execute("https://en.wikipedia.org/wiki/Main_Page");

        TextView paragraph = findViewById(R.id.paragraph);
        paragraph.setVisibility(View.GONE);

        //paragraph.setText("This will eventually be text grabbed from a Wikipedia article.\nFor now you should enjoy the fact that this app works at all.");

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
                elapsedTime /= 60000;

                //StringTokenizer counts the words
                CharSequence test = paragraph.getText();
                double wordCount = new StringTokenizer((String)test).countTokens();

                paragraph.setVisibility(View.GONE);

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;
                CharSequence text = "Your reading speed is: " + (int)(wordCount / elapsedTime) + " WPM";

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                finishedButton.setEnabled(false);
                beginButton.setEnabled(true);
                finishedButton.setBackgroundColor(Color.GRAY);
            }
        }
    }
}
