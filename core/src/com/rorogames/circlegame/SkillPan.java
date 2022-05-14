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
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.awt.Font;

public class SkillPan extends Group {
    private String namePan;
    public Skill skill;
    private boolean purchased, equipped;

    private CircleConstructor circleConstructor;
    private Button mainBtn;
    private LabelT namePanLabel, costLabel, reqLabel;
    private Image back;
    private int cost;

    public SkillPan(Skill skill, String namePan, int fontSize, CircleConstructor circleConstructor, float x, float y, float WIDTH, float HEIGHT, int cost)
    {
        this.skill = skill;
        this.namePan = namePan;
        this.circleConstructor = circleConstructor;
        this.cost = cost;

        purchased = circleConstructor.preferences.getBoolean("purchased" + skill.name, false);
        if(purchased)
            equipped = circleConstructor.preferences.getBoolean("equipped" + skill.name, false);
        if(equipped)
            addSkill();

        this.setPosition(x,y);
        this.setSize(WIDTH, HEIGHT);
        Texture texture = new Texture(Gdx.files.internal("shopPan/skillsPanB.png"));
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
        mainBtn.images[0].setVisible(!purchased);
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
        mainBtn.images[1].setVisible(equipped);
        mainBtn.addActor(mainBtn.images[1]);
        mainBtn.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                mainBtn.addAction(Actions.alpha(1, 0.2f, Interpolation.fade));
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainBtn.addAction(Actions.alpha(0.5f, 0.2f, Interpolation.fade));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!purchased) {
                    purchaseSkill();
                    return;
                }
                if(purchased) {
                    if(!equipped) {
                        addSkill();
                        equipped = true;
                        mainBtn.images[1].setVisible(true);
                        return;
                    }
                    if(equipped)
                    {
                        removeSkill();
                        equipped = false;
                        mainBtn.images[1].setVisible(false);
                    }
                }
            }
        });

        this.addActor(mainBtn);

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(fontSize*0.02f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        namePanLabel = new LabelT(namePan, labelStyle);
        namePanLabel.setAlignment(Align.center);
        namePanLabel.setPosition((70*UI.scaleCoeff), HEIGHT/2-fontSize/2);
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
        if(purchased)
            costLabel.setText(UI.selectedLanguage == Text.EN ? "PURCHASED" : "КУПЛЕНО");
        this.addActor(costLabel.createShadow(Color.RED, 0, -4*UI.scaleCoeff));
        this.addActor(costLabel);

        font = new BitmapFont(Gdx.files.internal("fonts/int.fnt"));
        font.getData().setScale(fontSize*0.013f);
        labelStyle = new Label.LabelStyle(font, Color.CORAL);
        reqLabel = new LabelT(namePan, labelStyle);
        reqLabel.setAlignment(Align.center);
        reqLabel.setSize(getWidth(), fontSize);
        reqLabel.setPosition(0*UI.scaleCoeff, HEIGHT-60*UI.scaleCoeff);
        reqLabel.setText(String.valueOf(cost));

        this.addActor(reqLabel);
    }
    private void purchaseSkill()
    {
        if(circleConstructor.preferences.getInteger("money") >= cost) {
            mainBtn.images[0].setVisible(false);
            mainBtn.images[1].setVisible(true);
            equipped = true;
            purchased = true;
            circleConstructor.preferences.putBoolean("purchased" + skill.name, true);
            costLabel.setText(UI.selectedLanguage == Text.EN ? "PURCHASED" : "КУПЛЕНО");
            circleConstructor.removeMoney(cost);
            UI.main.showMessage(MessageType.SKILL, UI.selectedLanguage == Text.EN ? "Bought!" : "куплено!");
            addSkill();
        }
        else UI.main.showMessage(MessageType.SKILL, UI.selectedLanguage == Text.EN ? "not enough\natoms!" : "недостаточно\nатомов!");
    }

    private void addSkill()
    {
        circleConstructor.skillsActive.add(skill);
        circleConstructor.preferences.putBoolean("equipped" + skill.name, true);
        circleConstructor.preferences.flush();
    }
    private void removeSkill()
    {
        circleConstructor.skillsActive.removeValue(skill, true);
        circleConstructor.preferences.putBoolean("equipped" + skill.name, false);
        circleConstructor.preferences.flush();
    }
    public void setReqLabel(String text){
        reqLabel.setText(text);
        //reqLabel.setPosition((getWidth()-reqLabel.getPrefWidth())/2, reqLabel.getY());
    }
    public int getCost()
    {
        return cost;
    }
}
