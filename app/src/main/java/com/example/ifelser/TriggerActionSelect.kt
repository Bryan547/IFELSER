package com.example.ifelser

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.provider.Settings
import android.app.NotificationManager
import android.os.Build
import android.view.WindowManager
import android.net.Uri
import android.app.AlertDialog
import android.content.IntentFilter
import android.os.BatteryManager


class TriggerActionSelect : ComponentActivity(){

    private lateinit var triggerRadio : RadioGroup
    private lateinit var trigger1 : RadioButton
    private lateinit var trigger2 : RadioButton
    private lateinit var trigger3 : RadioButton

    private lateinit var keyword : EditText
    private lateinit var keywordText : EditText
    private lateinit var keywordButton : Button
    var keywordTrigger = ""

    private lateinit var battery : EditText
    private lateinit var batteryText : EditText
    private lateinit var batteryButton : Button
    var batteryTrigger = ""


    private lateinit var time : EditText
    private lateinit var timeText : EditText
    private lateinit var timeButton : Button
    var timeTrigger = ""

    private lateinit var actionTitle : TextView
    private lateinit var actionRadio : RadioGroup
    private lateinit var action1 : RadioButton
    private lateinit var action2 : RadioButton
    private lateinit var action3 : RadioButton
    private lateinit var actionButton: Button

//    val objectNotification = NotificationAction()

    private val PERMISSION_REQUEST_CODE = 1

