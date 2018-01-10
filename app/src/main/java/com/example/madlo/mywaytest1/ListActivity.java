package com.example.madlo.mywaytest1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListActivity extends AppCompatActivity {
    WayListViewAdapter wayListViewAdapter;
//TODO: neustes ganz oben anzeigen https://stackoverflow.com/questions/9987402/is-it-possible-to-make-a-listview-populate-from-the-bottom versucht aber keine ahnung wegen chaos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Folgendes muss immer weit oben in der Methode stehen damit die Methode
        // quasi den Aufbau weiss
        setContentView(R.layout.activity_list);

        //Funktionsaufruf aus DBHelper heraus
        DbHelper.getInstance(ListActivity.this).loadWays();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //This steht in der runden Klammer fuer den Context der Main
        wayListViewAdapter = new WayListViewAdapter(this, WayData.getInstance().getWaysList());

        //ERZEUGT DIE LISTENANSICHT

        ListView listView = (ListView) findViewById(R.id.listViewWays);
        listView.setAdapter(wayListViewAdapter);


        listView.setOnItemClickListener(new ListView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Hier wird der Key POSITION fuer den zunaechst kreierten intent eingefuegt
                Intent intent = new Intent(ListActivity.this, DetailedView.class);

                intent.putExtra("POSITION", position);
                startActivity(intent);


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        wayListViewAdapter.notifyDataSetChanged();


    }

}
