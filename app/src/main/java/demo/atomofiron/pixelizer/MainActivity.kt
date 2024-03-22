package demo.atomofiron.pixelizer

import android.app.Activity
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.view.WindowCompat

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        val seekBar = findViewById<SeekBar>(R.id.seekbar)
        val pixelizerView = findViewById<PixelizerView>(R.id.android_view)
        pixelizerView.sync(seekBar)
    }
}
