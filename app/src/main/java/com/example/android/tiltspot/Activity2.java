package com.example.android.tiltspot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.view.View;

import com.squareup.picasso.Picasso;

//import com.aditya.filebrowser.FileBrowser;

public class Activity2 extends AppCompatActivity {





    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);





        BasicFileAttributes attributes = null;

        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> list2 = new ArrayList<String>();

        File directory = new File(Environment.getExternalStorageDirectory().getPath()  + "/Pictures/PoseCam/");
        final File[] files = directory.listFiles();

        try {
            for (int i = 0; i < files.length; i++)
            {
                list.add(files[i].getName());

                File file = new File(Environment.getExternalStorageDirectory().getPath()  + "/Pictures/PoseCam/" + files[i].getName());
                Path filePath = file.toPath();



                try
                {
                    attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
                }
                catch (IOException exception)
                {
                    System.out.println("Exception handled when trying to get file " +
                            "attributes: " + exception.getMessage());
                }
                Date creationDate = new Date(attributes.creationTime().to(TimeUnit.MILLISECONDS));

                DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
                String dateString2 = dateFormat2.format(creationDate).toString();


                list2.add(dateString2);

                System.out.println("creationDate is "+ attributes.creationTime().toString());
            }

        }
        catch (Exception e)
        {

        }




        //instantiate custom adapter
        MyCustomAdapter adapter = new MyCustomAdapter(list, list2,this);

        //handle listview and assign adapter
        final ListView lView = (ListView)findViewById(R.id.listview);
        lView.setAdapter(adapter);



        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openActivity1();

            }
        });


        findViewById(R.id.DeleteAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File directory = new File(Environment.getExternalStorageDirectory().getPath()  + "/Pictures/PoseCam/");
                File[] files = directory.listFiles();
                for (int i = 0; i < files.length; i++)
                {

                    File file = new File(Environment.getExternalStorageDirectory().getPath()
                            + "/Pictures/PoseCam/" + files[i].getName());
                    boolean deleted = file.delete();
                    recreate();
                }

            }
        });




    }

    public void openActivity1()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }



    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();

        private ArrayList<String> list2 = new ArrayList<String>();
        private Context context;



        public MyCustomAdapter(ArrayList<String> list,ArrayList<String> list2, Context context) {
            this.list = list;
            this.list2 = list2;

            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.customlayout, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
            listItemText.setText(list.get(position));




            File imgFile = new  File(Environment.getExternalStorageDirectory().getPath()
                    + "/Pictures/PoseCam/"+list.get(position));





            ImageView listItemimg = (ImageView)view.findViewById(R.id.imageView);
            Picasso.get().load(imgFile).config(Bitmap.Config.RGB_565).fit().into(listItemimg);


            //Handle buttons and add onClickListeners
            ImageButton deleteBtn = (ImageButton)view.findViewById(R.id.delete_btn);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    File file = new File(Environment.getExternalStorageDirectory().getPath()  + "/Pictures/PoseCam/", list.get(position));
                    boolean deleted = file.delete();

                    list.remove(position); //or some other task

                    notifyDataSetChanged();
                }
            });


            return view;
        }
    }








}