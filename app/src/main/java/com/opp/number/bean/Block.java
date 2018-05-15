package com.opp.number.bean;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP on 18.3.26.
 */

public class Block implements Cloneable, Parcelable {
    public static final int ORIENTATION_NONE = 100;
    public static final int ORIENTATION_LEFT = 101;
    public static final int ORIENTATION_UP = 102;
    public static final int ORIENTATION_RIGHT = 103;
    public static final int ORIENTATION_BOTTOM = 104;
    public static final int UNCONNECTED_BLOCK = 0;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private Block block = this;

    //存储方块坐标参数
    private RectF rectF;

//    //方块中心点x坐标
//    private float cx;
//
//    //方块中心点Y坐标
//    private float cy;

    //是否为连线起点
    private boolean isPort;

    //方块序号
    private int index;

    //方块颜色
    private Color blockColor;

    //如果是连线起点，起点数字
    private int portNumber;

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = isPort ? portNumber : 0;
        if (portNumber == 1){
            this.isConnecting = true;
            this.isConnected = true;
        }
    }

    //当前连接中可移动总步数及两个端点数字
    private int totalMoveCount;

    public int getTotalMoveCount() {
        return totalMoveCount;
    }

    public void setTotalMoveCount(int totalMoveCount) {
        this.totalMoveCount = isPort ? portNumber : totalMoveCount;
    }

    //如果是起点，起点连接线的颜色
    private int lineColor;

    //如果不是起点，则根据起点数字计算还可以走几步
    private int blockCount;

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = isPort ? 1 : blockCount;
    }

    //如果不是起点,当前连接中上一个方块
    private Block lastBlock;
    public Block getLastBlock(){
        return lastBlock;
    }

    //如果不是起点,当前连接中上一个方块
    private Block nextBlock;
    public Block getNextBlock(){
        return nextBlock;
    }

    //连接过来的其中一个连接方向
    private int connectLast = ORIENTATION_NONE;

    //连接过来的其中一个连接方向
    private int connectNext = ORIENTATION_NONE;

    //左边方块
    private Block leftBlock;

    //上边方块
    private Block upBlock;

    //右边方块
    private Block rightBlock;

    //下边方块
    private Block bottomBlock;

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
//        cx = rectF.centerX();
//        cy = rectF.centerY();
    }

