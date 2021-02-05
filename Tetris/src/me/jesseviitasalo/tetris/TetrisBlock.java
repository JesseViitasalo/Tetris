package me.jesseviitasalo.tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

public class TetrisBlock {
	public static ArrayList<TetrisBlock> blocks = new ArrayList<TetrisBlock>();
	public static int size = 30;
	
	private ArrayList<TetrisBlock> connectedBlocks = new ArrayList<TetrisBlock>();
	private int x, y;
	private Color color;
	private int rotation = 1;
	private char shape;
	
	public TetrisBlock(int x, int y, TetrisBlock parent) {
		this.x = x;
		this.y = y;
		this.rotation = 1;
		blocks.add(this);
		
		if (parent != null) {
			shape = parent.shape;
			color = parent.color;
		} else {
			char[] shapes = {'O', 'I', 'S', 'Z', 'L', 'J', 'T'};
			shape = shapes[new Random().nextInt(shapes.length)];
			
			rotate();
		}
	}
	 
	public boolean canMoveDown() {
		for (TetrisBlock block : blocks) {
			if (!isConnectedBlock(block)) {
				for (TetrisBlock connected : connectedBlocks) {					
					if (block.getX() == connected.getX() && block.getY() - connected.getY() == size) {
						return false;
					}
				}
			}
		}
		
		for (TetrisBlock tb : connectedBlocks) {
			if (tb.getY() > 550) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canMoveRight() {
		for (TetrisBlock block : blocks) {
			if (!isConnectedBlock(block)) {
				for (TetrisBlock connected : connectedBlocks) {					
					if (block.getY() == connected.getY() && block.getX() - connected.getX() == size) {
						return false;
					}
				}
			}
		}
		
		for (TetrisBlock tb : connectedBlocks) {
			if (tb.getX() > 350) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canMoveLeft() {
		for (TetrisBlock block : blocks) {
			if (!isConnectedBlock(block)) {
				for (TetrisBlock connected : connectedBlocks) {					
					if (block.getY() == connected.getY() && connected.getX() - block.getX() == size) {
						return false;
					}
				}
			}
		}
		
		for (TetrisBlock tb : connectedBlocks) {
			if (tb.getX() < 20) {
				return false;
			}
		}
		
		return true;
	}
	
	public void moveDown() {
		for (TetrisBlock block : connectedBlocks) {
			block.y += size;
		}
	}
	
	public void moveRight() {
		for (TetrisBlock block : connectedBlocks) {
			block.x += + size;
		}
	}
	
	public void moveLeft() {
		for (TetrisBlock block : connectedBlocks) {
			block.x += - size;
		}
	}
	
	/**
	 * Draws all the blocks and the score counter.
	 */
	public static void drawAll(Graphics g) {
		Thread thread = new Thread() {
			public void run() {
				Tetris.tetris.paint(g);
				
				//Draw the current bottom position with white thing
				for (TetrisBlock block : Tetris.current.getDownPosition().getConnectedBlocks()) {
					g.setColor(Color.WHITE);
					g.fillRect(block.getX(), block.getY(), size, size);
					
					g.setColor(new Color(69, 69, 69));
					g.fillRect(block.getX() + 2, block.getY() + 2, size - 4, size - 4);
				}
				
				Tetris.sleep(1);
				
				//Draw all blocks
				try {
					for (TetrisBlock block : TetrisBlock.blocks) {
						g.setColor(Color.BLACK);
						g.fillRect(block.getX(), block.getY(), size, size);
						
						g.setColor(block.getColor());
						g.fillRect(block.getX() + 2, block.getY() + 2, size - 4, size - 4);
					}
				} catch (ConcurrentModificationException ignored) {
					
				}
				
				//Draw score
				Tetris.drawCenterString(g, "-:33, 148, 166:-Score: -:21, 237, 75:-" + Tetris.score, 50, 20);
			}
		};
		
		thread.start();
	}
	
	public boolean isConnectedBlock(TetrisBlock block) {
		for (TetrisBlock connected : connectedBlocks) {
			if (connected.equals(block)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the theoretical bottom position of this block
	 */
	public TetrisBlock getDownPosition() {
		int[] oldYs = new int[5];
		for (int i = 0; i < getConnectedBlocks().size(); i++) {
			oldYs[i] = getConnectedBlocks().get(i).getY();
		}
		
		while(this.canMoveDown()) {
			this.moveDown();
		}
		
		//Creates another thread to set the old positions to this block and starts it just before this method returns the object.
		Thread thread = new Thread() {
			public void run() {
				for (int i = 0; i < getConnectedBlocks().size(); i++) {
					connectedBlocks.get(i).y = oldYs[i];
				}
			}
		};
		thread.start();
		
		return this;
	}
	
	/**
	 * Removes all full layers that are connectedwith this block
	 */
	public void removeLayers() {
		int lowestRemovedLayerY = -420;
		int amount = 0;
		
		for (TetrisBlock block : getConnectedBlocks()) {
			ArrayList<TetrisBlock> list = getBlocksInY(block.getY());
			if (list.size() >= 13) {
				amount++;
				
				for (TetrisBlock remove : list) {
					blocks.remove(remove);
				}
				
				if (block.getY() > lowestRemovedLayerY) {
					lowestRemovedLayerY = block.getY();
				}
			}
		}
		
		//Play clear sound
		if (amount > 0) {
			Tetris.playSound("Clear.wav");
		}
		
		//Make upper blocks go down
		for (TetrisBlock block : blocks) {
			if (block.getY() < lowestRemovedLayerY) {
				block.y += size * amount;
			}
		}
		
		Tetris.score += 10 * amount;
	}
	
	public ArrayList<TetrisBlock> getBlocksInY(int y) {
		ArrayList<TetrisBlock> list = new ArrayList<TetrisBlock>();
		
		for (TetrisBlock block : blocks) {
			if (block.getY() == y) {
				list.add(block);
			}
		}
		
		return list;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getRotation() {
		return rotation;
	}
	
	public ArrayList<TetrisBlock> getConnectedBlocks() {
		return connectedBlocks;
	}
	
	public void rotate() {
		rotation++;
		for (TetrisBlock block : connectedBlocks) {
			if (!block.equals(this)) {
				blocks.remove(block);
			}
		}
		connectedBlocks.clear();
		connectedBlocks.add(this);
		Point[] rotations = null;
		
		switch(shape) {
			case 'O':
				rotation = 1;
				color = new Color(0xFFFFFF00);
				
				rotations = new Point[]{new Point(1, 0), new Point(1, 1), new Point(0, 1)};
				break;
			case 'I':
				if (rotation > 2) rotation = 1;
				color = new Color(0xFF36EAFF);
				
				switch (rotation) {
					case 1:
						rotations = new Point[]{new Point(0, 1), new Point(0, 2), new Point(0, 3)};
						break;
					case 2:
						rotations = new Point[]{new Point(1, 0), new Point(2, 0), new Point(-1, 0)};
						break;
				}
				break;
			case 'S':
				if (rotation > 2) rotation = 1;
				color = new Color(0xFFFF0009);
				
				switch (rotation) {
					case 1:
						rotations = new Point[]{new Point(1, 0), new Point(0, 1), new Point(-1, 1)};
						break;
					case 2:
						rotations = new Point[]{new Point(0, 1), new Point(-1, 0), new Point(-1, -1)};
						break;
				}
				break;
			case 'Z':
				if (rotation > 2) rotation = 1;
				color = new Color(0xFF00FF2B);
				
				switch (rotation) {
					case 1:
						rotations = new Point[]{new Point(0, 1), new Point(1, 1), new Point(-1, 0)};
						break;
					case 2:
						rotations = new Point[]{new Point(1, 0), new Point(1, -1), new Point(0, 1)};
						break;
				}
				break;
			case 'L':
				if (rotation > 4) rotation = 1;
				color = new Color(0xFFEC830C);
				
				switch (rotation) {
					case 1:
						rotations = new Point[]{new Point(0, 1), new Point(0, 2), new Point(1, 2)};
						break;
					case 2:
						rotations = new Point[]{new Point(1, 0), new Point(-1, 0), new Point(-1, 1)};
						break;
					case 3:
						rotations = new Point[]{new Point(-1, 0), new Point(0, 1), new Point(0, 2)};
						break;
					case 4:
						rotations = new Point[]{new Point(1, 0), new Point(1, -1), new Point(-1, 0)};
						break;
				}
				break;
			case 'J':
				if (rotation > 4) rotation = 1;
				color = new Color(0xFFFF19EF);
				
				switch (rotation) {
					case 1:
						rotations = new Point[]{new Point(0, 1), new Point(0, 2), new Point(-1, 2)};
						break;
					case 2:
						rotations = new Point[]{new Point(0, 1), new Point(1, 1), new Point(2, 1)};
						break;
					case 3:
						rotations = new Point[]{new Point(0, 1), new Point(0, -1), new Point(1, -1)};
						break;
					case 4:
						rotations = new Point[]{new Point(1, 0), new Point(-1, 0), new Point(1, 1)};
						break;
				}
				break;
			case 'T':
				if (rotation > 4) rotation = 1;
				color = new Color(0xFF9100FF);
				
				switch (rotation) {
					case 1:
						rotations = new Point[]{new Point(1, 0), new Point(-1, 0), new Point(0, 1)};
						break;
					case 2:
						rotations = new Point[]{new Point(0, 1), new Point(0, -1), new Point(-1, 0)};
						break;
					case 3:
						rotations = new Point[]{new Point(0, -1), new Point(1, 0), new Point(-1, 0)};
						break;
					case 4:
						rotations = new Point[]{new Point(1, 0), new Point(0, 1), new Point(0, -1)};
						break;
				}
				break;
		}
		
		for (Point r : rotations) {
			connectedBlocks.add(new TetrisBlock(x + (r.x * size), y + (r.y * size), this));
		}
	}
}
