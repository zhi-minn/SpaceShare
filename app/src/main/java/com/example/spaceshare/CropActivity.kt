package com.example.spaceshare

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.example.spaceshare.databinding.ActivityCropBinding
import com.takusemba.cropme.OnCropListener
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CropActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCropBinding
    private val backButton by lazy { findViewById<ImageView>(R.id.btnClose) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progress) }
    private val cropButton by lazy { findViewById<ImageView>(R.id.btnCrop) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop)

        backButton.setOnClickListener { finish() }

        // Immediately launch image selector
        val contentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri:    Uri? ->
            if (uri == null) return@registerForActivityResult
            binding.cropView.setUri(uri)
        }
        contentLauncher.launch("image/*")

        binding.cropView.addOnCropListener(object : OnCropListener {
            override fun onSuccess(bitmap: Bitmap) {
                progressBar.visibility = View.GONE
                val file = createImageFile()
                saveBitmapToFile(bitmap, file)

                val intent = Intent()
                intent.putExtra("imageUri", file.toUri())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onFailure(e: Exception) {
                Toast.makeText(this@CropActivity, "Failed to clip image", Toast.LENGTH_SHORT).show()
            }
        })

        cropButton.setOnClickListener(View.OnClickListener {
            if (binding.cropView.isOffFrame()) {
                Toast.makeText(this@CropActivity, "Image off frame", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            progressBar.visibility = View.VISIBLE
            binding.cropView.crop()
        })

    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    }
}