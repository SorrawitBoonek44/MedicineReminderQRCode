package com.example.medicinereminderqrcode;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicinereminderqrcode.medicine.MedicineReminder;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.Holder> {

    private final ArrayList<MedicineReminder> mDataSet;

    public MyRecyclerAdapter(ArrayList<MedicineReminder> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        return new Holder(view);
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if(mDataSet.get(position).checkOutOfTime()){
            holder.setCardViewColor(Color.parseColor("#AEAEAE"));
            holder.setImageView(R.drawable.ic_medicine);
        }
        if(mDataSet.get(position).checkIsCurrentTime()){
            holder.setCardViewColor(Color.parseColor("#76BBB3"));
            holder.setImageView(R.drawable.medicine);
            holder.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.setTextTitle(mDataSet.get(position).getTime());
        holder.setTextDescription(mDataSet.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    static class Holder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textDescription;
        CardView cardView;
        ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void setImageView(int image) {
            this.imageView.setImageResource(image);
        }

        public void setTextTitle(String textTitle) {
            this.textTitle.setText(textTitle);
        }

        public void setTextDescription(String textDescription) {
            this.textDescription.setText(textDescription);
        }

        public void setCardViewColor(int color){
            cardView.setCardBackgroundColor(color);
        }

        public void setTextColor(int color){
            textDescription.setTextColor(color);
        }

    }


}
