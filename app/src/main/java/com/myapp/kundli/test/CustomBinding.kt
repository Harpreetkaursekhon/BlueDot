package com.myapp.kundli.test

import android.opengl.Visibility
import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("goneOrVisible")
fun View.goneOrVisible(isVisible: Boolean){
    if(isVisible){
        visibility= View.VISIBLE
    }else{
        visibility= View.GONE
    }
}