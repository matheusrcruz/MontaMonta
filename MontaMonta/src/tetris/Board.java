package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import sun.jvm.hotspot.utilities.ProcImageClassLoader;

public class Board extends JPanel implements KeyListener, MouseListener, MouseMotionListener{
	
	private static final long serialVersionUID = 1L;
	
	private BufferedImage pause, refresh;
	
	//tamanho da tela (area de jogo)
	private final int boardHeight = 20, boardWidth = 10;
	
	//tamanho dos blocos
	public static final int blockSize = 30;
	
	//campo
	private Color[][] board = new Color[boardHeight][boardWidth];
	
	// array com todos as formas possiveis de blocos
	private Shape[] shapes = new Shape[7];
	
	//forma atual
	private static Shape currentShape, nextShape;
	
	//game loop
	private Timer looper;
	
	private int FPS = 60;
	
	private int delay = 1000/FPS;
	
	//variaves de eventos do mouse
	private int mouseX, mouseY;
	
	private boolean leftClick = false;

	private Rectangle stopBounds, refreshBounds;
	
	private boolean gamePause = false;
	
	private boolean gameOver = false;
	
	private Color[] colors = {Color.decode("#ed1c24"), Color.decode("#ff7f27"), Color.decode("#fff200"),
			Color.decode("#22b14c"), Color.decode("#00a2e8"), Color.decode("#a349a4"), Color.decode("#3f48cc")};
	private Random random = new Random();
	
	//Mudar posicao da peça
	private Timer buttonLapse = new Timer(300, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			buttonLapse.stop();
			
		}
	});

	//pontuação
	private int score = 0;
	
	public Board() {
		
		//imagem de pause
		pause = ImageLoader.loadImage("/pause.png");
		
		refresh = ImageLoader.loadImage("/refresh.png");
		
		mouseX=0;
		mouseY=0;
		
		stopBounds = new Rectangle(350, 500, pause.getWidth(), pause.getHeight() + pause.getHeight() / 2);
		refreshBounds = new Rectangle(350, 500 - refresh.getHeight() - 20, refresh.getWidth(),
				refresh.getHeight() + refresh.getHeight() / 2);
		
		//cria loop do game
		looper = new Timer(delay, new GameLooper());
		
		//cria os formatos
		shapes[0] = new Shape(new int[][]{
			{1, 1, 1, 1} // formato de I;
		}, this, colors[0]);
		
		shapes[1] = new Shape(new int[][]{
            {1, 1, 1},
            {0, 1, 0}, // formato de T;
        }, this, colors[1]);

        shapes[2] = new Shape(new int[][]{
            {1, 1, 1},
            {1, 0, 0}, // formato de L;
        }, this, colors[2]);

        shapes[3] = new Shape(new int[][]{
            {1, 1, 1},
            {0, 0, 1}, // formato de J;
        }, this, colors[3]);

        shapes[4] = new Shape(new int[][]{
            {0, 1, 1},
            {1, 1, 0}, // formato de S;
        }, this, colors[4]);

        shapes[5] = new Shape(new int[][]{
            {1, 1, 0},
            {0, 1, 1}, // formato de Z;
        }, this, colors[5]);

        shapes[6] = new Shape(new int[][]{
            {1, 1},
            {1, 1}, // formato de O;
        }, this, colors[6]);
		
	}

	private void update() {
		if(stopBounds.contains(mouseX, mouseY) && leftClick && !buttonLapse.isRunning() && !gameOver) {
			buttonLapse.start();
			gamePaused = !gamePaused;
		}
		
		if(refreshBounds.contains(mouseX, mouseY) && leftClick){
			startGame();
		}
		
		if(gamePaused || gameOver) {
			return
		}
		currentShape.update();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		
		g.fillRect(0, 0, getWidth(), getHeight());
	
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col++) {
				
				if(board[row][col] != null) {
					g.setColor(board[row][col]);
					g.fillRect(col*blockSize, row*blockSize, blockSize, blockSize);
				}
			}
		}
	g.setColor(nextShape.getColor());
	for(int row=0; row < nextShape.getCoords().length; row++) {
		for(int col = 0; col < nextShape.getCoords()[0].length; col++) {
			if(nextShape.getCoords()[row][col] != 0) {
				g.fillRect(col * 30 + 320, row * 30 + 50, Board.blockSize, Board.blockSize);
			}
		}
	}
	currentShape.render(g);
	
	
	}

}
