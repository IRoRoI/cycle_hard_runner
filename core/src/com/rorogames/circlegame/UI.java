package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;

import java.net.URI;
import java.util.Date;
import java.util.Timer;

import javax.swing.text.DateFormatter;

public class UI extends Stage {
    final int W = 720;
    final int H = 1280;
    float HEIGHT, WIDTH, startMouseX = 0, startMouseY = 0, kAnim;
    public static float scaleCoeff;
    private int selectedSkin, currentAnim, curLevelProgress, tmpLevelProgress;
    public Vector2 mousePosition, lastMousePosition;
   // public SimpleLineRenderer slr;
    public Image flash;

    public static UI main;
    public static int selectedLanguage;
    private Group restartMenu, restartArcPan, restartLevPan, gameUI, menu, shopPan, levelPan, progressPan, messagePan, skinsPan, skinScroller;
    private Group skillsInGame, skinCostPan, levelEndPan, rocketRecievedText, pausePan, endStatPan, taskPan, taskPanBack, settingsPan, soonPan;
    private Group videoPan;
    private ScrollPane skillsPan;
    private Image progressImg, progressMan, msgPanBack, downLineShop, moneyImage, rocketReward, rocketRecieved,taskPanBImg, taskPanTBord, taskPanBBord;
    private Button btnNext, btnPrev, btnArcade, btnLevels, btnSkills, btnChars, btnSkinSelect, btnRand, btnStand, btnPause, btnAutoRetry, skipTask;
    private Button btnMusic, btnLowGraphics, btnSounds, btnOutline, btnShop;
    public Actor play;

    private Array<Button> skinActors;

    private Color selectedColor, mainColor, mainColorLight, mainColorDark, darkOrange;

    private BitmapFont mainFont;
    private LabelT timerText, bestTimeText, moneyText, tapToPlay, nameLevel, msgPanText;
    private LabelT descripSkin, levelProgress, levelName, gameModeText, progressText, deathCountTxt;
    private LabelT reachText, arcTimeText, newRecText, skinCostText, skinReqLevText, curDeathCText;
    private LabelT deathCPauseText, timePText, timeInGameText, taskText, taskProgressText, taskTimerText;
    private LabelT musicByText, notVideoText;

    private String urlMusic;
    private String[] messagesEN = {
            "TAP on\nscreen!",
            "BUY NEW\nSKIN!",
            "opened new\nskin!",
            "not enough\natoms!",
            "complete\nlevel without\nother skill.",
            "task\ncompleted",
            "purchased!",
            "buy new\nskill!"
    };
    private String[] messagesRU = {
            "Жми на\nэкран!",
            "купите новый\nскин!",
            "открыт новый\nскин!",
            "недостаточно\nатомов!",
            "пройдите\nуровень без\nдоп. навыков.",
            "задача\nвыполнена!",
            "куплено!",
            "купите новый\nнавык!"
    };
    private Array<Image> iconsMsg;
    private SimpleLineRenderer progressLevelCircle;

    private boolean touchDown;
    private float skinScrollDelta;

    public Array<StepSkillPan> stepSkillPans;
    public Array<SkillPan> skillPans;
    private CircleConstructor circleConstructor;

