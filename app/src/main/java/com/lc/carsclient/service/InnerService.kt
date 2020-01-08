package com.lc.carsclient.service

import com.orhanobut.logger.Logger
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.net.ServerSocket
import java.net.Socket

class InnerService private constructor(){

    companion object {

        val instance: InnerService by lazy {
            InnerService()
        }

    }
    var server : Socket? = null

    var writeThread: WriteThread? = null
    var readThread: ReadThread? = null



    fun connect(ip: String ,port: Int): Boolean{
        server = Socket(ip,port)
        server?.keepAlive = true
        if(server != null && server!!.isConnected){
            writeThread = WriteThread(server!!)
            readThread = ReadThread(server!!)
            writeThread?.start()
            readThread?.start()
            return true;
        }
        return false;
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
        private val cmd: IntArray = IntArray(1)

        init {
            fos =  BufferedOutputStream(socket.getOutputStream())
        }

        fun sendDrivingCommand(){
            cmd[0] = 1
            writeLock.notify()

        }

        override fun run() {
            while (!quite){
                if(cmd[0] != -1){
                    Logger.d("send driving command ${cmd[0]}")
                    fos.write(cmd[0])
                    fos.flush()
                }
                else
                    Logger.d("invalid Cmd")
                writeLock.wait()
            }

            fos.close()

        }
    }

    inner class ReadThread(socket: Socket) : Thread(){

        val dis: DataInputStream


        init {
            dis = DataInputStream(socket.getInputStream())
        }

        override fun run() {

           while (!quite){

               val type = dis.readInt()

               when(type){

                   0x10 -> {


                   }

                   0x20 ->{

                   }
               }

           }

        }
    }


}