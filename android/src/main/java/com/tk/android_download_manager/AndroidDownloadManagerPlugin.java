package com.tk.android_download_manager;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class AndroidDownloadManagerPlugin implements FlutterPlugin, ActivityAware {
    private MethodChannel methodChannel;
    private Activity activity;
    private EventChannel eventChannel;

    private void setupChannels(BinaryMessenger messenger, Context context, Activity activity) {
        methodChannel = new MethodChannel(messenger, "download_manager");
        eventChannel = new EventChannel(messenger, "download_manager/complete");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadMethodChannelHandler downloadMethodChannelHandler = new DownloadMethodChannelHandler(context, manager, activity);
        DownloadBroadcastReceiver receiver = new DownloadBroadcastReceiver(context);
        methodChannel.setMethodCallHandler(downloadMethodChannelHandler);
        eventChannel.setStreamHandler(receiver);
    }

    private void teardownChannels() {
        methodChannel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
        methodChannel = null;
        eventChannel = null;
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        setupChannels(binding.getBinaryMessenger(), binding.getApplicationContext(), this.activity);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
        teardownChannels();
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }
}