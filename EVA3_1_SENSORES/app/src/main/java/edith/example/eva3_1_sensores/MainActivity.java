package edith.example.eva3_1_sensores;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView lstVwSensores;
    TextView txtVwDatos;
    String[] aSensores; //Para la lista
    List<Sensor> lstSensores; //Lo que devuelve el SensorManager
    SensorManager sensores;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstVwSensores = findViewById(R.id.lstVwSensores);
        txtVwDatos = findViewById(R.id.txtVwDatos);

        sensores = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lstSensores = sensores.getSensorList(Sensor.TYPE_ALL);
        aSensores = new String[lstSensores.size()];
        int i = 0;
        for(Sensor sensor:lstSensores){
            aSensores[i] = sensor.getName();
            i++;
        }
        lstVwSensores.setAdapter(new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                aSensores
        ));
        lstVwSensores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sensor sensor = lstSensores.get(position);
                txtVwDatos.setText("Rango Máx: " + sensor.getMaximumRange() + "\n" + "Demora: " + sensor.getMaxDelay() + "\n" + "Consumo Energía: " +
                                   sensor.getPower() + "\n" + "Precisión: " + sensor.getResolution());
            }
        });
    }
}
