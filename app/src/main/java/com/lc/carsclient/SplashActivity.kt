package com.lc.carsclient

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        countDownLatch.start()
    }


    private val countDownLatch  = object : CountDownTimer(1000 * 5,1000) {
        override fun onFinish() {
            startActivity<MainActivity>()
            finish()
        }

        override fun onTick(p0: Long) {
            CountDown.text = (p0 / 1000).toString();
        }

    }

}