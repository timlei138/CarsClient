package com.lc.carsclient.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.lc.carsclient.R
import com.orhanobut.logger.Logger
import kotlin.math.sqrt
import kotlin.properties.Delegates

class SteeringWheel @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context,attrs, defStyleAttr, defStyleRes){


    val TAG = this.javaClass.simpleName
    var innerCircle: Int
    var outCirclce: Int
    var innerColor: Int
    var outColor: Int
    var mInnerPaint = Paint()
    var mOutPaint = Paint()
    var centerPoint = PointF()
    init {
        Logger.t(TAG).d("init...")
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SteeringWheelStyle)
        innerCircle = ta.getDimensionPixelSize(R.styleable.SteeringWheelStyle_inner_radius,15)
        outCirclce = ta.getDimensionPixelSize(R.styleable.SteeringWheelStyle_out_radius,28)
        innerColor = ta.getColor(R.styleable.SteeringWheelStyle_inner_bg, resources.getColor(R.color.colorAccent))
        outColor = ta.getColor(R.styleable.SteeringWheelStyle_out_bg,resources.getColor(R.color.colorPrimaryDark))
        Logger.d("inner $innerCircle,out $outCirclce")
        ta.recycle()
        mInnerPaint.color = innerColor
        mOutPaint.color = outColor
        mInnerPaint.isAntiAlias = true
        mOutPaint.isAntiAlias = true;
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Logger.d("onMeasure")
        val width = getSize(widthMeasureSpec)
        val height = getSize(heightMeasureSpec)
        Logger.d("widht:$width,height:$height")
        setMeasuredDimension(width,height)

        centerPoint.x  = outCirclce / 2f;
        centerPoint.y = outCirclce / 2f
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Logger.d("onLayout")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawOval(0f,0f,outCirclce.toFloat(),outCirclce.toFloat(),mOutPaint)
        val out = ((outCirclce / sqrt(2.0) - innerCircle / sqrt(2.0)) / sqrt(2.0)).toFloat()
        Logger.d("out $out")
        canvas?.drawOval(out,out,innerCircle.toFloat()+ out,innerCircle.toFloat() + out,mInnerPaint)
    }


    private fun getSize(spec: Int): Int{
        val mode = MeasureSpec.getMode(spec);
        val size = MeasureSpec.getSize(spec);
         return when(mode){
             MeasureSpec.UNSPECIFIED ->  size
             MeasureSpec.AT_MOST -> size
             MeasureSpec.EXACTLY -> size
             else ->  0
        }

    }

}