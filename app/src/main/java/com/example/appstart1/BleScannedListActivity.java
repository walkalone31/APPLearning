package com.example.appstart1;

/* to replace startActivityForResult */

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.Toast;

import com.example.appstart1.RecyclerViewAdapter;
import com.example.appstart1.ScannedData;
import com.example.appstart1.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class BleScannedListActivity extends AppCompatActivity {
    /* for ble test start */
    final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    private static final String TAG = MainActivity.class.getSimpleName() + "My";
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isScanning = false;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner bluetoothLeScanner;
    ArrayList<ScannedData> findDevice = new ArrayList<>();
    RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(this);;

    /* You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed*/
    /* to replace startActivityForResult */
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == REQUEST_ENABLE_BT) {
                    // There are no request codes
                    /* Intent data = result.getData(); */
                }
            });

    /* for ble test end */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.printf("- BleScannedListActivity (onCreate)!!!!!!!!!\n");
        setContentView(R.layout.ble_scanner_list);
        /*權限相關認證*/
        useAndCheckBluetooth();
        System.out.printf("- BleScannedListActivity (end useAndCheckBluetooth())\n");
        /*初始藍牙掃描及掃描開關之相關功能*/
        bluetoothScan();
        System.out.printf("- BleScannedListActivity (end bluetoothScan())\n");
        /*取得欲連線之裝置後跳轉頁面*/
        mAdapter.OnItemClick(itemClick);
    }

    public void useAndCheckBluetooth() {

        //System.out.printf("useAndCheckBluetooth\n");
        //System.out.printf("Build.VERSION.SDK_INT = %d\n", Build.VERSION.SDK_INT);
        //System.out.printf("Build.VERSION_CODES.M = %d\n", Build.VERSION_CODES.M);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            System.out.printf("- BleScannedListActivity (Build.VERSION.SDK_INT)\n");

            /* 確認是否已開啟取得手機位置功能以及權限 */
            int hasGone = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasGone != PackageManager.PERMISSION_GRANTED) {
                System.out.printf("- BleScannedListActivity (PackageManager check)\n");
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            }
            /* 確認手機是否支援藍牙BLE */
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                System.out.printf("- BleScannedListActivity (hasSystemFeature check)\n");
                Toast.makeText(this, "Not support Bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            }
            /* 開啟藍芽適配器 */
            if (!mBluetoothAdapter.isEnabled()) {
                System.out.printf("- BleScannedListActivity (mBluetoothAdapter check)\n");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                /* startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); */
                someActivityResultLauncher.launch(enableBtIntent);
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                //request location permission for bluetooth scanning for android API 23 and above
                System.out.printf("- BleScannedListActivity (checkSelfPermission useAndCheckBluetooth)...........\n");

                //ActivityCompat.requestPermissions(this, ANDROID_12_BLE_PERMISSIONS, 0);
                ActivityCompat.requestPermissions(this, BLE_PERMISSIONS, 0);


                return;
            }
        } else finish();
    }

    /* 初始藍牙掃描及掃描開關之相關功能 */
    //@SuppressLint("MissingPermission")
    private void bluetoothScan() {
        /* 啟用藍牙適配器 */
        System.out.printf("- BleScannedListActivity (in bluetoothScan)\n");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        /* 開始掃描 */
        //mBluetoothAdapter.startLeScan(mLeScanCallback);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.printf("- BleScannedListActivity (checkSelfPermission bluetoothScan)...........\n");

            ActivityCompat.requestPermissions(this, BLE_PERMISSIONS, 0);

            return;
        }
        bluetoothLeScanner.startScan(scanCallback);
        isScanning = true;
        /* 設置Recyclerview列表 */
        RecyclerView recyclerView = findViewById(R.id.recyclerView_ScannedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);
        /* 製作停止/開始掃描的按鈕 */
        final Button btScan = findViewById(R.id.button_Scan);
        btScan.setOnClickListener((v) -> {
            if (isScanning) {
                /* 關閉掃描 */
                System.out.printf("- BleScannedListActivity (stopScan-開始掃描)\n");
                isScanning = false;
                btScan.setText("開始掃描");
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                bluetoothLeScanner.stopScan(scanCallback);
            } else {
                /* 開啟掃描 */
                System.out.printf("- BleScannedListActivity (startScan-停止掃描)\n");
                isScanning = true;
                btScan.setText("停止掃描");
                findDevice.clear();
                //mBluetoothAdapter.startLeScan(mLeScanCallback);
                bluetoothLeScanner.startScan(scanCallback);
                mAdapter.clearDevice();
            }
        });
    }

    //@SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        //System.out.printf("onStart()\n");
        final Button btScan = findViewById(R.id.button_Scan);
        isScanning = true;
        btScan.setText("停止掃描");
        findDevice.clear();
        //mBluetoothAdapter.startLeScan(mLeScanCallback);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //request location permission for bluetooth scanning for android API 23 and above
            System.out.printf("- BleScannedListActivity (checkSelfPermission onStart)...........\n");
            ActivityCompat.requestPermissions(this, BLE_PERMISSIONS, 0);

            return;
        }
        bluetoothLeScanner.startScan(scanCallback);
        mAdapter.clearDevice();
    }

    /* 避免跳轉後掃描程序係續浪費效能，因此離開頁面後即停止掃描 */
    //@SuppressLint("MissingPermission")
    @Override
    protected void onStop() {
        super.onStop();
        //System.out.printf("onStop()\n");
        final Button btScan = findViewById(R.id.button_Scan);
        /* 關閉掃描 */
        isScanning = false;
        btScan.setText("開始掃描");
        //mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.printf("- BleScannedListActivity (checkSelfPermission onStop)...........\n");
            return;
        }
        bluetoothLeScanner.stopScan(scanCallback);
    }

    /* 顯示掃描到物件2 */
    private ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();

            if (device.getName()!= null) {
            //if (device.getName().equals("oCare R100")) {
                /* 將搜尋到的裝置加入陣列 */
                //System.out.printf("- BleScannedListActivity (device.getName() = %s)\n", device.getName());
                findDevice.add(new ScannedData(device.getName()
                        , String.valueOf(result.getRssi())
                        , byteArrayToHexStr(result.getScanRecord().getBytes())
                        , device.getAddress()));
                /* 將陣列中重複Address的裝置濾除，並使之成為最新數據*/
                ArrayList newList = getSingle(findDevice);
                runOnUiThread(() -> {
                    /* 將陣列送到RecyclerView列表中 */
                    mAdapter.addDevice(newList);
                });
            }
        }
    };

    /* 顯示掃描到物件1 */
