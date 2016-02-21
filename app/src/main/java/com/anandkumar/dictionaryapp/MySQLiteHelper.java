package com.anandkumar.dictionaryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anand on 2/19/2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 2;
    // database name
    private static final String database_NAME = "DentooDB";
    private static final String table_WORDS = "words";
    private static final String word_ID = "id";
    private static final String word_NAME = "name";
    private static final String word_DEFINATION = "defination";

    private static final String[] COLUMNS = { word_ID, word_NAME, word_DEFINATION };
    private static final String TAG ="Dictionary Database" ;
    private final Context context;
    boolean flag=false;


    public MySQLiteHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORDS_TABLE="CREATE TABLE IF NOT EXISTS words ( "+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, " + "defination TEXT )";

        db.execSQL(CREATE_WORDS_TABLE);
        loadDictionary();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS books");
        this.onCreate(db);
    }

    public long addWord(String word,String defination){
        SQLiteDatabase db=this.getWritableDatabase();
        //SQLiteDatabase mDataBase=this.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put(word_NAME, word);
        values.put(word_DEFINATION, defination);

     //   Cursor c = db.rawQuery("SELECT * FROM " + table_WORDS + " WHERE name= '"+word+"'",null);
      /*  while(c.moveToNext())
        {
            if(c.getString(1).equals(word))
            {
                flag=true;
            }
        }
        if(flag==false)
        {
            return db.insert(table_WORDS, null, values);
        }

        return -1;
        */
        return db.insert(table_WORDS, null, values);

    }


    private void loadDictionary() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadWords();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void loadWords() throws IOException {
        final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.definitions);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, "-");
                if (strings.length < 2) continue;
                long id = addWord(strings[0].trim(), strings[1].trim());
                if (id < 0) {
                    Log.e(TAG, "unable to add word: " + strings[0].trim());
                }
            }
        } finally {
            reader.close();
        }
    }

    public List<DentoolDict> getAllBooks() {
        List<DentoolDict> books = new LinkedList<DentoolDict>();

        // select book query
        String query = "SELECT  * FROM " + table_WORDS;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        DentoolDict book = null;
        if (cursor.moveToFirst()) {
            do {
                book = new DentoolDict();
                book.setID(Integer.parseInt(cursor.getString(0)));
                book.setWORD(cursor.getString(1));
                book.setDEFINATION(cursor.getString(2));

                // Add book to books
                books.add(book);
            } while (cursor.moveToNext());
        }
        return books;
    }

    public DentoolDict getRow(String word) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + table_WORDS + " WHERE "
                + word_NAME+ " = '" + word+"'";

        Log.e("Query", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        DentoolDict td = new DentoolDict();
        td.setID(c.getInt(c.getColumnIndex(word_ID)));
        td.setWORD((c.getString(c.getColumnIndex(word_NAME))));
        td.setDEFINATION(c.getString(c.getColumnIndex(word_DEFINATION)));

        return td;
    }

}
