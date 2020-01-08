package com.lc.carsclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import com.lc.carsclient.service.InnerService
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {



    val  service = InnerService.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startCar.setOnClickListener{
            alert {
                title("请输入车辆信息")
                val view = layoutInflater.inflate(R.layout.layout_host,null)
                customView(view)
                val hostEd = view.findViewById<EditText>(R.id.hostEd)
                val portEd = view.findViewById<EditText>(R.id.portEd)
                positiveButton {
                    val host = hostEd?.text.toString()
                    val port = portEd.text.toString()
                    if (host.isNullOrEmpty() || port.isNullOrEmpty()){
                        toast("IP地址或者端口不能为空！")
                        return@positiveButton
                    }
                    service.connect(host,port.toInt())
                }

                negativeButton {
                    toast("缺少必要的参数无法连接车辆")
                    dismiss()
                }


            }.show()
        }

    }


    override fun onResume() {
        super.onResume()
        Logger.d("onResume")

    }




}
