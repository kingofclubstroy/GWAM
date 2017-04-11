package com.example.my.facebookauth.utilities.scrollFilter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.my.facebookauth.R;

import java.util.List;

import static com.example.my.facebookauth.R.id.rectangle;

/**
 * Created by Owner on 2017-04-04.
 */

public class CircularArrayAdapter extends RecyclerView.Adapter<CircularArrayAdapter.ViewHolder> {


    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;

    public final int MIDDLE;
    private String[] objects;

    private String[] mcategories;

    private Context mContext;

    SnapHelper snapHelper;
    RecyclerView.LayoutManager layoutManager;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView categoryText;
        public View Rectangle;



        public ViewHolder(View itemView) {

            super(itemView);

            categoryText = (TextView) itemView.findViewById(R.id.category);
            Rectangle = (View) itemView.findViewById(rectangle);
        }

    }

    public CircularArrayAdapter(Context context, String[] categories) {


        mContext = context;
        mcategories = categories;
        MIDDLE = HALF_MAX_VALUE - (HALF_MAX_VALUE % categories.length);

    }

    private Context getContext() {
        return mContext;
    }



    @Override
    public CircularArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View categoryView = inflater.inflate(R.layout.calendar_list_adapter, parent, false);

        ViewHolder viewHolder = new ViewHolder(categoryView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CircularArrayAdapter.ViewHolder holder, int position) {

        String category = mcategories[position % mcategories.length];

        TextView textView = holder.categoryText;
        textView.setText(category);


        View rectangle = holder.Rectangle;

        switch (category) {

            case "Food":
                rectangle.setBackgroundColor(mContext.getResources().getColor(R.color.Food_color));
                break;
            case "Party":
                rectangle.setBackgroundColor(mContext.getResources().getColor(R.color.Party_color));
                break;
            case "All":
                rectangle.setBackgroundColor(mContext.getResources().getColor(R.color.All_color));
                break;
            case "Rec":
                rectangle.setBackgroundColor(mContext.getResources().getColor(R.color.Rec_color));
                break;
            case "Music":
                rectangle.setBackgroundColor(mContext.getResources().getColor(R.color.Music_color));
                break;
            case "Games":
                rectangle.setBackgroundColor(mContext.getResources().getColor(R.color.Games_color));
                break;
            default:
                rectangle.setBackgroundColor(Color.BLACK);
                break;
        }




    }



    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }


}
