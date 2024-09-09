package com.jyleon.scrolltext

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.util.TypedValue.applyDimension
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.MutableLiveData
import com.jyleon.scrolltext.helper.NumberCheckHelper

/**
 * @Name TextScrollView
 * @Descript TODO
 * @CreateTime 2024/8/20 16:03
 * @Created by Administrator
 */
class TextScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "TextScrollView"
    }

    private val charListColumns: MutableList<List<Char>> = mutableListOf()

    //字符动画控制
    private val charAnimList: MutableList<NumberAnim> = mutableListOf()

    private val textPaint = Paint()

    private var textWidth = 0f

    private var isAnim = false
    private var isFirst = true;

    private var offset = MutableLiveData(0f)

    private var maxHintNumber = "";

    private var singleNumW = 0f
    private var specialCharW = 0f

    private var defaultText = ""

    private val pointPaint = Paint().apply {
        color = Color.RED // 设置点的颜色
        strokeWidth = 6f // 设置点的大小
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    var gravity: Int = Gravity.END

    var textColor: Int = Color.BLACK
        set(color) {
            if (field != color) {
                field = color
                textPaint.color = color
                invalidate()
            }
        }

    var typeface: Typeface?
        set(value) {
            textPaint.typeface = when (textStyle) {
                Typeface.BOLD_ITALIC -> Typeface.create(value, Typeface.BOLD_ITALIC)
                Typeface.BOLD -> Typeface.create(value, Typeface.BOLD)
                Typeface.ITALIC -> Typeface.create(value, Typeface.ITALIC)
                else -> value
            }
            onTextPaintMeasurementChanged()
        }
        get() = textPaint.typeface


    private var textStyle = Typeface.NORMAL


    init {
        var textSize = applyDimension(COMPLEX_UNIT_SP, 12f, context.resources.displayMetrics)

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

        var shadowColor = 0
        var shadowDx = 0f
        var shadowDy = 0f
        var shadowRadius = 0f
        var text = ""

        //设置一个项目中的字体
        val iconfont = Typeface.createFromAsset(context.assets, "font/moneyfont.ttf")
        typeface = iconfont

        fun applyTypedArray(arr: TypedArray) {
            gravity = arr.getInt(R.styleable.TextScrollView_android_gravity, gravity)
            shadowColor = arr.getColor(R.styleable.TextScrollView_android_shadowColor, shadowColor)
            shadowDx = arr.getFloat(R.styleable.TextScrollView_android_shadowDx, shadowDx)
            shadowDy = arr.getFloat(R.styleable.TextScrollView_android_shadowDy, shadowDy)
            shadowRadius =
                arr.getFloat(R.styleable.TextScrollView_android_shadowRadius, shadowRadius)
            text = arr.getString(R.styleable.TextScrollView_android_text) ?: ""
            textColor = arr.getColor(R.styleable.TextScrollView_android_textColor, textColor)
            textSize = arr.getDimension(R.styleable.TextScrollView_android_textSize, textSize)
            textStyle = arr.getInt(R.styleable.TextScrollView_android_textStyle, textStyle)

//            setPadding(
//                arr.getDimensionPixelSize(R.styleable.CustomView_android_paddingLeft, paddingLeft),
//                arr.getDimensionPixelSize(R.styleable.CustomView_android_paddingTop, paddingTop),
//                arr.getDimensionPixelSize(R.styleable.CustomView_android_paddingRight, paddingRight),
//                arr.getDimensionPixelSize(R.styleable.CustomView_android_paddingBottom, paddingBottom)
//            )

        }



        val arr = context.obtainStyledAttributes(
            attrs, R.styleable.TextScrollView,
            defStyleAttr, defStyleRes
        )

        val textAppearanceResId = arr.getResourceId(
            R.styleable.TextScrollView_android_textAppearance, -1
        )

        if (textAppearanceResId != -1) {
            val textAppearanceArr = context.obtainStyledAttributes(
                textAppearanceResId, R.styleable.TextScrollView
            )
            applyTypedArray(textAppearanceArr)
            textAppearanceArr.recycle()
        }

        applyTypedArray(arr)


        textPaint.color = textColor
        textPaint.isAntiAlias = true
        if (shadowColor != 0) {
            textPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
        }
        if (textStyle != 0) {
            typeface = textPaint.typeface
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)


        arr.recycle()

        initListener()
//        measureText()
        setDefaultText(text)
    }

    private fun setDefaultText(text: String) {
        maxHintNumber = text
        defaultText = text
        requestLayout()
        invalidate()
    }

    private fun measureText() {
        // 2. 使用 getTextBounds
        val specialCharacter = "."
        val singleNum = "1"

        val bounds = Rect()
        textPaint.getTextBounds(specialCharacter, 0, specialCharacter.length, bounds)
        specialCharW = bounds.width().toFloat()


        val bounds2 = Rect()
        textPaint.getTextBounds(singleNum, 0, singleNum.length, bounds2)
        singleNumW = bounds2.width().toFloat()

    }

    private fun initListener() {
        offset.observeForever { value ->
            if (value == 0f) {
                return@observeForever
            }
            if (isFirst) {
                isFirst = false

                Log.i(TAG,"offset change isFirst:${isFirst} offset:${offset.value}")

//                startAnim()
                startRowAnim()
            }

        }
    }


    fun setTextSize(textSize: Float) = setTextSize(COMPLEX_UNIT_SP, textSize)

    fun getTextSize() = textPaint.textSize

    fun setTextSize(unit: Int, size: Float) {
        val r: Resources = context?.resources ?: Resources.getSystem()
        textPaint.textSize = applyDimension(unit, size, r.displayMetrics)
        onTextPaintMeasurementChanged()
    }

    private fun onTextPaintMeasurementChanged() {
//        invalidate()
    }

    /*
    * 构建一个翻转动画，兼容小数点的翻转(执行的时候记得初始化)
    * */
    fun setNum(startNum: String, endPosition: String) {
        if (isAnim){
            Log.i(TAG,"is still in anim")
            return
        }
        maxHintNumber = NumberCheckHelper.getLargerString(startNum, endPosition)

        charListColumns.clear()
        charAnimList.clear()
        offset.value = 0f
        charListColumns.addAll(NumberCheckHelper.getNumIntervalList(startNum, endPosition))

        isFirst = true

        requestLayout()
//        invalidate()
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i(TAG,"onLayout start height:${height.toFloat()}")
        offset.value = height.toFloat()
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
//        viewBounds.set(
//            paddingLeft, paddingTop, width - paddingRight,
//            height - paddingBottom
//        )
//        viewWidthLargerThanTextWidth = viewBounds.width() > computeDesiredWidth()
//        viewHeightLargerThanTextHeight = viewBounds.height() > computeDesiredHeight()
    }


    private fun startRowAnim() {
        if (charAnimList.isEmpty()) {
            for (i in 0 until charListColumns.size) {
                charAnimList.add(NumberAnim(0, 0f, anim =  createRowAnim2(i)))
            }
        }
    }

    private fun createRowAnim2(i: Int): ValueAnimator {
        val currentOffset = offset.value!!*(charListColumns[i].size - 1)
        val delayTime = 150L

        val animator = ValueAnimator.ofFloat(0f, currentOffset)
        animator.setDuration(2500L-delayTime*(charListColumns.size-i))
        animator.startDelay = (charListColumns.size-i)*delayTime
        animator.interpolator = DecelerateInterpolator(4f)
        animator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
            charAnimList[i].offset = animation.animatedValue as Float

            invalidate()
        })

        animator.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {
                charAnimList[i].status = true
                isAnim = true
            }

            override fun onAnimationEnd(p0: Animator) {
                if (i==charAnimList.size-1){
                    isAnim = false
                }
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        return animator
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (charListColumns.isEmpty()) {

            drawDefaultText(canvas)
            //绘制一个默认的数字
            return
        }

        for (columnIndex in charListColumns.withIndex()) {
            val index = columnIndex.index
            val column = columnIndex.value

            if (index == 0) {
                textWidth = (width - textPaint.measureText(maxHintNumber)) / 2
            }

            val startX = textWidth
            textWidth += (textPaint.measureText(column[0].toString()))

            val startY = (height - textPaint.descent() - textPaint.ascent()) / 2

            pointPaint.color = Color.RED


            if (column[0] == column[1]){
                drawAnimRow(
                    canvas,
                    index,
                    charAnimList[index].position,
                    startX,
                    startY
                )
            }else{
                for (holder in charListColumns[index].withIndex()){
                    val rowIndex = holder.index
                    var rowHolder = holder.value
                    val rowOffset = charAnimList[index].offset
                    //做一个兼容性的调整，第一位是零有点难看
//                    if (rowIndex==0&&rowHolder=='0'){
//                        rowHolder = ' '
//                    }
                    canvas.drawText(rowHolder.toString(), startX, startY - rowOffset+offset.value!!*rowIndex, textPaint)

                }
            }

            if (!charAnimList[index].status) {
                charAnimList[index].status = true
                charAnimList[index].anim.start()
            }

        }

    }

    private fun drawDefaultText(canvas: Canvas) {
        val startX  = (width - textPaint.measureText(defaultText)) / 2
        val startY = (height - textPaint.descent() - textPaint.ascent()) / 2

        canvas.drawText(defaultText, startX, startY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val textWidth = textPaint.measureText(maxHintNumber)
        val textHeight = textPaint.descent() - textPaint.ascent()

        // 默认加一些Padding
        val width = resolveSize(textWidth.toInt(), widthMeasureSpec)+ paddingLeft + paddingRight
        val height = resolveSize(textHeight.toInt(), heightMeasureSpec)+ paddingTop + paddingBottom

        setMeasuredDimension(width, height)
    }

    fun drawAnimRow(canvas: Canvas, rowPosition: Int, position: Int, x: Float, y: Float) {
        if (position>=charListColumns[rowPosition].size){
            canvas.drawText(charListColumns[rowPosition][charListColumns[rowPosition].size-1].toString(), x, y, textPaint)
        }else{
            canvas.drawText(charListColumns[rowPosition][position].toString(), x, y, textPaint)
        }
    }


    fun drawAnimRow2(canvas: Canvas, rowPosition: Int, position: Int, x: Float, y: Float) {
        if (position>=charListColumns[rowPosition].size){
            canvas.drawText(charListColumns[rowPosition][charListColumns[rowPosition].size-1].toString(), x, y, textPaint)
        }else{
            canvas.drawText(charListColumns[rowPosition][position].toString(), x, y, textPaint)
        }
    }


}

private data class NumberAnim(
    var position: Int = 0,
    var offset: Float = 0f,
    var animTime: Int = 0,
    val anim: ValueAnimator,
    var status: Boolean = false,
) {

}