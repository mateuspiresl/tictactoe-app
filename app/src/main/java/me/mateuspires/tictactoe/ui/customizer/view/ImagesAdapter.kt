package me.mateuspires.tictactoe.ui.customizer.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_board_cell.view.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.data.models.ImageSearch
import me.mateuspires.tictactoe.ui.customizer.CustomizerContract

class ImagesAdapter(
        private val context: Context,
        private val listener: OnImageClickListener
) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>(), CustomizerContract.ImageSearchAdapter {

    private var images: Array<ImageSearch.Item> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_image,
                parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(images[position].url).apply(RequestOptions().centerCrop())
                .into(holder.view.iv_content)
        holder.view.setOnClickListener { listener.onImageClick(images[position]) }
    }

    /**
     * Updated the status and the board cells.
     * Animations are triggered only when there is real change.
     */
    override fun update(images: Array<ImageSearch.Item>) {
        this.images = images
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface OnImageClickListener {

        /**
         * Notifies a image was click.
         */
        fun onImageClick(image: ImageSearch.Item)
    }
}
