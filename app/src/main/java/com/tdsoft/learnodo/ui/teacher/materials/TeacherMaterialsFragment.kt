package com.tdsoft.learnodo.ui.teacher.materials

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseFragment
import com.tdsoft.learnodo.common.*
import com.tdsoft.learnodo.data.models.ClassMaterial
import kotlinx.android.synthetic.main.fragment_add_material_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_student_home.*
import kotlinx.android.synthetic.main.fragment_teacher_materials.*
import kotlinx.android.synthetic.main.fragment_teacher_materials.progressBar
import java.io.File


class TeacherMaterialsFragment : BaseFragment(),
    AdapterTeacherMaterials.OnMaterialDownloadClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_materials, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadClassDetails()
    }

    private fun loadClassDetails() {

        if (requireContext().isInternetAvailable()) {
            learnodoRepo.getClassRoom(CLASS_ID) { it ->
                if (it != null) {
                    val materials = it.materials?.values?.toList() ?: emptyList()
                    learnodoRepo.saveMaterials(materials.map { it.mapToDBModel() })
                    prepareList(materials)
                } else {
                    showOKAlert(
                        "No class found",
                        "You have not registered with any class"
                    ) { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                    }
                }
            }
        } else {
            val materials = learnodoRepo.getAllMaterials().map {
                it.mapToFbModel()
            }
            prepareList(materials)
        }
    }

    private fun prepareList(materials: List<ClassMaterial>) {
        val adapterMaterials = AdapterTeacherMaterials(materials)
        adapterMaterials.onMaterialDownloadClickListener = this@TeacherMaterialsFragment

        listTeacherMaterials?.let {
            with(listTeacherMaterials) {

                layoutManager = LinearLayoutManager(context)
                adapter = adapterMaterials
            }
        }
    }

    override fun onMaterialDownloadClicked(classMaterial: ClassMaterial) {
        showYesNoAlert(
            "Confirm",
            "Are sure you want to download this file?",
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                progressBar?.show()
                learnodoRepo.downloadFile(classMaterial.fileName!!, classMaterial.documentUrl!!) {
                    if (isAdded && isVisible && userVisibleHint) {
                        progressBar?.hide()
                        if (it != null) {
                            val docFile =
                                FileUtils.createFile(
                                    requireContext(),
                                    FileUtils.ResourceCategory.DOCUMENT,
                                    classMaterial.fileName!!
                                )
                            val sss = it.copyTo(docFile!!, true)
                            FileUtils.openFile(requireContext(), sss)
                        } else {
                            showOKAlert(
                                "Failed",
                                "Failed downloading"
                            ) { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()
                            }
                        }
                    }
                }
            },
            { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            })
    }

    override fun onFabClick() {
        AddMaterialBottomSheetFragment.show(
            CLASS_ID,
            childFragmentManager,
            object : AddMaterialBottomSheetFragment.OnFileUploadedClickListener {

                override fun onFileUploadedClick(classMaterial: ClassMaterial, file: File) {
                    progressBar?.show()
                    learnodoRepo.uploadMaterial(
                        classMaterial, file
                    ) {
                        progressBar?.hide()

                        if (it != null) {
                            showOKAlert(
                                "Success",
                                "Uploaded successfully"
                            ) { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()

                            }
                        } else {
                            showOKAlert(
                                "Failed",
                                "Failed uploading"
                            ) { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()
                            }
                        }
                    }
                }


            })
    }


}