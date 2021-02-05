package me.jesseviitasalo.tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Tetris extends JFrame {
	public static int score;
	public static JButton play;
	public static TetrisBlock current;
	public static Tetris tetris;
	
	public static void main(String[] args) {
		new Tetris();
	}
	
	/**
	 * Initialize the JFrame and stuff.
	 */
	public Tetris() {
		tetris = this;
		this.setTitle("Tetris");
		this.setSize(405, 618);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.getContentPane().setBackground(new Color(69, 69, 69));
		this.setVisible(true);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setFocusable(true);
		this.addKeyListener(new Listener());
		
		play = new JButton("Play");
		play.setFont(new Font("Arial", Font.BOLD, 45));
		play.setForeground(Color.PINK);
		play.setBounds(100, 400, 200, 100);
		play.setVisible(false);
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				score = 0;
				play.setVisible(false);
				paint(getGraphics());
				drawGame(getGraphics());
			}
		});
		this.add(play);
		
		sleep(10);
		
		drawGameOver(this.getGraphics());
	}
	
	/**
	 * Draws the game over screen.
	 */
	public void drawGameOver(Graphics g) {
		g.setColor(new Color(69, 69, 69, 220));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		drawCenterString(g, "-:255, 0, 0:-Game over", 100, 50);
	    drawCenterString(g, "-:33, 148, 166:-Score: -:21, 237, 75:-" + score, 150, 50);
		
	    drawCenterString(g, "-:213, 135, 237:-Controls", 220, 30);
	    
	    drawCenterString(g, "-:33, 148, 166:-Space: -:21, 237, 75:-Hard drop", 250, 20);
	    drawCenterString(g, "-:33, 148, 166:-Up: -:21, 237, 75:-Rotate", 270, 20);
	    drawCenterString(g, "-:33, 148, 166:-Down: -:21, 237, 75:-Soft drop", 290, 20);
	    drawCenterString(g, "-:33, 148, 166:-Right: -:21, 237, 75:-Move right", 310, 20);
	    drawCenterString(g, "-:33, 148, 166:-Left: -:21, 237, 75:-Move left", 330, 20);
	    
	    play.setVisible(true);
	}
	
	/**
	 * Draws and creates the game.
	 */
	public void drawGame(Graphics g) {
		Thread thread = new Thread() {
			public void run() {
				while(true) {
					if (current == null || !current.canMoveDown()) {
						if (current != null) {
							Tetris.playSound("Fall.wav");
							current.removeLayers();
						}
						current = new TetrisBlock(217, -20, null);
						if (!current.canMoveDown()) {
							Tetris.playSound("GameOver.wav");
							current = null;
							TetrisBlock.blocks.clear();
							drawGameOver(g);
							break;
						}
						score++;
					}
					
					if (current.canMoveDown()) {
						current.moveDown();
						TetrisBlock.drawAll(g);
					}
					
					//Sleep x time before updating the block.
					Tetris.sleep(250);
				}
			}
		};
		
		thread.start();
	}
	
	/**
	 * Sleeps using Thread.sleep and catches possible exception.
	 * @param ms milliseconds to sleep
	 */
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Draws an x centered string to the screen using the passed graphics.
	 * You can pass an rgb color with the string like this -:0, 255, 0:- and it will use the color with the upcoming text until u pass another color.
	 */
	public static void drawCenterString(Graphics g, String text, int y, int size) {
		String startRegex = "-:";
		
		int widthPlus = 0;
		int textWidth = 0;
		String[] texts = {text};
		
		//Set font
		g.setFont(new Font("Arial", Font.BOLD, size));
		
		//Split the text into list for each given color
		if (text.contains(startRegex)) {
			ArrayList<String> list = new ArrayList<String>();
			for (String s : text.split(startRegex)) {
				if (!s.isEmpty()) {
					list.add(startRegex + s);
					
					//Calculate total text width including all parts of the real text
					String parsed = s.substring(s.indexOf(":-") + 2);
					textWidth += g.getFontMetrics().stringWidth(parsed);
				}
			}
			
	        String temp[] = new String[list.size()]; 
	        for (int i = 0; i < list.size(); i++) { 
	            temp[i] = list.get(i); 
	        } 
	  
	        texts = temp;
		}
		
		//Loop through the splitted color text things and add the width of the text to widthPlus so next piece will be rendered in the correct location
		for (String s : texts) {
			//Set color
			Color color = Color.BLACK;
			if (s.contains(startRegex)) {
				String[] split = s.replace(startRegex, "").substring(0, s.indexOf(":-") - 2).replace(" ", "").split(",");
				color = new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			}
			g.setColor(color);
			
			//Draw text and add its width to widthPlus
			String parsedText = s;
			if (parsedText.contains(startRegex)) {
				parsedText = parsedText.substring(parsedText.indexOf(":-") + 2);
			}
			
			g.drawString(parsedText, ((tetris.getWidth() / 2) - (textWidth / 2)) + widthPlus, y);
			widthPlus += g.getFontMetrics().stringWidth(parsedText);
		}
	}
	
	public static synchronized void playSound(String name) {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(Tetris.class.getResourceAsStream("sounds/" + name));
			clip.open(inputStream);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
