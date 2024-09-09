package com.JYLeon.ScrollTextTest

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ComponentActivity
import com.jyleon.scrolltext.TextScrollView


class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn_cilck)
        btn.setOnClickListener {
            val rollingView = findViewById<TextScrollView>(R.id.rollText)
            rollingView.setNum("2.36","7.62")
        }

    }
}
