package dev.abhishekkumar.memeapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.abhishekkumar.memeapp.Model.Model
import dev.abhishekkumar.memeapp.api.ApiService
import dev.abhishekkumar.memeapp.api.RetrofitClient
import retrofit2.*
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.BitmapFactory
import android.os.Environment
import java.net.URL
import android.media.MediaScannerConnection
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.Chip
import dev.abhishekkumar.memeapp.R
import java.net.InetAddress


class MainActivity : AppCompatActivity() {
    lateinit var reloadBtn: FloatingActionButton
    lateinit var saveBtn: FloatingActionButton
    lateinit var imageView: ImageView
    lateinit var internetIcon: ImageView
    lateinit var textView: TextView
    lateinit var editText: EditText
    lateinit var imageName: String
    lateinit var imageURL: String
    lateinit var chip1: Chip
    lateinit var chip2: Chip
    lateinit var chip3: Chip
    lateinit var chip4: Chip
    lateinit var chip5: Chip
    lateinit var internetLayout: ConstraintLayout
    lateinit var noInternetLayout: ConstraintLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reloadBtn = findViewById(R.id.reloadFAB)
        saveBtn = findViewById(R.id.saveFAB)
        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.memeTitle)
        editText = findViewById(R.id.editCustom)
        chip1= findViewById(R.id.chip)
        chip2= findViewById(R.id.chip2)
        chip3= findViewById(R.id.chip3)
        chip4= findViewById(R.id.chip4)
        chip5= findViewById(R.id.chip5)
        internetLayout = findViewById(R.id.internet)
        noInternetLayout=findViewById(R.id.noInternet)
        internetIcon= findViewById(R.id.imageView2)


        val rotateFwd = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_forward)

        checkConnection()

        internetIcon.setOnClickListener{
            checkConnection()
        }

        reloadBtn.setOnClickListener {

            apiCall()
            reloadBtn.startAnimation(rotateFwd)
        }

        chip1.setOnClickListener {
            editText.setText(chip1.text.toString())
            apiCall()
        }
        chip2.setOnClickListener {
            editText.setText(chip2.text.toString())
            apiCall()
        }
        chip3.setOnClickListener {
            editText.setText(chip3.text.toString())
            apiCall()
        }
        chip4.setOnClickListener {
            editText.setText(chip4.text.toString())
            apiCall()
        }
        chip5.setOnClickListener {
            editText.setText(chip5.text.toString())
            apiCall()
        }
        saveBtn.setOnClickListener {
            val thread = Thread(Runnable {
                try {
                    try {
                        val url = URL(imageURL)
                        val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        SaveImage(image)
                    } catch (e: IOException) {
                        Log.d("Exception", e.toString())

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            thread.start()
            Toast.makeText(applicationContext, "Saved.", Toast.LENGTH_SHORT).show()
        }


    }

    private fun checkConnection() {

        try {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

           if(isConnected){
               apiCall()
               internetLayout.visibility= View.VISIBLE
               noInternetLayout.visibility=View.GONE
           }
            else{
               internetLayout.visibility= View.GONE
               noInternetLayout.visibility=View.VISIBLE
           }
        }catch (e:java.lang.Exception){
            Log.d("Internet",e.toString())

        }


    }

    fun grantPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            } else {

                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 1)

            }
        }
    }

    private fun SaveImage(finalBitmap: Bitmap) {
        grantPermission()
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File(root + "/Memes")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        imageName += ".jpg"
        val file = File(myDir, imageName)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MediaScannerConnection.scanFile(
            this, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }

    }

    private fun apiCall() {
        val retrofit: Retrofit = RetrofitClient.getClient("https://meme-api.herokuapp.com/")

        val apiService: ApiService = retrofit.create()
        val call: Call<Model> = apiService.loadMemes("gimme"+getCustomMeme())
        call.enqueue(object : Callback<Model> {
            override fun onFailure(call: Call<Model>?, t: Throwable?) {
                Log.i("Result", t.toString())
            }

            override fun onResponse(call: Call<Model>?, response: Response<Model>?) {
                val result = response?.body()
                if (result != null) {
                    Log.i("Result", result.url)
                    textView.text = result.title
                    imageURL = result.url
                    imageName = result.url.substring(18, 26)
                    Picasso.get()
                        .load(imageURL)
                        .into(imageView)

                }
            }
        })
    }

    private fun getCustomMeme(): String {
        return if (editText.text==null || editText.text.toString()=="" || editText.text.toString()==" ")
            ""
        else
            "/" +editText.text.toString()+"memes"

    }
}
