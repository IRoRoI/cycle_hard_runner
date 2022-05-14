package com.rorogames.circlegame;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.WorldManifold;

public class ContactProcess implements ContactListener {
    SceneObject sceneObject;

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (contact.isTouching()) {

            if ((fixA.getBody().getUserData().equals("player") || fixB.getBody().getUserData().equals("player")) && (fixA.getBody().getUserData().equals("obs") || fixB.getBody().getUserData().equals("obs"))) {
                if(!CircleConstructor.main.hasSkill("noSpikeDamage"))
                    CircleConstructor.main.gameOver();
            }
            if ((fixA.getBody().getUserData().equals("player") || fixB.getBody().getUserData().equals("player")) && (fixA.getBody().getUserData() instanceof LineRenderer || fixB.getBody().getUserData() instanceof LineRenderer)) {
                CircleConstructor.isGround = true;
            }
        }
        if(fixA.getBody().getUserData() instanceof LineRenderer || fixB.getBody().getUserData() instanceof LineRenderer)
        {
            if(fixA.getUserData() != null){
                if(fixA.getUserData().equals("playerHead")){
                    CircleConstructor.main.gameOver();
                }
            }
            if(fixB.getUserData() != null){
                if(fixB.getUserData().equals("playerHead")){
                    CircleConstructor.main.gameOver();
                   // return;
                }
            }
        }
        if ((fixA.getBody().getUserData().equals("player") || fixB.getBody().getUserData().equals("player"))) {
            //COININCREASE
            if (fixA.getBody().getUserData() instanceof SceneObject) {
                sceneObject = (SceneObject) fixA.getBody().getUserData();
            }
            if (fixB.getBody().getUserData() instanceof SceneObject) {
                sceneObject = (SceneObject) fixB.getBody().getUserData();
            }
            if(sceneObject != null) {
                if (sceneObject.name.equalsIgnoreCase("coin")) {
                    CircleConstructor.main.objectsToDestroy.add(sceneObject);
                }
                else if (sceneObject.name.equalsIgnoreCase("GMChanger")) {
                    GMChanger gmChanger = (GMChanger) sceneObject.getUserData();
                    sceneObject.sprite.getTexture().dispose();
                    sceneObject.sprite = null;
                    sceneObject.name = "changed";
                    CircleConstructor.main.setGameMode(gmChanger.gameMode, gmChanger.arg);
                    CircleConstructor.main.objectsToDestroy.add(sceneObject);
                }
                else if (sceneObject.name.equalsIgnoreCase("gCh")) {
                    CircleConstructor.activePlayer = false;
                    CircleConstructor.main.lines.get(0).edge.getBody().setActive(false);
                    CircleConstructor.main.objectsToDestroy.add(sceneObject);
                    sceneObject.getBody().setActive(false);
                }
                else if (sceneObject.name.equalsIgnoreCase("gCh2")) {
                    CircleConstructor.main.lines.get(0).edge.getBody().setActive(true);
                    CircleConstructor.activePlayer = true;
                    CircleConstructor.main.objectsToDestroy.add(sceneObject);
                    sceneObject.getBody().setActive(false);
                }
                else if (sceneObject.name.equalsIgnoreCase("spCh")) {
                    CircleConstructor.main.speedChange();
                    CircleConstructor.main.objectsToDestroy.add(sceneObject);
                    sceneObject.getBody().setActive(false);
                }
                else if (sceneObject.name.equalsIgnoreCase("LevelEnd"))
                {
                    sceneObject.sprite.getTexture().dispose();
                    sceneObject.name = "LevelEnded";
                    sceneObject.setVisible(false);
                    CircleConstructor.main.levelEnding = true;
                    CircleConstructor.main.objectsToDestroy.add(sceneObject);
                }
                else if(sceneObject.name.equalsIgnoreCase("DirChanger"))
                {
                    sceneObject.sprite.getTexture().dispose();
                    sceneObject.sprite = null;
                    sceneObject.name = "DirChange";
                    CircleConstructor.main.dirChanging = true;
                    CircleConstructor.main.objectsToDestroy.add(sceneObject);
                }
                else if(sceneObject.name.equalsIgnoreCase("Letter"))
                {
                    sceneObject.sprite = null;
                    sceneObject.name = "LetterFound";
                    CircleConstructor.main.letterFound();
                }
                sceneObject = null;
            }
            //CHECKGROUND
            if (!(fixA.getBody().getUserData() instanceof LineRenderer || fixB.getBody().getUserData() instanceof LineRenderer))
                CircleConstructor.isGround = false;
            if (fixA.getBody().getUserData() instanceof LineRenderer || fixB.getBody().getUserData() instanceof LineRenderer)
            {
                WorldManifold manifold = contact.getWorldManifold();

                for (int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
                    float angle = (float)Math.toDegrees(Math.atan((CircleConstructor.main.getPlayerPos().y - manifold.getPoints()[j].y)/(CircleConstructor.main.getPlayerPos().x - manifold.getPoints()[j].x)));
                    float a = (float)Math.toDegrees(Math.atan((CircleConstructor.centerY - CircleConstructor.main.getPlayerPos().y)/(CircleConstructor.centerX - CircleConstructor.main.getPlayerPos().x)));
                    CircleConstructor.player.spriteAngle = Math.abs(Math.abs(angle)-Math.abs(a));
                }
            }
        }
    }


    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (contact.isTouching()) {
            if ((fixA.getBody().getUserData().equals("playerHead") || fixB.getBody().getUserData().equals("playerHead")) && (fixA.getBody().getUserData().equals("obs") || fixB.getBody().getUserData().equals("obs"))) {
              //  CircleConstructor.main.gameOver();
            }

            if ((fixA.getBody().getUserData().equals("player") || fixB.getBody().getUserData().equals("player")) && (fixA.getBody().getUserData() instanceof LineRenderer || fixB.getBody().getUserData() instanceof LineRenderer)) {
                CircleConstructor.isGround = true;
             }
        }
    }
}
