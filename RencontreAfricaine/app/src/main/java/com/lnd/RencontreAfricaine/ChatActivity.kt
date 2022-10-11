package com.lnd.RencontreAfricaine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ChatActivity : AppCompatActivity() {
    companion object{
        var partner: Users? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }
}