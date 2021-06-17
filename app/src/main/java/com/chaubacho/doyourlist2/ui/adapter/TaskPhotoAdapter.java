package com.chaubacho.doyourlist2.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.chaubacho.doyourlist2.R;
import com.chaubacho.doyourlist2.control.ItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class TaskPhotoAdapter extends RecyclerView.Adapter<TaskPhotoAdapter.MyViewHolder> {
    private static final String TAG = "TaskPhotoAdapter";
    private List<StorageReference> referenceList;
    private Context context;

    public TaskPhotoAdapter() {
        referenceList = new ArrayList<>();
    }

    public TaskPhotoAdapter(List<StorageReference> referenceList) {
        this.referenceList = referenceList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new TaskPhotoAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskPhotoAdapter.MyViewHolder holder, int position) {
        StorageReference reference = referenceList.get(position);

        final long ONE_MEGABYTE = 1024 * 1024;
        reference
                .getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytesPrm -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.length);
                    holder.imageView.setImageBitmap(bmp);
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);
            }
        });

        holder.setClickListener(new ItemClickListener() {

            @Override
            public void onClickListener(int position, View v) {

            }

            @Override
            public void onLongClickListener(int position, View v) {
                Log.d(TAG, "onLongClick: LONGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                final CharSequence[] items = {"Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            referenceList.get(position)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: Delete success!");
                                            Toast.makeText(context, "Deleted a photo", Toast.LENGTH_SHORT).show();
                                            referenceList.remove(position);
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: Cannot delete" + e);
                                            Toast.makeText(context, "Can't delete a photo", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
                builder.show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return referenceList == null ? 0 : referenceList.size();
    }

    public void updateData(List<StorageReference> newList) {
        if (referenceList != null) referenceList.clear();
        else referenceList = new ArrayList<>();
        referenceList.addAll(newList);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final ImageView imageView;
        private ItemClickListener clickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_photo);
            itemView.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener listener) {
            this.clickListener = listener;
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "onLongClick: " + getAdapterPosition());
            clickListener.onLongClickListener(getAdapterPosition(), v);
            return true;
        }

    }
}
