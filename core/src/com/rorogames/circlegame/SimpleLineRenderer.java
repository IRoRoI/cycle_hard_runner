package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

public class SimpleLineRenderer {
    public static final int MOUNTAIN = 0;
    public static final int WAVE = 1;
    public static final int CITY = 2;

    public List<Vector2> positions;
    private Vector2 lv, v;

    private float[] dirs = {3, 2, 6, 4, 8};
    public float radius, rstRadius, angle, angleInRadians, x, y, a, kRad;
    public int TYPE;
    private float timer, timerCMD, rstTime, toR, speedExpand, speedImpulse;
    private int dir = 1, cityStage = 1;
    private boolean isImpulse, isActive, isExpand;
    private ShapeRenderer.ShapeType shapeType;
    private ShapeRenderer sr;

    public SimpleLineRenderer()
    {}
    public SimpleLineRenderer(float x, float y, ShapeRenderer.ShapeType shapeType, float radius)
    {
        lv = new Vector2();
        v = new Vector2();
        sr = new ShapeRenderer();
        kRad = 1;
        this.shapeType = shapeType;
        this.radius = radius;
        positions = new ArrayList<Vector2>();
        sr.setTransformMatrix(new Matrix4(new Vector3(x,y,0), new Quaternion(),new Vector3(1,1,1)));
    }
    public void setColor(Color color)
    {
        sr.setColor(color);
    }
    public void updateLine(Color colorBack, Color colorEdge)
    {
        angle += CircleConstructor.speedLine;
        if(isActive) {
            a += 15;
            angleInRadians = (float) Math.toRadians(angle);
            x = (float) Math.cos(angleInRadians);
            y = (float) Math.sin(angleInRadians);
            float curRadius = 0;
            switch (TYPE)
            {
                case MOUNTAIN:
                    curRadius = radius + (float) (Math.sin(Math.toRadians(a))) + MathUtils.random(-3f, 3f) * (timer % 3 == 0 ? 1 : 0);
                    break;
                case WAVE:
                    curRadius = radius + (float) (Math.sin(Math.toRadians(a)))*2;
                    break;
                case CITY:
                    if(timer > timerCMD) {
                        radius = radius + cityStage * (dirs[MathUtils.random(0, dirs.length-1)]);
                        if(radius > 28)
                            radius = 20;
                        if(radius < 14)
                            radius = 20;
                        timerCMD += 4;
                        cityStage = -cityStage;
                    }
                    curRadius = radius;
                    break;
            }

            Vector2 position = new Vector2(x * curRadius, y * curRadius);
            timer += 1;
            if (isImpulse) {
                kRad += dir * speedImpulse;
                if (timer > rstTime) {
                    dir = -dir;
                    timer = 0;
                }
            }
            if(isExpand) {
                kRad = MathUtils.lerp(kRad, toR, speedExpand * Gdx.graphics.getDeltaTime());
                if(Math.abs(kRad - toR) < 0.02f) {
                    isExpand = false;
                }
            }

            if (CircleConstructor.speedLine < 0) {
                positions.remove(0);
                positions.add(position);

            }
        }
    }
    public void updateLine(Color colorBack, Color colorEdge, float k)
    {
        angle += CircleConstructor.speedLine;
        if(isActive) {
            a += 15;
            angleInRadians = (float) Math.toRadians(angle);
            x = (float) Math.cos(angleInRadians);
            y = (float) Math.sin(angleInRadians);
            float curRadius = 0;
            switch (TYPE)
            {
                case MOUNTAIN:
                    curRadius = radius + (float) (Math.sin(Math.toRadians(a))) + MathUtils.random(-3f, 3f) * (timer % 3 == 0 ? 1 : 0);
                    break;
                case WAVE:
                    curRadius = radius + (float) (Math.sin(Math.toRadians(a)))*2;
                    break;
                case CITY:
                    if(timer > timerCMD) {
                        radius = radius + cityStage * (dirs[MathUtils.random(0, dirs.length-1)]);
                        if(radius > 28)
                            radius = 20;
                        if(radius < 14)
                            radius = 20;
                        timerCMD += 4*k;
                        cityStage = -cityStage;
                    }
                    curRadius = radius;
                    break;
            }

            Vector2 position = new Vector2(x * curRadius, y * curRadius);
            timer += 1*k;
            if (isImpulse) {
                kRad += dir * speedImpulse;
                if (timer > rstTime) {
                    dir = -dir;
                    timer = 0;
                }
            }
            if(isExpand) {
                kRad = MathUtils.lerp(kRad, toR, speedExpand * Gdx.graphics.getDeltaTime());
                if(Math.abs(kRad - toR) < 0.02f) {
                    isExpand = false;
                }
            }

            if (CircleConstructor.speedLine < 0) {
                positions.remove(0);
                positions.add(position);

            }
        }
    }
    public void draw(Color colorBack, Color colorEdge)
    {
        if(isActive) {
            if (positions.size() > 0) {
                sr.begin(shapeType);
                lv.set(positions.get(0));
                for (int i = 1; i < positions.size(); i++) {
                    v.set(positions.get(i));
                    sr.triangle(lv.x * kRad, lv.y * kRad, v.x * kRad, v.y * kRad, 0, 0, colorBack, colorBack, colorEdge);
                    sr.rectLine(lv.x * kRad, lv.y * kRad, v.x* kRad, v.y * kRad, 0.1f, Color.GRAY, Color.GRAY);
                    lv.set(positions.get(i));
                }
                sr.end();
            }
        }
    }
    void setProjection(OrthographicCamera camera)
    {
        sr.setProjectionMatrix(camera.combined);
    }
    public void setImpulse(boolean impulse, int dir, float time, float speedImpulse)
    {
        timer = 0;
        rstTime = time;
        this.dir = dir;
        this.speedImpulse = speedImpulse;
        isImpulse = impulse;
        isExpand = false;
    }
    public void setActive(boolean active, int type)
    {
        isActive = active;
        setTYPE(type);

        float k = 1f/180f;
        for(int i = 0; i < 180; i ++)
        {
            if(type != MOUNTAIN)
                updateLine(Color.WHITE, Color.WHITE, k);
            else updateLine(Color.WHITE, Color.WHITE);
        }
    }
    public void setActive(boolean active)
    {
        isActive = active;
    }
    public void setExpand(boolean expand, float fromR, float toR, float speedExpand)
    {
        timerCMD = 0;
        isExpand = expand;
        kRad = fromR;
        this.toR = toR;
        this.speedExpand = speedExpand;
    }
    public void setTYPE(int TYPE)
    {
        this.TYPE = TYPE;
    }
}
