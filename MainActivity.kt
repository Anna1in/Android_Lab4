package com.example.lab_andr4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.widget.*


class MainActivity : AppCompatActivity() {
    private lateinit var typeGroup: RadioGroup
    private lateinit var urlInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        typeGroup = findViewById(R.id.typeGroup)
        urlInput = findViewById(R.id.urlInput)

        findViewById<Button>(R.id.pickFileButton).setOnClickListener {
            if (typeGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Оберіть тип файлу (аудіо або відео)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = when (typeGroup.checkedRadioButtonId) {
                R.id.audioButton -> "audio/*"
                else -> "video/*"
            }

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                this.type = type
            }
            startActivityForResult(intent, 100)
        }

        findViewById<Button>(R.id.playFromUrlButton).setOnClickListener {
            if (typeGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Оберіть тип файлу (аудіо або відео)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = urlInput.text.toString()
            if (url.isBlank()) {
                Toast.makeText(this, "Введіть URL-адресу", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PlayerAct::class.java)
            intent.putExtra("url", url)
            intent.putExtra("isVideo", typeGroup.checkedRadioButtonId == R.id.videoButton)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val intent = Intent(this, PlayerAct::class.java)
                intent.putExtra("uri", uri.toString())
                intent.putExtra("isVideo", typeGroup.checkedRadioButtonId == R.id.videoButton)
                startActivity(intent)
            }
        }
    }
}