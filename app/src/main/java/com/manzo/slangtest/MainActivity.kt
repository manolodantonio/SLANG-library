package com.manzo.slangtest

import android.content.Intent
import android.os.Bundle
import com.manzo.slang.navigation.BaseActivity
class MainActivity : BaseActivity() {

    override fun getFragmentContainer() = R.id.test_fragment_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFragment(testFragment())

        //////////
    }


    override val baseBroadcastIntentFilter = "testFilter"

    override fun onBaseBroadcastReceive(intent: Intent?) {
        super.onBaseBroadcastReceive(intent)
        sendBaseBroadcast(Intent().apply { putExtra("key", "fromActivity") })
    }
}


