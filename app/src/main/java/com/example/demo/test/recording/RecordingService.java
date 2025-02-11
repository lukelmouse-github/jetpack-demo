package com.example.demo.test.recording;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecordingService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "recording_channel";
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private File outputFile;

    private FileOutputStream wavOutputStream;
    private int audioLength = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY; // 服务被杀死后自动重启
    }

    private void startRecording() {
        // 配置 AudioRecord 参数
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // 创建 WAV 输出文件
        File outputFile = new File(getExternalFilesDir(null), "recording.wav");

        try {
            wavOutputStream = new FileOutputStream(outputFile);
            writeWavHeader(wavOutputStream, sampleRate, audioFormat == AudioFormat.ENCODING_PCM_16BIT ? 16 : 8, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        audioRecord = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        );

        isRecording = true;
        audioRecord.startRecording();

        // 将录音数据写入文件（在子线程中运行）
        new Thread(() -> {
            byte[] buffer = new byte[bufferSize];
            while (isRecording) {
                int bytesRead = audioRecord.read(buffer, 0, bufferSize);
                if (bytesRead > 0) {
                    try {
                        wavOutputStream.write(buffer, 0, bytesRead);
                        audioLength += bytesRead;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                updateWavHeader();
                wavOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    private void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    private Notification buildNotification() {
        // 创建通知（Android 8.0+ 必须设置通知渠道）
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("后台录音中")
            .setContentText("正在录制音频...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "录音通道",
                NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void writeWavHeader(FileOutputStream out, int sampleRate, int bitsPerSample, int channels) throws IOException {
        writeString(out, "RIFF"); // chunk id
        writeInt(out, 36); // chunk size (will be updated later)
        writeString(out, "WAVE"); // format
        writeString(out, "fmt "); // subchunk 1 id
        writeInt(out, 16); // subchunk 1 size
        writeShort(out, (short) 1); // audio format (1 = PCM)
        writeShort(out, (short) channels); // number of channels
        writeInt(out, sampleRate); // sample rate
        writeInt(out, sampleRate * channels * bitsPerSample / 8); // byte rate
        writeShort(out, (short) (channels * bitsPerSample / 8)); // block align
        writeShort(out, (short) bitsPerSample); // bits per sample
        writeString(out, "data"); // subchunk 2 id
        writeInt(out, 0); // subchunk 2 size (will be updated later)
    }

    private void updateWavHeader() throws IOException {
        wavOutputStream.getChannel().position(4);
        writeInt(wavOutputStream, 36 + audioLength);
        wavOutputStream.getChannel().position(40);
        writeInt(wavOutputStream, audioLength);
    }

    private void writeInt(FileOutputStream out, int value) throws IOException {
        out.write(value);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

    private void writeShort(FileOutputStream out, short value) throws IOException {
        out.write(value);
        out.write(value >> 8);
    }

    private void writeString(FileOutputStream out, String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            out.write(value.charAt(i));
        }
    }
}
