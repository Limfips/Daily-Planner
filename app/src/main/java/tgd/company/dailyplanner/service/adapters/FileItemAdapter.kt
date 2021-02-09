package tgd.company.dailyplanner.service.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.other.getFileItemNameText
import javax.inject.Inject

class FileItemAdapter @Inject constructor(
        private val glide: RequestManager
) : RecyclerView.Adapter<FileItemAdapter.FileItemViewHolder>() {

    class FileItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem.roomUrl == newItem.roomUrl
        }

        override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var fileItems: List<FileItem>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemViewHolder {
        return FileItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_file,
                        parent,
                        false
                )
        )
    }

    private var onItemClickListener: ((FileItem) -> Unit)? = null
    fun setOnItemClickListener(listener: (FileItem) -> Unit) {
        onItemClickListener = listener
    }

    private var isVisibleDeleteIcon = false
    fun setVisibleDeleteIcon(value: Boolean) {
        isVisibleDeleteIcon = value
    }

    private var onDeleteItemClickListener: ((FileItem) -> Unit)? = null
    fun setonDeleteItemClickListener(listener: (FileItem) -> Unit) {
        onDeleteItemClickListener = listener
    }

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        val file = fileItems[position]
        val tvNameText = getFileItemNameText(file.name)
        holder.itemView.apply {

            if (file.serverUrl.isNotEmpty()) {
                glide.load(file.serverUrl).into(findViewById(R.id.ivItemShoppingImage))
            } else {
                glide.load(file.roomUrl).into(findViewById(R.id.ivItemShoppingImage))
            }

            if (isVisibleDeleteIcon) {
                findViewById<AppCompatImageButton>(R.id.aivDelete).visibility = View.VISIBLE
            } else {
                findViewById<AppCompatImageButton>(R.id.aivDelete).visibility = View.INVISIBLE
            }
            findViewById<AppCompatImageButton>(R.id.aivDelete).setOnClickListener {
                onDeleteItemClickListener?.let { click ->
                    click(file)
                }
            }

            findViewById<TextView>(R.id.tvItemName).text = tvNameText

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(file)
                }
            }
        }
    }



    override fun getItemCount(): Int {
       return fileItems.size
    }
}