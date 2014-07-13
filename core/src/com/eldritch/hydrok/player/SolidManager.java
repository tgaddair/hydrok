package com.eldritch.hydrok.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.eldritch.hydrok.util.Settings;

public class SolidManager extends AbstractPhaseManager {
	private static final int MAX_VELOCITY = 8;
	private static final float JUMP = 2.75f;
	
	private final TextureRegion texture;
	
	public SolidManager(Player player, World world, int x, int y, float width, float height) {
	    super(player, world, x, y, width, height, 0.5f, 0.3f, Settings.BIT_SOLID);
		texture = new TextureRegion(new Texture("sprite/solid.png"));
	}
	
	@Override
	public void applyImpulse(float x, float y) {
	    if (getPlayer().canJump) {
    	    // jump
    	    Vector2 pos = getBody().getPosition();
    	    getBody().applyLinearImpulse(0, JUMP, pos.x, pos.y, true);
    	    getPlayer().canJump = false;
	    }
	}
	
	@Override
	public void doUpdate(float delta, boolean grounded) {
		// apply right impulse, but only if on the ground and max velocity is not reached yet
		Vector2 pos = getBody().getPosition();
		if (grounded && getBody().getLinearVelocity().x < MAX_VELOCITY) {  
			getBody().applyLinearImpulse(0.15f, 0, pos.x, pos.y, true);
		}
	}
	
	@Override
	public void render(OrthogonalTiledMapRenderer renderer) {
	    Body body = getBody();
		Vector2 position = body.getPosition();
		
		float width = getWidth();
        float height = getHeight();
		
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		batch.draw(texture, position.x - width / 2, position.y - height / 2, width / 2, height / 2,
				width, height, 1f, 1f, (float) (body.getAngle() * 180 / Math.PI));
		batch.end();
	}
}