    public UI(final CircleConstructor circleConstructor) {
        super(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        //super(new FitViewport(circleConstructor.WIDTH, circleConstructor.HEIGHT));

        selectedLanguage = Text.RU;//CurrentLocale.locale.equals("ru_RU") ? Text.RU : Text.EN;

        darkOrange = new Color(Color.ORANGE.r *0.7f, Color.ORANGE.g *0.7f, Color.ORANGE.b *0.7f, 1);
        HEIGHT = Gdx.graphics.getHeight();
        WIDTH = Gdx.graphics.getWidth();

        scaleCoeff = Math.min(HEIGHT/H, WIDTH/W);
       // scaleCoeff = 1;
        main = this;

        this.circleConstructor = circleConstructor;
        mousePosition = new Vector2();
        lastMousePosition = new Vector2();
        skinActors = new Array<Button>();
        iconsMsg = new Array<Image>();
        stepSkillPans=  new Array<StepSkillPan>();
        skillPans = new Array<SkillPan>();

        float widthOfElement, heightOfElement;

        BitmapFont font;

        LabelT label1;
        LabelStyle labelStyle;

        Image image;
        Texture texture;

        mainFont = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        mainFont.getData().setScale(48);
        selectedColor = Color.valueOf("#FF4364");
        mainColor = Color.valueOf("#DC143C");
        mainColorLight = new Color(mainColor.r * 1.3f,mainColor.g * 1.3f, mainColor.b * 1.3f, 1);
        mainColorDark = new Color(mainColor.r * 0.4f,mainColor.g * 0.4f, mainColor.b * 0.4f, 1);

        ///INITIALIZEGROUP
        gameUI = new Group();

        restartMenu = new Group();
        restartLevPan = new Group();
        restartArcPan = new Group();

        menu = new Group();

        shopPan = new Group();
        skinsPan = new Group();
        skinScroller = new Group();
        final Group skillScroller = new Group();
        skillScroller.setSize(WIDTH, 1150*scaleCoeff);
        skillsPan = new ScrollPane(skillScroller);

        progressPan = new Group();

        Group forText;

        this.addActor(gameUI);
        this.addActor(restartMenu);
        this.addActor(menu);
        ///GAME
        //widthOfElement = 938*scaleCoeff;
        //heightOfElement = 1532*scaleCoeff;

        texture = new Texture(Gdx.files.internal("ram1.png"));
        image = new Image(texture);
        image.setSize(WIDTH, HEIGHT);
        image.setColor(Color.BLACK);
        image.setPosition(0, 0);
        image.setVisible(true);

        gameUI.addActor(image);

        //TIMER
        forText = new Group();
        forText.setPosition(WIDTH/2, HEIGHT-60*scaleCoeff);
        forText.setScale(0.1f*scaleCoeff*0.25f);
        widthOfElement = WIDTH;
        heightOfElement = 50*scaleCoeff;

        //font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        //font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(mainFont, Color.WHITE);
        timerText = new LabelT("timer", labelStyle);

        timerText.setSize(widthOfElement, heightOfElement);
       // timerText.setPosition(-widthOfElement/2, HEIGHT-80*scaleCoeff);
        timerText.setPosition(-widthOfElement/2, -heightOfElement/2);
        timerText.setAlignment(Align.center);

        forText.addActor(timerText.createShadow(Color.RED, 0, -94*scaleCoeff));
        forText.addActor(timerText);
        gameUI.addActor(forText);

        //SKILLS
        skillsInGame = new Group();
        skillsInGame.setPosition(0,0);
        //skillsInGame.setPosition(WIDTH/2f, 0);

        widthOfElement = WIDTH;
        heightOfElement = 128*scaleCoeff;

        texture = new Texture(Gdx.files.internal("quad.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.WHITE);
        image.setPosition(0, 0);
        image.setVisible(false);

        skillsInGame.addActor(image);


        ///PROGRESSPAN
        final float scaleProgressBar = 2f;
        widthOfElement = 160* scaleProgressBar*scaleCoeff;
        heightOfElement = 26*scaleCoeff * scaleProgressBar;

        texture = new Texture(Gdx.files.internal("progressMain.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColor);
        image.setPosition(WIDTH/2 - widthOfElement/2, HEIGHT - 62*scaleCoeff);
        image.setVisible(true);

        widthOfElement = 25 * scaleProgressBar * scaleCoeff;
        heightOfElement = 25 * scaleProgressBar * scaleCoeff;

        texture = new Texture(Gdx.files.internal("male_base/run/1.png"));
        progressMan = new Image(texture);
        progressMan.setSize(-widthOfElement, heightOfElement);
        progressMan.setColor(Color.valueOf("#FDEAA8"));
        progressMan.setPosition(WIDTH/2 - 80 * scaleProgressBar - widthOfElement/2, HEIGHT - 35*scaleCoeff - heightOfElement);
        progressMan.setVisible(true);

        widthOfElement = 1 * scaleProgressBar * scaleCoeff;
        heightOfElement =6*scaleCoeff * scaleProgressBar;

        texture = new Texture(Gdx.files.internal("quad.png"));
        progressImg = new Image(texture);
        progressImg.setSize(widthOfElement, heightOfElement);
        progressImg.setColor(Color.valueOf("#FDEAA8"));
        progressImg.setPosition(WIDTH/2 - 80 * scaleProgressBar*scaleCoeff + 15*scaleCoeff, HEIGHT - 42*scaleCoeff);
        progressImg.setVisible(true);

        progressPan.addActor(image);
        progressPan.addActor(progressImg);
       // progressPan.addActor(progressMan);

        widthOfElement = WIDTH;
        heightOfElement = 30*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        gameModeText = new LabelT("game_mode", labelStyle);

        gameModeText.setSize(widthOfElement, heightOfElement);
        gameModeText.setPosition(0, HEIGHT-heightOfElement-120*scaleCoeff);
        gameModeText.setAlignment(Align.center);

        ///FLASH
        widthOfElement = WIDTH;
        heightOfElement = HEIGHT;

        texture = new Texture(Gdx.files.internal("quad.png"));
        flash = new Image(texture);
        flash.setSize(widthOfElement, heightOfElement);
        flash.setColor(1,1,1, 1);
        flash.setVisible(false);
        flash.setTouchable(Touchable.disabled);

        widthOfElement = 70*scaleCoeff;
        heightOfElement = 70*scaleCoeff;

        btnPause = new Button(WIDTH - widthOfElement - 5*scaleCoeff, HEIGHT - heightOfElement - 5*scaleCoeff, widthOfElement, heightOfElement, 0, "pauseBtn.png");
        btnPause.setColor(mainColor);
        btnPause.setVisible(true);

        btnPause.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnPause.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnPause.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showPauseMenu();
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        widthOfElement = 1;
        heightOfElement = 25*scaleCoeff;
        forText = new Group();
        forText.setPosition(180*scaleCoeff, HEIGHT-heightOfElement-10*scaleCoeff);
        forText.setScale(0.07f*scaleCoeff*0.25f);
       // font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        //font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(mainFont, mainColorLight);

        timeInGameText = new LabelT("100%", labelStyle);

        timeInGameText.setSize(widthOfElement, heightOfElement);
      //  timeInGameText.setPosition(180*scaleCoeff, HEIGHT-heightOfElement- 20*scaleCoeff);
        timeInGameText.setPosition(-widthOfElement/2, -heightOfElement/2);

        timeInGameText.setAlignment(Align.right);
        forText.addActor(timeInGameText);
        progressPan.addActor(forText);
        gameUI.addActor(flash);
        gameUI.addActor(progressPan);
        gameUI.addActor(gameModeText);

        //PROCESSINGTOUCHES
        widthOfElement = WIDTH;
        heightOfElement = HEIGHT;

        final Actor gamePanel = new Actor();
        gamePanel.setBounds(0, 0, widthOfElement, heightOfElement);
        gamePanel.addListener(new ClickListener() {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                mousePosition.set(x,y);
                circleConstructor.checkVelocityTouch(mousePosition, lastMousePosition);
                lastMousePosition.set(x,y);
                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startMouseX = x;
                startMouseY = y;
                mousePosition.set(x,y);
                lastMousePosition.set(mousePosition);
                circleConstructor.jetpackEffect.complete = false;
                circleConstructor.jetpackEffect.effect.reset();

                touchDown = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                touchDown = false;
                if(!CircleConstructor.isGround) {
                    circleConstructor.canDJump++;
                }
                circleConstructor.checkVelocityTouch(Vector2.Zero, Vector2.Zero);
                super.touchUp(event, x, y, pointer, button);
            }
        });


        gameUI.addActor(gamePanel);
        gameUI.addActor(skillsInGame);
        gameUI.addActor(btnPause);

        ///RESTART MENU
        //BACKGROUND
        widthOfElement = WIDTH;
        heightOfElement = HEIGHT;

        texture = new Texture(Gdx.files.internal("quad.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(0,0,0, 0.8f);

        restartMenu.addActor(image);

        ////ARCADE PANEL
        widthOfElement = WIDTH;
        heightOfElement = 55*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.04f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        arcTimeText = new LabelT("ARC_TIME", labelStyle);

        arcTimeText.setSize(widthOfElement, heightOfElement);
        arcTimeText.setPosition(0, HEIGHT/2+250*scaleCoeff);
        arcTimeText.setAlignment(Align.center);

        restartArcPan.addActor(arcTimeText.createShadow(Color.RED, 0, -4*scaleCoeff));
        restartArcPan.addActor(arcTimeText);

        widthOfElement = WIDTH;
        heightOfElement = 50*scaleCoeff;
        Group nR = new Group();
        nR.setPosition(WIDTH/2, HEIGHT/2+390*scaleCoeff);
        nR.setRotation(20);
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.ORANGE);
        newRecText = new LabelT(selectedLanguage == Text.EN ? "NEW RECORD!" : "Новый рекорд!", labelStyle);

        newRecText.setSize(widthOfElement, heightOfElement);
        newRecText.setPosition(-widthOfElement/2, -heightOfElement/2);
        newRecText.setAlignment(Align.center);

        nR.addAction(Actions.forever(Actions.sequence(Actions.parallel(Actions.scaleTo(1.2f, 1.2f, 0.4f, Interpolation.fade),
                Actions.rotateTo(0,0.4f, Interpolation.fade)),
                Actions.parallel(Actions.scaleTo(1, 1, 0.4f, Interpolation.fade), Actions.rotateTo(-10, 0.4f, Interpolation.fade)),
                Actions.parallel(Actions.scaleTo(1.2f, 1.2f, 0.4f, Interpolation.fade), Actions.rotateTo(0, 0.4f, Interpolation.fade)),
                Actions.parallel(Actions.scaleTo(1, 1, 0.4f, Interpolation.fade), Actions.rotateTo(10, 0.4f, Interpolation.fade)))));
        nR.addActor(newRecText.createShadow(Color.RED, 0, -4*scaleCoeff));
        nR.addActor(newRecText);
        restartMenu.addActor(nR);

        ////LEVEL PANEL
        //LEVEL NAME
        widthOfElement = WIDTH;
        heightOfElement = 50*scaleCoeff;
        forText = new Group();
        forText.setPosition(WIDTH/2, HEIGHT-heightOfElement-1*scaleCoeff);
        forText.setScale(heightOfElement*0.003f*0.25f);

        //font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        //font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(mainFont, Color.ORANGE);
        levelName = new LabelT("level_name", labelStyle);

        levelName.setSize(widthOfElement, heightOfElement);
        //levelName.setPosition(0, HEIGHT-heightOfElement-30*scaleCoeff);
        levelName.setPosition(-widthOfElement/2, -heightOfElement/2);
        levelName.setAlignment(Align.center);

        forText.addActor(levelName.createShadow(Color.RED, 0, -104*scaleCoeff));
        forText.addActor(levelName);
        restartMenu.addActor(forText);

        widthOfElement = WIDTH;
        heightOfElement = 55*scaleCoeff;

        texture = new Texture(Gdx.files.internal("shopPan/cons.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.valueOf("#DC143C"));
        image.setPosition(WIDTH/2-widthOfElement/2, HEIGHT - 140*scaleCoeff);
        image.setVisible(true);

        restartMenu.addActor(image);

        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.valueOf("#DC143C"));
        image.setPosition(WIDTH/2-widthOfElement/2, 95*scaleCoeff);
        image.setVisible(true);

        restartMenu.addActor(image);

        //RESTARTBUTTON
        widthOfElement = 206f*scaleCoeff*0.8f;
        heightOfElement = 135f*scaleCoeff*0.8f;

        Button restartBtn = new Button(15*scaleCoeff, 15*scaleCoeff, widthOfElement, heightOfElement, 0, "main_menu/restartBtn.png");
        //restartBtn.setText(selectedLanguage == Text.EN ? "RESTART" : "ЕЩЕ РАЗ", Color.WHITE);
        restartBtn.setColor(mainColor);
        restartBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newGame(false);
                gameUI.setVisible(true);
                restartMenu.setVisible(false);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        restartMenu.addActor(restartBtn);

        //MAINMENURETURNER
        widthOfElement = 206f*scaleCoeff*0.8f;
        heightOfElement = 135f*scaleCoeff*0.8f;

        Button menuBtn = new Button(WIDTH - 15*scaleCoeff - widthOfElement, 15*scaleCoeff, widthOfElement, heightOfElement, 0, "main_menu/menuBtn.png");
        menuBtn.setColor(mainColor);
        //menuBtn.setText(selectedLanguage == Text.EN ? "MENU" : "МЕНЮ", Color.WHITE);
        menuBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //circleConstructor.setDifficulty(diffs[0]);
                newGame(true);
                gameUI.setVisible(false);
                restartMenu.setVisible(false);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        restartMenu.addActor(menuBtn);

        //PROGRESS LEVEL CIRCLE
        widthOfElement = 400*scaleCoeff;
        heightOfElement = 400*scaleCoeff;

        texture = new Texture(Gdx.files.internal("back_circle.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.valueOf("#DC143C"));
        image.setPosition(WIDTH/2-widthOfElement/2, (HEIGHT - heightOfElement)/2f);
        image.setVisible(true);

        restartLevPan.addActor(image);

        progressLevelCircle = new SimpleLineRenderer(CircleConstructor.centerX, CircleConstructor.centerY, ShapeRenderer.ShapeType.Filled, 8);

        //PROGRESS
        Group prg = new Group();
        prg.setPosition(WIDTH/2, HEIGHT/2f + 270*scaleCoeff);
        widthOfElement = WIDTH;
        heightOfElement = 70*scaleCoeff;

        forText = new Group();
        forText.setPosition(0, 0);
        forText.setScale(heightOfElement*0.003f*0.25f);

       // font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
       // font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(mainFont, Color.WHITE);
        progressText = new LabelT("level_progress", labelStyle);

        progressText.setSize(widthOfElement, heightOfElement);
        progressText.setPosition(-widthOfElement/2, -heightOfElement/2);
        progressText.setAlignment(Align.center);
        forText.addActor(progressText.createShadow(Color.ORANGE, 0, -105*scaleCoeff));
        forText.addActor(progressText);
        prg.addActor(forText);
        prg.addAction(Actions.forever(Actions.sequence(Actions.scaleBy(0.1f,0.1f, 0.3f, Interpolation.fade),
                Actions.scaleBy(-0.1f, -0.1f, 0.3f, Interpolation.fade))));

        restartLevPan.addActor(prg);

        levelEndPan = new Group();
        restartLevPan.addActor(levelEndPan);

        Group cmp = new Group();
        cmp.setPosition(WIDTH/2, HEIGHT/2f + 390*scaleCoeff);
        widthOfElement = WIDTH;
        heightOfElement = 60*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.ORANGE);
        label1 = new LabelT(selectedLanguage == Text.EN ? "Completed!" : "ПРОЙДЕНО!", labelStyle);

        label1.setSize(widthOfElement, heightOfElement);
        label1.setPosition(-widthOfElement/2, -heightOfElement/2);
        label1.setAlignment(Align.center);
        cmp.addActor(label1.createShadow(Color.RED, 0, -5*scaleCoeff));
        cmp.addActor(label1);
        cmp.addAction(Actions.forever(Actions.sequence(Actions.scaleTo(1,1f, 0.3f, Interpolation.fade),
                Actions.scaleTo(1.1f,1.1f, 0.3f, Interpolation.fade))));

        levelEndPan.addActor(cmp);

        ParticleEffectActor p = new ParticleEffectActor(new Vector2(WIDTH/2 - 140*scaleCoeff, HEIGHT/2 + 420*scaleCoeff), CircleConstructor.physWorld, 20, 20, "fireworkPart.p");
        p.effect.scaleEffect(4*UI.scaleCoeff);
        levelEndPan.addActor(p);

        p = new ParticleEffectActor(new Vector2(WIDTH/2 + 140*scaleCoeff, HEIGHT/2 + 420*scaleCoeff), CircleConstructor.physWorld, 20, 20, "fireworkPart.p");
        p.effect.scaleEffect(4*UI.scaleCoeff);
        levelEndPan.addActor(p);

        p = new ParticleEffectActor(new Vector2(WIDTH/2, HEIGHT/2 + 530*scaleCoeff), CircleConstructor.physWorld, 20, 20, "fireworkPart.p");
        p.effect.scaleEffect(4*UI.scaleCoeff);
        levelEndPan.addActor(p);

        widthOfElement = 100*scaleCoeff;
        heightOfElement = 100*scaleCoeff;

        texture = new Texture(Gdx.files.internal("rocket.png"));
        rocketRecieved = new Image(texture);
        rocketRecieved.setSize(widthOfElement, heightOfElement);
        rocketRecieved.setColor(Color.GRAY);
        rocketRecieved.setPosition((WIDTH-widthOfElement)/2, HEIGHT/2f - 540*scaleCoeff);
        rocketRecieved.setVisible(true);

        levelEndPan.addActor(rocketRecieved);

        rocketRecievedText = new Group();
        rocketRecievedText.setPosition(WIDTH/2+60*scaleCoeff, rocketRecieved.getY() + rocketRecieved.getHeight()/2);
        widthOfElement = 1;
        heightOfElement = 25*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.LIGHT_GRAY);

        label1 = new LabelT(selectedLanguage == Text.EN ? "without other skills" : "без доп. навыков", labelStyle);

        label1.setSize(widthOfElement, heightOfElement);
        label1.setPosition(-label1.getPrefWidth()/2, -heightOfElement/2);
        label1.setAlignment(Align.left);

        rocketRecievedText.setScale(0, 1);
        rocketRecievedText.addActor(label1.createShadow(Color.RED, 0, -4*scaleCoeff));
        rocketRecievedText.addActor(label1);
        levelEndPan.addActor(rocketRecievedText);
        levelEndPan.setVisible(false);
/////////////////////////////////////////////////////////////////////////////////////////////////
        pausePan = new Group();

        widthOfElement = 1;
        heightOfElement = 25*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.ORANGE);

        label1 = new LabelT(selectedLanguage == Text.EN ? "Deaths : " : "Смертей : ", labelStyle);
        label1.setSize(widthOfElement, heightOfElement);
        label1.setPosition(WIDTH/2 - 140*scaleCoeff, HEIGHT/2 - 300*scaleCoeff);
        label1.setAlignment(Align.left);

        pausePan.addActor(label1.createShadow(Color.RED, 0, -4*scaleCoeff));
        pausePan.addActor(label1);

        widthOfElement = 1;
        heightOfElement = 25*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.WHITE);

        deathCPauseText = new LabelT("0", labelStyle);
        deathCPauseText.setSize(widthOfElement, heightOfElement);
        deathCPauseText.setPosition(label1.getX() + label1.getPrefWidth(), label1.getY());
        deathCPauseText.setAlignment(Align.left);

        pausePan.addActor(deathCPauseText.createShadow(Color.RED, 0, -4*scaleCoeff));
        pausePan.addActor(deathCPauseText);

        widthOfElement = 1;
        heightOfElement = 25*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.ORANGE);

        label1 = new LabelT(selectedLanguage == Text.EN ? "Time : " : "Время : ", labelStyle);
        label1.setSize(widthOfElement, heightOfElement);
        label1.setPosition(WIDTH/2 - 140*scaleCoeff, HEIGHT/2 - 380*scaleCoeff);
        label1.setAlignment(Align.left);

        pausePan.addActor(label1.createShadow(Color.RED, 0, -4*scaleCoeff));
        pausePan.addActor(label1);

        widthOfElement = 1;
        heightOfElement = 25*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.WHITE);

        timePText = new LabelT("0", labelStyle);
        timePText.setSize(widthOfElement, heightOfElement);
        timePText.setPosition(label1.getX() + label1.getPrefWidth(), label1.getY());
        timePText.setAlignment(Align.left);

        pausePan.addActor(timePText.createShadow(Color.RED, 0, -4*scaleCoeff));
        pausePan.addActor(timePText);

        widthOfElement = 123*scaleCoeff * 0.7f;
        heightOfElement = 121*scaleCoeff * 0.7f;

        btnAutoRetry = new Button(widthOfElement+100*scaleCoeff,  20*scaleCoeff, widthOfElement, heightOfElement, 0, "shopPan/button.png");
        btnAutoRetry.setColor(mainColor);
        btnAutoRetry.images = new Image[1];

        widthOfElement = 123*scaleCoeff*0.4f;
        heightOfElement = 121*scaleCoeff*0.4f;

        texture = new Texture(Gdx.files.internal("shopPan/buttonFill.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((btnAutoRetry.getWidth()- widthOfElement)/2,  (btnAutoRetry.getHeight() - heightOfElement)/2);
        image.setVisible(true);

        btnAutoRetry.images[0] = image;
        btnAutoRetry.images[0].setVisible(circleConstructor.preferences.getBoolean("autoRetry"));
        btnAutoRetry.addActor(btnAutoRetry.images[0]);

        btnAutoRetry.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.setAutoRetry();
                btnAutoRetry.images[0].setVisible(!btnAutoRetry.images[0].isVisible());
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        pausePan.addActor(btnAutoRetry);

        widthOfElement = 1;
        heightOfElement = 25*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.WHITE);

        label1 = new LabelT(selectedLanguage == Text.EN ? "Auto-Retry" : "Авто-Рестарт", labelStyle);
        label1.setSize(widthOfElement, heightOfElement);
        label1.setPosition(btnAutoRetry.getX() + btnAutoRetry.getWidth(), btnAutoRetry.getY() + (btnAutoRetry.getHeight() - heightOfElement)/2);
        label1.setAlignment(Align.left);

        pausePan.addActor(label1.createShadow(Color.RED, 0, -4*scaleCoeff));
        pausePan.addActor(label1);

        widthOfElement = 425*scaleCoeff;
        heightOfElement = 425*scaleCoeff;

        texture = new Texture(Gdx.files.internal("continueBtn.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColor);
        image.setPosition((WIDTH-widthOfElement)/2, (HEIGHT-heightOfElement)/2);
        image.setVisible(true);

        image.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                hidePauseMenu();
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        pausePan.addActor(image);

        restartMenu.addActor(pausePan);

        ////////////////////////////////////////////////////////////////////////

        endStatPan = new Group();

        widthOfElement = 97*scaleCoeff;
        heightOfElement = 101*scaleCoeff;

        texture = new Texture(Gdx.files.internal("shopPan/money.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition(WIDTH/2f-widthOfElement-10*scaleCoeff, HEIGHT/2f - 330*scaleCoeff);
        image.setVisible(true);

        endStatPan.addActor(image);

        widthOfElement = 1;
        heightOfElement = 40*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.WHITE);

        reachText = new LabelT("Reached : ", labelStyle);

        reachText.setSize(widthOfElement, heightOfElement);
        reachText.setPosition(image.getX() + image.getWidth(), image.getY() + (image.getHeight() - heightOfElement)/2);
        reachText.setAlignment(Align.left);

        endStatPan.addActor(reachText.createShadow(Color.RED, 0, -4*scaleCoeff));
        endStatPan.addActor(reachText);
/////
        widthOfElement = 1;
        heightOfElement = 40*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.CORAL);
////
        widthOfElement = 200*scaleCoeff*0.35f;
        heightOfElement = 246*scaleCoeff*0.35f;

        texture = new Texture(Gdx.files.internal("death.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition(WIDTH/2f-widthOfElement-10*scaleCoeff, HEIGHT/2f - 420*scaleCoeff);
        image.setVisible(true);

        endStatPan.addActor(image);

        widthOfElement = 1;
        heightOfElement = 40*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.025f);
        labelStyle = new LabelStyle(font, Color.WHITE);

        curDeathCText = new LabelT("deaths ", labelStyle);

        curDeathCText.setSize(widthOfElement, heightOfElement);
        curDeathCText.setPosition(image.getX() + image.getWidth(), image.getY() + (image.getHeight() - heightOfElement)/2);
        curDeathCText.setAlignment(Align.left);

        endStatPan.addActor(curDeathCText.createShadow(Color.RED, 0, -4*scaleCoeff));
        endStatPan.addActor(curDeathCText);

        restartLevPan.setVisible(false);
        restartArcPan.setVisible(false);
        endStatPan.setVisible(true);
        restartMenu.addActor(restartLevPan);
        restartMenu.addActor(restartArcPan);
        restartMenu.addActor(endStatPan);

        //VIDEO
        videoPan = new Group();

        widthOfElement = WIDTH;
        heightOfElement = HEIGHT;

        texture = new Texture(Gdx.files.internal("quad.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.9f);
        image.setVisible(true);

        videoPan.addActor(image);

        Group ad = new Group();

        widthOfElement = 250*scaleCoeff;
        heightOfElement = 250*scaleCoeff;

        texture = new Texture(Gdx.files.internal("watchAd.png"));
        image = new Image(texture);
        // image.setColor(Color.valueOf("#DC143C"));
        image.setSize(widthOfElement, heightOfElement);
        ad.setPosition(WIDTH/2, HEIGHT/2);
        image.setPosition(-widthOfElement/2, -heightOfElement/2);
        image.setVisible(true);
        image.setColor(mainColor);
        image.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.adInterface.showAfterDeadAd();
            }
        });
        ad.addAction(Actions.forever(Actions.sequence(Actions.scaleBy(0.2f,0.2f, 0.3f, Interpolation.fade),
                Actions.scaleBy(-0.2f, -0.2f, 0.3f, Interpolation.fade))));

        ad.addActor(image);
        videoPan.addActor(ad);

        widthOfElement = 100*scaleCoeff;
        heightOfElement = 50*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.ORANGE);
        label1 = new LabelT(selectedLanguage == Text.EN ? "continue?" : "продолжить?", labelStyle);
        label1.setSize(widthOfElement,heightOfElement);
        label1.setPosition(WIDTH/2-widthOfElement/2, HEIGHT/2 + 190*scaleCoeff);
        label1.setAlignment(Align.center);
        videoPan.addActor(label1.createShadow(Color.RED, 0, -4*scaleCoeff));
        videoPan.addActor(label1);

        widthOfElement = WIDTH;
        heightOfElement = 50*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.015f);
        labelStyle = new LabelStyle(font, Color.GRAY);
        notVideoText = new LabelT(selectedLanguage == Text.EN ? "no thanks" : "нет спасибо", labelStyle);
        notVideoText.setSize(widthOfElement,heightOfElement);
        notVideoText.setPosition(WIDTH/2-widthOfElement/2, HEIGHT/2 - 250*scaleCoeff);
        notVideoText.setAlignment(Align.center);
        notVideoText.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.videoNotViewed();
                videoPan.setVisible(false);
            }
        });
        videoPan.addActor(notVideoText.createShadow(Color.RED, 0, -4*scaleCoeff));
        videoPan.addActor(notVideoText);

        videoPan.setVisible(false);
        this.addActor(videoPan);
        ///MENU
        //TEXTPLAY

        widthOfElement = 100*scaleCoeff;
        heightOfElement = 50*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        tapToPlay = new LabelT(selectedLanguage == Text.EN ? "tap to play" : "играть", labelStyle);
        tapToPlay.setSize(widthOfElement,heightOfElement);
        tapToPlay.setPosition(WIDTH/2-widthOfElement/2, 90*scaleCoeff);
        tapToPlay.setAlignment(Align.center);
        menu.addActor(tapToPlay.createShadow(Color.ORANGE, 0, -4*scaleCoeff));

        widthOfElement = 400*scaleCoeff;
        heightOfElement = 25*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.GRAY);
        musicByText = new LabelT("music by audionautix.com", labelStyle);
        musicByText.setSize(widthOfElement,heightOfElement);
        musicByText.setPosition(WIDTH/2-widthOfElement/2, 20*scaleCoeff);
        musicByText.setAlignment(Align.center);
        musicByText.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.adInterface.redirect(urlMusic);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        menu.addActor(musicByText.createShadow(Color.RED, 0, -4*scaleCoeff));