//    public float getCx() {
//        return cx;
//    }
//
//    public float getCy() {
//        return cy;
//    }

    public boolean isPort() {
        return isPort;
    }

    public void setPort(boolean port) {
        isPort = port;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getConnectLast() {
        return connectLast;
    }

    public void setConnectLast(int connectLast) {
        this.connectLast = isPort ? ORIENTATION_NONE : connectLast;
        switch (connectLast){
            case ORIENTATION_LEFT:
                lastBlock =  getLeftBlock();
                break;
            case ORIENTATION_UP:
                lastBlock =  getUpBlock();
                break;
            case ORIENTATION_RIGHT:
                lastBlock =  getRightBlock();
                break;
            case ORIENTATION_BOTTOM:
                lastBlock =  getBottomBlock();
                break;
            default:
                lastBlock =  null;
                break;
        }
    }

    public int getConnectNext() {
        return connectNext;
    }

    public void setConnectNext(int connectNext) {
        this.connectNext = connectNext;
        switch (connectNext){
            case ORIENTATION_LEFT:
                nextBlock =  getLeftBlock();
                break;
            case ORIENTATION_UP:
                nextBlock =  getUpBlock();
                break;
            case ORIENTATION_RIGHT:
                nextBlock =  getRightBlock();
                break;
            case ORIENTATION_BOTTOM:
                nextBlock =  getBottomBlock();
                break;
            default:
                nextBlock =  null;
                break;
        }
    }

    public Block getLeftBlock() {
        return leftBlock;
    }

    public Block getUpBlock() {
        return upBlock;
    }

    public Block getRightBlock() {
        return rightBlock;
    }

    public Block getBottomBlock() {
        return bottomBlock;
    }

    public void setLeftBlock(Block leftBlock) {
        this.leftBlock = leftBlock;
    }

    public void setUpBlock(Block upBlock) {
        this.upBlock = upBlock;
    }

    public void setRightBlock(Block rightBlock) {
        this.rightBlock = rightBlock;
    }

    public void setBottomBlock(Block bottomBlock) {
        this.bottomBlock = bottomBlock;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int color) {
        this.lineColor = color;
    }


    public Color getBlockColor() {
        return blockColor;
    }

    public void setBlockColor(Color blockColor) {
        this.blockColor = blockColor;
    }

    private boolean isConnecting = false;

    public boolean isConnecting(){
        return isConnecting;
    }

    public void setConnecting(boolean connecting){
        this.isConnecting = connecting;
    }

    private boolean isConnected = false;

    public boolean isConnected(){
        return isConnected;
    }

    public void setConnected(boolean connected){
        this.isConnected = connected;
        if (lastBlock != null){
            lastBlock.setConnected(connected);
        }
    }

    public void initConnectionBlockLast() {
        if (isPort) {
            setConnecting(false);
            setConnected(false);
            setTotalMoveCount(0);
            setBlockCount(UNCONNECTED_BLOCK);
            if (lastBlock != null){
                lastBlock.initConnectionBlockLast();
            }
            setConnectNext(Block.ORIENTATION_NONE);
            setConnectLast(Block.ORIENTATION_NONE);
        } else {
            setLineColor(0);
            setConnecting(false);
            setConnected(false);
            setTotalMoveCount(0);
            setBlockCount(UNCONNECTED_BLOCK);
            if (lastBlock != null){
                lastBlock.initConnectionBlockLast();
            }
            setConnectNext(Block.ORIENTATION_NONE);
            setConnectLast(Block.ORIENTATION_NONE);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.block, flags);
        dest.writeParcelable(this.rectF, flags);
        dest.writeByte(this.isPort ? (byte) 1 : (byte) 0);
        dest.writeInt(this.index);
        dest.writeParcelable((Parcelable) this.blockColor, flags);
        dest.writeInt(this.portNumber);
        dest.writeInt(this.totalMoveCount);
        dest.writeInt(this.lineColor);
        dest.writeInt(this.blockCount);
        dest.writeParcelable(this.lastBlock, flags);
        dest.writeParcelable(this.nextBlock, flags);
        dest.writeInt(this.connectLast);
        dest.writeInt(this.connectNext);
        dest.writeParcelable(this.leftBlock, flags);
        dest.writeParcelable(this.upBlock, flags);
        dest.writeParcelable(this.rightBlock, flags);
        dest.writeParcelable(this.bottomBlock, flags);
        dest.writeByte(this.isConnecting ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isConnected ? (byte) 1 : (byte) 0);
    }

    public Block() {
    }

    protected Block(Parcel in) {
        this.block = in.readParcelable(Block.class.getClassLoader());
        this.rectF = in.readParcelable(RectF.class.getClassLoader());
        this.isPort = in.readByte() != 0;
        this.index = in.readInt();
        this.blockColor = in.readParcelable(Color.class.getClassLoader());
        this.portNumber = in.readInt();
        this.totalMoveCount = in.readInt();
        this.lineColor = in.readInt();
        this.blockCount = in.readInt();
        this.lastBlock = in.readParcelable(Block.class.getClassLoader());
        this.nextBlock = in.readParcelable(Block.class.getClassLoader());
        this.connectLast = in.readInt();
        this.connectNext = in.readInt();
        this.leftBlock = in.readParcelable(Block.class.getClassLoader());
        this.upBlock = in.readParcelable(Block.class.getClassLoader());
        this.rightBlock = in.readParcelable(Block.class.getClassLoader());
        this.bottomBlock = in.readParcelable(Block.class.getClassLoader());
        this.isConnecting = in.readByte() != 0;
        this.isConnected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Block> CREATOR = new Parcelable.Creator<Block>() {
        @Override
        public Block createFromParcel(Parcel source) {
            return new Block(source);
        }

        @Override
        public Block[] newArray(int size) {
            return new Block[size];
        }
    };
}
