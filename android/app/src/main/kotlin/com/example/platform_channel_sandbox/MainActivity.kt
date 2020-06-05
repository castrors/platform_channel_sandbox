package com.example.platform_channel_sandbox

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"
    private val DART_CHANNEL = "samples.flutter.dev/dart"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        val dartChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, DART_CHANNEL)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // Note: this method is invoked on the main thread.
            call, result ->
            if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()

                accessCalculatorFromFlutter(dartChannel, "sum", listOf<Int>(2, 4) as Object)
                accessCalculatorFromFlutter(dartChannel, "div", listOf<Int>(4, 2) as Object)
                accessCalculatorFromFlutter(dartChannel, "sub", listOf<Int>(4, 2) as Object)

                if (batteryLevel != -1) {

                    result.success(batteryLevel)

                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else {
                result.notImplemented()
            }
        }
    }

    private fun accessCalculatorFromFlutter(dartChannel: MethodChannel, method: String, args: Object) {
        Handler(Looper.getMainLooper()).post {
            dartChannel.invokeMethod(method, args, object : MethodChannel.Result {
                override fun notImplemented() {
                    Log.e("method", "notImplemented")
                }

                override fun error(errorCode: String?, errorMessage: String?, errorDetails: Any?) {
                    Log.e("method", "error")
                    Log.e("method", errorMessage)
                    Log.e("method", errorDetails.toString())
                }

                override fun success(result: Any?) {
                    Log.e("method", method + " success with result:" + result.toString())

                }

            })
        }
    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }

        return batteryLevel
    }
}
