import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.spaceshare.R
import com.example.spaceshare.models.Message
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.example.spaceshare.databinding.ImageMessageBinding
import com.example.spaceshare.databinding.MessageBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.sql.Date
import java.text.SimpleDateFormat

// The FirebaseRecyclerAdapter class and options come from the FirebaseUI library
class MessageAdapter(
    private val options: FirebaseRecyclerOptions<Message>,
    private val currentUserName: String?
) : FirebaseRecyclerAdapter<Message, ViewHolder>(options) {

    companion object {
        const val TAG = "MessageAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        if (options.snapshots[position].text != null) {
            (holder as MessageViewHolder).bind(model)
        } else {
            (holder as ImageMessageViewHolder).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    inner class MessageViewHolder(private val binding: MessageBinding) : ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.messageTextView.text = item.text
            setTextColor(item.name, binding.messageTextView)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
            val sentAtTimeFormatted = simpleDateFormat.format(Date(item.timestamp))
            binding.messengerTextView.text = item.name + "  " + sentAtTimeFormatted

            if (item.profilePhotoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.profilePhotoUrl)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.account_circle_black_36dp)
            }
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != "anonymous" && currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.drawable.rounded_message_blue)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.rounded_message_gray)
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    inner class ImageMessageViewHolder(private val binding: ImageMessageBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Message) {
            loadImageIntoView(binding.messageImageView, item.imageUrl!!, false)

            binding.messengerTextView.text = item.name ?: "anonymous"
            if (item.profilePhotoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.profilePhotoUrl)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.account_circle_black_36dp)
            }
        }
    }

    private fun loadImageIntoView(view: ImageView, url: String, isCircular: Boolean = true) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    loadWithGlide(view, downloadUrl, isCircular)
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            loadWithGlide(view, url, isCircular)
        }
    }

    private fun loadWithGlide(view: ImageView, url: String, isCircular: Boolean = true) {
        Glide.with(view.context).load(url).into(view)
        var requestBuilder = Glide.with(view.context).load(url)
        if (isCircular) {
            requestBuilder = requestBuilder.transform(CircleCrop())
        }
        requestBuilder.into(view)
    }
}