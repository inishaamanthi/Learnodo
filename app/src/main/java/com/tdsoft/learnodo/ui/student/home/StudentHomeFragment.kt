package com.tdsoft.learnodo.ui.student.home

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
import kotlinx.android.synthetic.main.fragment_student_home.*
import kotlinx.android.synthetic.main.fragment_student_home.progressBar
import kotlinx.android.synthetic.main.fragment_teacher_materials.*
import java.io.File


class StudentHomeFragment : BaseFragment(), AdapterMaterials.OnMaterialDownloadClickListener {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadClassDetails()
    }

    private fun loadClassDetails() {

        if(requireContext().isInternetAvailable()) {
            progressBar?.show()
            learnodoRepo.getClassRoom(CLASS_ID) { it ->
                progressBar?.hide()
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
        }else{
            val materials = learnodoRepo.getAllMaterials()?.map {
                it.mapToFbModel()
            }
            prepareList(materials)
        }
    }

    private fun prepareList(materials: List<ClassMaterial>) {
        val adapterMaterials = AdapterMaterials(materials)
        adapterMaterials.onMaterialDownloadClickListener = this@StudentHomeFragment
        listMaterials?.let {
            with(listMaterials) {

                layoutManager = LinearLayoutManager(context)
                adapter = adapterMaterials
            }
        }
    }

    override fun onMaterialDownloadClicked(classMaterial: ClassMaterial) {
        if(requireContext().isInternetAvailable()) {
            showYesNoAlert(
                "Confirm",
                "Are sure you want to download this file?",
                { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    progressBar?.show()
                    learnodoRepo.downloadFile(
                        classMaterial.fileName!!,
                        classMaterial.documentUrl!!
                    ) {
                        if (isAdded && isVisible && userVisibleHint) {
                            progressBar?.hide()
                            if (it != null) {
                                val docFile =
                                    FileUtils.createFile(
                                        requireContext(),
                                        FileUtils.ResourceCategory.DOCUMENT,
                                        classMaterial.fileName!!
                                    )
                                val file = it.copyTo(docFile!!, true)
                                learnodoRepo.updateLocalMaterialFile(
                                    classMaterial.materialId,
                                    file.absolutePath
                                )
                                FileUtils.openFile(requireContext(), file)
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
        }else{
            val file = File(classMaterial.documentUrl!!)
            FileUtils.openFile(requireContext(), file)
        }
    }
}