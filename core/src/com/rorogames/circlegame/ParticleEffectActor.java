package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends SceneObject {
    ParticleEffect effect;
    public Vector2 offset;
    boolean complete;

    public ParticleEffectActor(Vector2 pos, World world, float sizeX, float sizeY, String path) {
        super(pos, world, sizeX, sizeY);
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal(path), Gdx.files.internal("particles"));
        effect.setPosition(pos.x, pos.y);
        effect.start();
    }

    @Override
    public void removeThis() {
        super.removeThis();
        effect.dispose();
        CircleConstructor.removeHelper(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        effect.setPosition(getX(), getY());
        effect.update(Gdx.graphics.getDeltaTime());
        effect.draw(batch);
        if(effect.isComplete()) {
            if(!complete)
            effect.reset();
            else {setVisible(false); }
        }
    }
    public void setAngle(float angle)
    {
        for(int i =0; i < effect.getEmitters().size; i++)
        {
            effect.getEmitters().get(i).getAngle().setLow(angle);
            effect.getEmitters().get(i).getAngle().setHigh(angle);
        }
    }
}
