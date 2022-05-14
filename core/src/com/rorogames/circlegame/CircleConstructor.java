package com.rorogames.circlegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.net.URL;
import java.util.Currency;
import java.util.EmptyStackException;
import java.util.Random;

import javax.sound.sampled.Line;


public class CircleConstructor extends Game {
    static CircleConstructor main;
    //CATEGORIES AND MASKS
	final public static short CATEGORY_PLAYER = 0x0001;
	final public static short CATEGORY_BORDERS = 0x0002;
	final public static short CATEGORY_CIRCLE = 0x0004;
	final public static short CATEGORY_DESTROYER = 0x0008;

	final public static short MASK_PLAYER = -1;
	final public static short MASK_BORDERS= CATEGORY_PLAYER;
	final public static short MASK_CIRCLE = CATEGORY_PLAYER;
	final public static short MASK_DESTROYER = CATEGORY_PLAYER;

	//POSES
    private float x, y, angle, jumpStep, playerSCoeffX, playerSCoeffY, plRad, borderCoeff, lastLen, realLastLen, jumpValue, genCoeff, lastR;
   public float camCoeff;
    static float centerX, centerY;
   	static float lenPlayer, realLenPlayer;
    static float radiusTmp, radiusPl, tempRForGen, rPlbefore;

    private byte[] deltas = {-1, 1};
   // private int deltaOP;

    //TIMERS
    private float timer, timeLevelCmd, levelEndTimer;
    private int numCmd, countCmd;
    private double angleInRadians;
	public OrthographicCamera cam;

	//SETTINGS
	public AdInterface adInterface;
	public String localeText;
	private final int STATE_SLOW = 0, STATE_ACCEL = 1, STATE_NORMAL = 2;
	private int STATE;
	private int money, bestTime, currentLevel, currentLevelCmds, currentPreset, currentDeaths, adsDeaths, playerSTAGE, difficulty;
	private float dZoom, stockLenPl, stockCamZoom = 1, speedCamCoeff, speedColor, camAngle, scaleFactor, dImZoom, nextSF;
	private float curProgressVal, progressVal, gravityAcceleration, deathTimer;
	private Vector2 curTouchPos, lastTouchPos, velocityTouch;
	public Preferences preferences;
    private float lnthOp, lOp, emptyLength;
    public float WIDTH, HEIGHT;
	private int operation, lOperation, iterations;

	private Array<Level> levels;
	private Integer[][] diffLevels = {

			//EASY PEASY
			//{18, 20, 21, 2, 4, 22, 23, 24, 25},
			{3, 4, 26},
			//THRESHOLD
			{0},//{1, 2, 3, 4, 5, 6, 2, 10, 12, 14, 17, 16, 25, 40},
			//EPIC SERIES
			{0,2,3,4,5,6,7,10, 12, 13, 14},
			//BIG CAR THEFT
			{0, 17,40, 33, 6, 2, 3, 9, 10, 13, 14, 15},
			//2 Above Zero
            {0, 2, 9, 15, 16, 17},
			//PHASE SHIFTER
			{1,2,3,4,5,6,7,10, 12, 13, 14, 11, 9, 15, 16, 17}
			//{22, 16, 17, 18, 23, 27, 28, 29, 31, 32, 33, 34, 35, 36}, {2,17, 12, 13, 14, 16, 18,19,4, 5, 15,7,3,9,8, 21, 30}, {7,3,9, 12,14}, {8, 4, 3}
	};
	private Array<Integer> diffArcade;
	private Integer[][] diffBaseArcade = {
			{2, 3,4, 5, 18},
			{6, 2, 7, 17, 15, 26, 40, 38, 46, 33, 45, 63, 58, 59, 8, 9, 13, 14, 85, 77, 74, 73},
			{7, 10, 5, 4, 11, 33, 39, 32, 2, 9, 8, 12, 31, 17, 60, 61, 62, 66, 67, 68, 69, 75, 77, 81, 80, 78, 79},
			{60, 61, 62, 66, 67, 68, 69, 75, 77, 81, 80, 78, 79, 59, 57, 55,  41, 44, 43}
	};
	private float diffTime;
	private int[] diffTimes = { 20, 25, 40, 60 };

	private Integer[][] gameMode0presets = {
			{5, 2, 3, 4, 18, 75, 92, 97, 98, 99},
			{57, 59, 6, 61, 62, 63, 18, 38, 45, 67, 68, 69, 17},
			{64, 58, 66, 56, 17, 45, 9, 14, 34, 16, 15, 40, 41, 32, 13},
			{18, 2},
			{73, 84, 77, 45, 83, 85, 86, 87, 88, 75, 92, 91, 90, 93, 10, 14, 97, 98, 99},
			{4, 5, 3}
	};
	private Integer[][] gameMode1presets = {
			{1, 2, 3, 4, 5},
			{6, 7, 8, 9},
			{10, 11, 12, 13, 14, 15, 16, 17},
			{15, 16, 17, 1, 2, 3, 4, 5}
	};
	private Integer[][] gameMode2presets = {
			{1, 2, 3, 4, 5}
	};
	private Integer[][] gameMode3presets = {
			{1, 2, 3, 4}
	};
	private Integer[][] microModepresets = {
			{1, 2, 3,4, 10, 18}
	};

	private final int CHANGE_COLOR = 1;
    private final int CHANGE_ZOOM = 2;
    private final int GEN_MODE = 3;
    private final int NEXT_OP = 4;
    private final int FLASH = 10;
    private final int CHANGE_SCALE = 6;
    private final int IMPULSE_ON = 7;
    private final int IMPULSE_OFF = 8;
    private final int PAUSE_LENGTH = 9;
	private final int MESSAGE = 16;
	private final int DIR_CHANGE = 17;
	private final int SPEED_CAM = 5;
    private final int BACKL_ACTIVE = 18;
    private final int BACKL_IMPULSE = 19;
    private final int BACKL_EXPAND = 22;
	private final int CHANGE_PRESET = 23;
	private final int CREATE_SPAWNER = 24;
    private final int EXPAND_EFFECT = 25;
	private final int CHANGE_COLOR_SP = 26;

	private float timeArcMode, timeWord, timeColor;
	private int currentArcadeMode;
	private float[][] arcadeModes = {
			{GEN_MODE, 0, -1},
			{GEN_MODE, 1, 0},
			{GEN_MODE, 1, 1},
			{14, 0.7f, 0},
			//{SPEED_CAM, 1.5f},
			//{SPEED_CAM, -1.5f}
	};

	private String[] levelNames = {"Tutorial", "Metaphor", "Threshold", "Epic Series" /*, "2 Above Zero", "Phase Shifter"*/};
	private float[] levelLength = {90, 97, 96, 120/*, 103, 112*/};
	private int[] levelRewards = {250, 500, 750, 1000/*, 1250, 1500*/};
	private int[] levelFrames = {30, 30, 30, 29};
	private float[][] levelCommands = {
	        //at first time, next count command
	        //1 - changeColor
            //2 - changeZoom, arg - deltaZoom
            //3 - genMode = 1, arg - preset
			//4 - genMode = 0
			//6 - nextOP, arg - op
			//7 - flash
			//8 - scaleFactor change
			//9 - impulse true, delta
			//10 - impulse false
			//12 - player impulse, arg - value
			//13 - genMode = 2, arg - preset
			//14 - genMode = 3

            //METAPHOR
			{0.2f, 2, MESSAGE, MessageType.QUESTION, 0, NEXT_OP, 100, 1, 2, CHANGE_PRESET, 3, PAUSE_LENGTH, 150, 8, 1, CHANGE_COLOR_SP, 0.5f, 10, 3, CHANGE_PRESET, 5, PAUSE_LENGTH, 50, CHANGE_ZOOM, 1, 21, 1, EXPAND_EFFECT, 0, 5,
					16, 1, CHANGE_PRESET, 0, 23, 2, EXPAND_EFFECT, 1, 6, CHANGE_COLOR, 26, 1, EXPAND_EFFECT, 0, 6, 30.5f, 1, EXPAND_EFFECT, 0, 6,
					33, 2, EXPAND_EFFECT, 1, 6, CHANGE_COLOR, 35, 1, EXPAND_EFFECT, 0, 6,
					38.5f, 7, CHANGE_COLOR, EXPAND_EFFECT, 1, 8, CHANGE_PRESET, 4, CHANGE_ZOOM, 1.2f, CHANGE_COLOR_SP, 1, BACKL_ACTIVE, 1, 18, SimpleLineRenderer.WAVE, BACKL_EXPAND, 1, 0.3f, 1, 3, 58, 4, CHANGE_PRESET, 0, CHANGE_ZOOM, 0.8f, EXPAND_EFFECT, 1, 12, CHANGE_COLOR,
					68, 1, EXPAND_EFFECT, 1, 16, 77, 4, NEXT_OP, 89, EXPAND_EFFECT, 1, 12, CREATE_SPAWNER, 1, 4, 5, 0.5f, CHANGE_PRESET, 0, 88, 2, CHANGE_COLOR, EXPAND_EFFECT, 1, 8, 88.5f, 2, EXPAND_EFFECT, 0, 10, CHANGE_ZOOM, 0.9f},
			{0.2f, 3,MESSAGE, MessageType.QUESTION, 0, CHANGE_PRESET, 3, PAUSE_LENGTH, 150, 8, 1, CHANGE_COLOR_SP, 0.5f, 10, 3, CHANGE_PRESET, 5, PAUSE_LENGTH, 50, CHANGE_ZOOM, 1, 19, 1, NEXT_OP, 95, 21, 1, EXPAND_EFFECT, 0, 5,
				16, 1, CHANGE_PRESET, 0, 23, 2, EXPAND_EFFECT, 1, 6, CHANGE_COLOR, 26, 1, EXPAND_EFFECT, 0, 6, 30.5f, 1, EXPAND_EFFECT, 0, 6,
				33, 2, EXPAND_EFFECT, 1, 6, CHANGE_COLOR, 35, 1, EXPAND_EFFECT, 0, 6, 36, 1, NEXT_OP, 20,
				38.5f, 7, CHANGE_COLOR, EXPAND_EFFECT, 1, 8, CHANGE_PRESET, 4, CHANGE_ZOOM, 1.2f, CHANGE_COLOR_SP, 1, BACKL_ACTIVE, 1, 18, SimpleLineRenderer.WAVE, BACKL_EXPAND, 1, 0.3f, 1, 3, 46, 1, NEXT_OP, 94, 56, 1, GEN_MODE, 1, 0, 58, 4, CHANGE_PRESET, 0, CHANGE_ZOOM, 0.8f, EXPAND_EFFECT, 1, 12, CHANGE_COLOR,
				68, 1, EXPAND_EFFECT, 1, 16, 75, 1, GEN_MODE, 0, 4, 77, 4, NEXT_OP, 89, EXPAND_EFFECT, 1, 12, CREATE_SPAWNER, 1, 4, 5, 0.5f, CHANGE_PRESET, 0, 88, 2, CHANGE_COLOR, EXPAND_EFFECT, 1, 8, 88.5f, 2, EXPAND_EFFECT, 0, 10, CHANGE_ZOOM, 0.9f,
				97, 1, NEXT_OP, 104},
			//THRESHOLD
			{
				0.2f, 1, NEXT_OP, 101, 7, 1, EXPAND_EFFECT, 1, 3, 9, 5, CHANGE_COLOR, CHANGE_ZOOM, 1.1f, CHANGE_PRESET, 1, BACKL_ACTIVE, 1, 18, SimpleLineRenderer.WAVE, BACKL_EXPAND, 1, 0.3f, 1, 3,
					19, 1, EXPAND_EFFECT, 1, 15, 19.2f, 1, EXPAND_EFFECT, 0, 13, 19.5f, 1, EXPAND_EFFECT, 1, 15, 20, 1, CHANGE_COLOR,
					22, 1, NEXT_OP, 8, 29, 5, CHANGE_COLOR, CHANGE_ZOOM, 0.9f, BACKL_EXPAND, 1, 1f, 0.3f, 2, CHANGE_PRESET, 0, CREATE_SPAWNER, 1, 3, 4.5f, 0.6f,
					39, 4, PAUSE_LENGTH, 40, FLASH, BACKL_IMPULSE, 1, 1, 8, 0.01f, BACKL_EXPAND, 1, 0.3f, 1, 4, 48, 4, CHANGE_COLOR, BACKL_EXPAND, 1, 1, 0.3f, 3, CHANGE_ZOOM, 1.1f, CHANGE_PRESET, 1,
					60, 1, NEXT_OP, 8, 65, 1, GEN_MODE, 1, 0, 74, 3, GEN_MODE, 0, 1, BACKL_EXPAND, 1, 0.3f, 1, 3, BACKL_IMPULSE, 0, 1, 5, 0.01f, 96, 3, CHANGE_ZOOM, 0.8f, CHANGE_COLOR, CHANGE_PRESET, 0,
					104, 1, GEN_MODE, 1,1, 116, 1, GEN_MODE, 0, 0
			},
			{1, 1, PAUSE_LENGTH, 70, 7, 1, EXPAND_EFFECT, 1, 3, 9, 5, CHANGE_COLOR, CHANGE_ZOOM, 1.1f, CHANGE_PRESET, 1, BACKL_ACTIVE, 1, 18, SimpleLineRenderer.WAVE, BACKL_EXPAND, 1, 0.3f, 1, 3,
					19, 1, EXPAND_EFFECT, 1, 15, 19.2f, 1, EXPAND_EFFECT, 0, 13, 19.5f, 1, EXPAND_EFFECT, 1, 15, 20, 1, CHANGE_COLOR,
					22, 1, NEXT_OP, 8, 26, 1, DIR_CHANGE, 29, 5, CHANGE_COLOR, CHANGE_ZOOM, 0.9f, BACKL_EXPAND, 1, 1f, 0.3f, 2, CHANGE_PRESET, 0, CREATE_SPAWNER, 1, 3, 4.5f, 0.6f,
                39, 4, PAUSE_LENGTH, 40, FLASH, BACKL_IMPULSE, 1, 1, 8, 0.01f, BACKL_EXPAND, 1, 0.3f, 1, 4,  47, 1, DIR_CHANGE, 48, 4, CHANGE_COLOR, BACKL_EXPAND, 1, 1, 0.3f, 3, CHANGE_ZOOM, 1.1f, CHANGE_PRESET, 1,
                60, 1, NEXT_OP, 8, 65, 1, GEN_MODE, 1, 0, 74, 3, GEN_MODE, 0, 1, BACKL_EXPAND, 1, 0.3f, 1, 3, BACKL_IMPULSE, 0, 1, 5, 0.01f, 85, 2, CHANGE_PRESET, 0, DIR_CHANGE, 96, 1, NEXT_OP, 104
			},
			//EPIC SERIES
			{0.2f, 1, NEXT_OP, 102,1, 1, PAUSE_LENGTH, 50, 9, 7, CHANGE_PRESET, 2, PAUSE_LENGTH, 40, EXPAND_EFFECT, 1, 12, BACKL_ACTIVE, 1, 20, SimpleLineRenderer.CITY, BACKL_EXPAND, 1, 0.3f, 1, 3, CHANGE_COLOR, CHANGE_ZOOM, 1, 13, 1, EXPAND_EFFECT, 1, 12, 15.5f, 1, NEXT_OP, 70,
					17.5f, 1, EXPAND_EFFECT, 1, 12,
					21.5f, 2, BACKL_EXPAND, 1, 1, 0.3f, 3, EXPAND_EFFECT, 0, 14, 22.5f, 2, EXPAND_EFFECT, 1, 8, CHANGE_COLOR, 27, 5, BACKL_ACTIVE, 1, 19, SimpleLineRenderer.MOUNTAIN, BACKL_IMPULSE, 1, 1,
					8, 0.01f, BACKL_EXPAND, 1, 0.3f, 1, 3, CHANGE_COLOR, CHANGE_ZOOM, 1.5f, 29, 1, CHANGE_ZOOM, 0.85f, 36, 1, NEXT_OP, 22,
					43, 1, GEN_MODE, 1, 3, 50, 1, NEXT_OP, 72, 63, 1, GEN_MODE, 0, 2, 67, 1, BACKL_EXPAND, 1, 1, 0.3f, 3 , 81, 1, EXPAND_EFFECT, 0, 14, 86, 2, CHANGE_ZOOM, 1.2f, CHANGE_COLOR, 98, 2, CHANGE_PRESET, 1, 14, 0.7f, 0, 107,3,
					GEN_MODE, 1,1, BACKL_ACTIVE, 1, 21, SimpleLineRenderer.CITY, BACKL_EXPAND, 1, 1, 0.3f, 3},
            {1, 1, PAUSE_LENGTH, 50, 9, 7, CHANGE_PRESET, 2, PAUSE_LENGTH, 40, EXPAND_EFFECT, 1, 12, BACKL_ACTIVE, 1, 20, SimpleLineRenderer.CITY, BACKL_EXPAND, 1, 0.3f, 1, 3, CHANGE_COLOR, CHANGE_ZOOM, 1,13, 1, EXPAND_EFFECT, 1, 12, 15.5f, 1, NEXT_OP, 70,
					17.5f, 1, EXPAND_EFFECT, 1, 12,
					21.5f, 2, BACKL_EXPAND, 1, 1, 0.3f, 3, EXPAND_EFFECT, 0, 14, 22.5f, 2, EXPAND_EFFECT, 1, 8, CHANGE_COLOR, 27, 5, BACKL_ACTIVE, 1, 19, SimpleLineRenderer.MOUNTAIN, BACKL_IMPULSE, 1, 1,
				8, 0.01f, BACKL_EXPAND, 1, 0.3f, 1, 3, CHANGE_COLOR, CHANGE_ZOOM, 1.5f, 29, 1, CHANGE_ZOOM, 0.7f, 36, 1, NEXT_OP, 22,
				43, 1, GEN_MODE, 1, 3, 50, 1, NEXT_OP, 72, 63, 1, GEN_MODE, 0, 2, 67, 1, BACKL_EXPAND, 1, 1, 0.3f, 3 , 81, 1, EXPAND_EFFECT, 0, 14, 86, 2, CHANGE_ZOOM, 1.2f, CHANGE_COLOR, 98, 2, CHANGE_PRESET, 1, GEN_MODE, 1, 1, 107,2,
				 BACKL_ACTIVE, 1, 21, SimpleLineRenderer.CITY, BACKL_EXPAND, 1, 1, 0.3f, 3, 120, 1, NEXT_OP, 104},
			//2 ABOVE ZERO
			{7, 2, CHANGE_COLOR, CHANGE_ZOOM, 1.1f, 17, 1, GEN_MODE, 1, 1, 36, 1, GEN_MODE, 0, 1, 44, 1, NEXT_OP, 8, 56, 1,
				CHANGE_ZOOM, 0.8f, 74, 1, 13, 0, 84, CHANGE_ZOOM, IMPULSE_ON, 0.1f, GEN_MODE, 1, 1},
			//PHASE SHIFTER
            //{3, 1, 3, 2}
			{1, 1, PAUSE_LENGTH, 33, 10f, 2f, CHANGE_COLOR, CHANGE_ZOOM, 1f, 28f, 1, GEN_MODE, 1, 1, 38, 1, GEN_MODE, 0, 2, 42f, 1, GEN_MODE, 1,2,
				52, 1, GEN_MODE, 0, 2, 62, 1, 14, 0.7f, 83, 1, GEN_MODE, 1, 1, 95, 1, GEN_MODE, 0, 2}
	};

	private Array<Integer> diffStages;
	private Music[] musics;
	private Music mainMusic;
	public SoundManager soundManager;

	private final int EMPTY = 0;
	private final int LINE = 1;
	private final int SPIKE = 2;
	private final int RADIUS = 3;
	private final int GM_CHANGER = 4;
	private final int CUR_LINE = 5;
	private final int ARC = 6;
	private final int N_LINE = 7;
	private final int SPIKE_D = 8;
	private final int LAST_LEN = 9;
	private final int HELPER = 10;
	private final int LEVEL_END = 21;
	private final int DIR_CHANGER = 22;
	private final int RESET_LINES = 23;
	private final int LETTER = 24;
	private final int N_LINE_ACT = 25;
	private final int ROCKET_POS = 26;

	private int[] complexOpList;
	private int complexId, linesGen, gameMode, genMode, levelMode, constFrames; //0 - stepping, 1 - jumping
	public boolean isComplex, firstStartGame, mainMenu, isArcade, dirChanging, levelEnding, activeMusic, activeOutline, activeSounds, lowGraphics, taskWord;
	private boolean isChangingColor, isChangingZoom, isFlash, isImpulse, isGrowZoom, nonProportion, autoRetry, autoRetryGame, videoViewed;
	//skills
	public Array<Skill> skillsActive;
	//wave
	private int waveStart = 0; // waveStart < waves arcade
	//score multiplier
	private int scoreMultiplier = 1; // 4 point

