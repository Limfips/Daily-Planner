package tgd.company.dailyplanner.other

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


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

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // For 29 api or above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->    true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->   true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->   true
            else ->     false
        }
    }
    // For below 29 api
    else {
        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
            return true
        }
    }
    return false
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