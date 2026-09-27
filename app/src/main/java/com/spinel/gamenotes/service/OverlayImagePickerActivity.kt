package com.spinel.gamenotes.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.spinel.gamenotes.util.StorageUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object OverlayImagePickerBridge {
    private val _pickedImageUri = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val pickedImageUri = _pickedImageUri.asSharedFlow()

    private val _isPickerActive = MutableSharedFlow<Boolean>(replay = 1, extraBufferCapacity = 1)
    val isPickerActive = _isPickerActive.asSharedFlow()

    fun setPickerActive(active: Boolean) {
        _isPickerActive.tryEmit(active)
    }

    fun onImagePicked(internalUri: String) {
        _pickedImageUri.tryEmit(internalUri)
    }
}

class OverlayImagePickerActivity : ComponentActivity() {

    private val photoPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        try {
            if (uri != null) {
                val savedPath = StorageUtils.saveImageToInternalStorage(this, uri)
                OverlayImagePickerBridge.onImagePicked(savedPath)
            }
        } catch (e: Exception) {
            Log.e("OverlayImagePicker", "Failed to save image from picker", e)
        } finally {
            finish()
        }
    }

    private val fallbackPicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        try {
            if (uri != null) {
                val savedPath = StorageUtils.saveImageToInternalStorage(this, uri)
                OverlayImagePickerBridge.onImagePicked(savedPath)
            }
        } catch (e: Exception) {
            Log.e("OverlayImagePicker", "Failed to save image from fallback", e)
        } finally {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OverlayImagePickerBridge.setPickerActive(true)
        try {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (e: Exception) {
            try {
                fallbackPicker.launch("image/*")
            } catch (e2: Exception) {
                Log.e("OverlayImagePicker", "Failed to launch both photo pickers", e2)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OverlayImagePickerBridge.setPickerActive(false)
    }

    companion object {
        fun launch(context: Context) {
            try {
                val intent = Intent(context, OverlayImagePickerActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("OverlayImagePicker", "Failed to launch OverlayImagePickerActivity", e)
                OverlayImagePickerBridge.setPickerActive(false)
            }
        }
    }
}
