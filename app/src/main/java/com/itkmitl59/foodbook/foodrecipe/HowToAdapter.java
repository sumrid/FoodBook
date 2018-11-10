package com.itkmitl59.foodbook.foodrecipe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itkmitl59.foodbook.R;

import java.util.List;

public class HowToAdapter extends ArrayAdapter<HowTo> {

    private Context mContext;
    private List<HowTo> mHowTo;

    public HowToAdapter(@NonNull Context context, int resource, @NonNull List<HowTo> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mHowTo = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.how_to_item, parent, false);

        TextView howTo = view.findViewById(R.id.how_to_item);
        TextView howToDescrip = view.findViewById(R.id.how_to_item_descrip);
        ImageView imageView = view.findViewById(R.id.how_to_image);

        HowTo item = mHowTo.get(position);
        howTo.setText(String.format("ขั้นตอน %d/%d", position+1, mHowTo.size()));
        howToDescrip.setText(item.getDescription());

        return view;
    }
}
