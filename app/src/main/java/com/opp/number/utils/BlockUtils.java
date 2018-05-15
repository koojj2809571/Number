package com.opp.number.utils;

import android.graphics.Color;

import com.opp.number.bean.ConnectionSetting;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    private ConnectionSetting[] mResult;
    private int mSize;
    private List<Integer> mIndexes;

    public BlockUtils(int size) {
        mSize = size;
        mResult = new ConnectionSetting[mSize * mSize];
        for (int i = 0; i < mResult.length; i++) {
            mResult[i] = new ConnectionSetting();
            mResult[i].setPortNumber(0);
            mResult[i].setPort(false);
            mResult[i].setLineColor(0);
        }
        mIndexes = new ArrayList<>();
        for (int i = 0; i < mSize * mSize; i++) {
            mIndexes.add(i);
        }
    }

    private int getRandomStartIndex(int length) {
        return (int) (Math.random() * length);
    }

    private int getRandomNextIndex(Integer startIndex) {

        List<Integer> list = new ArrayList<>();

        Integer leftIndex;
        Integer rightIndex;
        if (startIndex % mSize == 0) {
            leftIndex = -1;
            rightIndex = startIndex + 1;
        } else if ((startIndex + 1) % mSize == 0) {
            leftIndex = startIndex - 1;
            rightIndex = -1;
        } else {
            leftIndex = startIndex - 1;
            rightIndex = startIndex + 1;
        }
        if (leftIndex >= 0 && leftIndex < mSize * mSize && mIndexes.contains(leftIndex)) {
            list.add(leftIndex);
        }
        if (rightIndex >= 0 && rightIndex < mSize * mSize && mIndexes.contains(rightIndex)) {
            list.add(rightIndex);
        }
        Integer topIndex = startIndex - mSize;
        if (topIndex >= 0 && topIndex < mSize * mSize && mIndexes.contains(topIndex)) {
            list.add(topIndex);
        }
        Integer bottomIndex = startIndex + mSize;
        if (bottomIndex >= 0 && bottomIndex < mSize * mSize && mIndexes.contains(bottomIndex)) {
            list.add(bottomIndex);
        }

        if (list.size() < 1) {
            return -1;
        }

        return list.get((int) (Math.random() * list.size()));
    }

    private void setPort(int index, int pathLength) {
        mResult[index].setPort(true);
        mResult[index].setLineColor(Color.BLACK);
        mResult[index].setPortNumber(pathLength);
    }

    private boolean isRandomFinished() {
        return mIndexes.size() == 0;
    }

    private boolean isBlocksEnough(int maxPathLength, int maxPathCount) {
        return maxPathCount * maxPathLength > mIndexes.size() * mIndexes.size()
                || maxPathLength > mIndexes.size() * mIndexes.size();
    }

    private boolean getRandomPath(int pathLength, int pathCount) {
        int abnormalCycleTimes = 0;
        //循环获取参数中设置的最大路径
        for (int i = 0; i < pathCount; i++) {
            int startIndex = getRandomStartIndex(mIndexes.size());
            Integer start = mIndexes.get(startIndex);
            mIndexes.remove(startIndex);
            setPort(start, pathLength);
            ConnectionSetting setting = mResult[start];
            for (int j = 0; j < pathLength; j++) {
                if (j == pathLength - 1) {
                    setPort(start, pathLength);
                    break;
                }
                Integer next = getRandomNextIndex(start);
                int nextIndex = mIndexes.indexOf(next);
                if (isRandomFinished()) {
                    return true;
                }
                if (next < 0) {
                    setting.setPortNumber(j + 1);
                    setPort(start, j + 1);
                    i = i - 1;
                    abnormalCycleTimes++;
                    break;
                }
                mIndexes.remove(nextIndex);
                start = next;
                startIndex = nextIndex;
                if (isRandomFinished()) {
                    return true;
                }
            }
            if (abnormalCycleTimes > 20) {
                abnormalCycleTimes = 0;
                break;
            }
            if (isRandomFinished()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取随机的游戏配置
     *
     * @param maxPathLength 游戏中最长一条路径经过的方块数量。
     * @param maxPathCount  游戏中最长路径数量。
     * @return 返回随机游戏配置，既游戏初始化数组。
     */
    public ConnectionSetting[] getRandomConnectionSetting(int maxPathLength, int maxPathCount) {
        if (isBlocksEnough(maxPathLength, maxPathCount)) {
            return null;
        }

        while (true) {
            if (maxPathLength <= 0) {
                maxPathLength = 1;
            }
            if (maxPathCount * maxPathLength > mIndexes.size()) {
                maxPathCount = maxPathCount - 1;
            }

            if (getRandomPath(maxPathLength, maxPathCount)) {
                break;
            }

            //下面判断中加减的int值应该使其有随机性
            if (maxPathLength <= 0) {
                maxPathLength = 1;
            } else {
                if (mIndexes.size() / maxPathLength > 10) {
                    maxPathLength = maxPathLength - 2 < 0 ? maxPathLength : maxPathLength - 2;
                    maxPathCount = (maxPathCount + 2) * maxPathLength > mIndexes.size() ? 5 : maxPathCount + 2;
                } else if (mIndexes.size() / maxPathLength > 5 && mIndexes.size() / maxPathLength <= 10) {
                    maxPathLength = maxPathLength - 4 < 0 ? maxPathLength : maxPathLength - 4;
                    maxPathCount = (maxPathCount + 2) * maxPathLength > mIndexes.size() ? 2 : maxPathCount + 2;
                } else if (mIndexes.size() / maxPathLength >= 2 && mIndexes.size() / maxPathLength < 5) {
                    maxPathLength = maxPathLength - 4 < 0 ? maxPathLength : maxPathLength - 4;
                    maxPathCount = (maxPathCount + 2) * maxPathLength > mIndexes.size() ? maxPathCount : maxPathCount + 2;
                } else if (mIndexes.size() / maxPathLength < 2) {
                    maxPathLength = maxPathLength - 4 < 0 ? 1 : maxPathLength - 4;
                    maxPathCount = (maxPathCount + 2) * maxPathLength > mIndexes.size() ? maxPathCount : maxPathCount + 2;
                }
            }

        }

        return mResult;
    }

    /**
     * 获取一个固定的初始化游戏设置
     * @return 返回一个4X4的初始游戏设置
     */
    public static ConnectionSetting[] getEasySettings(){
        int models = 4;
        int length = models * models;
        ConnectionSetting[] settings = new ConnectionSetting[length];
        for (int i = 0; i < settings.length; i++) {
            settings[i] = new ConnectionSetting();
            if (i == 0 || i == 4 || i == 11 || i == 15) {
                settings[i].setPort(true);
                settings[i].setPortNumber(4);
                settings[i].setLineColor(Color.BLUE);
            } else if (i == 3 || i == 7 || i == 8 || i == 12) {
                settings[i].setPort(true);
                settings[i].setPortNumber(4);
                settings[i].setLineColor(Color.GREEN);
            } else {
                settings[i].setPort(false);
                settings[i].setPortNumber(0);
                settings[i].setLineColor(0);
            }
        }
        return settings;
    }

    /**
     * 获取一个固定的初始化游戏设置
     * @return 返回一个20X20的初始游戏设置
     */
    public static ConnectionSetting[] getHardSettings(){
        int models = 20;
        int length = models * models;
        ConnectionSetting[] settings = new ConnectionSetting[length];
        for (int i = 0; i < settings.length; i++) {
            settings[i] = new ConnectionSetting();
            if (i % models == 0 || (i + 1) % models == 0) {
                settings[i].setPort(true);
                settings[i].setPortNumber(20);
                settings[i].setLineColor(Color.BLACK);
            } else {
                settings[i].setPort(false);
                settings[i].setPortNumber(0);
                settings[i].setLineColor(0);
            }
        }
        return settings;
    }

}
