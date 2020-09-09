package com.example.android.tiltspot

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.android.tiltspot.MainActivity
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

//import com.aditya.filebrowser.FileBrowser;
class Activity2 : AppCompatActivity() {
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity2)
        var attributes: BasicFileAttributes? = null
        val list = ArrayList<String>()
        val list2 = ArrayList<String>()
        val directory = File(Environment.getExternalStorageDirectory().path + "/Pictures/PoseCam/")
        val files = directory.listFiles()

        Arrays.sort(files)


        try {
            for (i in files.indices) {
                list.add(files[i].name)
                val file = File(Environment.getExternalStorageDirectory().path + "/Pictures/PoseCam/" + files[i].name)
                val filePath = file.toPath()
                try {
                    attributes = Files.readAttributes(filePath, BasicFileAttributes::class.java)
                } catch (exception: IOException) {
                    println("Exception handled when trying to get file " +
                            "attributes: " + exception.message)
                }
                val creationDate = Date(attributes!!.creationTime().to(TimeUnit.MILLISECONDS))
                val dateFormat2: DateFormat = SimpleDateFormat("dd/MM/yyyy hh.mm aa")
                val dateString2 = dateFormat2.format(creationDate).toString()
                list2.add(dateString2)
                println("creationDate is " + attributes.creationTime().toString())
            }
        } catch (e: Exception) {
        }


        //instantiate custom adapter
        val adapter = MyCustomAdapter(list, list2, this)

        //handle listview and assign adapter
        val lView = findViewById<View>(R.id.listview) as ListView
        lView.adapter = adapter
        findViewById<View>(R.id.previous).setOnClickListener { openActivity1() }
        findViewById<View>(R.id.DeleteAll).setOnClickListener {
            val directory = File(Environment.getExternalStorageDirectory().path + "/Pictures/PoseCam/")
            val files = directory.listFiles()
            for (i in files.indices) {
                val file = File(Environment.getExternalStorageDirectory().path
                        + "/Pictures/PoseCam/" + files[i].name)
                val deleted = file.delete()
                recreate()
            }
        }
    }

    fun openActivity1() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    inner class MyCustomAdapter(list: ArrayList<String>, list2: ArrayList<String>, context: Context) : BaseAdapter(), ListAdapter {
        private var list = ArrayList<String>()
        private var list2 = ArrayList<String>()
        private val context: Context
        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(pos: Int): Any {
            return list[pos]
        }

        override fun getItemId(pos: Int): Long {
            return 0
            //just return 0 if your list items do not have an Id variable.
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View ?{

                var view = convertView


                val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.customlayout, null)

                //Handle TextView and display string from your list

                val listItemText = view.findViewById<View>(R.id.list_item_string) as TextView
                listItemText.text = list[position]
                val imgFile = File(Environment.getExternalStorageDirectory().path
                        + "/Pictures/PoseCam/" + list[position])
                val listItemimg = view.findViewById<View>(R.id.imageView) as ImageView
                Picasso.get().load(imgFile).config(Bitmap.Config.RGB_565).fit().into(listItemimg)


                //Handle buttons and add onClickListeners
                val deleteBtn = view.findViewById<View>(R.id.delete_btn) as ImageButton
                deleteBtn.setOnClickListener { //do something
                    val file = File(Environment.getExternalStorageDirectory().path + "/Pictures/PoseCam/", list[position])
                    val deleted = file.delete()
                    list.removeAt(position) //or some other task
                    notifyDataSetChanged()
                }
                return view

        }

        init {
            this.list = list
            this.list2 = list2
            this.context = context
        }
    }
}