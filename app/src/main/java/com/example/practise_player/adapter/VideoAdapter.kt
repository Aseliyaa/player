package com.example.practise_player.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practise_player.R
import com.example.practise_player.models.Video


class VideoAdapter(var context: Context, arrayList: ArrayList<Video>?) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder?>() {
    init {
        Companion.arrayList = arrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(arrayList!![position].link).centerCrop()
            .into(holder.imageView)
        holder.textView.setText(arrayList!![position].userName)
        holder.itemView.setOnClickListener { view: View? ->
            onItemClickListener!!.onClick(
                arrayList!![position]
            )
        }
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var textView: TextView

        init {
            setIsRecyclable(false)
            imageView = itemView.findViewById<ImageView>(R.id.list_item_image)
            textView = itemView.findViewById<TextView>(R.id.tags)
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        Companion.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(video: Video?)
    }

    companion object {
        var arrayList: ArrayList<Video>? = null
        var onItemClickListener: OnItemClickListener? = null
    }
}