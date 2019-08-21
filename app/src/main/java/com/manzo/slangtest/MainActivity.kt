package com.manzo.slangtest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.manzo.slang.extensions.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Intent(this, MainActivity::class.java).run {
            resolveActivity(packageManager)?.run {
                baseContext.startActivity<MainActivity>()
            }
        }
    }
}


