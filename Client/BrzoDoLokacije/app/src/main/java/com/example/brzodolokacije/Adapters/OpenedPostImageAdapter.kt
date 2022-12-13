package com.example.brzodolokacije.Adapters

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.brzodolokacije.Models.PostImage
import com.example.brzodolokacije.Services.RetrofitHelper
import com.example.brzodolokacije.databinding.OpenedPostImageBinding
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.github.piasy.biv.view.BigImageView
import java.io.File

class OpenedPostImageAdapter(val items:List<PostImage>?,val activity:Activity): RecyclerView.Adapter<OpenedPostImageAdapter.ViewHolder>() {
    lateinit var binding:OpenedPostImageBinding

    inner class ViewHolder(itemView: OpenedPostImageBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item:PostImage){
            binding.apply {
                ivOpenedImage.setImageLoaderCallback(object : ImageLoader.Callback{
                    override fun onCacheHit(imageType: Int, image: File?) {
                        ivOpenedImage.ssiv.orientation = SubsamplingScaleImageView.ORIENTATION_USE_EXIF
                    }

                    override fun onCacheMiss(imageType: Int, image: File?) {
                    }

                    override fun onStart() {
                    }

                    override fun onProgress(progress: Int) {
                    }

                    override fun onFinish() {
                        ivOpenedImage.ssiv.orientation = SubsamplingScaleImageView.ORIENTATION_USE_EXIF
                    }

                    override fun onSuccess(image: File?) {
                    }

                    override fun onFail(error: Exception?) {
                    }

                })
                ivOpenedImage.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_CROP)
                ivOpenedImage.showImage(Uri.parse(RetrofitHelper.baseUrl + "/api/post/image/compress/" + item._id))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        BigImageViewer.initialize(GlideImageLoader.with(activity))
        val inflater = LayoutInflater.from(parent.context)
        binding= OpenedPostImageBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items!![position])
    }

    override fun getItemCount(): Int {
        if(items==null){
            return 0
        }
        else{
            return items.size
        }
    }

}