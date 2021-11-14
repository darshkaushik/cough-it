package com.ibmhack2021.coughit.ui.pasttests

import androidx.recyclerview.widget.DiffUtil
import com.ibmhack2021.coughit.model.pasttests.Test

class CustomDiffUtl : DiffUtil.ItemCallback<Test>() {
    override fun areItemsTheSame(oldItem: Test, newItem: Test): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: Test, newItem: Test): Boolean {
        return oldItem.date == newItem.date  && oldItem.prediction == newItem.prediction
    }

}