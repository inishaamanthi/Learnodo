package com.tdsoft.learnodo.base

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.tdsoft.learnodo.data.LearnodoRepo

import android.content.Intent
import android.app.Activity
import android.content.pm.PackageManager
import android.webkit.MimeTypeMap

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.tdsoft.learnodo.common.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

open class BaseFragment : Fragment() {
    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var learnodoRepo: LearnodoRepo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        learnodoRepo = LearnodoRepo.getInstance(requireContext())!!

        initIntentLaunchers()
    }

    private fun initIntentLaunchers() {
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        someActivityResultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getFile(result.data)
            }
        }
    }

    fun showOKAlert(
        title: String, message: String,
        onOkClick: DialogInterface.OnClickListener
    ): AlertDialog = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Ok", onOkClick)
        .show()

    fun showYesNoAlert(
        title: String, message: String,
        onYesClick: DialogInterface.OnClickListener, onNoClick: DialogInterface.OnClickListener
    ): AlertDialog = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Yes", onYesClick)
        .setNegativeButton("No", onNoClick)
        .show()

    fun pickFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        // Update with mime types

        // Update with mime types
        intent.type = "*/*"


        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.

        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)

        someActivityResultLauncher.launch(intent)
    }

    private fun getFile(data: Intent?) {
        if (data == null || data.data == null) {
            showOKAlert("Error", "Something went wrong"){ dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                onFileSelect(null)
            }
            return
        }
        var inputStream: InputStream? = null
        try {
            inputStream = requireContext().contentResolver.openInputStream(data.data!!)
            val mimeType = requireContext().contentResolver.getType(data.data!!)
            val extension = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            val docFile =
                FileUtils.createFile(requireContext(), FileUtils.ResourceCategory.DOCUMENT, extension)
            org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, docFile)
           onFileSelect(docFile)
        } catch (e: Exception) {
            showOKAlert("Error", "File not supported"){ dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            onFileSelect(null)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    open fun onFileSelect(docFile: File?) {
        TODO("Not yet implemented")
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            onPermissionGranted(isGranted)
        }

    fun requestPermission(@NonNull permission: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
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


    open fun onFabClick(){

    }
}