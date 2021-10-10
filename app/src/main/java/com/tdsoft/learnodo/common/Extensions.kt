package com.tdsoft.learnodo.common

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.LayoutRes
import com.google.gson.Gson
import java.util.*


fun View.show(btnDisableButton: Button? = null) {
    btnDisableButton?.disable()
    visibility = View.VISIBLE
}

fun View.hide(btnEnableButton: Button? = null) {
    btnEnableButton?.enable()
    visibility = View.GONE
}

fun View.disable() {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}

fun ViewGroup.inflate(@LayoutRes layout: Int) =
    LayoutInflater.from(context).inflate(layout, this, false)

fun String.sendEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:$this") // only email apps should handle this

    intent.putExtra(Intent.EXTRA_SUBJECT, "")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

//fun Context.isInternetAvailable(): Boolean {
//    var result = false
//    val connectivityManager =
//        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//        val networkCapabilities = connectivityManager.activeNetwork ?: return false
//        val actNw =
//            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
//        result = when {
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//            else -> false
//        }
//    } else {
//        connectivityManager.run {
//            connectivityManager.activeNetworkInfo?.run {
//                result = when (type) {
//                    ConnectivityManager.TYPE_WIFI -> true
//                    ConnectivityManager.TYPE_MOBILE -> true
//                    ConnectivityManager.TYPE_ETHERNET -> true
//                    else -> false
//                }
//
//            }
//        }
//    }
//
//    return result
//}

fun Context.isInternetAvailable(): Boolean {
    val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connMgr.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isAvailable && activeNetworkInfo.isConnected;
}

fun String.myMethod(){

}


fun <T : Any> T.convertToJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T : Any> String.convertToObject(): T? {
    return Gson().fromJson(this, T::class.java)
}

fun disableEnableControls(enable: Boolean, vg: ViewGroup) {
    for (i in 0 until vg.childCount) {
        val child = vg.getChildAt(i)
        child.isEnabled = enable
        if (child is ViewGroup) {
            disableEnableControls(enable, child)
        }
    }
}

fun Long.getDate(): String? {
    val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = this * 1000
    return DateFormat.format("dd-MM-yyyy", cal).toString()
}
