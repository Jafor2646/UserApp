package com.example.userapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.userapp.R

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("imageFromPath")
    fun loadImage(view: ImageView, imagePath: String?) {
        if (!imagePath.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imagePath)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(view)
        } else {
            view.setImageResource(R.drawable.ic_person)
        }
    }

    @JvmStatic
    @BindingAdapter("circleImageFromPath")
    fun loadCircleImage(view: de.hdodenhof.circleimageview.CircleImageView, imagePath: String?) {
        if (!imagePath.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imagePath)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(view)
        } else {
            view.setImageResource(R.drawable.ic_person)
        }
    }
}
