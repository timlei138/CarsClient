package com.lc.carsclient

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.LogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class CarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val build = PrettyFormatStrategy.newBuilder().apply {
            showThreadInfo(false)
            methodCount(3)
            methodOffset(5)
            tag("CarsClient")
            build()
        }.build()
        Logger.addLogAdapter(AndroidLogAdapter(build))

    }

}