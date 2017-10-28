package com.byobdev.kamal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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
    private String[] direccionLista;
    private String[] fechaILista;
    private String[] fechaTLista;
    private ListView lista;

    public ListAdapter(Context context, ArrayList<String> dataItem, String[] keyLista, String[] sectorLista,String[] descripcionLista,String[] imageLista,ListView lista, String[] fechaILista, String[] fechaTLista, String[] direccionLista) {
        super(context, R.layout.activity_listview, dataItem);
        this.context = context;
        this.imageLista=imageLista;
        this.descripcionLista=descripcionLista;
        this.lista=lista;
        this.fechaILista = fechaILista;
        this.fechaTLista = fechaTLista;
        this.direccionLista = direccionLista;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.childTextView);
            viewHolder.imageView=(ImageView) convertView.findViewById(R.id.childImage);
            viewHolder.descripcion=(TextView) convertView.findViewById(R.id.descrip);
            viewHolder.fechaInicio=(TextView) convertView.findViewById(R.id.hILista);
            viewHolder.fechaTermino=(TextView) convertView.findViewById(R.id.hTLista);
            viewHolder.direccion=(TextView) convertView.findViewById(R.id.inPlaceLista);
            viewHolder.ll=(RelativeLayout) convertView.findViewById(R.id.lllista);
            String image=imageLista[position];

            String url = "https://firebasestorage.googleapis.com/v0/b/prime-boulevard-168121.appspot.com/o/Images%2F"+image+"?alt=media";
            Picasso.with(this.getContext())
                    .load(url)
                    .error(R.drawable.kamal_logo)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(viewHolder.imageView);
            convertView.setTag(viewHolder);

        final String temp = getItem(position);
        viewHolder.text.setText(temp);
        viewHolder.ll.setBackgroundColor(Color.WHITE);
        viewHolder.descripcion.setText(descripcionLista[position]);
        viewHolder.fechaInicio.setText(fechaILista[position]);
        viewHolder.fechaTermino.setText(fechaTLista[position]);
        viewHolder.direccion.setText(direccionLista[position]);
        viewHolder.ll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(customListner!=null){

                    int colorID = ((ColorDrawable) viewHolder.ll.getBackground()).getColor();
                    if(colorID == Color.LTGRAY){
                        customListner.getPosition1234(position);
                    }else{
                        customListner.getPosition1234(position);
                        viewHolder.ll.setBackgroundColor(Color.LTGRAY);
                    }

                    }

            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView text;
        TextView descripcion;
        TextView fechaInicio;
        TextView fechaTermino;
        TextView direccion;
        RelativeLayout ll;
    }
}
