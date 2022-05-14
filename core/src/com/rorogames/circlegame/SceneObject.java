package com.rorogames.circlegame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class SceneObject extends GameObject {
    public float angle = 0;
    public int cost = 0;
    public float dir = 0;
    public int line = 0;
    public float hx = 0, hy = 0, r;
    private Object userData;
    public SceneObject(Vector2 pos , World world, float sizeX, float sizeY)
    {
        super(pos, world, sizeX, sizeY);
        hx = sizeX;
        hy = sizeY;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(CircleConstructor.isGame && !CircleConstructor.isPause) {
            if (!(CircleConstructor.main.increaseSpeed | CircleConstructor.main.lowGraphics))
                angle += CircleConstructor.speedLine;
            else angle += CircleConstructor.speedLine*2;
        }
        //System.out.println(angle);
        if(angle > 360 || angle < -360)
        {
            removeThis();
        }
    }
    public void removeThis()
    {
        CircleConstructor.main.bodies.removeValue(this, true);
        if(body != null) {
            body.setActive(false);
            world.destroyBody(body);
        }
        super.remove();
    }
    public void setUserData(Object arg)
    {
        userData = arg;
    }
    public Object getUserData()
    {
        return userData;
    }
}
