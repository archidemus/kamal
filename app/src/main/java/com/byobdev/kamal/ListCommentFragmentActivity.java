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
 * Created by crono on 07-10-17.
 */

public class ListCommentFragmentActivity extends ArrayAdapter<String> {



    private Context context;
    private String[] imageLista;
    private String[] descripcionLista;
    private ListView lista;

    public ListCommentFragmentActivity(Context context, ArrayList<String> dataItem, String[] keyLista, String[] descripcionLista, String[] imageLista, ListView lista) {
        super(context, R.layout.activity_comment_fragment, dataItem);
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
            convertView = inflater.inflate(R.layout.activity_comment_fragment, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.childTextViewComment);
            viewHolder.imageView=(ImageView) convertView.findViewById(R.id.childImagecomment);
            viewHolder.descripcion=(TextView) convertView.findViewById(R.id.comment);
            viewHolder.ll=(RelativeLayout) convertView.findViewById(R.id.listaComment);
            String image=imageLista[position];

           String url = image;
            Picasso.with(this.getContext())
                    .load(url)
                    .error(R.drawable.kamal_logo).transform(new CircleTransform())
                    .into(viewHolder.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);
        viewHolder.descripcion.setText(descripcionLista[position]);
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView text;
        TextView descripcion;
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
