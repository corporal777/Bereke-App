package org.wilderkek.bereke.util.imagepicker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemGalleryImageBinding
import org.wilderkek.bereke.util.imagepicker.model.Image
import org.wilderkek.bereke.util.imagepicker.utils.scaleRevert
import org.wilderkek.bereke.util.imagepicker.utils.scaleStart

internal class ImagePickerAdapter(
    private val items: List<Image>,
    private val listener: GalleryListener,
    private val isSingleType: Boolean
) : RecyclerView.Adapter<ImagePickerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {
        val binding =
            ItemGalleryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagePickerViewHolder(
            binding,
            listener,
            isSingleType
        )
    }

    override fun onBindViewHolder(holder: ImagePickerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItem(item: Image) {
        val position = items.indexOf(item)
        notifyItemChanged(position)
    }
}

internal class ImagePickerViewHolder(
    private val binding: ItemGalleryImageBinding,
    private val listener: GalleryListener,
    private val isSingleType: Boolean
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(image: Image) {

        if (isSingleType) {
            binding.root.setOnClickListener {
                listener.onClick(image)
            }
            binding.imageCheckbox.isVisible = false
        } else {
            binding.itemImage.transitionName = image.id.toString()
            itemView.setOnClickListener {
                listener.onChecked(image)
            }
//            binding.root.setOnClickListener {
//                if (listener.isMultipleChecked) {
//                    listener.onChecked(image)
//                } else {
//                    listener.onShowDetail(binding.itemImage, image)
//                }
//            }
//
//
//            itemView.setOnLongClickListener {
//                if (!listener.isMultipleChecked) {
//                    listener.onChecked(image)
//                }
//                return@setOnLongClickListener true
//            }

            if (image.selected) {
                binding.imageCheckbox.setBackgroundResource(R.drawable.bg_checked)
                scaleStart(binding.itemImage) {
                    binding.imageFilter.isVisible = true
                }
            } else {
                binding.imageCheckbox.setBackgroundResource(R.drawable.bg_uchecked)
                scaleRevert(binding.itemImage) {
                    binding.imageFilter.isVisible = false
                }
            }
        }

        Glide.with(binding.itemImage)
            .load(image.path)
            .centerCrop()
            .into(binding.itemImage)
    }
}