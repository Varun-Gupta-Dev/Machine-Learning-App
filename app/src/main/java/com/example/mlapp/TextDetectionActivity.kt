package com.example.mlapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mlapp.databinding.ActivityTextDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextDetectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextDetectionBinding
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTextDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.llCamera.setOnClickListener {
            // open up the camera and store the image
            // Upon clicking an image, we will run the ML algo to detect text out of it

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null){
                // i want to recieve the image and send it for result extraction
                startActivityForResult(intent, 183)
            }else{
                // something went wrong
                Toast.makeText(this, "OOPS, something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        binding.llErase.setOnClickListener{
            binding.etResult.setText("")
        }

        binding.llCopy.setOnClickListener{
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("text", binding.etResult.text)
            clipBoard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to ClipBoard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 183 && resultCode == RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap
            detectTextUsingML(bitmap)
        }
    }

    private fun detectTextUsingML(bitmap: Bitmap) {
        val recognizerLatin = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        // When using Chinese script library
        val recognizerChinese = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

// When using Devanagari script library
        val recognizerDevanagari = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())

// When using Japanese script library
        val recognizerJapanese = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

// When using Korean script library
        val recognizerKorean = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        val image = InputImage.fromBitmap(bitmap, 0)

        // Code for processing image by ML
        recognizerLatin.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                binding.etResult.setText(visionText.text)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Toast.makeText(this, "OOPS, something went wrong", Toast.LENGTH_SHORT).show()
            }

    }
}