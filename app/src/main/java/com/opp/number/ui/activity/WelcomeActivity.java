package com.opp.number.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.opp.number.R;
import com.opp.number.database.DatabaseConstant;
import com.opp.number.database.GameLevelDatabaseHelper;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "欢迎界面";

    private Button mRandomGame, mLevelGame, mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().setStatusBarColor(getColor(R.color.colorToolbar));
        } else {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Stetho.initializeWithDefaults(getApplicationContext());
        mRandomGame = findViewById(R.id.wel_bt_start_random_game);
        mRandomGame.setOnClickListener(this);
        mLevelGame = findViewById(R.id.wel_bt_start_level_game);
        mLevelGame.setOnClickListener(this);
        mSetting = findViewById(R.id.wel_bt_setting);
        mSetting.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wel_bt_start_random_game: {
                GameActivity.startAction(this, GameActivity.RANDOM_MODEL);
                break;
            }
            case R.id.wel_bt_start_level_game: {
                if (!checkReadPermission()){
                    Log.d(TAG, "onClick: 申请");
                    ActivityCompat.requestPermissions(WelcomeActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                }else {
                    GameLevelListActivity.startAction(this);
                }

                break;
            }
            case R.id.wel_bt_setting: {
                clearDatabase();
                Toast.makeText(this,"未添加功能",Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GameLevelListActivity.startAction(this);
                }else {
                    Toast.makeText(this,"决绝相关权限申请，无法开始关卡模式",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }

    private boolean checkLocationPermission(){
        return ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkReadPermission(){
        return ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String[] permission,int requestCode){
        ActivityCompat.requestPermissions(WelcomeActivity.this,
                permission, requestCode);
    }

    private void clearDatabase(){
        new GameLevelDatabaseHelper(this).getReadableDatabase().execSQL("DELETE FROM " + DatabaseConstant.GameLevelTable.TABLE_NAME);
    }
}
