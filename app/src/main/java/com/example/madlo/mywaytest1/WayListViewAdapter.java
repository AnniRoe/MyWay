package com.example.madlo.mywaytest1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by madlo on 22.11.2017.
 */

public class WayListViewAdapter extends BaseAdapter {

    int length;
    private LayoutInflater layoutInflater;
    private List<Ways> waysList2;

    @Override
    public int getCount() {
        //Soll die Laenge der Wegliste zurueckgeben
        length = waysList2.size();
        return length;
    }

    @Override
    public Object getItem(int position) {
        //Soll das Element an der Position position der Liste zurueckgeben
        return waysList2.get(position);
    }

    @Override
    public long getItemId(int position) {
        //SOll einfach nur position zurueckgeben
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (convertView == null) {

            vi = layoutInflater.inflate(R.layout.way_list_item, null);
            TextView textView = (TextView) vi.findViewById(R.id.textView_date_transport);
            textView.setText(waysList2.get(position).getDate() + "\n" + waysList2.get(position).getTransport());

        }
        return vi;
    }


    // Konstruktor bekommt später beim erstellen der Klasse die Wegliste vom Typ
    //ArrayList<Ways> übergeben und dann dem entsprechenden Member in der WayListAdapter
    //Klasse zugewiesen Somit steht dem Adapter dann die Wegliste zur verfuegung
    //Konstruktor hat immer gleichen Namen wie die Klasse
    public WayListViewAdapter(Context context, List<Ways> waysList) {

        waysList2 = waysList;
        layoutInflater = LayoutInflater.from(context);


    }

}

