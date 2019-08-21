package com.manzo.slangtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.manzo.slang.extensions.fromJsonToList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Gson().fromJsonToList("", Test::class.java)
    }
}

data class Test(val test: String)


