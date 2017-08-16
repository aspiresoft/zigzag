package com.weebly.aspiresoft.zigzag;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity
{
    public static String[] BG_COLORS     = {"#60FD0E35", "#60FF7518", "#60FFc901", "#60FFF14F", "#60B0e313", "#60bfff00", "#60009dc4", "#60000080", "#609932cc", "#60e0115f"};
    public static float    SIZE          = 32;
    public static float    WIDTH         = 64;
    public static float    HEIGHT        = 32;
    public static float    DENSTIY       = 1.0f;
    public static int      SCREEN_WIDTH  = 1;
    public static int      SCREEN_HEIGHT = 1;

    public static Bitmap[] BLOCK_IMAGES = new Bitmap[10];
    public static Bitmap[] EXP_1        = new Bitmap[5];
    public static Bitmap BALL_IMAGE;
    public static Bitmap BG_IMAGE;

    private GamePanel gamePanel;


    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        DENSTIY = getResources().getDisplayMetrics().density;
        SIZE = SIZE * DENSTIY;
        WIDTH = WIDTH * DENSTIY;
        HEIGHT = HEIGHT * DENSTIY;
        WindowManager  wm      = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH = metrics.heightPixels;
        SCREEN_HEIGHT = metrics.widthPixels;
        BALL_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.ball2);

        BLOCK_IMAGES[0] = BitmapFactory.decodeResource(getResources(), R.drawable.box_01);
        BLOCK_IMAGES[1] = BitmapFactory.decodeResource(getResources(), R.drawable.box_02);
        BLOCK_IMAGES[2] = BitmapFactory.decodeResource(getResources(), R.drawable.box_03);
        BLOCK_IMAGES[3] = BitmapFactory.decodeResource(getResources(), R.drawable.box_04);
        BLOCK_IMAGES[4] = BitmapFactory.decodeResource(getResources(), R.drawable.box_05);
        BLOCK_IMAGES[5] = BitmapFactory.decodeResource(getResources(), R.drawable.box_06);
        BLOCK_IMAGES[6] = BitmapFactory.decodeResource(getResources(), R.drawable.box_07);
        BLOCK_IMAGES[7] = BitmapFactory.decodeResource(getResources(), R.drawable.box_08);
        BLOCK_IMAGES[8] = BitmapFactory.decodeResource(getResources(), R.drawable.box_09);
        BLOCK_IMAGES[9] = BitmapFactory.decodeResource(getResources(), R.drawable.box_10);

        EXP_1[0] = BitmapFactory.decodeResource(getResources(), R.drawable.explosionpink01);
        EXP_1[1] = BitmapFactory.decodeResource(getResources(), R.drawable.explosionpink02);
        EXP_1[2] = BitmapFactory.decodeResource(getResources(), R.drawable.explosionpink03);
        EXP_1[3] = BitmapFactory.decodeResource(getResources(), R.drawable.explosionpink04);
        EXP_1[4] = BitmapFactory.decodeResource(getResources(), R.drawable.explosionpink05);

        BG_IMAGE= BitmapFactory.decodeResource(getResources(), R.drawable.bg_01);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        gamePanel = new GamePanel(this);
        setContentView(gamePanel);
    }


    @Override
    protected void onPause ()
    {
        super.onPause();
        gamePanel.paused = true;

    }

    @Override
    public void onBackPressed ()
    {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        if (!gamePanel.firstTime)
        {
            gamePanel.restarted = true;
        }
        gamePanel.paused = false;
    }
}
