package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Character extends GameObject {
    private CircleConstructor circleConstructor;
    private Sprite outline;
    static int Male = 0, Thief = 1, Builder = 2, Girl = 3, Wizard = 4;
    private int type = 0;
    private String[] pathTextures = {"male_base", "girl", "thief", "wizard", "builder", "robot"};

    @Override
    public boolean animate(int idAnim, float speedAnim) {
        boolean result = false;
        if(accelerating) {
            sprite = animSprites[0][7];
            outlineSprite.set(sprite);
            outlineSprite.setSize(sprite.getWidth()*1.07f, sprite.getHeight()*1.07f);
            outlineSprite.setRotation(sprite.getRotation());
            outlineSprite.setOrigin(outlineSprite.getWidth()/2, outlineSprite.getHeight()/2);
            //  outlineSprite.setScale(sprite.getScaleX()*1.05f, sprite.getScaleY()*1.05f);
            outlineSprite.setColor(Color.WHITE);
            result = false;
        }
        else result = super.animate(idAnim, speedAnim);
        return result;

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    static String[] descriptionEN = {
            "default skin\ncharacter",
            "has a slowdown",
            "+30 % money after\ngame",
            "has flying",
            "can broke all\ngenerations",
            "no damage from spikes"
    };
    static String[] descriptionRU = {
            "без дополнительных\nнавыков",
            "имеет замедление",
            "+30 % атомов после\nпроигрыша",
            "имеет полет",
            "может разрушить все\nгенерации на круге",
            "нет урона от шипов"
    };
    static int[] costSkins = {
            0, 7500, 5000, 10000, 15000, 5000
    };
    static int[] reqLevels = {
            0, 0, 0, 1, 2, 1
    };
    static int[] reqLevelModes = {
            0, 0, 1, 0, 1, 1
    };
    private float[][] scalesX = {
            {0.95f, 0.9f, 0.6f, 0.45f},
            {0.95f, 0.9f, 0.6f, 0.45f},
            {0.95f, 0.9f, 0.6f, 0.45f},
            {0.45f, 0.45f, 0.5f, 0.5f},
            {0.95f, 0.9f, 0.6f, 0.45f},
            {0.95f, 0.9f, 0.6f, 0.45f}
    };
    private float[][] scalesY = {
            {1, 1, 1, 1},
            {1, 1, 1, 1},
            {1, 1, 1, 1},
            {0.8f, 0.8f, 0.8f, 0.8f},
            {1, 1, 1, 1},
            {1, 1, 1, 1}
    };
    public Integer[][] nCommands = {
            {},
            {},
            {},
            {},
            {},
            {}
    };
    public float scaleFactor = 1;
    public Vector2[] jetPoses = {
            new Vector2(0.3f, -0.2f),
            new Vector2(0.1f, -0.2f),
            new Vector2(0.4f, -0.2f),
            new Vector2(0.3f, 2f),
            new Vector2(0.3f, -0.2f),
            new Vector2(0.3f, -0.2f)
    };
    public Vector2[] jetPoses2 = {
            new Vector2(0.25f, -0.2f),
            new Vector2(0.3f, -0.2f),
            new Vector2(0.25f, -0.2f),
            new Vector2(0f, 0.3f),
            new Vector2(0.3f, -0.2f),
            new Vector2(0.3f, -0.2f)
    };
    public Vector2[] jetPosesFly = {
            new Vector2(0.4f, -0.2f),
            new Vector2(0.6f, -0.2f),
            new Vector2(0.6f, -0.1f),
            new Vector2(0f, 0.5f),
            new Vector2(0.6f, -0.2f),
            new Vector2(0.6f, -0.2f)
    };
    public Vector2[] jetPosesFly2 = {
            new Vector2(0.1f, -0.2f),
            new Vector2(0.1f, -0.2f),
            new Vector2(0.1f, -0.1f),
            new Vector2(0f, 0.5f),
            new Vector2(0.1f, -0.2f),
            new Vector2(0.1f, -0.2f)
    };
    public boolean accelerating, slowing;
    private Skill[] nSkill;

    public Character(Vector2 pos, World world, float sizeX, float sizeY, CircleConstructor circleConstructor) {
        super(pos, world, sizeX, sizeY);
        this.type = circleConstructor.preferences.getInteger("equippedSkin");
        this.circleConstructor = circleConstructor;
        animSprites = new Sprite[4][0];
        scaleFactor = 1;

        createSkills();
        changeCharacter(circleConstructor.preferences.getInteger("equippedSkin"));
    }

    public void loadTextures() {
        loadPlayerTextures(0, 14, scalesX[type][0], scalesY[type][0], pathTextures[type] + "/run");
        loadPlayerTextures(1, 7,  scalesX[type][1], scalesY[type][1], pathTextures[type] + "/jump");
        loadPlayerTextures(2, 1,  scalesX[type][2], scalesY[type][2], pathTextures[type] + "/fly");
        loadPlayerTextures(3, 1,  scalesX[type][3], scalesY[type][3], pathTextures[type] + "/fall");
    }
    public void loadPlayerTextures(int id, int countSprites, float scaleX, float scaleY, String pathTextures)
    {
        this.animSprites[id] = new Sprite[countSprites];
        for(int i = 0; i < countSprites; i++)
        {
            this.animSprites[id][i] = new Sprite(new Texture(Gdx.files.internal(pathTextures + "/" + (i+1) + ".png")));
            //this.animSprites[id][i].setFlip(true, false);
            this.animSprites[id][i].setSize(1.8f, 2f);
            this.animSprites[id][i].setScale(scaleX, scaleY);
        }
    }
    public void disposeTextures()
    {
        for(int i = 0; i < this.animSprites.length; i++)
        {
            for(int j = 0; j < this.animSprites[i].length; j++)
            {
                this.animSprites[i][j].getTexture().dispose();
            }
        }
    }

    public Integer[] getIndCmds()
    {
        return nCommands[type];
    }
    public void createSkills()
    {
        nSkill = new Skill[]{
                null,
                Skill.newSkill("skillsIcons/sldBtn.png", "slowdown", 1, circleConstructor),
                Skill.newSkill("not", "thief", 1, circleConstructor),
                Skill.newSkill("skillsIcons/jetBtn.png", "jet", 0.3f, circleConstructor),
                Skill.newSkill("skillsIcons/buildBtn.png", "builder", 1, circleConstructor),
                Skill.newSkill("not", "noSpikeDamage", 1, circleConstructor)
        };
        nSkill[3].kRefill = 4;
        nSkill[1].onlyArcade = true;
        nSkill[4].onlyArcade = true;
        nSkill[4].kRefill = 6;
    }
    public Vector2 getJetPos()
    {
        return (CircleConstructor.speedLine < 0 ? jetPoses[type] : jetPoses2[type]);
    }
    public Vector2 getJetPosFly()
    {
        return CircleConstructor.speedLine < 0 ? jetPosesFly[type] : jetPosesFly2[type];
    }
    public void changeCharacter(int type)
    {
        circleConstructor.skillsActive.removeValue(nSkill[this.type], true);

        this.type = type;
        disposeTextures();
        loadTextures();

        if(nSkill[type] != null)
            circleConstructor.skillsActive.add(nSkill[type]);
    }
}
