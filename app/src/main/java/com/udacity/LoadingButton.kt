package com.udacity

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val START_ANGLE = 30f
        private const val SWEEP_SIZE = 360f
        private const val LOADING_CIRCLE_SIZE = 70
        private const val TEXT_SIZE = 45.0f
        const val ANIMATION_DURATION = 3000L
    }

    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()
    private var loadingCircleAnimator = ValueAnimator()

    private var buttonClickedText = 0
    private var buttonLoadingText = 0
    private var buttonCompleteText = 0
    private var buttonTextColor = 0
    private var buttonBackgroundColor = 0
    private var loadingProgress: Float = 0f

    private var loadingArcFrame = RectF()
    private var currentSweepAngle = 0

    private val loadingArc = LoadingCircle(start = 0f, sweep = SWEEP_SIZE, color = Color.YELLOW)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                animateColorChange()
                animateLoadingArc()
                invalidate()

            }
            ButtonState.Completed -> {
                completeAnimations()
                invalidate()
            }
            else -> invalidate()
        }

    }

    fun setNewButtonState(newButtonState: ButtonState) {
        buttonState = newButtonState
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = TEXT_SIZE
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val loadingRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.colorPrimaryDark)
    }

    private val loadingArcPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        buttonState = ButtonState.Clicked

        context.withStyledAttributes(set = attrs, attrs = R.styleable.LoadingButton) {
            buttonClickedText = getInt(R.styleable.LoadingButton_buttonColor1, 0)
            buttonLoadingText = getInt(R.styleable.LoadingButton_buttonColor2, 0)
            buttonCompleteText = getInt(R.styleable.LoadingButton_buttonColor3, 0)
            buttonTextColor = getInt(R.styleable.LoadingButton_buttonTextColor, context.getColor(R.color.white))
            buttonBackgroundColor = getInt(R.styleable.LoadingButton_buttonBackgroundColor, context.getColor(R.color.colorPrimary))
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setLoadingArcFrame()
    }

    private fun setLoadingArcFrame() {
        loadingArcFrame.set((widthSize - LOADING_CIRCLE_SIZE * 2).toFloat(), (heightSize - LOADING_CIRCLE_SIZE / 2f) - LOADING_CIRCLE_SIZE.toFloat(),
            (widthSize - LOADING_CIRCLE_SIZE).toFloat(), (heightSize - (LOADING_CIRCLE_SIZE / 1.7f)))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)

        if (buttonState == ButtonState.Loading) {
            drawLoadingProgress(canvas)
            drawLoadingCircle(canvas)
        }

        drawText(canvas)
    }
    private fun drawLoadingCircle(canvas: Canvas) {
        if (currentSweepAngle > loadingArc.start + loadingArc.sweep) {
            loadingArcPaint.color = loadingArc.color
            canvas.drawArc(loadingArcFrame,
                START_ANGLE + loadingArc.start,
                loadingArc.sweep,
                true,
                loadingArcPaint)
        } else {
            if (currentSweepAngle > loadingArc.start) {
                loadingArcPaint.color = loadingArc.color
                canvas.drawArc(loadingArcFrame,
                    START_ANGLE,
                    currentSweepAngle - loadingArc.start,
                    true,
                    loadingArcPaint)
            }
        }
    }

    private fun drawLoadingProgress(canvas: Canvas) {
        canvas.drawRect(paddingLeft.toFloat(), heightSize.toFloat(), widthSize.toFloat() * loadingProgress / 100,
            0f, loadingRectPaint)
    }

    private fun drawText(canvas: Canvas) {
        val buttonText = when (buttonState) {
            ButtonState.Clicked -> context.getString(R.string.download)
            ButtonState.Loading -> context.getString(R.string.button_loading)
            ButtonState.Completed -> context.getString(R.string.download)
        }

        textPaint.color = buttonTextColor
        canvas.drawText(buttonText, widthSize / 2f, heightSize / 1.7f, textPaint)
    }

    private fun drawBackground(canvas: Canvas) {
        backgroundPaint.color = buttonBackgroundColor

        canvas.drawRect(0f, heightSize.toFloat(), widthSize.toFloat(),
            0f, backgroundPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun completeAnimations() {
        loadingCircleAnimator.end()
        valueAnimator.end()
    }

    private fun animateColorChange() {
        valueAnimator.cancel()
        valueAnimator = ValueAnimator.ofInt(0, 100).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = INFINITE
            duration = ANIMATION_DURATION
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                loadingProgress = (valueAnimator.animatedValue as Int).toFloat()
                invalidate()
            }
        }
        valueAnimator.start()
    }

    private fun animateLoadingArc() {
        loadingCircleAnimator.cancel()
        loadingCircleAnimator = ValueAnimator.ofInt(0, SWEEP_SIZE.toInt()).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = INFINITE
            duration = ANIMATION_DURATION
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        loadingCircleAnimator.start()
    }
}
private class LoadingCircle(val start: Float, val sweep: Float, val color: Int)
