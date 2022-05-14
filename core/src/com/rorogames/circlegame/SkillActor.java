package com.rorogames.circlegame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SkillActor extends Group {
    Skill skill;
    CircleConstructor circleConstructor;
    Image status, image;

    boolean touched;

    @Override
    public void act(float delta) {
        super.act(delta);
        skill.touched(touched);
    }

    public SkillActor(float x, float y, float sizeX, float sizeY, Image img, final Skill skill, CircleConstructor circleConstructor){
        setPosition(x, y);
        setSize(sizeX, sizeY);

        image = img;
        status = new Image(new Texture("quad.png"));
        status.setPosition(0 ,0);
        status.setSize(0, sizeY);
        status.setColor(0,0,0, 0.5f);

        Actor listener = new Actor();
        listener.setSize(sizeX, sizeY);
        listener.setTouchable(((skill.onlyArcade & circleConstructor.isArcade) | !skill.onlyArcade | (!circleConstructor.isArcade) & circleConstructor.getLevelMode() == 1) ? Touchable.enabled : Touchable.disabled);
        listener.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                touched = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                touched = false;
                skill.reset();
                super.touchUp(event, x, y, pointer, button);
            }
        });

        this.addActor(img);
        this.addActor(status);
        this.addActor(listener);
        this.circleConstructor = circleConstructor;
        this.skill = skill;
    }
}
