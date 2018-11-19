package com.mccarty.marc.readspeed;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoresActivity extends Activity implements View.OnClickListener
{
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    void scoresLoad(String[] text)
    {
        MainActivity.getScores().addAll(Arrays.asList(text));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if (useDarkTheme)
        {
            setTheme(R.style.AppTheme_Dark);
        }

        MainActivity.getScores().clear();
        Gson gson = new Gson();
        String jsonText = preferences.getString("key", null);
        String[] text = gson.fromJson(jsonText, String[].class);
        scoresLoad(text);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        Toolbar toolbar = findViewById(R.id.scores_toolbar);
        setActionBar(toolbar);

        TextView scoresTextView = findViewById(R.id.scores_list);
        CharSequence scoresAsCharSeq = charSeqScores(MainActivity.getScores());
        scoresTextView.setText(scoresAsCharSeq);
    }

    CharSequence charSeqScores(ArrayList<String> scores)
    {
        CharSequence scoresAsCharSeq = "SCORES:\n";

        for (int i = 0; i < scores.size(); i++)
        {
            scoresAsCharSeq = scoresAsCharSeq + scores.get(i) + "\n";
        }

        return scoresAsCharSeq;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v)
    {
        //TODO: Add Score clicking logic
        if (v != null)
        {
            int id = v.getId();

            if (id == R.id.clear_scores_button)
            {
                MainActivity.clearScores();

                //Blanks out the saved text by adding the now empty array.
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor prefEdit = preferences.edit();
                Gson gson = new Gson();
                List<String> textList = MainActivity.getScores();
                String jsonText = gson.toJson(textList);
                prefEdit.putString("key", jsonText);
                prefEdit.apply();

                //Reload screen to show cleared scores.
                finish();
                startActivity(getIntent());
            }
        }
    }
}
