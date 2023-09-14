package com.example.paintapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.paintapp.adapters.FilesAdapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListFilesAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);
        initToolbar();
        initViews();
    }

    private void initViews() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_files);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        FilesAdapters filesAdapters = new FilesAdapters(this, loadFile());
        recyclerView.setAdapter(filesAdapters);
    }

    private List<File> loadFile() {
        ArrayList<File> inFiles = new ArrayList<>();
        File parendDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        File[] files = parendDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".png")) {
                inFiles.add(file);
            }

        }
        if(files.length>0)
        {
            TextView textView = findViewById(R.id.status_empty);
            textView.setVisibility(View.GONE);
        }
        return inFiles;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pictures");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}