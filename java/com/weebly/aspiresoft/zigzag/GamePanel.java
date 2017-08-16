package com.weebly.aspiresoft.zigzag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    public static float GAME_SPEED = 0.5f;
    public static float BALL_SPEED = 5 * Game.DENSTIY;
    public static String TEXT_COLOR = "#ccd0d0ff";
    public static String BACKGROUND_COLOR = "#a39090a0";
    public static long RESTART_DELAY = 800;
    public static float InvertX = 0;
    public static float InvertY = 0;
    public static String FILENAME = "WWW";
    public static int MARGIN = 5;

    public static int backgroundImageIndex;
    public static boolean clicked;
    public static boolean paused;

    public String SCORE;
    public MainThread thread;

    public Context context;
    public Ball ball;
    public boolean felt;
    public long restartTimer;
    public boolean firstTime;
    public int pointGathered;
    public boolean surfaceCreated;
    public int oldHighScore;
    public boolean highScoreBroken;
    public int BLOCK_COUNT;
    public MediaPlayer mPlayer;
    public int STAGE;
    public boolean restarted;
    private long explosionTimer;

    private ArrayList<Animation> animations;
    public ArrayList<Block> blocks;
    private Iterator<Animation> iterAnim;
    private Paint paint, paint2;
    private Rect bounds;

    public GamePanel(Context context) {
        super(context);
        this.context = context;

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        getHolder().addCallback(this);
        setFocusable(true);
        surfaceCreated = false;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        bounds = new Rect();

        paused = false;
        clicked = false;
        firstTime = true;
        thread = new MainThread(getHolder(), this);

        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (thread.getState() == Thread.State.NEW) {
            thread.setRunning(true);
            thread.start();
        } else if (thread.getState() == Thread.State.TERMINATED) {
            thread = new MainThread(getHolder(), this);
            thread.setRunning(true);
            thread.start();
        }
        paused = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        paused = true;
        surfaceCreated = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (firstTime) {
                firstTime = false;
            } else if (restarted) {
                restarted = false;
            } else {
                if (ball.alive) {
                    ball.direction = !ball.direction;
                } else {
                    if (System.currentTimeMillis() - restartTimer >= RESTART_DELAY) {
                        init();
                    }
                }
            }
        }
        return false;
    }

    public void init() {
        highScoreBroken = false;
        backgroundImageIndex = 0;
        STAGE = 1;
        restarted = false;
        if (animations == null) {
            animations = new ArrayList<>();
        } else {
            animations.clear();
        }

        try {
            oldHighScore = Integer.parseInt(readFromFile());
        } catch (Exception e) {
            oldHighScore = 0;
            writeToFile("0");
            Log.d("", e.toString());
            e.printStackTrace();
        }
        playSound("");

        explosionTimer = System.currentTimeMillis();
        paused = false;
        restarted = false;
        InvertX = InvertY = pointGathered = 0;
        felt = false;
        STAGE = 1;
        BLOCK_COUNT = 0;
        pointGathered = 0;
        GAME_SPEED = 0.5f;
        SCORE = Engine.zeroFiller(pointGathered);
        ball = new Ball(Game.SCREEN_HEIGHT / 2 - Game.WIDTH / 8, Game.SCREEN_HEIGHT / 2 - Game.WIDTH / 8);
        ball.currentBlockNumber = 1;

        blocks = new ArrayList<>();

        Block block1 = createBlock((float) Math.floor(ball.x + ball.width / 2 - Game.HEIGHT), ball.y, 0, !ball.direction);
        blocks.add(block1);
        Block block2 = createBlock((float) Math.floor(block1.x + Game.WIDTH / 2), (float) Math.floor(block1.y + Game.HEIGHT / 2), 0, !ball.direction);
        blocks.add(block2);
        Block block3 = createBlock((float) Math.floor(block2.x + Game.WIDTH / 2), (float) Math.floor(block2.y + Game.HEIGHT / 2), 0, !ball.direction);
        blocks.add(block3);
        Block block4 = createBlock((float) Math.floor(block3.x + Game.WIDTH / 2), (float) Math.floor(block3.y + Game.HEIGHT / 2), 0, !ball.direction);
        blocks.add(block4);
        Block block5 = createBlock((float) Math.floor(block4.x + Game.WIDTH / 2), (float) Math.floor(block4.y + Game.HEIGHT / 2), 0, !ball.direction);
        blocks.add(block5);
        Block block6 = createBlock((float) Math.floor(block5.x + Game.WIDTH / 2), (float) Math.floor(block5.y + Game.HEIGHT / 2), 0, !ball.direction);
        blocks.add(block6);

        for (int i = 1; i <= 50; i++) {
            Block b = blocks.get(blocks.size() - 1);
            if (b.number == 23 || b.number == 48 || b.number == 73) {
                addRandomBlock(b.direction);
                addRandomBlock(b.direction);
                addRandomBlock(b.direction);
                addRandomBlock(b.direction);
                addRandomBlock(b.direction);
            } else {
                addRandomBlock();
            }
        }
    }

    public void update() {
        if (paused || restarted) {

        } else {
            if (!firstTime) {
                ball.update();
                for (int i = blocks.size(); i <= 50; i++) {
                    Block b = blocks.get(blocks.size() - 1);
                    if (b.number == 23 || b.number == 48 || b.number == 73 || b.number == 98 ||
                            b.number == 148 || b.number == 198 || b.number == 248 || b.number == 298 ||
                            b.number == 398) {
                        addRandomBlock(b.direction);
                        addRandomBlock(b.direction);
                        addRandomBlock(b.direction);
                        addRandomBlock(b.direction);
                        addRandomBlock(b.direction);
                    } else {
                        addRandomBlock();
                    }
                }
                iterAnim = animations.iterator();
                while (iterAnim.hasNext()) {
                    Animation a = iterAnim.next();
                    if ((a != null) && a.shouldAnimate) {
                        a.update();
                        if (!a.shouldAnimate) {
                            iterAnim.remove();
                        }
                    }
                }
                //=================================================================
                if (ball.alive) {
                    if (ball.direction) {
                        InvertX -= BALL_SPEED * GAME_SPEED;
                        InvertY -= BALL_SPEED / 2 * GAME_SPEED;
                    } else {
                        InvertX += BALL_SPEED * GAME_SPEED;
                        InvertY -= BALL_SPEED / 2 * GAME_SPEED;
                    }
                    Iterator<Block> iter = blocks.iterator();
                    while (iter.hasNext()) {
                        iter.next().update();
                    }
                    iter = blocks.iterator();
                    while (iter.hasNext()) {
                        Block b = iter.next();
                        if (b.alive) {
                            if (b.y + InvertY < ball.y && b.number + 1 <= ball.currentBlockNumber) {
                                b.alive = false;
                                b.time = System.currentTimeMillis();
                            }
                        } else {
                            if (b.dead) {
                                iter.remove();
                            }
                        }
                        b = null;
                    }
                    iter = blocks.iterator();
                    felt = true;
                    while (iter.hasNext()) {
                        Block b = iter.next();
                        if (b.alive && !b.dead && b.number >= ball.currentBlockNumber) {
                            float x = b.x + InvertX;
                            float y = b.y + InvertY;
                            float width = b.width;
                            float height = b.height;
                            PointF pA = new PointF(x + width / 2, y);
                            PointF pB = new PointF(x + width, y + height / 4);
                            PointF pC = new PointF(x + width / 2, y + height / 2);
                            PointF pD = new PointF(x, y + height / 4);
                            float bot_x = ball.x + ball.width / 2;
                            float bot_y = ball.y + ball.height;
                            if (Engine.isInRhombus(bot_x, bot_y, pA, pB, pC, pD)) {
                                if (!b.gavePoint) {
                                    b.gavePoint = true;
                                    updateScore();
                                }
                                if (ball.currentBlockNumber != b.number) {
                                    ball.currentBlockNumber = b.number;
                                }
                                felt = false;
                                break;
                            }
                        }
                        b = null;
                    }
                    switch (ball.currentBlockNumber) {
                        case 25:
                            if (STAGE != 2) {
                                STAGE = 2;
                                GAME_SPEED = 0.60f;
                                backgroundImageIndex = 1;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                        case 50:
                            if (STAGE != 3) {
                                STAGE = 3;
                                GAME_SPEED = 0.70f;
                                backgroundImageIndex = 2;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                        case 75:
                            if (STAGE != 4) {
                                STAGE = 4;
                                GAME_SPEED = 0.80f;
                                backgroundImageIndex = 3;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                        case 100:
                            if (STAGE != 5) {
                                STAGE = 5;
                                GAME_SPEED = 0.90f;
                                backgroundImageIndex = 4;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                        case 150:
                            if (STAGE != 6) {
                                STAGE = 6;
                                GAME_SPEED = 1f;
                                backgroundImageIndex = 5;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                        case 200:
                            if (STAGE != 7) {
                                STAGE = 7;
                                GAME_SPEED = 1.1f;
                                backgroundImageIndex = 6;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                        case 250:
                            if (STAGE != 8) {
                                STAGE = 8;
                                GAME_SPEED = 1.2f;
                                backgroundImageIndex = 7;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                        case 300:
                            if (STAGE != 9) {
                                STAGE = 9;
                                GAME_SPEED = 1.3f;
                                backgroundImageIndex = 8;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                        case 400:
                            if (STAGE != 10) {
                                STAGE = 10;
                                GAME_SPEED = 1.4f;
                                backgroundImageIndex = 9;
                                animations.add(new Animation(ball.x - Game.WIDTH, ball.y - Game.WIDTH, Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                                playSound("NEXT");
                            }
                            break;
                    }
                    if (felt) {
                        ball.alive = false;
                        restartTimer = System.currentTimeMillis();
                        int high = 0;
                        try {
                            high = Integer.parseInt(readFromFile());
                        } catch (Exception e) {
                            high = 0;
                            e.printStackTrace();
                            Log.d("", e.toString());
                            writeToFile("0");
                        }
                        Log.d("", high + "");
                        if (ball.currentBlockNumber > high) {
                            playSound("WIN");
                            writeToFile(ball.currentBlockNumber + "");
                            highScoreBroken = true;
                        } else {
                            playSound("LOSE");
                        }
                    }
                } else {
                    if (highScoreBroken) {
                        if (System.currentTimeMillis() - explosionTimer > 250) {
                            explosionTimer = System.currentTimeMillis();
                            animations.add(new Animation(new Random().nextInt(Game.SCREEN_HEIGHT), new Random().nextInt(Game.SCREEN_WIDTH), Game.WIDTH * 2, Game.WIDTH * 2, 300, false));
                        }
                    }
                }
            }
        }
    }

    private void playSound(String id) {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        switch (id) {
            case "WIN":

                mPlayer = MediaPlayer.create(context, R.raw.win);
                mPlayer.start();
                break;
            case "LOSE":
                mPlayer = MediaPlayer.create(context, R.raw.lose);
                mPlayer.start();
                break;
            case "NEXT":
                mPlayer = MediaPlayer.create(context, R.raw.run);
                mPlayer.start();
                break;
        }
    }

    public void render(Canvas canvas) {
        if (paused) {
        } else {
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);
                //canvas.drawColor(Color.parseColor(Game.BG_COLORS[baGckgroundImageIndex]));
                canvas.drawBitmap(Game.BG_IMAGE, null, new RectF(0, 0, Game.SCREEN_HEIGHT, Game.SCREEN_WIDTH), null);
                for (Block b : blocks) {
                    if (b.number > ball.currentBlockNumber) {
                        break;
                    } else {
                        b.render(canvas);
                    }
                }
                ball.render(canvas);
                for (Block b : blocks) {
                    if (b.number > ball.currentBlockNumber) {
                        b.render(canvas);
                    }
                }

                for (Iterator<Animation> iter = animations.iterator();
                     iter.hasNext(); ) {
                    iter.next().draw(canvas);
                }


                if (firstTime) {
                    paint.setColor(Color.parseColor(BACKGROUND_COLOR));
                    canvas.drawRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
                    drawStringAllignMidScreen(canvas, "ZigZag", TEXT_COLOR, 0, canvas.getHeight() / 4, 50);
                    drawStringAllignMidScreen(canvas, "Tap to Start", TEXT_COLOR, 0, canvas.getHeight() / 4 * 2, 40);
                } else {
                    if (restarted) {
                        paint.setColor(Color.parseColor(BACKGROUND_COLOR));
                        canvas.drawRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
                        drawStringAllignMidScreen(canvas, "Paused", TEXT_COLOR, 0, canvas.getHeight() / 2, 40);
                    } else {
                        if (!ball.alive) {
                            paint.setColor(Color.parseColor(BACKGROUND_COLOR));
                            canvas.drawRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
                            drawStringAllignMidScreen(canvas, (Integer.parseInt(SCORE)) + "", TEXT_COLOR, 0, canvas.getHeight() / 4, 50);
                            drawStringAllignMidScreen(canvas, "Tap to start again", TEXT_COLOR, 0, canvas.getHeight() / 4 * 2, 40);
                            if (highScoreBroken) {
                                drawStringAllignMidScreen(canvas, "NEW RECORD", TEXT_COLOR, 0, canvas.getHeight() / 4 * 3, 50);
                            } else {
                                drawStringAllignMidScreen(canvas, oldHighScore + "", TEXT_COLOR, 0, canvas.getHeight() / 4 * 3, 50);
                            }

                        } else {
                            drawStringAllignLeftScreen(canvas, SCORE, TEXT_COLOR, 0, (Game.DENSTIY * 5), 20);
                            drawStringAllignRightScreen(canvas, "STAGE " + STAGE, TEXT_COLOR, 0, (int) (Game.DENSTIY * 5), 20);
                        }
                    }
                }
            }
        }
    }

    public void drawStringAllignMidScreen(Canvas c, String text, String color, float x, float y, float size) {
        paint2.setTextSize((int) (size * Game.DENSTIY));
        paint2.getTextBounds(text, 0, text.length(), bounds);

        x = (c.getWidth() - bounds.width()) / 2;
        y += bounds.height();

        paint2.setColor(Color.parseColor(color));
        c.drawText(text, x, y, paint);
    }

    public void drawStringAllignLeftScreen(Canvas c, String text, String color, float x, float y, float size) {
        paint2.setTextSize((int) (size * Game.DENSTIY));
        paint2.getTextBounds(text, 0, text.length(), bounds);

        x = MARGIN * Game.DENSTIY;
        y += bounds.height();

        paint2.setColor(Color.parseColor(color));
        c.drawText(text, x, y, paint);
    }

    public void drawStringAllignRightScreen(Canvas c, String text, String color, float x, float y, float size) {
        paint2.setTextSize((int) (size * Game.DENSTIY));
        paint2.getTextBounds(text, 0, text.length(), bounds);

        x = c.getWidth() - bounds.width() - Game.DENSTIY * MARGIN;
        y += bounds.height();

        paint2.setColor(Color.parseColor(color));
        c.drawText(text, x, y, paint);
    }

    public void addRandomBlock() {
        Block b = blocks.get(blocks.size() - 1);
        float x = 0;
        float y = 0;
        boolean dir = true;
        y = b.y + Game.HEIGHT / 2;
        if (new Random().nextInt(2) == 0) {
            x = b.x + Game.WIDTH / 2;
            dir = false;
        } else {
            x = b.x - Game.WIDTH / 2;
        }
        blocks.add(createBlock(x, y, 0, dir));
    }

    public void addRandomBlock(boolean bool) {
        Block b = blocks.get(blocks.size() - 1);
        float x = 0;
        float y = 0;
        y = b.y + Game.HEIGHT / 2;
        if (!bool) {
            x = b.x + Game.WIDTH / 2;
        } else {
            x = b.x - Game.WIDTH / 2;
        }
        blocks.add(createBlock(x, y, 0, bool));
    }

    private Block createBlock(float x, float y, int id, boolean direction) {
        BLOCK_COUNT++;
        return new Block(x, y, id, BLOCK_COUNT, direction);
    }

    private void updateScore(int amount) {
        pointGathered += amount;
        SCORE = Engine.zeroFiller(pointGathered);
    }

    private void updateScore() {
        pointGathered++;
        SCORE = Engine.zeroFiller(pointGathered);
    }

    private String readFromFile() {
        String ret = "";
        File f = context.getDir(FILENAME, context.MODE_PRIVATE);
        try {
            InputStream inputStream = context.openFileInput(FILENAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                boolean b = true;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    b = false;
                    stringBuilder.append(receiveString);
                }


                inputStream.close();
                ret = stringBuilder.toString();
                if (stringBuilder.toString().isEmpty()) {
                    ret = "0";
                }
                if (b) {
                    ret = "0";
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("", "File not found: " + e.toString());
            try {
                f.createNewFile();
                ret = "0";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            Log.e("", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILENAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("", "File write failed: " + e.toString());
        }
    }


}
