package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entity.Player;
import object.SuperObject;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{

	//SCREEN SETTINGS
	final int originalTileSize = 16; //16x16 tile
	final int scale = 3;
	
	public int tileSize = originalTileSize * scale; // 48x48 tile
	public int maxScreenCol = 16;
	public int maxScreenRow = 12;
	public int screenWidth = tileSize * maxScreenCol; // 768 pixels
	public int screenHeight = tileSize * maxScreenRow; // 576 pixels
	
	//WORLD SETTINGS
	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;
	
	//FPS
	int FPS = 60;
	
	//SYSTEM
	TileManager tileM = new TileManager(this);
	KeyHandler keyH = new KeyHandler(this);
	Sound se = new Sound();
	Sound music = new Sound();
	
	public CollisionChecker cChecker = new CollisionChecker(this);
	public AssetSetter aSetter = new AssetSetter(this);
	Thread gameThread;
	
	//ENTITY AND OBJECT
	public Player player = new Player(this,keyH);
	public SuperObject obj[] = new SuperObject[10];
	
		
	public GamePanel( ) {
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}
	
	public void setUpGame() {
		aSetter.setObject();
		
		playMusic(0);
	}
	
	public void zoomInOut(int i) {
		
		int oldWorldWidth = tileSize * maxWorldCol;
		tileSize += i;
		int newWorldWidth = tileSize * maxWorldRow;
		
		player.speed = (double)newWorldWidth/600;
		
		double multiplier = (double)newWorldWidth/oldWorldWidth;
		
		double newPlayerWorldX = player.worldX * multiplier;
		double newPlayerWorldY = player.worldY * multiplier;
		
		player.worldX = newPlayerWorldX;
		player.worldY = newPlayerWorldY;
		
		for(int x = 0; x < obj.length; x++) {
			if(obj[x] != null) {
				double newObjectWorldX = obj[x].worldX * multiplier;
				double newObjectWorldY = obj[x].worldY * multiplier;
				
				obj[x].worldX = newObjectWorldX;
				obj[x].worldY = newObjectWorldY;
			}
		}
		
	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void run() {
		
		double drawInterval = 1000000000/FPS;
		double delta = 0; 
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		int drawCount = 0;
		
		while (gameThread != null) {
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			
			if (delta > 1) {
				update();
				repaint();
				delta--;
				drawCount++;
			}
			
			if (timer >= 1000000000) {
				drawCount = 0;
				timer = 0;
			}
		}
	}
	
	public void update() {
		player.update();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		//TILE
		tileM.draw(g2);
		
		//OBJECT
		for(int i = 0;i < obj.length;i++) {
			if(obj[i] != null) {
				obj[i].draw(g2, this);
			}
		}
		
		//PLAYER
		player.draw(g2);
		
		g2.dispose();
	}
	
	public void playMusic(int i) {
		
		music.setFile(i);
		music.play();
		music.loop();
	}
	
	public void stopMusic() {

		music.stop();
	}
	
	public void playSE(int i) {
		
		se.setFile(i);
		se.play();
	}
}
