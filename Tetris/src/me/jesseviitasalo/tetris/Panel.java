package me.jesseviitasalo.tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Panel extends JPanel {
	
	public Panel() {
		this.setBounds(0, 0, Tetris.tetris.getWidth(), Tetris.tetris.getHeight());
		this.setBackground(new Color(69, 69, 69));
		this.setLayout(null);
		this.setVisible(true);
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D)graphics;
		
		if (Tetris.current == null) {
			Tetris.drawGameOver(g);
		} else {
			TetrisBlock.drawAll(g);
		}
	}
}
