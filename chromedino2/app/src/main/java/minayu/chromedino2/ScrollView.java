package minayu.chromedino2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.Random;

/**
 * Created by minayu on 2017/12/10.
 */

public class ScrollView extends View {

    Paint paint = new Paint();
    // GameOver画面用 背景と文字色を同化させないため分けた
    Paint fullScr = new Paint();
    Paint gameOverPaint = new Paint();
    Paint scorePaint = new Paint();
    int screenWidth;
    int screenHeight;
    int ran;
    int secondRnd;
    // GameOver判定用
    public static final int GAME_RUN = 1;
    public static final int GAME_OVER = 2;
    public static final int GAME_STOP = 3;
    private int state = 1;
    String gameOverStr = "Game Over";

    // PLAYER描画用
    final int drawPlayerY = 570;
    int playerY = drawPlayerY;
    int playerVY = 0;
    // ENEMY描画用
    final int drawEnemyX = 1600;
    int enemyX = drawEnemyX;
    int enemyVX = -12;
    int enemyY = 650;
    // Score用の時間
    long startTime = System.currentTimeMillis();
    long playTime = 0;
    long score = 0;
    String scoreStr;

    // Player画像の読み込み
    Resources res = this.getContext().getResources();
    Bitmap player = BitmapFactory.decodeResource(res, R.drawable.player);
    Bitmap enemy = BitmapFactory.decodeResource(res, R.drawable.enemy);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        state = GAME_RUN;
        //プレイヤーのジャンプ描画
        playerY += playerVY;
        playerVY += 4;
        // ジャンプから降りてくる
        if(playerY > drawPlayerY) {
            playerY = drawPlayerY;
            playerVY = 0;
        }

        // エネミーが迫る描画
        enemyX += enemyVX;
        // 左端まで行ったら再描画
        if(enemyX < -60) { // エネミーの画像サイズ加味
            canvas.drawBitmap(enemy, 1000, 1000, paint);
            enemyX = drawEnemyX;
            enemyY = 650;
            // 次エネミーの速度を変更する
            createRandom();
            Log.v("random", String.valueOf(enemyVX));
        }

        // 描画処理
        canvas.drawBitmap(player, 0, playerY, paint);
        canvas.drawBitmap(enemy, enemyX, enemyY, paint);
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
        // サイズ player 85/80 (71/121), enemy 49/59
        if(enemyX <= 85 && playerY > drawPlayerY - 49) {
            state = GAME_STOP;
            //ウェイト処理
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
            }
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
            canvas.drawText("TAP TO GAME RESTART", screenWidth / 2, screenHeight / 2 + 200, scorePaint);
            //ウェイト処理
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
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

    public void createRandom() {
        // ENEMYの速度用(乱数:10 ~ 29)
        Random rnd = new Random();
        ran = (rnd.nextInt(2) + 1) * 10;
        secondRnd = rnd.nextInt(10);
        enemyVX = -(ran + secondRnd);
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
        enemyVX = -12;
        state = GAME_OVER;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 多重ジャンプの防止、ゲームリスタート時のジャンプを抑止
            if (playerY == drawPlayerY & state == GAME_RUN) {
                playerVY = -60;
            // ゲームリスタート
            } else if (state == GAME_OVER) {
                restartGame();
            }
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 描画位置用
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
