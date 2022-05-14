package com.rorogames.circlegame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class LinePoint extends Vector2 implements Disposable {
    public int type;
    public float spCf;
    public Vector2 offset;
    public LinePoint(){}
    public LinePoint(float x, float y, int type)
    {
        super(x,y);
        this.type = type;
    }

    public void set(LinePoint linePoint){
        this.x = linePoint.x;
        this.y = linePoint.y;
        this.type = linePoint.type;
        spCf = linePoint.spCf;
    }

    @Override
    public void dispose() {

    }
}
