package tgd.company.dailyplanner.service.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.AsyncListDiffer
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.RequestManager
//import tgd.company.educationproject.R
//import tgd.company.educationproject.data.local.ShoppingItem
//import javax.inject.Inject
//
//class ShoppingItemAdapter @Inject constructor(
//        private val glide: RequestManager
//) : RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder>() {
//
//    class ShoppingItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
//
//    private val diffCallback = object : DiffUtil.ItemCallback<ShoppingItem>() {
//        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
//            return oldItem.hashCode() == newItem.hashCode()
//        }
//    }
//
//    private val differ = AsyncListDiffer(this, diffCallback)
//
//    var shoppingItems: List<ShoppingItem>
//        get() = differ.currentList
//        set(value) = differ.submitList(value)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {
//        return ShoppingItemViewHolder(
//                LayoutInflater.from(parent.context).inflate(
//                        R.layout.item_shopping,
//                        parent,
//                        false
//                )
//        )
//    }
//
//    override fun onBindViewHolder(holder: ShoppingItemViewHolder, position: Int) {
//        val shoppingItem = shoppingItems[position]
//        holder.itemView.apply {
//            glide.load(shoppingItem.imageUrl).into(findViewById(R.id.ivShoppingImage))
//            this.findViewById<TextView>(R.id.tvName).text = shoppingItem.name
//            this.findViewById<TextView>(R.id.tvShoppingItemAmount).text = shoppingItem.amount.toString()
//            this.findViewById<TextView>(R.id.tvShoppingItemPrice).text = shoppingItem.price.toString()
//        }
//    }
//
//    override fun getItemCount(): Int {
//       return shoppingItems.size
//    }
//}