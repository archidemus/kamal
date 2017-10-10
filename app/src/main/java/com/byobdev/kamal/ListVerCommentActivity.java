package com.byobdev.kamal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by crono on 08-10-17.
 */

public class ListVerCommentActivity extends ArrayAdapter<String> {

    private Context context;
    private String[] imageLista;
    private String[] descripcionLista;
    private ListView lista;
    private String[] respuestaLista;

    customButtonListener customListner;

    public interface customButtonListener {
        void getPosition1(int position);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    public ListVerCommentActivity(Context context, ArrayList<String> dataItem, String[] keyLista, String[] descripcionLista, String[] imageLista, ListView lista, String[] respuestaLista) {
        super(context, R.layout.activity_ver_comment, dataItem);
        this.context = context;
        this.imageLista=imageLista;
        this.descripcionLista=descripcionLista;
        this.lista=lista;
        this.respuestaLista = respuestaLista;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_ver_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.childTextViewVerComment);
            viewHolder.imageView=(ImageView) convertView.findViewById(R.id.childVerImagecomment);
            viewHolder.descripcion=(TextView) convertView.findViewById(R.id.Vercomment);
            viewHolder.respuesta=(TextView) convertView.findViewById(R.id.VerRespuesta);
            viewHolder.respuestaContenido=(TextView) convertView.findViewById(R.id.VerRespuestaContenido);
            viewHolder.ll=(RelativeLayout) convertView.findViewById(R.id.listaVerComment);
            String image=imageLista[position];

            String url = image;
            Picasso.with(this.getContext())
                    .load(url)
                    .error(R.drawable.kamal_logo).transform(new CircleTransform())
                    .into(viewHolder.imageView);
            convertView.setTag(viewHolder);

        final String temp = getItem(position);
        viewHolder.text.setText(temp);
        viewHolder.descripcion.setText(descripcionLista[position]);
        if(respuestaLista[position] == null){
            viewHolder.respuesta.setVisibility(View.GONE);
        }else if(respuestaLista[position].equals("")){
            viewHolder.respuesta.setVisibility(View.GONE); // Esta linea no funciona
        }else{

            viewHolder.respuestaContenido.setText(respuestaLista[position]);
        }
        viewHolder.ll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(customListner!=null){
                    customListner.getPosition1(position);}

            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView text;
        TextView descripcion;
        TextView respuesta;
        TextView respuestaContenido;
        RelativeLayout ll;
    }
    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

}
