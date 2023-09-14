package com.example.paintapp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paintapp.Interface.ToolsListener;
import com.example.paintapp.adapters.ToolsAdapter;
import com.example.paintapp.common.Common;
import com.example.paintapp.model.ToolsItem;
import com.example.paintapp.widget.PaintView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ToolsListener {

    private static final int PICK_IMAGE = 1000;
    PaintView mPaintView;
    int colorBackground, colorBrush;
    int brushSize, eraserSize;
    private static final int REQUEST_PERMISSION =1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTools();
    }

    private void initTools() {
        colorBackground = Color.WHITE;
        colorBrush = Color.BLACK;
        eraserSize = brushSize = 12;
        mPaintView = findViewById(R.id.paint_view);
        RecyclerView recyclerView = findViewById(R.id.recycle_view_tools);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        ToolsAdapter toolsAdapter = new ToolsAdapter(loadTools(), this);
        recyclerView.setAdapter(toolsAdapter);
    }

    private List<ToolsItem> loadTools() {
        List<ToolsItem> result = new ArrayList<>();
        result.add(new ToolsItem(R.drawable.baseline_brush_24, Common.BRUSH));
        result.add(new ToolsItem(R.drawable.eraser, Common.ERASER));
        result.add(new ToolsItem(R.drawable.baseline_image_24,Common.IMAGE));
        result.add(new ToolsItem(R.drawable.baseline_palette_24, Common.COLORS));
        result.add(new ToolsItem(R.drawable.paint_bucket, Common.BACKGROUND));
        result.add(new ToolsItem(R.drawable.baseline_undo_24, Common.RETURN));
        return result;
    }

    public void finishPaint(View view) {
    }

    public void shareApp(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String bodyText="http://play.google.com/store/apps/details?id="+getPackageName();
        intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT,bodyText);
        startActivity(Intent.createChooser(intent,"share this app"));
    }

    public void showFiles(View view) {
        startActivity(new Intent(this,ListFilesAct.class));
    }

    public void saveFile(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);
        }else {
            saveBitmap();
        }

    }

    private void saveBitmap() {
        Bitmap bitmap = mPaintView.getBitmap();
        String file_name = UUID.randomUUID()+".png";
        File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator+getString(R.string.app_name));
        if(!folder.exists())
        {
            folder.mkdir();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(folder+ File.separator+file_name);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            Toast.makeText(this, "picture saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_PERMISSION && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            saveBitmap();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSelected(String name) {
        switch (name) {
            case Common.BRUSH:
                mPaintView.desableEraser();
                showDialogSize(false);
                break;
            case Common.ERASER:
                mPaintView.enabledEraser();
                showDialogSize(true);
                break;
            case Common.RETURN:
                mPaintView.returnLastAction();
                break;
            case Common.BACKGROUND:
                updateColor(name);
                break;
            case Common.COLORS:
                updateColor(name);
                break;
            case Common.IMAGE:
                getImage();
                break;
        }
    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select picture"),PICK_IMAGE);
    }

//    private void selectImage(){
//        Intent i = new Intent();
//        i.setType("image/*");
//        i.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(i,100);
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && data!=null && data.getData()!=null)
        {
            // data.getData();    geting uri
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            Uri pickedImage = data.getData();
//            String[] filePath = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(pickedImage,filePath,null,null,null);
//            cursor.moveToFirst();
//            String k = filePath[0];
//            Toast.makeText(this, pickedImage.toString(), Toast.LENGTH_SHORT).show();
//            String imagePath = cursor.getString(cursor.getColumnIndex(k));
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap1 = BitmapFactory.decodeFile(imagePath, options);
            mPaintView.setImage(bitmap);
        //    cursor.close();
        }
    }
    //@Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode == PICK_IMAGE && data!=null && resultCode==RESULT_OK)
//        {
//            Uri pickedImage = data.getData();
//            String[] filePath = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(pickedImage,filePath,null,null,null);
//            cursor.moveToFirst();
//            String k = pickedImage.toString();
//            Toast.makeText(this, k, Toast.LENGTH_SHORT).show();
//            String imagePath = cursor.getString(cursor.getColumnIndex(k.toString()));
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
//            mPaintView.setImage(bitmap);
//            cursor.close();
//
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void updateColor(String name) {
        int color;
        if (name.equals(Common.BACKGROUND)) {
            color = colorBackground;
        } else {
            color = colorBrush;
        }
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(color)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                        if (name.equals(Common.BACKGROUND)) {
                            colorBackground = lastSelectedColor;
                            mPaintView.setColorBackground(colorBackground);
                        } else {
                            colorBrush = lastSelectedColor;
                            mPaintView.setBrushColor(colorBrush);
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).build().show();
    }

    private void showDialogSize(boolean isEraser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null, false);
        TextView toolsSelected = view.findViewById(R.id.status_tools_selected);
        TextView statusSize = view.findViewById(R.id.status_size);
        ImageView ivTools = view.findViewById(R.id.iv_tools);
        SeekBar seekBar = view.findViewById(R.id.seekbar_size);
        if (isEraser) {
            toolsSelected.setText("Eraser Size");
            ivTools.setImageResource(R.drawable.eraser);
            statusSize.setText("Selected Size : " + eraserSize);
        } else {
            toolsSelected.setText("Brush Size");
            ivTools.setImageResource(R.drawable.baseline_black_brush);
            statusSize.setText("Selected Size : " + brushSize);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isEraser) {
                    eraserSize = i + 1;
                    statusSize.setText("Selected Size : " + eraserSize);
                    mPaintView.setSizeEraser(eraserSize);
                } else {
                    brushSize = i + 1;
                    statusSize.setText("Selected Size : " + brushSize);
                    mPaintView.setSizeBrush(brushSize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }
}