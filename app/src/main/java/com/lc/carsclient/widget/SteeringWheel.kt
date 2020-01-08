package com.lc.carsclient.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lc.carsclient.R
import com.orhanobut.logger.Logger
import kotlin.math.sqrt
import kotlin.properties.Delegates

class SteeringWheel @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context,attrs, defStyleAttr, defStyleRes){

    var innerCircle: Int
    var outCirclce: Int
    var innerColor: Int
    var outColor: Int
    var mInnerPaint = Paint()
    var mOutPaint = Paint()
    val innerRadius: Float
    var outRectF  = RectF();
    var innerRectF = RectF()
    var restore = true

    var moveX = 0f;
    var moveY = 0f;


    init {
        Logger.d("init...")
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SteeringWheelStyle)
        innerCircle = ta.getDimensionPixelSize(R.styleable.SteeringWheelStyle_inner_radius,15)
        innerRadius = innerCircle / 2f
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
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val loc = IntArray(2)
        getLocationOnScreen(loc)
        outRectF.set(loc[0].toFloat(),loc[1].toFloat(),loc[0]+outCirclce.toFloat(),loc[1] + outCirclce.toFloat())
        Logger.d("onLayout $outRectF")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawOval(0f,0f,outCirclce.toFloat(),outCirclce.toFloat(),mOutPaint)

    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        //val out = ((outCirclce / sqrt(2.0) - innerCircle / sqrt(2.0)) / sqrt(2.0)).toFloat()
        var x = 0f;
        var y = 0f;
        if(restore){
            x = (outCirclce - innerCircle) / 2f
            y = (outCirclce - innerCircle) / 2f
        }else{
            x = moveX
            y = moveY
        }
        Logger.d("onDrawForeground x:$x,y:$y")
        canvas?.drawOval(x,y,innerCircle.toFloat()+ x,innerCircle.toFloat() + y,mInnerPaint)
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


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                val x = event.rawX
                val y = event.rawY
                restore = false
                innerRectF.set(x - innerRadius,y -innerRadius,x+innerRadius,y+innerRadius)
                Logger.d("Down x: $x,y $y innerRectF $innerRectF")
            }
            MotionEvent.ACTION_MOVE ->{
                if(!restore){
                    val x = event.rawX
                    val y = event.rawY
                    innerRectF.set(x - innerRadius,y -innerRadius,x+innerRadius,y+innerRadius)
                    if(!outRectF.contains(innerRectF)){
                        if(innerRectF.left < outRectF.left){
                            innerRectF.left = outRectF.left
                        }
                        if(innerRectF.top < outRectF.top){
                            innerRectF.top = outRectF.top
                        }
                        if(innerRectF.right >= outRectF.right){
                            innerRectF.left = outRectF.right - innerCircle
                        }
                        if(innerRectF.bottom >= outRectF.bottom){
                            innerRectF.top = outRectF.bottom - innerCircle
                        }
                    }

                    moveX = innerRectF.left - outRectF.left;
                    moveY = innerRectF.top  - outRectF.top
                    Logger.d("move $innerRectF moveX:$moveX,moveY:$moveY")
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL ->{
                Logger.d("Up")
                restore = true;
                moveX = 0f
                moveY = 0f
                invalidate()
            }
        }
        return true
    }

}