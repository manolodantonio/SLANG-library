package com.manzo.slangtest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.manzo.slang.extensions.raw
import com.manzo.slang.extensions.text
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //////////


        main_tv.text = raw(R.raw.hazards).text()
    }
}


