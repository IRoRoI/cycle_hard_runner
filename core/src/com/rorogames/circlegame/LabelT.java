package com.rorogames.circlegame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.awt.Font;

public class LabelT extends Label {
    Label shadow;
    public LabelT(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    @Override
    public void setFontScale(float fontScale) {
        super.setFontScale(fontScale);

    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if(shadow != null)
            shadow.setSize(width, height);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(shadow != null)
            shadow.setVisible(visible);
    }

    @Override
    public void setText(CharSequence newText) {
        super.setText(newText);
        if(shadow != null)
            shadow.setText(newText);
    }


    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (shadow != null)
            shadow.setPosition(x, y);
    }

    public Label createShadow(Color color, float offsetX, float offsetY)
    {
        //BitmapFont font = new BitmapFont(getStyle().font.getData().fontFile);
        //LabelStyle labelStyle = new LabelStyle(font, color);
        shadow = new Label(getText(), getStyle());
        shadow.setColor(color);
        shadow.setSize(getWidth(),getHeight());
        shadow.setPosition(getX() + offsetX, getY() + offsetY);
        shadow.setAlignment(getLabelAlign());
        shadow.getStyle().font.getData().setScale(getStyle().font.getData().scaleX,getStyle().font.getData().scaleY);
        return shadow;
    }
}
