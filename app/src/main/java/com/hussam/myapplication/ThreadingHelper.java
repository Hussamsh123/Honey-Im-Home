package com.hussam.myapplication;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ThreadingHelper {
  private static Executor bgExecutor = Executors.newCachedThreadPool();

  static void runAsyncInBackground(Runnable block) {
    bgExecutor.execute(block);
  }

  static void runAsyncInMainThread(Runnable block) {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(block);
  }
}
