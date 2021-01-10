package com.dev_candra.mynotesapp

import android.content.Context
import android.view.View

class CustomOnItemClickListener(private val position: Int,private val onItemClickCallback: OnItemClickCallback): View.OnClickListener{



    interface OnItemClickCallback {
        fun onItemClicked(view: View,position: Int)
    }

    override fun onClick(p0: View?) {
        // Your Code
        p0?.let { onItemClickCallback.onItemClicked(it,position) }
    }


}