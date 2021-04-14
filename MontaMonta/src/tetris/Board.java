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
	}//Gerar formas na tela
	currentShape.render(g);
		
	if(stopBounds.contains(mouseX, mouseY)){
	    g.drawImage(pause.getScaledInstance(pause.getWidth()+ 3, pause.getHeight() + 3, BufferedImage.SCALE_DEFAULT), stopBounds.x + 3, stopBounds.y + 3, null); 
	
	}else{
	   g.drawImage(pause, stopBounds.x, stopBounds.y, null);
		}
	if(refreshBounds.contains(mouseX, mouseY)){
	  g.drawImage(refresh.getScaledInstace(refresh.getWidth() + 3, refresh.getHeight() + 3,
		BufferedImage.SCALE_DEFAULT), refreshBounds.x + 3, refreshBounds.y + 3,null);
	 }else {
		 g.drawImage(refresh, refreshBounds.x, refreshBounds.y, null);
	 }  
		//Escrever Mensagem na tela de pause no jogo
		if(gamePaused){
			String gamePusedString = "GAME PAUSED";
			g.setColor(Color.WHITE);
			g.setFont(new Font("Georgia", Font.BOLD, 30));
			g.drawString(gamePausedString, 35, WindowGame.HEIGHT / 2);
		}
		if(gamerOver){
			String gameOverString = "GAME PAUSED";
			g.setColor(Color.WHITE);
			g.setFont(new Font("Georgia", Font.BOLD, 30));
			g.drawString(gamePausedString, 50, WindowGame.HEIGHT / 2);
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font("Georgia", Font.BOLD, 20));
		
		g.drawString("SCORE", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 );
		g.drawString(score + " ", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 + 30 );
		
		g.setColor(Color.WHITE);
		
		for ( int i = 0; i <= boardHeight; i ++ ) {
			g.drawLine(0, i * blockSize, boardWhidth * blockSize, i * blockSize);
		}
		for (int j = 0; j= boardWidth; j++) {
			g.drawLine(j * blockSize, 0, j * blockSize, boardHeight * 30);
		}
	}
	
	public void setNextShape() {
		int index = random.nextInt(shapes.length);
		int colorIndex = random.nextInt(colors.length);
		nextShape = new Shape(shape[index].getCoords(),this, colors[colorIndex]);
	}
	public void setCurrentShape() {
		currentShape = nextShape;
		setNextShape();
		
		for(int row = 0; row < currentShape.getCoords().length; row++){
			for (int col = 0; col < currentShape.getCoords()[0].legth; col ++ ) {
				if(currentShape.getCoords()[row][col] != 0) {
					if(board[currentShape.getY() + row][currentShape.getX() + col] != null) {
					gameOver = true;
					}
				}
			}
			
		}
		
	}
	public Color[][] getBoard(){
		return board;
	}
	
	@Override  //Ação das teclas 
	public void keyPressed(KeyEvent e){ 
		if (e.getKeyCode() == KeyEvent.VK_UP){
			currentShape.rotateShape();
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
		   currentShape().setDeltaX(1);
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			 currentShape().setDeltaX(-1);
		}
		if(e.getKeyCode() == keyEvent.VK_down) {
			currentShape.speedUp();
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			currentShape.speedDown();
		}
	}
	@Override 
	public void keyTyped(KeyEvent e){
		
	}
	//iniciar jogo CHmado metodos de parar jogo proxima tela e adicionar tela tela atual fim de jogo e restarta
    public void startGame() {
    	stopGame();
    	setNextShape();
    	setCurrentShape();
    	gameOver = false;
    	looper.start();
    } 
    // parar o jogo
	public void StopGame() {
		score = 0 ;
		
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; board[row].length; col++) {
				board[row][col] = null;
				
			}
		}
		looper.stop();
	}
		// Atualizar a tela e recolocar os objetos no caso peças
		class GameLooper implements ActionListener {
			
			@Override
			public void actionPerformed(ActionEvent e){
				update();
				repaint();
			}
			
		}
		//Ação de movimento do mouse
		@Override
		public void mouseDragged(MouseEvent e){
			mouseX = e.getX();
			mouseY = e.getY();
			
		}
		//Mover? mouse ou Mudar mouse
		@Override
		public void mouseMoved(MouseEvent e){
			mouseX = e.getX();
			mouseY = e.getY();	
		}
		@Override
		public void mousePressed(){
			if(e.getButton() == MouseEvent.Button1) {
				leftClick = true;
				}
			}
		}
	  @Override 
	  public void mouseRelased() {
		  if(e.getButton() == MouseEvent.BUTTON1){
			 leftClick = false;
		  }
	  }
	  @Override
	  public public void mouseEntered(MouseEvent e) {
	  }
	  @Override 
	  public void mouseExited(MouseEvent e) {  
	  }
	  public void addScore() {
		 score ++; 
	  }

	

}//fechamento da classe 

