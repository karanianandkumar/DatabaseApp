package com.anandkumar.dictionaryapp;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    MySQLiteHelper db;
    ListView listView;
    ArrayList<String> listTitle;
    int pos;
    String searchString;
    List<DentoolDict> list;
    ArrayAdapter<String> myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db= new MySQLiteHelper(this);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listView);
        //db.onUpgrade(db.getWritableDatabase(), 1, 2);

        tts = new TextToSpeech(this, this);


        // get all books
        list = db.getAllBooks();
        Log.d("Database Length is:\t",list.size()+"");
        listTitle = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++) {
            listTitle.add(i, list.get(i).getWORD());
        }

        myAdapter = new ArrayAdapter<String>(this, R.layout.row_layout, R.id.listText, listTitle);
        //getListView().setOnItemClickListener(this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)     mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);

        int autoCompleteTextViewID = R.id.search_src_text;
        final AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) mSearchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setThreshold(1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, listTitle);
        searchAutoComplete.setAdapter(adapter);

        SearchManager searchManager =
                (SearchManager) getSystemService(this.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // TODO Auto-generated method stub
                 searchString = (String) parent.getItemAtPosition(position);

                DentoolDict row=db.getRow(searchString);

                int rowID=row.getID();
                String result="Row ID is: "+rowID+" and Word is "+row.getDEFINATION();
                Toast.makeText(MainActivity.this,result ,Toast.LENGTH_LONG).show();
                pos=list.indexOf(searchString);

                speakOut(searchString);
                searchAutoComplete.setText("" + searchString);
                searchAutoCompleteTextView.setSelection(searchString.length());

                Log.d("Position of Selected",""+pos);


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchAutoComplete.getWindowToken(), 0);





            }
        });


        return true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                speakOut(searchString);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String word) {

        String text = word;

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
