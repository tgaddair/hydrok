package com.eldritch.hydrok.level;

import static com.eldritch.hydrok.util.Settings.SCALE;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class Platform {
    private final Body body;
    
    public Platform(WorldCell cell, World world, float scaleY) {
        TiledMapTile tile = cell.getTile();
        float halfWidth = (tile.getTextureRegion().getRegionWidth() / 2.0f) * SCALE;
        float halfHeight = (scaleY * tile.getTextureRegion().getRegionHeight() / 2.0f) * SCALE;
        
        // Create our body definition
        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(cell.getWorldX() + halfWidth, cell.getWorldY() + halfHeight));

        // Create a body from the defintion and add it to the world
        body = world.createBody(groundBodyDef);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();

        // setAsBox takes half-width and half-height as arguments
        groundBox.setAsBox(halfWidth, halfHeight);

        // Create a fixture from our polygon shape and add it to our ground body
        Fixture fixture = body.createFixture(groundBox, 0.0f);
        body.setUserData("ground");

        // set collision masks
        Filter filter = fixture.getFilterData();
        filter.categoryBits = 0x0001;
        filter.maskBits = cell.getType().getMaskBits();
        fixture.setFilterData(filter);

        // Clean up after ourselves
        groundBox.dispose();
    }
    
    
    public Body getBody() {
        return body;
    }
}
