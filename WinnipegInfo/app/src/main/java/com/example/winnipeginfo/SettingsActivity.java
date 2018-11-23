package com.example.winnipeginfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
   SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences=getSharedPreferences("general_prefs", MODE_PRIVATE);
    }


    public static class PrefsFragment extends PreferenceFragment {
        private ListPreference mListPreference;
        private   SharedPreferences  sharedPreferences;
        private  Toolbar toolbar;
        //Docs say that fragments should have an empty constructor
        public PrefsFragment() { }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //This handy feature means you don't have to declare SharedPreferences

             sharedPreferences= getActivity().getSharedPreferences("general_prefs", MODE_PRIVATE);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.first_fragment);
            toolbar=(Toolbar)getActivity().findViewById(R.id.toolbar);
            updateSubjectColor();

            mListPreference = (ListPreference)  getPreferenceManager().findPreference("list_preference");
            mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // your code here
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                   editor.putString("subjectColor",newValue.toString());
                   editor.commit();
//                    Toolbar toolbar =getActivity().findViewById(R.id.toolbar);
//                    toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                    return true;
                }
            });
        }
        @Override
        public void onResume()
        {
            super.onResume();
            updateSubjectColor();

        }

        private void updateSubjectColor()
        {

            String value =sharedPreferences.getString("subjectColor","");

            if (value=="Green")
            {
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
            }
            else if (value=="Blue")
            {
                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            else if (value=="Black")
            {
                toolbar.setBackgroundColor(getResources().getColor(R.color.black));
            }
        }


    }

}
