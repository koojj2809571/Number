package com.opp.number.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.opp.number.R;
import com.opp.number.database.GameLevelDatabaseHelper;
import com.opp.number.utils.BlockUtils;
import com.opp.number.bean.Block;
import com.opp.number.utils.DatabaseUtils;
import com.opp.number.widget.BlockView;
import com.opp.number.widget.BlockView.*;
import com.opp.number.bean.ConnectionSetting;

import org.json.JSONException;

/*BUG：如果在还未用尽步数的情况下，
       非常快的滑动到与最后一次移动到的方块不相邻的方块会出现不按照滑动路径连接的情况。

  例如：0号方块为4的端点，从0号开始滑动到2号还剩一步，此时非常快的滑动到10号方块，
        继续向右滑动当前路径从11号方块开始连接。可能是在给3号方块赋值并重绘前，因为产生了快速滑动到10号方块的动作，赋值并重绘作用在了11号方块上
 */
public class GameActivity extends AppCompatActivity implements OnBlockClickListener,OnGameCompleteListener {
    private static final String TAG = "游戏界面";
    public static final String MODELS = "models";
    public static final int RANDOM_MODEL = 101;
    private TextView mTextView;
    private BlockView mBlockView;

    private GameLevelDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;
    private ConnectionSetting[] mInitSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getType() == 16){
            setContentView(R.layout.activity_game_4);
        }else if (getType() == 400){
            setContentView(R.layout.activity_game_20);
        }else if (getType() == 900){
            setContentView(R.layout.activity_game_30);
        }else if (getType() == 1600){
            setContentView(R.layout.activity_game_40);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().setStatusBarColor(getColor(R.color.colorToolbar));
        }else {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mTextView = findViewById(R.id.text_view);
        mBlockView = findViewById(R.id.block_view);
        mBlockView.initConnectionSettings(mInitSettings);
        mBlockView.setOnBlockClickListener(this);
        mBlockView.setOnGameCompleteListener(this);
    }

    public static void startAction(Context context,int data){
        Intent intent = new Intent(context,GameActivity.class);
        intent.putExtra(MODELS,data);
        context.startActivity(intent);
    }

    private int getType(){
        int type = getIntent().getIntExtra(MODELS,-1);
        if (type == RANDOM_MODEL){
            BlockUtils utils = new BlockUtils(30);
            mInitSettings = utils.getRandomConnectionSetting(15,6);
            return 900;
        }else {
            mDatabaseHelper = new GameLevelDatabaseHelper(this);
            mDatabase = mDatabaseHelper.getReadableDatabase();
            try {
                mInitSettings = DatabaseUtils.queryItem(mDatabase, type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mInitSettings.length;
    }

    @Override
    public void onBlockClick(Block block) {
//        mTextView.setTextColor(Color.BLACK);
//        mTextView.setText(getBlockInfo(block));
    }

    @Override
    public void onGamePlaying(int progress) {
        mTextView.setTextColor(Color.BLACK);
        String result = "已完成：" + (progress * 100/mBlockView.getSize()) + "%";
        mTextView.setText(result);
    }

    @Override
    public void onGameComplete() {
        mTextView.setTextColor(Color.RED);
        mTextView.setText("~完成~");
    }

}
