package com.likandr.minispy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    boolean serviceON = false;

    List<String> listForShow = new ArrayList<>();
    ArrayAdapter<String> mAdapter;

    ListView lvProcesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && USUtils.getUsageStatsList(this).isEmpty()){
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
        }

        final Button btnServiceManager = (Button) findViewById(R.id.service_indicator);
        Button btnClearLog = (Button) findViewById(R.id.clear_log);
        lvProcesses = (ListView) findViewById(R.id.id_process_listview);

        if(isMyServiceRunning()) {
            serviceON = !serviceON;
            btnServiceManager.setText("Service ON");
        }

        btnServiceManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceON = !serviceON;
                if (serviceON) {
                    btnServiceManager.setText("Service ON");
                    startService(new Intent(getApplicationContext(), LogService.class));
                } else {
                    btnServiceManager.setText("Service OFF");
                    stopService(new Intent(getApplicationContext(), LogService.class));
                }
            }
        });
        btnClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listForShow.size() > 0) {
                    listForShow.clear();
                    SharedPreferencesUtils.clearList();
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Нечего удалять", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listForShow = SharedPreferencesUtils.getProcessList();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listForShow);
        lvProcesses.setAdapter(mAdapter);
    }

    public boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LogService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
