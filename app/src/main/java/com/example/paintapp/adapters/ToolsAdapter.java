package com.example.paintapp.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paintapp.Interface.ToolsListener;
import com.example.paintapp.Interface.ViewOnClick;
import com.example.paintapp.R;
import com.example.paintapp.model.ToolsItem;
import com.example.paintapp.viewHolder.ToolsViewHolder;

import java.util.List;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsViewHolder> {

    private List<ToolsItem> toolsItemList;
    private int selected = -1;
    private ToolsListener listener;

    public ToolsAdapter(List<ToolsItem> toolsItemList, ToolsListener listener) {
        this.toolsItemList = toolsItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToolsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tools_item, parent, false);
        return new ToolsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolsViewHolder holder, int position) {
        holder.name.setText(toolsItemList.get(position).getName());
        holder.icone.setImageResource(toolsItemList.get(position).getIcone());
        holder.setViewOnClick(new ViewOnClick() {
            @Override
            public void onClick(int pos) {
                selected=pos;
                listener.onSelected(toolsItemList.get(pos).getName());
                notifyDataSetChanged();
            }
        });
        if(selected == position){
            holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
        }else {
            holder.name.setTypeface(Typeface.DEFAULT);
        }
    }

    @Override
    public int getItemCount() {
        return toolsItemList.size();
    }
}
