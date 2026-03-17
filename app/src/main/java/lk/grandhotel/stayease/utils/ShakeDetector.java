package lk.grandhotel.stayease.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_G = 12.0f;
    private static final long  COOLDOWN_MS        = 1000L;

    public interface OnShakeListener {
        void onShake();
    }

    private final OnShakeListener listener;
    private long lastShakeTime = 0L;

    public ShakeDetector(OnShakeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
        float x = event.values[0], y = event.values[1], z = event.values[2];
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z) - 9.81f;
        if (magnitude > SHAKE_THRESHOLD_G) {
            long now = System.currentTimeMillis();
            if (now - lastShakeTime > COOLDOWN_MS) {
                lastShakeTime = now;
                if (listener != null) listener.onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}