        widthOfElement = 450*scaleCoeff;
        heightOfElement = 10*scaleCoeff;

        texture = new Texture(Gdx.files.internal("gradientLine.png"));
        image = new Image(texture);
        image.setColor(Color.valueOf("#DC143C"));
        image.setSize(widthOfElement, heightOfElement);
        image.setPosition(WIDTH/2-widthOfElement/2, 75*scaleCoeff);
        image.setVisible(true);

        menu.addActor(image);
        menu.addActor(tapToPlay);

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        labelStyle = new LabelStyle(font, Color.ORANGE);
        bestTimeText = new LabelT("best_time", labelStyle);

        widthOfElement = WIDTH;
        heightOfElement = 30*scaleCoeff;

        bestTimeText.setSize(widthOfElement, heightOfElement);
        bestTimeText.setPosition(0, HEIGHT);
        bestTimeText.setAlignment(Align.center);
        bestTimeText.setFontScale(heightOfElement*0.03f);
        bestTimeText.setVisible(false);
        //menu.addActor(bestTimeText.createShadow(Color.RED, 0, -4*scaleCoeff));

        menu.addActor(bestTimeText);

        //Level
        levelPan = new Group();

        soonPan = new Group();
        widthOfElement = WIDTH;
        heightOfElement = HEIGHT;

