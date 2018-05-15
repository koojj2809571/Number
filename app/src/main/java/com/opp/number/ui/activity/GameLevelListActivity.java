package com.opp.number.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.opp.number.R;
import com.opp.number.bean.LevelSetting;
import com.opp.number.database.DatabaseConstant.GameLevelTable.Cols;
import com.opp.number.database.GameLevelDatabaseHelper;
import com.opp.number.utils.DatabaseUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.widget.Toast.LENGTH_SHORT;


public class GameLevelListActivity extends AppCompatActivity {

    private static final String TAG = "关卡列表页面";
    private static final String APP_ID = "b3f421503ac1eeeea18f1944ed4eb845";

    public RecyclerView mRecyclerView;
    public ProgressBar mProgressBar;
    public List<LevelSetting> mDatabaseSettings;
    public List<LevelSetting> mBmobSettings;
    public List<LevelSetting> mSettings;
    private Handler mHandler;
    private GameLevelDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;
    private GameLevelAdapter mGameLevelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_level_list);
        Bmob.initialize(this, APP_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().setStatusBarColor(getColor(R.color.colorToolbar));
        } else {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        initDatabase();
        initView();
        initListData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("HandlerLeak")
    private void initView() {
        mProgressBar = findViewById(R.id.list_progressbar);
        mRecyclerView = findViewById(R.id.list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        Log.d(TAG, "handleMessage: 处理1");
                        mSettings = mDatabaseSettings;
                        UpdateUI();
                        mRecyclerView.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 2: {
                        mProgressBar.setVisibility(View.GONE);
                        mSettings = mBmobSettings;
                        UpdateUI();
                        mRecyclerView.setVisibility(View.VISIBLE);
                        for (LevelSetting setting : mBmobSettings) {
                            try {
                                DatabaseUtils.insertData(setting,mDatabase);
                            } catch (JSONException jsone) {
                                jsone.printStackTrace();
                            }
                        }
                        break;
                    }
                    case 3:{
                        mProgressBar.setVisibility(View.GONE);
                        mGameLevelAdapter.notifyDataSetChanged();
                    }
                    case 4:{
                        Toast.makeText(GameLevelListActivity.this,"暂无跟新", LENGTH_SHORT).show();
                    }
                    default:
                        break;
                }
            }
        };
    }

    private void initDatabase() {
        mDatabaseHelper = new GameLevelDatabaseHelper(this);
        mDatabase = mDatabaseHelper.getReadableDatabase();
    }

    private void initListData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mDatabaseSettings = DatabaseUtils.getInitDataFromDatabase(mDatabase);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mDatabaseSettings.size() == 0) {
                    Log.d(TAG, "run: 无法获取数据设置");
                    getInitDataFromBmob();
                } else {
                    Message msg1 = new Message();
                    msg1.what = 1;
                    mHandler.sendMessage(msg1);
                    getInitDataFromBmob();
                    int count = mBmobSettings.size() - mDatabaseSettings.size();
                    if (count > 0){
                        for (int i = count;i > 0;i--){
                            LevelSetting ls = mBmobSettings.get(mBmobSettings.size() - i);
                            mSettings.add(ls);
                            try {
                                DatabaseUtils.insertData(ls,mDatabase);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg3 = new Message();
                        msg3.what = 3;
                        mHandler.sendMessage(msg3);
                    }else if (count == 0){
                        Message msg4 = new Message();
                        msg4.what = 4;
                        mHandler.sendMessage(msg4);
                    }
                }
            }
        }).start();
    }

    private void getInitDataFromBmob() {
        mBmobSettings = new ArrayList<>();
        BmobQuery<LevelSetting> query = new BmobQuery<>();
        query.order(Cols.ORDER_NUMBER);
        query.findObjects(new FindListener<LevelSetting>() {
            @Override
            public void done(List<LevelSetting> list, BmobException e) {
                if (e == null) {
                    mBmobSettings = list;
                    Message msg2 = new Message();
                    msg2.what = 2;
                    mHandler.sendMessage(msg2);
                } else {
                    Log.e(TAG, "done: " + e.toString());
                }
            }
        });
    }

    public static void startAction(Context context) {
        Intent intent = new Intent(context, GameLevelListActivity.class);
        context.startActivity(intent);
    }

    private void UpdateUI() {
        mGameLevelAdapter = new GameLevelAdapter(mSettings);
        mRecyclerView.setAdapter(mGameLevelAdapter);
    }

    private class GameLevelAdapter extends RecyclerView.Adapter<GameLevelVIewHolder> {

        GameLevelAdapter(List<LevelSetting> settings) {
            mSettings = settings;
        }

        @Override
        public GameLevelVIewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_level, parent, false);
            final GameLevelVIewHolder holder = new GameLevelVIewHolder(view);
            holder.levelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    GameActivity.startAction(GameLevelListActivity.this,mSettings.get(position).getOrder());
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(GameLevelVIewHolder holder, int position) {
            LevelSetting setting = mSettings.get(position);
            holder.levelName.setText(setting.getName());
        }

        @Override
        public int getItemCount() {
            return mSettings.size();
        }
    }

    private class GameLevelVIewHolder extends RecyclerView.ViewHolder {

        private View levelView;
        private TextView levelName;
        private ImageView levelImage;

        GameLevelVIewHolder(View itemView) {
            super(itemView);
            levelView = itemView;
            levelName = levelView.findViewById(R.id.list_item_level_name);
            levelImage = levelView.findViewById(R.id.list_item_level_is_new_image_view);
        }
    }
}
