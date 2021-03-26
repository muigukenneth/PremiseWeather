package com.premise.premiseweather.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.razir.progressbutton.hideDrawable
import com.github.razir.progressbutton.showDrawable
import com.github.razir.progressbutton.showProgress
import com.premise.premiseweather.R

object ViewUtils {
    fun resetSaved(context: Context, button: Button) {
        val animatedDrawable = ContextCompat.getDrawable(context, R.drawable.animated_check)!!
        //Defined bounds are required for your drawable
        val drawableSize = context.resources.getDimensionPixelSize(R.dimen.doneSize)
        animatedDrawable.setBounds(0, 0, drawableSize, drawableSize)
        button.showDrawable(animatedDrawable) {
            buttonTextRes = R.string.done
        }
        Handler().postDelayed({
            button.hideDrawable(R.string.go)
        }, 2000)
    }
    fun showSend(button: Button) {
        button.showProgress {
            buttonTextRes = R.string.forecasting
            progressColor = Color.WHITE
        }
    }
    fun showView(view: View) {
        view.visibility = View.VISIBLE
    }

    fun hideView(view: View) {
        view.visibility = View.GONE
    }
    fun requestAvatarTransitionOptions(): DrawableTransitionOptions {
        val options = DrawableTransitionOptions()
        return options.crossFade()
    }
    @SuppressLint("CheckResult")
    fun requestOptions(width: Int, height: Int): RequestOptions {
        val options = RequestOptions()
        options.placeholder(android.R.color.transparent)
            .priority(Priority.NORMAL)
            .error(android.R.color.transparent)
            .override(width, height)
            .fitCenter()
        return options
    }
}