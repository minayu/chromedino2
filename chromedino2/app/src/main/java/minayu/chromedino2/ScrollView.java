package minayu.chromedino2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;

/**
 * Created by minayu on 2017/12/10.
 */

public class ScrollView extends View {
    // Imageの表示用
    Paint paint = new Paint();
    // Score表示用
    Paint totalTime = new Paint();
    // GameOver画面用
    Paint fullScr = new Paint();
    Paint gameOverPaint = new Paint();
    Paint scorePaint = new Paint();
    int screenWidth;
    int screenHeight;
    public static final int GAME_RUN = 1;
    public static final int GAME_OVER = 2;
    private int state = 0;
    String gameOverStr = "Game Over";

    // 描画位置Y軸
    int constY = 570;
    int playerY = constY;
    // Y軸に加算する値
    int playerVY = 0;

    int enemyX = 1400;
    //int enemyVX = 0;
    int enemyY = 650;

    // Score用の時間
    long startTime = System.currentTimeMillis();
    long playTime = 0;
    long score = 0;
    String scoreStr;

    // Player画像の読み込み
    Resources res = this.getContext().getResources();
    Bitmap player = BitmapFactory.decodeResource(res, R.drawable.itachi);
    Bitmap enemy = BitmapFactory.decodeResource(res, R.drawable.enemy);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        state = GAME_RUN;

        //プレイヤーのジャンプ描画
        playerY += playerVY;
        playerVY += 4;
        // ジャンプから降りてくる
        if(playerY > constY) {
            playerY = constY;
            playerVY = 0;
        }

        // エネミーが迫る描画
        enemyX -= 12;
        // 左端まで行ったら再描画
        if(enemyX < -60) { // エネミーの画像サイズ加味
            canvas.drawBitmap(enemy, 1000, 1000, paint);
            enemyX = 1400;
            enemyY = 650;
        }

        // 描画処理
        canvas.drawBitmap(player, 0, playerY, paint);
        canvas.drawBitmap(enemy, enemyX, enemyY, paint);
//        totalTime.setColor(Color.BLACK);
//        totalTime.setAntiAlias(true);
//        totalTime.setTextSize(70);
//        totalTime.setTextAlign(Paint.Align.CENTER);
        scorePaint.setColor(Color.BLACK);
        scorePaint.setAntiAlias(true);
        scorePaint.setTextSize(70);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        // SCORE（プレイ時間）の表示
        playTime = System.currentTimeMillis() - startTime;
        score = playTime * 10 / 100;
        scoreStr = "SCORE: " + String.valueOf(score);
        canvas.drawText(scoreStr, 1500, 100, scorePaint);

        // 当たり判定 ゲームオーバー
        // サイズ player 71/121, enemy 49/59
        if(enemyX <= 71 && playerY > constY-49) {
            // ゲームオーバー画面の表示
            fullScr.setColor(0xDD000000);
            gameOverPaint.setColor(Color.WHITE);
            gameOverPaint.setAntiAlias(true);
            gameOverPaint.setTextSize(70);
            gameOverPaint.setTextAlign(Paint.Align.CENTER);
            scorePaint.setColor(Color.WHITE);
            // スコアの設定
            canvas.drawBitmap(enemy, 1000, 1000, paint);
            canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, fullScr);
            canvas.drawText(gameOverStr, screenWidth / 2, screenHeight / 2, gameOverPaint);
            canvas.drawText("SCORE: " +String.valueOf(score), screenWidth / 2, screenHeight / 2 - 100, scorePaint);
            isGameOver();
        }

        if (state == GAME_RUN) {
            //ループ処理（onDrawを実行）
            invalidate();
        }

        //ウェイト処理
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
    }

    public void restartGame() {
        state = GAME_RUN;
        startTime = System.currentTimeMillis();
        invalidate();
    }

    public void isGameOver() {
        playTime = 0;
        score = 0;
        enemyX = 1400;
        enemyY = 650;
        state = GAME_OVER;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // タッチされた時
        // 多重ジャンプの防止、ゲームリスタート時のジャンプを抑止
        if (playerY == constY & state == GAME_RUN) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                playerVY = -60;
            }
        }
        // ゲームリスタート
        if (state == GAME_OVER) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                restartGame();
            }
        }

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        Log.v("ViewSize:", w + ":" + h);
    }

    public ScrollView(Context context) {
        super(context);
    }

    public ScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
