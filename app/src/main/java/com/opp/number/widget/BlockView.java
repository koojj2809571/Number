package com.opp.number.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.opp.number.R;
import com.opp.number.bean.Block;
import com.opp.number.bean.ConnectionSetting;

/**
 * Created by HP on 18.3.21.
 */

public class BlockView extends View {
    private static final String TAG = "&*测试测试*&";

    /*变量及字段------begin------*/
    private Context mContext;

    private OnBlockClickListener mOnBlockClickListener;
    private OnGameCompleteListener mOnGameCompleteListener;

    private Paint mBlockPaint;
    private Paint mColorLinePaint;
    private Paint mBorderPaint;
    private Paint mBackPaint;

    private Block mBlock;
    private Block[] mBlocks;
    private ConnectionSetting[] mSettings;

    private RectF mOutBorder;
    private int mCenterX;
    private int mCenterY;
    private float mViewWidthAndHeight;
    private float mFirstRecFLeft;
    private float mFirstRecFTop;
    private float mLayoutLeft;
    private float mLayoutTop;
    private float mLayoutRight;
    private float mLayoutBottom;
    private float mBlockWidthAndHeight;

    //控件属性
    private boolean mCanScroll;
    private boolean mCanScrollVertical;
    private boolean mCanScrollHorizontal;
    private int mLineNumber;
    private int mSize;
    private int mCounts;
    private float mHalfBlockWidth;
    private int mBackColor;
    private int mBlockDefaultColor;
    private int mBlockColor;
    private float mBlockInterval;
    private int connectedBlock;

    /*变量及字段------end------*/

    /*构造器------begin------*/

    public BlockView(Context context) {
        this(context, null);
    }

