package com.ibmhack2021.coughit.ui.pasttests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibmhack2021.coughit.databinding.TestRowLayoutBinding
import com.ibmhack2021.coughit.model.pasttests.Test
import com.ibmhack2021.coughit.util.Utility

class PastTestAdapter(diffCallback: CustomDiffUtl) : ListAdapter<Test, PastTestAdapter.MyViewHolder>(diffCallback) {

    class MyViewHolder(val viewBinding: TestRowLayoutBinding) :
        RecyclerView.ViewHolder(viewBinding.root){
            fun bind(test: Test){

                viewBinding.run {
                    dateTextView.text = Utility.extractDate(test.date)
                    timeTextView.text = Utility.extractTime(test.date)
                    val resultText = String.format("%.2f" , test.prediction.toDouble()*100) + "%"
                    resultTextView.text = resultText
                    tagChip.text = if(test.prediction.toDouble()*100 < 70) "Safe" else "Unsafe"
                }
            }

        companion object{
            fun from(parent: ViewGroup) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TestRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

}