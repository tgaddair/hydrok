package com.eldritch.hydrok.level;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class ChunkLayer extends TiledMapTileLayer {
    private final Set<Body> bodies = new HashSet<Body>();
    private final World world;

    public ChunkLayer(World world, int width, int height, int tileWidth, int tileHeight) {
        super(width, height, tileWidth, tileHeight);
        this.world = world;
    }

    public void addBody(Body body) {
        bodies.add(body);
    }
    
    @Override
    public void setCell(int x, int y, Cell cell) {
        if (cell instanceof WorldCell) {
            setCell(x, y, (WorldCell) cell);
        } else {
            throw new IllegalArgumentException("Can only add WorldCells to the ChunkLayer");
        }
    }

    public void setCell(int x, int y, WorldCell cell) {
        super.setCell(x, y, cell);
        addBody(cell.getBody());
    }

    @Override
    public WorldCell getCell(int x, int y) {
        Cell cell = super.getCell(x, y);
        return cell != null ? (WorldCell) cell : null;
    }

    public void destroy() {
        for (Body body : bodies) {
            world.destroyBody(body);
        }
    }
}