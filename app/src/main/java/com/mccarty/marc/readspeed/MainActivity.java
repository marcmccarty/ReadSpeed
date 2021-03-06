package com.mccarty.marc.readspeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends Activity implements View.OnClickListener
{
    // Save Theme Selection
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    // Timer for WPM
    double elapsedTime = 0;
    double startTime = 0;
    // Scores - May rewrite where this is stored in ScoresActivity and Main can reference
    static ArrayList<String> scores = new ArrayList<>();
    // Saved preferences
    SharedPreferences preferences;

    class ParsePageTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            try
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //Grabs the first paragraph on the page
                Document doc = Jsoup.connect(urls[0]).get();
                Element p = doc.select("p").first();
                return p.text();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return "";
        }

        protected void onPostExecute(String result)
        {
            ((TextView) findViewById(R.id.paragraph)).setText(result);
        }
    }

    static ArrayList<String> getScores()
    {
        return scores;
    }

    static void clearScores()
    {
        scores.clear();
    }

    int averageScore()
    {
        int totalScore = 0;

        for (int i = 0; i < scores.size(); i++)
        {
            totalScore += Integer.parseInt(scores.get(i));
        }

        return (totalScore / scores.size());
    }

    void scoresLoad(String[] text)
    {
        MainActivity.getScores().addAll(Arrays.asList(text));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme)
        {
            setTheme(R.style.AppTheme_Dark);
        }

        // Clear score array, in case returning from another activity
        scores.clear();
        // Do load from preferences, though, and store them back in the array, for data manipulation
        Gson gson = new Gson();
        String jsonText = preferences.getString("key", null);
        String[] text = gson.fromJson(jsonText, String[].class);
        if (text != null)
        {
            scoresLoad(text);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        Button beginButton = findViewById(R.id.begin_button);
        Button finishedButton = findViewById(R.id.finished_button);

        new ParsePageTask().execute("https://en.wikipedia.org/wiki/Main_Page");

        TextView paragraph = findViewById(R.id.paragraph);
        // Hide paragraph before "Begin" is pressed, for accurate measurement
        paragraph.setVisibility(View.GONE);

        // Preparing enabled/disabled Begin/Finished buttons, color-wise
        beginButton.setBackgroundColor(Color.rgb(255,128,0));
        finishedButton.setBackgroundColor(Color.GRAY);

        beginButton.setOnClickListener(MainActivity.this);
        finishedButton.setOnClickListener(MainActivity.this);
        finishedButton.setEnabled(false);


        // Most Recent Score and Average Score Notice
        // TODO: Change to a function
        // TODO: Add switch in settings to turn this option off
        // Potentially make appear as TextView before hitting "Begin" instead of Toast
        if (scores.size() > 0)
        {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            CharSequence text2 = "Your most recent score was: " + scores.get(scores.size()-1) + " WPM" + "\nYour average score is: " + averageScore() + " WPM";
            Toast toast = Toast.makeText(context, text2, duration);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.scores:
                Intent scoresIntent = new Intent(this, ScoresActivity.class);
                this.startActivity(scoresIntent);
                break;

            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                this.startActivity(settingsIntent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
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

                //Show paragraph
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
                //Hide paragraph
                //Present WPM to user
                elapsedTime = System.currentTimeMillis() - startTime;
                elapsedTime /= 60000;

                //StringTokenizer counts the words
                CharSequence test = paragraph.getText();
                double wordCount = new StringTokenizer((String)test).countTokens();

                paragraph.setVisibility(View.GONE);

                int wpmScore = (int)(wordCount / elapsedTime);

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;
                CharSequence text = "Your reading speed is: " + wpmScore + " WPM";
                scores.add("" + wpmScore);

                // May make into function, if possible.
                // Stores the score for future app uses.
                SharedPreferences.Editor prefEdit = preferences.edit();
                Gson gson = new Gson();
                List<String> textList = scores;
                String jsonText = gson.toJson(textList);
                prefEdit.putString("key", jsonText);
                prefEdit.apply();

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                finishedButton.setEnabled(false);
                beginButton.setEnabled(true);
                finishedButton.setBackgroundColor(Color.GRAY);
            }
        }
    }
}
