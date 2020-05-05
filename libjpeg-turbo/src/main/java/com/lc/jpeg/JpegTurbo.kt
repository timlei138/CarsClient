package com.lc.jpeg

import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.security.cert.CertPath

class JpegTurbo {


    val TAG = javaClass.simpleName

    var frameCb:((ByteArray) -> Unit)? = null

    companion object{

        val turbo by lazy {
            JpegTurbo()
        }
    }

    init {
        System.loadLibrary("native-jpeg")

        File("/sdcard/jpg").apply {
            if(!exists()|| !isDirectory){
                mkdirs()
            }
        }
    }


    external fun yuvJpeg(yuv: ByteArray,width:Int,height: Int)


    private fun onFrame(byte: ByteArray){
        //Log.d(TAG,"jpeg -> ${jpegBuffer.capacity()}")
        //BitmapFactory.decodeByteArray(jpegBuffer.array(),0,jpegBuffer.capacity())
//        FileOutputStream(File("/sdcard/jpg/${System.currentTimeMillis()}.jpg")).let {
//            it.write(byte)
//            it.close()
//        }
        frameCb?.invoke(byte)
    }


}