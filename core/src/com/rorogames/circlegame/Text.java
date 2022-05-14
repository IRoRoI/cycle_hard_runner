package com.rorogames.circlegame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class Text extends Group {
    public static int EN = 0;
    public static int RU = 1;
    public Array<String> text;
    private Label textLabel;
    private Label.LabelStyle labelStyle;

    public Text(float x, float y, float widthOfElement, float heightOfElement, BitmapFont font)
    {
        text = new Array<String>();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        textLabel = new Label("", labelStyle);

        textLabel.setSize(widthOfElement, heightOfElement);
        textLabel.setPosition(x,y);
        textLabel.setFontScale(heightOfElement*0.04f);
        this.addActor(textLabel);
    }
    public void setAlign(int align)
    {
        textLabel.setAlignment(align);
    }
    public void addText(String text)
    {
        this.text.add(text);
    }
    public void changeText(int selectedLanguage)
    {
        textLabel.setText(text.get(selectedLanguage));
    }
}
