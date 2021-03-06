package com.eldritch.hydrok.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.eldritch.hydrok.GameScreen;
import com.eldritch.hydrok.util.Settings;

public class GasManager extends AbstractPhaseManager {
	private static final int MAX_VELOCITY_Y = 3;
	private static final int MAX_VELOCITY_X = 2;
	private static final float JUMP = 0.5f;
	
	private final Animation animation;
	
	public GasManager(Player player, World world, int x, int y, float width, float height) {
	    super(player, world, x, y, width, height, 0.20f, 0.3f, Settings.BIT_GAS);
	    
		TextureRegion[][] regions = GameScreen.getRegions("sprite/gas.png", 64, 64);
		Array<TextureRegion> allRegions = new Array<TextureRegion>();
		for (TextureRegion[] region : regions) {
			allRegions.addAll(region);
		}
        animation = new Animation(0.15f, allRegions);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        
        // air resistance to slow down the body
        getBody().setLinearDamping(0.5f);
	}
	
	@Override
    public void applyImpulseFrom(float x, float y) {
        Vector2 pos = getBody().getPosition();
        Vector2 dir = new Vector2(pos.x, pos.y).sub(x, y).nor();
        if (dir.x > 0 && getBody().getLinearVelocity().x >= MAX_VELOCITY_X) {
            // can't exceed max velocity to discourage gas form for moving
            dir.x = 0;
        }
        getBody().applyLinearImpulse(dir.x * JUMP, dir.y * JUMP, pos.x, pos.y, true);
    }
	
	@Override
	public void doUpdate(float delta) {
		// apply upwards impulse, but only if max velocity is not reached yet
		Body body = getBody();
		Vector2 pos = body.getPosition();
		if (body.getLinearVelocity().y < MAX_VELOCITY_Y) {
		    float force = Math.max(1 / (getPlayer().lastGround * 20), 0.0515f);
			body.applyLinearImpulse(0, force, pos.x, pos.y, true);
		}
	}
	
	@Override
	public void render(OrthogonalTiledMapRenderer renderer) {
		Vector2 position = getBody().getPosition();
		
		float width = getWidth();
        float height = getHeight();
        
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		batch.setColor(getRedIntensity(), getGreenIntensity(), getBlueIntensity(), 1);
		batch.draw(animation.getKeyFrame(getStateTime()),
				position.x - width / 2, position.y - height / 2, width, height);
		batch.setColor(Color.WHITE);
		batch.end();
	}
	
	private float getRedIntensity() {
	    if (player.getRelativeTemperature() > 0) {
	        return 1;
	    } else {
	        return Math.min(1 - player.getPreviousPercent() + 0.25f, 1);
	    }
    }
	
	private float getGreenIntensity() {
        if (player.getRelativeTemperature() > 0) {
            return Math.min(1 - player.getTemperaturePercent() + 0.25f, 1);
        } else {
            return Math.min(1 - player.getPreviousPercent() + 0.25f, 1);
        }
    }
	
	private float getBlueIntensity() {
        if (player.getRelativeTemperature() > 0) {
            return Math.min(1 - player.getTemperaturePercent() + 0.25f, 1);
        } else {
            return 1;
        }
    }
}
