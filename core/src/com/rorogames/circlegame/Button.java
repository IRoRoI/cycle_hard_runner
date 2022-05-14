package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class Button extends Group {

    public LabelT text;
    private Label shadow;
    public Image image;
    public Image[] images;
    public int userData;
    public Button(float x, float y, float WIDTH, float HEIGHT, float fontHeight, String pathIcnBtn)
    {
        this.setPosition(x,y);
        this.setSize(WIDTH, HEIGHT);

        if(!pathIcnBtn.equals("")) {
            Texture texture = new Texture(Gdx.files.internal(pathIcnBtn));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Linear);
            image = new Image(texture);
            image.setSize(WIDTH, HEIGHT);
            image.setColor(Color.WHITE);
            image.setPosition(0, 0);
            image.setVisible(true);

            this.addActor(image);
        }
        if(fontHeight != 0) {
            BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
            font.getData().setScale(fontHeight * 0.04f);
            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
            text = new LabelT("", labelStyle);
            text.setAlignment(Align.center);
            text.setPosition(0, 0);

            shadow = text.createShadow(Color.RED, 0, 0);
            shadow.setVisible(false);
            this.addActor(shadow);
            this.addActor(text);
        }
    }
    public void setShadow(Color color, float x, float y)
    {
        shadow.setVisible(true);
        shadow.setColor(color);
        shadow.setPosition(text.getX() + x, text.getY() + y);
    }
    public void setText(String text, Color color)
    {
        this.text.setText(text);
        this.text.setColor(color);
        this.text.setPosition((getWidth()-this.text.getWidth())/2, (getHeight()-this.text.getHeight())/2);
    }
    public void setColor(Color color)
    {
        image.setColor(color);
    }
    public void setAlpha(float a)
    {
        image.setColor(image.getColor().r, image.getColor().g, image.getColor().b, a);
    }

}
