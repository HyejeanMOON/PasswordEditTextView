package com.example.passwordedittextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordEditTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var circleRadius = context.dpToPx(10F)
    private var circleColor = Color.BLACK

    private var barColor = Color.LTGRAY
    private var barHeight = context.dpToPx(1F)
    private var barWidth = 2 * circleRadius

    private lateinit var circlePaint: Paint
    private lateinit var barPaint: Paint

    private var currentHeight = 0
    private var currentWidth = 0

    private var maxCount = 4
    private var currentInputCount = 0

    private var layoutSpace = 200F

    private var listener: OnPasswordCompleteListener? = null

    init {
        initPaint()
        this.isCursorVisible = false
        this.filters = listOf(InputFilter.LengthFilter(maxCount)).toTypedArray()
        this.inputType = InputType.TYPE_CLASS_NUMBER
        this.textSize = 0F
        this.setTextIsSelectable(false)

        this.setOnLongClickListener { true }
    }

    private fun initPaint() {
        circlePaint = getPaint(Paint.Style.FILL, circleColor, circleRadius)
        barPaint = getPaint(Paint.Style.STROKE, barColor, barHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawLayout(canvas)
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)

        // let the cursor always on the last position
        if (selStart == selEnd) {
            setSelection(text?.length ?: 0)
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        currentInputCount = text?.length ?: 0

        if (text?.length == maxCount && listener != null) {
            listener?.onComplete(text.toString())
        } else if (text?.length != maxCount && listener != null) {
            listener?.onNotComplete()
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.currentHeight = h
        this.currentWidth = w
    }

    private fun getPaint(style: Paint.Style, color: Int, strokeWidth: Float): Paint {
        val paint = Paint()
        paint.color = color
        paint.style = style
        paint.strokeWidth = context.dpToPx(strokeWidth)
        paint.isAntiAlias = true
        return paint
    }

    private fun drawLayout(canvas: Canvas?) {
        var leftPositionX = layoutSpace
        val itemSpace = (currentWidth - (2 * layoutSpace) - (4 * barWidth)) / 3

        for (i in 0 until currentInputCount) {
            drawCircle(leftPositionX, canvas)
            leftPositionX += itemSpace + 2 * circleRadius
        }

        for (i in currentInputCount until maxCount) {
            drawBar(leftPositionX.toInt(), canvas)
            leftPositionX += itemSpace + barWidth
        }
    }

    private fun drawBar(leftPositionX: Int, canvas: Canvas?) {
        canvas?.drawRect(
            Rect(
                leftPositionX,
                (currentHeight / 2 - barHeight / 2).toInt(),
                leftPositionX + barWidth.toInt(),
                (currentHeight / 2 + barHeight / 2).toInt()
            ), paint
        )
    }

    private fun drawCircle(leftPositionX: Float, canvas: Canvas?) {
        canvas?.drawCircle(leftPositionX + circleRadius, height / 2F, circleRadius, paint)
    }

    fun getPasswordString(): String {
        return text.toString().trim()
    }

    fun setOnPasswordCompleteListener(listener: OnPasswordCompleteListener) {
        this.listener = listener
    }

    interface OnPasswordCompleteListener {
        fun onComplete(password: String)
        fun onNotComplete()
    }

    fun Context.dpToPx(dp: Float) = resources.displayMetrics.density * dp
}