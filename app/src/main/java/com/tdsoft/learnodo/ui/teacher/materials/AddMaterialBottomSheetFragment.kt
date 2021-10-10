package com.tdsoft.learnodo.ui.teacher.materials

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseBottomSheetDialogFragment
import com.tdsoft.learnodo.common.hide
import com.tdsoft.learnodo.common.show
import com.tdsoft.learnodo.data.models.ClassMaterial
import kotlinx.android.synthetic.main.fragment_add_material_bottom_sheet.*
import java.io.File


class AddMaterialBottomSheetFragment : BaseBottomSheetDialogFragment() {
    private lateinit var classId: String
    private var selectedFile: File? = null
    private lateinit var onFileUploaded: OnFileUploadedClickListener

    interface OnFileUploadedClickListener {
        fun onFileUploadedClick(classMaterial: ClassMaterial, file: File)
    }

    companion object {
        fun show(
            classId: String,
            fragmentManager: FragmentManager,
            onFileUploadedClickListener: OnFileUploadedClickListener
        ) {
            val bSheet = AddMaterialBottomSheetFragment()
            bSheet.classId = classId
            bSheet.onFileUploaded = onFileUploadedClickListener
            bSheet.show(fragmentManager, "AddMaterialBottomSheetFragment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_material_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnPickFile.setOnClickListener {
            requestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        btnUpload.setOnClickListener {
            if (selectedFile == null) {
                showOKAlert(
                    "Not found",
                    "Please select a file"
                ) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                return@setOnClickListener
            }
            dismiss()
            onFileUploaded.onFileUploadedClick(
                ClassMaterial(
                    null,
                    null,
                    textInputTitle.editText?.text.toString(),
                    textInputDescription.editText?.text.toString(),
                    classId,
                    selectedFile!!.name
                ),
                selectedFile!!
            )
        }

        btnClearFile.setOnClickListener {
            selectedFile = null
            btnClearFile.hide()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onPermissionGranted(granted: Boolean) {
        super.onPermissionGranted(granted)
        if (granted) {
            pickFile()
        } else {
            Toast.makeText(requireContext(), "Permission grant failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFileSelect(docFile: File?) {
        if (docFile != null) {
            selectedFile = docFile
            btnCancel.show()
        } else {
            showOKAlert(
                "Failed",
                "File loading failed"
            ) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
        }
    }

}