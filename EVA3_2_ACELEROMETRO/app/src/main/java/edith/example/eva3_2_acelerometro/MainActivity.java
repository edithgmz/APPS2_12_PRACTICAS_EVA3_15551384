package edith.example.eva3_2_acelerometro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView txtVwDatos;
    SensorManager manager;
    Sensor sAcel, sGiro, sRot;
    private int presicion;
    final int ACEL = 0;
    final int ROT = 1;
    final int GIRO = 2;
    float[][] datosSen = new float[3][3];
    private WindowManager mWindowManager;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtVwDatos = findViewById(R.id.txtVwDatos);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sAcel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sGiro = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sRot = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mWindowManager = getWindow().getWindowManager();
    }

    @Override protected void onResume() {
        super.onResume();
        /*if((sGiro == null)){
            return;
        } else if((sAcel == null)){
            return;
        }*/
        manager.registerListener(this, sAcel, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(this, sRot, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(this, sGiro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override public void onSensorChanged(SensorEvent event) {
        String sCade = "";
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            datosSen[ACEL][0] = event.values[0];
            datosSen[ACEL][1] = event.values[1];
            datosSen[ACEL][2] = event.values[2];
            //updateOrientation(datosSen);
        }
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            datosSen[ROT][0] = event.values[0];
            datosSen[ROT][1] = event.values[1];
            datosSen[ROT][2] = event.values[2];
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            datosSen[GIRO][0] = event.values[0];
            datosSen[GIRO][1] = event.values[1];
            datosSen[GIRO][2] = event.values[2];
        }
        sCade = sCade + "ACELEROMETRO:\n" +
                "X = " + datosSen[ACEL][1] + "\n" +
                "Y = " + datosSen[ACEL][2] + "\n" +
                "Z = " + datosSen[ACEL][0] + "\n";
        sCade = sCade + "\nROTATION VECTOR:\n" +
                "X = " + datosSen[ROT][0] + "\n" +
                "Y = " + datosSen[ROT][1] + "\n" +
                "Z = " + datosSen[ROT][2] + "\n";
        sCade = sCade + "\nGIROSCOPIO:\n" +
                "X = " + datosSen[ACEL][0] + "\n" +
                "Y = " + datosSen[ACEL][1] + "\n" +
                "Z = " + datosSen[ACEL][2] + "\n";
        txtVwDatos.setText(sCade);
    }

    private void updateOrientation(float[][] rotationVector) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector[ACEL]);
        String sCade = "X =        " + rotationVector[ACEL][0] + "\n" +
                       "Y =        " + rotationVector[ACEL][1] + "\n" +
                       "Z =        " + rotationVector[ACEL][2] + "\n";
        for(int i = 0; i < rotationMatrix.length; i++){
            sCade = sCade + "["+ rotationMatrix[i] + "]";
            if (((i + 1)%3)==0){
                sCade = sCade + "\n";
            }
        }
        final int worldAxisForDeviceAxisX;
        final int worldAxisForDeviceAxisY;

        // Remap the axes as if the device screen was the instrument panel,
        // and adjust the rotation matrix for the device orientation.
        switch (mWindowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
            default:
                worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                break;
            case Surface.ROTATION_90:
                worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                break;
            case Surface.ROTATION_180:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                break;
            case Surface.ROTATION_270:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                break;
        }
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
                                            worldAxisForDeviceAxisY, adjustedRotationMatrix);

        sCade = sCade + "\n";
        for(int i = 0; i < adjustedRotationMatrix.length; i++){
            sCade = sCade + "["+ adjustedRotationMatrix[i] + "]";
            if (((i + 1)%3)==0){
                sCade = sCade + "\n";
            }
        }

        // Transform rotation matrix into azimuth/pitch/roll
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        // Convert radians to degrees
        float pitch = orientation[1] * -57.2958f; //X
        float roll = orientation[2] * -57.2958f; //Y
        float yaw = orientation[0] * -57.2958f;  //Z

        sCade = sCade + "\n" +
                "X trans =  " + orientation[1] + "\n" +
                "Y trans =  " + orientation[2] + "\n" +
                "Z trans =  " + orientation[0] + "\n" +
                "\n" +
                "pitch (x) = " + roll + "\n" +
                "roll(y) = " + pitch + "\n" +
                "yaw (z)=   " + yaw;
        txtVwDatos.setText(sCade);
        //smAdminSensor.unregisterListener(this);
        //return;
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(presicion != accuracy){
            presicion = accuracy;
        }
    }

    @Override protected void onStop() {
        super.onStop();
        manager.unregisterListener(this);
    }
}
