package Adaptor;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import Classes.Stores;
import example.luisnava.com.firebaseexample.R;

/**
 * Created by DELL on 14/02/2018.
 */

public class ListViewAdapter extends ArrayAdapter<Stores> {
    private Activity context;
    private int resource;
    private List<Stores> listData;

    public ListViewAdapter(@NonNull Activity context, int resource, @NonNull List<Stores> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.listData = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View view = inflater.inflate(resource, null);
        TextView title = (TextView) view.findViewById(R.id.item);
        TextView address = (TextView) view.findViewById(R.id.textView1);
        ImageView img = (ImageView) view.findViewById(R.id.iconList);

        title.setText(listData.get(position).getTitle());
        address.setText(listData.get(position).getAddress());
        Glide.with(context).load(listData.get(position).getImage()).into(img);

        return view;

    }

    public void addElement(Stores element) {

        listData.add(element);
    }
}
