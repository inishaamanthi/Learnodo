package com.tdsoft.learnodo.ui.student.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.common.hide
import com.tdsoft.learnodo.common.inflate
import com.tdsoft.learnodo.common.show
import com.tdsoft.learnodo.data.models.ClassMaterial
import kotlinx.android.synthetic.main.listitem_materials.view.*

class AdapterMaterials(private val list: List<ClassMaterial>) : RecyclerView.Adapter<AdapterMaterials.MaterialViewHolder>() {
    var onMaterialDownloadClickListener:OnMaterialDownloadClickListener? = null

    interface OnMaterialDownloadClickListener{
        fun onMaterialDownloadClicked(classMaterial: ClassMaterial)
    }
    inner class MaterialViewHolder(parent: ViewGroup):RecyclerView.ViewHolder(parent.inflate(R.layout.listitem_materials)){
        fun bind(item:ClassMaterial) = with(itemView){
            txtTitle.text = item.title
            txtDescription.text = item.description
            if(item.documentUrl.isNullOrEmpty()){
                imgAttachment.hide()
            }else{
                imgAttachment.show()
            }

            imgAttachment.setOnClickListener {
                onMaterialDownloadClickListener?.onMaterialDownloadClicked(item)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MaterialViewHolder(parent)

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}