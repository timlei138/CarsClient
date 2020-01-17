package com.lc.carsclient

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.lc.carsclient.service.InnerService
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import org.jetbrains.anko.progressDialog as progressDialog1

class MainActivity : AppCompatActivity() {



    val service = InnerService.instance


    private var bitmap: Bitmap? = null

    private val paint = Paint()

    private var findProgress: ProgressDialog? = null;

    private val updateHandler: Handler = Handler()

    val mFrameQueue: FrameQueue = FrameQueue()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startCar.setOnClickListener{
            indeterminateProgressDialog("正在查找设备...","发现设备") {
                setCanceledOnTouchOutside(false)
                findProgress = this
                service.SearchCarDevice {
                    if(it){
                        findProgress?.dismiss()
                    }
                }.start()
                updateHandler.postDelayed({
                    findProgress?.dismiss()
                    toast("未找到小车，请检查网络再试")
                },10000)
            }.show()
        }

        openCamera.setOnClickListener {


        }

        preView.holder.addCallback(callback)

        paint.color = Color.BLUE
        paint.isAntiAlias = true
        paint.strokeWidth = 2f



    }


    val callback = object : SurfaceHolder.Callback{
        override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
            Logger.d("surfaceChanged ")
        }

        override fun surfaceDestroyed(p0: SurfaceHolder?) {
            Logger.d("surfaceDestroyed ")
        }

        override fun surfaceCreated(p0: SurfaceHolder?) {
            Logger.d("surfaceCreated ")
            mFrameQueue.start()
        }

    }


    override fun onResume() {
        super.onResume()
        Logger.d("onResume")
        service.preViewCallbakc = {
            mFrameQueue.seedFrame(it)
        }
        service.msgCallback = {
            Logger.d("msg from server => $it")
        }

        service.quite = false

    }



    inner class FrameQueue : Thread(){

        val mQueue: LinkedBlockingQueue<ByteArray>

        init {
            mQueue = LinkedBlockingQueue(20)

        }

        fun seedFrame(frame: ByteArray){
            mQueue.offer(frame)
        }

        override fun run() {
            super.run()
            while (true){
                val frame = mQueue.take()
                Logger.d("take frame $frame")
                if(frame != null){
                    bitmap = BitmapFactory.decodeByteArray(frame,0,frame.size)
                }
                if(bitmap != null){
                    val canvas = preView.holder.lockCanvas()
                    Logger.d("canvas")
                    canvas.drawBitmap(bitmap!!,0f,0f,paint)

                    preView.holder.unlockCanvasAndPost(canvas)

                }

            }
        }
    }




}
