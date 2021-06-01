package com.chaubacho.doyourlist2.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chaubacho.doyourlist2.R;
import com.chaubacho.doyourlist2.data.model.ColorItem;

import java.util.ArrayList;

public class SpinnerColorAdapter extends ArrayAdapter<ColorItem> {
    public SpinnerColorAdapter(Context context, ArrayList<ColorItem> colorList) {
        super(context, 0, colorList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_row, parent, false
            );
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.image_view_color);


        ColorItem currentItem = getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getColorImage());

        }

        return convertView;
    }
}