/*    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            new Thread(()->{
                *//* 如果裝置沒有名字，就不顯示 *//*
                if (device.getName()!= null){
                    *//* 將搜尋到的裝置加入陣列 *//*
                    findDevice.add(new ScannedData(device.getName()
                            , String.valueOf(rssi)
                            , byteArrayToHexStr(scanRecord)
                            , device.getAddress()));
                    *//* 將陣列中重複Address的裝置濾除，並使之成為最新數據*//*
                    ArrayList newList = getSingle(findDevice);
                    runOnUiThread(()->{
                        *//* 將陣列送到RecyclerView列表中 *//*
                        mAdapter.addDevice(newList);
                    });
                }
            }).start();
        }
    };*/

    /* 濾除重複的藍牙裝置(以Address判定) */
    private ArrayList getSingle(ArrayList list) {
        ArrayList tempList = new ArrayList<>();
        try {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                } else {
                    tempList.set(getIndex(tempList, obj), obj);
                }
            }
            return tempList;
        } catch (ConcurrentModificationException e) {
            return tempList;
        }
    }

    /* 以Address篩選陣列->抓出該值在陣列的哪處 */
    private int getIndex(ArrayList temp, Object obj) {
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).toString().contains(obj.toString())) {
                return i;
            }
        }
        return -1;
    }
    /* Byte轉16進字串工具 */
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(byteArray.length * 2);
        for (byte aData : byteArray) {
            hex.append(String.format("%02X", aData));
        }
        String gethex = hex.toString();
        return gethex;
    }

    /*取得欲連線之裝置後跳轉頁面*/
    public RecyclerViewAdapter.OnItemClick itemClick = new RecyclerViewAdapter.OnItemClick() {
        @Override
        public void onItemClick(ScannedData selectedDevice) {
            System.out.printf("- BleScannedListActivity (onItemClick............)\n");
            Intent intent = new Intent(BleScannedListActivity.this, DeviceInfoActivity.class);
            intent.putExtra(DeviceInfoActivity.INTENT_KEY, selectedDevice);
            startActivity(intent);
        }
    };


}
