package me.jesseviitasalo.tetris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Listener implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (Tetris.current != null) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					Tetris.current.rotate();
					break;
				case KeyEvent.VK_DOWN:
					if (Tetris.current.canMoveDown()) {
						Tetris.current.moveDown();
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (Tetris.current.canMoveRight()) {
						Tetris.current.moveRight();
					}
					break;
				case KeyEvent.VK_LEFT:
					if (Tetris.current.canMoveLeft()) {
						Tetris.current.moveLeft();
					}
					break;
				case KeyEvent.VK_SPACE:
					while (Tetris.current.canMoveDown()) {
						Tetris.current.moveDown();
					}
					break;
				default:
					return;
			}
			
			TetrisBlock.drawAll(Tetris.tetris.getGraphics());
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Tetris.play.doClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}