    private fun requestPermissions() {
        val permissions = arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY)

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
        openDoNotDisturbSettings()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast(this, "Permission Granted")
            } else {
                showToast(this, "Permission Denied")
            }
        }
    }

    private fun openDoNotDisturbSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        startActivity(intent)
    }

    private fun requestWriteSettingsPermission(context: Context) {
        val packageName = context.packageName
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("Permission Required")
        dialogBuilder.setMessage("To adjust the screen brightness, you need to grant the WRITE_SETTINGS permission.")
        dialogBuilder.setPositiveButton("Go to Settings") { _, _ ->
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Settings.System.canWrite(this)) {
                // Permission granted
                showToast(this, "Permission Granted")
            } else {
                // Permission denied
                showToast(this, "Permission Denied")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trigger_action)

        requestPermissions()
        requestWriteSettingsPermission(this)

        triggerRadio = findViewById(R.id.triggerRadio)
        trigger1 = findViewById(R.id.triggerRadio1)
        trigger2 = findViewById(R.id.triggerRadio2)
        trigger3 = findViewById(R.id.triggerRadio3)
        keyword = findViewById(R.id.keywordEditText)
        keywordButton = findViewById(R.id.keywordSubmit)
        battery = findViewById(R.id.batteryEditText)
        batteryButton = findViewById(R.id.batterySubmit)
        time = findViewById(R.id.timeEditText)
        timeButton = findViewById(R.id.timeSubmit)


        triggerRadio.setOnCheckedChangeListener{_, checkedId ->
            if (checkedId == trigger1.id)
            {
                keyword.visibility = EditText.VISIBLE
                battery.visibility = EditText.GONE
                time.visibility = EditText.GONE
                keywordButton.visibility = Button.VISIBLE
                batteryButton.visibility = Button.GONE
                timeButton.visibility = Button.GONE
                keywordVar()
            }

            else if (checkedId == trigger2.id)
            {
                keyword.visibility = EditText.GONE
                battery.visibility = EditText.VISIBLE
                time.visibility = EditText.GONE
                keywordButton.visibility = Button.GONE
                batteryButton.visibility = Button.VISIBLE
                timeButton.visibility = Button.GONE
                batteryVar()
            }

            else if (checkedId == trigger3.id)
            {
                keyword.visibility = EditText.GONE
                battery.visibility = EditText.GONE
                time.visibility = EditText.VISIBLE
                keywordButton.visibility = Button.GONE
                batteryButton.visibility = Button.GONE
                timeButton.visibility = Button.VISIBLE
                editTimeVar()
            }
        }
    }

    fun keywordVar() {
        keywordButton = findViewById(R.id.keywordSubmit)

        keywordButton.setOnClickListener{
            actionSelect()
        }
    }

    fun batteryVar(){
        batteryButton = findViewById(R.id.batterySubmit)

        batteryButton.setOnClickListener{
            actionSelect()
        }
    }

    fun editTimeVar(){
        timeText = findViewById(R.id.timeEditText)
        timeButton = findViewById(R.id.timeSubmit)

        timeButton.setOnClickListener{
            timeTrigger = timeText.text.toString()
            actionSelect()
        }
    }

    fun getTargetTime(): String {
        timeText = findViewById(R.id.timeEditText)
        timeTrigger = timeText.text.toString()
        return timeTrigger
    }

    fun getTargetBattery(): String {
        batteryText = findViewById(R.id.batteryEditText)
        batteryTrigger = batteryText.text.toString()
        return batteryTrigger
    }

    fun detectBatVol(): Boolean{
        if (getTargetBattery() == getBatteryPercentage(this).toString())
        {
            setVolumeToMax(this)
            println("Conditions met")
            return true
        }
        return false
    }

    fun detectBatDND(): Boolean{
        if (getTargetBattery() == getBatteryPercentage(this).toString())
        {
            setDND(this)
            println("Conditions met")
            return true
        }
        return false
    }

    fun detectBatBright(): Boolean{
        if (getTargetBattery() == getBatteryPercentage(this).toString())
        {
            setBrightnessToMin(this)
            println("Conditions met")
            return true
        }
        return false
    }

    fun detectTimeVol(): Boolean{
        if (getTargetTime() == getCurrentTime())
        {
            setVolumeToMax(this)
            println("Conditions met")
            return true
        }
        return false
    }

    fun detectTimeDND(): Boolean{
        if (getTargetTime() == getCurrentTime())
        {
            setDND(this)
            println("Conditions met")
            return true
        }
        return false
    }

    fun detectTimeBright(): Boolean{
        if (getTargetTime() == getCurrentTime())
        {
            setBrightnessToMin(this)
            println("Conditions met")
            return true
        }
        return false
    }

    fun setVolumeToMax(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Unmute the volume
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_RING,
            AudioManager.ADJUST_UNMUTE,
            AudioManager.FLAG_SHOW_UI
        )

        // Set the volume to the maximum level
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
            AudioManager.FLAG_SHOW_UI
        )
    }
    fun setDND(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        }
    }

    fun setBrightnessToMin(context: Context) {
        // Get the current system brightness
        val currentBrightness = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        )

        // Set the brightness to the minimum value (0)
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            0
        )

        // Restore the original brightness after a delay (e.g., 3 seconds)
        window.decorView.postDelayed({
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                currentBrightness
            )
        }, 3000) // Adjust the delay as needed
    }

    fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun getBatteryPercentage(context: Context): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }

        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        return if (level != -1 && scale != -1) {
            (level.toFloat() / scale.toFloat() * 100).toInt()
        } else {
            -1
        }
    }

    fun actionSelect(){
        actionTitle = findViewById(R.id.actionTitle)
        actionRadio = findViewById(R.id.actionRadio)
        action1 = findViewById(R.id.actionRadio1)
        action2 = findViewById(R.id.actionRadio2)
        action3 = findViewById(R.id.actionRadio3)
        actionButton = findViewById(R.id.actionSubmit)

        actionTitle.visibility = TextView.VISIBLE
        actionRadio.visibility = RadioGroup.VISIBLE
        actionButton.visibility = Button.VISIBLE

        actionButton.setOnClickListener{
            if (action1.isChecked && trigger3.isChecked)
            {
                checkTimeVol()
                showToast(this, "Trigger & Action set")
            }

            else if (action2.isChecked && trigger3.isChecked)
            {
                checkTimeDND()
                showToast(this, "Trigger & Action set")
            }

            else if (action3.isChecked && trigger3.isChecked)
            {
                checkTimeBright()
                showToast(this, "Trigger & Action set")
            }

            else if (action1.isChecked && trigger2.isChecked)
            {
                checkBatVol()
                showToast(this, "Trigger & Action set")
                println(getBatteryPercentage(this).toString())
            }

            else if (action2.isChecked && trigger2.isChecked)
            {
                checkBatDND()
                showToast(this, "Trigger & Action set")
            }

            else if (action3.isChecked && trigger2.isChecked)
            {
                checkBatBright()
                showToast(this, "Trigger & Action set")
            }
        }
    }
    fun checkBatVol() {
        val timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                if (detectBatVol())
                {
                    timer.cancel()
                }
                detectBatVol()
                println("banana")
                println(getTargetBattery())
            }
        }
        timer.schedule(task, 0, 10000)
    }

    fun checkBatDND() {
        val timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                if (detectBatDND())
                {
                    timer.cancel()
                }
                detectBatDND()
                println("banana")
            }
        }
        timer.schedule(task, 0, 10000)
    }

    fun checkBatBright() {
        val timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                if (detectBatBright())
                {
                    timer.cancel()
                }
                detectBatBright()
                println("banana")
            }
        }
        timer.schedule(task, 0, 10000)
    }

    fun checkTimeBright() {
        val timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                if (detectTimeBright())
                {
                    timer.cancel()
                }
                detectTimeBright()
                println("banana")
            }
        }
        timer.schedule(task, 0, 10000)
    }

    fun checkTimeDND() {
        val timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                if (detectTimeDND())
                {
                    timer.cancel()
                }
                detectTimeDND()
                println("banana")
            }
        }
        timer.schedule(task, 0, 10000)
    }
    fun checkTimeVol() {
        val timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                if (detectTimeVol())
                {
                    timer.cancel()
                }
                detectTimeVol()
                println("banana")
            }
        }
        timer.schedule(task, 0, 10000)
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }




//    fun processNotification(sbn: StatusBarNotification) {
//        val notificationListener = NotificationAction()
//        notificationListener.onNotificationPosted(sbn)
//    }

    fun showTimePickerDialog(view: View) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                time.setText(selectedTime)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.setOnDismissListener {
            time.clearFocus()
        }

        timePickerDialog.show()
    }
}

//class NotificationAction : NotificationListenerService() {
//
//    val objectKeyword = TriggerActionSelect()
//    override fun onNotificationPosted(sbn: StatusBarNotification) {
//        val keyword = objectKeyword.getKeyword() // Replace with your desired keyword
//
//        val notification: Notification = sbn.notification
//        val text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
//
//        if (text?.contains(keyword, ignoreCase = true) == true) {
//            // Keyword found in the notification
//            // Perform your desired actions here
//            // For example, display a toast message
//            objectKeyword.setVolumeToMax(applicationContext)
//        }
//    }
//}