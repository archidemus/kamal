package com.byobdev.kamal;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by crono on 05-09-17.
 */

public class ListAdapter extends ArrayAdapter<String>{

    customButtonListener customListner;

    public interface customButtonListener {
        void getPosition1234(int position);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private String[] imageLista;
    private String[] descripcionLista;
    private ListView lista;

    public ListAdapter(Context context, ArrayList<String> dataItem, String[] keyLista, String[] sectorLista,String[] descripcionLista,String[] imageLista,ListView lista) {
        super(context, R.layout.activity_listview, dataItem);
        this.context = context;
        this.imageLista=imageLista;
        this.descripcionLista=descripcionLista;
        this.lista=lista;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.childTextView);
            viewHolder.imageView=(ImageView) convertView.findViewById(R.id.childImage);
            viewHolder.descripcion=(TextView) convertView.findViewById(R.id.descrip);
            viewHolder.ll=(RelativeLayout) convertView.findViewById(R.id.lllista);
            String image=imageLista[position];

            String url = "https://firebasestorage.googleapis.com/v0/b/prime-boulevard-168121.appspot.com/o/Images%2F"+image+"?alt=media";
            Picasso.with(this.getContext())
                    .load(url)
                    .error(R.drawable.kamal_logo)
                    .into(viewHolder.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);
        viewHolder.descripcion.setText(descripcionLista[position]);
        viewHolder.ll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(customListner!=null){
                    customListner.getPosition1234(position);}

            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView text;
        TextView descripcion;
        RelativeLayout ll;
    }
}
