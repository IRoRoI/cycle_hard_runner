package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ExpandEffect extends Group {
    private float speed;
    private Image image;
    public ExpandEffect(Texture texture, float speed, float x, float y, Color color)
    {
        this.speed = speed;
        this.setPosition(x, y);
        image = new Image(texture);
        image.setSize(10,10);
        image.setPosition(-image.getWidth()/2, -image.getHeight()/2);
        image.setColor(color);
        this.addActor(image);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        setScale(getScaleX() + Gdx.graphics.getDeltaTime()*speed);
        if(getColor().a > 0)
            setColor(getColor().r, getColor().g, getColor().b, getColor().a - Gdx.graphics.getDeltaTime()*speed*0.07f);
        if(getScaleX() > 15)
        {
            image.clear();
            clear();
            remove();
        }
    }
}
