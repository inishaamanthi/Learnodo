package com.tdsoft.learnodo.common

import android.provider.MediaStore
import android.database.DatabaseUtils
import android.annotation.SuppressLint
import android.os.Build
import android.provider.DocumentsContract
import android.content.ContentUris
import android.content.Context
import com.tdsoft.learnodo.common.FileUtils.ResourceCategory
import android.provider.OpenableColumns
import kotlin.Throws
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import androidx.core.content.FileProvider




object FileUtils {
    const val DOCUMENTS_DIR = "documents"
    const val AUTHORITY = "YOUR_AUTHORITY.provider"
    private val LOGGER = Logger.getLogger("FileUtils")

    /**
     * TAG for log messages.
     */
    const val TAG = "FileUtils"
    private const val DEBUG = false // Set to true to enable logging

    /**
     * @return Whether the URI is a local one.
     */
    fun isLocal(url: String?): Boolean {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://")
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is local.
     */
    fun isLocalStorageDocument(uri: Uri): Boolean {
        return AUTHORITY == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG) DatabaseUtils.dumpCursor(cursor)
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @see .isLocal
     * @see .getFile
     */
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {
        if (DEBUG) Log.d(
            TAG + " File -",
            "Authority: " + uri.authority +
                    ", Fragment: " + uri.fragment +
                    ", Port: " + uri.port +
                    ", Query: " + uri.query +
                    ", Scheme: " + uri.scheme +
                    ", Host: " + uri.host +
                    ", Segments: " + uri.pathSegments.toString()
        )
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri)
            } else if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    var filePath: String? = null
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        //getExternalMediaDirs() added in API 21
                        val external = context.externalMediaDirs
                        if (external.size > 1) {
                            filePath = external[1].absolutePath
                            filePath = filePath.substring(0, filePath.indexOf("Android")) + split[1]
                        }
                    } else {
                        filePath = "/storage/" + type + "/" + split[1]
                    }
                    filePath
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads"
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix),
                        java.lang.Long.valueOf(id)
                    )
                    try {
                        val path = getDataColumn(context, contentUri, null, null)
                        if (path != null) {
                            return path
                        }
                    } catch (e: Exception) {
                    }
                }

                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                val fileName = getFileName(context, uri)
                val cacheDir = getDocumentCacheDir(context)
                val file = generateFileName(fileName, cacheDir)
                var destinationPath: String? = null
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(context, uri, destinationPath)
                }
                return destinationPath
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see .getPath
     */
    fun getFile(context: Context, uri: Uri?): File? {
        if (uri != null) {
            val path = getPath(context, uri)
            if (path != null && isLocal(path)) {
                return File(path)
            }
        }
        return null
    }

    fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        logDir(context.cacheDir)
        logDir(dir)
        return dir
    }

    private fun logDir(dir: File) {
        if (!DEBUG) return
        Log.d(TAG, "Dir=$dir")
        val files = dir.listFiles()
        for (file in files) {
            Log.d(TAG, "File=" + file.path)
        }
    }

    fun generateFileName(name: String?, directory: File): File? {
        var name = name
        if (name == null) {
            return null
        }
        var file = File(directory, name)
        if (file.exists()) {
            var fileName: String = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                name = "$fileName($index)$extension"
                file = File(directory, name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            Log.w(TAG, e)
            return null
        }
        logDir(directory)
        return file
    }

    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            while (`is`!!.read(buf) != -1) {
                bos.write(buf)
            }
        } catch (e: IOException) {
            LOGGER.log(Level.WARNING, e.message, e)
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                LOGGER.log(Level.WARNING, e.message, e)
            }
        }
    }

    fun createFile(context: Context, fileCategory: ResourceCategory?, extention: String): File? {
        val timeStamp = SimpleDateFormat("ddMMyyhhmmssSSS", Locale.getDefault()).format(Date())
        val storageDir: File?
        var resultFile: File? = null
        when (fileCategory) {
            ResourceCategory.IMAGE -> {
                storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                resultFile = File(storageDir, "$timeStamp.jpg")
            }
            ResourceCategory.VIDEO -> {
                storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                resultFile = File(storageDir, "$timeStamp.mp4")
            }
            ResourceCategory.DOCUMENT -> {
                storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                resultFile = File(storageDir, timeStamp + extention)
            }
        }
        return resultFile
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null && context != null) {
            val path = getPath(context, uri)
            filename = if (path == null) {
                getName(uri.toString())
            } else {
                val file = File(path)
                file.name
            }
        } else {
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    @Throws(IOException::class)
    fun openFile(context: Context, url: File) {
        // Create URI
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName.toString() + ".provider",
            url
        )
        val intent = Intent(Intent.ACTION_VIEW)
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type, 
        // so Android knew what application to use to open the file
        if (url.name.toString().contains(".doc") || url.name.contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword")
        } else if (url.name.contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf")
        } else if (url.name.contains(".ppt") || url.name.contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        } else if (url.name.contains(".xls") || url.name.contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel")
        } else if (url.name.contains(".zip") || url.name.contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav")
        } else if (url.name.contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf")
        } else if (url.name.contains(".wav") || url.name.contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav")
        } else if (url.name.contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif")
        } else if (url.name.contains(".jpg") || url.name
                .contains(".jpeg") || url.name.contains(".png")
        ) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg")
        } else if (url.name.contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain")
        } else if (url.name.contains(".3gp") || url.name
                .contains(".mpg") || url.name.contains(".mpeg") || url.name
                .contains(".mpe") || url.name.contains(".mp4") || url.name
                .contains(".avi")
        ) {
            // Video files
            intent.setDataAndType(uri, "video/*")
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if(intent.resolveActivity(context.packageManager)!=null) {
            context.startActivity(intent)
        }else{
            Toast.makeText(context, "No supported application found for view the file", Toast.LENGTH_SHORT).show()
        }
    }

    enum class ResourceCategory {
        IMAGE, VIDEO, DOCUMENT
    }
}