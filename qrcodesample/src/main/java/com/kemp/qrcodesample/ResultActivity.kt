package com.kemp.qrcodesample

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        init()
    }

    fun init() {
        if (intent == null || !intent.hasExtra("result")) {
            return
        }
        val text = intent.getStringExtra("result")
        val tv: TextView = findViewById(R.id.text) as TextView
        tv.text = text

        val btnCopy = findViewById(R.id.btn_copy)
        btnCopy.setOnClickListener {
            val clipBoardManager: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoardManager.primaryClip = ClipData.newPlainText("text", text)
            Toast.makeText(this, "已复制", Toast.LENGTH_LONG).show()
        }

        val btnBrowser = findViewById(R.id.btn_browser)
        btnBrowser.setOnClickListener {
            if(!text.startsWith("http://")){
                Toast.makeText(this, "无效网址", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val contentUrl = Uri.parse(text)
            intent.data = contentUrl
            startActivity(intent)
        }
    }
}
