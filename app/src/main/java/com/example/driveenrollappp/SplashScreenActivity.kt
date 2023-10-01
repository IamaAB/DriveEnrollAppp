package com.example.driveenrollappp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import com.example.driveenrollappp.DB
import com.example.driveenrollappp.LoginActivity
import com.example.driveenrollappp.SessionManager
import com.example.driveenrollappp.databinding.ActivityMainBinding


// splash screen basically a screen consit of any logo or image

class SplashScreenActivity : AppCompatActivity() {

    private var myDelayHandler: Handler? = null
    private val splash_delay: Long = 900 // 2 secs
    var db: DB? = null
    var session: SessionManager? = null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        db = DB(this)
        session = SessionManager(this)




        insertAdminData()
        myDelayHandler = Handler()
        myDelayHandler?.postDelayed(mRunnable, splash_delay)
    }


    private val mRunnable: Runnable = Runnable {
        if (session?.isLoggedIn == true)
        {
            val intent = Intent (this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun insertAdminData() {
        try {
            val sqlCheck = "Select * FROM ADMIN"
            db?.fireQuery(sqlCheck)?.use {
                if (it.count > 0) {
                    Log.d("SplashAcyivity", "data available")
                } else {
                    val sqlQuery =
                        "INSERT OR REPLACE INTO ADMIN(ID,USER_NAME,PASSWORD,MOBILE)VALUES('1', 'Admin','1234567','Ad') "
                    db?.executeQuery(sqlQuery)
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        myDelayHandler?.removeCallbacks(mRunnable)
    }
}
