package com.example.paintapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.paintapp.Interface.ToolsListener;
import com.example.paintapp.adapters.ToolsAdapter;
import com.example.paintapp.model.ToolsItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ToolsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTools();
    }

    private void initTools() {
        RecyclerView recyclerView = findViewById(R.id.recycle_view_tools);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        ToolsAdapter toolsAdapter = new ToolsAdapter(loadTools(),this);
        recyclerView.setAdapter(toolsAdapter);
    }

    private List<ToolsItem> loadTools() {
        List<ToolsItem> result = new ArrayList<>();
        result.add(new ToolsItem(R.drawable.baseline_brush_24,"brush"));
        result.add(new ToolsItem(R.drawable.eraser,"eraser"));
        result.add(new ToolsItem(R.drawable.baseline_palette_24,"colors"));
        result.add(new ToolsItem(R.drawable.paint_bucket,"background"));
        result.add(new ToolsItem(R.drawable.baseline_undo_24,"undo"));
        return result;
    }

    public void finishPaint(View view) {
    }

    public void shareApp(View view) {
    }

    public void showFiles(View view) {
    }

    public void saveFile(View view) {
    }

    @Override
    public void onSelected(String name) {

    }
}