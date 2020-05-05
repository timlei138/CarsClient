package com.lc.jpeg

import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.security.cert.CertPath

class JpegTurbo {


    val TAG = javaClass.simpleName

    companion object{

        val turbo by lazy {
            JpegTurbo()
        }
    }


    val jpegBuffer: ByteBuffer

    init {
        System.loadLibrary("native-jpeg")
        jpegBuffer = ByteBuffer.allocateDirect(640 * 480)
        init(jpegBuffer)
        File("/sdcard/jpg").apply {
            if(!exists()|| !isDirectory){
                mkdirs()
            }
        }
    }

    private external fun init(byteBuffer: ByteBuffer);

    external fun yuvJpeg(yuv: ByteArray,width:Int,height: Int)




    private fun onFrame(byte: ByteArray){
        //Log.d(TAG,"jpeg -> ${jpegBuffer.capacity()}")
        //BitmapFactory.decodeByteArray(jpegBuffer.array(),0,jpegBuffer.capacity())
        FileOutputStream(File("/sdcard/jpg/${System.currentTimeMillis()}.jpg")).let {
            it.write(byte)
            it.close()
        }
    }


}