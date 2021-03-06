package com.muedsa.bilibililivetv.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskRunner {
    private static final String TAG = TaskRunner.class.getSimpleName();

    private static final TaskRunner instance = new TaskRunner();

    private final Executor executor =  new ThreadPoolExecutor(5, 128, 1,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private final Handler handler = new Handler(Looper.getMainLooper());


    private TaskRunner() {
    }

    public static TaskRunner getInstance() {
        return instance;
    }

    public interface Callable extends java.util.concurrent.Callable<Message> {}

    public interface Callback<R> {
        void onComplete(R result);
    }

    public void executeAsync(@NonNull Callable callable, @NonNull Callback<Message> callback) {
        executor.execute(() -> {
            Message result = null;
            try {
                result = callable.call();
            } catch (Exception e) {
                Log.e(TAG, "executeAsync failure", e);
            } finally {
                final Message finalResult = result == null? Message.obtain() : result;
                handler.post(() -> {
                    callback.onComplete(finalResult);
                });
            }
        });
    }

    public <R> void executeAsync(@NonNull Runnable runnable){
        executor.execute(runnable);
    }
}
