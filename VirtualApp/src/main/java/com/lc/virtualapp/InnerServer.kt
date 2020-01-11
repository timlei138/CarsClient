package com.lc.virtualapp

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.nfc.Tag
import android.util.Log
import android.util.Size
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue


class InnerServer private constructor() : Thread(){


    companion object{
        val TAG = "Server";
        val instance by lazy {
            InnerServer()
        }
    }


    private val mQueue: LinkedBlockingQueue<ByteArray>
    private val socket: ServerSocket


    var size: Size? = null

    init {
        mQueue = LinkedBlockingQueue(20)
        socket = ServerSocket(9999,1)

    }

    fun seedFrame(frame: ByteArray){
        mQueue.offer(frame)
    }


    override fun run() {
        while (true){
            Log.d(TAG,"wait client...")
            val client = socket.accept();
            Log.d(TAG,"connected client $client")
            FeedThread(client).start()
        }

    }


    inner class FeedThread(val socket: Socket) : Thread(){

        val bis: InputStream
        val bos: OutputStream

        init {
            bis = socket.getInputStream()
            bos = socket.getOutputStream()
        }

        override fun run() {
            while (true){
                val frame = mQueue.take()
                if(frame != null){
                    val image = YuvImage(frame,ImageFormat.JPEG,size!!.width,size!!.height,null)
                    val jpegByteArray = ByteArrayOutputStream()
                    image.compressToJpeg(Rect(0,0,size!!.width,size!!.height),50,jpegByteArray)
                    Log.d(TAG,"write size:${jpegByteArray.size()}")
                    bos.write(frame,0,jpegByteArray.size())
                    bos.flush()
                }
            }
        }
    }
}