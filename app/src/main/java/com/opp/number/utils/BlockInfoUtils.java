package com.opp.number.utils;

import com.opp.number.bean.Block;

public class BlockInfoUtils {

    public static String getBlockInfo(Block block){
        int leftIndex;
        int topIndex;
        int rightIndex;
        int bottomIndex;

        if (block.getLeftBlock() == null) {
            leftIndex = -1;
        } else {
            leftIndex = block.getLeftBlock().getIndex();
        }

        if (block.getUpBlock() == null) {
            topIndex = -1;
        } else {
            topIndex = block.getUpBlock().getIndex();
        }

        if (block.getRightBlock() == null) {
            rightIndex = -1;
        } else {
            rightIndex = block.getRightBlock().getIndex();
        }

        if (block.getBottomBlock() == null) {
            bottomIndex = -1;
        } else {
            bottomIndex = block.getBottomBlock().getIndex();
        }

        boolean connected = block.isConnected();
        boolean connecting = block.isConnecting();
        int color = block.getLineColor();
        int count = block.getBlockCount();
        int total = block.getTotalMoveCount();
        String last = "";
        String next = "";
        switch (block.getConnectNext()) {
            case Block.ORIENTATION_LEFT:
                next = "左";
                break;
            case Block.ORIENTATION_UP:
                next = "上";
                break;
            case Block.ORIENTATION_RIGHT:
                next = "右";
                break;
            case Block.ORIENTATION_BOTTOM:
                next = "下";
                break;
            default:
                next = "暂无";
                break;
        }
        switch (block.getConnectLast()) {
            case Block.ORIENTATION_LEFT:
                last = "左";
                break;
            case Block.ORIENTATION_UP:
                last = "上";
                break;
            case Block.ORIENTATION_RIGHT:
                last = "右";
                break;
            case Block.ORIENTATION_BOTTOM:
                last = "下";
                break;
            default:
                last = "暂无";
                break;
        }
        String text = "点击了：" + block.getIndex() + " 号方块！！" +
                "左上右下方块分别为 :" + leftIndex + " " + topIndex + " " + rightIndex + " " + bottomIndex +
                "；connected：" + connected + "，connecting：" + connecting + "，颜色是：" + color +
                "，已经移动步数：" + count + "，总可移动步数：" + total + "，上一个：" + last + "，下一个：" + next;
        return text;
    }
}
