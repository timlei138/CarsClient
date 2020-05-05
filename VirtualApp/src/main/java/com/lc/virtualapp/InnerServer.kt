package com.lc.virtualapp

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.nfc.Tag
import android.util.Log
import android.util.Size
import com.lc.command.*
import com.lc.jpeg.JpegTurbo
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

    private val frameQueue: LinkedBlockingQueue<ByteArray>
    private var client: Client? = null;
    var size: Size? = null
    private var clientHost = ""

    private var frameSocket: DatagramSocket?  = null

    init {
        frameQueue = LinkedBlockingQueue(5)
        JpegTurbo.turbo.frameCb = {
            frameQueue.offer(it)
        }
    }

    fun seedFrame(frame: ByteArray){
        JpegTurbo.turbo.yuvJpeg(frame,640,480)
    }


    inner class ResponseFindDeviceThread : Thread(){
        private var hasFind = false
        override fun run() {
            super.run()
            val socket  = DatagramSocket(DEVICE_FOUND_UDP_PORT)
            while (!hasFind){
                val buf = ByteArray(32)
                val packet = DatagramPacket(buf,buf.size)
                Log.d(TAG,"wait for client found...")
                socket.receive(packet)
                packet.apply {
                    if(String(buf,0,length) == COMMAND_FLAG){
                        client = Client(packet.address.hostName,packet.address.hostAddress,packet.port)
                        Log.d(TAG,"receiver client info -> $client")
                        val msg = COMMAND_FLAG.toByteArray()
                        val receivePacket = DatagramPacket(msg,msg.size,packet.address,packet.port)
                        socket.send(receivePacket)
                        hasFind = true
                        clientHost = packet.address.hostAddress
                        sleep(5000)
                        FeedFrameThread().start()
                    }
                }
            }
            socket.close()
        }
    }


    inner class FeedFrameThread : Thread() {
        override fun run() {
            super.run()
            Log.d(TAG,"start feedFrameThread...")
            val clientAddress = InetSocketAddress(clientHost, CLIENT_FRAME_UDP_PORT)
            frameSocket = DatagramSocket(LOCAL_FRAME_UDP_PORT)
            //frameSocket?.sendBufferSize = 640 * 480 * 3 /2 ;
            while (frameSocket != null) {
                if(frameSocket?.isConnected() == false){
                    frameSocket?.connect(clientAddress)
                    sleep(2000)
                    continue
                }
                val frame = frameQueue.take()
                Log.d(TAG,"send frame size ${frame.size}")
                frameSocket?.send(DatagramPacket(frame, frame.size))
            }
        }
    }
}