	private int[][] complexBase = {
			//11 = money, after money - cost - height
			//6 = emptyGen, after emptyGen - length of emptyGen
			//1 = drawLine, after this - deltaRadius
			//7 = newLine, after this - angleOfLine - height
			//10 = currentLine = 0
			//9 = currentLine = last
			//12 = mathFunc, after this - length - delta
			//13 = speedCh
			//15 = radius 0
			// 16 = radius temp
			//17 = gamemodeChange
			{},
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 30, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 30, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 30, LINE, 2, EMPTY, 4, LINE, -2},
			//2
			{SPIKE, SPIKE, SPIKE, SPIKE, SPIKE},
			//3
			{N_LINE, 20, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE},
			//4
			{LINE, -2, EMPTY, 20, LINE, 2},
			//5
			{LINE, -2, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 3},
			//6
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 4, LINE, -4, EMPTY, 8, LINE, 2, EMPTY, 4, LINE, -2},
			//7
			{LINE, 2, EMPTY, 4, LINE, -4, EMPTY, 13, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 13, LINE, 4, EMPTY, 4, LINE, -2},
			//8 - stairs
			{LINE, 2, LAST_LEN, 1, EMPTY, 20, LINE, 2, LAST_LEN, 1, EMPTY, 25, LINE, 2, LAST_LEN, 1,EMPTY, 25, LINE, 2},
			//9
			{N_LINE, 25, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, CUR_LINE, 1, SPIKE, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, EMPTY, 5, CUR_LINE, -1},
			//10
			{N_LINE, 13, 2, EMPTY, 3, SPIKE_D, CUR_LINE, -1},
			//11
			{ARC, 35, 1, SPIKE, ARC, 35, -1},
			//12
			{LINE, -2, EMPTY, 15, N_LINE, 62, 2, EMPTY, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1, EMPTY, 5, ARC, 15, 1},
			//13
			{RADIUS, 0, EMPTY, 15, RADIUS, 1, EMPTY, 15, RADIUS, 0, EMPTY, 15, RADIUS, 1, EMPTY, 15, RADIUS, 0, EMPTY, 15, RADIUS, 1},
			//14
			{N_LINE, 13, 2, EMPTY, 3, SPIKE},
			//15
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 12, N_LINE, 10, 4, EMPTY, 1, SPIKE, SPIKE, CUR_LINE, -1, EMPTY, 12, LINE, 2, EMPTY, 4, LINE, -2},
			//16
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, N_LINE, 10, 4, CUR_LINE, -1, EMPTY, 1, SPIKE, SPIKE, EMPTY, 8, LINE, 2, EMPTY, 4, LINE, -2},
			//17
			{LINE, 2, EMPTY, 4, LINE, -2, SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 4, LINE, -2},
            //18
            {LINE, 2, EMPTY, 4, LINE, -2},
			//19
			{EMPTY, 15},
			//20
			{LAST_LEN, 1, ARC, 50, 3, LAST_LEN, 1},
			//21
			{LAST_LEN, 1, ARC, 10, -1, LAST_LEN, 1},
			//22
			{LINE, -2, EMPTY, 25, N_LINE, 180, 3, EMPTY, 1, LINE, 60, CUR_LINE, -1, EMPTY, 6, LAST_LEN, 1, SPIKE, EMPTY, 25, SPIKE, EMPTY, 25, SPIKE, EMPTY, 35, LINE, 2, EMPTY, 15, LINE, 2, EMPTY, 10},
			//23
			{ARC, 25, 4, LAST_LEN, 1, EMPTY, 10, LINE, 2, EMPTY, 4, LINE, -2, LAST_LEN, -1, EMPTY, 10, ARC, 25, -4, EMPTY, 1, LAST_LEN, 1},
			//24
			{ARC, 25, 4, LAST_LEN, 1, N_LINE, 50, 2, EMPTY, 1, LINE,10, CUR_LINE, -1, EMPTY, 10, EMPTY, 6, LAST_LEN, -1, EMPTY, 10, ARC, 25, -4, EMPTY, 1, LAST_LEN, 1},
			//25
			{N_LINE, 90, 2, EMPTY, 5, SPIKE, EMPTY, 15, CUR_LINE, -1, ARC, 21, 1},
			//26
			{N_LINE, 25, 2, EMPTY, 4, CUR_LINE, -1, LINE, 2, EMPTY, 3, LINE, -2, EMPTY, 2, CUR_LINE, 1, SPIKE, EMPTY, 22, CUR_LINE, -1, N_LINE, 25, 2, EMPTY, 1, SPIKE, EMPTY, 2, LINE, 2, EMPTY, 2, LINE, -2, CUR_LINE, -1},
			//27
			{N_LINE, 25, 2, CUR_LINE, -1, EMPTY, 26, N_LINE, 25, 2, EMPTY, 4, CUR_LINE, -1, LINE, 2, EMPTY, 3, LINE, -2, EMPTY, 18, N_LINE, 25, 2, EMPTY, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1},
			//28
			{N_LINE, 25, 2, CUR_LINE, -1, EMPTY, 4, LINE, 2, EMPTY, 3, LINE, -2, EMPTY, 8, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 2, N_LINE, 25, 2, EMPTY, 28, CUR_LINE, -1, N_LINE, 25, 2, EMPTY, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1},
			//29
			{N_LINE, 25, 2, EMPTY, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1, EMPTY, 10, N_LINE, 25, 2, EMPTY, 26, CUR_LINE, -1, N_LINE, 25, 2, CUR_LINE, -1, EMPTY, 4, LINE, 2, EMPTY, 3, LINE, -2, EMPTY, 8},
			//30
			{SPIKE, ARC, 25, 1, SPIKE, ARC, 25, 1, SPIKE},
			//31
			{N_LINE, 25, 2, EMPTY, 1, LINE, 2, EMPTY, 2, LINE, -2, CUR_LINE, -1, EMPTY, 22, N_LINE, 25, 2, CUR_LINE, -1, EMPTY, 9, LINE, 2, EMPTY, 3, LINE, -2},
			//32
			{LINE, 2, EMPTY, 4, LINE, -2, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 4, LINE, -2, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 4, LINE, -2, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 4, LINE, -2},
			//33
			{ARC, 15, 1, SPIKE, LINE, -5, EMPTY, 6, LINE, 5, EMPTY, 14, LINE, -5, EMPTY, 6, LINE, 5, SPIKE, ARC, 15, -1},
			//34
			{N_LINE, 22, 2, CUR_LINE, -1, LINE, -5, CUR_LINE, 1, EMPTY, 4, SPIKE, CUR_LINE, -1, EMPTY, 4, LINE, 5, EMPTY, 20, N_LINE, 22, 2, EMPTY, 4, SPIKE},
			//35
			{LINE, 2, LAST_LEN, 1, EMPTY, 25, LINE, 2, LAST_LEN, 1, EMPTY, 25,LINE, 2, LAST_LEN, 1, EMPTY, 25, LINE, 2, LAST_LEN, 1, EMPTY, 25, LINE, 2, LAST_LEN, 1, EMPTY, 10, N_LINE, 20, 2, EMPTY, 5, SPIKE, CUR_LINE, -1, LAST_LEN, -1},
			//36
			{LINE, 2, LAST_LEN, 1, EMPTY, 25, LINE, 2, LAST_LEN, 1, EMPTY, 25,LINE, 2, LAST_LEN, 1, EMPTY, 25, LINE, 2, LAST_LEN, 1, EMPTY, 25, LINE, 2, LAST_LEN, 1, EMPTY, 25, N_LINE, 50, 2, EMPTY, 8, SPIKE, EMPTY, 14, CUR_LINE, -1, LINE, 2},
			//37
			{N_LINE, 120, 8, CUR_LINE, -1, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 4, LINE, -4, EMPTY, 8, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 4, LINE, -4, EMPTY, 8, LINE, 2, EMPTY, 4, LINE, -2},
			//38
			{LINE, 2, EMPTY, 4, LINE, -2, N_LINE, 141, 5, EMPTY, 1, SPIKE, CUR_LINE, -1, EMPTY, 22, LINE, 2, EMPTY, 20, LINE, -2, EMPTY, 22, CUR_LINE, 1, SPIKE, CUR_LINE,-1, LINE, 2, EMPTY, 4, LINE, -2},
			//39
			{ARC, 10, -1, LINE, -4, EMPTY, 10, LINE, 3, N_LINE, 29, 2, EMPTY, 1, LINE, 20, EMPTY, 14, LINE, -20, CUR_LINE, -1, LINE, -3, EMPTY, 10, LINE, 4, ARC, 10, 1},
			//40
			{N_LINE, 5, 2, EMPTY, 1, SPIKE, CUR_LINE, -1, EMPTY, 14, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 14, N_LINE, 5, 2, EMPTY, 1, SPIKE, CUR_LINE, -1},
			//41
			{N_LINE, 20, 1, EMPTY, 12, CUR_LINE, -1, LINE, 2, SPIKE, SPIKE, LINE, -2, N_LINE, 20, 1},
			//42
			{LINE, -3, EMPTY, 20, N_LINE, 125, 3, LINE, -2, EMPTY, 4, LINE, 2, EMPTY, 8, CUR_LINE, -1, LAST_LEN, 1, LINE, 2, EMPTY,4 ,LINE, -2, SPIKE, EMPTY, 20, LINE, 2, EMPTY,4, LINE, -2, EMPTY, 8, CUR_LINE, 1, LINE, -2, EMPTY, 4, LINE, 2, CUR_LINE, -1, EMPTY, 12, LINE, 2, EMPTY, 16, LINE, 2},
			//43
			{ARC, 20, 1, EMPTY, 16, LAST_LEN, 1, N_LINE, 65, 2, ARC, 30, 1, CUR_LINE, -1, LINE, 5},
			//44
			{ARC, 20, 1, EMPTY, 14, LAST_LEN, 1, N_LINE, 65, 2, SPIKE, ARC, 16, 1,ARC, 16, -1, CUR_LINE, -1},
			//45
			{N_LINE, 20, 2, CUR_LINE, -1, N_LINE, 20, 5, CUR_LINE, -1, EMPTY, 5, SPIKE},
			//46
			{N_LINE, 21, 2, CUR_LINE, -1, EMPTY, 3, LINE, 2, EMPTY, 4, LINE, -2, CUR_LINE, 1, SPIKE, CUR_LINE, -1, EMPTY, 30, N_LINE, 21, 2, CUR_LINE, -1, EMPTY, 3, LINE, 2, EMPTY, 4, LINE, -2, CUR_LINE, 1, CUR_LINE, 1, SPIKE, CUR_LINE, -1},
			//47 - direction
			{EMPTY, 50, DIR_CHANGER, EMPTY, 5, LINE, 15, SPIKE,SPIKE, LINE, -15},
			//48 - e
			{LETTER, N_LINE, 40, 1, CUR_LINE, -1, RESET_LINES, LINE, -2, LAST_LEN, 1, EMPTY, 3, N_LINE, 20, 1, CUR_LINE, -1, N_LINE, 20, 2, CUR_LINE, -1, N_LINE, 20, 3, CUR_LINE, -1, CUR_LINE, 1, CUR_LINE, 1, LINE, 1, EMPTY, 1,
                    LINE,-1, EMPTY, 5, CUR_LINE, -1, CUR_LINE, 1, LINE, 2, EMPTY, 6, CUR_LINE, -1, LINE, 3},
			//49 - l
			{LETTER, N_LINE, 26, 1, CUR_LINE, -1, RESET_LINES, LINE, -2, LAST_LEN, 1, EMPTY, 3,N_LINE, 5, 1, LINE, 2, EMPTY, 6, CUR_LINE, -1, LINE, 3},
			//50 - c
			{LETTER, N_LINE, 40, 1, CUR_LINE, -1, RESET_LINES, LINE, -2, LAST_LEN, 1, EMPTY, 3,N_LINE, 20, 1, CUR_LINE, -1, N_LINE, 20, 3, CUR_LINE,-1, CUR_LINE, 1, EMPTY, 7, LINE, 2, EMPTY, 6, CUR_LINE, -1, LINE, 3},
			//51 - y
			{LETTER, N_LINE, 40, 1, CUR_LINE, -1, RESET_LINES, LINE, -2, LAST_LEN, 1, EMPTY, 3,N_LINE, 20, 1, CUR_LINE, -1, N_LINE, 20, 2, CUR_LINE, -1, CUR_LINE, 1, LINE, 2, EMPTY, 1, LINE, -2, CUR_LINE, 1, EMPTY, 4, LINE, 1, EMPTY, 6, CUR_LINE, -1, LINE, 3},
			//52
			{N_LINE, 40, 2, ARC, 7, 3, LINE, 4, ARC, 2, 2, ARC, 2, -2, LINE, -4, ARC, 7, -3},
			//53 - cycle
			{
				N_LINE, 145, 1, CUR_LINE, -1, LAST_LEN, 0, RESET_LINES, LINE, -2, LAST_LEN, 1, EMPTY, 6,
					N_LINE, 20, 1, CUR_LINE, -1, N_LINE, 20, 2, CUR_LINE, -1, N_LINE, 20, 3, CUR_LINE, -1, CUR_LINE, 1, CUR_LINE, 1, LINE, 1, EMPTY, 1, LINE,-1, EMPTY, 5, CUR_LINE, -1, CUR_LINE, 1, LINE, 2, EMPTY, 6, RESET_LINES,
					N_LINE, 15, 1, EMPTY, 6, LINE, 2, EMPTY, 6, RESET_LINES,
					N_LINE, 20, 1, CUR_LINE, -1, N_LINE, 20, 3, CUR_LINE,-1, CUR_LINE, 1, EMPTY, 7, LINE, 2, CUR_LINE, -1, RESET_LINES, EMPTY, 6,
					N_LINE, 20, 1, CUR_LINE, -1, N_LINE, 20, 2, CUR_LINE, -1, CUR_LINE, 1, LINE, 2, EMPTY, 1, LINE, -2, CUR_LINE, 1, EMPTY, 4, LINE, 1, EMPTY, 6, RESET_LINES,
					N_LINE, 20, 1, CUR_LINE, -1, N_LINE, 20, 3, CUR_LINE,-1, CUR_LINE, 1, EMPTY, 7, LINE, 2, EMPTY, 6, CUR_LINE, -1, RESET_LINES,
					LINE, 4
			},
			//54 - roro
			{
					N_LINE, 120, 1, CUR_LINE, -1, LAST_LEN, 0, RESET_LINES, LINE, -2, LAST_LEN, 1, EMPTY, 6,
					N_LINE, 12, 2, CUR_LINE, -1, N_LINE, 12, 1, LINE, 1, EMPTY, 1, LINE, -1, EMPTY, 1, LINE, 1, EMPTY, 6, CUR_LINE, -1, RESET_LINES,
					N_LINE, 15, 3, CUR_LINE, -1, N_LINE, 15, 2, LINE, 1, EMPTY, 1, LINE, -1, CUR_LINE, -1, N_LINE, 5, 1, LINE, 1, CUR_LINE, -1, EMPTY, 3, N_LINE, 5, 1, LINE, 2, CUR_LINE, -1, EMPTY, 6, RESET_LINES,
					N_LINE, 12, 2, CUR_LINE, -1, N_LINE, 12, 1, LINE, 1, EMPTY, 1, LINE, -1, EMPTY, 1, LINE, 1, EMPTY, 6, CUR_LINE, -1, RESET_LINES,
					N_LINE, 15, 3, CUR_LINE, -1, N_LINE, 15, 2, LINE, 1, EMPTY, 1, LINE, -1, CUR_LINE, -1, N_LINE, 5, 1, LINE, 1, CUR_LINE, -1, EMPTY, 3, N_LINE, 5, 1, LINE, 2, CUR_LINE, -1, EMPTY, 6, RESET_LINES,
					LINE, 4
			},
			//55
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 4, N_LINE, 60, 4, EMPTY, 24, CUR_LINE, -1, LINE, 4, EMPTY, 5, LINE, -4},
			//56
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 8, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4},
			//57
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 8, LINE, 7, EMPTY, 5, LINE, -7, EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 8, LINE, 2, EMPTY, 4, LINE, -2},
			//58
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 22, LINE, 2, N_LINE, 10, 3, LINE, 20, CUR_LINE, -1, EMPTY, 4, LINE, -2, EMPTY, 22, LINE, 2, EMPTY, 4, LINE, -2},
			//59
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, N_LINE, 10, 3, LINE, 1, CUR_LINE, -1, EMPTY, 12, N_LINE, 10, 6, EMPTY, 2, SPIKE, CUR_LINE, -1},
			//60
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, N_LINE, 10, 3, LINE, 1, CUR_LINE, -1, EMPTY, 4, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, N_LINE, 10, 3, LINE,1, EMPTY, 12, CUR_LINE, -1, LINE, 2, EMPTY, 4, LINE, -2},
			//61
			{LINE, 2, EMPTY, 5, LINE, -2, N_LINE, 34, 3, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,CUR_LINE, -1, LINE, 2, EMPTY, 5, LINE, -2},
			//62
			{N_LINE, 95, 2, CUR_LINE, -1, SPIKE, CUR_LINE, 1, EMPTY, 6, LINE, 2, EMPTY, 3, LINE, -2, EMPTY, 22, LINE, 2, EMPTY, 3, LINE, -2, EMPTY, 6, CUR_LINE, -1, SPIKE},
			//63
			{LINE, 2, ARC, 6, 1, LINE, -3, EMPTY, 6, N_LINE, 10, 4, LINE, 1, CUR_LINE, -1, EMPTY, 2, SPIKE, EMPTY, 8, LINE, 3, ARC, 6, -1, LINE, -2},
			//64
			{SPIKE, LINE, -1, EMPTY, 6, N_LINE, 10, 2, LINE, 1, CUR_LINE, -1, EMPTY, 18, SPIKE, SPIKE, SPIKE, SPIKE},
			//65
			{},
			//66
			{LINE, 1, EMPTY, 6, LINE, -1, SPIKE,SPIKE,SPIKE, SPIKE, LINE, 1, EMPTY, 18, LINE, -1, SPIKE,SPIKE,SPIKE, SPIKE, LINE, 1, EMPTY, 6, LINE, -1},
			//67
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 8, LINE, 6},
			//68
			{LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 8, N_LINE, 10, 4, EMPTY, 2, SPIKE, EMPTY, 10, CUR_LINE, -1, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 12, LINE, 4},
			//69
			{ARC, 15, 1, LINE, -4, EMPTY, 10, LINE, 6, ARC, 15, 1},
			//70 - individ
			{LINE, 2, EMPTY, 15, LINE, -2, EMPTY, 8, N_LINE, 15, 4, CUR_LINE, -1, EMPTY, 18, LINE, 7, EMPTY, 9, LINE, -7, EMPTY, 8, N_LINE, 100, 8, EMPTY, 25, SPIKE, EMPTY, 35, CUR_LINE, -1, LINE, 4, EMPTY, 6, LINE, -4},
			//71 - individ
			{EMPTY, 30,LINE, 2, GM_CHANGER, 0, N_LINE, 10, 3, EMPTY, 1, LINE, 40, CUR_LINE, -1, EMPTY, 2, N_LINE, 330, 8, LINE, 40, CUR_LINE, -1, EMPTY, 19, SPIKE, EMPTY, 28, SPIKE, EMPTY, 8, GM_CHANGER, 1, EMPTY, 20, LINE, 4, GM_CHANGER, 0, EMPTY, 5, LINE, -4, EMPTY, 26, SPIKE, EMPTY, 28, SPIKE, EMPTY, 12, GM_CHANGER, 1, N_LINE, 10, 3, LINE, 40, EMPTY, 40},
			//72 - ind
			{LINE, 2, GM_CHANGER, 0, N_LINE, 10, 3, EMPTY, 1, LINE, 60, CUR_LINE, -1, EMPTY, 24, SPIKE, EMPTY, 24, SPIKE, EMPTY, 24, GM_CHANGER, 1, N_LINE, 10, 3, EMPTY, 1, LINE, 60},
			//73
			{LINE, -2, N_LINE, 50, 4, EMPTY, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1, LINE, 1},
			//74
			{ARC, 10, 1, LINE, -2, EMPTY, 10, LINE, 4, ARC, 10, -2},
			//75
			{N_LINE, 30, 2, CUR_LINE, -1, EMPTY, 5, LINE, 2, EMPTY, 4, LINE, -2},
			//76
			{LINE, 1, SPIKE, EMPTY, 12, SPIKE, LINE, -1},
			//77
			{LINE, -2, N_LINE, 50, 4, CUR_LINE, -1, EMPTY, 12, SPIKE, EMPTY, 12, LINE, 2},
			//78
			{LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE},
			//79
			{LINE, 2, EMPTY, 4, LINE, -4, EMPTY, 10, N_LINE, 10, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE},
			//80
			{LINE, 2, EMPTY, 4, LINE, -3, EMPTY, 8, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 3, SPIKE, SPIKE, LINE, -3, EMPTY, 8, LINE, 2},
			//81
			{LINE, 2, EMPTY, 4, LINE, -3, EMPTY, 8, LINE, 2, SPIKE, SPIKE, LINE, -2, EMPTY, 8, LINE, 3, EMPTY, 4, LINE, -3, EMPTY, 8, LINE, 2},
			//82
			{ARC, 10, 1, SPIKE, LINE, -3, EMPTY, 12, N_LINE, 10, 2, ARC, 3, 1, ARC, 3, -1, CUR_LINE, -1, EMPTY, 16, N_LINE, 10, 2, ARC, 3, 1, ARC, 3, -1, EMPTY, 12, CUR_LINE, -1, LINE, 3, SPIKE, ARC, 10, -1},
			//83
			{N_LINE, 10, 2, LINE, -2},
			//84
			{LINE, 2, ARC, 20, -1},
			//85
			{ARC, 15, 1, SPIKE, ARC, 15, -1},
			//86
			{ARC, 10, 1, LINE, -1, EMPTY, 5, LINE, 1, ARC, 10, -1},
			//87
			{SPIKE, EMPTY, 12, N_LINE , 25, 2, CUR_LINE, -1, EMPTY,25, SPIKE},
			//88
			{LINE, 1, EMPTY, 3, LINE, -1, EMPTY, 20, LINE,1, EMPTY, 3, LINE, -1},
			//89 - ind
			{LINE, 2, EMPTY, 12, SPIKE, LINE, -3, LAST_LEN, 1, EMPTY, 40,LINE, 2, EMPTY, 12, SPIKE, LINE, -3, LAST_LEN, 1, EMPTY, 40,LINE, 2, EMPTY, 12, SPIKE, LINE, -4, LAST_LEN, 1, EMPTY, 40,LINE, 2, EMPTY, 12, SPIKE, LINE, -4, LAST_LEN, 1, EMPTY, 40,LINE, 2, EMPTY, 12, SPIKE, LINE, -4, LAST_LEN, 1, EMPTY, 40},
			//90
			{LINE, -2, LAST_LEN, 1, EMPTY, 40, LINE, -2, EMPTY, 20},
			//91
			{LINE, 2, LAST_LEN, 1, EMPTY, 40, LINE,2},
			//92
			{SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 4, LINE, -2},
			//93
			{SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 4, LINE, -2, SPIKE, SPIKE, SPIKE},
			//94 - individ
			{N_LINE, 100, 2, CUR_LINE, -1, SPIKE, CUR_LINE, 1, EMPTY, 12, SPIKE, EMPTY, 16, SPIKE, EMPTY, 12, CUR_LINE, -1, SPIKE},
			//95 - individ
			{LINE, 2, LAST_LEN, 1, EMPTY, 80,LINE, 2, LAST_LEN, 1, EMPTY, 140,LINE, 2, LAST_LEN, 1, EMPTY, 140,LINE, 2, LAST_LEN, 1},
			//96 - individ
			{LINE, -2, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 140,
					LINE, -2, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 12, LINE, 2, LAST_LEN, 1, EMPTY, 12, LINE, 2, LAST_LEN, 1, EMPTY, 140,
					LINE, -2, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 12, LINE, 2, LAST_LEN, 1, EMPTY, 12, LINE, 2, LAST_LEN, 1, EMPTY, 140,
					LINE, -2, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 12, LINE, 2, LAST_LEN, 1, EMPTY, 12, LINE, 2, LAST_LEN, 1},
			//97
			{SPIKE},
			//98
			{SPIKE, LINE, 2, EMPTY, 6, LINE, -2, SPIKE},
			//99
			{SPIKE, N_LINE, 10, 1, LINE, 1, CUR_LINE, -1,EMPTY, 4, SPIKE},
			//METAPHOR -100
			{EMPTY, 50, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 130, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 130, LINE, 1, EMPTY, 5, LINE, -1,EMPTY, 130, LINE, 2, EMPTY, 4, LINE, -3, LAST_LEN, 1,
				EMPTY, 70, LINE, 2, EMPTY, 6, LINE, -2, EMPTY, 40, LINE, 2, LAST_LEN, 1, EMPTY, 14, LINE, 2, LAST_LEN, 1, EMPTY, 15, LINE, -1, LAST_LEN, 1, EMPTY, 40, LINE, 2, EMPTY, 5, LINE, -2,
				EMPTY, 90, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 55, LINE, 2, EMPTY, 5, LINE, -3, LAST_LEN, 1, EMPTY,45, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 35, LINE, -2, EMPTY, 20, LINE, 2, EMPTY, 30, SPIKE,
				EMPTY, 60, SPIKE, SPIKE, EMPTY, 50, LINE, 2, LAST_LEN, 1, EMPTY, 10,SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 12, N_LINE, 40, 2, EMPTY, 1, SPIKE, EMPTY, 15, SPIKE, CUR_LINE, -1, RESET_LINES, EMPTY, 8,SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 25, LINE, 2, LAST_LEN, 1, EMPTY, 30, SPIKE, N_LINE, 38, 2,
				EMPTY, 9, SPIKE, EMPTY, 7, CUR_LINE, -1, RESET_LINES, SPIKE, EMPTY, 30, SPIKE, EMPTY, 50, SPIKE, SPIKE, LINE, 2, LAST_LEN, 1, EMPTY, 20, LINE, 2, EMPTY, 5, LINE, -2, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE,LINE, 1, EMPTY, 5, LINE,-1,
				EMPTY, 100, N_LINE, 360, 2, EMPTY, 25, CUR_LINE, -1, SPIKE, CUR_LINE, 1, LAST_LEN, 1, EMPTY, 16, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 5 , LINE, -4, EMPTY, 40, LINE, 2, EMPTY, 4, LINE, -2,
				EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 8, N_LINE, 10, 8, EMPTY, 1, SPIKE, SPIKE, CUR_LINE, -1, CUR_LINE, 1, EMPTY, 10, EMPTY, 4, EMPTY, 26, CUR_LINE, -1, LINE, 3, RESET_LINES, LAST_LEN, 1, EMPTY, 20,
					LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 10, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 11, LINE, 6, EMPTY, 7, LINE, -6, EMPTY, 11, LINE, 9, EMPTY, 9, LINE, -9, EMPTY, 10, LINE, 6, EMPTY, 7, LINE, -6, EMPTY, 10, N_LINE, 615, 10, EMPTY, 30, CUR_LINE, -1,
					LINE, 10, EMPTY, 2, LINE, -10, CUR_LINE, 1, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, LINE,1, EMPTY, 5, LINE, -1, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, LINE, 1 , EMPTY, 5, LINE, -1,
					EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, LINE, 1, EMPTY, 5, LINE,-1, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 40, CUR_LINE, -1, LAST_LEN, 1, LINE, 10, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, RESET_LINES, ARC, 70, 3,
					LAST_LEN, 1, EMPTY, 60, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 8, LINE, 6, EMPTY, 6, LINE, -6, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,
					EMPTY, 90, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 8, LINE, 6, EMPTY, 8, LINE, -6, EMPTY, 10, LINE, 10, EMPTY, 20, LINE, -10, EMPTY, 18, N_LINE, 18, 9, LINE, 1, EMPTY, 16, CUR_LINE, -1, N_LINE, 18, 5, LINE, 1, CUR_LINE, -1, EMPTY, 18,
					LINE, 3, N_LINE, 18, 6, LINE, 1, EMPTY, 3, SPIKE, CUR_LINE, -1, LAST_LEN, 1, EMPTY, 20, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 30, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, EMPTY, 50, LAST_LEN, 1,
					LINE, -2, LAST_LEN, 1, EMPTY, 14,LINE, -2, LAST_LEN, 1, EMPTY, 14,LINE, -2, LAST_LEN, 1, EMPTY, 14,LINE, -2, LAST_LEN, 1, EMPTY, 40,LINE, 2, EMPTY, 14,LINE, -2, LAST_LEN, 1, EMPTY, 14,LINE, -2, LAST_LEN, 1, EMPTY, 14,LINE, -2, LAST_LEN, 1, EMPTY, 14,LINE, -2, LAST_LEN, 1, EMPTY, 40,
					LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 40, LINE,2, EMPTY, 5, LINE, -4, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 2, ARC, 10, -1, LAST_LEN, 1, EMPTY, 36,SPIKE, SPIKE, SPIKE, EMPTY, 38, LINE, 1, SPIKE, LINE, -1, EMPTY, 36,SPIKE, SPIKE, SPIKE, EMPTY, 38, LINE, 1, SPIKE, LINE, -1,
					EMPTY, 50, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 5, LINE, -2, LAST_LEN, 1, EMPTY, 16, LINE, -2, LAST_LEN, 1, EMPTY, 16, LINE, -2, LAST_LEN, 1, EMPTY, 16, LINE, -2, LAST_LEN, 1, SPIKE,SPIKE,
					SPIKE, ARC,50, 2, RESET_LINES, LAST_LEN, 1, GM_CHANGER, 1, N_LINE, 10, 2, LINE, 60, CUR_LINE, -1, RESET_LINES, EMPTY, 70, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 30, N_LINE, 10, 2, EMPTY, 1, LINE, 60, CUR_LINE, -1, RESET_LINES,
					EMPTY, 60,SPIKE, N_LINE, 40, 2, CUR_LINE, -1, RESET_LINES, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE, EMPTY, 30, ARC, 50,2, LAST_LEN, 1, SPIKE, ARC, 50, 2, LAST_LEN, 1, SPIKE, EMPTY, 50,
					LINE, 6, EMPTY, 5, LINE, -6,SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE,SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE,SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE,SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE,
					LINE, 6, EMPTY, 5, LINE, -6, EMPTY, 70, N_LINE, 10, 2, EMPTY, 1, LINE, 60, CUR_LINE, -1, RESET_LINES, EMPTY, 60, LINE, 3, EMPTY, 5, LINE, -3, EMPTY, 70, N_LINE, 10, 2, EMPTY, 1, LINE, 60, CUR_LINE, -1, RESET_LINES, EMPTY, 60, LINE, 5, EMPTY, 5, LINE, -5, EMPTY, 40,
					N_LINE, 10, 2, EMPTY, 1, LINE, 60, CUR_LINE, -1, RESET_LINES, EMPTY, 60, LINE, 3, EMPTY, 5, LINE, -3, EMPTY, 50, N_LINE, 10, 2, EMPTY, 1, LINE, 60, CUR_LINE, -1, RESET_LINES, LAST_LEN, 1, EMPTY, 60, LINE, 2, EMPTY, 10, LINE, -2, EMPTY, 60, LINE, 2, EMPTY, 10, LINE, -2, EMPTY, 40,
					GM_CHANGER, 0, N_LINE, 10, 2, EMPTY, 1, LINE, 60, CUR_LINE, -1, RESET_LINES, EMPTY, 30, LINE, 2, EMPTY, 20, LINE, -2, LAST_LEN, 1, EMPTY, 16, LINE, -2, LAST_LEN, 1, EMPTY, 16, LINE, -2, LAST_LEN, 1, EMPTY, 40, SPIKE, SPIKE, SPIKE, EMPTY, 40, LINE, 2, EMPTY, 5, LINE, -2,
					EMPTY, 12, SPIKE, SPIKE, N_LINE, 360, 2, EMPTY, 16, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 16, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 5, LINE, -1, LAST_LEN, 1,
					EMPTY, 25, LINE, 2, EMPTY, 16, LAST_LEN, 1, LINE, 2, EMPTY, 5,LAST_LEN,1,LINE, -2, LAST_LEN, 1, EMPTY, 16,LINE, -2, LAST_LEN, 1, EMPTY, 16,LINE, -2, LAST_LEN, 1, EMPTY, 34,CUR_LINE, -1, RESET_LINES, SPIKE,SPIKE, EMPTY, 44, LAST_LEN, 1,
					SPIKE, SPIKE, EMPTY, 24, LINE, 2, LAST_LEN, 1, EMPTY, 36, SPIKE, SPIKE, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 10, RESET_LINES, N_LINE, 40, 2, EMPTY, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,
					CUR_LINE, -1, EMPTY, 10, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 40, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, RESET_LINES, N_LINE, 160, 4, EMPTY, 40, LAST_LEN, 1,SPIKE, SPIKE, SPIKE, EMPTY, 40, CUR_LINE, -1, LAST_LEN, 1, LINE, 2, EMPTY,5, LINE, -2, EMPTY, 30, LINE, 2,
					EMPTY, 5, LINE, -4, EMPTY, 10, N_LINE, 20, 5, CUR_LINE, -1, EMPTY, 24, N_LINE, 20, 5, CUR_LINE, -1, EMPTY, 20, LINE, 5, RESET_LINES, EMPTY, 40, SPIKE, SPIKE, ARC, 35, 1, LAST_LEN, 1,
					SPIKE, SPIKE, SPIKE, EMPTY, 30, SPIKE, RESET_LINES, N_LINE, 50, 1, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, N_LINE, 50, 1,CUR_LINE, -1, SPIKE, SPIKE,
					SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 5, LINE, -2, RESET_LINES, N_LINE, 50, 1, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, N_LINE, 50, 1,CUR_LINE, -1, SPIKE, SPIKE,
					SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 30, SPIKE, SPIKE, SPIKE, EMPTY, 60, LINE, 2, N_LINE, 10, 3, EMPTY, 1, LINE, 60, CUR_LINE, -1, EMPTY, 3, LEVEL_END, LINE, -2, EMPTY, 10, HELPER,N_LINE_ACT, 14, 0, 1, ARC, 6,3, CUR_LINE, -1,HELPER,N_LINE_ACT, 17, 1, 1, LINE, 3,EMPTY,1, ARC, 3, 8, ARC, 3, -8,N_LINE_ACT, 14, 0, 1, HELPER,
					LINE, 2, ARC,6, -3,HELPER,CUR_LINE, -1, EMPTY, 15, LINE, 15
			},
			//THRESHOLD - 101,
			{
				LINE ,-1, SPIKE,SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 20, LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 7, LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 3, EMPTY, 7, LINE, -3, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY,7, LINE, -1,
					SPIKE, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 7, LINE, -2, EMPTY, 40, SPIKE, SPIKE, SPIKE, EMPTY, 30,SPIKE,SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 26,SPIKE, SPIKE, SPIKE, SPIKE, LINE, -2, LAST_LEN, 1, EMPTY, 16, N_LINE, 123, 5, LINE, 3, LAST_LEN, 1, SPIKE,
					CUR_LINE, -1, LAST_LEN, 1, EMPTY, 16, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, EMPTY, 10, CUR_LINE, 1, LAST_LEN, 1, SPIKE, CUR_LINE, -1, LAST_LEN,1, ARC, 50, 1, LAST_LEN, 1, SPIKE, SPIKE, ARC, 30, 1,
					LAST_LEN, 1, EMPTY, 40, LINE, 2, EMPTY, 7, LINE, -2, EMPTY, 30, SPIKE, SPIKE, SPIKE, SPIKE,SPIKE, LINE, 2, EMPTY, 10, LAST_LEN, 1, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2,
					LAST_LEN,1, EMPTY, 13, RESET_LINES, N_LINE, 18, 2, EMPTY, 1, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1, RESET_LINES,EMPTY, 8, LINE, -2, LAST_LEN, 1,
					EMPTY, 14, LINE, -2, LAST_LEN, 1, EMPTY, 40, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16,
					LINE, 2, LAST_LEN, 1, EMPTY, 16, SPIKE, N_LINE, 15, 2, CUR_LINE, -1, SPIKE,SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 12, N_LINE, 18, 4, EMPTY, 1, SPIKE,SPIKE,
					SPIKE,SPIKE, CUR_LINE, -1, RESET_LINES, EMPTY, 40, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 10, LINE, 4, LAST_LEN, 1, EMPTY, 45, N_LINE, 15, 2, EMPTY, 12, CUR_LINE, -1,
					N_LINE, 15, 4, EMPTY, 8, CUR_LINE, -1, LINE, 4, LAST_LEN, 1, EMPTY, 8, SPIKE, SPIKE, SPIKE, EMPTY, 40, LINE, -1, LAST_LEN, 1, EMPTY, 10,LINE, -1, LAST_LEN, 1, EMPTY, 10,
					LINE, -1, LAST_LEN, 1, EMPTY, 10,LINE, -1, LAST_LEN, 1, EMPTY, 90, RESET_LINES, N_LINE, 140, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, CUR_LINE, 1,
					EMPTY, 20, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, EMPTY, 32, CUR_LINE, -1, N_LINE, 22, 2, EMPTY, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1,
					RESET_LINES, EMPTY, 20, RESET_LINES, N_LINE, 95, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, CUR_LINE, 1, LAST_LEN, 1, EMPTY, 11, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1, LAST_LEN, 1, EMPTY, 11, SPIKE, SPIKE, SPIKE,
					EMPTY, 30, LINE, -2, LAST_LEN, 1,EMPTY, 16, LINE, -5, EMPTY, 12,  RESET_LINES, N_LINE, 13, 5, LINE, 1, EMPTY, 18, N_LINE, 13, 6, LINE, 1, EMPTY, 22, N_LINE, 13, 6, LINE, 1, EMPTY, 12,/* N_LINE, 13, 4, LINE, 1, EMPTY, 16, N_LINE, 13, 6, LINE, 1, EMPTY, 16, N_LINE, 13, 5, LINE, 1, EMPTY, 16, N_LINE, 13, 4, LINE, 1,/*N_LINE, 20, 6, CUR_LINE, -1, EMPTY, 24, N_LINE, 20, 6, CUR_LINE, -1,
					EMPTY, 24, N_LINE, 20, 4, CUR_LINE, -1, RESET_LINES, EMPTY, 24,*/CUR_LINE, -1, EMPTY, 12, LINE, 5, LAST_LEN, 1, EMPTY, 30, LINE, 2, LAST_LEN, 1,
					EMPTY, 16,LINE, 2, LAST_LEN, 1, EMPTY, 16,LINE, 2, LAST_LEN, 1, EMPTY, 34,LINE, 2, LAST_LEN, 1, EMPTY, 40, ARC, 38, 1, SPIKE, SPIKE, SPIKE,
					LAST_LEN, 1, EMPTY, 40, SPIKE, SPIKE, SPIKE, LINE, 2, LAST_LEN, 1,RESET_LINES, N_LINE, 50, 2, LINE,60, CUR_LINE, -1, EMPTY, 16, DIR_CHANGER, EMPTY, 3, LINE, 30,
					EMPTY, 170, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 20, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 50, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 40, SPIKE,SPIKE,SPIKE,
					LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 30, RESET_LINES, N_LINE, 57, 2, CUR_LINE, -1, LINE, -2, CUR_LINE, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,
					SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, -1, LAST_LEN, 1, LINE, 2, EMPTY, 50, LAST_LEN,1, LINE, 2, EMPTY, 5, LINE, -2,
					EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -5, EMPTY, 16, ARC, 20, 1, LAST_LEN, 1, EMPTY, 12, SPIKE, SPIKE, SPIKE, EMPTY, 55, LINE, 2,EMPTY, 5, LINE, -2,
					EMPTY, 10, RESET_LINES, N_LINE, 220, 4, CUR_LINE, -1, EMPTY, 5, LINE, 4,EMPTY, 3, LINE, -4, CUR_LINE, 1, LAST_LEN, 1, EMPTY, 5, SPIKE, SPIKE,
					SPIKE, LINE, 2, LAST_LEN, 1, EMPTY, 20, SPIKE, SPIKE, EMPTY, 20, LINE, 1, EMPTY, 2, LINE, 1, EMPTY,2, LINE, -1, EMPTY, 2, LINE, -1, EMPTY, 14, LINE, -1,
					LAST_LEN, 1, EMPTY, 36, CUR_LINE, -1, LAST_LEN, 1, N_LINE, 750, 2, EMPTY, 1, LINE, 30, CUR_LINE, -1,EMPTY, 5, LAST_LEN, 1, LINE, -1, LAST_LEN, 1,
					EMPTY, 12, LINE, -1, LAST_LEN, 1, EMPTY, 30, LINE, 2, LAST_LEN, 1, EMPTY, 20, ARC, 15, -1, LAST_LEN, 1, EMPTY, 20, SPIKE, SPIKE, SPIKE,
                    EMPTY, 50, SPIKE, SPIKE, SPIKE, EMPTY, 50, LINE, 2, EMPTY, 5, LINE, -4, LAST_LEN, 1, EMPTY, 30, SPIKE, SPIKE, EMPTY, 30, LINE, 2, EMPTY, 5, LINE, -2,
					EMPTY, 8, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 8, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 30, SPIKE, SPIKE, EMPTY, 10, RESET_LINES, N_LINE, 170,14, CUR_LINE, -1, EMPTY, 10, SPIKE, SPIKE,
					CUR_LINE, 1, LAST_LEN, 1, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, CUR_LINE, -1,LAST_LEN,1, EMPTY, 6, LINE,2, LAST_LEN, 1,
					EMPTY, 14, CUR_LINE,1, LAST_LEN, 1, SPIKE, SPIKE, CUR_LINE,-1,LAST_LEN,1, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE,2, LAST_LEN, 1, EMPTY, 46, DIR_CHANGER, EMPTY, 4, LINE, 4, SPIKE, SPIKE, EMPTY, 100,
					CUR_LINE, -1, LAST_LEN,1, LINE, 2, EMPTY, 6, LINE, -4, EMPTY, 36, LAST_LEN,1, LINE, 2, LAST_LEN,1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 20, LINE, -1, EMPTY, 26,
					LINE, 1, LAST_LEN, 1, EMPTY, 16, RESET_LINES, N_LINE, 30, 2, EMPTY, 16,CUR_LINE, -1, LINE, 4, LAST_LEN, 1, EMPTY, 10, LINE, -1, EMPTY, 8, LAST_LEN, 1, LINE, -1,
					LAST_LEN, 1, EMPTY,30,RESET_LINES, N_LINE, 30, 2, EMPTY, 20, CUR_LINE, -1,RESET_LINES, N_LINE, 30, 4, EMPTY, 20, CUR_LINE, -1,RESET_LINES, N_LINE, 250, 7, CUR_LINE, -1, LINE, 7, EMPTY, 3, LINE, -7, CUR_LINE, 1, LAST_LEN, 1, EMPTY, 40, LINE, 2,
					EMPTY, 5, LINE, -2, EMPTY, 8, LINE,4, EMPTY, 5, LINE, -4, EMPTY, 9, LINE, 7, EMPTY, 8, LINE, -7, EMPTY, 9, LINE, 4, EMPTY, 5, LINE, -4, EMPTY, 8,
					SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, EMPTY, 70, CUR_LINE, -1,LAST_LEN,1, SPIKE, SPIKE, LINE, 2, EMPTY, 5, LINE, -2, SPIKE, SPIKE, SPIKE,SPIKE,
					SPIKE, RESET_LINES, N_LINE, 30, 2, EMPTY, 16, CUR_LINE, -1, N_LINE, 30, 4, EMPTY, 20, CUR_LINE, -1, LINE, 7, LAST_LEN, 1, RESET_LINES, N_LINE, 300, 25,
					LINE, -18, LAST_LEN, 1, SPIKE_D, SPIKE_D, EMPTY, 20, LINE, -2, EMPTY, 4, LINE, 2, EMPTY, 10, CUR_LINE, -1, LAST_LEN, 1, LINE, 2, CUR_LINE, 1, SPIKE_D, SPIKE_D, CUR_LINE, -1, LINE, -2,
					EMPTY, 10, CUR_LINE, 1, LAST_LEN, 1, LINE, -2, EMPTY, 4, LINE, 2, CUR_LINE, -1, LAST_LEN, 1, EMPTY, 20, SPIKE, SPIKE, EMPTY, 20, LINE, 2, CUR_LINE, 1, SPIKE_D, SPIKE_D,
					CUR_LINE, -1, LINE, -2, EMPTY, 8, N_LINE, 10, 4, EMPTY, 1, SPIKE,SPIKE,SPIKE, CUR_LINE, -1, EMPTY, 40, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 40, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 40,
					LINE, 2, LAST_LEN, 1, EMPTY, 20,LINE, 2, LAST_LEN, 1, EMPTY, 20,LINE, 2, LAST_LEN, 1, EMPTY, 20,LINE, 2, LAST_LEN, 1, EMPTY, 136, RESET_LINES, N_LINE, 1100, 25, EMPTY,1,
					LINE, -22, CUR_LINE, -1, GM_CHANGER, 1, EMPTY, 3, CUR_LINE, 1, LAST_LEN, 1, LINE, 3, EMPTY, 100, LAST_LEN, 1, LINE, -2, EMPTY, 4, LINE, 2, CUR_LINE, -1, LAST_LEN, 1, EMPTY, 16, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 120,
					CUR_LINE, 1, LAST_LEN, 1, LINE, 1, CUR_LINE, -1, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,
					SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, CUR_LINE, 1, LAST_LEN, 1, LINE, -1, CUR_LINE, -1, LAST_LEN, 1, EMPTY, 120,
					LINE, 3, EMPTY, 5, LINE, -3, EMPTY, 88, GM_CHANGER, 0, CUR_LINE, 1, LAST_LEN, 1, LINE, -2, CUR_LINE, -1, LAST_LEN, 1, EMPTY, 60, SPIKE, SPIKE, SPIKE,
					LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 10, LINE, -2, LAST_LEN, 1, EMPTY, 24, LINE,2, EMPTY, 18, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, LINE, -3, LAST_LEN, 1,
					EMPTY, 40, LINE,2, EMPTY, 18, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, LINE, -3, LAST_LEN, 1, EMPTY, 40, LINE,2, EMPTY, 18, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, LINE, -3, LAST_LEN, 1,
					EMPTY, 40, LINE,2, EMPTY, 18, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, LINE, -3, LAST_LEN, 1, EMPTY, 60, LINE, 2, LAST_LEN, 1, EMPTY, 16,
					LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 24, LINE, -1, LAST_LEN, 1, EMPTY, 10, LINE ,-1, EMPTY, 10, LAST_LEN, 1, LINE, -1, LAST_LEN, 1, EMPTY, 60,
					LINE, 2, DIR_CHANGER, RESET_LINES, N_LINE, 10, 3, EMPTY, 1,SPIKE, SPIKE, CUR_LINE, -1, LINE, -2, EMPTY, 180, LINE, 1, EMPTY, 5, LINE, -1,
					EMPTY, 60, LINE, 1, LAST_LEN, 1, EMPTY, 20, LINE, 1, LAST_LEN, 1, EMPTY, 16, LINE,-1, LAST_LEN, 1, EMPTY,18, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 18, LINE, 1, LAST_LEN, 1,
					EMPTY, 40, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1,EMPTY, 60, LINE, 2, N_LINE, 10, 3, EMPTY, 1, LINE, 60, CUR_LINE, -1, EMPTY, 3, LEVEL_END, LINE, -2, EMPTY, 10, HELPER,N_LINE_ACT, 14, 0, 1, ARC, 6,3, CUR_LINE, -1,HELPER,N_LINE_ACT, 17, 1, 1, LINE, 3,EMPTY,1, ARC, 3, 8, ARC, 3, -8,N_LINE_ACT, 14, 0, 1, HELPER,
					LINE, 2, ARC,6, -3,HELPER,CUR_LINE, -1, EMPTY, 15, LINE, 15
			},
			//EPIC SERIES - 102
			{
				LINE, 1, SPIKE, SPIKE, LINE,-1, SPIKE, SPIKE,  EMPTY, 30, SPIKE, LINE, 2, EMPTY, 4, LINE, -2, SPIKE, EMPTY, 30, LINE,1, SPIKE, SPIKE, LINE, -1, ///1 PART
					EMPTY, 30, N_LINE, 15, 2, EMPTY, 1, SPIKE, SPIKE, SPIKE, CUR_LINE, -1, EMPTY,10, SPIKE, SPIKE, SPIKE, EMPTY, 70, LINE, 2, EMPTY, 16, LAST_LEN, 1,
					LINE, 2, EMPTY, 16, LAST_LEN, 1,LINE, 2, EMPTY, 26, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, EMPTY, 30, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 9,
					LINE, 4, EMPTY, 7, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 8, LINE, -7, EMPTY, 12, LINE, 10, LAST_LEN, 1, RESET_LINES, N_LINE, 395, 25, LINE, -23,
					EMPTY, 4, LINE, 6, CUR_LINE, -1, LAST_LEN, 1, EMPTY, 30, SPIKE, SPIKE, SPIKE, EMPTY, 15, CUR_LINE, 1, LINE, -6, EMPTY, 4, LINE, 6, EMPTY, 15, CUR_LINE, -1,
					SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE, N_LINE, 25, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 30, SPIKE, SPIKE,
					SPIKE, LINE, 2, EMPTY, 14, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 16, LINE, -5, LAST_LEN, 1, EMPTY, 11,  LINE, 5, EMPTY, 7, LINE, -5,
					EMPTY, 10, LINE, 3, EMPTY, 5, LINE, -3, EMPTY, 8, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 25, SPIKE, SPIKE, SPIKE, EMPTY, 25, LINE, 2, EMPTY, 5, LINE, -2,
					EMPTY, 8, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 8, LINE, -7, EMPTY, 10, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 8, LINE, 2, EMPTY, 5, LINE, -2,
					SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 26, LINE, -5, EMPTY, 16, RESET_LINES, N_LINE, 30, 5, LAST_LEN, 1, LINE, -1, CUR_LINE, -1, LAST_LEN, -1, EMPTY, 26,
					N_LINE, 30, 4, LAST_LEN, 1, LINE, -1, CUR_LINE, -1, LAST_LEN, -1, EMPTY, 24, N_LINE, 30, 5, LAST_LEN, 1, LINE, -1, EMPTY, 30, CUR_LINE, -1, LAST_LEN, -1, LINE, 5,
					EMPTY, 40, LINE, 2, LAST_LEN, 1, EMPTY, 16, LINE, 2, LAST_LEN, 1, EMPTY, 30, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 6, LINE, -5, EMPTY, 18, LINE, 5, EMPTY, 6, LINE, -1,
					SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 30, SPIKE, SPIKE, SPIKE, LINE, -4, LAST_LEN, 1, EMPTY, 50, SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, SPIKE, EMPTY, 40, LINE, -1, LAST_LEN, 1,
					EMPTY, 14, LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 6, LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 20,
					SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 5, LINE, -1, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, LINE, 1, EMPTY,5, LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 40, LINE, 2, EMPTY, 16, LAST_LEN, 1,
					LINE, 2, EMPTY, 16, LAST_LEN, 1, LINE, 2, EMPTY, 16, LAST_LEN, 1, LINE, 2, EMPTY, 12, LAST_LEN, 1, LINE, -5, RESET_LINES, N_LINE, 580, 5, EMPTY, 20, LAST_LEN, 1,
					LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 25, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 10, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 2, EMPTY, 5, LINE, -2,
					EMPTY, 25, LINE, 1, EMPTY, 16, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 16, LINE, -1, LAST_LEN, 1, EMPTY, 30, LINE, 1, EMPTY, 5, LINE, -1,
					EMPTY, 20, ARC, 28, 2, LINE, -4, EMPTY, 12, LINE, 5, ARC, 20, 2, EMPTY, 10, CUR_LINE, -1, LAST_LEN, 1, N_LINE, 505,144, LAST_LEN, 1, EMPTY, 10, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE,
					SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 30, LINE, 2, EMPTY, 5, LINE, -2, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 6, LINE, -1,
					SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 6, LINE, -1,SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 6, LINE, -1,SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 5, LINE, -2,
					EMPTY, 8, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 7, LINE, -7, CUR_LINE, -1, LAST_LEN, 1, EMPTY, 10, LINE, 406, EMPTY, 10,LINE, -206, LAST_LEN, 1, EMPTY, 10, LINE, 10,EMPTY, 8, LINE, -10, EMPTY, 10,
					LINE,7, EMPTY, 10, LINE, -7, EMPTY, 10, LINE,10, EMPTY, 8, LINE, -10, EMPTY, 20, LINE, 10, EMPTY, 8, LINE, -10, EMPTY, 10, LINE, 7, EMPTY, 10, LINE, -7, EMPTY, 8, LINE, 4, LAST_LEN, 1, EMPTY, 10, LINE, -1,
					SPIKE, SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 8, LINE, -1, LAST_LEN, 1, EMPTY, 30, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20,
					SPIKE, SPIKE, SPIKE, EMPTY, 20,SPIKE, SPIKE, SPIKE, EMPTY, 30, LINE, 2, EMPTY, 5, LINE, -5, EMPTY, 8, LINE, 3, EMPTY, 7, LINE, -2, LAST_LEN, 1,
					SPIKE, SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1, EMPTY, 5, LINE, -5, EMPTY, 8, LINE, 3, EMPTY,7, LINE, -2, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,
					LINE, 1, EMPTY, 5, LINE, -5, EMPTY, 8, LINE, 3, EMPTY, 7, LINE, -2, LAST_LEN,1, SPIKE, SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE, SPIKE,SPIKE, SPIKE, SPIKE, SPIKE, LINE, 1,EMPTY, 30,
					LINE, 2, RESET_LINES, N_LINE, 1835, 46, EMPTY, 1, LINE, -42, EMPTY, 3, LINE, 8, CUR_LINE,-1, LAST_LEN, 1, EMPTY, 20, LINE, 2, EMPTY, 16, LINE, 2, EMPTY, 5, GM_CHANGER, 1, LINE, -5, LAST_LEN, 1,
					EMPTY, 45, LINE,3, SPIKE, SPIKE, SPIKE, LINE, -3, EMPTY, 30, CUR_LINE, 1, LINE, -8, EMPTY, 4, LINE,8, EMPTY, 30, CUR_LINE, -1, LINE,3, SPIKE, SPIKE, SPIKE, LINE, -3,
					EMPTY, 30, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, EMPTY, 6,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,EMPTY, 6, SPIKE,SPIKE, SPIKE,SPIKE,
					SPIKE,SPIKE, EMPTY, 6,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,EMPTY, 6, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, EMPTY, 6,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,
					EMPTY, 30, CUR_LINE,1, LINE, -8, CUR_LINE, -1, GM_CHANGER, 0, CUR_LINE, 1, EMPTY, 4, LINE,8, CUR_LINE, -1, EMPTY, 20,
					SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, CUR_LINE, 1, LINE, -8, CUR_LINE, -1, GM_CHANGER, 1, CUR_LINE, 1, EMPTY, 4, LINE,8,
					EMPTY, 30, CUR_LINE, -1, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, EMPTY, 6,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,EMPTY, 6, SPIKE,SPIKE, SPIKE,SPIKE,
					SPIKE,SPIKE, EMPTY, 6,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,EMPTY, 6, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, EMPTY, 6,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,
					EMPTY, 30, LINE, 3, SPIKE, SPIKE,SPIKE, LINE, -3, EMPTY, 30, LINE, 2, EMPTY, 7, LINE, -2, EMPTY, 30,
					LINE, 3, SPIKE, SPIKE, SPIKE, LINE, -3, EMPTY, 30, CUR_LINE, 1, LINE, -8, CUR_LINE, -1, GM_CHANGER, 0, CUR_LINE, 1, EMPTY, 3, LINE, 8, CUR_LINE, -1,
					EMPTY, 20, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 9, LINE, 4, EMPTY, 7, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 20, LAST_LEN, 1, LINE, -2, EMPTY, 25, LINE, 1, LAST_LEN, 1,
					EMPTY, 20, LINE, -2, EMPTY, 25, LAST_LEN, 1, SPIKE, SPIKE, SPIKE, EMPTY, 20, LINE, 2, LAST_LEN, 1, /**/CUR_LINE, 1, LINE, -2, CUR_LINE, -1,GM_CHANGER, 1, EMPTY, 30, ARC, 50, 3,LAST_LEN,1, SPIKE, SPIKE, ARC, 60, 3, EMPTY, 16,LAST_LEN,1,
					RESET_LINES, N_LINE,10,2, EMPTY,1, LINE,60, CUR_LINE,-1, GM_CHANGER, 0, EMPTY, 28, LINE, 1, EMPTY, 5, LINE, -1, EMPTY, 35, LINE, 1, EMPTY, 5, LINE, -1,
					EMPTY, 30, LINE, 2, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, LINE, -4, LAST_LEN, 1,EMPTY, 30, LINE, -4, EMPTY, 16, LINE, 4, EMPTY, 8, LINE, -4, EMPTY, 16, LINE, 4, EMPTY, 8, LINE, -4, EMPTY, 16, LINE, 4,
					EMPTY, 8, LINE, -4, EMPTY, 16, LINE, 4, EMPTY, 25, LINE, 2, EMPTY, 16, LAST_LEN, 1, LINE, 2, EMPTY, 16, LAST_LEN, 1, LINE,2, LAST_LEN, 1, EMPTY, 30, LINE, -2,LAST_LEN, 1,
					RESET_LINES, N_LINE, 30, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,LINE, -2,LAST_LEN, 1,
					RESET_LINES, N_LINE, 30, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,LINE, -2,LAST_LEN, 1,
					RESET_LINES, N_LINE, 30, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,LINE, -2,LAST_LEN, 1,
					RESET_LINES, N_LINE, 30, 2, CUR_LINE, -1, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 2, LAST_LEN, 1, EMPTY, 50,
					SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 40, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 5, LINE, -2, SPIKE, SPIKE, SPIKE, EMPTY, 40,SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 40,SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 5, LINE, -2, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE, SPIKE,
					LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 7, LINE, -7, EMPTY, 3, RESET_LINES, N_LINE, 50, 7, CUR_LINE, -1, EMPTY, 2, DIR_CHANGER, EMPTY, 20,
					N_LINE, 50, 3, EMPTY, 17, DIR_CHANGER, EMPTY, 3, LINE, 16, CUR_LINE, -1, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, EMPTY, 150, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 8, LINE, 4, LAST_LEN, 1, EMPTY, 14, SPIKE,SPIKE,SPIKE, LINE, -5,
					LAST_LEN, 1, EMPTY, 40, SPIKE, SPIKE, SPIKE, LINE, -4, LAST_LEN, 1, EMPTY, 30, ARC, 70, 2, LAST_LEN, 1, EMPTY, 20, LINE, -2, EMPTY, 16, N_LINE, 120, 2, EMPTY, 15, SPIKE, SPIKE, LINE, 2, EMPTY, 5, LINE, -2, SPIKE, SPIKE, EMPTY, 20, SPIKE, SPIKE,
					LINE, 2, EMPTY, 4, LINE, -2, CUR_LINE, -1, LINE, 2, EMPTY, 20, SPIKE,
					SPIKE, SPIKE, ARC, 70, 2, EMPTY, 10, LAST_LEN, 1, LINE, -3, EMPTY, 16, RESET_LINES, N_LINE, 300, 3, EMPTY, 15, SPIKE, SPIKE, EMPTY, 15, LINE, 50, CUR_LINE, -1, LAST_LEN, 1, LINE, 2, EMPTY, 7, LINE, -4, EMPTY, 8, LINE, 3, EMPTY, 8, LINE, -3,
					EMPTY, 8, LINE, 5, EMPTY, 8, LINE, -5, EMPTY, 8, LINE, 3, EMPTY, 8, LINE, -3, EMPTY, 8, LINE, 5, EMPTY, 8, LINE, -5, EMPTY, 8, LINE, 3, EMPTY, 8, LINE, -3, EMPTY, 8, LINE, 1, EMPTY, 6, LINE, -1,
					SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,LINE, 1, EMPTY, 6, LAST_LEN, 1, LINE, -4, EMPTY, 16, LINE, 4, EMPTY, 16, SPIKE, SPIKE, SPIKE, LINE, 2, EMPTY, 16, LAST_LEN, 1, SPIKE, SPIKE, SPIKE,
					LINE, 2, LAST_LEN, 1, EMPTY, 12, LINE, -5, EMPTY, 16, RESET_LINES, N_LINE, 30, 5, EMPTY, 26, CUR_LINE, -1, N_LINE, 30, 7, CUR_LINE, -1, N_LINE, 30, 4, EMPTY, 1, SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE,
					EMPTY, 12, CUR_LINE, -1, RESET_LINES, N_LINE, 30, 5, EMPTY, 22, CUR_LINE, -1, N_LINE, 30, 4, CUR_LINE, -1, EMPTY, 16, N_LINE, 28, 3, EMPTY, 1, SPIKE, SPIKE,SPIKE, SPIKE,SPIKE, SPIKE, CUR_LINE, -1,
					LINE, 4, LAST_LEN, 1, EMPTY, 20, SPIKE, SPIKE, SPIKE, EMPTY, 20, LINE, 2, EMPTY, 5, LINE, -2, SPIKE, SPIKE, SPIKE, LINE, 4, EMPTY, 16, LAST_LEN, 1, LINE, 2, EMPTY, 16, LAST_LEN, 1, LINE, 2, LAST_LEN, 1, RESET_LINES, N_LINE, 10, 2, EMPTY, 1, LINE, 40, EMPTY, 4,
					CUR_LINE, -1, GM_CHANGER, 1, LINE, -5, EMPTY, 40, LINE, 5, ARC, 25, 5, LAST_LEN, 1, RESET_LINES, N_LINE, 58, 3, EMPTY, 1, LINE, 40, CUR_LINE, -1, EMPTY, 5, DIR_CHANGER, EMPTY, 12, SPIKE, SPIKE, SPIKE, SPIKE, LINE, 40, EMPTY, 5, LINE, -20, EMPTY, 140,
					LAST_LEN, 1, SPIKE, SPIKE, SPIKE, EMPTY, 15, RESET_LINES, N_LINE,550, 36, EMPTY, 1, LINE, -34, CUR_LINE, -1, GM_CHANGER, 0, EMPTY,3, CUR_LINE, 1, LINE, 8, CUR_LINE, -1, EMPTY, 18,
					LINE, 2, LAST_LEN, 1, EMPTY, 24, LINE, 2, LAST_LEN, 1, EMPTY, 3, GM_CHANGER, 1, EMPTY, 2, LINE, -3, EMPTY, 36, LAST_LEN, 1, LINE, 3, EMPTY, 5, LINE, -3, EMPTY, 20, CUR_LINE, 1, LINE, -10, EMPTY, 30, LINE, 10,
					EMPTY, 22, CUR_LINE, -1, LINE, 5, EMPTY, 40, LINE, -5, EMPTY, 16, CUR_LINE, 1, LINE, -11, EMPTY, 40, CUR_LINE, -1, GM_CHANGER, 0,
					EMPTY, 20, SPIKE, SPIKE, SPIKE, SPIKE, LINE, -2, EMPTY, 16, ARC, 30, 1, LINE, -2, SPIKE, SPIKE, SPIKE, SPIKE, ARC, 30 ,1, LINE, -2, SPIKE, SPIKE, SPIKE, SPIKE, ARC, 30 ,1, LINE, -2, SPIKE, SPIKE, SPIKE, SPIKE, LAST_LEN, 1,
					EMPTY, 26, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 22, SPIKE, SPIKE, SPIKE, LINE, -2, LAST_LEN, 1, EMPTY, 24, SPIKE, SPIKE, SPIKE, RESET_LINES, N_LINE, 200, 2, EMPTY,20, LINE, 1, EMPTY, 5, LINE, -1,
					EMPTY, 20, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 20, LINE, 1, EMPTY, 5, LINE, -1, CUR_LINE, -1, EMPTY, 46/**/, LINE, 2, EMPTY, 5, LINE, -2, EMPTY, 8, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 7, LINE, -7, EMPTY, 10, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 7, LINE, -7,EMPTY, 10, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 7, LINE, -7,EMPTY, 10, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 7, LINE, -7, EMPTY, 10,EMPTY, 10, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 10, LINE, 7, EMPTY, 7, LINE, -7, EMPTY, 10, LINE, 4, EMPTY, 6, LINE, -4, EMPTY, 50,
					RESET_LINES, LINE, 2, N_LINE, 10, 3, EMPTY, 1, LINE, 60, CUR_LINE, -1, EMPTY, 3, LEVEL_END, LINE, -2, EMPTY, 10, HELPER,N_LINE_ACT, 14, 0, 1, ARC, 6,3, CUR_LINE, -1,HELPER,N_LINE_ACT, 17, 1, 1, LINE, 3,EMPTY,1, ARC, 3, 8, ARC, 3, -8,N_LINE_ACT, 14, 0, 1, HELPER,
					LINE, 2, ARC,6, -3,HELPER,CUR_LINE, -1, EMPTY, 15, LINE, 15
			},
			//103
			{
					EMPTY, 100000//DIR_CHANGER, EMPTY, 160, GM_CHANGER, 1
					//LINE, 2, N_LINE, 10, 3, EMPTY, 1, LINE, 60, CUR_LINE, -1, EMPTY, 3, LEVEL_END, LINE, -2, EMPTY, 10, HELPER,N_LINE_ACT, 17, 0, 1, ARC, 4,3, CUR_LINE, -1,HELPER,N_LINE_ACT, 17, 1, 1, LINE, 1,EMPTY,1, LINE, 2, ARC, 2, 8, ARC,2, -8, LINE, -2,N_LINE_ACT, 14, 0, 1, HELPER,//LINE, 1,EMPTY, 1, ARC,4, -3,HELPER,CUR_LINE, -1, EMPTY, 15, LINE, 15//, LINE, -3, EMPTY, 1, CUR_LINE, -1
            },
			//104 - level end
			{ LINE, 2, N_LINE, 10, 3, EMPTY, 1, LINE, 60, CUR_LINE, -1, EMPTY, 3, LEVEL_END, LINE, -2, EMPTY, 10, HELPER,N_LINE_ACT, 14, 0, 1, ARC, 6,3, CUR_LINE, -1,HELPER,N_LINE_ACT, 17, 1, 1, LINE, 3,EMPTY,1, ARC, 3, 8, ARC, 3, -8,N_LINE_ACT, 14, 0, 1, HELPER,
					LINE, 2, ARC,6, -3,HELPER,CUR_LINE, -1, EMPTY, 15, LINE, 15},
			//105 - r
			{LETTER, N_LINE, 35, 1, CUR_LINE, -1, RESET_LINES, LINE, -2, LAST_LEN, 1, EMPTY, 3, N_LINE, 15, 3, CUR_LINE, -1, N_LINE, 15, 2, LINE, 1, EMPTY, 1, LINE, -1, CUR_LINE, -1, N_LINE, 5, 1, LINE, 1, CUR_LINE, -1, EMPTY, 3, N_LINE, 5, 1, LINE, 2, RESET_LINES, EMPTY, 6, CUR_LINE, -1, LINE, 3},
			//106 - o
			{LETTER, N_LINE, 30, 1, CUR_LINE, -1, RESET_LINES, LINE, -2, LAST_LEN, 1, EMPTY, 3, N_LINE, 12, 2, CUR_LINE, -1, N_LINE, 12, 1, LINE, 1, EMPTY, 1, LINE, -1, EMPTY, 1, LINE, 1, EMPTY, 6, CUR_LINE, -1, LINE, 3},
            //107 - azimuth
            {

            },
			{LINE, 2, N_LINE, 12, 3, EMPTY, 1, LINE,60, EMPTY, 3, CUR_LINE, -1, GM_CHANGER, 0,LINE, -2, EMPTY,5},
			{EMPTY, 20, LINE, 2, EMPTY, 4, LINE, -2, EMPTY, 3, GM_CHANGER, 4},
			{LEVEL_END, 1000},
			{LINE, 2, N_LINE, 12, 3, EMPTY, 1, LINE,60, EMPTY, 3, CUR_LINE, -1, GM_CHANGER, 3,LINE, -2, EMPTY,5},
			{GM_CHANGER, 3, N_LINE, 10, 3, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, EMPTY,5},
			{GM_CHANGER, 1, N_LINE, 10, 2, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, EMPTY,5},
			{GM_CHANGER, 2, N_LINE, 10, 2, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, EMPTY,5},
			{GM_CHANGER, 0, N_LINE, 10, 2, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1},
			{GM_CHANGER, 1, N_LINE, 10, 2, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, EMPTY,5}
	};

	private int[][] complexBaseGM1 = {
			{},
			{N_LINE, 10, 2, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60},
			{LINE,4, EMPTY, 5, LINE, -4},
			{N_LINE, 10, 3, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60},
			{LINE,2, EMPTY, 5, LINE, -2},
			{LINE,3, EMPTY, 5, LINE, -3},

			{N_LINE, 10, 2, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60},
			{LINE,2, SPIKE,SPIKE,SPIKE,SPIKE, LINE, -2},
			{N_LINE, 10, 3, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60},
			{LINE,3, SPIKE,SPIKE,SPIKE,SPIKE, LINE, -3},

			{LINE, 2, N_LINE, 10, 4, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, LINE, -2},
			{LINE, 3, N_LINE, 10, 5, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, LINE, -3},
			{LINE, 4, N_LINE, 10, 6, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, LINE, -4},
			{LINE, 5, N_LINE, 10, 6, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, LINE, -5},
			{SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE,SPIKE},
			{LINE,1, SPIKE,SPIKE,SPIKE,SPIKE, LINE, -1},
            {LINE,2, SPIKE,SPIKE,SPIKE,SPIKE, LINE, -2},
			{LINE,3, SPIKE,SPIKE,SPIKE,SPIKE, LINE, -3},
			{LINE,4, SPIKE,SPIKE,SPIKE,SPIKE, LINE, -4},

			//19
			{LINE, 2, N_LINE, 35, 5, LINE, 30, EMPTY, 16, CUR_LINE, -1, N_LINE, 40, 8, LINE, 30, CUR_LINE, -1, EMPTY, 10, LINE, 2},
			//20
			{LINE, 3, N_LINE, 35, 5, LINE, 30, EMPTY, 16, CUR_LINE, -1, N_LINE, 90, 8, LINE, 30, CUR_LINE, -1, EMPTY, 10, LINE, 2, EMPTY, 20, LINE, -2, EMPTY, 10, N_LINE, 35, 5, LINE, 30, CUR_LINE, -1 }
	};
	private int[][] complexBaseGM2 = {
			{},
			{N_LINE, 10, 2, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60},
			{LINE,4, EMPTY, 5, LINE, -4},
			{N_LINE, 10, 3, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60},
			{LINE,3, EMPTY, 5, LINE, -3},
			{LINE,2, EMPTY, 5, LINE, -2},

			{LINE, 2, N_LINE, 10, 4, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, LINE, -2},
			{LINE, 3, N_LINE, 10, 5, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, LINE, -3},
			{LINE, 4, N_LINE, 10, 6, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, LINE, -4},
			{LINE, 5, N_LINE, 10, 6, EMPTY, 1, LINE,60, EMPTY, 3, LINE, -60, CUR_LINE, -1, LINE, -5}
	};
	private int[][] complexBaseGM3 = {
			{},
			{N_LINE, 75, 7, EMPTY, 15, LINE, 3, EMPTY, 3, LINE, -3, EMPTY, 15},
			{N_LINE, 55, 7, EMPTY, 26},
			{N_LINE, 55, 7, EMPTY, 12, LINE, 1, LINE, -1, EMPTY, 15},
			{N_LINE, 120, 10, EMPTY, 12, LINE, 4, EMPTY, 3, LINE, -4, EMPTY, 6, LINE, 7, EMPTY, 3, LINE, -7, EMPTY, 20}
	};

	private Array<MyColor[]> currentColors;
	private MyColor colors[][] = {
			//0 - back, 1 - edge, 2 - gradient
            {new MyColor(0,0,0), new MyColor(255,255,255), new MyColor(111, 111, 111)},
			{new MyColor(54,5,112), new MyColor(0,0,0), new MyColor(255, 154, 64)},
			{new MyColor(142,0,57), new MyColor(44,5,13), new MyColor(255, 154, 64)},
			{new MyColor(22,24,81), new MyColor(0,0,0), new MyColor(230, 36, 122)},
			{new MyColor(178, 83,144), new MyColor(0, 0, 15), new MyColor(237, 177, 159)},
			{new MyColor(0,70,70), new MyColor(0,0,0), new MyColor(255,255,0)},
			{new MyColor(77,0,60), new MyColor(20,0,22), new MyColor(0,86,60)},
			{new MyColor(20,22,21), new MyColor(0,0,0), new MyColor(145,35,32)}
    };
	private MyColor currentColorBack, currentColorEdge, currentColorGradient;
	private int colorIndex = -1, lastColor, nextOP;

    static float speedLine;
    static boolean isGame, isGround, isPause, firstStarted;
	private Random rnd;
	private ShapeRenderer sr;
    public Array<LineRenderer> lines;
    public LineRenderer curLine;
    public SimpleLineRenderer backLine;
    public Array<LineRenderer> linesToDestroy;
    private int currentLine;
    private GlyphLayout glyphLayout;
    private BitmapFont font;
    private Vector2 rocketPos;

    //PHYSICS
	private Box2DDebugRenderer box2dRen;
	static World physWorld;
	private double accumulator, currentTime;
	private float step = 1.0f/60.0f;
	static int gravity;
	static boolean activePlayer;

	//ACTORS
	private Stage stage, back;
	private UI ui;
	private TaskManager taskManager;
	static GameObject edgeBody, borderLeft, borderRight;
	public static Character player;
	private SceneObject destroyer;
	private Fixture fixture;
	private GameObject backGradient;
	public Array<SceneObject> bodies;
	public Array<SceneObject> objectsToDestroy;
	private Array<LineRenderer> toAction;
	static Array<ParticleEffectActor> helpers;
	private Array<Spawner> spawners;
	public ParticleEffectActor jetpackEffect;
	private Image jetpack, deathPoint, mainBack, jetpackOutline;

	private Group spawnerPlace;

	private Texture[] texturesSpawner;// = { new Texture(Gdx.files.internal("quad.png")), new Texture(Gdx.files.internal("back_circle.png"))};
	private float kAnimPlayer, speedDown, speedUp;
	int canDJump;

	//TASK STATS
	public int taskJumps;
	private int taskRecordTime, taskTimesRow;
	private float taskAirTime, taskGMTime;

	public class MyColor{
	    public float r;
	    public float g;
        public float b;

        public MyColor(float r, float g, float b)
        {
            this.r = r;
            this.b = b;
            this.g = g;
        }

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        ui.getViewport().update(width, height, true);
        //ui.setViewport();
    }

	@Override
	public void pause() {
		super.pause();
		taskManager.saveData();
		if(isGame && !mainMenu && !isArcade) {
			setPause(true);
			ui.showPauseMenu();
		}
	}

	@Override
	public void resume() {
		super.resume();
		taskManager.loadData();
	}

	@Override
	public void create () {
            adInterface= new AdInterface() {
            @Override
            public void loadInterstitialAd() {

            }

            @Override
            public void showInterstitialAd() {

            }

            @Override
            public void redirect(String url) {

            }

            @Override
            public void setBanner(boolean active) {

            }

            @Override
            public void loadAfterDeadRAd() {

            }

            @Override
            public void showAfterDeadAd() {

            }
        };

	    main = this;
		boolean debug = false;

		mainMenu = true;

        HEIGHT = 40f;
        WIDTH = HEIGHT*Gdx.graphics.getWidth()/Gdx.graphics.getHeight();

		centerX = WIDTH / 2;
		centerY = HEIGHT / 2;
		step = 1.0f / 60.0f;
		sr = new ShapeRenderer();

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.setToOrtho(true, WIDTH, HEIGHT);
		cam.near = 0;
		cam.update();

		preferences = Gdx.app.getPreferences("prefs");

		if(!preferences.contains("activeMusic")) {
			preferences.putBoolean("activeMusic", true);
			preferences.flush();
		}
		if(!preferences.contains("activeSounds")) {
			preferences.putBoolean("activeSounds", true);
			preferences.flush();
		}
		if(!preferences.contains("activeOutline")) {
			preferences.putBoolean("activeOutline", true);
			preferences.flush();
		}

		money = preferences.getInteger("money");
		bestTime = preferences.getInteger("bestTime");
		levelMode = preferences.getInteger("level_mode");
		autoRetry = preferences.getBoolean("autoRetry");
		activeMusic = preferences.getBoolean("activeMusic");
		activeSounds = preferences.getBoolean("activeSounds");
		activeOutline = preferences.getBoolean("activeOutline");
		lowGraphics = preferences.getBoolean("lowGraphics");
		if(lowGraphics)
			constFrames = 30;
		else constFrames = 60;
		if(!preferences.getBoolean("firstStarted"))
		{
            preferences.putBoolean("purchasedSkin" + Character.Male, true);
			preferences.putInteger("equippedSkin", Character.Male);
			preferences.putBoolean("firstStarted", true);
			preferences.flush();
		}

		box2dRen = new Box2DDebugRenderer();

		rnd = new Random();

		lines = new Array<LineRenderer>();
		linesToDestroy = new Array<LineRenderer>();
		toAction = new Array<LineRenderer>();
		helpers = new Array<ParticleEffectActor>();
		diffStages = new Array<Integer>();
		currentColors = new Array<MyColor[]>();
		diffArcade = new Array<Integer>();
		skillsActive = new Array<Skill>();
		spawners = new Array<Spawner>();

		glyphLayout = new GlyphLayout();
		font = new BitmapFont();
		font.setColor(Color.CYAN);
		font.getData().setScale(1);

		musics = new Music[5];
		for(int i = 0; i < musics.length-1; i++)
		{
			musics[i] = Gdx.audio.newMusic(Gdx.files.internal("music/" + (i) + ".mp3"));
		}
		musics[musics.length-1] = Gdx.audio.newMusic(Gdx.files.internal("music/endless.mp3"));
		soundManager = new SoundManager();
        texturesSpawner = new Texture[]{new Texture(Gdx.files.internal("quad.png")),
				new Texture(Gdx.files.internal("back_circle.png")),
				new Texture(Gdx.files.internal("dirChange.png"))
		};
		if (!debug) {
			stage = new Stage(new FillViewport(WIDTH, HEIGHT, cam));
			back = new Stage(new FillViewport(WIDTH, HEIGHT, cam));
			physWorld = new World(new Vector2(0, 0), false);
			ui = new UI(this);
			taskManager = new TaskManager(ui);

			Gdx.input.setInputProcessor(ui);

			bodies = new Array<SceneObject>();
			objectsToDestroy = new Array<SceneObject>();

			backGradient = new GameObject(new Vector2(centerX - 8, centerY - 8), physWorld, 16, 16);
			backGradient.name = "backGr";
			backGradient.setCamera(cam);
			backGradient.createTexture(new Texture(Gdx.files.internal("backGradient.png")), 22, 22);
			backGradient.sprite.setPosition(centerX -11, centerY -11);

			mainBack = new Image(new Texture(Gdx.files.internal("mainBack.png")));
			mainBack.setSize(130, 130);
			mainBack.setPosition(centerX - mainBack.getWidth()/2, centerY - mainBack.getHeight()/2);


			//PHYSICS OBJECTS
			Filter filter = new Filter();

			PolygonShape ps = new PolygonShape();
			ps.setAsBox(1, HEIGHT);

			filter.categoryBits = CATEGORY_BORDERS;
			filter.maskBits = MASK_BORDERS;
			borderLeft = new GameObject(new Vector2(centerX - 1, centerY), physWorld, 1, 1);
			borderLeft.name = "borderLeft";
			borderLeft.createPhysics(ps, BodyDef.BodyType.StaticBody, 1, 0, 0, filter);
			borderLeft.getBody().setUserData("BR");

			borderRight = new GameObject(new Vector2(centerX + 1, centerY), physWorld, 1, 1);
			borderRight.name = "borderRight";
			borderRight.createPhysics(ps, BodyDef.BodyType.StaticBody, 1, 0, 0, filter);
			borderRight.getBody().setUserData("BR");

			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(0.45f);
			circleShape.setPosition(new Vector2(0, 0f));

			filter.categoryBits = CATEGORY_PLAYER;
			filter.maskBits = MASK_PLAYER;
			player = new Character(new Vector2(centerX, centerY - 4), physWorld, 1.5f,1.5f,  this);
			player.name = "player";
			player.createPhysics(circleShape, BodyDef.BodyType.DynamicBody, 1, 0, 0, filter);
			player.getBody().getFixtureList().get(0).setUserData("playerHead");

			CircleShape circleShape2 = new CircleShape();
			circleShape2.setPosition(new Vector2(0, 0.5f));
			circleShape2.setRadius(0.45f);
			player.getBody().createFixture(circleShape2, 1);
			player.getBody().getFixtureList().get(1).setFriction(0);
			player.getBody().getFixtureList().get(1).setRestitution(0);
			player.getBody().getFixtureList().get(1).setFilterData(filter);
			//player.getBody().getFixtureList().get(1).setUserData("playerHead");

		 	CircleShape c = new CircleShape();
            c.setPosition(new Vector2(0, -0.5f));
			c.setRadius(0.45f);
            player.getBody().createFixture(c, 1);
            player.getBody().getFixtureList().get(2).setFriction(0);
            player.getBody().getFixtureList().get(2).setRestitution(0);
            player.getBody().getFixtureList().get(2).setFilterData(filter);
			player.getBody().getFixtureList().get(2).setUserData("playerHead");

			player.getBody().setUserData("player");
			player.setCamera(cam);
			player.sprite = player.animSprites[0][0];
			player.rotate = true;
			player.createOutline();
			jetpackEffect = new ParticleEffectActor(player.getBody().getPosition(), physWorld, 1.5f, 1.5f, "jetPart.p");
			jetpackEffect.effect.getEmitters().get(0).getVelocity().setHigh(6, 6);
			jetpackEffect.setVisible(false);

			jetpack = new Image(new Texture(Gdx.files.internal("jetpack.png")));
			jetpack.setSize(0.9f, 2f);
			jetpack.setColor(Color.ORANGE);
			jetpackOutline = new Image(new Texture(Gdx.files.internal("jetpack.png")));
			jetpackOutline.setSize(0.95f, 2.05f);
			jetpackOutline.setColor(Color.WHITE);
			jetpackOutline.setVisible(false);

			deathPoint = new Image(new Texture(Gdx.files.internal("back_circle.png")));
			deathPoint.setSize(1, 1);
			deathPoint.setColor(Color.CORAL);

			///*****************

			//PARENTS
			back.addActor(mainBack);
			stage.addActor(backGradient);
			stage.addActor(jetpackOutline);
			stage.addActor(jetpackEffect);
			stage.addActor(jetpack);
			stage.addActor(player);
			stage.addActor(borderRight);
			stage.addActor(borderLeft);
			stage.addActor(deathPoint);










			

			physWorld.setContactListener(new ContactProcess());

            currentColorBack = colors[0][0];
            currentColorEdge = colors[0][1];
            currentColorGradient = colors[0][2];
            backGradient.sprite.setColor(currentColorGradient.r/255f, currentColorGradient.g/255f, currentColorGradient.b/255f, 1);

			lines.add(new LineRenderer(centerX, centerY, ShapeRenderer.ShapeType.Filled, physWorld, 270, 0, false));
			backLine = new SimpleLineRenderer(centerX, centerY, ShapeRenderer.ShapeType.Filled, 21);
			backLine.setColor(new Color(0.5f,0.5f,0.5f, 1f));
			backLine.setProjection(cam);
			for (int i = 0; i < 180; i++)
			{
				angleInRadians = (float)Math.toRadians(lines.get(0).angle);
				x = (float)Math.cos(angleInRadians) * lines.get(0).radius;
				y = (float)Math.sin(angleInRadians) * lines.get(0).radius;
				LinePoint position = new LinePoint(x, y, 0);
				lines.get(0).positions.add(position);
				backLine.positions.add(position);
				System.out.println(position);
				lines.get(0).angle -= 2;
			}
			lines.get(0).hasOutline = activeOutline;
			player.hasOutline = activeOutline;
			backLine.positions.add(new Vector2(0,0));
			backLine.positions.add(new Vector2(0,0));

			///load skills
			///

			gravity = 1;
			newGame(true);

			System.out.println(lines.get(0).positions.size);
			Vector2 dir = new Vector2(centerX - player.getBody().getPosition().x, centerY - player.getBody().getPosition().y);
			stockLenPl = dir.len();
			cam.zoom = 0.8f;
			camCoeff = cam.zoom/stockLenPl;
			plRad = circleShape.getRadius()/stockLenPl;
			genCoeff = 1.25f/7f;
			playerSCoeffX = player.sprite.getWidth()/stockLenPl;
			playerSCoeffY = player.sprite.getHeight()/stockLenPl;
			jumpValue = 8;
			borderCoeff = 2f;
		}
		spawnerPlace = new Group();
		stage.addActor(spawnerPlace);
		Spawner spawner = new Spawner(new Texture(Gdx.files.internal("quad.png")), 0.1f, 0.3f, 0.04f);
		spawnerPlace.addActor(spawner);
		spawners.add(spawner);

		Image aster = new Image(new Texture(Gdx.files.internal("aster.png")));
		aster.setSize(100,100);
		aster.setPosition(centerX - aster.getWidth()/2, centerY - aster.getHeight()/2);
		aster.setColor(Color.BLACK);
		//stage.addActor(aster);

		//ui.setDebugAll(true);
		radiusPl = 1;
		lnthOp = 0;
		Gdx.graphics.setVSync(true);

		fpsLogger = new FPSLogger();

		box2dRen.AABB_COLOR.set(Color.WHITE);
		box2dRen.setDrawVelocities(true);

		ui.play.setVisible(true);
	}
	FPSLogger fpsLogger;
	private long diff, start = System.currentTimeMillis();

	public void sleep(int fps) {
		if(fps>0){
			diff = System.currentTimeMillis() - start;
			long targetDelay = 1000/fps;
			if (diff < targetDelay) {
				try{
					Thread.sleep(targetDelay - diff);
				} catch (InterruptedException e) {}
			}
			start = System.currentTimeMillis();
		}
	}
	boolean increaseSpeed = true;
	int b = 0;
	@Override
	public void render () {
		Color backColor = new Color(currentColorBack.r/255f,currentColorBack.g/255f, currentColorBack.b/255f, 1);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, backColor.a);
		mainBack.setColor(backColor);

		//TIMERSUPDATE
		if(!isPause && !mainMenu) {
			timer += Gdx.graphics.getRawDeltaTime();
			taskGMTime += Gdx.graphics.getRawDeltaTime();
			//System.out.println("gmtime " + taskGMTime);
			if(gameMode != 0)
				diffTime += Gdx.graphics.getRawDeltaTime();

			if(timer < levelLength[currentLevel])
				if(timer < levelLength[currentLevel] - 5)
					curProgressVal += 290f / levelLength[currentLevel] * Gdx.graphics.getRawDeltaTime();

			if(isArcade)
				ui.setTime((int)timer);
			else {
				if(timer < levelLength[currentLevel] - 5)
					ui.setProgress((int) curProgressVal, (int) (timer / levelLength[currentLevel] * 100));
				else ui.setProgress((int) curProgressVal, 99);
			}
			if(!isGround)
				taskAirTime += Gdx.graphics.getRawDeltaTime();
		}
		update();
		Color bC = new Color(Math.abs(currentColorGradient.r / 255f) * 0.9f, Math.abs(currentColorGradient.g / 255f)*0.9f, Math.abs(currentColorGradient.b / 255f) * 0.9f, 1);
		Color bC2 = new Color(bC.r * 0.7f, bC.g * 0.7f, bC.b * 0.7f, 1);
		back.draw();
		backLine.draw(bC, bC2);
		stage.draw();


		if(increaseSpeed || lowGraphics)
			update();
		for (LineRenderer lineRenderer : lines) {
			lineRenderer.setProjection(cam);
			lineRenderer.draw();
		}
		//DIRECTIONTRIANGLE
		if(!isGame || isPause) {
			//LINEDRAW
			drawTriangle(0);
		} else {
			if(increaseSpeed || lowGraphics)
				drawTriangle(speedLine * 2);
			else
				drawTriangle(speedLine);
		}
		if(deathPoint.isVisible()) {
			if (deathPoint.getWidth() < 4f)
				deathPoint.setSize(deathPoint.getWidth() + 0.2f, deathPoint.getHeight() + 0.2f);
			else deathPoint.setSize(0, 0);
			deathPoint.setPosition(player.getBody().getPosition().x - deathPoint.getWidth() / 2f, player.getBody().getPosition().y - deathPoint.getHeight() / 2f);
		}

		//System.out.println(spawners.get(0).getChildren().size);
		//box2dRen.render(physWorld, cam.combined);

		taskManager.update(Gdx.graphics.getDeltaTime());
        ui.draw();
		ui.update();
		ui.act(Gdx.graphics.getDeltaTime());
		back.act(Gdx.graphics.getDeltaTime());

		if(player.accelerating)
		{
			if(STATE == STATE_NORMAL)
			{
				acceleratePlayer(true);
				ui.flash.setColor(ui.flash.getColor().r,ui.flash.getColor().g,ui.flash.getColor().b, 0.2f);
				ui.flash.setVisible(true);
				ui.flash.addAction(Actions.forever(Actions.sequence(Actions.alpha(0f, 0.3f, Interpolation.fade),Actions.alpha(0.2f, 0.3f, Interpolation.fade))));
				STATE = STATE_ACCEL;
			}
		}
		else {
			if(STATE == STATE_ACCEL) {
				acceleratePlayer(false);
				ui.flash.setVisible(false);
				ui.flash.clearActions();
				STATE = STATE_NORMAL;
			}
		}
		if(player.slowing)
		{
			if(STATE == STATE_NORMAL)
			{
				slowdownPlayer(true);
				STATE = STATE_SLOW;
			}
		}
		else {
			if(STATE == STATE_SLOW) {
				slowdownPlayer(false);
				STATE = STATE_NORMAL;
			}
		}

		for(int i = 0; i < spawners.size; i++)
			spawners.get(i).update(curLine.getColor());

		if(increaseSpeed || lowGraphics)
			sleep(constFrames);
	}
	void update(){
		float delta = 1;

		if(isGame && !isPause) {

			//ANGLE CHANGES AND POSES
			angleInRadians = (float) Math.toRadians(angle);
			x = (float) Math.cos(angleInRadians);
			y = (float) Math.sin(angleInRadians);

			Vector2 dir = new Vector2(centerX - player.getBody().getPosition().x, centerY - player.getBody().getPosition().y);

			realLenPlayer = dir.len();
			if (gravity > 0 && !nonProportion) {
				lenPlayer = realLenPlayer;
			}
			radiusPl = plRad * lenPlayer* player.scaleFactor;

			backGradient.sprite.setScale(radiusPl*2);

			dir = dir.nor();

			//PLAYERCONTROL
			int swipeVertical = 0;
			if (ui.GetMouseButton()) {
				swipeVertical = (int)(ui.mousePosition.y - ui.startMouseY);
			}
			//
			Vector2 jetPos = player.getBody().getWorldPoint(player.getJetPos());
			jetpack.setPosition(jetPos.x, jetPos.y);
			jetpack.setRotation(player.getRotation()+speedLine*10+player.spriteAngle);
			jetpack.setColor(player.sprite.getColor());
			jetpackOutline.setPosition(jetpack.getX(), jetpack.getY());
			jetpackOutline.setRotation(jetpack.getRotation());
			jetpackEffect.setPosition(jetpack.getX(), jetpack.getY());
			jetpackEffect.setRotation(player.getRotation() - 40);
			jetpackEffect.angle = 0;

			if (activePlayer) {
				if (ui.GetMouseButton()) {
					switch (gameMode) {
						case 0:
							if(isGround && playerSTAGE == 0)
								taskJumps++;
							if (isGround) {
								jumpValue = 101;
								player.getBody().setLinearVelocity(new Vector2(0, 0));
								kAnimPlayer = 0;
								playerSTAGE = 1;
								player.cleanAnim();

								player.getBody().applyLinearImpulse(new Vector2(-dir.x * jumpValue * gravity * delta* (2 -player.scaleFactor), -dir.y * jumpValue * gravity * delta* (2 -player.scaleFactor)), player.body.getWorldCenter(), true);

								isGround = false;
							}

							if (hasSkill("doubleJump") && canDJump == 1) {
								jumpValue = 103;
								player.getBody().setLinearVelocity(new Vector2(0, 0));
								player.getBody().applyLinearImpulse(new Vector2(-dir.x * jumpValue * gravity * delta* (2 -player.scaleFactor), -dir.y * jumpValue * gravity * delta* (2 -player.scaleFactor)), player.body.getWorldCenter(), true);
								canDJump++;
							}

							break;
						case 1:
							if (realLenPlayer < (lastLen + 45 * plRad * lastLen)) {
								jumpValue = 73f * radiusPl;
								player.getBody().setLinearVelocity(new Vector2(0, 0));
								playerSTAGE = 2;
								//jetpackEffect.effect.reset();
								jetpackEffect.setVisible(true);

								player.getBody().applyLinearImpulse(new Vector2(-dir.x * jumpValue * gravity * (2 -player.scaleFactor), -dir.y * jumpValue * gravity* (2 -player.scaleFactor)), player.body.getWorldCenter(), true);
							}
							break;
						case 2:
							if(realLenPlayer < (lastLen+35*plRad*lastLen) || swipeVertical < 0) {
								player.getBody().setLinearVelocity(new Vector2(0, 0));
								player.getBody().applyLinearImpulse(new Vector2(-dir.x * swipeVertical * 103 * gravity/200f, -dir.y * swipeVertical * 103 * gravity/200f), player.body.getWorldCenter(), true);
							}
							break;

					}
				}

				//GRAVITY

				if (gameMode != 2) {
					if(player.scaleFactor < 1)
						player.getBody().applyLinearImpulse(new Vector2(dir.x * gravityAcceleration * gravity* (2 -player.scaleFactor) * 17f/14f, dir.y * gravityAcceleration * gravity* (2 -player.scaleFactor)* 17f/14f), player.body.getWorldCenter(), true);
					else player.getBody().applyLinearImpulse(new Vector2(dir.x * gravityAcceleration * gravity, dir.y * gravityAcceleration * gravity), player.body.getWorldCenter(), true);
				}
			}


			if (playerSTAGE == 0) {
				if(isGround)
					player.animate(0, 0.6f);
				else {
					if(gameMode == 1) {
						player.animate(3, 0.2f);
						jetPos = player.getBody().getWorldPoint(player.getJetPosFly());
						jetpack.setPosition(jetPos.x, jetPos.y);
						jetpackOutline.setPosition(jetpack.getX(), jetpack.getY());
						if(player.spriteAngle < 0)
							player.spriteAngle += 2f;
					}
					else player.animate(0, 0.6f);
				}
				jetpackEffect.setVisible(false);
			} else if (playerSTAGE == 1) {
				if (!player.animate(1, 0.4f)) {
					playerSTAGE = 0;
				}
			} else if (playerSTAGE == 2) {
				if (player.animate(2, 0.2f))
				{
					jetPos = player.getBody().getWorldPoint(player.getJetPosFly());
					jetpack.setPosition(jetPos.x, jetPos.y);
					jetpackOutline.setPosition(jetpack.getX(), jetpack.getY());
				}
				if(player.spriteAngle > -6)
					player.spriteAngle -= 2f;
				playerSTAGE = 0;
			}

			if (isGround)
				canDJump = 0;

			//CAMCHANGES
			cam.zoom = MathUtils.lerp(cam.zoom, camCoeff * realLenPlayer, Gdx.graphics.getDeltaTime()*2);

			player.sprite.setColor(new Color(currentColorEdge.r / 255f + 0.1f, currentColorEdge.g / 255f+ 0.1f, currentColorEdge.b / 255f+ 0.1f, 1));
			player.sprite.setSize(-playerSCoeffX * lenPlayer * player.scaleFactor, playerSCoeffY * lenPlayer * player.scaleFactor);

			player.getBody().getFixtureList().get(0).getShape().setRadius(radiusPl);
			player.getBody().getFixtureList().get(1).getShape().setRadius(radiusPl);
			player.getBody().getFixtureList().get(2).getShape().setRadius(radiusPl);
			((CircleShape)(player.getBody().getFixtureList().get(0).getShape())).setPosition(new Vector2(0, 0));
			((CircleShape)(player.getBody().getFixtureList().get(1).getShape())).setPosition(new Vector2(0, radiusPl));
			((CircleShape)(player.getBody().getFixtureList().get(2).getShape())).setPosition(new Vector2(0, -radiusPl));
			player.rotateBy(speedLine);
			cam.rotate(Vector3.Z, speedLine * speedCamCoeff);
			camAngle += speedLine * speedCamCoeff;
			cam.update();

			//BORDERSCHANGE
			borderCoeff = 2.015f*radiusPl;
			//borderCoeff = 2.015f * radiusPl;
			borderLeft.getBody().setTransform(x * borderCoeff + centerX, y * borderCoeff + centerY, (float)(Math.toRadians(angle-2)));
			borderRight.getBody().setTransform(-x * borderCoeff + centerX, -y * borderCoeff + centerY, (float)(Math.toRadians(angle-2)));
			PolygonShape polygonShape = new PolygonShape();
			polygonShape.setAsBox(radiusPl, HEIGHT * cam.zoom / 2);
			Filter filter = new Filter();
			filter.maskBits = MASK_BORDERS;
			filter.categoryBits = CATEGORY_BORDERS;
			fixture = borderLeft.getBody().getFixtureList().get(0);
			borderLeft.getBody().destroyFixture(fixture);
			borderLeft.createFixture(polygonShape, filter);
			fixture = borderRight.getBody().getFixtureList().get(0);
			borderRight.getBody().destroyFixture(fixture);
			borderRight.createFixture(polygonShape, filter);

			//CIRCLEGENERATOR
			if (operation == 0) {
				if (nextOP == -1) {
					if (genMode == 0 && scaleFactor >= 1) {
						if (diffStages.size <= 0) {
							if(isArcade && !mainMenu) {
								diffStages.addAll(diffArcade);
								diffStages.addAll(player.getIndCmds());
							}
							else if(!isArcade && !mainMenu) diffStages.addAll(gameMode0presets[currentPreset]); //diffStages.addAll(diffLevels[currentLevel]);
							else if(mainMenu) diffStages.addAll(diffLevels[0]);
						}
						operation = diffStages.get(rnd.nextInt(diffStages.size));
						diffStages.removeValue(operation, true);
						lOp = (int) ((45 + Math.abs(MathUtils.random(5, 10)) * radiusPl*2) / 2);
						complexOpList = complexBase[operation];
					} else if(genMode == 0 && scaleFactor < 1){
						operation = microModepresets[currentPreset][MathUtils.random(0, microModepresets[currentPreset].length - 1)];//MathUtils.random(1, complexBaseGM1.length-1);
						lOp = (int) ((5 + Math.abs(MathUtils.random(5, 10)) * radiusPl*2) / 2);
						complexOpList = complexBase[operation];
					} else if (genMode == 1) {
						operation = gameMode1presets[currentPreset][MathUtils.random(0, gameMode1presets[currentPreset].length - 1)];//MathUtils.random(1, complexBaseGM1.length-1);
						lOp = (int) ((5 + Math.abs(MathUtils.random(5, 10)) * radiusPl*2) / 2);
						complexOpList = complexBaseGM1[operation];
					} else if (genMode == 2) {
						operation = gameMode2presets[currentPreset][MathUtils.random(0, gameMode2presets[currentPreset].length - 1)];//MathUtils.random(1, complexBaseGM2.length-1);
						lOp = (int) ((5 + Math.abs(MathUtils.random(5, 10)) * radiusPl*2) / 2);
						complexOpList = complexBaseGM2[operation];
					} else if (genMode == 4) {
						operation = gameMode3presets[currentPreset][MathUtils.random(0, gameMode3presets[currentPreset].length - 1)];//MathUtils.random(1, complexBaseGM2.length-1);
						lOp = (int) ((5 + Math.abs(MathUtils.random(5, 10)) * radiusPl*2) / 2);
						complexOpList = complexBaseGM3[operation];
					}
				} else {
					operation = nextOP;
					lOp = (int) (60 * radiusPl*2);
					complexOpList = complexBase[nextOP];
					nextOP = -1;
				}
				curLine = lines.get(0);
				radiusTmp = lines.get(0).radius;
				lnthOp = 0;

				isComplex = true;
			}
			else if (operation == -1) {
				empty();
				isComplex = false;
			}

			if (isComplex) {
				complexGenerating();
			}
			angle += speedLine;

			//LEVEL COMMAND UPDATE
			if(!isArcade) {
				if (!mainMenu && timer > timeLevelCmd && numCmd < levelCommands[currentLevelCmds].length) {
					numCmd += 2;
					System.out.println("NumCMD : " + levelCommands[currentLevelCmds][numCmd]);
					if (countCmd == 0)
						countCmd = (int) levelCommands[currentLevelCmds][numCmd - 1];
					for (int i = 0; i < countCmd; i++) {
						numCmd = changingCommand(numCmd, levelCommands[currentLevelCmds][numCmd], currentLevelCmds, levelCommands);
						System.out.println("ChgCMD : " + numCmd);
					}

					if (numCmd < levelCommands[currentLevelCmds].length) {
						timeLevelCmd = levelCommands[currentLevelCmds][numCmd];
						countCmd = 0;
					}
				}
			}
			else {
				if(timer > diffTime)
				{
					changeDifficulty();
				}
				else if(timer > timeArcMode)
				{
					int mode = Math.abs(MathUtils.random(1, arcadeModes.length-1));
					if(gameMode != 0 || player.scaleFactor < 1)
						mode = 0;

					numCmd = 0;
					numCmd = changingCommand(numCmd, arcadeModes[mode][numCmd], mode, arcadeModes);
					timeArcMode += 10 + Math.abs(MathUtils.random(0, 8));
				}
				if(taskWord && timer > timeWord)
				{
					nextOP = taskManager.getTask().word[preferences.getInteger("taskCompleteWord")];

					timeWord += 12 + Math.abs(MathUtils.random(0, 15));
				}
			}

			if (isChangingColor) {
				if (colorIndex == -1) {
					if (currentColors.size <= 0)
						currentColors.addAll(colors);
					colorIndex = Math.abs(rnd.nextInt(currentColors.size));
				}
				boolean a = colorChange(currentColorEdge, currentColors.get(colorIndex)[1]);
				boolean b = colorChange(currentColorGradient, currentColors.get(colorIndex)[2]);
				boolean c = colorChange(currentColorBack, currentColors.get(colorIndex)[0]);
				//	System.out.println("color edge : " + backGradient.sprite.getColor().r + " " + backGradient.sprite.getColor().g + " " + backGradient.sprite.getColor().b);
				MyColor grColor = new MyColor(Math.abs(currentColorGradient.r / 255f), Math.abs(currentColorGradient.g / 255f), Math.abs(currentColorGradient.b / 255f));
				backGradient.sprite.setColor(grColor.r, grColor.g, grColor.b, 1);

				for (LineRenderer lineRenderer : lines) {
					lineRenderer.setColor(new Color(currentColorEdge.r / 255f, currentColorEdge.g / 255f, currentColorEdge.b / 255f, 1));
					//lineRenderer.obstacleColor.set(Color.WHITE);
				}
				ui.setBGSkillsColor(lines.get(0).sr.getColor());
				if (a && b && c){
					currentColors.removeIndex(colorIndex);
					colorIndex = -1;
					isChangingColor = false;
				}
			}

			if (isChangingZoom) {
				float zoom = camCoeff * stockLenPl;
				if (zoom < dZoom) {
					camCoeff += 0.01f / stockLenPl;
					stockCamZoom += 0.01f;
				} else if (zoom > dZoom) {
					camCoeff -= 0.01f / stockLenPl;
					stockCamZoom -= 0.01f;
				}
				if (zoom - 0.05f <= dZoom && dZoom <= zoom + 0.05f) isChangingZoom = false;

			}
			if (isImpulse) {
				float zoom = camCoeff * stockLenPl;
				if (isGrowZoom && zoom < stockCamZoom + dImZoom) {
					camCoeff += 0.005f / stockLenPl;
				} else if (!isGrowZoom && zoom > stockCamZoom - dImZoom) {
					camCoeff -= 0.005f / stockLenPl;
				}
				if ((zoom - 0.005f <= stockCamZoom + dImZoom && stockCamZoom + dImZoom <= zoom + 0.005f))
					isGrowZoom = false;
				if ((zoom - 0.005f <= stockCamZoom - dImZoom && stockCamZoom - dImZoom <= zoom + 0.005f))
					isGrowZoom = true;
			}

			if(dirChanging && !physWorld.isLocked())
			{
				if(levelMode == 0)
					speedChange();
				else speedChangeRand();
			}
			if(levelEnding && !physWorld.isLocked())
			{
				cam.zoom = 1f;
				soundManager.play(soundManager.SOUND_ROCKET);

				if(speedLine < 0) {
					camAngle += speedLine * 20;
					cam.rotate(speedLine * 20);
				} else{
					camAngle += 50;
					cam.rotate(50);
				}

				Vector2 rocketPos = new Vector2((float)Math.cos(Math.toRadians(camAngle-90))*20, (float)Math.sin(Math.toRadians(camAngle-90))*20);
				for (int i = 0; i < toAction.size; i++)
				{
					toAction.get(i).setGlobalPosition(rocketPos);
				}
				for(int i = 0; i < helpers.size; i++)
				{
					helpers.get(i).setVisible(true);
					helpers.get(i).offset = new Vector2(rocketPos.x -  helpers.get(i).getX(), rocketPos.y -  helpers.get(i).getY());
				}
				player.setVisible(false);
				levelEndTimer = 4;
				darkFlash();
				setPause(true);
			}

			if(gravity > 0)
				rebuildCircle();

			if (!mainMenu)
				lastTouchPos = ui.mousePosition;
		}

		if(!isGame) {
			if (!physWorld.isLocked() && !isPause) {
				isPause = true;
			}
		}

	//	stage.draw();
		if (isFlash) {
			ui.flash.setColor(ui.flash.getColor().r, ui.flash.getColor().g, ui.flash.getColor().b, ui.flash.getColor().a - 0.1f);
			if (ui.flash.getColor().a <= 0) {
				ui.flash.setColor(1, 1, 1, 1);
				ui.flash.setVisible(false);
				isFlash = false;
			}
		}
		if(isGame && levelEnding) {
			levelEndTimer -= Gdx.graphics.getDeltaTime();
			if (levelEndTimer < 0) {
				levelEnd();
				levelEndTimer = 100000;
				toAction.clear();
			} else {
				if(levelEndTimer < 1) {
					float rKX = MathUtils.random(1.008f,1.02f);
					float rKY = MathUtils.random(1.008f,1.02f);
					for (int i = 0; i < toAction.size; i++) {
						toAction.get(i).actOn = true;
						toAction.get(i).globalPosition.x *= rKX;
						toAction.get(i).globalPosition.y *= rKY;
					}
					for (int i = 0; i < helpers.size; i++) {
						helpers.get(i).setPosition(toAction.get(0).globalPosition.x- helpers.get(i).offset.x,toAction.get(0).globalPosition.y- helpers.get(i).offset.y);
					}
				}
				cam.zoom += 0.002f;
			}
		}

		for (LineRenderer lineRenderer : lines) {
			lineRenderer.act();
		}

		if(isGame && !isPause) {
			//LINEUPDATE
			for (LineRenderer lineRenderer : lines) {
				lineRenderer.setProjection(cam);
				lineRenderer.updateLine();

				//lineRenderer.draw();
			}
			backLine.setProjection(cam);
			backLine.updateLine(Color.ORANGE, Color.CORAL);
		}

		///////PHYSICSUPDATE
		if (physWorld != null && !isPause && isGame) {
			physWorld.step(step, 6, 8);
		}

		///DESTROYLINES
		if (linesToDestroy.size > 0 && isGame) {
			for (int i = 0; i < linesToDestroy.size; i++)
				destroyLine(linesToDestroy.get(i));
			linesToDestroy.clear();
		}
		///DESTROYOBJECTS
		if (objectsToDestroy.size > 0 && isGame) {
			for (int i = 0; i < objectsToDestroy.size; i++) {
				objectsToDestroy.get(i).getBody().setActive(false);
			}
			objectsToDestroy.clear();
		}

		if(physWorld != null && autoRetryGame && !physWorld.isLocked()) {
			deathTimer-= Gdx.graphics.getRawDeltaTime();
			if(deathTimer < 0) {
				autoRetryGame = false;
				deathTimer = 1;
				newGame(false);
			}
		}

		rPlbefore = radiusPl;
		x = 0;
		y = 0;
		fpsLogger.log();
	}
	public void setLowGraphics()
	{
		lowGraphics = !lowGraphics;
		if(lowGraphics)
			constFrames = 30;
		else constFrames = 60;
		preferences.putBoolean("lowGraphics", lowGraphics);
		preferences.flush();
	}
	public void setActiveMusic()
	{
		activeMusic = !activeMusic;
		preferences.putBoolean("activeMusic", activeMusic);
		preferences.flush();
	}
	public void setActiveSounds()
	{
		activeSounds = !activeSounds;
		preferences.putBoolean("activeSounds", activeSounds);
		preferences.flush();
	}
	public void setActiveOutline()
	{
		activeOutline = !activeOutline;
		for(int i = 0; i < lines.size; i++)
			lines.get(i).hasOutline= activeOutline;
		player.hasOutline = activeOutline;
		preferences.putBoolean("activeOutline", activeOutline);
		preferences.flush();
	}
    public void setAutoRetry()
    {
        autoRetry = !autoRetry;
        preferences.putBoolean("autoRetry", autoRetry);
        preferences.flush();
    }
	public boolean hasSkill(String name)
	{
		boolean has = false;
		for(Skill skill : skillsActive)
		{
			if(skill.name.equalsIgnoreCase(name))
				has = true;
		}
		return has;
	}
	public Skill getSkill(String name)
	{
		Skill has = null;
		for(int i = 0; i < skillsActive.size; i++)
		{
			if(skillsActive.get(i).name.equalsIgnoreCase(name))
				has = skillsActive.get(i);
		}
		return has;
	}
	public void completeTask(int reward)
	{
		money += reward;
		preferences.putInteger("money", money);
		preferences.flush();
		taskWord = false;
	}
    public void videoViewed()
    {
        setPause(false);
        mine();
        player.setVisible(true);
        isGame = true;
        ui.hideVideoPan();
    }
    public void videoRequestDie()
    {
        setPause(true);
        player.setVisible(false);
        videoViewed = true;
        ui.showVideoPan();
    }
    public void videoNotViewed()
	{
		isGame = true;
		gameOver();
	}
	public void checkVelocityTouch(Vector2 cur, Vector2 last)
	{
		if(gameMode == 2) {
			if(Math.abs(cur.x - last.x) > 0.2f)
				velocityTouch.set(cur.x - last.x, cur.y - last.y);
			else velocityTouch.set(0,0);
			player.getBody().setLinearVelocity(new Vector2(0, 0));
		}
	}

	private void changeDifficulty()
	{
		//показать сообщение, типа : легко))
		difficulty++;
		if(difficulty < diffBaseArcade.length) {
			diffArcade.addAll(diffBaseArcade[difficulty]);
			diffStages.clear();
			diffStages.addAll(diffArcade);
            diffTime += diffTimes[difficulty];
		}
		//commands
		if(difficulty == 1)
		{
			float[][] cmds = {{CHANGE_ZOOM, 1.1f, CHANGE_COLOR, PAUSE_LENGTH, 60}};
			numCmd = 0;
			for(int i = 0; i < 3; i++) {
				numCmd = changingCommand(numCmd, cmds[0][numCmd], 0, cmds);
			}
			timeArcMode = timer + 10 + Math.abs(MathUtils.random(0, 8));
		}
		else if(difficulty == 2) {
			float[][] cmds = {{CHANGE_ZOOM, 1f, CHANGE_COLOR, PAUSE_LENGTH, 50}};
			numCmd = 0;
			for(int i = 0; i < 3; i++) {
				numCmd = changingCommand(numCmd, cmds[0][numCmd], 0, cmds);
			}
			timeArcMode = timer + 10 + Math.abs(MathUtils.random(0, 8));
			//increaseSpeed = true;
		}
	}

	public int changingCommand(int n, float cmd, int currentLevel, float[][] levelCommands)
	{
		int result = n;

		switch ((int)cmd)
		{
			case CHANGE_COLOR:
				isChangingColor = true;
				colorIndex = -1;
				speedColor = 2;
				result++;
				break;
			case CHANGE_ZOOM:
				isChangingZoom = true;
				dZoom = levelCommands[currentLevel][result+1];
				result +=2;
				break;
			case GEN_MODE:
				genMode = (int)levelCommands[currentLevel][result+1];
				if(genMode == 1) {
                    if (gameMode == 2)
                        nextOP = complexBase.length - 4;
                    else
                        nextOP = complexBase.length - 1;
                }
				else if(genMode == 0)
                {
                    nextOP = complexBase.length-2;
                    if(player.scaleFactor < 1) {
						nextOP = complexBase.length - 9;
						nextSF = 1;
					}
                }
				else if(genMode == 4)
					nextOP = complexBase.length-8;

				int preset = (int)levelCommands[currentLevel][result+2];
				if(preset != -1)
					currentPreset = preset;
				else currentPreset = 0;
				result+=3;
				break;
			case SPEED_CAM:
				speedCamCoeff = levelCommands[currentLevel][result+1];
				result+=2;
				break;
			case NEXT_OP:
				nextOP = (int)levelCommands[currentLevel][result+1];
				result+=2;
				break;
			case FLASH:
				isFlash = true;
				ui.flash.setVisible(true);
				result++;
				break;
			case CHANGE_SCALE:
				//rebuildCircle();
				scaleFactor = levelCommands[currentLevel][result+1];
				result+=2;
				break;
			case IMPULSE_ON:
				isImpulse = true;
				isGrowZoom = true;
				stockCamZoom = camCoeff*lastLen;
				dImZoom = levelCommands[currentLevel][result+1];
				result+=2;
				break;
			case IMPULSE_OFF:
				isImpulse = false;
				result++;
				break;
			case DIR_CHANGE:
				nextOP = 47;
				result++;
				break;
			case 12:
				player.getBody().setLinearVelocity((float)(levelCommands[currentLevel][result+1]*Math.cos(player.getBody().getAngle())), (float)(levelCommands[currentLevel][result+1]*Math.sin(player.getBody().getAngle())));
				result += 2;
				break;
			case 13:
				genMode = 2;
				nextOP = complexBase.length-3;
				currentPreset = (int)levelCommands[currentLevel][result+1];
				result+=2;
				break;
			case 14:
				genMode = 0;
				nextOP = complexBase.length-5;
				//currentPreset = (int)levelCommands[currentLevel][result+1];
				nextSF = levelCommands[currentLevel][result+1];
				currentPreset = (int)levelCommands[currentLevel][result+2];
				if(nextSF > scaleFactor)
					nextOP = complexBase.length-6;
				result+=3;
				break;
			case PAUSE_LENGTH:
				emptyLength = (int)levelCommands[currentLevel][result+1];
				result+=2;
				break;
			case MESSAGE:
				if(!preferences.getBoolean("messageShowed" + (int)levelCommands[currentLevel][result+2])) {
					ui.showMessage((int) levelCommands[currentLevel][result + 1], (int) levelCommands[currentLevel][result + 2]);
					preferences.putBoolean("messageShowed" + (int)levelCommands[currentLevel][result+2], true);
				}
				result+=3;
				break;
			case 20:
				rebuildCircle();
				result++;
				break;
			case LEVEL_END:
                createLevelEnd();
                break;
            case BACKL_ACTIVE:
                if((int) levelCommands[currentLevel][result + 1] == 1) {
					backLine.radius = levelCommands[currentLevel][result + 2];
					backLine.rstRadius = levelCommands[currentLevel][result + 2];
					backLine.setActive(true, (int) levelCommands[currentLevel][result + 3]);
				}
                else backLine.setActive(false, (int) levelCommands[currentLevel][result + 3]);
                result+=4;
                break;
            case BACKL_EXPAND:
                if((int) levelCommands[currentLevel][result + 1] == 1)
                    backLine.setExpand(true, levelCommands[currentLevel][result + 2], levelCommands[currentLevel][result + 3], levelCommands[currentLevel][result + 4]);
                else  backLine.setExpand(false, levelCommands[currentLevel][result + 2], levelCommands[currentLevel][result + 3], levelCommands[currentLevel][result + 4]);
                result+=5;
                break;
			case BACKL_IMPULSE:
				if((int) levelCommands[currentLevel][result + 1] == 1)
					backLine.setImpulse(true, (int) levelCommands[currentLevel][result + 2], levelCommands[currentLevel][result + 3], levelCommands[currentLevel][result + 4]);
				else backLine.setImpulse(false, (int) levelCommands[currentLevel][result + 2], levelCommands[currentLevel][result + 3], levelCommands[currentLevel][result + 4]);
				result+=5;
				break;
			case  CHANGE_PRESET:
				currentPreset = (int) levelCommands[currentLevel][result + 1];
				diffStages.clear();
				result+=2;
				break;
			case CREATE_SPAWNER:
				Spawner newSpawner = new Spawner(texturesSpawner[(int) levelCommands[currentLevel][result + 1]], levelCommands[currentLevel][result + 2], levelCommands[currentLevel][result + 3], levelCommands[currentLevel][result + 4]);
				stage.addActor(newSpawner);
				spawners.add(newSpawner);
				result+=5;
				break;
            case EXPAND_EFFECT:
                back.addActor(new ExpandEffect(texturesSpawner[(int) levelCommands[currentLevel][result + 1]], (int) levelCommands[currentLevel][result + 2], centerX, centerY, new Color(Math.abs(currentColorGradient.r / 255f), Math.abs(currentColorGradient.g / 255f), Math.abs(currentColorGradient.b / 255f), 1)));
                result+=3;
                break;
			case CHANGE_COLOR_SP:
				isChangingColor = true;
				colorIndex = -1;
				speedColor = levelCommands[currentLevel][result + 1];
				result+=2;
				break;
		}

		return result;
	}
	public void flash()
    {
    	ui.flash.setVisible(true);
    	ui.flash.setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, 1);
    	ui.flash.addAction(Actions.alpha(0, 0.5f, Interpolation.fade));
        //isFlash = true;
    }
	public void darkFlash()
	{
		ui.flash.setVisible(true);
		ui.flash.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 1);
		ui.flash.addAction(Actions.alpha(0, 0.5f, Interpolation.fade));
	}
	public void rebuildCircle()
	{
		float coeff = 1/radiusPl;

		lastLen *= coeff;
		lOp *= coeff;
		radiusTmp *= coeff;
		tempRForGen *= coeff;

		player.getBody().setTransform((player.getBody().getPosition().x - centerX)*coeff + centerX, (player.getBody().getPosition().y - centerY)*coeff + centerY, player.getBody().getAngle());
		for(LineRenderer lineRenderer : lines){
			if(!lineRenderer.toRemove) {
				lineRenderer.radius *= coeff;
				if (lineRenderer.rotate) {
					lineRenderer.bR *= coeff;
				}

				for (int i = 0; i < lineRenderer.positions.size; i++) {
					lineRenderer.positions.get(i).x *= coeff;
					lineRenderer.positions.get(i).y *= coeff;
					if (lineRenderer.rotate) {
						lineRenderer.begPos.get(i).x *= coeff;
						lineRenderer.begPos.get(i).y *= coeff;
					}
				}
			}
		}
		for(int i = 0; i < bodies.size; i++)
		{
			bodies.get(i).getBody().setTransform((bodies.get(i).getBody().getPosition().x - centerX)*coeff + centerX, (bodies.get(i).getBody().getPosition().y - centerY)*coeff + centerY, bodies.get(i).getBody().getAngle());
			if(bodies.get(i).sprite != null)
				bodies.get(i).sprite.setSize(4*coeff, 4*coeff);
			//float r = (float)Math.sqrt(Math.pow(bodies.get(i).getBody().getPosition().x - centerX, 2) + Math.pow(bodies.get(i).getBody().getPosition().y - centerY, 2));

			//((PolygonShape)(bodies.get(i).getBody().getFixtureList().get(0).getShape())).setAsBox(bodies.get(i).hx*r/14f/speedUp/speedDown, bodies.get(i).hy*r/14f/speedUp/speedDown);
		}
		for(int i = 0; i < helpers.size; i++)
		{
			helpers.get(i).setPosition((helpers.get(i).getX() - centerX)*coeff + centerX, (helpers.get(i).getY() - centerY)*coeff + centerY);
		}
	}
	public void rebuildCircle(float r)
	{
		lines.get(0).angle = 0;
		operation = -1;
		nextOP = -1;
		lOp = 40;
		lastLen = r;
		realLastLen = r;
		radiusTmp = r;
		lines.get(0).radius = r;
		lnthOp = 0;
		complexId = 0;
		isComplex = false;
		for (int i = 0; i < lines.get(0).positions.size; i++)
		{
			angleInRadians = (float)Math.toRadians(lines.get(0).angle);
			lines.get(0).positions.get(i).x = (float)Math.cos(angleInRadians) * r;
			lines.get(0).positions.get(i).y = (float)Math.sin(angleInRadians) * r;
			lines.get(0).positions.get(i).type = 0;
			lines.get(0).angle += speedLine;
		}
		for(int i = 0; i < bodies.size; i++)
		{
			bodies.get(i).getBody().setActive(false);
		}
		for(int i =1; i < lines.size; i++) {
			lines.get(i).isDrawing = false;
			lines.get(i).edge.getBody().setActive(false);
		}
		angle = lines.get(0).angle;
	}

	public void changeLevel(int side)
	{
		currentLevel += side;

		boolean left = true;
		boolean right = true;

		if(currentLevel < 0)
			currentLevel = 0;
		if(currentLevel > levelNames.length-1)
			currentLevel = levelNames.length-1;

		if(currentLevel < 1) {
			left = false;
		}
		if(currentLevel >= levelNames.length-1) {
			right = false;
		}

		ui.changeLevel(levelNames[currentLevel], left, right);
		ui.refreshLevelData(currentLevel, levelMode);
	}
	public void mine()
	{
		flash();
        lnthOp = 0;
        operation = -1;
        complexId = 0;
        curLine = lines.get(0);
        currentLine = 0;
        linesGen= 0;
        isComplex = false;
        lastLen = lenPlayer-2*radiusPl;
		float a = angle, rad = lastLen;
		lines.get(0).radius = lastLen;
		for(int i = 0; i < lines.get(0).positions.size;i++)
		{
			lines.get(0).positions.get(i).set(MathUtils.cos(a*MathUtils.degRad)*(rad), MathUtils.sin(a*MathUtils.degRad)*rad);
			a += speedLine;
		}
        for(int i = 0; i<bodies.size; i++)
        {
            if(speedLine >0)
            {
                bodies.get(i).angle -= 360;
            }
            else{
                bodies.get(i).angle += 360;
            }
            if(bodies.get(i) != null) {
                if(bodies.get(i).line == 1)
                    bodies.get(i).getBody().setActive(false);
                if (bodies.get(i).dir > 0) {
                    bodies.get(i).getBody().setActive(false);

                }
                else bodies.get(i).getBody().setActive(true);
            }
        }
        for(int i = 1; i < lines.size;i++)
        {
            lines.get(i).thisLineAngle = 10000;
            for(int j = 0; j < lines.get(i).positions.size; j++)
            {
                lines.get(i).positions.get(j).x *= 20;
                lines.get(i).positions.get(j).y *= 20;
            }
            for(int j = 0; j < lines.get(i).begPos.size; j++)
            {
                lines.get(i).begPos.get(j).x *= 20;
                lines.get(i).begPos.get(j).y *= 20;
            }
        }
	}

	public void acceleratePlayer(boolean o)
	{
		increaseSpeed = o;
		if(o)
		{
			gravityAcceleration = 0;
			player.getBody().setLinearVelocity(0,0);
			constFrames = 43;
		}
		else {
			gravityAcceleration = 8.5f;
			if(lowGraphics)
				constFrames = levelFrames[currentLevel];
			else constFrames = 60;
		}
	}
    public void slowdownPlayer(boolean o)
    {
        increaseSpeed = o;
        if(o) {
			constFrames = 20;
		}
        else {
        	if(lowGraphics)
				constFrames = levelFrames[currentLevel];
			else constFrames = 60;
        }
    }
    public void letterFind()
	{
		taskWord = true;
	}
    public void letterFound()
	{
		preferences.putInteger("taskCompleteWord", preferences.getInteger("taskCompleteWord") + 1);
		preferences.flush();
		ui.showMessage(MessageType.QUESTION, UI.selectedLanguage == Text.EN ? "letter\nfound!" : "буква\nнайдена!");
	}
    public void jetEffect()
	{
		Vector2 dir = new Vector2(centerX - player.getBody().getPosition().x, centerY - player.getBody().getPosition().y).nor();
		if (realLenPlayer < (lastLen + 45 * plRad * lastLen)) {
			jumpValue = 73f * radiusPl;
			player.getBody().setLinearVelocity(new Vector2(0, 0));
			playerSTAGE = 2;
			jetpackEffect.effect.reset();
			jetpackEffect.setVisible(true);
			player.getBody().applyLinearImpulse(new Vector2(-dir.x * jumpValue * gravity * (2 -player.scaleFactor), -dir.y * jumpValue * gravity* (2 -player.scaleFactor)), player.body.getWorldCenter(), true);
		}
	}
	public String getCurrentLevelName()
    {
        return levelNames[currentLevel];
    }
    public int getLevelReward()
	{
		return levelRewards[currentLevel];
	}

	public float getAngle()
	{
		return angle;
	}
	public float getCamAngle()
	{
		return camAngle;
	}
	public Vector2 getPlayerPos()
	{
		return player.getBody().getPosition();
	}
	static void removeHelper(ParticleEffectActor pe)
	{
		helpers.removeValue(pe, true);
	}

	public boolean colorChange(MyColor toColor, MyColor fromColor)
	{
		boolean result = true;

		toColor.r = MathUtils.lerp(toColor.r,fromColor.r, speedColor*Gdx.graphics.getDeltaTime());
		toColor.g = MathUtils.lerp(toColor.g,fromColor.g, speedColor*Gdx.graphics.getDeltaTime());
		toColor.b = MathUtils.lerp(toColor.b,fromColor.b, speedColor*Gdx.graphics.getDeltaTime());

		result &= Math.abs(toColor.r - fromColor.r) < 1f;
		result &= Math.abs(toColor.g - fromColor.g) < 1f;
		result &= Math.abs(toColor.b - fromColor.b) < 1f;

		return result;
	}
	public void levelEnd()
	{
		float m = (timer + levelRewards[currentLevel]) * (hasSkill("thief") ? 1.3f : 1) * (hasSkill("multiplier") ? getSkill("multiplier").skillValue : 1);
		money += m;
		preferences.putInteger("money", money);
		preferences.putInteger("level_progress" + currentLevel + "mode" + levelMode, 100);
		if(!hasSkill("doubleJump") && !hasSkill("jet"))
			preferences.putBoolean("has_rocket" + currentLevel + "mode" + levelMode, true);

		isPause = false;
		levelEnding = false;

		if (mainMusic != null)
			mainMusic.pause();

		for(int i = 0; i < Character.reqLevels.length; i++)
		{
			if(!preferences.getBoolean("purchasedSkin" + i))
			{
				if(Character.reqLevels[i] == currentLevel && Character.reqLevelModes[i] == levelMode)
					ui.rewardSkin(i);
			}
		}

		jetpackOutline.setVisible(false);
		jetpack.setVisible(false);
		jetpackEffect.setVisible(false);

		ui.levelComplete((int) m, currentDeaths, !hasSkill("doubleJump") && !hasSkill("jet"));
		ui.refreshMoney(money);

		soundManager.play(soundManager.SOUND_COMPLETE);
		if(preferences.getInteger("taskMoney") < money)
			preferences.putInteger("taskMoney", money);

		preferences.flush();

		//player.setVisible(false);
		//deathPoint.setVisible(true);
		//deathPoint.setColor(player.sprite.getColor());
		//deathPoint.setRotation(player.getRotation());
		flash();
		isGame = false;
	}
	public void gameOver()
	{
		if(isGame) {
			System.out.println("bvcxcv");
			int p = 0;
			boolean newBest = false;
			if (isArcade) {
			    if(!videoViewed) {
					if(timer>18) {
						videoRequestDie();
						isGame = false;
						return;
					}
                }
				if (bestTime < timer) {
					newBest = true;
					bestTime = (int) timer;
					preferences.putInteger("bestTime", (int) timer);
					ui.setBestTime(bestTime);
				}
				preferences.putInteger("taskJumps", taskJumps);
				if(preferences.getInteger("taskRecordTime") < timer)
					preferences.putInteger("taskRecordTime", (int)timer);
				preferences.putInteger("taskAirTime", (int)taskAirTime);
				if(preferences.getInteger("taskSecondsRow") <= timer)
				{
					preferences.putInteger("taskTimesRow", preferences.getInteger("taskTimesRow") + 1);
				}
				else {
					if(preferences.getInteger("taskTimesRow") > 0) {
						preferences.putInteger("taskTimesRow", 0);
						//ui.showMessage(MessageType.SKILL, UI.selectedLanguage == Text.EN ? "Task updated!" : "задание обновлено!");
					}
				}
				if(gameMode == 1)
					preferences.putInteger("taskJetTime", preferences.getInteger("taskJetTime") + (int)taskGMTime);
				if(gameMode == 0 && player.scaleFactor < 1)
					preferences.putInteger("taskMicroTime", preferences.getInteger("taskMicroTime") + (int)taskGMTime);
				taskGMTime = 0;
			}
			if (!isArcade) {
				p = (int) (timer / levelLength[currentLevel] * 100);
				if (p >= 100)
					p = 98;
				if (preferences.getInteger("level_progress" + currentLevel + "mode" + levelMode) < (int) (timer / levelLength[currentLevel] * 100)) {
					preferences.putInteger("level_progress" + currentLevel + "mode" + levelMode, p);
					newBest = true;
				}
				int deathCount = preferences.getInteger("level_deaths" + currentLevel + "mode" + levelMode) + 1;
				preferences.putInteger("level_deaths" + currentLevel + "mode" + levelMode, deathCount);
			}
			if (mainMusic != null)
				mainMusic.pause();

			float m = timer * (hasSkill("thief") ? 1.3f : 1) * (hasSkill("multiplier") ? getSkill("multiplier").skillValue : 1);
			int t = 0;
			if(isArcade)
				t = (int)timer;
			money += m;
			preferences.putInteger("money", money);
			currentDeaths++;
			adsDeaths++;

			///MESSAGES
			for(int i = 0; i < Character.costSkins.length; i++)
			{
				if(money >= Character.costSkins[i] && !preferences.getBoolean("purchasedSkin" + i) && !preferences.getBoolean("skin_showed" + i))
				{
					ui.showMessage(MessageType.SKIN, 1);
					ui.newShop();
					preferences.putBoolean("skin_showed" + i, true);
				}
			}
			for(int i = 0; i < ui.skillPans.size; i++)
			{
				if(money >= ui.skillPans.get(i).getCost() && !preferences.getBoolean("purchased" + ui.skillPans.get(i).skill.name) && !preferences.getBoolean("skill_showed" + i))
				{
					ui.showMessage(MessageType.SKIN, 7);
					ui.newShop();
					preferences.putBoolean("skill_showed" + i, true);
				}
			}
			for(int i = 0; i < ui.stepSkillPans.size; i++)
			{
				if(money >= ui.stepSkillPans.get(i).getCost() && preferences.getInteger("skillLevel" + ui.stepSkillPans.get(i).skill.name) != ui.stepSkillPans.get(i).getMaxLevel()  && !preferences.getBoolean("step_skill_showed" + i + ui.stepSkillPans.get(i).getLevel()))
				{
					ui.showMessage(MessageType.SKIN, 7);
					ui.newShop();
					preferences.putBoolean("step_skill_showed" + i + ui.stepSkillPans.get(i).getLevel(), true);
				}
			}
///////////////////
			if(!autoRetry)
				ui.showRestartMenu(p, (int) m, t, currentDeaths, newBest);
			ui.refreshMoney(money);

			if(preferences.getInteger("taskMoney") < money)
				preferences.putInteger("taskMoney", money);

			preferences.flush();

			player.setVisible(false);
			deathPoint.setVisible(true);
			deathPoint.setColor(player.sprite.getColor());
			jetpackOutline.setVisible(false);
			jetpack.setVisible(false);
			jetpackEffect.setVisible(false);
			//deathPoint.setRotation(player.getRotation());

			if(adsDeaths >= 8) {
				adInterface.showInterstitialAd();
				adsDeaths = 0;
			}
			adInterface.setBanner(true);

			isGame = false;
			if(autoRetry) {
				ui.hideGameUI();
				autoRetryGame = true;
				deathTimer = 0.5f;
			}
		}
		//player.getBody().setActive(false);
	}
	private void drawTriangle(float degrees)
	{
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.setColor(new Color(currentColorEdge.r/255f - 0.1f, currentColorEdge.g/255f - 0.1f, currentColorEdge.b/255f - 0.1f, 1));
		sr.triangle(HEIGHT * cam.zoom, 4*cam.zoom, HEIGHT * cam.zoom, -4*cam.zoom, 0, 0);
		if(isGame)
			sr.rotate(0, 0, 1, degrees);
		sr.setProjectionMatrix(cam.combined);
		sr.end();
	}

	public void newGame(boolean menu) {
		System.out.println("jhkjhk");
		for (int i = 0; i < helpers.size; i++)
			helpers.get(i).effect.dispose();
		for (int i = 0; i < bodies.size; i++)
			if (bodies.get(i) != null) {
				bodies.get(i).toRemove = true;
				bodies.get(i).getBody().setActive(false);
			}

		Gdx.gl.glClear(0);
		bodies.clear();
		toAction.clear();
		objectsToDestroy.clear();
		physWorld.clearForces();

		sr.dispose();
		sr = new ShapeRenderer();
		//drawTriangle(angle);
		borderRight.getBody().setTransform(new Vector2(centerX + 64, centerY), 0);
		borderLeft.getBody().setTransform(new Vector2(centerX - 64, centerY), 0);
		player.getBody().setTransform(new Vector2(centerX, centerY - 8f), 0);

		player.setVisible(!menu);
		player.getBody().setActive(!menu);
		player.setRotation(180);
		player.scaleFactor = 1;

		jetpack.setVisible(false);
		deathPoint.setVisible(false);

		if(speedLine > 0)
			for(int i =0; i < player.animSprites.length; i++)
			{
				for(int j = 0; j < player.animSprites[i].length; j++)
				{
					player.animSprites[i][j].flip(true, false);
				}
			}

		//POSES
		cam.rotate(-camAngle);
		cam.zoom = 1;
		sr.setTransformMatrix(new Matrix4(new Vector3(centerX, centerY, 2), new Quaternion(), new Vector3(1,1,1)));

		backLine.setActive(false, SimpleLineRenderer.WAVE);
		backLine.setImpulse(false, 1, 1, 1);
		backLine.setExpand(false, 1,1,1);
		lnthOp = 0;

		lOp = 46;

		for(int i = 1; i < spawners.size; i++)
		{
			spawners.get(i).destroyAll();
			spawnerPlace.removeActor(spawners.get(i));
		}
		if(spawners.size > 1)
			spawners.removeRange(1, spawners.size-1);
        //angleInRadians = 0;
        curLine = lines.get(0);
		lines.get(0).radius = 7;
        speedLine = -2f;
        velocityTouch = new Vector2(0,0);

		//SETTINGS
		isGame = true;
		isComplex = false;
		firstStartGame = false;
		activePlayer = true;
		isChangingZoom = false;
		isChangingColor = false;
		isImpulse = false;
		nonProportion = false;
		isPause = false;
		increaseSpeed = false;
		videoViewed = false;
		levelEnding = false;
		//isFlash = true;
		gravityAcceleration = 8.5f;
		mainMenu = menu;
		currentLevelCmds = 2*currentLevel + levelMode;
		timeLevelCmd = levelCommands[currentLevelCmds][0];
		colorIndex = -1;
		lastColor = 1;
		radiusTmp = lines.get(0).radius;
		complexId = 0;
		numCmd = 0;
		countCmd = 0;
		nextOP = -1;
		camCoeff = 0.8f/stockLenPl;
		camAngle = 0;
		speedDown = 1;
		speedUp =1;
		money = 1000000;

		if(gravity < 0)
			gravityChange();
		if (mainMusic != null)
			mainMusic.setPosition(0);
			//mainMusic.dispose();

		if(!menu){
			if(!isArcade)
				mainMusic = musics[currentLevel];
			else mainMusic = musics[musics.length-1];
			if(activeMusic)
				mainMusic.play();
			mainMusic.setLooping(isArcade);
		}

		currentColors.clear();
		currentColors.addAll(colors);
		currentColors.removeIndex(0);

        currentColorEdge = new MyColor(255,255,255);
        currentColorGradient = new MyColor(111,111,111);
        currentColorBack = new MyColor(0,0,0);

		lines.get(0).setColor(Color.WHITE);
		player.sprite.setColor(1,1,1,1);
		backGradient.sprite.setColor(currentColorGradient.r/255f, currentColorGradient.g/255f, currentColorGradient.b/255f, 1);

		diffStages.clear();
		diffArcade.clear();
		helpers.clear();
		emptyLength = 45;
		if(!menu) {
			if (!isArcade) {
				constFrames = levelFrames[currentLevel];
				diffStages.addAll(gameMode0presets[diffLevels[currentLevel][0]]);
			} else {
				emptyLength = 110;
				difficulty = -1;
				changeDifficulty();
				diffTime = diffTimes[difficulty];
			}

			if(preferences.getInteger("taskFirstGame") == 0) {
				preferences.putInteger("taskFirstGame", 1);
			}
			ui.showGameUI();
		}
		else {
			//diffStages.addAll(diffLevels[0]);
		}
		progressVal = levelLength[currentLevel];
		curProgressVal = 0;
		taskGMTime = 0;
		levelEndTimer = 10000;
		timer = 0f;
		timeArcMode = 1000;
		timeWord = MathUtils.random(10, 20)+preferences.getInteger("taskCompleteWord")*5;
		timeColor = 8 + Math.abs(MathUtils.random(0,8));
        angle = lines.get(0).angle;
        backLine.angle = 0;
        angleInRadians = 0;
		operation = -1;
		genMode = 0;
		gameMode = 0;
		speedCamCoeff = 1;
		speedColor = 1;
		scaleFactor = 1f;
		currentArcadeMode = 0;
		currentPreset = 0;
		if(menu) {
            operation = 0;
            currentDeaths = 0;
        }
		lastLen = radiusTmp;
		realLastLen = lastLen;
		x = 1;
		y = 0;
		jumpStep = 1;
		linesGen = 0;

		//TASK VALUES
		taskJumps = preferences.getInteger("taskJumps");
		taskRecordTime =preferences.getInteger("taskRecordTime");
		taskAirTime = preferences.getInteger("taskAirTime");

		//player.changeCharacter(Character.Male);
		ui.refreshSkillsInGame();
		ui.refreshMoney(money);
		ui.refreshLevelData(currentLevel, levelMode);
		ui.setBGSkillsColor(1,1,1);
		ui.setGameModeText("");
		ui.setBestTime(bestTime);


		if(!menu)
			adInterface.loadInterstitialAd();
		adInterface.setBanner(false);

		rebuildCircle(7);
	}
	public int getLevelMode()
	{
		return levelMode;
	}
	public String getLevelName(int index, int levelMode)
	{
		return levelNames[index] + "\n(" + (levelMode == 0 ? (UI.selectedLanguage == Text.EN ? "Normal" : "нормальный") : (UI.selectedLanguage == Text.EN ? "random" : "случайный")) + ")";
	}
	public int getCurrentDeaths()
	{
		return currentDeaths;
	}
	public float getTimer()
	{
		return timer;
	}
	public void changeLevelMode(int mode)
	{
		levelMode = mode;
		preferences.putInteger("level_mode", levelMode);
		preferences.flush();
		ui.refreshLevelData(currentLevel, levelMode);
	}

	public void speedChange()
	{
        lines.get(0).setRadius(lastLen);
		lOp = (int)((45 + MathUtils.random(5, 35))*radiusPl);
		//lnthOp = 0;
        //operation = -1;
		//complexId = 0;
        curLine = lines.get(0);
        currentLine = 0;
        linesGen= 0;
      //  isComplex = false;
        lastLen = lenPlayer-2*radiusPl;
        curLine.radius = lastLen;
		ExpandEffect dir = new ExpandEffect(texturesSpawner[2], 6, centerX, centerY, backGradient.getColor());
		back.addActor(dir);
		dir.addAction(Actions.rotateBy(speedLine*10, 1, Interpolation.fade));
        for(int i =0; i < player.animSprites.length; i++)
		{
			for(int j = 0; j < player.animSprites[i].length; j++)
			{
				player.animSprites[i][j].flip(true, false);
			}
		}
        for(int i = 0; i<bodies.size; i++)
        {
        	if(speedLine >0)
			{
				bodies.get(i).angle -= 360;
			}
			else{
        		bodies.get(i).angle += 360;
			}
            if(bodies.get(i) != null) {
        		/*if(bodies.get(i).line == 1)
					bodies.get(i).getBody().setActive(false);
				if (bodies.get(i).dir > 0) {
					bodies.get(i).getBody().setActive(false);

				}
				else bodies.get(i).getBody().setActive(true);*/
			}
        }
        for(int i = 1; i < lines.size;i++)
		{
			lines.get(i).thisLineAngle -= (speedLine)/Math.abs(speedLine)*(360 + lines.get(i).constAngle);//3*lines.get(i).constAngle;
			//lines.get(i).thisLineAngle = 10000;
		}
		speedLine = -speedLine;
        dirChanging = false;

		flash();
    }
    public void removeMoney(int count)
	{
		if(count > 0)
		{
			money = money - count;
			preferences.putInteger("money", money);
			ui.refreshMoney(this.money);
		}
	}
	public void speedChangeRand()
	{
		lines.get(0).setRadius(lenPlayer);
		lOp = (int)((45 + MathUtils.random(5, 35))*radiusPl);
		lnthOp = 0;
		operation = -1;
		complexId = 0;
		curLine = lines.get(0);
		currentLine = 0;
		linesGen= 0;
		isComplex = false;
		lastLen = lenPlayer-2*radiusPl;
		curLine.radius = lastLen;
		ExpandEffect dir = new ExpandEffect(texturesSpawner[2], 6, centerX, centerY, Color.WHITE);
		back.addActor(dir);
		dir.addAction(Actions.rotateBy(speedLine*10, 1, Interpolation.fade));
		for(int i =0; i < player.animSprites.length; i++)
		{
			for(int j = 0; j < player.animSprites[i].length; j++)
			{
				player.animSprites[i][j].flip(true, false);
			}
		}
		for(int i = 0; i<bodies.size; i++)
		{
			if(speedLine >0)
			{
				bodies.get(i).angle -= 360;
			}
			else{
				bodies.get(i).angle += 360;
			}
			if(bodies.get(i) != null) {
        		if(bodies.get(i).line == 1)
					bodies.get(i).getBody().setActive(false);
				if (bodies.get(i).dir > 0) {
					bodies.get(i).getBody().setActive(false);

				}
				else bodies.get(i).getBody().setActive(true);
			}
		}
		for(int i = 1; i < lines.size;i++)
		{
			//lines.get(i).thisLineAngle -= (speedLine)/Math.abs(speedLine)*(360 + lines.get(i).constAngle);//3*lines.get(i).constAngle;
			lines.get(i).thisLineAngle = 10000;
		}

		speedLine = -speedLine;
		curLine.angle +=speedLine;
		angle += speedLine;
		dirChanging = false;
		flash();
	}
    public void skipTask()
	{
		if(money >= 150) {
			money -= 150;
			taskManager.skipTask();
			preferences.putInteger("money", money);
			preferences.flush();
			ui.refreshMoney(money);
			ui.showMessage(MessageType.SKILL, 6);
		}
		else ui.showMessage(MessageType.QUESTION, 3);

	}
    public void setGameMode(int gm, float arg)
	{
		if(isArcade)
		{
			if(gameMode == 1) {
				preferences.putInteger("taskJetCount", preferences.getInteger("taskJetCount") + 1);
				preferences.putInteger("taskJetTime", preferences.getInteger("taskJetTime") + (int)taskGMTime);
			}else if(gameMode == 0 && player.scaleFactor < 1)
			{
				flash();
				preferences.putInteger("taskMicroCount", preferences.getInteger("taskMicroCount") + 1);
				preferences.putInteger("taskMicroTime", preferences.getInteger("taskMicroTime") + (int)taskGMTime);
			}
		}
		gameMode = gm;
		taskGMTime = 0;
		if(isGame) {
            isChangingColor = true;
            colorIndex = -1;
			jetpack.setVisible(gm == 1);
			jetpackOutline.setVisible(gm == 1);

            isChangingZoom = true;
            if (gm == 0){
                dZoom = 1.1f;
				player.scaleFactor = 1;
            	ui.setGameModeText("");
            }
            else if (gm == 1) {
				dZoom = 0.8f;
				if(!preferences.getBoolean("jetpackHas"))
					ui.showMessage(MessageType.QUESTION, UI.selectedLanguage == Text.EN ? "hold!" : "удерживай!");
				preferences.putBoolean("jetpackHas", true);
				ui.setGameModeText("JETPACK");
			}
            else if (gm == 2)
                dZoom = 1;
            else if(gm == 3)
			{
				gameMode = 0;
				player.scaleFactor = scaleFactor;
				dZoom = 1;
				flash();
				preferences.putBoolean("microHas", true);
				ui.setGameModeText("MICRO");
			}
            else if(gm == 4)
			{
				gameMode =0;
				gravityChange();
				dZoom = 1;
				ui.setGameModeText("");
			}
        }
		preferences.flush();
	}

    public void gravityChange()
    {
        gravity = -gravity;
        if(gravity < 0) {
			player.getBody().getFixtureList().get(1).setUserData("playerHead");
			player.getBody().getFixtureList().get(2).setUserData("pl");
		}
        else {
			player.getBody().getFixtureList().get(1).setUserData("pl");
			player.getBody().getFixtureList().get(2).setUserData("playerHead");
        }
		for(int i =0; i < player.animSprites.length; i++)
		{
			for(int j = 0; j < player.animSprites[i].length; j++)
			{
				player.animSprites[i][j].flip(false, true);
			}
		}
	}

	///
	///PART GENERATING
	///
    void drawLine(float dir)
    {
		lOperation = 2;
		curLine.radius += dir * gravity * genCoeff * lastLen;
    }
	void empty()
	{
		float time = lOp*7/radiusTmp;
		/*if(lnthOp >= time - 11&& lnthOp < time -10)
			curLine.setType(2);
		if(lnthOp >= time - 10&& lnthOp < time -9)
			curLine.setType(0);*/
		if (lnthOp < time)
			lnthOp++;
		else
		{
			operation = 0;
			lnthOp = 0;
			lOperation = 1;
		}
	}
	///
	///COMPLEX GENERATING
	///
	void complexGenerating()
	{
		if(complexId < complexOpList.length) {
			switch (complexOpList[complexId]) {
				case LINE:
					drawLine(complexOpList[complexId+1]*scaleFactor);
					complexId+=2;
					break;
				case EMPTY:
					if(currentLine == 0)
						drawEmptyComplex((float)(complexOpList[complexId+1]*scaleFactor));
					else drawEmptyComplex((float)(complexOpList[complexId+1]));
					break;
				case SPIKE:
					drawSpikeComplex(0.7f);
					break;
				case SPIKE_D:
					drawSpikeComplex(-0.7f);
					break;
				case CUR_LINE:
					if(complexOpList[complexId+1] == 1) {
						currentLine = currentLine+1;
						//curLine = lines.get(lines.size - 1);
						curLine = lines.get(currentLine);
					}
					else if(complexOpList[complexId+1] == -1){
						curLine = lines.get(0);
						currentLine = 0;
					}
					complexId+=2;
					complexGenerating();
					break;
				case N_LINE:
					newLine(lines.get(0).radius+complexOpList[complexId+2]*genCoeff*lastLen*scaleFactor, complexOpList[complexId+1], 0);
					complexGenerating();
					break;
				case ARC:
					arc(complexOpList[complexId+1], complexOpList[complexId+2]);
					break;
				case RADIUS:
					if(complexOpList[complexId+1] == 0) {
						tempRForGen = curLine.radius-1;
						drawLine((-curLine.radius+1) / lastLen / genCoeff);
					}
					else if(complexOpList[complexId+1] == 1)
					{
						drawLine(tempRForGen/lastLen/genCoeff);
						tempRForGen = 0;
					}
					complexId+=2;
					break;
				case GM_CHANGER:
					createGMChanger(complexOpList[complexId+1], 0);
					if(complexOpList[complexId+1] == 3)
					{
						scaleFactor = nextSF;
						nextSF = 0;
					}
					if(complexOpList[complexId+1] == 0 && player.scaleFactor < 1)
					{
						scaleFactor = 1;
						nextSF = 0;
					}
					complexId+=2;
					break;
				case LAST_LEN:
					if(complexOpList[complexId+1] == 1)  {
						lastR = lastLen;
						lastLen =  curLine.radius;
					}
					else if(complexOpList[complexId+1] == -1)
					{
						lastLen =  lastR/radiusPl;
						//lastR = curLine.radius;
					}

					complexId+=2;
					break;
				case HELPER:
					helpers.add(createHelper());
					complexId++;
					complexGenerating();
					break;
				case DIR_CHANGER:
					createDirectionChanger();
					complexId++;
					break;
				case RESET_LINES:
					linesGen = 0;
					complexId++;
					complexGenerating();
					break;
				case LEVEL_END:
					createLevelEnd();
					complexId++;
					break;
				case LETTER:
					createLetter();
					complexId++;
					break;
				case N_LINE_ACT:
					newLine(lines.get(0).radius+complexOpList[complexId+2]*genCoeff*lastLen*scaleFactor, complexOpList[complexId+1], complexOpList[complexId + 3]);
					complexId++;
					complexGenerating();
					break;
				case ROCKET_POS:
					rocketPos = new Vector2((float)Math.cos(Math.toRadians(curLine.angle))*(curLine.radius+(scaleFactor*genCoeff*lastLen))+centerX, (float)Math.sin(Math.toRadians(curLine.angle))*(curLine.radius+scaleFactor*genCoeff*lastLen)+centerY);
					complexId++;
					break;
			}
		}
		else {
		    isComplex = false;
		    complexId = 0;

		    if(genMode == 0)
				lOp = (int)((emptyLength + Math.abs(MathUtils.random(5, 10)))*plRad*2*lastLen*0.8f);
		    else
		    	lOp = (int)(emptyLength*plRad*2*lastLen);

			lnthOp = 0;

			lastLen = lines.get(0).radius;
			realLastLen = lastLen;
			currentLine = 0;
			curLine = lines.get(0);
			linesGen = 0;
		    operation = -1;
		}
	}

	//void createGMChanger(String nameGM){
	void createGMChanger(int gameMode, float arg){
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(1f, 1f);
		Filter filter = new Filter();
		filter.categoryBits = CATEGORY_DESTROYER;
		filter.maskBits = MASK_DESTROYER;
		SceneObject gmChanger = new SceneObject(new Vector2(0,0), physWorld, 1f, 1f);
		gmChanger.createPhysics(shape, BodyDef.BodyType.KinematicBody, 1,0,0, filter);
		gmChanger.getBody().setTransform((float)Math.cos(Math.toRadians(curLine.angle-7))*(curLine.radius+(scaleFactor*genCoeff*lastLen*1.1f))+centerX, (float)Math.sin(Math.toRadians(curLine.angle-7))*(curLine.radius+scaleFactor*genCoeff*lastLen*1.1f)+centerY, (float)Math.toRadians(angle+90 + 3*Math.abs(speedLine)/speedLine));
		gmChanger.getBody().getFixtureList().get(0).setSensor(true);
		if(gameMode == 0)
			gmChanger.createTexture(new Texture(Gdx.files.internal("iconGM0.png")), 4, 4);
		else if(gameMode == 1)
			gmChanger.createTexture(new Texture(Gdx.files.internal("iconGM1.png")), 4, 4);
		else if(gameMode == 3)
			gmChanger.createTexture(new Texture(Gdx.files.internal("iconMICRO.png")), 4, 4);


		gmChanger.sprite.flip(false, true);
		//gmChanger.rotate = true;
		gmChanger.line = currentLine;
		gmChanger.setUserData(new GMChanger(gameMode, arg));
		gmChanger.name = "GMChanger";

		gmChanger.getBody().setUserData(gmChanger);
		bodies.add(gmChanger);
		stage.addActor(gmChanger);
	}

	void createDirectionChanger(){
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(1f, 1f);
		Filter filter = new Filter();
		filter.categoryBits = CATEGORY_DESTROYER;
		filter.maskBits = MASK_DESTROYER;
		SceneObject gmChanger = new SceneObject(new Vector2(0,0), physWorld, 1f, 1f);
		gmChanger.createPhysics(shape, BodyDef.BodyType.KinematicBody, 1,0,0, filter);
		gmChanger.getBody().setTransform((float)Math.cos(Math.toRadians(curLine.angle))*(curLine.radius+(scaleFactor*genCoeff*lastLen*1.2f))+centerX, (float)Math.sin(Math.toRadians(curLine.angle))*(curLine.radius+scaleFactor*genCoeff*lastLen*1.2f)+centerY, (float)Math.toRadians(angle+90 + 3*Math.abs(speedLine)/speedLine));
		gmChanger.getBody().getFixtureList().get(0).setSensor(true);
		gmChanger.createTexture(new Texture(Gdx.files.internal("iconDIR.png")), 4, 4);
		gmChanger.sprite.flip(false, true);
		gmChanger.line = currentLine;
		gmChanger.setUserData("dirCh");
		gmChanger.name = "DirChanger";

		gmChanger.getBody().setUserData(gmChanger);
		bodies.add(gmChanger);
		stage.addActor(gmChanger);
	}
	void createLetter(){
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(1f, 4f);
		Filter filter = new Filter();
		filter.categoryBits = CATEGORY_DESTROYER;
		filter.maskBits = MASK_DESTROYER;
		SceneObject letter = new SceneObject(new Vector2(0,0), physWorld, 1f, 4f);
		letter.createPhysics(shape, BodyDef.BodyType.KinematicBody, 1,0,0, filter);
		letter.getBody().setTransform((float)Math.cos(Math.toRadians(curLine.angle))*(curLine.radius+(scaleFactor*genCoeff*lastLen*1.2f))+centerX, (float)Math.sin(Math.toRadians(curLine.angle))*(curLine.radius+scaleFactor*genCoeff*lastLen*1.2f)+centerY, (float)Math.toRadians(angle+90 + 3*Math.abs(speedLine)/speedLine));
		letter.getBody().getFixtureList().get(0).setSensor(true);
		//letter.createTexture(new Texture(Gdx.files.internal("iconGM0.png")), 4, 4);
//		letter.sprite.flip(false, true);
		letter.line = currentLine;
		letter.setUserData("letter");
		letter.name = "Letter";

		letter.getBody().setUserData(letter);
		bodies.add(letter);
		stage.addActor(letter);
	}
	private ParticleEffectActor createHelper()
	{
		ParticleEffectActor helper = new ParticleEffectActor(new Vector2((float)Math.cos(Math.toRadians(curLine.angle))*(curLine.radius)+centerX, (float)Math.sin(Math.toRadians(curLine.angle))*(curLine.radius)+centerY), physWorld, 1, 1, "jetPart.p");
		helper.setAngle(curLine.angle-180);
		helper.setVisible(false);
		helper.effect.getEmitters().get(0).scaleSize(3);
		stage.addActor(helper);
		return helper;
	}

	private void createLevelEnd()
    {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1f, 1f);
        Filter filter = new Filter();
        filter.categoryBits = CATEGORY_DESTROYER;
        filter.maskBits = MASK_DESTROYER;
        SceneObject gmChanger = new SceneObject(new Vector2(0,0), physWorld, 1f, 1f);
        gmChanger.createPhysics(shape, BodyDef.BodyType.KinematicBody, 1,0,0, filter);
        gmChanger.getBody().setTransform((float)Math.cos(Math.toRadians(curLine.angle-7))*(curLine.radius+(scaleFactor*genCoeff*lastLen*1.2f))+centerX, (float)Math.sin(Math.toRadians(curLine.angle-7))*(curLine.radius+scaleFactor*genCoeff*lastLen*1.2f)+centerY, (float)Math.toRadians(angle+90 + 3*Math.abs(speedLine)/speedLine));
        gmChanger.getBody().getFixtureList().get(0).setSensor(true);
        gmChanger.createTexture(new Texture(Gdx.files.internal("iconGM0.png")), 4, 4);
        gmChanger.sprite.flip(false, true);
        gmChanger.line = currentLine;
        gmChanger.setUserData("levelEnd");
        gmChanger.name = "LevelEnd";

        gmChanger.getBody().setUserData(gmChanger);
        bodies.add(gmChanger);
        stage.addActor(gmChanger);
    }
	public void setPause(boolean pause){
		isPause = pause;
		if (mainMusic != null) {
			if(activeMusic) {
				if (pause)
					mainMusic.pause();
				else mainMusic.play();
			}
		}
	}

	void drawEmptyComplex(float length)
	{
		/*if(lnthOp == length - 6)
			curLine.setType(2);
		if(lnthOp == length - 5)
			curLine.setType(0);*/
        if(lnthOp < length)
			lnthOp = lnthOp + 1;
		else
		{
			lnthOp = 0;
			complexId+=2;
			complexGenerating();
		}
	}

	void arc(float length, float delta)
	{
		if(lnthOp < length) {
			lnthOp++;
			curLine.radius += delta*scaleFactor/10*genCoeff*lastLen;
		}
		else
		{
			lnthOp = 0;
			complexId+=3;
			//lastLen = curLine.radius;
			complexGenerating();
		}
	}
	void newLine(float radius, float constAngle, int action)
	{
		linesGen++;
	    lines.insert(linesGen, new LineRenderer(centerX, centerY, ShapeRenderer.ShapeType.Filled, physWorld, constAngle, angle, true));
		lines.get(linesGen).setRadius(radius+0.3f);
		lines.get(linesGen).angle = angle;
		lines.get(linesGen).setColor(new Color(currentColorEdge.r/255f, currentColorEdge.g/255f, currentColorEdge.b/255f, 1));
		lines.get(linesGen).hasOutline = activeOutline;
		curLine = lines.get(linesGen);
		currentLine = linesGen;
	    complexId+=3;
		lOperation = 2;

		if(action == 1)
		{
			lines.get(linesGen).action = action;
			toAction.add(lines.get(linesGen));
		}

	    lnthOp = 0;
	}
	public void destroyLine(LineRenderer lineRenderer)
	{
		physWorld.destroyBody(lineRenderer.edge.getBody());
//        physWorld.destroyBody(lineRenderer.edgeDown.getBody());
		lineRenderer.positions.clear();
		lines.removeValue(lineRenderer, true);
	}
	void drawSpikeComplex(float dir)
	{
		lOperation = 4;
		if(lnthOp <= 0)
		{
			curLine.setType(1);
			drawLine(scaleFactor*dir);
			destroyerSpawn(0.05f, 0.05f, (float)Math.cos(Math.toRadians(angle + 2*Math.abs(speedLine)/speedLine))*(curLine.radius)+centerX, (float)Math.sin(Math.toRadians(angle + 2*Math.abs(speedLine)/speedLine))*(curLine.radius)+centerY, "obs", 1);
		}
		if(lnthOp < 1) {
			lnthOp = lnthOp + 1;
		}
		else {
			lnthOp = 0;
			complexId++;
			drawLine(-scaleFactor*dir);
			curLine.setType(0);
		}
	}
	///
	///
	///
	void destroyerSpawn(float hx, float hy, float x, float y, Object userData, float dir)
	{
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(hx*genCoeff*lastLen, hy*genCoeff*lastLen);
		//shape.setAsBox(hx*1.25f, hy*1.8f);
		Filter filter = new Filter();
		filter.categoryBits = CATEGORY_DESTROYER;
		filter.maskBits = MASK_DESTROYER;///hx*2
		destroyer = new SceneObject(new Vector2(0,0), physWorld, hx*2, hy);
		destroyer.createPhysics(shape, BodyDef.BodyType.StaticBody, 1,0,0, filter);
		destroyer.getBody().setTransform(x, y, (float)Math.toRadians(angle+90 + 3*Math.abs(speedLine)/speedLine));
		destroyer.dir = dir;
		destroyer.r = (float)Math.sqrt(Math.pow(x-centerX,2) + Math.pow(y-centerY,2));
		destroyer.line = currentLine;
		destroyer.name = "obs";
		destroyer.getBody().setUserData(userData);
		bodies.add(destroyer);
		stage.addActor(destroyer);
		if(dir < 0)
		{
			destroyer.getBody().setActive(false);
		}
	}
	@Override
	public void dispose () {
		physWorld.dispose();
		stage.dispose();
		box2dRen.dispose();
		taskManager.saveData();
	}
}
