package com.premise.premiseweather.activities

import android.animation.LayoutTransition
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.premise.premiseweather.R
import com.premise.premiseweather.fragments.WeatherFragment
import kotlinx.android.synthetic.main.view_main_toolbar.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        initAnimation()
        /**
         * Launch the fragment
         */
        supportFragmentManager.beginTransaction()
            .add(R.id.relativeLayoutContainer, WeatherFragment.newInstance())
            .commit()
    }

    private fun initAnimation() {
        (findViewById<View>(R.id.relativeLayoutContainer) as ViewGroup).layoutTransition
            .enableTransitionType(LayoutTransition.CHANGING)
    }
}