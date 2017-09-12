package com.byobdev.kamal;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by crono on 05-09-17.
 */

public class ListAdapter extends ArrayAdapter<String>{

    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(int position,String[] keyLista, String[] sectorLista);
        public void onButtonClickListner2(int position,String[] keyLista, String[] sectorLista);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    private String[] keyLista;
    private String[] sectorLista;
    private String[] descriptionLista;
    private String[] imageLista;

    public ListAdapter(Context context, ArrayList<String> dataItem, String[] keyLista, String[] sectorLista,String[] descriptionLista,String[] imageLista) {
        super(context, R.layout.activity_listview, dataItem);
        this.data = dataItem;
        this.context = context;
        this.keyLista = keyLista;
        this.sectorLista = sectorLista;
        this.descriptionLista=descriptionLista;
        this.imageLista=imageLista;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.childImage);
            viewHolder.text = (TextView) convertView.findViewById(R.id.childTextView);
            viewHolder.button = (ImageButton) convertView.findViewById(R.id.childButton);
            viewHolder.button2 = (ImageButton) convertView.findViewById(R.id.childButton1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.imageView.setImageURI(Uri.parse(imageLista[position]));
        viewHolder.text.setText(temp);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position,keyLista, sectorLista);
                }

            }
        });
        viewHolder.button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner2(position,keyLista, sectorLista);
                }

            }
        });

        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView text;
        ImageButton button;
        ImageButton button2;
    }
}
