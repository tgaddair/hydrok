package com.eldritch.hydrok.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.eldritch.hydrok.HydrokGame;
import com.eldritch.hydrok.activator.Activator;
import com.eldritch.hydrok.player.Player;

public class HydrokContactListener implements ContactListener {
    private final Player player;
    private int groundContacts = 0;
    
    public HydrokContactListener(Player player) {
        this.player = player;
    }

    public boolean isGrounded() {
        return groundContacts > 0;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null) {
            return;
        }
        
        checkBeginContact(fa.getUserData());
        checkBeginContact(fb.getUserData());
    }
    
    private boolean checkActivation(Object userData) {
        if (userData instanceof Activator) {
            Activator a = (Activator) userData;
            a.activate(player);
            return true;
        }
        return false;
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null) {
            return;
        }

        checkEndContact(fa.getUserData());
        checkEndContact(fb.getUserData());
    }
    
    private void checkBeginContact(Object userData) {
        if (userData != null) {
            // ground
            if (userData.equals("ground")) {
                groundContacts++;
                player.markGrounded();
            }
            
            // activator
            checkActivation(userData);
        }
    }
    
    private void checkEndContact(Object userData) {
        if (userData != null) {
            // ground
            if (userData.equals("ground")) {
                groundContacts--;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
