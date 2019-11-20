package com.manzo.slangtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.manzo.slang.extensions.delayed
import com.manzo.slang.navigation.BaseFragment

/**
 * Created by Manolo D'Antonio on 18/11/2019
 */
class testFragment : BaseFragment() {
    override fun setLayout() = R.layout.row_layout

    override fun setupInterface(view: View, savedInstanceState: Bundle?) {
        delayed { sendBaseBroadcast(Intent().apply { putExtra("key", "fromFragment") }) }
    }

    override val baseBroadcastIntentFilter = "testFilter"


}