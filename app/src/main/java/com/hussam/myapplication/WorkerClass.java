package com.hussam.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;



public class WorkerClass extends ListenableWorker {
    public static final int MINIMAL_DISTANCE = 50;
    private CallbackToFutureAdapter.Completer<Result> callBack;
    private double oldLatitude, oldLongitude;
    BroadcastReceiver broadCast;
    Context context;
    private LocationInfo locationInfo;


    WorkerClass(Context context, WorkerParameters parameters){
        super(context, parameters);
        this.context = context;
        locationInfo = new LocationInfo(context);
    }
    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        final ListenableFuture future = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<Result>() {
            @Nullable
            @Override
            public Object attachCompleter(@NonNull CallbackToFutureAdapter.Completer completer) throws Exception {
                callBack = completer;
                return null;
            }
        });
        return future;
    }
}