    public BlockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(context, attrs);
        init();

    }

    /*构造器------end------*/

    /*属性、视图初始化------begin------*/

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BlockNumberView);
        mLineNumber = ta.getInteger(R.styleable.BlockNumberView_lineNumber, 2);
        if (mSettings != null) {
            mLineNumber = (int) Math.sqrt(mSettings.length);
        }
        if (mLineNumber < 2) {
            mLineNumber = 2;
        } else if (mLineNumber % 2 != 0) {
            mLineNumber = mLineNumber + 1;
        }

        mSize = mLineNumber * mLineNumber;

        mCounts = 0;

        mBackColor = ta.getColor(R.styleable.BlockNumberView_blockBackColor, Color.WHITE);

        mHalfBlockWidth = ta.getDimension(R.styleable.BlockNumberView_halfBlockWidth, 40);

        mViewWidthAndHeight = mHalfBlockWidth * 2 * mLineNumber;

        mBackColor = ta.getColor(R.styleable.BlockNumberView_blockBackColor, Color.TRANSPARENT);
        mBlockDefaultColor = context.getResources().getColor(R.color.blockColorDefault);
        mBlockColor = ta.getColor(R.styleable.BlockNumberView_blockColor, Color.GRAY);

        mBlockInterval = ta.getDimension(R.styleable.BlockNumberView_blockInterval, 5);

        ta.recycle();
    }

    private void init() {
        mBlockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlockPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStrokeWidth(10);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.BLACK);
        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackPaint.setColor(mBackColor);
        mColorLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        initBlocks();
    }

    private void initBlocks() {
        mOutBorder = new RectF();
        mBlocks = new Block[mSize];
        for (int i = 0; i < mBlocks.length; i++) {
            mBlocks[i] = new Block();
            mBlocks[i].setIndex(i);
            mBlocks[i].setRectF(new RectF());
            if (mSettings != null) {
                mBlocks[i].setPort(mSettings[i].isPort());
                mBlocks[i].setPortNumber(mSettings[i].getPortNumber());
                if (mSettings[i].getPortNumber() == 1){
                    connectedBlock ++;
                }
                mBlocks[i].setLineColor(mSettings[i].getLineColor());
                mBlocks[i].setTotalMoveCount(0);
                mBlocks[i].setBlockCount(Block.UNCONNECTED_BLOCK);
            }
        }
        initBlockRelation(mBlocks);
    }

    private void initBlockRelation(Block[] blocks) {
        Block block;
        for (int i = 0; i < blocks.length; i++) {
            block = blocks[i];
            if (i % mLineNumber == 0) {
                setBlockOrientation(blocks, block, mLineNumber, -1, i - mLineNumber, i + 1, i + mLineNumber);
            } else if ((i + 1) % mLineNumber == 0) {
                setBlockOrientation(blocks, block, mLineNumber, i - 1, i - mLineNumber, blocks.length, i + mLineNumber);
            } else {
                setBlockOrientation(blocks, block, mLineNumber, i - 1, i - mLineNumber, i + 1, i + mLineNumber);
            }

        }
    }

    private void setBlockOrientation(Block[] blocks, Block block, int lineCount, int left, int up, int right, int bottom) {
        if (left < 0 || left >= blocks.length) {
            block.setLeftBlock(null);
        } else {
            block.setLeftBlock(blocks[left]);
        }
        if (up < 0 || up >= blocks.length) {
            block.setUpBlock(null);
        } else {
            block.setUpBlock(blocks[up]);
        }
        if (right < 0 || right >= blocks.length) {
            block.setRightBlock(null);
        } else {
            block.setRightBlock(blocks[right]);
        }
        if (bottom < 0 || bottom >= blocks.length) {
            block.setBottomBlock(null);
        } else {
            block.setBottomBlock(blocks[bottom]);
        }
    }
    /*属性、视图初始化------end------*/

    /*继承父类方法------begin------*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) (2 * mHalfBlockWidth * mLineNumber);
        int height = (int) (2 * mHalfBlockWidth * mLineNumber);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//onMeasure后，onDraw前调用
        super.onSizeChanged(w, h, oldw, oldh);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        mCenterX = ((View) getParent()).getMeasuredWidth() / 2;
        mCenterY = ((View) getParent()).getMeasuredHeight() / 2;
        if (((View) getParent()).getMeasuredWidth() > measuredHeight && ((View) getParent()).getMeasuredHeight() > measuredWidth) {
            mCanScroll = false;
            mCanScrollVertical = false;
            mCanScrollHorizontal = false;
            centerPosition(measuredWidth, measuredHeight);
        } else if (((View) getParent()).getMeasuredWidth() > measuredHeight && ((View) getParent()).getMeasuredHeight() < measuredWidth) {//超高
            mCanScroll = true;
            mCanScrollVertical = true;
            mCanScrollHorizontal = false;
            centerHorizontalPosition(measuredWidth, measuredHeight);
        } else if (((View) getParent()).getMeasuredWidth() < measuredHeight && ((View) getParent()).getMeasuredHeight() > measuredWidth) {//超宽
            mCanScroll = true;
            mCanScrollVertical = false;
            mCanScrollHorizontal = true;
            centerVerticalPosition(measuredWidth, measuredHeight);
        } else {//超宽高
            mCanScroll = true;
            mCanScrollVertical = true;
            mCanScrollHorizontal = true;
            startPosition(measuredWidth, measuredHeight);
        }
//        super.layout((int)mLayoutLeft,(int)mLayoutTop,(int)mLayoutRight,(int)mLayoutBottom);
        ((View) getParent()).scrollTo(-(int) mLayoutLeft, -(int) mLayoutTop);
        mBlockWidthAndHeight = mBlocks[0].getRectF().right - mBlocks[0].getRectF().left;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mOutBorder, 15, 15, mBackPaint);
        canvas.drawRoundRect(mOutBorder, 15, 15, mBorderPaint);
        for (int i = 0; i < mLineNumber; i++) {
            for (int j = 0; j < mLineNumber; j++) {
                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        drawBlocks(canvas, mBlockDefaultColor, i, j);
                    } else {
                        drawBlocks(canvas, mBlockColor, i, j);
                    }
                } else {
                    if (j % 2 == 0) {
                        drawBlocks(canvas, mBlockColor, i, j);
                    } else {
                        drawBlocks(canvas, mBlockDefaultColor, i, j);
                    }
                }
            }
        }
    }

    private int rawX;//记录当前事件绝对横坐标
    private int x;//记录相对父控件横坐标
    private int lastX;//记录上一次事件绝对横坐标
    private int offSetX;//记录横坐标偏移量
    private int rawY;//记录当前事件绝对纵坐标
    private int y;//记录相对父控件纵坐标
    private int lastY;//记录上一次事件绝对横纵标
    private int offSetY;//记录纵坐标偏移量
    private int distanceX;//记录总偏移量横坐标
    private int distanceY;//记录总偏移量纵坐标
    private Block currentTouchBlock;
    private Block lastBlock;
    private long lastClickTime;
    private Block lastClickBlock;
    private boolean mScrollOrConnection;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        rawX = (int) event.getRawX();
        rawY = (int) event.getRawY();
        x = (int) event.getX();
        y = (int) event.getY();
        distanceX = ((View) getParent()).getScrollX();
        distanceY = ((View) getParent()).getScrollY();
        int limitTop = -(int) (mHalfBlockWidth * 5 / 4);
        int limitLeft = -(int) (mHalfBlockWidth * 5 / 4);
        int limitRight = getWidth() + (int) (mHalfBlockWidth * 5 / 4) - ((View) getParent()).getWidth();
        int limitBottom = getHeight() + (int) (mHalfBlockWidth * 5 / 4) - ((View) getParent()).getHeight();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ((System.currentTimeMillis() - lastClickTime) <= 500) {
                    if (lastClickBlock != null && getCurrentTouchBlock() == lastClickBlock) {
                        Log.d(TAG, "onTouchEvent: 双击执行");
                        if (getCurrentTouchBlock().getPortNumber() == 1){
                            break;
                        }
                        Block target = getCurrentTouchBlock();
                        while (true) {
                            if (target.getNextBlock() == null) {
                                target.initConnectionBlockLast();
                                break;
                            } else if (target.getNextBlock() != null && target.getNextBlock().getNextBlock() == target) {
                                countConnectedBlock(-target.getTotalMoveCount());
                                target.getNextBlock().initConnectionBlockLast();
                                target.initConnectionBlockLast();
                                break;
                            } else {
                                target = target.getNextBlock();
                                continue;
                            }
                        }
                        invalidate();
                    }
                } else {
                    lastClickBlock = getCurrentTouchBlock();
                    lastClickTime = System.currentTimeMillis();
                }
                lastX = rawX;
                lastY = rawY;
//                currentTouchBlock = null;
                lastBlock = null;

//                if (getCurrentTouchBlock().getBlockCount() != Block.UNCONNECTED_BLOCK && !getCurrentTouchBlock().isConnecting()) {
//
//                    Log.d(TAG, "onTouchEvent: 执行");
                currentTouchBlock = getCurrentTouchBlock();

//                }

                if(mCanScroll){
                    if ((currentTouchBlock.getLineColor() == 0 && currentTouchBlock.getBlockCount() == 0
                            && currentTouchBlock.getTotalMoveCount() == 0)
                            || (currentTouchBlock.isConnected() && currentTouchBlock.isConnecting())
                            || (!currentTouchBlock.isConnected() && currentTouchBlock.isConnecting())){
                        mScrollOrConnection = true;
                    }else {
                        mScrollOrConnection = false;
                    }
                }else {
                    mScrollOrConnection = false;
                }

                mOnBlockClickListener.onBlockClick(getCurrentTouchBlock());

                break;
            case MotionEvent.ACTION_MOVE:
                if (mScrollOrConnection) {
                    offSetX = lastX - rawX;
                    offSetY = lastY - rawY;
                    scrollView(distanceX, distanceY, limitLeft, limitTop, limitRight, limitBottom);
                    lastX = rawX;
                    lastY = rawY;
                } else {

                    if (currentTouchBlock != null) {
//                        Log.d(TAG, "rawX == " + x);
//                        Log.d(TAG, "\n");
//                        Log.d(TAG, "currentLeft == " + currentTouchBlock.getRectF().left);
//                        Log.d(TAG, "\n");
//                        Log.d(TAG, "currentRight == " + currentTouchBlock.getRectF().right);
//                        Log.d(TAG, "rawY == " + y);
//                        Log.d(TAG, "\n");
//                        Log.d(TAG, "currentTop == " + currentTouchBlock.getRectF().top);
//                        Log.d(TAG, "\n");
//                        Log.d(TAG, "currentBottom == " + currentTouchBlock.getRectF().bottom);
//                        Log.d(TAG, "\n");
//                        Log.d(TAG, "___________________________________________________________________________________");
                        if (currentTouchBlock.getRightBlock() != null &&
                                !currentTouchBlock.isConnecting() &&
                                y > currentTouchBlock.getRectF().top &&
                                y < currentTouchBlock.getRectF().bottom &&
                                x > currentTouchBlock.getRectF().right) {
                            Log.d(TAG, "rawX == currentTouchBlock.getRightBlock().getRectF().left: 执行");
                            doOnConnectBlock(Block.ORIENTATION_RIGHT);
                            invalidate();

                        } else if (currentTouchBlock.getLeftBlock() != null &&
                                !currentTouchBlock.isConnecting() &&
                                y > currentTouchBlock.getRectF().top &&
                                y < currentTouchBlock.getRectF().bottom &&
                                x < currentTouchBlock.getRectF().left) {
                            Log.d(TAG, "rawX == currentTouchBlock.getRightBlock().getRectF().left: 执行");
                            doOnConnectBlock(Block.ORIENTATION_LEFT);
                            invalidate();

                        } else if (currentTouchBlock.getUpBlock() != null &&
                                !currentTouchBlock.isConnecting() &&
                                x > currentTouchBlock.getRectF().left &&
                                x < currentTouchBlock.getRectF().right &&
                                y < currentTouchBlock.getRectF().top) {
                            Log.d(TAG, "rawX == currentTouchBlock.getRightBlock().getRectF().left: 执行");
                            doOnConnectBlock(Block.ORIENTATION_UP);
                            invalidate();

                        } else if (currentTouchBlock.getBottomBlock() != null &&
                                !currentTouchBlock.isConnecting() &&
                                x > currentTouchBlock.getRectF().left &&
                                x < currentTouchBlock.getRectF().right &&
                                y > currentTouchBlock.getRectF().bottom) {
                            Log.d(TAG, "rawX == currentTouchBlock.getRightBlock().getRectF().left: 执行");
                            doOnConnectBlock(Block.ORIENTATION_BOTTOM);
                            invalidate();

                        }

                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCanScroll) {
                    doOnMoveBlockFingerUp(distanceX, distanceY, limitLeft, limitTop, limitRight, limitBottom);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mCanScroll) {
                    ((View) getParent()).scrollTo(limitLeft, limitTop);
                }
                break;
            default:
                break;
        }
        return true;
    }

    /*继承父类方法------end------*/

    /*自定义私有及供外部调用接口方法------begin------*/

    //私有方法
    private void centerVerticalPosition(int width, int height) {
        float blockWidth = mHalfBlockWidth * 2;
        int blockCountInALine = mLineNumber / 2;
        float firstBlockLeftTopFromCenter = blockCountInALine * blockWidth;
        positionBlocks(width, height);
        mLayoutLeft = 0;
        mLayoutTop = mCenterY - firstBlockLeftTopFromCenter;
        mLayoutRight = width;
        mLayoutBottom = mCenterY + firstBlockLeftTopFromCenter;
    }

    private void centerHorizontalPosition(int width, int height) {
        float blockWidth = mHalfBlockWidth * 2;
        int blockCountInALine = mLineNumber / 2;
        float firstBlockLeftTopFromCenter = blockCountInALine * blockWidth;
        positionBlocks(width, height);
        mLayoutLeft = mCenterX - firstBlockLeftTopFromCenter;
        mLayoutTop = 0;
        mLayoutRight = mCenterX + firstBlockLeftTopFromCenter;
        mLayoutBottom = height;
    }

    private void centerPosition(int width, int height) {
        float blockWidth = mHalfBlockWidth * 2;
        int blockCountInALine = mLineNumber / 2;
        float firstBlockLeftTopFromCenter = blockCountInALine * blockWidth;
//        mFirstRecFLeft = centerX - firstBlockLeftTopFromCenter;
//        mFirstRecFTop = centerY - firstBlockLeftTopFromCenter;
//
//        mOutBorder.set(mFirstRecFLeft - mBlockInterval,
//                mFirstRecFTop - mBlockInterval,
//                mFirstRecFLeft + firstBlockLeftTopFromCenter * 2 + mBlockInterval,
//                mFirstRecFTop + firstBlockLeftTopFromCenter * 2 + mBlockInterval);
//        mFirstRecFLeft = 0;
//        mFirstRecFTop = 0;
//
//        mOutBorder.set(-mBlockInterval, -mBlockInterval, width-mBlockInterval, height-mBlockInterval);
        positionBlocks(width, height);
        mLayoutLeft = mCenterX - firstBlockLeftTopFromCenter;
        mLayoutTop = mCenterY - firstBlockLeftTopFromCenter;
        mLayoutRight = mCenterX + firstBlockLeftTopFromCenter;
        mLayoutBottom = mCenterY + firstBlockLeftTopFromCenter;
    }

    private void startPosition(int width, int height) {
//        mFirstRecFLeft = 0;
//        mFirstRecFTop = 0;
//
//        mOutBorder.set(-mBlockInterval, -mBlockInterval, width-mBlockInterval, height-mBlockInterval);
        positionBlocks(width, height);
        mLayoutLeft = 0;
        mLayoutTop = 0;
        mLayoutRight = width;
        mLayoutBottom = height;
    }

    private void positionBlocks(int width, int height) {
        mFirstRecFLeft = 0;
        mFirstRecFTop = 0;

        mOutBorder.set(-mBlockInterval, -mBlockInterval, width + mBlockInterval, height + mBlockInterval);

        float blockWidth = mHalfBlockWidth * 2;
        for (int i = 0; i < mLineNumber; i++) {
            for (int j = 0; j < mLineNumber; j++) {
                if (i == 0) {
                    if (j == 0) {
                        mBlocks[0].getRectF().set(
                                mFirstRecFLeft,
                                mFirstRecFTop,
                                mFirstRecFLeft + blockWidth,
                                mFirstRecFTop + blockWidth);
                    } else {
                        mBlocks[j].getRectF().set(mBlocks[j - 1].getRectF());
                        mBlocks[j].getRectF().offset(blockWidth, 0);
                    }
                } else {
                    int currIndex = i * mLineNumber + j;
                    mBlocks[currIndex].getRectF().set(mBlocks[currIndex - mLineNumber].getRectF());
                    mBlocks[currIndex].getRectF().offset(0, blockWidth);
                }
            }
        }
    }

    private void drawBlocks(Canvas canvas, int color, int line, int rank) {

        mBlockPaint.setColor(color);
        mBlock = mBlocks[mLineNumber * line + rank];
        canvas.drawRect(mBlock.getRectF(), mBlockPaint);
        boolean isPort = mBlock.isPort();
        boolean isConnecting = mBlock.isConnecting();
        boolean isConnected = mBlock.isConnected();
        int lineColor = mBlock.getLineColor();
        float cx = mBlock.getRectF().centerX();
        float cy = mBlock.getRectF().centerY();

        if (isPort) {
            int portNumber = mBlock.getPortNumber();
            if (portNumber == 1){
                mColorLinePaint.setColor(lineColor);
                canvas.drawRect(mBlock.getRectF().left + mHalfBlockWidth / 10,
                        mBlock.getRectF().top + mHalfBlockWidth / 10,
                        mBlock.getRectF().right - mHalfBlockWidth / 10,
                        mBlock.getRectF().bottom - mHalfBlockWidth / 10, mColorLinePaint );
            }
            if (!isConnecting && !isConnected) {

                mColorLinePaint.setColor(lineColor);
                canvas.drawCircle(cx, cy, mHalfBlockWidth * 3 / 4, mColorLinePaint);
                drawNumber(canvas, portNumber, cx - mHalfBlockWidth * 1 / 12, cy + mHalfBlockWidth * 3 / 8, mHalfBlockWidth);

            } else if (isConnecting && !isConnected) {

                mColorLinePaint.setColor(lineColor);
                canvas.drawCircle(cx, cy, mHalfBlockWidth * 3 / 4, mColorLinePaint);
                drawConnectingBlock(canvas, lineColor, cx, cy, mBlock.getConnectNext());
                drawNumber(canvas, portNumber, cx - mHalfBlockWidth * 1 / 12, cy + mHalfBlockWidth * 3 / 8, mHalfBlockWidth);

            } else if (isConnecting && isConnected) {

                drawConnectedBlock(canvas, lineColor, mBlock.getConnectNext());
                drawNumber(canvas, portNumber, cx - mHalfBlockWidth * 1 / 12, cy + mHalfBlockWidth * 3 / 8, mHalfBlockWidth);

            }
        } else {
            int blockCount = mBlock.getBlockCount();
            if (!isConnecting && !isConnected) {
                if (lineColor != 0) {
                    drawConnectingBlock(canvas, lineColor, cx, cy, mBlock.getConnectLast());
                }
                if (blockCount != Block.UNCONNECTED_BLOCK) {
                    mColorLinePaint.setColor(lineColor);
                    canvas.drawCircle(cx, cy, mHalfBlockWidth / 2, mColorLinePaint);
                    drawNumber(canvas, blockCount, cx, cy + mHalfBlockWidth * 1 / 4, mHalfBlockWidth * 3 / 4);
                }
            } else if (!isConnected && isConnecting) {
                if (lineColor != 0) {
                    drawConnectingBlock(canvas, lineColor, cx, cy, mBlock.getConnectLast());
                    drawConnectingBlock(canvas, lineColor, cx, cy, mBlock.getConnectNext());
                }
            } else if (isConnected && isConnecting) {
                if (lineColor != 0) {
                    drawConnectedBlock(canvas, lineColor, mBlock.getConnectLast());
                    drawConnectedBlock(canvas, lineColor, mBlock.getConnectNext());
                }
            }
        }
    }

    private void drawNumber(Canvas canvas, int portNumber, float numberCx, float numberCy, float numberSize) {
        mColorLinePaint.setColor(Color.WHITE);
        mColorLinePaint.setTextSize(numberSize);
        mColorLinePaint.setTextAlign(Paint.Align.CENTER);
        mColorLinePaint.setFakeBoldText(true);
        canvas.drawText(String.valueOf(portNumber), numberCx, numberCy, mColorLinePaint);
    }

    private void drawConnectingBlock(Canvas canvas, int lineColor, float cx, float cy, int connectOrientation) {
        if (connectOrientation != Block.ORIENTATION_NONE) {
            mColorLinePaint.setColor(lineColor);
            switch (connectOrientation) {
                case Block.ORIENTATION_LEFT:
                    canvas.drawRect(mBlock.getRectF().left,
                            cy - mHalfBlockWidth / 9,
                            cx + mHalfBlockWidth / 9,
                            cy + mHalfBlockWidth / 9, mColorLinePaint);
                    break;
                case Block.ORIENTATION_UP:
                    canvas.drawRect(cx - mHalfBlockWidth / 9,
                            mBlock.getRectF().top,
                            cx + mHalfBlockWidth / 9,
                            cy + mHalfBlockWidth / 9, mColorLinePaint);
                    break;
                case Block.ORIENTATION_RIGHT:
                    canvas.drawRect(cx - mHalfBlockWidth / 9,
                            cy - mHalfBlockWidth / 9,
                            mBlock.getRectF().right,
                            cy + mHalfBlockWidth / 9, mColorLinePaint);
                    break;
                case Block.ORIENTATION_BOTTOM:
                    canvas.drawRect(cx - mHalfBlockWidth / 9,
                            cy - mHalfBlockWidth / 9,
                            cx + mHalfBlockWidth / 9,
                            mBlock.getRectF().bottom, mColorLinePaint);
                    break;
                default:
                    break;
            }
        }
    }

    private void drawConnectedBlock(Canvas canvas, int lineColor, int connectOrientation) {
        if (connectOrientation != Block.ORIENTATION_NONE) {
            mColorLinePaint.setColor(lineColor);
            switch (connectOrientation) {
                case Block.ORIENTATION_LEFT:
                    canvas.drawRect(mBlock.getRectF().left,
                            mBlock.getRectF().top + mHalfBlockWidth / 10,
                            mBlock.getRectF().right - mHalfBlockWidth / 10,
                            mBlock.getRectF().bottom - mHalfBlockWidth / 10, mColorLinePaint);
                    break;
                case Block.ORIENTATION_UP:
                    canvas.drawRect(mBlock.getRectF().left + mHalfBlockWidth / 10,
                            mBlock.getRectF().top,
                            mBlock.getRectF().right - mHalfBlockWidth / 10,
                            mBlock.getRectF().bottom - mHalfBlockWidth / 10, mColorLinePaint);
                    break;
                case Block.ORIENTATION_RIGHT:
                    canvas.drawRect(mBlock.getRectF().left + mHalfBlockWidth / 10,
                            mBlock.getRectF().top + mHalfBlockWidth / 10,
                            mBlock.getRectF().right,
                            mBlock.getRectF().bottom - mHalfBlockWidth / 10, mColorLinePaint);
                    break;
                case Block.ORIENTATION_BOTTOM:
                    canvas.drawRect(mBlock.getRectF().left + mHalfBlockWidth / 10,
                            mBlock.getRectF().top + mHalfBlockWidth / 10,
                            mBlock.getRectF().right - mHalfBlockWidth / 10,
                            mBlock.getRectF().bottom, mColorLinePaint);
                    break;
                default:
                    break;
            }
        }
    }

    private void scrollView(int dx, int dy, int l, int t, int r, int b) {

        if (dx >= l && dy >= t && dx <= r && dy <= b) { //view内滚动
            if (mCanScrollVertical && mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(offSetX, offSetY);
            } else if (mCanScrollVertical && !mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(0, offSetY);
            } else if (!mCanScrollVertical && mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(offSetX, 0);
            }

        } else if (dx > l && dx < r && dy < t
                || dx > l && dx < r && dy > b) {//超过view上/下，但在左/右内滚动

            if (mCanScrollVertical && mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(offSetX, offSetY / 8);
            } else if (mCanScrollVertical && !mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(0, offSetY / 8);
            } else if (!mCanScrollVertical && mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(offSetX, 0);
            }

        } else if (dy > t && dy < b && dx < l
                || dy > t && dy < b && dx > r) {//超过view左/右，但在上/下内滚动

            if (mCanScrollVertical && mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(offSetX / 8, offSetY);
            } else if (!mCanScrollVertical && mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(offSetX / 8, 0);
            } else if (mCanScrollVertical && !mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(0, offSetY);
            }

        } else {//超过左/上或者超过右/下滚动

            if (mCanScrollVertical && mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(offSetX / 8, offSetY / 8);
            } else if (!mCanScrollVertical && mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(offSetX / 8, 0);
            } else if (mCanScrollVertical && !mCanScrollHorizontal) {
                ((View) getParent()).scrollBy(0, offSetY / 8);
            }

        }
    }

    private Block getCurrentTouchBlock() {
        int blockIndexX = (int) ((x - mFirstRecFLeft) / mBlockWidthAndHeight);
        int blockIndexY = (int) ((y - mFirstRecFTop) / mBlockWidthAndHeight);
        int index = (blockIndexY) * mLineNumber + blockIndexX;
        if (index < mSize && index >= 0) {
            return mBlocks[index];
        } else {
            return null;
        }
    }

    private void doOnConnectBlock(int orientation) {
        int last = 0;
        int next = 0;
        switch (orientation) {
            case Block.ORIENTATION_LEFT:
                last = Block.ORIENTATION_LEFT;
                next = Block.ORIENTATION_RIGHT;
                break;
            case Block.ORIENTATION_RIGHT:
                last = Block.ORIENTATION_RIGHT;
                next = Block.ORIENTATION_LEFT;
                break;
            case Block.ORIENTATION_UP:
                last = Block.ORIENTATION_UP;
                next = Block.ORIENTATION_BOTTOM;
                break;
            case Block.ORIENTATION_BOTTOM:
                last = Block.ORIENTATION_BOTTOM;
                next = Block.ORIENTATION_UP;
                break;
            default:
                break;
        }

        lastBlock = currentTouchBlock;
        currentTouchBlock = getCurrentTouchBlock();

        switch (getConnectedAbleStatus()) {
            case CONNECTION_MATCH:
                Log.d(TAG, "连接匹配 执行~~~");
                lastBlock.setConnectNext(last);
                lastBlock.setConnecting(true);
                lastBlock.setConnected(true);
                currentTouchBlock.setConnectNext(next);
                currentTouchBlock.setConnecting(true);
                currentTouchBlock.setConnected(true);
                countConnectedBlock(lastBlock.getBlockCount() + currentTouchBlock.getBlockCount());
                break;
            case CONNECTION_IS_EMPTY:
                Log.d(TAG, "遇到空方块 执行~~~");
                lastBlock.setConnectNext(last);
                lastBlock.setConnecting(true);
                currentTouchBlock.setConnectLast(next);
                currentTouchBlock.setBlockCount(lastBlock.getBlockCount() + 1);
                currentTouchBlock.setLineColor(lastBlock.getLineColor());
                currentTouchBlock.setTotalMoveCount(lastBlock.getTotalMoveCount());
                break;
            case CONNECTION_IS_BACK:
                Log.d(TAG, "回退连接 执行~~~");
                lastBlock.setConnectLast(Block.ORIENTATION_NONE);
                lastBlock.setConnecting(false);
                lastBlock.setConnected(false);
                lastBlock.setBlockCount(Block.UNCONNECTED_BLOCK);
                lastBlock.setLineColor(0);
                lastBlock.setTotalMoveCount(0);
                currentTouchBlock.setConnecting(false);
                currentTouchBlock.setConnectNext(Block.ORIENTATION_NONE);
                break;
            case CONNECTION_ERROR_CONNECTED:
                Log.d("****连接操作****", "注意：操作中出现错误：方块未开始连接，但已经被设置为Connected状态！！！");
                break;
            case CONNECTION_IS_USING:
                Log.d("****连接操作****", "正在试图连接一个已被占用的方块 ");
                break;
            case CONNECTION_STEP_NOT_MATCH:
                Log.d("****连接操作****", "正在试图连接一个步数不匹配的方块，滑动中上一个方块已经移动步数为：" +
                        lastBlock.getBlockCount() + "，总步数为：" + lastBlock.getTotalMoveCount() +
                        "；当前方块已经移动步数为：" + currentTouchBlock.getBlockCount() + "，总步数为：" + currentTouchBlock.getTotalMoveCount());
                break;
            case CONNECTION_TYPE_NOT_MATCH:
                Log.d("****连接操作****", "正在试图连接一个类型不匹配的方块....");
                if (currentTouchBlock.getLineColor() != lastBlock.getLineColor()) {
                    Log.d("****连接操作****", "颜色不匹配");
                } else if (currentTouchBlock.getTotalMoveCount() != lastBlock.getTotalMoveCount()) {
                    Log.d("****连接操作****", "总步数不匹配");
                }
                break;
            case CONNECTION_STEPS_USED_UP:
                Log.d("****连接操作****", "无可移动步数");
                break;
            case CLICK_ERROR_BLOCK:
                Log.d("****连接操作****", "没有点击方块");
                break;
            default:
                Log.d("****连接操作****", "！！！出现未知情况！！！");
                break;
        }
    }

    private static final int CONNECTION_MATCH = 200;//当前连接匹配包括一个端点直接连接到领一个端点时的匹配，或者两个端点都连接出去在中途相遇时的匹配
    private static final int CONNECTION_IS_USING = 201;//当前连接遇到了一个已经被占用（处于已连接状态）的方块
    private static final int CONNECTION_STEP_NOT_MATCH = 202;//类型（数字、颜色）相同，但步数不匹配
    private static final int CONNECTION_TYPE_NOT_MATCH = 203;//类型不匹配
    private static final int CONNECTION_IS_EMPTY = 204;//当前连接遇到了一个未被连接且不是端点的方块
    private static final int CONNECTION_ERROR_CONNECTED = 205;//出现异常方块不在连接中但已连上
    private static final int CONNECTION_IS_BACK = 206;//回退连接路线时
    private static final int CONNECTION_STEPS_USED_UP = 207;//按照端点可用步数计算，可用步数已经用完
    private static final int CLICK_ERROR_BLOCK = 208;

    private int getConnectedAbleStatus() {
        if (currentTouchBlock != null) {
            boolean connecting = currentTouchBlock.isConnecting();
            boolean connected = currentTouchBlock.isConnected();
            int lastColor = lastBlock.getLineColor();
            int currentColor = currentTouchBlock.getLineColor();
            int lastTotal = lastBlock.getTotalMoveCount();
            int currentTotal = currentTouchBlock.getTotalMoveCount();
            int i = currentTouchBlock.getBlockCount() + lastBlock.getBlockCount();
            if (lastBlock.getBlockCount() < lastTotal) {
                if (connecting) {
                    if (connected) {
                        return CONNECTION_IS_USING;
                    } else {
                        if (lastColor == currentColor && lastTotal == currentTotal) {
                            if (i == lastTotal && i == currentTotal) {
                                return CONNECTION_IS_USING;
                            } else {
                                if (currentTouchBlock == lastBlock.getLastBlock() && lastBlock.getNextBlock() == null) {
                                    return CONNECTION_IS_BACK;
                                } else {
                                    return CONNECTION_STEP_NOT_MATCH;
                                }
                            }
                        } else {
                            return CONNECTION_TYPE_NOT_MATCH;
                        }
                    }
                } else {
                    if (connected) {
                        return CONNECTION_ERROR_CONNECTED;
                    } else {
                        if (lastColor == currentColor && lastTotal == currentTotal) {
                            if (i == lastTotal && i == currentTotal) {
                                return CONNECTION_MATCH;
                            } else {
                                return CONNECTION_STEP_NOT_MATCH;
                            }
                        } else {
                            if (currentColor == 0 && currentTotal == 0) {
                                return CONNECTION_IS_EMPTY;
                            } else {
                                return CONNECTION_TYPE_NOT_MATCH;
                            }
                        }
                    }
                }
            } else {
                if (currentTouchBlock == lastBlock.getLastBlock() && lastBlock.getNextBlock() == null) {
                    return CONNECTION_IS_BACK;
                } else {
                    return CONNECTION_STEPS_USED_UP;
                }
            }
        } else {
            return CLICK_ERROR_BLOCK;
        }
    }

