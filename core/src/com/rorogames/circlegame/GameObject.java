package com.rorogames.circlegame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.sun.management.VMOption;

import java.rmi.ConnectIOException;

public class GameObject extends Group {
    protected Texture tex;
    protected Body body;
    protected World world;
    protected OrthographicCamera camera;
    public Sprite sprite, outlineSprite;
    public Sprite[][] animSprites;
    public String name = "name";

    private float kAnim;
    private int currentAnim;
    public float spriteAngle;

    public boolean rotate, isPhysObject=false, toRemove, hasOutline;

    public GameObject(Vector2 pos , World world, float sizeX, float sizeY) {
        setPosition(pos.x, pos.y);
        setSize(sizeX, sizeY);
        setOrigin(sizeX/2, sizeY/2);

        this.world = world;
        body = null;
        name = "name";
    }
    public void createPhysics(Shape shape, BodyDef.BodyType type, float density, float restitution, float friction, Filter filter)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(getX(), getY());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.shape = shape;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        body = world.createBody(bodyDef);
        Fixture fixture = body.createFixture(fixtureDef);
        setFilter(filter);

        isPhysObject = true;
    }
    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        if(isPhysObject)
            setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        if(!rotate && isPhysObject)
            setRotation((float) body.getTransform().getRotation()*MathUtils.radiansToDegrees);
        if(sprite != null)
        {
            sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
            sprite.setRotation(getRotation()+spriteAngle);
            if(isPhysObject) {
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
                if(hasOutline)
                {
                    outlineSprite.setRotation(sprite.getRotation());
                    outlineSprite.setOrigin(outlineSprite.getWidth()/2, outlineSprite.getHeight()/2);
                    outlineSprite.setPosition(body.getPosition().x - outlineSprite.getWidth() / 2, body.getPosition().y - outlineSprite.getHeight() / 2);
                }
            }
            if(!toRemove) {
                if(hasOutline)
                    outlineSprite.draw(batch);
                sprite.draw(batch);
            }
        }
    }
    public boolean animate(int idAnim, float speedAnim)
    {
        //System.out.println(currentAnim);
        boolean result = true;

        kAnim += speedAnim;
        currentAnim = (int)kAnim;

        if(currentAnim >= animSprites[idAnim].length) {
            currentAnim = 0;
            kAnim = 0;
            result = false;
        }
        else {
            sprite = animSprites[idAnim][currentAnim];
            if(hasOutline)
            {
                outlineSprite.set(sprite);
                outlineSprite.setSize(sprite.getWidth()*1.07f, sprite.getHeight()*1.07f);
                outlineSprite.setRotation(sprite.getRotation());
                outlineSprite.setOrigin(outlineSprite.getWidth()/2, outlineSprite.getHeight()/2);
              //  outlineSprite.setScale(sprite.getScaleX()*1.05f, sprite.getScaleY()*1.05f);
                outlineSprite.setColor(Color.WHITE);
            }
        }
        return result;
    }
    public void cleanAnim()
    {
        currentAnim = 0;
        kAnim = 0;
    }


    public void createTexture(Texture tex, float sizeX, float sizeY)
    {
        this.tex = tex;
        sprite = new Sprite(tex);
        sprite.setSize(sizeX, sizeY);
        sprite.setOrigin(sizeX/2, sizeY/2);
    }

    public Body getBody() {
        return body;
    }
    public void createFixture(Shape shape, Filter filter)
    {
        body.createFixture(shape, 0);
    }
    public void createOutline()
    {
        outlineSprite = new Sprite();
        outlineSprite.setSize(1.8f, 2);

        hasOutline = true;
    }
    public void setFilter(Filter filter)
    {
        body.getFixtureList().get(0).setFilterData(filter);
    }
    public void setCamera(OrthographicCamera camera)
    {
        this.camera = camera;
    }
}
