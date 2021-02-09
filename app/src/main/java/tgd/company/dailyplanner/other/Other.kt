package tgd.company.dailyplanner.other

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity

fun getFileItemNameText(text: String): String {
    return buildString {
        for (i in 0 until Constants.MAX_NAME_LENGTH) {
            if (i < Constants.MAX_NAME_LENGTH && i < text.length) {
                append(text[i])
            }
        }
        if (text.length > Constants.MAX_NAME_LENGTH) append("...")
    }
}

//fun openFileIntent(pickerInitialUri: Uri, requireActivity: FragmentActivity): Intent? {
////    val intent = Intent(Intent.ACTION_VIEW)
////    intent.setDataAndType(pickerInitialUri, "image/*")
////    return if (intent.resolveActivity(requireActivity.packageManager) != null) {
////        intent
////    } else {
////        Log.w("openFile_TAG", "No activity to handle image/* intent");
////        null
////    }
//    return null
//}