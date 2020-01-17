package com.lc.virtualapp

import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File

internal class RawImageSaver(

    val byteWrite: ByteArrayOutputStream

) : Runnable{


    override fun run() {
        val file = File(Environment.getExternalStorageDirectory(),System.currentTimeMillis().toString().plus(".jpg"))
        file.createNewFile()
        file.writeBytes(byteWrite.toByteArray())

    }

}