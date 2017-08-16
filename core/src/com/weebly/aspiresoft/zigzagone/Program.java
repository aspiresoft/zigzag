package com.weebly.aspiresoft.zigzagone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;
import java.util.Random;

public class Program extends ApplicationAdapter {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 800;
    public static final String TITLE = "ZigZag One";
    public static final float BRICK_WIDTH = 64;
    public static final float BRICK_HEIGHT = 64;

    private static final int SPEED_UP_FREQ = 25;
    private static final int COIN_SPAWN_FREQ = SPEED_UP_FREQ / 2;
    private static final int SPEED_SPAWN_FREQ = SPEED_UP_FREQ * 3;
    private static final long SPEED_DURATION = 8000;

    public enum Status {
        MENU, PLAY, SHOP, LEADERBOARD
    }

    private SpriteBatch batch;
    private Texture background;
    private Texture bar;
    private Texture[] infoIcon;
    private Texture[] holder;
    private Sound coinSound;
    private Sound speedSound;

    private OrthographicCamera camera;
    private OrthographicCamera pivotCamera;
    private Viewport viewport;
    private Viewport pivotViewport;
    private ShapeRenderer shaper;
    private GlyphLayout layout;
    private BitmapFont font;

    private Player player;
    private Array<Brick> bricks;
    private Array<Coin> coins;

    private long lastTime;
    private long restartTimer;
    private boolean canRestart;
    private long speedTimer;
    private float oldSpeed;

    private int pointGathered;
    private String SCORE;
    private Status currentStatus;
    private int dollarGathered;

    private boolean paused;

    private Button PLAY;
    private Button SHOP;
    private Button LEADERBOARD;
    private Button EXIT;
    private Button PAUSE;
    private Button BACK;
    private Button RESTART;

    @Override
    public void create() {
        currentStatus = Status.MENU;
        font = new BitmapFont(Gdx.files.internal("my.fnt"), Gdx.files.internal("my.png"), false);
        InputHandler inputProcessor = new InputHandler(this);
        Gdx.input.setInputProcessor(inputProcessor);
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("BG.png"));

        holder = new Texture[3];
        holder[0] = new Texture(Gdx.files.internal("holder1.png"));
        holder[1] = new Texture(Gdx.files.internal("holder2.png"));
        holder[2] = new Texture(Gdx.files.internal("holder3.png"));
        infoIcon = new Texture[3];
        infoIcon[0] = new Texture(Gdx.files.internal("point.png"));
        infoIcon[1] = new Texture(Gdx.files.internal("dollar.png"));
        infoIcon[2] = new Texture(Gdx.files.internal("speed.png"));
        bar = new Texture(Gdx.files.internal("bar.png"));
        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sound/coin.ogg"));
        speedSound = Gdx.audio.newSound(Gdx.files.internal("Sound/speed.ogg"));

        layout = new GlyphLayout();
        camera = new OrthographicCamera();
        pivotCamera = new OrthographicCamera();
        viewport = new StretchViewport(WIDTH, HEIGHT, camera);
        viewport.apply();
        pivotViewport = new StretchViewport(WIDTH, HEIGHT, pivotCamera);
        pivotViewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        pivotCamera.position.set(pivotCamera.viewportWidth / 2, pivotCamera.viewportHeight / 2, 0);
        shaper = new ShapeRenderer();

