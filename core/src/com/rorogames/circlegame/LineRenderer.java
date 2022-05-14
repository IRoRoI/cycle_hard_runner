package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class LineRenderer {
    public Array<LinePoint> positions;
    public Array<LinePoint> begPos;
    public Array<LinePoint> allPos;

    public GameObject edge, edgeDown;
    private LinePoint lv, v;
    public int action;
    public float radius, spikeCoeff, bR, angle, angleInRadians, thisLineAngle, constAngle, _360angle, x, y;
    public boolean rotate, toRemove, isDrawing, hasOutline, hasSpikeOutline, actOn;
    private float width;
    private  int type, g;

    private ShapeRenderer.ShapeType shapeType;
    public ShapeRenderer sr;
    public Color obstacleColor;
    public Vector2 globalPosition;

    ChainShape edgeShape;

    public LineRenderer() {
    }

    public LineRenderer(float x, float y, ShapeRenderer.ShapeType shapeType, World world, float cstAgl, float angle, boolean rotate) {
        edge = null;
        edgeDown = null;
        lv = new LinePoint();
        v = new LinePoint();
        sr = new ShapeRenderer();
        obstacleColor = Color.FIREBRICK;
        width = 0.96f;
        this.shapeType = shapeType;
        this.rotate = rotate;
        constAngle = cstAgl;
        isDrawing = true;
        hasOutline = true;
        hasSpikeOutline = false;
        //constAngle = constAngle*7/CircleConstructor.radiusTmp;

        _360angle = 360 + Math.abs(angle);

        edge = new GameObject(new Vector2(x, y), world, 1, 1);
        edge.createPhysics(new PolygonShape(), BodyDef.BodyType.StaticBody, 1, 0, 0, new Filter());
        edge.getBody().setUserData(this);

        //edgeDown = new GameObject(new Vector2(x, y), world, 1, 1);
        //edgeDown.createPhysics(new PolygonShape(), BodyDef.BodyType.KinematicBody, 1, 0, 0, new Filter());
        //edgeDown.getBody().setUserData(this);

        positions = new Array<LinePoint>();
        begPos = new Array<LinePoint>();
        allPos = new Array<LinePoint>();

        sr.setTransformMatrix(new Matrix4(new Vector3(x, y, 0), new Quaternion(), new Vector3(1, 1, 1)));
        Gdx.gl.glLineWidth(0.1f);
    }

    public void setColor(Color color) {
        sr.setColor(color);
       // obstacleColor.set(color.r*1.3f, color.g * 1.3f, color.b * 1.3f, 1);
    }
    public Color getColor() { return sr.getColor(); }

    public void updateLine() {
        if (!toRemove) {
            angle = CircleConstructor.main.getAngle();
            angleInRadians = (float) Math.toRadians(angle);
            x = (float) Math.cos(angleInRadians);
            y = (float) Math.sin(angleInRadians);

            Filter filter = new Filter();
            LinePoint position = new LinePoint(x * radius, y * radius, type);

            if (CircleConstructor.speedLine < 0) {
                if (!rotate) {
                    positions.removeIndex(0);
                    if(g == 8) {
                        position.type = 2;
                        g =-1;
                    }
                    g++;
                    positions.add(position);

                } else {
                    if (Math.abs(thisLineAngle) < Math.abs(constAngle)) {
                        if(globalPosition == null)
                            globalPosition = new Vector2(position);
                        positions.add(position);
                        begPos.insert(0, new LinePoint(x * bR, y * bR, 0));
                    }
                    if (positions.size > 0 && Math.abs(thisLineAngle) > 360) {
                        positions.removeIndex(0);
                        begPos.removeIndex(begPos.size - 1);
                    }
                }
            } else {
                if (!rotate) {
                    positions.removeIndex(positions.size - 1);
                    positions.insert(0, position);
                    if(g == 8) {
                        position.type = 2;
                        g =-1;
                    }
                    g++;
                } else {
                    if (Math.abs(thisLineAngle) < Math.abs(constAngle)) {
                        positions.insert(0, position);
                        begPos.add(new LinePoint(x * bR, y * bR, 0));
                    }
                    if (positions.size > 0 && Math.abs(thisLineAngle) > 360) {
                        positions.removeIndex(positions.size - 1);
                        begPos.removeIndex(0);
                    }
                }
            }

            thisLineAngle += CircleConstructor.speedLine;
            if (positions.size > 0) {
                //  thisLineAngle += CircleConstructor.speedLine;
                filter.categoryBits = CircleConstructor.CATEGORY_CIRCLE;
                filter.maskBits = CircleConstructor.MASK_CIRCLE;
                // Fixture fixture = edge.getBody().getFixtureList().get(0);
                edge.getBody().destroyFixture(edge.getBody().getFixtureList().get(0));
                edgeShape = new ChainShape();

                if (!rotate) {
                    edgeShape.createChain((Vector2[]) positions.toArray(Vector2.class));

                } else {
                    // allPos.addAll(positions);
                    //allPos.addAll(begPos);
                    for (int i = 0; i < positions.size; i++) {
                        if (i % 3 == 0)
                            allPos.add(positions.get(i));
                    }
                    for (int i = 0; i < begPos.size; i++) {
                        if (i % 6 == 0)
                            allPos.add(begPos.get(i));
                    }

                    edgeShape.createChain((Vector2[]) allPos.toArray(Vector2.class));
                    allPos.clear();
                }
                // if(!rotate)
                edge.createFixture(edgeShape, filter);
                edgeShape.dispose();
            }
        }
        if (positions.size <= 0) {
            CircleConstructor.main.linesToDestroy.add(this);
            System.out.println("added to removeLine");
            toRemove = true;
            isDrawing = false;
            return;
        }
    }
    public void setGlobalPosition(Vector2 pos)
    {
        globalPosition = new Vector2(pos);
        for(int i =0; i < positions.size; i++)
        {
            positions.get(i).offset = new Vector2(globalPosition.x -  positions.get(i).x, globalPosition.y -  positions.get(i).y);
            begPos.get(i).offset = new Vector2(globalPosition.x - begPos.get(i).x,globalPosition.y - begPos.get(i).y);
        }
    }
    public void act()
    {
        if(actOn && !toRemove) {
            if(action == 1){
                for (int i = 0; i < positions.size; i++) {
                    if(positions.get(i).offset != null && begPos.get(i).offset != null) {
                        positions.get(i).x = globalPosition.x - positions.get(i).offset.x;
                        positions.get(i).y = globalPosition.y - positions.get(i).offset.y;
                        begPos.get(i).x = globalPosition.x - begPos.get(i).offset.x;
                        begPos.get(i).y = globalPosition.y - begPos.get(i).offset.y;
                    }
                }
            }
        }
    }
    int b = 0;
    public void draw() {
        if(isDrawing) {
           // outline.begin(ShapeRenderer.ShapeType.Line);
            sr.begin(shapeType);
            if (!rotate) {
                lv.set(positions.get(0));
                int kSpike = 0;
                for (int i = 1; i < positions.size; i++) {
                    v.set(positions.get(i));
                    sr.triangle(lv.x, lv.y, v.x, v.y, 0, 0);
                    Color color = new Color(sr.getColor().r+0.05f,sr.getColor().g+0.05f,sr.getColor().b+0.05f, 1);
                    Color color2 = new Color(color.r+0.05f,color.g+0.05f,color.b+0.05f, 1);
                    sr.triangle(lv.x*0.75f, lv.y*0.75f, v.x*0.75f, v.y*0.75f, 0, 0, sr.getColor(), sr.getColor(), color);
                    sr.triangle(lv.x*0.25f, lv.y*0.25f, v.x*0.25f, v.y*0.25f, 0, 0, color, color, color2);
                    if(hasSpikeOutline)
                    if(lv.type == 1 || v.type == 1) {
                        if(kSpike == 0) {
                            sr.triangle(lv.x, lv.y, v.x, v.y, v.x * 0.9f, v.y * 0.9f, obstacleColor, obstacleColor, obstacleColor);
                        }
                        if(kSpike > 0) {
                            sr.triangle(lv.x * 0.9f, lv.y * 0.9f, v.x, v.y, lv.x, lv.y, obstacleColor, obstacleColor, obstacleColor);
                            kSpike = -1;
                        }

                        kSpike++;
                    }
                    if(hasOutline) {
                        sr.rectLine(lv.x, lv.y, v.x, v.y, 0.1f, Color.WHITE, Color.WHITE);
                        sr.rectLine(lv.x*0.95f, lv.y*0.95f, v.x*0.95f, v.y*0.95f, 0.05f, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
                        if(lv.type == 2)
                            sr.rectLine(lv.x, lv.y, lv.x*0.95f, lv.y*0.95f, 0.2f, Color.WHITE, Color.LIGHT_GRAY);
                    }
                    lv.set(positions.get(i));
                }
            } else {
                lv.set(positions.get(0));
                int begIterator = positions.size-1;
                int kSpike = 0;
                for (int i = 1; i < positions.size; i++) {
                    v.set(positions.get(i));
                    sr.triangle(lv.x, lv.y, v.x, v.y, begPos.get(begIterator).x, begPos.get(begIterator).y);
                    sr.triangle(begPos.get(begIterator - 1).x, begPos.get(begIterator-1).y, v.x, v.y, begPos.get(begIterator).x, begPos.get(begIterator).y);
                    if(hasSpikeOutline)
                    if(lv.type == 1 || v.type == 1) {
                        if(kSpike == 0) {
                            sr.triangle(lv.x, lv.y, v.x, v.y, v.x * 0.9f, v.y * 0.9f, obstacleColor, obstacleColor, obstacleColor);
                        }
                        if(kSpike > 0) {
                            sr.triangle(lv.x * 0.9f, lv.y * 0.9f, v.x, v.y, lv.x, lv.y, obstacleColor, obstacleColor, obstacleColor);
                            kSpike = -1;
                        }

                        kSpike++;
                    }
                     if(hasOutline) {
                            sr.rectLine(lv.x, lv.y, v.x, v.y, 0.13f, Color.WHITE, Color.WHITE);
                            sr.rectLine(begPos.get(i - 1).x, begPos.get(i - 1).y, begPos.get(i).x, begPos.get(i).y, 0.1f, Color.WHITE, Color.WHITE);
                     }

                    begIterator--;
                    lv.set(positions.get(i));
                }
                if(hasOutline) {
                    sr.rectLine(begPos.get(0).x, begPos.get(0).y, positions.get(positions.size - 1).x, positions.get(positions.size - 1).y, 0.1f, Color.WHITE, Color.WHITE);
                    sr.rectLine(begPos.get(begPos.size - 1).x, begPos.get(begPos.size - 1).y, positions.get(0).x, positions.get(0).y, 0.08f, Color.WHITE, Color.WHITE);
                }
            }
            if(b > 0)
                b = -1;
            b++;
            sr.end();
          //  outline.end();
        }
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
        bR = radius*width;
    }
    public void setType(int t)
    {
        type = t;
    }
    void setProjection(OrthographicCamera camera)
    {
        sr.setProjectionMatrix(camera.combined);
    }
}
