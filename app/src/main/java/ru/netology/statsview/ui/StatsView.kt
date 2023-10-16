package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes,
) {
    private var textSize = AndroidUtils.dp(context, 5).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()
    private var valueAnimator: ValueAnimator? = null
    private var progress = 0F
    private var backgroundColor = 0

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()
            val resId = getResourceId(R.styleable.StatsView_colors, 0)
            colors = resources.getIntArray(resId).toList()
            backgroundColor = getColor(R.styleable.StatsView_backgroundColor, 0)

        }
    }


    var date: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()


    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth.toFloat()
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND

    }


    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = backgroundColor
        canvas.drawCircle(center.x, center.y, radius, paint)

        canvas.drawText(
            "%.2f%%".format(date.sum() * progress * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )

        if (date.isEmpty()) {
            canvas.drawText(
                "%.2f%%".format(0),
                center.x,
                center.y + textPaint.textSize / 4,
                textPaint,
            )
            return
        }

        val progressAngle = progress * 360F
        var startAngle = -90F
        val max = date.sum() * 360F

        if (progressAngle > max) {
            for ((index, datum) in date.withIndex()) {
                val angle = datum * 360F
                paint.color = colors.getOrElse(index) { generateRandomColor() }
                canvas.drawArc(oval, startAngle , angle , false, paint)
                startAngle += angle
            }
            return

        }
        var filled = 0F
        for ((index, datum) in date.withIndex()) {
            val angle = datum * 360F
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startAngle, progressAngle - filled, false, paint)
            startAngle += angle
            filled += angle
            if (filled > progressAngle) return
        }
        paint.color = colors.getOrElse(0) { generateRandomColor() }
        canvas.drawArc(oval, -90F, 10F, false, paint)


    }

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 2_000
            interpolator = LinearInterpolator()
        }.also {
            it.start()
        }
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}