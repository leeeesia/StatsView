package ru.netology.statsview

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val view = findViewById<StatsView>(R.id.statsView)
        view.date = listOf(
            0.25F,
            0.25F,
            0.25F,

        )

        val textView = findViewById<TextView>(R.id.label)
        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.animation).apply {

                setAnimationListener(object : Animation.AnimationListener{
                   override fun onAnimationStart(p0: Animation?) {
                       textView.text = "onAnimationStart"
                   }

                   override fun onAnimationEnd(p0: Animation?) {
                       textView.text = "onAnimationEnd"
                   }

                   override fun onAnimationRepeat(p0: Animation?) {
                       textView.text = "onAnimationRepeat"
                   }

               })
            }
        )

    }
}