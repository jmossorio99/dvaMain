package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.Game;
import model.Hero;
import threads.RunningAnimationThread;
import threads.CrouchAnimationThread;
import threads.IddleAnimationThread;
import threads.MoveThread;

public class GameViewController implements Initializable {

	@FXML
	private ImageView heroImageView;
	private Game game;
	private Hero hero;
	private MoveThread moveThread;
	private RunningAnimationThread runningAnimationThread;
	private IddleAnimationThread iddleAnimationThread;
	private CrouchAnimationThread crouchAnimationThread;
	private Scene scene;
	private ArrayList<Image> iddleLeft = new ArrayList<Image>();
	private ArrayList<Image> iddleRight = new ArrayList<Image>();
	private ArrayList<Image> runningLeft = new ArrayList<Image>();
	private ArrayList<Image> runningRight = new ArrayList<Image>();
	private ArrayList<Image> crouchingRight = new ArrayList<Image>(); 
	private ArrayList<Image> crouchingLeft = new ArrayList<Image>(); 
	

	public void setGame(Scene scene) {

		hero = new Hero(heroImageView.getLayoutX(), heroImageView.getLayoutY());
		game = new Game(hero);
		addSpriteImages();
		startThreads();

		this.scene = scene;
		scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				if (event.getCode() == KeyCode.RIGHT) {

					setHeroMoving(true);
					setHeroDirection(hero.RIGHT);

				} else if (event.getCode() == KeyCode.LEFT) {

					setHeroMoving(true);
					setHeroDirection(hero.LEFT);

				} else if (event.getCode() == KeyCode.UP) {

					hero.setAimingUp(true);

				} else if (event.getCode() == KeyCode.DOWN) {

					hero.setCrouching(true);
					if (getHeroImageViewPosY()<570) {
						setHeroY(getHeroImageViewPosY()+28);
					}

				}else if (event.getCode() == KeyCode.J) {
					System.out.println(hero.isMoving());
				}

			}

		});
		scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				hero.setMoving(false);
				hero.setCrouching(false);
				hero.setAimingUp(false);
				if (event.getCode() == KeyCode.DOWN) {
					setHeroY(getHeroImageViewPosY()-28);
				}

			}

		});

	}

	public void setHeroMoving(boolean moving) {

		hero.setMoving(moving);

	}

	public boolean getHeroMoving() {

		return hero.isMoving();

	}

	public void setHeroDirection(int direction) {

		hero.setDirection(direction);

	}

	public int getHeroDirection() {

		return hero.getDirection();

	}

	public double getHeroImageViewPosX() {

		return heroImageView.getLayoutX();

	}

	public double getHeroImageViewPosY() {

		return heroImageView.getLayoutY();

	}
	
	public boolean getHeroCrouching() {
		
		return hero.isCrouching();
		
	}
	
	public boolean getHeroAimingUp() {
		
		return hero.isAimingUp();
		
	}

	public void setHeroX(double x) {

		heroImageView.setLayoutX(x);
		hero.setPosX(x);

	}

	public void setHeroY(double y) {

		heroImageView.setLayoutY(y);
		hero.setPosY(y);

	}

	public void startThreads() {

		moveThread = new MoveThread(this, hero);
		moveThread.start();
		runningAnimationThread = new RunningAnimationThread(this, hero);
		runningAnimationThread.start();
		iddleAnimationThread = new IddleAnimationThread(this, hero);
		iddleAnimationThread.start();
		crouchAnimationThread = new CrouchAnimationThread(this, hero);
		crouchAnimationThread.start();

	}

	public void setHeroImage(Image img) {

		heroImageView.setImage(img);

	}

	public void addSpriteImages() {

		for (int i = 0; i < 6; i++) {

			iddleLeft.add(new Image("file:data/sprites/hero/Iddle/left/Idle" + (i + 1) + "I.png"));
			iddleRight.add(new Image("file:data/sprites/hero/Iddle/right/Idle" + (i + 1) + "D.png"));

		}
		for (int i = 0; i < 11; i++) {

			runningLeft.add(new Image("file:data/sprites/hero/Running/Left/Run" + (i + 1) + "I.png"));
			runningRight.add(new Image("file:data/sprites/hero/Running/Right/Run" + (i + 1) + "D.png"));

		}
		for (int i = 0 ;i<5;i++) {
			crouchingRight.add(new Image("file:data/sprites/hero/Crouch/right/crouch" + (i + 1) + "D.png"));
			crouchingLeft.add(new Image("file:data/sprites/hero/Crouch/left/crouch" + (i + 1) + "I.png"));
		}

	}

	public Image getIddleLeftImage(int i) {

		return iddleLeft.get(i);

	}

	public Image getIddleRightImage(int i) {

		return iddleRight.get(i);

	}

	public Image getRunningLeftImage(int i) {

		return runningLeft.get(i);

	}

	public Image getRunningRightImage(int i) {

		return runningRight.get(i);

	}
	
	public Image getCrouchingRightImage(int i) {
		return crouchingRight.get(i);
	}
	
	public Image getCrouchingLeftImage(int i) {
		return crouchingLeft.get(i);
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

}
