package com.eldritch.hydrok.level;

import static com.eldritch.hydrok.util.Settings.TILE_WIDTH;
import static com.eldritch.hydrok.util.Settings.TILE_HEIGHT;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.eldritch.hydrok.player.Player;

public class ProceduralTiledMap extends TiledMap {
    public static final int L = 3;

    private final TiledMap[][] chunks = new TiledMap[L][L];
    private final MapChunkGenerator generator;
    private final int chunkWidth;
    private final int chunkHeight;
    private final Vector2 lastPosition = new Vector2();

    private int minX = 0;
    private int minY = 0;

    public ProceduralTiledMap(World world, int width, int height) {
        this.chunkWidth = width;
        this.chunkHeight = height;
        generator = new MapChunkGenerator(chunks, world, width, height);

        // generate initial chunk setup: [0, 0] is bottom left
        for (int j = 0; j < L; j++) {
            for (int i = L - 1; i >= 0; i--) {
                chunks[i][j] = generate(i, j, 0, 0);
            }
        }

        // add index layers
        getLayers().add(new ProceduralLayer(0, width * L, height * L, TILE_WIDTH, TILE_HEIGHT));
    }

    public void update(Player player) {
        // compare last chunk with the current chunk
        int lastX = getIndex(lastPosition.x, chunkWidth);
        int lastY = getIndex(lastPosition.y, chunkHeight);
        
        // compare current chunk with the position chunk
        
        Vector2 position = player.getPosition();
        int chunkX = getIndex(position.x, chunkWidth);
        int currentY = getIndex(minY, chunkHeight);

        // check for horizontal crossing
        if (lastX < chunkX) {
            // right
            for (int i = L - 1; i >= 0; i--) {
                // destroy the first column
                for (MapLayer layer : chunks[i][0].getLayers()) {
                    ChunkLayer chunk = (ChunkLayer) layer;
                    chunk.destroy();
                    generator.removeVertices(chunk.getVertexCount());
                }

                for (int j = 0; j < chunks[i].length - 1; j++) {
                    // shift left
                    chunks[i][j] = chunks[i][j + 1];
                }

                // regen last column
                int j = chunks.length - 1;
                chunks[i][j] = generate(i, j, chunkX, currentY);
            }

            // reset min x position
            minX = chunkX * chunkWidth;
        }
        
        int currentX = getIndex(minX, chunkWidth);
        int chunkY = getIndex(position.y, chunkHeight);

        // check for vertical crossing
        if (currentY < chunkY) {
            // up
            for (int j = 0; j < chunks.length; j++) {
                // destroy the first row
                for (MapLayer layer : chunks[0][j].getLayers()) {
                    ((ChunkLayer) layer).destroy();
                }

                for (int i = 0; i < chunks.length - 1; i++) {
                    // shift down
                    chunks[i][j] = chunks[i + 1][j];
                }

                // regen last row
                int i = chunks.length - 1;
                chunks[i][j] = generate(i, j, currentX, chunkY);
            }

            // reset min y position
            minY = chunkY * chunkHeight;
        } else if (currentY > chunkY) {
            // down
            for (int j = 0; j < chunks.length; j++) {
                // destroy the last two rows
                for (int offset = 0; offset <= 0; offset++) {
                    for (MapLayer layer : chunks[chunks.length - 1 - offset][j].getLayers()) {
                        ((ChunkLayer) layer).destroy();
                    }
                }

                for (int i = chunks.length - 1; i >= 1; i--) {
                    // shift up
                    chunks[i][j] = chunks[i - 1][j];
                }

                // regen first two rows
                for (int offset = 0; offset <= 0; offset++) {
                    chunks[offset][j] = generate(offset, j, currentX, chunkY);
                }
            }

            // reset min y position
            minY = chunkY * chunkHeight;
        }

        // reset the last position
        lastPosition.set(position);
    }
    
    private int getIndex(float a, int length) {
        return (int) Math.floor(a / length);
    }

    private TiledMap generate(int i, int j, int chunkX, int chunkY) {
        return generator.generate(i, j,
                (chunkX + j) * chunkWidth - chunkWidth,
                (chunkY + i) * chunkHeight - chunkHeight);
    }

    private class ProceduralLayer extends TiledMapTileLayer {
        private final int index;

        public ProceduralLayer(int index, int width, int height, int tileWidth, int tileHeight) {
            super(width, height, tileWidth, tileHeight);
            this.index = index;
        }

        @Override
        public Cell getCell(int x, int y) {
            // adjust for shifting
            x -= minX;
            y -= minY;

            x += chunkWidth;
            y += chunkHeight;

            // get relevant chunk
            int chunkX = getIndex(x, chunkWidth);
            int chunkY = getIndex(y, chunkHeight);

            // handle out of bounds
            if (chunkX < 0 || chunkY < 0 || chunkX >= L || chunkY >= L) {
                return null;
            }

            // check for layer existence
            if (chunks[chunkY][chunkX].getLayers().getCount() <= index) {
                return null;
            }

            // return the cell within chunk
            TiledMapTileLayer layer = (TiledMapTileLayer) chunks[chunkY][chunkX].getLayers().get(
                    index);
            int tileX = x - chunkX * chunkWidth;
            int tileY = y - chunkY * chunkHeight;
            return layer.getCell(tileX, tileY);
        }
    }
}
