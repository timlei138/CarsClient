package com.lc.carsclient.service

import android.util.Log
import com.lc.command.*
import com.orhanobut.logger.Logger

import java.io.BufferedOutputStream

import java.io.InputStream
import java.net.*

class InnerService private constructor(){


    val TAG = "Client-InnerService";

    companion object {

        val instance: InnerService by lazy {
            InnerService()
        }

    }

    var writeThread: WriteThread? = null
    var readThread: ReadThread? = null

    private var host: String? = null

    private var frameSocket: DatagramSocket? = null

    var preViewCallback: ((data: ByteArray) -> Unit)? = null
    var msgCallback: ((msg: String) -> Unit)? = null


    //发送行车控制命令
    fun sendDrivingCommand(){
        writeThread?.sendDrivingCommand()
    }


    var isFindDevice: Boolean = false
        get() = host?.isNotEmpty() ?: false

    var quite: Boolean = false
        set(value) {
            if(field != value){
                field = value
            }
        }


    inner class SearchCarDevice(val cb: (Boolean) -> Unit) : Thread(){

        override fun run() {
            Logger.d("start find device...")
            sleep(3000)
            val socket = DatagramSocket();
            val dataByte = COMMAND_FLAG.toByteArray()
            val packet = DatagramPacket(dataByte,dataByte.size,
                InetAddress.getByName(BROADCAST_HOST), DEVICE_FOUND_UDP_PORT)
            socket.broadcast = true
            socket.send(packet)
            Logger.d("isFindDevice $isFindDevice")
            while (!isFindDevice){
                val receiveBuf = ByteArray(32)
                val receivePacket = DatagramPacket(receiveBuf,receiveBuf.size)
                socket.receive(receivePacket)
                Logger.d("receiver msg${String(receiveBuf)}")
                receivePacket.apply {
                    if(String(receiveBuf,0,length) == COMMAND_FLAG){
                        host = address.hostAddress
                        Logger.d("find device host-> $host")
                        isFindDevice = true
                        cb.invoke(true)
                    }
                }
            }
        }


    }


    inner class WriteThread(socket: Socket) : Thread(){
        val fos: BufferedOutputStream
        private val writeLock = Object()
        private val cmd: IntArray = IntArray(1){-1}

        init {
            fos =  BufferedOutputStream(socket.getOutputStream())
        }

        fun sendDrivingCommand(){
            cmd[0] = 1
            writeLock.notify()

        }

        override fun run() {
            while (!quite){
                synchronized(writeLock){
                    if(cmd[0] != -1){
                        Logger.d("send driving command ${cmd[0]}")
                        fos.write(cmd[0])
                        fos.flush()
                    }
                    else
                        Logger.d("invalid Cmd")
                    writeLock.wait()
                }

            }

            fos.close()

        }
    }

    inner class ReadThread(val socket: Socket) : Thread(){

        var dis: InputStream
        init {
            dis = socket.getInputStream()
        }

        override fun run() {
            Logger.d("read thread run... $quite")
            while (!quite){
                val type = dis.read()
                Logger.d("type -> $type")
                if(type == TYPE_VIDEO){
                    val lenArray = ByteArray(4)
                    dis.read(lenArray)
                    Logger.d("${lenArray[0]} ${lenArray[1]} ${lenArray[2]} ${lenArray[3]}")
                    val len = BytesUtils.byteArrayToInt(lenArray)
                    Logger.d("data length $len")
                    val data = ByteArray(len)
                    dis.read(data,0,len)
                    preViewCallback?.invoke(data)
                }
            }

        }

    }

    inner class CameraFrameThread : Thread(){
        override fun run() {
            super.run()
            var open = true;
            Log.d(TAG,"CameraFrameThread start ...")
            if(host?.isNotEmpty() == true){
                frameSocket = DatagramSocket(CLIENT_FRAME_UDP_PORT)
                val frameSize = 640 * 480 * 3 / 2
                while (open){
                    val data =  DatagramPacket(ByteArray(frameSize),frameSize)
                    frameSocket?.receive(data)
                    Log.d(TAG,"receiver data ${data.address.hostAddress} ${data.data.size}")
                }

            }

        }
    }


}