        texture = new Texture(Gdx.files.internal("quad.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.5f);
        image.setPosition(0, 0);
        image.setVisible(true);

        soonPan.addActor(image);

        widthOfElement = 1;
        heightOfElement = 45*scaleCoeff;
        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.ORANGE);

        label1 = new LabelT(selectedLanguage == Text.EN ? "soon:)" : "скоро:)", labelStyle);
        label1.setSize(widthOfElement, heightOfElement);
        label1.setPosition((WIDTH-widthOfElement)/2f, (HEIGHT-heightOfElement)/2f);
        label1.setAlignment(Align.center);

        soonPan.addActor(label1);
        levelPan.addActor(soonPan);

        widthOfElement = 428*scaleCoeff;
        heightOfElement = 128*scaleCoeff;

        texture = new Texture(Gdx.files.internal("main_menu/statLevelPan.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColorLight);
        image.setPosition(WIDTH/2-widthOfElement/2, HEIGHT - 145*scaleCoeff);
        image.setVisible(true);

        levelPan.addActor(image);

        widthOfElement = 495*scaleCoeff;
        heightOfElement = 134*scaleCoeff;

        texture = new Texture(Gdx.files.internal("main_menu/levelNamePan.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColorLight);
        image.setPosition(WIDTH/2-widthOfElement/2, HEIGHT - 250*scaleCoeff);
        image.setVisible(true);

        widthOfElement = 100*scaleCoeff;
        heightOfElement = 50*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.023f);
       // font.getData().setScale(12);
        labelStyle = new LabelStyle(font, Color.WHITE);
        nameLevel = new LabelT("Metaphor", labelStyle);
        nameLevel.setColor(Color.ORANGE);
        nameLevel.setSize(widthOfElement,heightOfElement);
        nameLevel.setPosition(WIDTH/2-widthOfElement/2, image.getY() + 40*scaleCoeff);
        nameLevel.setAlignment(Align.center);
       // nameLevel.getStyle().font.getData().setScale(1.8f*scaleCoeff);
        levelPan.addActor(nameLevel.createShadow(Color.RED, 0, -4*scaleCoeff));

        widthOfElement = 50*scaleCoeff;
        heightOfElement = 70*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.017f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        levelProgress = new LabelT("100%", labelStyle);
        levelProgress.setColor(Color.RED);
        levelProgress.setSize(widthOfElement,heightOfElement);
        levelProgress.setPosition(WIDTH/2- widthOfElement/2 - 115*scaleCoeff, image.getY() + image.getHeight());
        levelProgress.setAlignment(Align.center);
        levelPan.addActor(levelProgress.createShadow(mainColorDark, 0, -4*scaleCoeff));

        widthOfElement = 50*scaleCoeff;
        heightOfElement = 35*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.015f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        deathCountTxt = new LabelT("0", labelStyle);
        deathCountTxt.setColor(Color.RED);
        deathCountTxt.setSize(widthOfElement,heightOfElement);
        deathCountTxt.setPosition(WIDTH/2- widthOfElement/2 + 125*scaleCoeff, image.getY() + image.getHeight());
        deathCountTxt.setAlignment(Align.center);
        levelPan.addActor(deathCountTxt.createShadow(mainColorDark, 0, -3*scaleCoeff));

        widthOfElement = 109*scaleCoeff;
        heightOfElement = 121*scaleCoeff;

        btnPrev = new Button(-widthOfElement+20*scaleCoeff + image.getX(), image.getY(), widthOfElement, heightOfElement, 0, "main_menu/prev.png");
        btnPrev.setColor(mainColorDark);
        btnPrev.setVisible(true);

        btnPrev.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnPrev.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnPrev.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                circleConstructor.changeLevel(-1);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        levelPan.addActor(btnPrev);

        btnNext = new Button(image.getX() + image.getWidth()-20*scaleCoeff, image.getY(), widthOfElement, heightOfElement, 0, "main_menu/next.png");
        btnNext.setColor(mainColor);
        btnNext.setVisible(true);

        btnNext.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnNext.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnNext.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                circleConstructor.changeLevel(1);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        levelPan.addActor(btnNext);

        widthOfElement = 256*scaleCoeff;
        heightOfElement = 94*scaleCoeff;

        btnStand = new Button(image.getX(), image.getY()-heightOfElement+25*scaleCoeff, widthOfElement, heightOfElement, (int)(15*scaleCoeff), "main_menu/modeBtn.png");
        btnStand.setColor(Color.ORANGE);
        btnStand.setVisible(true);
        btnStand.setText(selectedLanguage == Text.EN ? "NORMAL" : "НОРМАЛЬНЫЙ", Color.CORAL);

        btnStand.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnStand.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnStand.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                btnStand.setColor(Color.ORANGE);
                btnRand.setColor(mainColor);
                circleConstructor.changeLevelMode(0);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        btnStand.setShadow(Color.RED, 0, -2*scaleCoeff);
        levelPan.addActor(btnStand);

        btnRand = new Button(image.getX()+image.getWidth()-widthOfElement, image.getY()-heightOfElement+25*scaleCoeff, widthOfElement, heightOfElement, (int)(15*scaleCoeff), "main_menu/modeBtn.png");
        btnRand.setColor(mainColor);
        btnRand.setVisible(true);
        btnRand.setText(selectedLanguage == Text.EN ? "RANDOM" : "СЛУЧАЙНЫЙ", Color.CORAL);

        btnRand.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnRand.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnRand.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                btnRand.setColor(Color.ORANGE);
                btnStand.setColor(mainColor);
                circleConstructor.changeLevelMode(1);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        btnRand.setColor(circleConstructor.preferences.getInteger("level_mode") == 1 ? Color.ORANGE : mainColor);
        btnStand.setColor(circleConstructor.preferences.getInteger("level_mode") == 0 ? Color.ORANGE : mainColor);

        btnRand.setShadow(Color.RED, 0, -2*scaleCoeff);
        levelPan.addActor(btnRand);

        levelPan.addActor(nameLevel);
        levelPan.addActor(levelProgress);
        levelPan.addActor(deathCountTxt);
        levelPan.addActor(image);

        widthOfElement = 159*scaleCoeff * 0.2f;
        heightOfElement = 205*scaleCoeff * 0.2f;

        texture = new Texture(Gdx.files.internal("death.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition(WIDTH/2-widthOfElement/2 + 123*scaleCoeff, HEIGHT - 80*scaleCoeff);
        image.setVisible(true);

        levelPan.addActor(image);

        widthOfElement = 219*scaleCoeff * 0.3f;
        heightOfElement = 219*scaleCoeff * 0.3f;

        texture = new Texture(Gdx.files.internal("rocket.png"));
        rocketReward = new Image(texture);
        rocketReward.setSize(widthOfElement, heightOfElement);
        rocketReward.setColor(Color.ORANGE);
        rocketReward.setPosition(WIDTH/2-widthOfElement/2, HEIGHT - 110*scaleCoeff);
        rocketReward.setVisible(true);

        rocketReward.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showMessage(MessageType.SKILL, 4);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        levelPan.addActor(rocketReward);

        //BUTTONTOPLAY

        widthOfElement = WIDTH;
        heightOfElement = HEIGHT;

        play = new Actor();
        play.setBounds(0,0, widthOfElement, heightOfElement);
        play.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //circleConstructor.setDifficulty(diffs[1]);
                newGame(false);
                gameUI.setVisible(true);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        play.setVisible(false);
        menu.addActor(play);
        menu.addActor(levelPan);

        widthOfElement = 123*scaleCoeff;
        heightOfElement = 121*scaleCoeff;

        final Button btnSettings = new Button(WIDTH - 20*scaleCoeff - widthOfElement, 20*scaleCoeff, widthOfElement, heightOfElement, 0, "btnSettings.png");
        btnSettings.setColor(mainColor);
        btnSettings.setVisible(true);

        btnSettings.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnSettings.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnSettings.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsPan.setVisible(true);
                taskPan.addAction(Actions.moveTo(-400*scaleCoeff, taskPan.getY(), 1, Interpolation.fade));
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        menu.addActor(btnSettings);

        btnShop = new Button(20*scaleCoeff, 20*scaleCoeff, widthOfElement, heightOfElement, 0, "btnShop.png");
        btnShop.setColor(mainColor);
        btnShop.setVisible(true);

        btnShop.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnShop.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnShop.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                play.setVisible(false);
                shopPan.setVisible(true);
                btnShop.clearActions();
                btnShop.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                btnShop.setScale(1);
                btnShop.image.setColor(mainColor);
                btnShop.image.clearActions();
                btnShop.setAlpha(1);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        menu.addActor(btnShop);

        widthOfElement = 123*scaleCoeff;
        heightOfElement = 121*scaleCoeff;

        btnArcade = new Button(20*scaleCoeff, HEIGHT - heightOfElement - 10*scaleCoeff, widthOfElement, heightOfElement, 0, "btnArcade.png");
        btnArcade.setColor(mainColor);
        btnArcade.setVisible(true);

        btnArcade.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnArcade.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnArcade.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeGlobalMode(true);
                musicByText.setText("music by freesound.org");
                urlMusic = "https://freesound.org/s/384468/";
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        menu.addActor(btnArcade);

        btnLevels = new Button(WIDTH - widthOfElement - 20*scaleCoeff, HEIGHT - heightOfElement - 10*scaleCoeff, widthOfElement, heightOfElement, 0, "btnLevels.png");
        btnLevels.setColor(selectedColor);
        btnLevels.setVisible(true);

        btnLevels.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnLevels.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnLevels.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeGlobalMode(false);
                tapToPlay.setSize(tapToPlay.getWidth(), 50*scaleCoeff);
                tapToPlay.setColor(Color.WHITE);
                musicByText.setText("music by audionautix.com");
                urlMusic = "http://audionautix.com";
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
              //  tapToPlay.setText(selectedLanguage == Text.EN ? "tap to play");
            }
        });

        menu.addActor(btnLevels);

        ///TASK PANEL
        taskPan = new Group();
        taskPan.setPosition(0, HEIGHT/2 - 200*scaleCoeff);

        widthOfElement =  327*scaleCoeff;
        heightOfElement = 371*scaleCoeff;

        taskPanBack = new Group();
        taskPanBack.setPosition(widthOfElement/2, heightOfElement/2);

        texture = new Texture(Gdx.files.internal("taskPan/back.png"));
        taskPanBImg = new Image(texture);
        taskPanBImg.setSize(widthOfElement, heightOfElement);
        taskPanBImg.setColor(mainColor);
        taskPanBImg.setPosition(-widthOfElement/2,  -heightOfElement/2);
        taskPanBImg.setVisible(true);
        taskPanBack.addActor(taskPanBImg);

        taskPan.addActor(taskPanBack);

        widthOfElement = 158*scaleCoeff;
        heightOfElement = 127*scaleCoeff;

        texture = new Texture(Gdx.files.internal("taskPan/anonim.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColor);
        image.setPosition(-widthOfElement/2,  45*scaleCoeff);
        image.setVisible(true);
        taskPanBack.addActor(image);

        widthOfElement = 293*scaleCoeff;
        heightOfElement = 163*scaleCoeff;

        texture = new Texture(Gdx.files.internal("taskPan/backText.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColor);
        image.setPosition(-widthOfElement/2,  -heightOfElement+ 15*scaleCoeff);
        image.setVisible(true);
        taskPanBack.addActor(image);

        widthOfElement =1;
        heightOfElement = 14f*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.ORANGE);
        taskText = new LabelT("привет!", labelStyle);
        taskText.setPosition(-130*scaleCoeff, image.getY() + image.getHeight() -40*scaleCoeff);
        taskText.setAlignment(Align.topLeft);

        taskPanBack.addActor(taskText.createShadow(mainColor, 0, -1*scaleCoeff));
        taskPanBack.addActor(taskText);

        widthOfElement =1;
        heightOfElement = 16*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        taskProgressText = new LabelT("выполнено : 1", labelStyle);
        taskProgressText.setPosition(-taskProgressText.getPrefWidth()/2, -taskPanBImg.getHeight()/2 + 10*scaleCoeff);
        taskProgressText.setAlignment(Align.center);

        taskPanBack.addActor(taskProgressText.createShadow(mainColor, 0, -2*scaleCoeff));
        taskPanBack.addActor(taskProgressText);

        widthOfElement =1;
        heightOfElement = 30*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        taskTimerText = new LabelT("00:00", labelStyle);
        taskTimerText.setPosition((taskPanBImg.getWidth() - taskTimerText.getPrefWidth())/2, (taskPanBImg.getHeight()-heightOfElement*2)/2);
        taskTimerText.setAlignment(Align.left);

        taskPan.addActor(taskTimerText.createShadow(mainColor, 0, -2*scaleCoeff));
        taskPan.addActor(taskTimerText);

        widthOfElement = 54*scaleCoeff;
        heightOfElement = 52*scaleCoeff;

        texture = new Texture(Gdx.files.internal("taskPan/endlessMode.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition(-140*scaleCoeff,  45*scaleCoeff);
        image.setVisible(true);
        image.addAction(Actions.forever(Actions.sequence(Actions.color(Color.ORANGE, 0.5f, Interpolation.fade), Actions.color(Color.GOLDENROD, 0.5f, Interpolation.fade))));
        taskPanBack.addActor(image);

        widthOfElement = 353*scaleCoeff;
        heightOfElement = 41*scaleCoeff;

        texture = new Texture(Gdx.files.internal("taskPan/tBoard.png"));
        taskPanTBord = new Image(texture);
        taskPanTBord.setSize(widthOfElement, heightOfElement);
        taskPanTBord.setColor(mainColor);
        taskPanTBord.setPosition(-12*scaleCoeff,  taskPanBImg.getHeight()/2 + taskPanBack.getY() - 15*scaleCoeff);
        taskPanTBord.setVisible(true);

        taskPan.addActor(taskPanTBord);

        widthOfElement = 353*scaleCoeff;
        heightOfElement = 41*scaleCoeff;

        texture = new Texture(Gdx.files.internal("taskPan/bBoard.png"));
        taskPanBBord = new Image(texture);
        taskPanBBord.setSize(widthOfElement, heightOfElement);
        taskPanBBord.setColor(mainColor);
        taskPanBBord.setPosition(-12*scaleCoeff,  15*scaleCoeff-heightOfElement);
        taskPanBBord.setVisible(true);

        taskPan.addActor(taskPanBBord);

        widthOfElement = 270*scaleCoeff;
        heightOfElement = 74*scaleCoeff;

        skipTask = new Button(taskPanBBord.getX()+(taskPanBBord.getWidth()-widthOfElement)/2, taskPanBBord.getY()-heightOfElement+25*scaleCoeff, widthOfElement, heightOfElement, 20*scaleCoeff, "taskPan/refreshTask.png");
        skipTask.setColor(mainColor);
        skipTask.setText("150", Color.GOLD);
        skipTask.text.setPosition(skipTask.text.getX(), skipTask.text.getY() + 3*scaleCoeff);
        skipTask.setShadow(Color.RED, 0, -2*scaleCoeff);
        widthOfElement = 40*scaleCoeff;
        heightOfElement = 40*scaleCoeff;

        texture = new Texture(Gdx.files.internal("shopPan/money.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.GOLD);
        image.setPosition(skipTask.getWidth() - widthOfElement - 35*scaleCoeff,  (skipTask.getHeight()-heightOfElement)/2);
        image.setVisible(true);
        skipTask.addActor(image);
        skipTask.setVisible(false);

        skipTask.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                skipTask.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                skipTask.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.skipTask();
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        taskPan.addActor(skipTask);
        taskPan.swapActor(skipTask, taskPanBBord);

        Actor helpPan = new Actor();
        helpPan.setSize(30*scaleCoeff, taskPanBImg.getHeight());
        taskPan.addActor(helpPan);
        helpPan.setPosition(taskPanBImg.getWidth(), taskPanBImg.getY());
        taskPan.addListener(new ClickListener(){
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                if(Gdx.input.getDeltaX() < 0)
                    taskPan.addAction(Actions.moveTo(-400*scaleCoeff, taskPan.getY(), 1, Interpolation.fade));
                else if(Gdx.input.getDeltaX() > 0)
                    taskPan.addAction(Actions.moveTo(0, taskPan.getY(), 1, Interpolation.fade));
            }
        });

      //  taskPanBack.addAction(Actions.scaleTo(1, 0.2f, 4, Interpolation.fade));
       // taskPanTBord.addAction(Actions.moveTo(taskPanTBord.getX(), taskPanBImg.getHeight()*0.2f*0.5f+taskPanBack.getY()- 15*scaleCoeff, 4, Interpolation.fade));
       // taskPanBBord.addAction(Actions.moveTo(taskPanBBord.getX(), taskPanBack.getY() - taskPanBImg.getHeight()*0.2f*0.5f + 15*scaleCoeff-heightOfElement, 4, Interpolation.fade));

        taskPan.setScale(1.3f);
        taskPan.setVisible(false);
        menu.addActor(taskPan);
        menu.addActor(musicByText);

        ///SHOP
        texture = new Texture(Gdx.files.internal("quad.png"));
        Image back = new Image(texture);
        back.setSize(WIDTH, HEIGHT);
        back.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.95f);

        shopPan.addActor(back);

        widthOfElement = 272*scaleCoeff;
        heightOfElement = 110*scaleCoeff;

        Button btn = new Button(7*scaleCoeff, HEIGHT-heightOfElement, widthOfElement, heightOfElement, (int)(19*scaleCoeff), "shopPan/shpBorder.png");
        btn.setText(selectedLanguage == Text.EN ? "SHOP" : "МАГАЗИН", Color.WHITE);
        btn.setColor(mainColor);
        // btn.setAlpha(0.8f);
        btn.text.setPosition(btn.text.getX()-20*scaleCoeff, btn.text.getY()-10*scaleCoeff);
        btn.setShadow(Color.LIGHT_GRAY, 0, -3*scaleCoeff);

        shopPan.addActor(btn);

        Group skinDescripPan = new Group();
        skinsPan.addActor(skinDescripPan);

        widthOfElement = 655*scaleCoeff;
        heightOfElement = 203*scaleCoeff;
        skinDescripPan.setPosition(WIDTH/2, HEIGHT-190*scaleCoeff);

        texture = new Texture(Gdx.files.internal("shopPan/skinDescrip.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColor);
        image.setPosition(-widthOfElement/2,  -heightOfElement/2);
        image.setVisible(true);

        skinDescripPan.addActor(image);

        widthOfElement = 123*scaleCoeff;
        heightOfElement = 121*scaleCoeff;

        btnSkinSelect = new Button(widthOfElement+40*scaleCoeff,  -heightOfElement/2, widthOfElement, heightOfElement, 0, "shopPan/button.png");
        btnSkinSelect.setColor(mainColor);
        btnSkinSelect.images = new Image[2];

        widthOfElement = 97*scaleCoeff*0.7f;
        heightOfElement = 101*scaleCoeff*0.7f;

        texture = new Texture(Gdx.files.internal("shopPan/money.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((btnSkinSelect.getWidth()- widthOfElement)/2,  (btnSkinSelect.getHeight() - heightOfElement)/2);
        image.addAction(Actions.forever(Actions.sequence(Actions.color(Color.ORANGE, 0.5f, Interpolation.fade), Actions.color(Color.GOLD, 0.5f, Interpolation.fade))));
        image.setVisible(true);

        btnSkinSelect.images[0] = image;
        btnSkinSelect.images[0].setVisible(false);
        btnSkinSelect.addActor(btnSkinSelect.images[0]);

        widthOfElement = 123*scaleCoeff*0.6f;
        heightOfElement = 121*scaleCoeff*0.6f;

        texture = new Texture(Gdx.files.internal("shopPan/buttonFill.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((btnSkinSelect.getWidth()- widthOfElement)/2,  (btnSkinSelect.getHeight() - heightOfElement)/2);
        image.setVisible(true);

        btnSkinSelect.images[1] = image;
        btnSkinSelect.images[1].setVisible(circleConstructor.preferences.getInteger("equippedSkin") == selectedSkin);
        btnSkinSelect.addActor(btnSkinSelect.images[1]);

        btnSkinSelect.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btnSkinSelect.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnSkinSelect.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                purchaseSkin(selectedSkin);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        skinDescripPan.addActor(btnSkinSelect);
        //btnSkinSelect.setVisible(false);

        //DESCRIPTION OF SKIN
        widthOfElement = WIDTH/2f;
        heightOfElement = 20*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.ORANGE);
        descripSkin = new LabelT(selectedLanguage == Text.EN ? Character.descriptionEN[Character.Male] : Character.descriptionRU[Character.Male], labelStyle);
        descripSkin.setPosition(-280*scaleCoeff, -20*scaleCoeff);

        skinDescripPan.addActor(descripSkin.createShadow(Color.RED, 0, -3*scaleCoeff));
        skinDescripPan.addActor(descripSkin);

        skinCostPan = new Group();
        skinCostPan.setPosition(WIDTH/2, 220*scaleCoeff);
        skinsPan.addActor(skinCostPan);

        widthOfElement = 661*scaleCoeff;
        heightOfElement = 138*scaleCoeff;

        texture = new Texture(Gdx.files.internal("shopPan/costSkinPan.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setPosition(-widthOfElement/2, -heightOfElement/2);
        image.setColor(mainColor);
        skinCostPan.addActor(image);

        widthOfElement = 97*scaleCoeff*0.6f;
        heightOfElement = 101*scaleCoeff*0.6f;

        texture = new Texture(Gdx.files.internal("shopPan/money.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition(-280*scaleCoeff, -heightOfElement/2);
        skinCostPan.addActor(image);

        widthOfElement = 250*scaleCoeff;
        heightOfElement = 30*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.GOLDENROD);
        skinCostText = new LabelT("skin_cost", labelStyle);
        skinCostText.setSize(widthOfElement, heightOfElement);
        skinCostText.setAlignment(Align.left);
        skinCostText.setPosition(image.getX() + image.getWidth()+20*scaleCoeff, -heightOfElement/2);

        skinCostPan.addActor(skinCostText);

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.RED);
        skinReqLevText = new LabelT("METAPHOR\n(NORMAL)", labelStyle);
        skinReqLevText.setSize(widthOfElement, heightOfElement);
        skinReqLevText.setAlignment(Align.center);
        skinReqLevText.setPosition(40*scaleCoeff, -heightOfElement/2);

        skinCostPan.addActor(skinReqLevText);
        skinCostPan.setVisible(false);

        widthOfElement = WIDTH;
        heightOfElement = 60*scaleCoeff;

        texture = new Texture(Gdx.files.internal("shopPan/cons.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColor);
        image.setPosition(0,  HEIGHT-75*scaleCoeff-heightOfElement);
        image.setVisible(true);

        shopPan.addActor(image);

        downLineShop = new Image(texture);
        downLineShop.setSize(widthOfElement, heightOfElement);
        downLineShop.setColor(mainColor);
        downLineShop.setPosition(0,  90*scaleCoeff);
        downLineShop.setVisible(true);

        //SELECT CHARACTER
        widthOfElement = 178*scaleCoeff;
        heightOfElement = 152*scaleCoeff;

        btnChars = new Button(WIDTH - 150*scaleCoeff - widthOfElement, 0, widthOfElement, heightOfElement, 23, "shopPan/mainBtn2.png");
        btnChars.image.setColor(mainColor);
        btnChars.text.setPosition(btnChars.text.getX(), btnChars.text.getY()-40);

        btnChars.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                btnChars.setColor(mainColor);
                btnSkills.image.setColor(mainColor.r*0.5f, mainColor.g *0.5f, mainColor.b *0.5f, 1);
                shopPan.removeActor(btnSkills);
                shopPan.removeActor(btnChars);
                shopPan.removeActor(downLineShop);
                shopPan.addActor(btnSkills);
                shopPan.addActor(downLineShop);
                shopPan.addActor(btnChars);
                skinsPan.setVisible(true);
                skillsPan.setVisible(false);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        //SELECT SKILL
        widthOfElement = 165*scaleCoeff;
        heightOfElement = 155*scaleCoeff;

        btnSkills = new Button(WIDTH - widthOfElement-13*scaleCoeff, 0, widthOfElement, heightOfElement, 23, "shopPan/mainBtnD.png");
        btnSkills.image.setColor(mainColor.r*0.5f, mainColor.g *0.5f, mainColor.b *0.5f, 1);
        btnSkills.text.setPosition(btnSkills.text.getX()+15, btnSkills.text.getY()-40);

        btnSkills.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                btnSkills.setColor(mainColor);
                btnChars.image.setColor(mainColor.r*0.5f, mainColor.g *0.5f, mainColor.b *0.5f, 1);
                shopPan.removeActor(btnSkills);
                shopPan.removeActor(btnChars);
                shopPan.removeActor(downLineShop);
                shopPan.addActor(btnChars);
                shopPan.addActor(downLineShop);
                shopPan.addActor(btnSkills);
                skinsPan.setVisible(false);
                skillsPan.setVisible(true);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        shopPan.addActor(btnSkills);
        shopPan.addActor(downLineShop);
        shopPan.addActor(btnChars);

        //CLOSE BUTTON
        widthOfElement = 175*scaleCoeff*1;
        heightOfElement = 107*scaleCoeff*1f;

        texture = new Texture(Gdx.files.internal("shopPan/btnClose.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColor);
        image.setPosition((WIDTH - widthOfElement-8*scaleCoeff), HEIGHT -110*scaleCoeff);
        image.setVisible(true);

        image.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                play.setVisible(true);
                shopPan.setVisible(false);
                btnShop.setAlpha(1);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });

        shopPan.addActor(image);

        //MONEY
        widthOfElement = 400*scaleCoeff;
        heightOfElement = 117*scaleCoeff;

        texture = new Texture(Gdx.files.internal("shopPan/moneyPan.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(mainColor);
        image.setPosition(15*scaleCoeff, 0);
        image.setVisible(true);

        shopPan.addActor(image);

        widthOfElement = 250*scaleCoeff;
        heightOfElement = 30*scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.03f);
        labelStyle = new LabelStyle(font, Color.GOLD);
        moneyText = new LabelT("0", labelStyle);
        moneyText.setSize(widthOfElement, heightOfElement);
        moneyText.setAlignment(Align.center);
        moneyText.setPosition(image.getX() + (image.getWidth()- moneyText.getWidth())/2, image.getY() + (image.getHeight() - moneyText.getHeight())/2 + 5*scaleCoeff);

        shopPan.addActor(moneyText.createShadow(Color.RED, 0, -4*scaleCoeff));
        shopPan.addActor(moneyText);

        widthOfElement = 97*scaleCoeff*0.6f;
        heightOfElement = 101*scaleCoeff*0.6f;

        texture = new Texture(Gdx.files.internal("shopPan/money.png"));
        moneyImage = new Image(texture);
        moneyImage.setSize(widthOfElement, heightOfElement);
        moneyImage.setColor(Color.GOLD);
        moneyImage.setPosition(40*scaleCoeff, moneyText.getY()-15*scaleCoeff);
        moneyImage.setVisible(true);

        moneyImage.addAction(Actions.forever(Actions.sequence(Actions.color(Color.ORANGE, 0.5f, Interpolation.fade), Actions.color(Color.GOLD, 0.5f, Interpolation.fade))));
        shopPan.addActor(moneyImage);

        //PANELS IN SHOP
        shopPan.addActor(skinsPan);

        texture = new Texture(Gdx.files.internal("platform.png"));
        image = new Image(texture);
        image.setColor(mainColor);
        image.setSize(WIDTH, 320*scaleCoeff);
        image.setPosition(0, 150*scaleCoeff);
        image.setTouchable(Touchable.disabled);
        //skinsPan.addActor(image);

        skinsPan.addActor(skinScroller);
        //SKINS PANEL
        //CIRCLE

        widthOfElement = 247*scaleCoeff*0.52f;
        heightOfElement = 1293*scaleCoeff*0.52f;
        Button newSkin = createSkinActor(WIDTH/2f, 280*scaleCoeff + heightOfElement/2f, widthOfElement, heightOfElement, "male_base/idle.png");
        skinActors.add(newSkin);

        newSkin.getParent().setScale(1);

        widthOfElement = 234*scaleCoeff*0.48f;
        heightOfElement = 1339*scaleCoeff*0.48f;
        newSkin = createSkinActor(WIDTH/2f + 250*scaleCoeff, 280*scaleCoeff + heightOfElement/2f, widthOfElement, heightOfElement, "girl/idle.png");
        skinActors.add(newSkin);

        newSkin.getParent().setScale(0.5f);

        widthOfElement = 440*scaleCoeff*0.42f;
        heightOfElement = 1607*scaleCoeff*0.42f;
        newSkin = createSkinActor(WIDTH/2f + 500*scaleCoeff, 280*scaleCoeff + heightOfElement/2f, widthOfElement, heightOfElement, "thief/idle.png");
        skinActors.add(newSkin);

        newSkin.getParent().setScale(0.5f);

        widthOfElement = 394*scaleCoeff*0.7f;
        heightOfElement = 847*scaleCoeff*0.7f;
        newSkin = createSkinActor(WIDTH/2f + 750*scaleCoeff, 280*scaleCoeff + heightOfElement/2f, widthOfElement, heightOfElement, "wizard/idle.png");
        skinActors.add(newSkin);

        newSkin.getParent().setScale(0.5f);

        widthOfElement = 407*scaleCoeff*0.52f;
        heightOfElement = 1353*scaleCoeff*0.52f;
        newSkin = createSkinActor(WIDTH/2f + 1000*scaleCoeff, 280*scaleCoeff + heightOfElement/2f, widthOfElement, heightOfElement, "builder/idle.png");
        skinActors.add(newSkin);

        newSkin.getParent().setScale(0.5f);

        widthOfElement = 253*scaleCoeff*0.52f;
        heightOfElement = 1259*scaleCoeff*0.52f;
        newSkin = createSkinActor(WIDTH/2f + 1250*scaleCoeff, 280*scaleCoeff + heightOfElement/2f, widthOfElement, heightOfElement, "robot/idle.png");
        skinActors.add(newSkin);

        newSkin.getParent().setScale(0.5f);
        skinScroller.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                float start = -80;
                if(skinScrollDelta >= start)
                {
                    selectedSkin = 0;
                    changeSkin(selectedSkin);
                    skinScrollDelta = 0;
                    skinActors.get(selectedSkin).getParent().addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fade));
                    skinScroller.addAction(Actions.moveTo(skinScrollDelta, skinScroller.getY(), 0.5f, Interpolation.fade));
                    skinCostPan.setVisible(!circleConstructor.preferences.getBoolean("purchasedSkin" + selectedSkin));
                    skinCostText.setText(Character.costSkins[selectedSkin] + "");
                    skinReqLevText.setText(circleConstructor.getLevelName(Character.reqLevels[selectedSkin], Character.reqLevelModes[selectedSkin]));
                    return;
                }
                if(skinScrollDelta >= (start - 250)*scaleCoeff)
                {
                    selectedSkin = 1;
                    changeSkin(selectedSkin);
                    skinScrollDelta = -250*scaleCoeff;
                    skinActors.get(selectedSkin).getParent().addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fade));
                    skinScroller.addAction(Actions.moveTo(skinScrollDelta, skinScroller.getY(), 0.5f, Interpolation.fade));
                    skinCostPan.setVisible(!circleConstructor.preferences.getBoolean("purchasedSkin" + selectedSkin));
                    skinCostText.setText(Character.costSkins[selectedSkin] + "");
                    skinReqLevText.setText(circleConstructor.getLevelName(Character.reqLevels[selectedSkin], Character.reqLevelModes[selectedSkin]));
                    return;
                }
                if(skinScrollDelta >= (start - 500)*scaleCoeff)
                {
                    selectedSkin = 2;
                    changeSkin(selectedSkin);
                    skinScrollDelta = -500*scaleCoeff;
                    skinActors.get(selectedSkin).getParent().addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fade));
                    skinScroller.addAction(Actions.moveTo(skinScrollDelta, skinScroller.getY(), 0.5f, Interpolation.fade));
                    skinCostPan.setVisible(!circleConstructor.preferences.getBoolean("purchasedSkin" + selectedSkin));
                    skinCostText.setText(Character.costSkins[selectedSkin] + "");
                    skinReqLevText.setText(circleConstructor.getLevelName(Character.reqLevels[selectedSkin], Character.reqLevelModes[selectedSkin]));
                    return;
                }
                if(skinScrollDelta >= (start - 750)*scaleCoeff)
                {
                    selectedSkin = 3;
                    changeSkin(selectedSkin);
                    skinScrollDelta = -750*scaleCoeff;
                    skinActors.get(selectedSkin).getParent().addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fade));
                    skinScroller.addAction(Actions.moveTo(skinScrollDelta, skinScroller.getY(), 0.5f, Interpolation.fade));
                    skinCostPan.setVisible(!circleConstructor.preferences.getBoolean("purchasedSkin" + selectedSkin));
                    skinCostText.setText(Character.costSkins[selectedSkin] + "");
                    skinReqLevText.setText(circleConstructor.getLevelName(Character.reqLevels[selectedSkin], Character.reqLevelModes[selectedSkin]));
                    return;
                }
                if(skinScrollDelta >= (start - 1000)*scaleCoeff)
                {
                    selectedSkin = 4;
                    changeSkin(selectedSkin);
                    skinScrollDelta = -1000*scaleCoeff;
                    skinActors.get(selectedSkin).getParent().addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fade));
                    skinScroller.addAction(Actions.moveTo(skinScrollDelta, skinScroller.getY(), 0.5f, Interpolation.fade));
                    skinCostPan.setVisible(!circleConstructor.preferences.getBoolean("purchasedSkin" + selectedSkin));
                    skinCostText.setText(Character.costSkins[selectedSkin] + "");
                    skinReqLevText.setText(circleConstructor.getLevelName(Character.reqLevels[selectedSkin], Character.reqLevelModes[selectedSkin]));
                    return;
                }
                if(skinScrollDelta >= -1250*scaleCoeff)
                {
                    selectedSkin = 5;
                    changeSkin(selectedSkin);
                    skinScrollDelta = -1250*scaleCoeff;
                    skinActors.get(selectedSkin).getParent().addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fade));
                    skinScroller.addAction(Actions.moveTo(skinScrollDelta, skinScroller.getY(), 0.5f, Interpolation.fade));
                    skinCostPan.setVisible(!circleConstructor.preferences.getBoolean("purchasedSkin" + selectedSkin));
                    skinCostText.setText(Character.costSkins[selectedSkin] + "");
                    skinReqLevText.setText(circleConstructor.getLevelName(Character.reqLevels[selectedSkin], Character.reqLevelModes[selectedSkin]));
                    return;
                }
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                float delta = (Gdx.input.getDeltaX())*2*scaleCoeff;

                skinScrollDelta += delta;
                if(skinScrollDelta < -1250*scaleCoeff)
                    skinScrollDelta = -1250*scaleCoeff;
                if(skinScrollDelta > 0)
                    skinScrollDelta = 0;
                skinScroller.setPosition(skinScrollDelta, skinScroller.getY());
            }
        });
        Actor checkTouch = new Actor();
        checkTouch.setSize(5000, 600*scaleCoeff);
        skinScroller.addActor(checkTouch);
        checkTouch.setPosition(0, 90*scaleCoeff);
        menu.addActor(shopPan);
        //MESSAGE PAN

        messagePan = new Group();
        widthOfElement = 347*scaleCoeff;
        heightOfElement = 150*scaleCoeff;
        messagePan.setPosition(-widthOfElement - 100*scaleCoeff, HEIGHT - heightOfElement - 40*scaleCoeff);

        texture = new Texture(Gdx.files.internal("message_pan/messagePan.png"));
        msgPanBack = new Image(texture);
        msgPanBack.setSize(widthOfElement, heightOfElement);
        msgPanBack.setColor(mainColor);

        messagePan.addActor(msgPanBack);

        widthOfElement = 1 * scaleCoeff;
        heightOfElement = 25 * scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.ORANGE);
        msgPanText = new LabelT("message_pan", labelStyle);
        msgPanText.setPosition(120*scaleCoeff, (msgPanBack.getHeight() - heightOfElement)/2);
        messagePan.addActor(msgPanText.createShadow(Color.RED, 0, -3*scaleCoeff));
        messagePan.addActor(msgPanText);

        widthOfElement = 100*scaleCoeff;
        heightOfElement = 100*scaleCoeff;

        texture = new Texture(Gdx.files.internal("message_pan/iconSkin.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setPosition(15*scaleCoeff, (msgPanBack.getHeight() - heightOfElement)/2);
        image.setColor(Color.ORANGE);

        messagePan.addActor(image);
        iconsMsg.add(image);

        texture = new Texture(Gdx.files.internal("message_pan/iconQuest.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setPosition(15*scaleCoeff, (msgPanBack.getHeight() - heightOfElement)/2);
        image.setColor(Color.ORANGE);

        messagePan.addActor(image);
        iconsMsg.add(image);

        texture = new Texture(Gdx.files.internal("message_pan/iconSkill.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setPosition(15*scaleCoeff, (msgPanBack.getHeight() - heightOfElement)/2);
        image.setColor(Color.ORANGE);

        messagePan.addActor(image);
        iconsMsg.add(image);

        messagePan.setScale(1.3f);
        this.addActor(messagePan);

        //Settings pan
        settingsPan = new Group();
        settingsPan.setPosition(WIDTH/2, HEIGHT/2);
        Actor notT = new Actor();
        notT.setSize(WIDTH, HEIGHT);
        notT.setPosition(-WIDTH/2, -HEIGHT/2);
        settingsPan.addActor(notT);

        widthOfElement = 483*scaleCoeff*1.3f;
        heightOfElement = 313*scaleCoeff*1.3f;

        texture = new Texture(Gdx.files.internal("settingsPan/back.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setPosition(-widthOfElement/2, -heightOfElement/2);
        image.setColor(mainColor);

        settingsPan.addActor(image);

        widthOfElement = 1 * scaleCoeff;
        heightOfElement = 35 * scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        label1 = new LabelT(selectedLanguage == Text.EN ? "Music : " : "музыка : ", labelStyle);
        label1.setPosition(-130*scaleCoeff, 110*scaleCoeff);
        settingsPan.addActor(label1.createShadow(mainColor, 0, -3*scaleCoeff));
        settingsPan.addActor(label1);

        widthOfElement = 123*scaleCoeff * 0.4f;
        heightOfElement = 121*scaleCoeff * 0.4f;

        btnMusic = new Button(label1.getPrefWidth() + label1.getX(),  label1.getY(), widthOfElement, heightOfElement, 0, "shopPan/button.png");
        btnMusic.setColor(mainColor);
        btnMusic.images = new Image[1];

        widthOfElement = 123*scaleCoeff*0.25f;
        heightOfElement = 121*scaleCoeff*0.25f;

        texture = new Texture(Gdx.files.internal("shopPan/buttonFill.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((btnMusic.getWidth()- widthOfElement)/2,  (btnMusic.getHeight() - heightOfElement)/2);

        btnMusic.images[0] = image;
        btnMusic.images[0].setVisible(circleConstructor.preferences.getBoolean("activeMusic"));
        btnMusic.addActor(btnMusic.images[0]);

        btnMusic.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.setActiveMusic();
                btnMusic.images[0].setVisible(!btnMusic.images[0].isVisible());
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        settingsPan.addActor(btnMusic);

        widthOfElement = 1 * scaleCoeff;
        heightOfElement = 35 * scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        label1 = new LabelT(selectedLanguage == Text.EN ? "Sounds : " : "звуки : ", labelStyle);
        label1.setPosition(-110*scaleCoeff, 60*scaleCoeff);
        settingsPan.addActor(label1.createShadow(mainColor, 0, -3*scaleCoeff));
        settingsPan.addActor(label1);

        widthOfElement = 123*scaleCoeff * 0.4f;
        heightOfElement = 121*scaleCoeff * 0.4f;

        btnSounds = new Button(label1.getPrefWidth() + label1.getX(),  label1.getY(), widthOfElement, heightOfElement, 0, "shopPan/button.png");
        btnSounds.setColor(mainColor);
        btnSounds.images = new Image[1];

        widthOfElement = 123*scaleCoeff*0.25f;
        heightOfElement = 121*scaleCoeff*0.25f;

        texture = new Texture(Gdx.files.internal("shopPan/buttonFill.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((btnSounds.getWidth()- widthOfElement)/2,  (btnSounds.getHeight() - heightOfElement)/2);

        btnSounds.images[0] = image;
        btnSounds.images[0].setVisible(circleConstructor.preferences.getBoolean("activeSounds"));
        btnSounds.addActor(btnSounds.images[0]);

        btnSounds.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.setActiveSounds();
                btnSounds.images[0].setVisible(!btnSounds.images[0].isVisible());
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        settingsPan.addActor(btnSounds);

        widthOfElement = 1 * scaleCoeff;
        heightOfElement = 35 * scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        label1 = new LabelT(selectedLanguage == Text.EN ? "fast graphics : " : "быстрая графика : ", labelStyle);
        label1.setPosition(-220*scaleCoeff, 10*scaleCoeff);
        settingsPan.addActor(label1.createShadow(mainColor, 0, -3*scaleCoeff));
        settingsPan.addActor(label1);


        widthOfElement = 123*scaleCoeff * 0.4f;
        heightOfElement = 121*scaleCoeff * 0.4f;

        btnLowGraphics = new Button(label1.getPrefWidth() + label1.getX(),  label1.getY(), widthOfElement, heightOfElement, 0, "shopPan/button.png");
        btnLowGraphics.setColor(mainColor);
        btnLowGraphics.images = new Image[1];

        widthOfElement = 123*scaleCoeff*0.25f;
        heightOfElement = 121*scaleCoeff*0.25f;

        texture = new Texture(Gdx.files.internal("shopPan/buttonFill.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((btnLowGraphics.getWidth()- widthOfElement)/2,  (btnLowGraphics.getHeight() - heightOfElement)/2);

        btnLowGraphics.images[0] = image;
        btnLowGraphics.images[0].setVisible(circleConstructor.preferences.getBoolean("lowGraphics"));
        btnLowGraphics.addActor(btnLowGraphics.images[0]);

        btnLowGraphics.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.setLowGraphics();
                btnLowGraphics.images[0].setVisible(!btnLowGraphics.images[0].isVisible());
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        settingsPan.addActor(btnLowGraphics);

        widthOfElement = 1 * scaleCoeff;
        heightOfElement = 35 * scaleCoeff;

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(heightOfElement*0.02f);
        labelStyle = new LabelStyle(font, Color.WHITE);
        label1 = new LabelT(selectedLanguage == Text.EN ? "outline : " : "обводка : ", labelStyle);
        label1.setPosition(-120*scaleCoeff, -40*scaleCoeff);
        settingsPan.addActor(label1.createShadow(mainColor, 0, -3*scaleCoeff));
        settingsPan.addActor(label1);


        widthOfElement = 123*scaleCoeff * 0.4f;
        heightOfElement = 121*scaleCoeff * 0.4f;

        btnOutline = new Button(label1.getPrefWidth() + label1.getX(),  label1.getY(), widthOfElement, heightOfElement, 0, "shopPan/button.png");
        btnOutline.setColor(mainColor);
        btnOutline.images = new Image[1];

        widthOfElement = 123*scaleCoeff*0.25f;
        heightOfElement = 121*scaleCoeff*0.25f;

        texture = new Texture(Gdx.files.internal("shopPan/buttonFill.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((btnOutline.getWidth()- widthOfElement)/2,  (btnOutline.getHeight() - heightOfElement)/2);

        btnOutline.images[0] = image;
        btnOutline.images[0].setVisible(circleConstructor.preferences.getBoolean("activeOutline"));
        btnOutline.addActor(btnOutline.images[0]);

        btnOutline.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                circleConstructor.setActiveOutline();
                btnOutline.images[0].setVisible(!btnOutline.images[0].isVisible());
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        settingsPan.addActor(btnOutline);

        widthOfElement = 655*scaleCoeff * 0.3f;
        heightOfElement = 196*scaleCoeff * 0.3f;

        Button button = new Button(-widthOfElement/2,  -heightOfElement-70*scaleCoeff, widthOfElement, heightOfElement, 15*scaleCoeff, "shopPan/button2.png");
        button.setText(selectedLanguage == Text.EN ? "Close" : "Закрыть", Color.ORANGE);
        button.setColor(mainColor);
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                settingsPan.setVisible(false);
                circleConstructor.soundManager.play(circleConstructor.soundManager.SOUND_CLICK);
            }
        });
        settingsPan.addActor(button);

        settingsPan.setVisible(false);

        this.addActor(settingsPan);

        ///SKILLS PAN
        widthOfElement = 655*scaleCoeff;
        heightOfElement = 276*scaleCoeff;

        SkillPan skillPan = new SkillPan(Skill.newSkill("not", "doubleJump", 0, circleConstructor), selectedLanguage == Text.EN ? "DOUBLE JUMP" : "ДВОЙНОЙ ПРЫЖОК", (int)(43*scaleCoeff), circleConstructor, (WIDTH - widthOfElement)/2, skillScroller.getY() + skillScroller.getHeight() - heightOfElement, widthOfElement, heightOfElement, 2000);
        skillPan.setReqLabel(selectedLanguage == Text.EN ? "available all modes" : "доступно во всех режимах");
        skillScroller.addActor(skillPan);
        skillPans.add(skillPan);

        skillPan = new SkillPan(Skill.newSkill("skillsIcons/accelBtn.png", "accelerationBtn", 0.5f, circleConstructor), selectedLanguage == Text.EN ? "dash" : "УСКОРЕНИЕ", (int)(43*scaleCoeff), circleConstructor, (WIDTH - widthOfElement)/2, skillScroller.getY() + skillScroller.getHeight() - heightOfElement - 290*scaleCoeff, widthOfElement, heightOfElement, 1000);
        skillPan.skill.onlyArcade = true;
        skillPan.setReqLabel(selectedLanguage == Text.EN ? "only endless and random" : "только в случ. и бесконечном");
        skillScroller.addActor(skillPan);
        skillPans.add(skillPan);

        widthOfElement = 658*scaleCoeff;
        heightOfElement = 279*scaleCoeff;
        StepSkillPan stepSkillPan = new StepSkillPan(Skill.newSkill("not", "multiplier", 0, circleConstructor), selectedLanguage == Text.EN ? "\n\nATOMS\nMULTIPLIER" : "\nМНОЖИТЕЛЬ\nАТОМОВ", (int)(43*scaleCoeff), circleConstructor, (WIDTH - widthOfElement)/2, skillScroller.getY() + skillScroller.getHeight() - heightOfElement - 580*scaleCoeff, widthOfElement, heightOfElement, 1500, 4);
        stepSkillPan.setTexts("x1", "x1.25", "x1.5", "x1.75", "x2");
        stepSkillPan.setValues(1, 1.25f, 1.5f, 1.75f, 2);
        skillScroller.addActor(stepSkillPan);
        stepSkillPans.add(stepSkillPan);

        stepSkillPan = new StepSkillPan(Skill.newSkill("not", "taskTimeRefresh", 0, circleConstructor), selectedLanguage == Text.EN ? "\nrefresh\n task" : "\nобновление\n задачи", (int)(43*scaleCoeff), circleConstructor, (WIDTH - widthOfElement)/2, skillScroller.getY() + skillScroller.getHeight() - heightOfElement - 880*scaleCoeff, widthOfElement, heightOfElement, 500, 4);
        stepSkillPan.setTexts("02:00", "01:30", "01:00", "00:30", "00:10");
        stepSkillPan.setValues(120, 90, 60, 30, 10);
        skillScroller.addActor(stepSkillPan);
        stepSkillPans.add(stepSkillPan);

        skillsPan.setSize(WIDTH, HEIGHT-320*scaleCoeff);
        skillsPan.setPosition(0, 160*scaleCoeff);
       // skillsPan.
        shopPan.addActor(skillsPan);
        // skillsPan.addActor(image);
        ///MENUSETTINGS
        //circleConstructor.setDifficulty(diffs[0]);

        gameUI.setVisible(false);
        restartMenu.setVisible(false);
        menu.setVisible(true);

        shopPan.setVisible(false);
        skinsPan.setVisible(true);
        skillsPan.setVisible(false);

        play.setVisible(true);

        changeGlobalMode(false);
    }

    public void update() {
        if(restartMenu.isVisible() && restartLevPan.isVisible() && restartMenu.getY() < 5) {
            progressText.setVisible(true);
            if (progressLevelCircle.positions.size() < 1.81f*curLevelProgress)
            {
                float x = (float)Math.cos(Math.toRadians(progressLevelCircle.angle))*progressLevelCircle.radius*circleConstructor.cam.zoom*0.5f;
                float y = (float)Math.sin(Math.toRadians(progressLevelCircle.angle))*progressLevelCircle.radius*circleConstructor.cam.zoom*0.5f;
                Vector2 position = new Vector2();
                position.set(x, y);
                progressLevelCircle.positions.add(position);
                progressLevelCircle.angle -= 2;
            }
            if(tmpLevelProgress < curLevelProgress) {
                tmpLevelProgress += 1;
                progressText.setText(tmpLevelProgress + "%");
            }

            progressLevelCircle.setActive(true);
            progressLevelCircle.setProjection(circleConstructor.cam);
            progressLevelCircle.draw(Color.ORANGE, Color.ORANGE);
        }
    }
    public void setProgress(int progress, int percent)
    {
        progressImg.setSize(progress*scaleCoeff, progressImg.getHeight());
        timeInGameText.setText(percent + "%");
    }

    public boolean GetMouseButton()
    {
        return touchDown;
    }
    public void showMessage(int type, int messageIndex)
    {
        messagePan.clearActions();
        msgPanText.setText(selectedLanguage == Text.EN ? messagesEN[messageIndex] : messagesRU[messageIndex]);
        //msgPanText.setPosition(msgPanText.getX(), );
        messagePan.addAction(Actions.sequence(Actions.moveTo(-20*scaleCoeff, messagePan.getY(), 1, Interpolation.fade), Actions.delay(1f),Actions.moveTo(-msgPanBack.getWidth() - 110*scaleCoeff, messagePan.getY(), 1, Interpolation.fade)));
        for(int i = 0; i < iconsMsg.size; i++)
            iconsMsg.get(i).setVisible(false);
        iconsMsg.get(type).setVisible(true);
    }
    public void showMessage(int type, String message)
    {
        messagePan.clearActions();
        msgPanText.setText(message);
        //msgPanText.setPosition(msgPanText.getX(), );
        messagePan.addAction(Actions.sequence(Actions.moveTo(-20*scaleCoeff, messagePan.getY(), 1, Interpolation.fade), Actions.delay(1f),Actions.moveTo(-msgPanBack.getWidth()- 110*scaleCoeff, messagePan.getY(), 1, Interpolation.fade)));
        for(int i = 0; i < iconsMsg.size; i++)
            iconsMsg.get(i).setVisible(false);
        iconsMsg.get(type).setVisible(true);
    }
    public void changeGlobalMode(boolean isArcade)
    {
        circleConstructor.isArcade = isArcade;
        bestTimeText.setVisible(isArcade);
        levelPan.addAction(Actions.moveTo(WIDTH * (isArcade ? 1:0), 0, 0.1f, Interpolation.fade));
        bestTimeText.addAction(Actions.moveTo(0, HEIGHT - (90 * scaleCoeff * (isArcade ? 1:0)), 0.1f, Interpolation.fade));
        //bestTimeText.shadow.addAction(Actions.moveTo(0, HEIGHT - (90 * scaleCoeff * (isArcade ? 1:0)), 0.1f, Interpolation.fade));
        progressPan.setVisible(!isArcade);
        timerText.setVisible(isArcade);
        taskPan.setVisible(isArcade);

        if(isArcade)
        {
            btnArcade.setColor(Color.ORANGE);
            btnLevels.setColor(mainColor);
        }
        else
        {
            btnArcade.setColor(mainColor);
            btnLevels.setColor(Color.ORANGE);
        }
    }
    public void changeLevel(String name, boolean bPrev, boolean bNext)
    {
        btnPrev.setColor(bPrev ? mainColor : mainColorDark);
        btnNext.setColor(bNext ? mainColor : mainColorDark);

        nameLevel.setText(name);
    }

    public void setBGSkillsColor(float r, float g, float b)
    {
        skillsInGame.getChildren().get(0).setColor(r, g, b, 1);
    }
    public void setBGSkillsColor(Color color)
    {
        skillsInGame.getChildren().get(0).setColor(color);
    }

    public void setTime(Integer value)
    {
        timerText.setText(value.toString());
    }
    public void setBestTime(Integer value)
    {
        bestTimeText.setText((selectedLanguage == Text.EN ? "BEST TIME:\n" : "ЛУЧШЕЕ ВРЕМЯ:\n") + value.toString());
    }
    public void setGameModeText(String gameModeText)
    {
        this.gameModeText.clearActions();
        this.gameModeText.setText(gameModeText);
        this.gameModeText.setSize(0.1f, 0.1f);
        this.gameModeText.addAction(Actions.sizeTo(WIDTH,30, 2f, Interpolation.fade));
    }
    public void showPauseMenu()
    {
        levelName.setText(selectedLanguage == Text.EN ? "pause" : "ПАУЗА");
        deathCPauseText.setText(circleConstructor.getCurrentDeaths() + "");
        String minutes = String.valueOf((int)(circleConstructor.getTimer() / 60));
        if(minutes.length() < 2)
            minutes = "0" + minutes;
        String seconds = String.valueOf((int)(circleConstructor.getTimer() % 60));
        if(seconds.length() < 2)
            seconds = "0" + seconds;
        timePText.setText(minutes + ":" + seconds);
        restartMenu.setVisible(true);
        restartArcPan.setVisible(false);
        restartLevPan.setVisible(false);
        endStatPan.setVisible(false);
        pausePan.setVisible(true);
        newRecText.setVisible(false);
        restartMenu.setPosition(0, HEIGHT);
        restartMenu.addAction(Actions.moveTo(0,0, 0.5f, Interpolation.fade));

        circleConstructor.setPause(true);
    }
    public void hidePauseMenu()
    {
        restartMenu.setVisible(false);
        circleConstructor.setPause(false);
    }
    public void hideGameUI()
    {
        gameUI.setVisible(false);
    }
    public void showGameUI(){
        gameUI.setVisible(true);
    }
    public void showRestartMenu(int progress, int reachedMoney, int arcTime, int deathC, boolean newBest)
    {
        rocketRecieved.setColor(Color.GRAY);
        rocketRecieved.setPosition((WIDTH-rocketRecieved.getWidth())/2, HEIGHT/2f - 540*scaleCoeff);
        rocketRecievedText.setScale(0,1);
        rocketRecievedText.clearActions();
        rocketRecieved.clearActions();
        rocketRecievedText.getChildren().get(1).setColor(Color.LIGHT_GRAY);
        rocketRecievedText.getChildren().get(1).addAction(Actions.sequence(Actions.delay(0.5f), Actions.color(Color.YELLOW, 1, Interpolation.fade)));
        rocketRecievedText.addAction(Actions.sequence(Actions.delay(0.5f), Actions.scaleTo(1, 1, 0.5f, Interpolation.fade)));
        rocketRecieved.addAction(Actions.sequence(Actions.delay(0.5f), Actions.moveTo(WIDTH/2 - 250*scaleCoeff, rocketRecieved.getY(), 0.5f, Interpolation.fade), Actions.color(Color.GOLDENROD, 1, Interpolation.fade)));

        gameUI.setVisible(false);
        levelName.setText(arcTime > 0 ? (selectedLanguage == Text.EN ? "ENDLESS" : "БЕСКОНЕЧНЫЙ") : circleConstructor.getCurrentLevelName());
        progressText.setText("0%");
        reachText.setText("+" + reachedMoney);
        curDeathCText.setText(" " + deathC);
        arcTimeText.setText(arcTime + "");
        newRecText.setVisible(newBest);
        restartMenu.setVisible(true);
        endStatPan.setVisible(true);
        pausePan.setVisible(false);
        levelEndPan.setVisible(false);
        restartArcPan.setVisible(circleConstructor.isArcade);
        restartLevPan.setVisible(!circleConstructor.isArcade);
        restartMenu.setPosition(0, HEIGHT);
        restartMenu.addAction(Actions.moveTo(0,0, 0.5f, Interpolation.fade));

        progressLevelCircle.positions.clear();
        progressLevelCircle.radius = 10 * CircleConstructor.radiusPl;
        progressLevelCircle.angle = circleConstructor.getAngle();

        curLevelProgress = progress;
        tmpLevelProgress = 0;
    }
    public void newGame(boolean menu)
    {
        this.menu.setVisible(menu);

        circleConstructor.newGame(menu);
    }
    public void levelComplete(int money, int deaths, boolean hasRocket)
    {
        rocketRecieved.setColor(Color.GRAY);
        rocketRecieved.setPosition((WIDTH-rocketRecieved.getWidth())/2, HEIGHT/2f - 540*scaleCoeff);
        rocketRecievedText.setScale(0,1);
        rocketRecievedText.clearActions();
        rocketRecieved.clearActions();
        rocketRecievedText.getChildren().get(1).setColor(Color.LIGHT_GRAY);
        if(hasRocket) {
            rocketRecievedText.getChildren().get(1).addAction(Actions.sequence(Actions.delay(0.5f), Actions.color(Color.YELLOW, 1, Interpolation.fade)));
            rocketRecievedText.addAction(Actions.sequence(Actions.delay(0.5f), Actions.scaleTo(1, 1, 0.5f, Interpolation.fade)));
            rocketRecieved.addAction(Actions.sequence(Actions.delay(0.5f), Actions.moveTo(WIDTH / 2 - 250 * scaleCoeff, rocketRecieved.getY(), 0.5f, Interpolation.fade), Actions.color(Color.GOLDENROD, 1, Interpolation.fade)));
        }
        else {
            rocketRecievedText.getChildren().get(1).addAction(Actions.sequence(Actions.delay(0.5f)));
            rocketRecievedText.addAction(Actions.sequence(Actions.delay(0.5f), Actions.scaleTo(1, 1, 0.5f, Interpolation.fade)));
            rocketRecieved.addAction(Actions.sequence(Actions.delay(0.5f), Actions.moveTo(WIDTH / 2 - 250 * scaleCoeff, rocketRecieved.getY(), 0.5f, Interpolation.fade)));
        }

        gameUI.setVisible(false);
        levelName.setText(circleConstructor.getCurrentLevelName());
        progressText.setText("0%");
        reachText.setText("+" + money);
        curDeathCText.setText(" " + deaths);
        levelEndPan.setVisible(true);
        restartMenu.setVisible(true);
        endStatPan.setVisible(true);
        pausePan.setVisible(false);
        newRecText.setVisible(false);
        restartArcPan.setVisible(circleConstructor.isArcade);
        restartLevPan.setVisible(!circleConstructor.isArcade);
        restartMenu.setPosition(0, HEIGHT);
        restartMenu.addAction(Actions.moveTo(0,0, 0.5f, Interpolation.fade));

        progressLevelCircle.positions.clear();
        progressLevelCircle.radius = 10 * CircleConstructor.radiusPl;
        progressLevelCircle.angle = circleConstructor.getCamAngle();

        curLevelProgress = 100;
        tmpLevelProgress = 0;
    }
    public void refreshMoney(int money)
    {
        moneyText.setText(String.valueOf(money));
    }

    public void refreshLevelData(int level, int mode)
    {
        levelProgress.setText(String.valueOf(circleConstructor.preferences.getInteger("level_progress"+level+ "mode" + mode)) + "%");
        deathCountTxt.setText(String.valueOf(circleConstructor.preferences.getInteger("level_deaths" + level + "mode" + mode)));
        rocketReward.setColor(circleConstructor.preferences.getBoolean("has_rocket" + level + "mode" + mode) ? Color.ORANGE : Color.BLACK);

        soonPan.setVisible(level>2);
    }
    public void refreshTask(int progress)
    {
        taskProgressText.setText("Выполнено : " + progress);
    }
    public void refreshTask(int progress, int prog2)
    {
        taskProgressText.setText("Выполнено : " + progress);
    }
    public void refreshTaskTimer(float time)
    {
        int t = (int)time;
        String minutes = String.valueOf((t / 60));
        if(minutes.length() < 2)
            minutes = "0" + minutes;
        String seconds = String.valueOf(t % 60);
        if(seconds.length() < 2)
            seconds = "0" + seconds;

        taskTimerText.setText(minutes + ":" + seconds);
    }
    public void newTask(String taskText)
    {
        taskPanBack.addAction(Actions.scaleTo(1, 1, 1, Interpolation.fade));
        taskPanTBord.addAction(Actions.moveTo(-12*scaleCoeff,  taskPanBImg.getHeight()/2 + taskPanBack.getY() - 15*scaleCoeff, 1, Interpolation.fade));
        taskPanBBord.addAction(Actions.moveTo(-12*scaleCoeff,  15*scaleCoeff-taskPanBBord.getHeight(), 1, Interpolation.fade));

        for(int i = 1; i < taskPanBack.getChildren().size; i++)
        {
            taskPanBack.getChildren().get(i).addAction(Actions.sequence(Actions.delay(1), Actions.visible(true), Actions.alpha(1, 1, Interpolation.fade)));
        }
        if(!circleConstructor.mainMenu && !CircleConstructor.isPause)
            showMessage(MessageType.SKILL, selectedLanguage == Text.EN ? "new\ntask!" : "новая\nзадача!");
        taskTimerText.setVisible(false);
        skipTask.addAction(Actions.sequence(Actions.delay(1), Actions.visible(true), Actions.alpha(1, 1, Interpolation.fade)));
        this.taskText.setText(taskText);
    }
    public void completeTask()
    {
        taskPanBack.addAction(Actions.scaleTo(1, 0.2f, 1, Interpolation.fade));
        taskPanTBord.addAction(Actions.moveTo(taskPanTBord.getX(), taskPanBImg.getHeight()*0.2f*0.5f+taskPanBack.getY()- 15*scaleCoeff, 1, Interpolation.fade));
        taskPanBBord.addAction(Actions.moveTo(taskPanBBord.getX(), taskPanBack.getY() - taskPanBImg.getHeight()*0.2f*0.5f + 15*scaleCoeff-taskPanBBord.getHeight(), 1, Interpolation.fade));

        for(int i = 1; i < taskPanBack.getChildren().size; i++)
        {
            taskPanBack.getChildren().get(i).addAction(Actions.sequence(Actions.alpha(0, 0.2f, Interpolation.fade), Actions.visible(false)));
        }
        taskTimerText.setVisible(true);
        skipTask.setVisible(false);
        showMessage(MessageType.SKILL, 5);
    }
    public void withoutTask(){
        taskPanBack.addAction(Actions.scaleTo(1, 0.2f, 1, Interpolation.fade));
        taskPanTBord.addAction(Actions.moveTo(taskPanTBord.getX(), taskPanBImg.getHeight()*0.2f*0.5f+taskPanBack.getY()- 15*scaleCoeff, 1, Interpolation.fade));
        taskPanBBord.addAction(Actions.moveTo(taskPanBBord.getX(), taskPanBack.getY() - taskPanBImg.getHeight()*0.2f*0.5f + 15*scaleCoeff-taskPanBBord.getHeight(), 1, Interpolation.fade));

        for(int i = 1; i < taskPanBack.getChildren().size; i++)
        {
            taskPanBack.getChildren().get(i).addAction(Actions.sequence(Actions.alpha(0, 0.2f, Interpolation.fade), Actions.visible(false)));
        }
        taskTimerText.setVisible(true);
        skipTask.setVisible(false);
    }
    public void refreshSkillsInGame()
    {
        skillsInGame.setVisible(false);
        SnapshotArray<Actor> childs = skillsInGame.getChildren();
        for(int i = 1; i < childs.size; i++)
        {
            skillsInGame.removeActor(childs.get(i));
        }
        float size = 128*scaleCoeff;
        int count = 0;
        Group sG = new Group();
        for(int i = 0; i < circleConstructor.skillsActive.size; i++)
        {
            Skill skill = circleConstructor.skillsActive.get(i);
            skill.resetProgress();
            if(!skill.pathIcon.equalsIgnoreCase("not"))
            {
                skillsInGame.setVisible(true);
                Image image = new Image(new Texture(Gdx.files.internal(skill.pathIcon)));
                image.setSize(size, size);
                image.setPosition((159*scaleCoeff-size)/2f, (159*scaleCoeff-size)/2f);
                image.setColor(((skill.onlyArcade & circleConstructor.isArcade) | !skill.onlyArcade | (!circleConstructor.isArcade) & circleConstructor.getLevelMode() == 1) ? mainColor : Color.GRAY);
                skill.sizeX = size;
                skill.setStep();
                SkillActor skillActor = new SkillActor((count*128 + 15 * (count > 0 ? 1:0))*scaleCoeff, 0, size, size, image, skill, circleConstructor);
                skill.skillActor = skillActor;
                image = new Image(new Texture(Gdx.files.internal("skillsIcons/lightBtn.png")));
                image.setSize(159*scaleCoeff, 159*scaleCoeff);
                image.setPosition(0, 0);
                image.setColor(Color.RED);
                skillActor.addActorAt(0, image);
                sG.addActor(skillActor);
                count++;
            }
        }
        skillsInGame.addActor(sG);
        sG.setPosition((WIDTH - (count*159 - (count-1)*15)*scaleCoeff)/2f,0);
    }
    public void rewardSkin(int skin)
    {
        circleConstructor.preferences.putBoolean("purchasedSkin" + skin, true);
        circleConstructor.preferences.flush();
        if(skin == selectedSkin) {
            btnSkinSelect.images[0].setVisible(false);
            skinCostPan.setVisible(false);
        }
        showMessage(MessageType.SKIN, 2);
    }
    private void purchaseSkin(int skin)
    {
        if (!circleConstructor.preferences.getBoolean("purchasedSkin" + skin)) {
            if (circleConstructor.preferences.getInteger("money") >= Character.costSkins[skin]) {
                circleConstructor.preferences.putBoolean("purchasedSkin" + skin, true);
                circleConstructor.preferences.flush();
                equipSkin(skin);
                circleConstructor.removeMoney(Character.costSkins[skin]);
                skinCostPan.setVisible(false);
            } else
                UI.main.showMessage(MessageType.SKILL, UI.selectedLanguage == Text.EN ? "not enough\natoms!" : "недостаточно\nатомов!");
        } else equipSkin(skin);
    }
    private void equipSkin(int skin)
    {
        if(circleConstructor.preferences.getInteger("equippedSkin") != skin) {
            circleConstructor.preferences.putInteger("equippedSkin", skin);
            circleConstructor.preferences.flush();
            CircleConstructor.player.changeCharacter(skin);
            changeSkin(selectedSkin);
        }
    }

    private void changeSkin(int type)
    {
        btnSkinSelect.images[0].setVisible(!circleConstructor.preferences.getBoolean("purchasedSkin" + type));
        btnSkinSelect.images[1].setVisible(circleConstructor.preferences.getInteger("equippedSkin") == type);
        descripSkin.setText(selectedLanguage == Text.EN ? Character.descriptionEN[type] : Character.descriptionRU[type]);
        for(int i = 0; i < skinActors.size; i++)
            skinActors.get(i).getParent().addAction(Actions.scaleTo(0.5f, 0.5f, 0.1f, Interpolation.fade));
        skinActors.get(selectedSkin).getParent().addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fade));
    }
    private Button createSkinActor(float x, float y, float widthOfElement, float heightOfElement, String path)
    {
        Group skinElement = new Group();
        skinElement.setPosition(x, y);
        Button skinActor = new Button(-widthOfElement/2, -heightOfElement/2f, widthOfElement, heightOfElement, 0, "");

        Texture texture = new Texture(Gdx.files.internal("platform.png"));
        Image image = new Image(texture);
        image.setColor(mainColor);
        image.setSize(464*scaleCoeff, 122f*scaleCoeff);
        image.setPosition(-widthOfElement/2, -70*scaleCoeff);

       // skinActor.addActor(image);

        texture = new Texture(Gdx.files.internal(path));
        image = new Image(texture);
        image.setSize(skinActor.getWidth(), skinActor.getHeight());
        image.setPosition(0, 0);

        skinActor.addActor(image);
        skinActor.userData = 0;

        skinElement.addActor(skinActor);
        skinScroller.addActor(skinElement);

        return skinActor;
    }
    public void showVideoPan()
    {
        videoPan.setVisible(true);
        notVideoText.setVisible(false);
        notVideoText.addAction(Actions.sequence(Actions.delay(0.5f), Actions.visible(true)));
    }
    public void hideVideoPan()
    {
        videoPan.setVisible(false);
    }
    public void newShop()
    {
        btnShop.addAction(Actions.forever(Actions.sequence( Actions.scaleTo(1.1f, 1.1f, 0.5f, Interpolation.fade),
                Actions.scaleTo(1f, 1f, 0.5f, Interpolation.fade))));
        btnShop.image.addAction(Actions.forever(Actions.sequence(Actions.color(Color.CORAL, 0.5f, Interpolation.fade), Actions.color(mainColor, 0.5f, Interpolation.fade))));
    }
}
