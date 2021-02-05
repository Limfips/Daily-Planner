package tgd.company.dailyplanner.service.adapters

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.data.customevent.CustomEvent
import java.util.*
import java.util.Locale


class CustomEventAdapter(private val context: Context) :
        ListAdapter<
                CustomEvent,
                CustomEventAdapter.CustomEventListAdapterViewHolder
                >(CurrencyComparator()) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): CustomEventListAdapterViewHolder {
        return CustomEventListAdapterViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CustomEventListAdapterViewHolder, position: Int) {
        holder.bind(getItem(position), context)
    }

    class CustomEventListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeTextView: TextView = itemView.findViewById(R.id.time_text_id)
        private val nameTextView: TextView = itemView.findViewById(R.id.name_text_id)


        fun bind(customEvent: CustomEvent, context: Context) {
            val builder: StringBuilder = StringBuilder()
            val secondApiFormat = SimpleDateFormat(context.getString(R.string.basic_time_format), Locale.ROOT)

            builder.append(secondApiFormat.format(Date(customEvent.date_start)))
            builder.append(" - ")
            builder.append(secondApiFormat.format(Date(customEvent.date_finish)))
            timeTextView.text = builder.toString()
            nameTextView.text = customEvent.name
        }

        companion object {
            fun create(parent: ViewGroup): CustomEventListAdapterViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_event, parent, false)
                return CustomEventListAdapterViewHolder(view)
            }
        }
    }

    class CurrencyComparator : DiffUtil.ItemCallback<CustomEvent>() {
        override fun areItemsTheSame(oldItem: CustomEvent, newItem: CustomEvent): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CustomEvent, newItem: CustomEvent): Boolean {
            return  oldItem.date_start == newItem.date_start
                    && oldItem.date_finish == newItem.date_finish
                    && oldItem.name == newItem.name
                    && oldItem.description == newItem.description
                    && oldItem.id == newItem.id
        }
    }
}