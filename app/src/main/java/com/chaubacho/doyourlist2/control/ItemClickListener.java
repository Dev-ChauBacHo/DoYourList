package com.chaubacho.doyourlist2.control;

import android.view.View;

public interface ItemClickListener {
    void onClickListener(int position, View v);
    void onLongClickListener(int position, View v);
}
