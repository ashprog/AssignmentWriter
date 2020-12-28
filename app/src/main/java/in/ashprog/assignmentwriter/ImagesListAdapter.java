package in.ashprog.assignmentwriter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

class ImagesListAdapter extends RecyclerView.Adapter<ImagesListAdapter.ImagesListViewHolder> {

    Context context;
    ArrayList<File> imagesList;
    ArrayList<Integer> checkedPositions;

    SelectAllCBStateChanger selectAllCBStateChanger;

    public ImagesListAdapter(Context context, ArrayList<File> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
        checkedPositions = new ArrayList<>();
    }

    class ImagesListViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        ImageView pageIV;
        TextView imageNameTV;

        public ImagesListViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            pageIV = itemView.findViewById(R.id.pageIV);
            imageNameTV = itemView.findViewById(R.id.imageNameTV);
        }
    }

    @NonNull
    @Override
    public ImagesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_list_view, parent, false);

        return new ImagesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagesListViewHolder holder, final int position) {
        holder.pageIV.setImageBitmap(BitmapFactory.decodeFile(imagesList.get(position).getAbsolutePath()));
        holder.imageNameTV.setText(imagesList.get(position).getName());

        if (checkedPositions.contains(position)) holder.checkBox.setChecked(true);
        else holder.checkBox.setChecked(false);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) checkedPositions.add(position);
                else checkedPositions.remove(new Integer(position));

                selectAllCBStateChanger.changeSelectAllCBState();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public void clearCheckedList() {
        checkedPositions.clear();
    }

    public void selectAll() {
        clearCheckedList();
        for (int i = 0; i < imagesList.size(); i++) {
            checkedPositions.add(i);
        }
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        clearCheckedList();
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getCheckedPositions() {
        return checkedPositions;
    }

    interface SelectAllCBStateChanger {
        void changeSelectAllCBState();
    }

    void setSelectAllCBStateChangeListener(SelectAllCBStateChanger selectAllCBStateChanger) {
        this.selectAllCBStateChanger = selectAllCBStateChanger;
    }
}
