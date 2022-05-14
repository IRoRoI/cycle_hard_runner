package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Skill {
    public boolean isActive, onlyArcade;
    public String pathIcon;
    public String name;

    public float sizeX, step, stepA = 1f/60f, kRefill = 1, skillValue = 1;
    private float progress, rstProgress;
    private Integer[] nCommands;

    private CircleConstructor circleConstructor;
    public SkillActor skillActor;

    public static Skill newSkill(String pathIcon, String name, float progress, CircleConstructor circleConstructor)
    {
        Skill skill = new Skill();
        skill.pathIcon = pathIcon;
        skill.name = name;
        skill.circleConstructor = circleConstructor;
        skill.progress = 0;
        skill.rstProgress = progress;
        skill.isActive = true;

        return skill;
    }
    public void addSpawnCommands(Integer[] spawnCommands)
    {
        nCommands = spawnCommands;
    }

    public void touched(boolean o)
    {
        skillActor.status.setPosition(skillActor.image.getX(), skillActor.image.getY());
        if(o) {
            if(progress < rstProgress) {
                if (name.equalsIgnoreCase("accelerationBtn")) {
                    progress += stepA;
                    CircleConstructor.player.accelerating = true;
                    skillActor.status.setSize(skillActor.status.getWidth() + step, skillActor.status.getHeight());
                }
                else if(name.equalsIgnoreCase("slowdown"))
                {
                    progress += stepA;
                    CircleConstructor.player.slowing = true;
                    skillActor.status.setSize(skillActor.status.getWidth() + step, skillActor.status.getHeight());
                }
                else if(name.equalsIgnoreCase("jet"))
                {
                    progress += stepA;
                    circleConstructor.jetEffect();
                    skillActor.status.setSize(skillActor.status.getWidth() + step, skillActor.status.getHeight());
                }
                else if(name.equalsIgnoreCase("builder") && progress <= 0)
                {
                    circleConstructor.mine();
                    progress = rstProgress;
                    skillActor.status.setSize(skillActor.status.getWidth() + step*60*rstProgress, skillActor.status.getHeight());
                }
            }
            else reset();
        }
        else {
            if(progress > 0) {
                progress -= stepA/2/kRefill;
                skillActor.status.setSize(skillActor.status.getWidth() - step/2/kRefill, skillActor.status.getHeight());
                if(skillActor.status.getWidth() < 0)
                    skillActor.status.setSize(0, 128*UI.scaleCoeff);
            }
        }
    }
    public void setStep()
    {
        step = sizeX/rstProgress/60f;
    }
    public void reset()
    {
        if(name.equalsIgnoreCase("accelerationBtn"))
        {
            CircleConstructor.player.accelerating = false;
        }
        else if(name.equalsIgnoreCase("slowdown"))
        {
            CircleConstructor.player.slowing = false;
        }
    }
    public void resetProgress()
    {
        progress = 0;
    }

    public void saveSkill()
    {

    }
}