        PLAY = new Button(this, WIDTH / 2 - BRICK_WIDTH, HEIGHT / 2 + BRICK_HEIGHT * 7 / 2, BRICK_WIDTH * 2, BRICK_HEIGHT * 2, "play2.png", "play.png");
        SHOP = new Button(this, WIDTH / 2 - BRICK_WIDTH, HEIGHT / 2 + BRICK_HEIGHT * 1 / 2, BRICK_WIDTH * 2, BRICK_HEIGHT * 2, "shop2.png", "shop.png");
        LEADERBOARD = new Button(this, WIDTH / 2 - BRICK_WIDTH, HEIGHT / 2 - BRICK_HEIGHT * 5 / 2, BRICK_WIDTH * 2, BRICK_HEIGHT * 2, "leaderboard2.png", "leaderboard.png");
        EXIT = new Button(this, WIDTH / 2 - BRICK_WIDTH, HEIGHT / 2 - BRICK_HEIGHT * 11 / 2, BRICK_WIDTH * 2, BRICK_HEIGHT * 2, "exit2.png", "exit.png");
        PAUSE = new Button(this, BRICK_WIDTH / 2, BRICK_HEIGHT / 2, BRICK_WIDTH / 2, BRICK_HEIGHT / 2, "exit2.png", "exit.png");
        BACK = new Button(this, WIDTH - BRICK_WIDTH, BRICK_HEIGHT / 2, BRICK_WIDTH / 2, BRICK_HEIGHT / 2, "back2.png", "back.png");
        RESTART = new Button(this, WIDTH / 2 - BRICK_WIDTH, HEIGHT / 2 - BRICK_HEIGHT * 11 / 2, BRICK_WIDTH * 2, BRICK_HEIGHT * 2, "restart2.png", "restart.png");
        init();
    }

    public void init() {
        paused = false;
        canRestart = false;
        lastTime = 0;
        restartTimer = 0;
        speedTimer = 0;
        pointGathered = -1;
        dollarGathered = 0;
        oldSpeed = 0;
        SCORE = Engine.zeroFiller(pointGathered);
        player = new Player(WIDTH / 2 - BRICK_WIDTH / 4, HEIGHT / 2, BRICK_WIDTH / 2, BRICK_HEIGHT / 2, "p1.png");
        bricks = new Array<Brick>();
        coins = new Array<Coin>();
        bricks.add(new Brick(this, player.getX() - BRICK_WIDTH / 4, player.getY() - BRICK_HEIGHT * 7 / 8, BRICK_WIDTH, BRICK_HEIGHT, "Brick/low_05.png", 1));
        for (int i = 0; i < 5; i++) {
            createBrick(false);
        }
        for (int i = 0; i < 45; i++) {
            createBrick();
        }
        camera.position.set(player.getX() + player.getWidth() / 2, player.getY() - HEIGHT / 4 + player.getHeight() / 2, 0);
        camera.update();
    }

    public void update(float dt) {

        if (currentStatus == Status.MENU) {

            PLAY.update(dt);
            SHOP.update(dt);
            LEADERBOARD.update(dt);
            EXIT.update(dt);

            if (PLAY.isClicked()) {
                PLAY.setClicked(false);
                currentStatus = Status.PLAY;
                init();
            }
            if (SHOP.isClicked()) {
                SHOP.setClicked(false);
                currentStatus = Status.SHOP;
            }
            if (LEADERBOARD.isClicked()) {
                LEADERBOARD.setClicked(false);
                currentStatus = Status.LEADERBOARD;
            }
            if (EXIT.isClicked()) {
                EXIT.setClicked(false);
                System.out.println("Are you sure you want to exit?");
            }
        }
        if (currentStatus == Status.PLAY) {

            if (Gdx.input.justTouched()) {
                if (!player.isAlive()) {
                    if (canRestart && RESTART.contains(InputHandler.orgX, InputHandler.orgY)) {
                        init();
                    }
                } else {
                    if (paused) {
                        paused = false;
                    } else {
                        if (!PAUSE.contains(InputHandler.orgX, InputHandler.orgY) && !BACK.contains(InputHandler.orgX, InputHandler.orgY)) {
                            player.setDirection(!player.getDirection());
                        } else {
                            if (PAUSE.contains(InputHandler.orgX, InputHandler.orgY)) {
                                PAUSE.setTouched(false);
                                paused = true;
                            }
                            if (BACK.contains(InputHandler.orgX, InputHandler.orgY)) {
                                BACK.setTouched(false);
                                currentStatus = Status.MENU;
                                return;
                            }
                        }
                    }
                }
            }
            if (!paused) {

                player.update(dt);

                for (Iterator<Brick> iter = bricks.iterator(); iter.hasNext(); ) {
                    iter.next().update(dt);
                }
                if (player.isAlive()) {
                    PAUSE.update(dt);
                    BACK.update(dt);
                    camera.position.set(player.getX() + player.getWidth() / 2, player.getY() - HEIGHT / 4 + player.getHeight() / 2, 0);
                    camera.update();

                    if (player.getNumber() > 0 && player.getNumber() % SPEED_UP_FREQ == 0) {
                        player.setSpeed(1 + (0.1f * (player.getNumber() / SPEED_UP_FREQ)));
                    }


                    if (System.currentTimeMillis() - speedTimer <= SPEED_DURATION) {
                        player.setSpeed(oldSpeed / 2);
                    } else {
                        if (oldSpeed != 0) {
                            player.setSpeed(oldSpeed);
                            oldSpeed = 0;
                        }
                    }

                    boolean felt = true;
                    for (Iterator<Brick> iter = bricks.iterator(); iter.hasNext(); ) {
                        Brick b = iter.next();
                        if (b.alive && !b.buried && b.number <= player.getNumber() + 2) {
                            Vector2[] points = new Vector2[4];
                            points[0] = new Vector2(b.x + b.width / 2, b.y + b.height);
                            points[1] = new Vector2(b.x + b.width, b.y + b.height * 3 / 4);
                            points[2] = new Vector2(b.x + b.width / 2, b.y + b.height / 2);
                            points[3] = new Vector2(b.x, b.y + b.height * 3 / 4);
                            if (Engine.contains(points, new Vector2(player.getX() + player.getWidth() / 2, player.getY()))) {
                                if (!b.gavePoint) {
                                    b.gavePoint = true;
                                    updateScore();
                                }
                                if (player.getNumber() != b.number) {
                                    player.setNumber(b.number);
                                }
                                felt = false;
                            }
                            Coin c = null;
                            for (Coin cn : coins) {
                                if (cn.getNumber() == b.number) {
                                    c = cn;
                                    break;
                                }
                            }
                            if (c != null && c.isAlive()) {
                                Vector2[] pointsCoin = new Vector2[4];

                                if (c.getItemLoc() == 1) {
                                    pointsCoin[0] = new Vector2(b.x, b.y + b.height * 3 / 4);
                                    pointsCoin[1] = new Vector2(b.x + b.width / 4, b.y + b.height * 7 / 8);
                                    pointsCoin[2] = new Vector2(b.x + b.width / 2, b.y + b.height * 3 / 4);
                                    pointsCoin[3] = new Vector2(b.x + b.width / 4, b.y + b.height * 5 / 8);
                                } else if (c.getItemLoc() == 2) {
                                    pointsCoin[0] = new Vector2(b.x + b.width / 4, b.y + b.height * 7 / 8);
                                    pointsCoin[1] = new Vector2(b.x + b.width / 2, b.y + b.height);
                                    pointsCoin[2] = new Vector2(b.x + b.width * 3 / 4, b.y + b.height * 7 / 8);
                                    pointsCoin[3] = new Vector2(b.x + b.width / 2, b.y + b.height * 3 / 4);
                                } else if (c.getItemLoc() == 3) {
                                    pointsCoin[0] = new Vector2(b.x + b.width / 2, b.y + b.height * 3 / 4);
                                    pointsCoin[1] = new Vector2(b.x + b.width * 3 / 4, b.y + b.height * 7 / 8);
                                    pointsCoin[2] = new Vector2(b.x + b.width, b.y + b.height * 3 / 4);
                                    pointsCoin[3] = new Vector2(b.x + b.width * 3 / 4, b.y + b.height * 5 / 8);
                                } else if (c.getItemLoc() == 4) {
                                    pointsCoin[0] = new Vector2(b.x + b.width / 4, b.y + b.height * 5 / 8);
                                    pointsCoin[1] = new Vector2(b.x + b.width / 2, b.y + b.height * 3 / 4);
                                    pointsCoin[2] = new Vector2(b.x + b.width * 3 / 4, b.y + b.height * 5 / 8);
                                    pointsCoin[3] = new Vector2(b.x + b.width / 2, b.y + b.height / 2);
                                }

                                if (Engine.contains(pointsCoin, new Vector2(player.getX() + player.getWidth() / 2, player.getY()))) {
                                    c.setAlive(false);
                                    if (c.getType() == 1) {
                                        dollarGathered++;
                                        coinSound.play(1.0f);
                                    } else if (c.getType() == 2) {
                                        oldSpeed = player.getSpeed();
                                        speedTimer = System.currentTimeMillis();
                                        speedSound.play(1.0f);
                                    }
                                }
                            }


                        }
                    }
                    if (felt) {
                        player.setAlive(false);
                        restartTimer = System.currentTimeMillis();
                    }
                    for (Iterator<Brick> iter = bricks.iterator(); iter.hasNext(); ) {
                        Brick b = iter.next();
                        if (b.y > player.getY()) {
                            b.kill();
                        }
                    }
                    for (Iterator<Brick> iter = bricks.iterator(); iter.hasNext(); ) {
                        Brick b = iter.next();
                        if (b.buried) {
                            iter.remove();
                            b.dispose();
                        }
                    }
                    for (Iterator<Coin> iter = coins.iterator(); iter.hasNext(); ) {
                        Coin c = iter.next();
                        c.update();
                        boolean found = false;
                        for (Brick b : bricks) {
                            if (b.number == c.getNumber()) {
                                if (b.alive) {
                                    found = true;
                                }
                            }
                            if (b.number > c.getNumber()) {
                                break;
                            }
                        }
                        if (!found) {
                            iter.remove();
                        }
                    }
                    for (int i = bricks.size; i < 60; i++) {
                        createBrick();
                    }
                } else { // PLAYER DEAD
                    RESTART.update(dt);
                    if (System.currentTimeMillis() - restartTimer >= 1000) {
                        canRestart = true;
                    }
                }

            }
        }
        if (currentStatus == Status.LEADERBOARD) {

        }
        if (currentStatus == Status.SHOP) {

        }

    }

    public void draw() {
        if (currentStatus == Status.MENU) {
            batch.setProjectionMatrix(pivotCamera.combined);

            batch.begin();
            batch.draw(background, 0, 0, WIDTH, HEIGHT);
            PLAY.render(batch);
            SHOP.render(batch);
            LEADERBOARD.render(batch);
            EXIT.render(batch);


            batch.end();
        }
        if (currentStatus == Status.PLAY) {
            batch.setProjectionMatrix(pivotCamera.combined);

            batch.begin();
            batch.draw(background, 0, 0, WIDTH, HEIGHT);
            batch.end();

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            if (player.isAlive()) {
                for (int i = 0; i < bricks.size; i++) {
                    bricks.get(i).render(batch);
                }
                int last = 0;
                for (int i = 0; i < coins.size; i++) {
                    if (coins.get(i).getY() > player.getY()) {
                        coins.get(i).render(batch);
                    } else {
                        last = i;
                        break;
                    }
                }
                player.render(batch);
                for (int i = last; i < coins.size; i++) {
                    coins.get(i).render(batch);
                }
            } else {
                int last = 0;
                for (int i = 0; i < bricks.size; i++) {
                    if (bricks.get(i).number <= player.getNumber()) {
                        bricks.get(i).render(batch);
                    } else {
                        last = i;
                        break;
                    }
                }
                player.render(batch);
                for (int i = last; i < bricks.size; i++) {
                    bricks.get(i).render(batch);
                }
                for (int i = 0; i < coins.size; i++) {
                    coins.get(i).render(batch);
                }
            }
            batch.end();


            if (paused) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                shaper.setProjectionMatrix(pivotCamera.combined);
                shaper.begin(ShapeRenderer.ShapeType.Filled);
                shaper.setColor(0.3f, 0.3f, 0.3f, 0.5f);
                shaper.rect(0, 0, WIDTH, HEIGHT);
                shaper.end();
                Gdx.gl.glDisable(GL20.GL_BLEND);

                font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                font.getData().setScale(1f);
                font.setColor(0.2f, 0.8f, 1f, 1f);
                layout.setText(font, "CLICK TO RESUME");
                batch.setProjectionMatrix(pivotCamera.combined);
                batch.begin();
                font.draw(batch, layout, (WIDTH / 2 - layout.width / 2), (HEIGHT / 2 + layout.height / 2));
                batch.end();
            } else {
                if (player.isAlive()) {
                    batch.setProjectionMatrix(pivotCamera.combined);

                    batch.begin();
                    font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    font.getData().setScale(0.7f);
                    font.setColor(Color.WHITE);

                    batch.draw(bar, 24, HEIGHT - 24 - BRICK_HEIGHT / 2, BRICK_WIDTH * 2, BRICK_HEIGHT / 2);
                    batch.draw(holder[0], 24 - 4, HEIGHT - 24 - BRICK_HEIGHT / 2 - 4, BRICK_HEIGHT / 2 + 8, BRICK_HEIGHT / 2 + 8);
                    batch.draw(infoIcon[0], 24, HEIGHT - 24 - BRICK_HEIGHT / 2 + 4, BRICK_HEIGHT / 2 - 4, BRICK_HEIGHT / 2 - 4);
                    font.draw(batch, SCORE, 24 + BRICK_HEIGHT / 2 + 8 + 8, HEIGHT - BRICK_HEIGHT / 2);

                    batch.draw(bar, 24 * 2 + BRICK_WIDTH * 2, HEIGHT - 24 - BRICK_HEIGHT / 2, BRICK_WIDTH * 2, BRICK_HEIGHT / 2);
                    batch.draw(holder[1], 24 * 2 - 4 + BRICK_WIDTH * 2, HEIGHT - 24 - BRICK_HEIGHT / 2 - 4, BRICK_HEIGHT / 2 + 8, BRICK_HEIGHT / 2 + 8);
                    batch.draw(infoIcon[1], 24 * 2 + BRICK_WIDTH * 2, HEIGHT - 24 - BRICK_HEIGHT / 2 + 4, BRICK_HEIGHT / 2 - 4, BRICK_HEIGHT / 2 - 4);
                    font.draw(batch, Engine.zeroFiller(dollarGathered), 24 * 2 + BRICK_HEIGHT / 2 + 8 + 8 + BRICK_WIDTH * 2, HEIGHT - BRICK_HEIGHT / 2);

                    batch.draw(bar, 24 * 3 + BRICK_WIDTH * 4, HEIGHT - 24 - BRICK_HEIGHT / 2, BRICK_WIDTH * 2, BRICK_HEIGHT / 2);
                    batch.draw(holder[2], 24 * 3 - 4 + BRICK_WIDTH * 4, HEIGHT - 24 - BRICK_HEIGHT / 2 - 4, BRICK_HEIGHT / 2 + 8, BRICK_HEIGHT / 2 + 8);
                    batch.draw(infoIcon[2], 24 * 3 + BRICK_WIDTH * 4, HEIGHT - 24 - BRICK_HEIGHT / 2 + 4, BRICK_HEIGHT / 2 - 4, BRICK_HEIGHT / 2 - 4);
                    font.draw(batch, "   " + String.format("%.1f", player.getSpeed()), 24 * 3 + BRICK_HEIGHT / 2 + 8 + 8 + BRICK_WIDTH * 4, HEIGHT - BRICK_HEIGHT / 2);

                    if (System.currentTimeMillis() - speedTimer <= SPEED_DURATION) {
                        batch.draw(bar, 24 * 3 + BRICK_WIDTH * 4, HEIGHT - 24 - BRICK_HEIGHT * 3 / 2, BRICK_WIDTH * 2, BRICK_HEIGHT / 2);
                        font.draw(batch, String.format("%.1f", (float) ((SPEED_DURATION - (System.currentTimeMillis() - speedTimer))) / 1000) + "", 24 * 3 + BRICK_HEIGHT / 2 + 8 + 8 + BRICK_WIDTH * 4, HEIGHT - BRICK_HEIGHT * 3 / 2);
                    }
                    PAUSE.render(batch);
                    BACK.render(batch);
                    batch.end();
                } else {
                    Gdx.gl.glEnable(GL20.GL_BLEND);
                    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    shaper.setProjectionMatrix(pivotCamera.combined);
                    shaper.begin(ShapeRenderer.ShapeType.Filled);
                    shaper.setColor(0.3f, 0.3f, 0.3f, 0.5f);
                    shaper.rect(0, 0, WIDTH, HEIGHT);
                    shaper.end();
                    Gdx.gl.glDisable(GL20.GL_BLEND);

                    font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    font.getData().setScale(1f);
                    font.setColor(0.2f, 0.8f, 1f, 1f);
                    layout.setText(font, SCORE);
                    batch.setProjectionMatrix(pivotCamera.combined);
                    batch.begin();
                    font.draw(batch, layout, (WIDTH / 2 - layout.width / 2), (HEIGHT / 2 + layout.height / 2));

                    RESTART.render(batch);
                    batch.end();


                }
            }

        }
        if (currentStatus == Status.LEADERBOARD) {

        }
        if (currentStatus == Status.SHOP) {

        }

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(Gdx.graphics.getDeltaTime());
        draw();
    }

    @Override
    public void dispose() {
        background.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        pivotViewport.update(width, height);
        pivotCamera.position.set(pivotCamera.viewportWidth / 2, pivotCamera.viewportHeight / 2, 0);
    }

    private void createBrick() {
        Random r = new Random();
        float x = bricks.peek().x;
        float y = bricks.peek().y;
        int number = bricks.peek().number;

        if (r.nextBoolean() == false) {
            y -= BRICK_HEIGHT / 4;
            x += BRICK_WIDTH / 2;
        } else {
            y -= BRICK_HEIGHT / 4;
            x -= BRICK_WIDTH / 2;
        }
        bricks.add(new Brick(this, x, y, BRICK_WIDTH, BRICK_HEIGHT, "Brick/low_05.png", (number + 1)));
        int freq = COIN_SPAWN_FREQ + r.nextInt(COIN_SPAWN_FREQ / 4);
        if (number % freq == 0) {
            int rand = r.nextInt(4);
            float sizeX = BRICK_WIDTH / 4;
            float sizeY = BRICK_HEIGHT / 4;
            float x0 = 0, y0 = 0;
            int itemLoc = 0;
            if (rand == 0) {
                x0 = x;
                y0 = y + BRICK_HEIGHT * 3 / 4;
                itemLoc = 1;
            } else if (rand == 1) {
                x0 = x + BRICK_WIDTH / 2 - sizeX;
                y0 = y + BRICK_HEIGHT * 7 / 8;
                itemLoc = 2;
            } else if (rand == 2) {
                x0 = x + BRICK_WIDTH / 2;
                y0 = y + BRICK_HEIGHT * 3 / 4;
                itemLoc = 3;
            } else if (rand == 3) {
                x0 = x + BRICK_WIDTH / 2 - sizeX;
                y0 = y + BRICK_HEIGHT * 5 / 8;
                itemLoc = 4;
            }
            coins.add(new Coin(x0, y0, sizeX * 2, sizeY * 2, number + 1, itemLoc, 1));
            return;
        }
        freq = SPEED_SPAWN_FREQ;
        if (number % freq == 0) {
            int rand = r.nextInt(4);
            float sizeX = BRICK_WIDTH / 4;
            float sizeY = BRICK_HEIGHT / 4;
            float x0 = 0, y0 = 0;
            int itemLoc = 0;
            if (rand == 0) {
                x0 = x;
                y0 = y + BRICK_HEIGHT * 3 / 4;
                itemLoc = 1;
            } else if (rand == 1) {
                x0 = x + BRICK_WIDTH / 2 - sizeX;
                y0 = y + BRICK_HEIGHT * 7 / 8;
                itemLoc = 2;
            } else if (rand == 2) {
                x0 = x + BRICK_WIDTH / 2;
                y0 = y + BRICK_HEIGHT * 3 / 4;
                itemLoc = 3;
            } else if (rand == 3) {
                x0 = x + BRICK_WIDTH / 2 - sizeX;
                y0 = y + BRICK_HEIGHT * 5 / 8;
                itemLoc = 4;
            }
            coins.add(new Coin(x0, y0, sizeX * 2, sizeY * 2, number + 1, itemLoc, 2));
            return;
        }
    }

    private void createBrick(boolean b) {
        float x = bricks.peek().x;
        float y = bricks.peek().y;
        int number = bricks.peek().number;

        if (b == false) {
            y -= BRICK_HEIGHT / 4;
            x += BRICK_WIDTH / 2;
        } else {
            y -= BRICK_HEIGHT / 4;
            x -= BRICK_WIDTH / 2;
        }
        bricks.add(new Brick(this, x, y, BRICK_WIDTH, BRICK_HEIGHT, "Brick/low_05.png", (number + 1)));
    }

    private void updateScore(int amount) {
        pointGathered += amount;
        SCORE = Engine.zeroFiller(pointGathered);
    }

    private void updateScore() {
        pointGathered++;
        SCORE = Engine.zeroFiller(pointGathered);
    }


    public OrthographicCamera getCamera() {
        return camera;
    }

    public OrthographicCamera getPivotCamera() {
        return pivotCamera;
    }

    public Player getPlayer() {
        return player;
    }

}
