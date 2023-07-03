package com.company.smstestingapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.company.smstestingapp.databinding.ActivityMainBinding
import com.company.smstestingapp.room.SmsObserverService
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

//        val content =   SmsObserver1(Handler())
//
//        content.setmContext(this)
//
//        val contentResolver = contentResolver
//        contentResolver.registerContentObserver(
//            Uri.parse("content://sms/sent"),
//            true,
//            content
//        )

        Intent(this, SmsObserverService::class.java).also { intent ->
            startService(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

//            getMMS()
        }
    }

//    private fun getMMS() {
//        val mmsThread = Thread {
////            val uri = Uri.parse("content://mms-sms/conversations?simple=true")
////            val cursor: Cursor? = contentResolver.query(uri, null, null, null, "_date DESC")
//            this.contentResolver?.query(
//                Telephony.Mms.CONTENT_URI,
//                null,
//                null,
//                null,
//                "date DESC"
//            ) ?.apply {
//                if (moveToFirst()) {
//                    val idColumn = getColumnIndex("_id")
//                    val dateColumn = getColumnIndex("date")
//                    val textColumn = getColumnIndex("text_only")
//                    val typeColumn = getColumnIndex("msg_box")
////                   if (smsChecker(idColumn.toString())) {
//                    do {
//                        val id = getString(idColumn)
//                        val isMms = getString(textColumn) == "0"
//                        val date = getString(dateColumn).toLong() * 1000
//                        val type = getString(typeColumn).toInt()
//                        if (isMms) {
//                            val selectionPart = "mid=$id"
//                            val partUri = Uri.parse("content://mms/part")
//                            val cursor = this@MainActivity.contentResolver?.query(
//                                partUri, null,
//                                selectionPart, null, null
//                            )!!
//                            var body = ""
//                            var file: String? = null
//                            if (cursor.moveToFirst()) {
//                                do {
//                                    val partId: String = cursor.getString(cursor.getColumnIndex("_id"))
//                                    val typeString = cursor.getString(cursor.getColumnIndex("ct"))
//                                    if (file == null &&
//                                        (typeString.startsWith("video") ||
//                                                typeString.startsWith("image") ||
//                                                typeString.startsWith("audio"))
//                                    ) {
//                                        file = saveFile(partId, typeString, date)
//                                    }
//                                    if (file != null && body.isNotEmpty()) break
//
//                                } while (cursor.moveToNext())
//                            }
//                            cursor.close()
//
////                            val sender = getAddressNumber(id.toInt())
////                            val rawNumber = sender.second
////
////                            val sh = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext)
////
////                            val myNumber = sh.getString("myNumber", "000");
////                            val dataToSend = Data.Builder()
////                                .putString(SaveMessageWorker.SENDER_PHONE_NUMBER, rawNumber)
////                                .putString(SaveMessageWorker.RECIEVER_PHONE_NUMBER,myNumber )
////                                .putString(SaveMessageWorker.STATUS,"incoming" )
////                                .putString(SaveMessageWorker.MESSAGE_CONTENT,"empty")
////                                .putString(SaveMessageWorker.SENT_AT, Calendar.getInstance().time.toString())
////                                .putString(SaveMessageWorker.RECIEVE_AT, Calendar.getInstance().time.toString())
////                                .putString(SaveMessageWorker.FILE_PATH, file)
////                                .build()
////                            val createPostConstraints =
////                                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
////                            val saveNumberWorkRequest = OneTimeWorkRequest.Builder(SaveMessageWorker::class.java)
////                                .setConstraints(createPostConstraints)
////                                .setInputData(dataToSend).build()
////
////                            mContext?.let { WorkManager.getInstance(it).enqueue(saveNumberWorkRequest) }
////
////                            return@Thread
//
//                        }
//                    } while (moveToNext())
////                   }
//                }
//                close()
//            }
//        }
//        mmsThread.start()
//    }
//
//
//    private fun saveFile(_id: String, typeString: String, date: Long): String {
//        val partURI = Uri.parse("content://mms/part/$_id")
//        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(typeString)
//        val name = "$date.$ext"
//        val destination = File(this?.filesDir, name)
//        val output = FileOutputStream(destination)
//        val input = this?.contentResolver?.openInputStream(partURI) ?: return ""
//        val buffer = ByteArray(4 * 1024)
//        var read: Int
//        while (input.read(buffer).also { read = it } != -1) {
//            output.write(buffer, 0, read)
//        }
//        output.flush()
//        return destination.absolutePath
//    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    //Home button is pressed.
//    override fun onUserLeaveHint() {
//        enterPipMode()
//    }
//
//    private fun enterPipMode() {
//        val aspectRatio = Rational(8, 9)
//        val params = PictureInPictureParams
//            .Builder()
//            .setAspectRatio(aspectRatio)
//            .build()
//        enterPictureInPictureMode(params)
//    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