//    private int getCrossWay(){
//        if (currentTouchBlock.getRightBlock() != null &&
//                !currentTouchBlock.isConnecting() &&
//                y > currentTouchBlock.getRectF().top &&
//                y < currentTouchBlock.getRectF().bottom &&
//                x > currentTouchBlock.getRectF().right) {
//
//            return Block.ORIENTATION_RIGHT;
//
//        } else if (currentTouchBlock.getLeftBlock() != null &&
//                !currentTouchBlock.isConnecting() &&
//                y > currentTouchBlock.getRectF().top &&
//                y < currentTouchBlock.getRectF().bottom &&
//                x < currentTouchBlock.getRectF().left) {
//
//            return Block.ORIENTATION_LEFT;
//
//        } else if (currentTouchBlock.getUpBlock() != null &&
//                !currentTouchBlock.isConnecting() &&
//                x > currentTouchBlock.getRectF().left &&
//                x < currentTouchBlock.getRectF().right &&
//                y < currentTouchBlock.getRectF().top) {
//
//            return Block.ORIENTATION_UP;
//
//        } else if (currentTouchBlock.getBottomBlock() != null &&
//                !currentTouchBlock.isConnecting() &&
//                x > currentTouchBlock.getRectF().left &&
//                x < currentTouchBlock.getRectF().right &&
//                y > currentTouchBlock.getRectF().bottom) {
//
//            return Block.ORIENTATION_BOTTOM;
//
//        }else {
//            return Block.ORIENTATION_NONE;
//        }
//    }

    private void doOnMoveBlockFingerUp(int dx, int dy, int l, int t, int r, int b) {
        if (mCanScrollHorizontal && mCanScrollVertical) {
            if (dx < l && dy < t) {//超左上离开
                ((View) getParent()).scrollTo(l, t);
            } else if (dy > t && dy < b && dx < l) {//仅超左离开
                ((View) getParent()).scrollTo(l, dy);
            } else if (dx < l && dy > b) {//超左下离开
                ((View) getParent()).scrollTo(l, b);
            } else if (dx > l && dx < r && dy > b) {//仅超下离开
                ((View) getParent()).scrollTo(dx, b);
            } else if (dx > r && dy > b) {//超右下离开
                ((View) getParent()).scrollTo(r, b);
            } else if (dy > t && dy < b && dx > r) {//仅超右离开
                ((View) getParent()).scrollTo(r, dy);
            } else if (dx > r && dy < t) {//超右上离开
                ((View) getParent()).scrollTo(r, t);
            } else if (dx > l && dx < r && dy < t) {//仅超上离开
                ((View) getParent()).scrollTo(dx, t);
            }
        } else if (!mCanScrollVertical && mCanScrollHorizontal) {
            if (dx < l) {
                ((View) getParent()).scrollTo(l, -(int) mLayoutTop);
            } else if (dx > r) {
                ((View) getParent()).scrollTo(r, -(int) mLayoutTop);
            }
        } else if (mCanScrollVertical && !mCanScrollHorizontal) {
            if (dy < t) {
                ((View) getParent()).scrollTo(-(int) mLayoutLeft, t);
            } else if (dy > b) {
                ((View) getParent()).scrollTo(-(int) mLayoutLeft, b);
            }
        }
    }

    private void countConnectedBlock(int counts) {
        mCounts += counts;
        if (mCounts + connectedBlock == mSize) {
            mOnGameCompleteListener.onGameComplete();
        } else if (mCounts >= 0 && mCounts < mSize) {
            mOnGameCompleteListener.onGamePlaying(mCounts + connectedBlock);
        } else {
            Log.d(TAG, "countConnectedBlock: 出现未知情况！！！");
        }
    }

    //外部调用接口
    public void initConnectionSettings(ConnectionSetting[] settings) {
        mSettings = settings;
        initBlocks();
        invalidate();
    }

    public int getSize() {
        return mSize;
    }

    public Block[] getBlocks() {
        return mBlocks;
    }

    public void setOnBlockClickListener(OnBlockClickListener listener) {
        mOnBlockClickListener = listener;
    }

    public void setOnGameCompleteListener(OnGameCompleteListener listener) {
        mOnGameCompleteListener = listener;
    }

    public void setScrollAble() {
        mCanScroll = !mCanScroll;
    }

    //监听器
    public interface OnBlockClickListener {
        void onBlockClick(Block block);
    }

    public interface OnGameCompleteListener {
        void onGamePlaying(int progress);

        void onGameComplete();
    }
    /*自定义私有及供外部调用接口方法------end------*/
}
