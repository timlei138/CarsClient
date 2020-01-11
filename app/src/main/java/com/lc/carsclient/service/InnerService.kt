package com.lc.carsclient.service

import android.graphics.ImageFormat
import android.graphics.YuvImage
import com.orhanobut.logger.Logger
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket

class InnerService private constructor() : Thread(){

    companion object {

        val instance: InnerService by lazy {
            InnerService()
        }

    }


    var server : Socket? = null

    var writeThread: WriteThread? = null
    var readThread: ReadThread? = null


    var host: String? = null
    var port: Int = 9999



    var preViewCallbakc: ((data: ByteArray) -> Unit)? = null
    var msgCallback: ((msg: String) -> Unit)? = null


    override fun run() {
        super.run()
        Logger.d("connect server")
        server = Socket(host,port)
        server?.keepAlive = true
        if(server != null && server!!.isConnected){
            writeThread = WriteThread(server!!)
            readThread = ReadThread(server!!)
            writeThread?.start()
            readThread?.start()
        }
    }

    //发送行车控制命令
    fun sendDrivingCommand(){
        writeThread?.sendDrivingCommand()
    }

    val isConnected
        get() = server?.isConnected ?: false


    var quite: Boolean = false
        set(value) {
            if(field != value){
                field = value
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
                val byteArray = ByteArray(1024 * 1024)
                dis.read(byteArray)
                Logger.d("read bytes:${byteArray.size}")
                preViewCallbakc?.invoke(byteArray)
            }

        }
    }


}