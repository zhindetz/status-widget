package dezz.status.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

import dezz.status.widget.databinding.ActivityLogsBinding;

public class LogsActivity extends AppCompatActivity {

    ActivityLogsBinding binding;
    Context themedContext;
    private static LogsActivity instance;
    private static final StringBuilder fallbackBuffer = new StringBuilder();
    private static final int MAX_LOG_LINES = 500;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final StringBuilder logBuffer = new StringBuilder();
    
    public static void log(String tag, String message) {
        logInternal(tag, message, null);
        Log.d(tag, message);
    }

    public static void log(String tag, String message, Throwable throwable) {
        logInternal(tag, message, throwable);
        Log.e(tag, message, throwable);
    }

    private static void logInternal(String tag, String message, Throwable throwable) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(System.currentTimeMillis());
        String threadName = Thread.currentThread().getName();

        StringBuilder logEntry = new StringBuilder();
        logEntry.append("[").append(timestamp).append("] ")
//                .append("[").append(threadName).append("] ")
                .append(tag).append(": ").append(message);

        if (throwable != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            logEntry.append("\n").append(sw.toString());
        }

        logEntry.append("\n");

        // Send log to LogActivity, if it's running
        LogsActivity instance = LogsActivity.getInstance();
        if (instance != null) {
            instance.postLog(logEntry.toString());
        } else {
            // Save to fallback buffer if LogActivity is not running
            fallbackBuffer.append(logEntry);
        }
    }

    private void postLog(String log) {
        mainHandler.post(() -> {
            logBuffer.append(log);
            // Limit log size
            if (logBuffer.length() > MAX_LOG_LINES * 200) {
                int index = findNthNewline(logBuffer, MAX_LOG_LINES / 2);
                logBuffer.delete(0, index);
            }
            binding.logsTextView.append(log);
            // Auto-scroll to bottom
            binding.logsScrollView.post(() -> binding.logsScrollView.fullScroll(View.FOCUS_DOWN));
        });
    }

    private int findNthNewline(StringBuilder sb, int n) {
        int count = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\n') {
                count++;
                if (count >= n) return i + 1;
            }
        }
        return sb.length() / 2;
    }

    public static LogsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themedContext = new ContextThemeWrapper(this, Helpers.getThemeResId(this));
        binding = ActivityLogsBinding.inflate(LayoutInflater.from(themedContext));
        setContentView(binding.getRoot());

        instance = this;

        // Clear logs
        binding.clearLogsButton.setOnClickListener(v -> {
            logBuffer.setLength(0);
            binding.logsTextView.setText("");
        });
        
        // Hide logs
        binding.hideLogsButton.setOnClickListener(v -> {
            finish();
        });

        // Restore logs from fallback buffer (if there are any before activity creation)
        if (fallbackBuffer.length() != 0) { // for some reason StringBuilder.isEmpty() doesn't work on my emulator
            postLog(fallbackBuffer.toString());
            fallbackBuffer.setLength(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
