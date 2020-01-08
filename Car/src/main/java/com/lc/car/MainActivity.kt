package com.lc.car

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.IOException
import android.util.Log
import com.google.android.things.contrib.driver.button.Button

private val TAG = MainActivity::class.java.simpleName
private val gpioButtonPinName = "BUS NAME"

class MainActivity : AppCompatActivity() {
    private lateinit var mButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyButton()
    }

    private fun setupButton() {
        try {
            mButton = Button(
                gpioButtonPinName,
                // high signal indicates the button is pressed
                // use with a pull-down resistor
                Button.LogicState.PRESSED_WHEN_HIGH
            )
            mButton.setOnButtonEventListener(object : Button.OnButtonEventListener {
                override fun onButtonEvent(button: Button, pressed: Boolean) {
                    // do something awesome
                }
            })
        } catch (e: IOException) {
            // couldn't configure the button...
        }

    }

    private fun destroyButton() {
        Log.i(TAG, "Closing button")
        try {
            mButton.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing button", e)
        }
    }

}
