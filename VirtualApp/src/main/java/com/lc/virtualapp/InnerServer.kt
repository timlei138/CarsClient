package com.lc.virtualapp

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.nfc.Tag
import android.util.Log
import android.util.Size
import com.lc.command.BytesUtils
import com.lc.command.COMMAND_FLAG
import com.lc.command.TYPE_VIDEO
import com.lc.command.UDP_PORT
import java.io.*
import java.net.*
import java.util.concurrent.LinkedBlockingQueue


class InnerServer private constructor(){


    companion object{
        val TAG = "Server";
        val instance by lazy {
            InnerServer()
        }
    }

    private val mQueue: LinkedBlockingQueue<ByteArray>
    private var client: Client? = null;
    var size: Size? = null

    init {
        mQueue = LinkedBlockingQueue(20)

    }

    fun seedFrame(frame: ByteArray){
        mQueue.offer(frame)
    }


    inner class FindClientThread : Thread(){
        private var hasFind = false
        override fun run() {
            super.run()
            Log.d(TAG,"start find client thread")
            val socket  = DatagramSocket(UDP_PORT)
            while (!hasFind){
                val buf = ByteArray(32)
                val packet = DatagramPacket(buf,buf.size)
                socket.receive(packet)
                packet.apply {
                    if(String(buf,0,length) == COMMAND_FLAG){
                        client = Client(packet.address.hostName,packet.address.hostAddress,packet.port)
                        Log.d(TAG,"receiver client info -> $client")
                        val msg = COMMAND_FLAG.toByteArray()
                        val receivePacket = DatagramPacket(msg,msg.size,packet.address,packet.port)
                        socket.send(receivePacket)
                        hasFind = true
                    }
                }
            }
            socket.close()
        }
    }

    inner class FeedFrameThread(val socket: Socket) : Thread(){

        val bis: InputStream
        val bos: OutputStream

        init {
            bis = socket.getInputStream()
            bos = socket.getOutputStream()
        }

        override fun run() {
            var i = 0
            while (true){
                val frame = mQueue.take()
                if(frame != null && i < 2){
                    val image = YuvImage(frame,ImageFormat.NV21,size!!.width,size!!.height,null)
                    val jpegByteArray = ByteArrayOutputStream()
                    image.compressToJpeg(Rect(0,0,size!!.width,size!!.height),50,jpegByteArray)
                    bos.write(TYPE_VIDEO)
                    val len = jpegByteArray.size()
                    val lenByte = BytesUtils.intToByteArray(len)
                    bos.write(lenByte)
                    Log.d(TAG,"write size:$len ${lenByte[0]} ${lenByte[1]} ${lenByte[2]} ${lenByte[3]}")
                    bos.write(jpegByteArray.toByteArray(),0,len)
                    bos.flush()
                    //i++
                }
            }
        }


        fun intToByteArray(i: Int): ByteArray {
            val result = ByteArray(4)
            result[0] = (i shr 24 and 0xFF).toByte()
            result[1] = (i shr 16 and 0xFF).toByte()
            result[2] = (i shr 8 and 0xFF).toByte()
            result[3] = (i and 0xFF).toByte()
            return result
        }
    }
}