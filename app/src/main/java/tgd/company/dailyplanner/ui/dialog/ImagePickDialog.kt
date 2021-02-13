package tgd.company.dailyplanner.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.RequestManager
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.other.Constants.IMAGE_TYPE
import tgd.company.dailyplanner.other.Constants.REQUEST_CODE_IMAGE
import tgd.company.dailyplanner.other.getFileItemNameText

class ImagePickDialog(
        private var listener: NoticeDialogListener,
        private var glide: RequestManager
) : DialogFragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_image_pick, container)
    }

    private lateinit var dialogView: View
    private var name: String = ""
    private var uri: String = ""

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            dialogView = inflater.inflate(R.layout.dialog_image_pick, null)

            dialogView.findViewById<EditText>(R.id.etNameCustom).addTextChangedListener { editable ->
                editable?.let {
                    dialogView
                        .findViewById<TextView>(R.id.tvItemName)
                        .text = getFileItemNameText(editable.toString())
                    name = editable.toString()
                }
            }

            dialogView.findViewById<ImageView>(R.id.ivItemShoppingImage).setOnClickListener {
                Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).also { intent ->
                    intent.type = IMAGE_TYPE
                    startActivityForResult(intent, REQUEST_CODE_IMAGE)
                }
            }

            builder.setView(dialogView)

            builder
                .setPositiveButton(R.string.accept
                ) { dialog, id ->
                    listener.onDialogPositiveClick(name, uri)
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, id ->
                    listener.onDialogNegativeClick()
                    getDialog()?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE) {
            data?.data?.let {
                val takeFlags = (data.flags
                        and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
                activity?.contentResolver?.takePersistableUriPermission(it, takeFlags)
                uri = it.toString()
                glide.load(it).into(dialogView.findViewById(R.id.ivItemShoppingImage))
            }
        }
    }

    interface NoticeDialogListener {
        fun onDialogPositiveClick(imageName: String, imageUri: String)
        fun onDialogNegativeClick()
    }
}