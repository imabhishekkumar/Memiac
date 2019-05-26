package dev.abhishekkumar.memeapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.content.Intent



class splash : AppCompatActivity() {
    val SPLASH_TIME_OUT: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dev.abhishekkumar.memeapp.R.layout.activity_splash)

        Handler().postDelayed( {

            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }, SPLASH_TIME_OUT)
    }

}
