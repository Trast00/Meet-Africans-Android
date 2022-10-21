package com.lnd.RencontreAfricaine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class BuyCodeActivity : AppCompatActivity() {

    private lateinit var web : WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_code)

        web = findViewById(R.id.web)
        web.webViewClient = WebViewClient()
        web.loadUrl(PremiumActivity.buyUrl)

        web.settings.javaScriptEnabled = true
    }

    override fun onBackPressed() {
        if (web.canGoBack()){
            web.goBack()
        }else{
            super.onBackPressed()
        }
    }
}