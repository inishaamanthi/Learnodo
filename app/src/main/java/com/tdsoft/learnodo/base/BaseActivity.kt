package com.tdsoft.learnodo.base

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.tdsoft.learnodo.data.LearnodoRepo
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity : AppCompatActivity() {
    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var learnodoRepo: LearnodoRepo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        learnodoRepo = LearnodoRepo.getInstance(this)!!

        someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                onImageCaptured(currentPhotoPath)
            }else{
                onImageCaptured(null)
            }
        }
    }

    fun showOKAlert(
        title: String, message: String,
        onOkClick: DialogInterface.OnClickListener
    ): AlertDialog = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Ok", onOkClick)
        .show()

    fun showYesNoAlert(
        title: String, message: String,
        onYesClick: DialogInterface.OnClickListener, onNoClick: DialogInterface.OnClickListener
    ) = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Yes", onYesClick)
        .setNegativeButton("No", onNoClick)
        .show()

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${application.packageName}.provider",
                        it
                    )
                    takePictureIntent.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    someActivityResultLauncher.launch(
                        takePictureIntent
                    )
                }
            }
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = externalCacheDir!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun setPic(imageView: ImageView, imagePath: String) {
       val uri = FileProvider.getUriForFile(
            this,
            "${application.packageName}.provider",
            File(imagePath)
        )
        Glide.with(this).load(uri).into(imageView)
    }

    open fun onImageCaptured(imagePath: String?) {

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            onPermissionGranted(isGranted)
        }

    fun requestPermission(@NonNull permission: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionGranted(true)
        } else {
            requestPermissionLauncher.launch(
                permission
            )
        }

    }

    open fun onPermissionGranted(granted: Boolean) {

    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}