package demo.atomofiron.pixelizer

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import kotlin.math.pow
import kotlin.math.roundToInt

private const val SIZE = 512
private const val POW = 3
private const val DURATION = 10000L

class PixelizerView : View {

    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_android) as VectorDrawable
    private val animator = ValueAnimator.ofFloat(0f, 1f)
    private var value = 0f
    private var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    private val bitmapCanvas = Canvas(bitmap)
    private val paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        paint.isFilterBitmap = false
        icon.setBounds(0, 0, SIZE, SIZE)
        animator.interpolator = LinearInterpolator()
        animator.duration = DURATION
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap.eraseColor(Color.TRANSPARENT)
        var dx = (bitmap.width - SIZE) / 2f
        var dy = (bitmap.height - SIZE) / 2f
        bitmapCanvas.withTranslation(dx, dy) {
            withScale(value, value, SIZE / 2f, SIZE / 2f) {
                icon.draw(this)
            }
        }
        dx = (measuredWidth - bitmap.width) / 2f
        dy = (measuredHeight - bitmap.height) / 2f
        canvas.withTranslation(dx, dy) {
            withScale(1 / value, 1 / value, measuredWidth / 2f, measuredHeight / 2f) {
                drawBitmap(bitmap, 0f, 0f, paint)
            }
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = right - left
        val height = bottom - top
        when {
            width <= 0 -> return
            height <= 0 -> return
            bitmap.width != width -> Unit
            bitmap.height != height -> Unit
            else -> return
        }
        val old = bitmap
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        bitmapCanvas.setBitmap(bitmap)
        old.recycle()
    }

    fun sync(seekBar: SeekBar) {
        seekBar.setOnSeekBarChangeListener(SeekBarListener())
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            value = animatedValue.pow(POW)
            seekBar.progress = (seekBar.max * animatedValue).roundToInt()
            invalidate()
        }
    }

    private inner class SeekBarListener : SeekBar.OnSeekBarChangeListener {

        override fun onStartTrackingTouch(seekBar: SeekBar) = animator.pause()

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                value = (progress.toFloat() / seekBar.max).pow(POW)
                invalidate()
            }
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) = animator.resume()
    }
}