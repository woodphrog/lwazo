package dev.hongjun.lwazo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dev.hongjun.lwazo.databinding.ActivityMainBinding
import java.lang.System.exit

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }
        ensurePermissions()
        initSmsFunctionalities()

        loadTextMessages(context = this)

        val myIntent = Intent(this, ConversationListActivity::class.java)
        startActivity(myIntent)
    }

    private fun permissionNotGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=
                PackageManager.PERMISSION_GRANTED
    }

    private var requestCode = 1;

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
        ), requestCode++)
    }

    private fun ensurePermissions() {
        while (permissionNotGranted()) {
            askPermissions()
            Thread.sleep(1000)
        }
    }


    private fun initSmsFunctionalities() {

        val subscriptionManager = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as
                SubscriptionManager

        //ActivityCompat.requestPermissions(this, Array(1){Manifest.permission.READ_PHONE_NUMBERS},1 )



        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ensurePermissions()
            return
        }
        val value = getSystemService(TELEPHONY_SERVICE) as TelephonyManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PhoneNumberManager.myPhoneNumber = subscriptionManager.getPhoneNumber(1)
        }
        else{
            Log.d("COMPOSE", "HELP")
            PhoneNumberManager.myPhoneNumber = value.line1Number
        }
        Log.d("PhoneNumber", PhoneNumberManager.myPhoneNumber)
        SmsReceptionManager.addSmsReceptionListener(onSmsReceivedRef)
    }

    override fun onDestroy() {
        SmsReceptionManager.removeSmsReceptionListener(onSmsReceivedRef)
        super.onDestroy()
    }

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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private val onSmsReceivedRef = { message: SmsEntry ->
        onSmsReceived(message)
    }

    private fun onSmsReceived(smsEntry: SmsEntry) {
        Log.d("SmsReceived", smsEntry.toTransmissionFormat())
        //replyToSms(smsEntry, "Thank you for your message!")
    }
}