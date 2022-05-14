package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class StepSkillPan extends Group {
    private String namePan;
    private String[] texts;
    public Skill skill;

    private CircleConstructor circleConstructor;
    private Button mainBtn;
    private LabelT namePanLabel, costLabel;
    private Image back;
    private int skillLevel, maxSkillLevel, cost, minCost;
    private Image[] levelImages;
    private float[] values;

    public StepSkillPan(Skill skill, String namePan, int fontSize, CircleConstructor circleConstructor, float x, float y, float WIDTH, float HEIGHT, int minCost, final int maxSkillLevel)
    {
        this.skill = skill;
        this.namePan = namePan;
        this.circleConstructor = circleConstructor;
        this.maxSkillLevel = maxSkillLevel;
        this.minCost = minCost;

        //circleConstructor.preferences.putInteger("skillLevel"+skill.name, 0);
        //circleConstructor.preferences.flush();
        skillLevel = circleConstructor.preferences.getInteger("skillLevel" + skill.name);
        //skill.skillValue = values[skillLevel];
        cost = minCost*(skillLevel+1);

        this.setPosition(x,y);
        this.setSize(WIDTH, HEIGHT);
        Texture texture = new Texture(Gdx.files.internal("shopPan/stepSkillsPanB.png"));
        back = new Image(texture);
        back.setSize(WIDTH, HEIGHT);
        back.setColor(Color.valueOf("#DC143C"));
        back.setPosition(0, 0);
        back.setVisible(true);

        this.addActor(back);

        float widthOfElement = 123*UI.scaleCoeff;
        float heightOfElement = 121*UI.scaleCoeff;

        mainBtn = new Button(getWidth() - widthOfElement - 30*UI.scaleCoeff,  (getHeight()-heightOfElement)/2, widthOfElement, heightOfElement, 0, "shopPan/button.png");
        mainBtn.setColor(Color.valueOf("#DC143C"));
        mainBtn.images = new Image[2];

        widthOfElement = 97*0.7f*UI.scaleCoeff;
        heightOfElement = 101*0.7f*UI.scaleCoeff;

        texture = new Texture(Gdx.files.internal("shopPan/money.png"));
        Image image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((mainBtn.getWidth()- widthOfElement)/2,  (mainBtn.getHeight() - heightOfElement)/2);
        image.setVisible(true);
        image.addAction(Actions.forever(Actions.sequence(Actions.color(Color.ORANGE, 0.5f, Interpolation.fade), Actions.color(Color.GOLD, 0.5f, Interpolation.fade))));

        mainBtn.images[0] = image;
        mainBtn.images[0].setVisible(skillLevel < maxSkillLevel);
        mainBtn.addActor(mainBtn.images[0]);

        widthOfElement = 123*0.6f*UI.scaleCoeff;
        heightOfElement = 121*0.6f*UI.scaleCoeff;

        texture = new Texture(Gdx.files.internal("shopPan/buttonFill.png"));
        image = new Image(texture);
        image.setSize(widthOfElement, heightOfElement);
        image.setColor(Color.ORANGE);
        image.setPosition((mainBtn.getWidth()- widthOfElement)/2,  (mainBtn.getHeight() - heightOfElement)/2);
        image.setVisible(true);

        mainBtn.images[1] = image;
        mainBtn.images[1].setVisible(skillLevel == maxSkillLevel);
        mainBtn.addActor(mainBtn.images[1]);

        mainBtn.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(skillLevel != maxSkillLevel)
                mainBtn.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(skillLevel != maxSkillLevel)
                mainBtn.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(skillLevel < maxSkillLevel) {
                    upLevelSkill();
                }
            }
        });
        this.addActor(mainBtn);

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(fontSize*0.02f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        namePanLabel = new LabelT(namePan, labelStyle);
        namePanLabel.setText(namePan + " x" + skill.skillValue);
        namePanLabel.setAlignment(Align.center);
        namePanLabel.setPosition(70*UI.scaleCoeff, HEIGHT/2-fontSize - 15*UI.scaleCoeff);
        this.addActor(namePanLabel.createShadow(Color.DARK_GRAY, 0, -5*UI.scaleCoeff));
        this.addActor(namePanLabel);

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(fontSize*0.015f);
        labelStyle = new Label.LabelStyle(font, Color.ORANGE);
        costLabel = new LabelT(namePan, labelStyle);
        costLabel.setAlignment(Align.center);
        costLabel.setSize(getWidth(), fontSize);
        costLabel.setPosition(0, 25*UI.scaleCoeff);
        costLabel.setText(String.valueOf(cost));
        if(skillLevel == maxSkillLevel)
            costLabel.setText(UI.selectedLanguage == Text.EN ? "max level" : "максимальный уровень");
        this.addActor(costLabel.createShadow(Color.RED, 0, -4*UI.scaleCoeff));
        this.addActor(costLabel);

        levelImages = new Image[4];
        widthOfElement = 125*UI.scaleCoeff;
        heightOfElement = 30*UI.scaleCoeff;
        texture = new Texture(Gdx.files.internal("quad.png"));
        for(int i = 0;i < levelImages.length; i++) {
            levelImages[i] = new Image(texture);
            levelImages[i].setSize(widthOfElement, heightOfElement);
            levelImages[i].setColor(Color.CORAL.r,Color.CORAL.g,Color.CORAL.b, 1);
            levelImages[i].setPosition(57*UI.scaleCoeff + (widthOfElement+14*UI.scaleCoeff)*i, back.getHeight() - 57*UI.scaleCoeff);
            levelImages[i].setVisible(i < skillLevel);
            this.addActor(levelImages[i]);
        }

        circleConstructor.skillsActive.add(skill);
    }
    private void upLevelSkill()
    {
        if(circleConstructor.preferences.getInteger("money") >= cost) {
            circleConstructor.removeMoney(cost);
            levelImages[skillLevel].setVisible(true);
            skillLevel++;
            mainBtn.images[0].setVisible(skillLevel < maxSkillLevel);
            mainBtn.images[1].setVisible(skillLevel == maxSkillLevel);

            cost = minCost * (skillLevel + 1);

            if (skillLevel == maxSkillLevel)
                costLabel.setText(UI.selectedLanguage == Text.EN ? "max level" : "максимальный уровень");
            else costLabel.setText(String.valueOf(cost));

            skill.skillValue = values[skillLevel];

            namePanLabel.setText(namePan + " " + texts[skillLevel]);
            circleConstructor.preferences.putInteger("skillLevel" + skill.name, skillLevel);
            circleConstructor.preferences.flush();
            UI.main.showMessage(MessageType.SKILL, UI.selectedLanguage == Text.EN ? "Bought!" : "куплено!");
        }
        else
            UI.main.showMessage(MessageType.SKILL, UI.selectedLanguage == Text.EN ? "not enough\natoms!" : "недостаточно\nатомов!");
    }
    public void setValues(float v1,float v2,float v3, float v4, float v5)
    {
        values =new float[5];
        values[0] = v1;
        values[1] = v2;
        values[2] = v3;
        values[3] = v4;
        values[4] = v5;
        skill.skillValue = values[skillLevel];
        namePanLabel.setText(namePan + " " +  texts[skillLevel]);
    }
    public void setTexts(String s1,String s2,String s3,String s4, String s5)
    {
        texts = new String[5];
        texts[0] = s1;
        texts[1] = s2;
        texts[2] = s3;
        texts[3] = s4;
        texts[4] = s5;
        namePanLabel.setText(namePan + " " +  texts[skillLevel]);
    }
    public int getLevel()
    {
        return skillLevel;
    }
    public int getMaxLevel()
    {
        return maxSkillLevel;
    }
    public int getCost()
    {
        return cost;
    }
}
