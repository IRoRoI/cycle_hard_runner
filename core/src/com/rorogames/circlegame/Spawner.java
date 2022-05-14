package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class Spawner extends Group {
    private Array<Group> particles;
    private Texture mainTexture;
    private float minSize, maxSize, timer, time;
    public Spawner(Texture texture, float minSize, float maxSize, float time)
    {
        super();
        particles = new Array<Group>();
        mainTexture = texture;
        this.time = time;
        setSize(minSize, maxSize);
    }
    public void update(Color color)
    {
        timer += Gdx.graphics.getDeltaTime();
        for(int i = 0; i < particles.size; i++)
        {
            particles.get(i).setRotation(particles.get(i).getRotation() + 1);
            particles.get(i).setScale(particles.get(i).getScaleX() + 0.03f);
            particles.get(i).getChildren().get(0).setColor(color.r, color.g, color.b, particles.get(i).getChildren().get(0).getColor().a - 0.01f);
            if(particles.get(i).getChildren().get(0).getColor().a <= 0)
            {
                particles.get(i).clear();
                this.removeActor(particles.get(i));
                particles.removeValue(particles.get(i), true);
            }
        }

        if(timer > time)
        {
            particles.add(createParticle(color));
            timer = 0;
        }
    }
    private Group createParticle(Color color)
    {
       /* float newSize = MathUtils.random(minSize, maxSize);
        Group nP = new Group();
        Image newPart = new Image(mainTexture);
        newPart.setSize(newSize, newSize);
        newPart.setPosition(-newSize/2f, -newSize/2f);
        newPart.setColor(color);
        nP.setPosition(MathUtils.random(0, Gdx.graphics.getWidth()), MathUtils.random(0, Gdx.graphics.getHeight()));
        nP.addActor(newPart);
        this.addActor(nP);
        return nP;*/
       float newSize = MathUtils.random(minSize, maxSize);
       float rndAngle = MathUtils.degRad*MathUtils.random(0, 360);
       float rndRad = MathUtils.random(16, 40);
        Group nP = new Group();
        Image newPart = new Image(mainTexture);
        newPart.setSize(newSize, newSize);
        newPart.setPosition(-newSize/2f, -newSize/2f);
        newPart.setColor(color);
        nP.setPosition(MathUtils.cos(rndAngle)*rndRad+CircleConstructor.centerX, MathUtils.sin(rndAngle)*rndRad+CircleConstructor.centerY);
        nP.addActor(newPart);
        this.addActor(nP);
        return nP;
    }
    public void destroyAll()
    {
        for(int i =0; i < particles.size;i++)
        {
            particles.get(i).clear();
            this.removeActor(particles.get(i));
        }
        particles.clear();
    }
    public void setSize(float min, float max)
    {
        minSize = min;
        maxSize = max;
    }
}
