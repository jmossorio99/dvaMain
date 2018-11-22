package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.Bullet;
import model.Game;
import model.Hero;
import model.Player;
import model.Robot;
import threads.HeroThread;
import threads.RobotThread;
import threads.ViewThread;

public class GameViewController implements Initializable {

	public static final String ORANGE_BULLET_ROUTE = "file:data/sprites/hero/Shoot/OrangeBullet.png";
	public static final int BULLET_SPEED = 5;
	public static final int ROBOT_SPEED = 1;
	@FXML
	private ImageView heroImageView;
	@FXML
	private AnchorPane anchorPane;
	private int robotNum = 0;
	private Game game;
	private Hero hero;
	private Player player;
	private Bullet firstBullet = null;
	private HeroThread heroThread;
	private ViewThread viewThread;
	private RobotThread rThread;
	private Scene scene;
	private Parent root;
	private double width;
	private double height;
	private int modifier = 150;
	private int robotCounter = modifier - 1;
	private ArrayList<Node> robots = new ArrayList<Node>();
	private ArrayList<Image> robotMoving = new ArrayList<Image>();
	private ArrayList<Node> heroBulletsRight = new ArrayList<Node>();
	private ArrayList<Node> heroBulletsLeft = new ArrayList<Node>();
	private ArrayList<Image> iddleLeft = new ArrayList<Image>();
	private ArrayList<Image> iddleRight = new ArrayList<Image>();
	private ArrayList<Image> runningLeft = new ArrayList<Image>();
	private ArrayList<Image> runningRight = new ArrayList<Image>();
	private ArrayList<Image> crouchingRight = new ArrayList<Image>();
	private ArrayList<Image> crouchingLeft = new ArrayList<Image>();
	private ArrayList<Image> dyingRight = new ArrayList<Image>();
	private ArrayList<Image> dyingLeft = new ArrayList<Image>();
	private ArrayList<Image> fireStandingLeft = new ArrayList<Image>();
	private ArrayList<Image> fireStandingRight = new ArrayList<Image>();
	private ArrayList<Image> fireUpLeft = new ArrayList<Image>();
	private ArrayList<Image> fireUpRight = new ArrayList<Image>();
	private ArrayList<Image> fireCrouchingRight = new ArrayList<Image>();
	private ArrayList<Image> fireCrouchingLeft = new ArrayList<Image>();
	private double centerHeroX;
	private double centerHeroY;

	@SuppressWarnings("deprecation")
	public void setGame(Scene scene, Game game, Player player) {

		hero = new Hero(heroImageView.getLayoutX(), heroImageView.getLayoutY(), heroImageView.getFitHeight());
		this.game = game;
		this.player = player;
		System.out.println(player.getName());
		for (int i = 0; i < 15; i++) {
			game.addRobot(new Robot(1100, 585, i));
		}
		addSpriteImages();
		this.scene = scene;
		width = scene.getWidth();
		height = scene.getHeight();
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				switch (event.getCode()) {

				case RIGHT:
					setHeroMoving(true);
					setHeroDirection(Hero.RIGHT);
					break;

				case LEFT:
					setHeroMoving(true);
					setHeroDirection(Hero.LEFT);
					break;

				case UP:
					setHeroAimingUp(true);
					break;

				case DOWN:
					setHeroCrouching(true);
					hero.setPosY(585);
					break;

				case A:
					if (!hero.isShooting() && !hero.isDead()) {
						hero.setShooting(true);
						ImageView orangeBullet = new ImageView(new Image(ORANGE_BULLET_ROUTE));
						Node newOrangeBullet = orangeBullet;
						if (hero.getDirection() == Hero.RIGHT) {
							newOrangeBullet.relocate(
									heroImageView.getLayoutX() + heroImageView.getBoundsInLocal().getWidth(),
									heroImageView.getLayoutY());
							heroBulletsRight.add(newOrangeBullet);
						} else if (hero.getDirection() == Hero.LEFT) {
							newOrangeBullet.relocate(
									heroImageView.getLayoutX() - heroImageView.getBoundsInLocal().getWidth(),
									heroImageView.getLayoutY());
							heroBulletsLeft.add(newOrangeBullet);
						}

						anchorPane.getChildren().add(newOrangeBullet);
					}
					break;

				default:
					break;

				}

			}

		});
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {

				case RIGHT:
					hero.setMoving(false);
					break;
				case LEFT:
					hero.setMoving(false);
					break;
				case DOWN:
					hero.setCrouching(false);
					hero.setPosY(560);
					break;
				case A:
					hero.setShooting(false);
					break;
				case D:
					hero.setDying(true);
					break;
				default:
					break;

				}

			}

		});
		startThreads();

		AnimationTimer timer = new AnimationTimer() {

			@Override
			public void handle(long now) {

				heroShootRight();
				heroShootLeft();
				robotCounter++;

				if (robotCounter % modifier == 0 && robotNum < 15) {
					Node robot = new ImageView("file:data/sprites/mini robot/mini-robot_1.png");
					robot.relocate(1100, 590);
					robots.add(robot);
					anchorPane.getChildren().add(robot);
					robotNum++;
				}
				moveRobot();
				checkBulletHit();
				checkHeroHit();

			}
		};
		timer.start();

	}

	public void heroShootRight() {

		for (int i = 0; i < heroBulletsRight.size(); i++) {
			Node bullet = heroBulletsRight.get(i);
			if (bullet.getLayoutX() < 1200 && bullet.getLayoutX() > 0 && bullet.getLayoutY() < 700
					&& bullet.getLayoutY() > 0) {
				bullet.relocate(bullet.getLayoutX() + BULLET_SPEED, bullet.getLayoutY());
			} else {
				heroBulletsRight.remove(i);
				anchorPane.getChildren().remove(bullet);
			}

		}

	}

	public void heroShootLeft() {

		for (int i = 0; i < heroBulletsLeft.size(); i++) {
			Node bullet = heroBulletsLeft.get(i);
			if (bullet.getLayoutX() < 1200 && bullet.getLayoutX() > 0 && bullet.getLayoutY() < 700
					&& bullet.getLayoutY() > 0) {
				bullet.relocate(bullet.getLayoutX() - BULLET_SPEED, bullet.getLayoutY());
			} else {
				heroBulletsLeft.remove(i);
				anchorPane.getChildren().remove(bullet);
			}
		}

	}

	public void moveRobot() {

		for (int i = 0; i < robots.size() && !hero.isDead(); i++) {

			if (robots.get(i).getLayoutX() < 1200 && robots.get(i).getLayoutX() > -50) {

				robots.get(i).relocate(robots.get(i).getLayoutX() - ROBOT_SPEED, robots.get(i).getLayoutY());

			} else {
				anchorPane.getChildren().remove(robots.get(i));
				robots.remove(i);
			}

		}

	}

	public void checkBulletHit() {

		try {

			for (int i = 0; i < heroBulletsRight.size(); i++) {

				for (int j = 0; j < robots.size(); j++) {

					if (heroBulletsRight.get(i).getBoundsInParent().intersects(robots.get(j).getBoundsInParent())) {
						anchorPane.getChildren().remove(robots.get(j));
						robots.remove(j);
						anchorPane.getChildren().remove(heroBulletsRight.get(i));
						heroBulletsRight.remove(i);
						player.robotKilled();
					}

				}

			}
			for (int i = 0; i < heroBulletsLeft.size(); i++) {

				for (int j = 0; j < robots.size(); j++) {

					if (heroBulletsLeft.get(i).getBoundsInParent().intersects(robots.get(j).getBoundsInParent())) {
						anchorPane.getChildren().remove(robots.get(j));
						robots.remove(j);
						anchorPane.getChildren().remove(heroBulletsLeft.get(i));
						heroBulletsLeft.remove(i);
						player.robotKilled();
					}

				}

			}
		} catch (Exception e) {

		}

	}

	public void checkHeroHit() {

		for (int i = 0; i < robots.size(); i++) {
			if (heroImageView.getBoundsInParent().intersects(robots.get(i).getBoundsInParent())
					&& !hero.isTakingDamage()) {
				hero.setHealth(hero.getHealth() - 1);
				hero.setTakingDamage(true);
			}
		}

	}

	public void setHeroDying(boolean dying) {
		hero.setDying(dying);
	}

	public boolean getHeroDying() {
		return hero.getDying();
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

	}

	public void setHeroY(double y) {

		heroImageView.setLayoutY(y);

	}

	public void startThreads() {

		heroThread = new HeroThread(this, hero, game);
		heroThread.start();
		viewThread = new ViewThread(this, hero);
		viewThread.start();
		rThread = new RobotThread(robotMoving, robots, this);
		rThread.start();

	}

	public void setHeroImage(String img) {

		heroImageView.setImage(new Image(img));

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
		for (int i = 0; i < 5; i++) {
			crouchingRight.add(new Image("file:data/sprites/hero/Crouch/right/crouch" + (i + 1) + "D.png"));
			crouchingLeft.add(new Image("file:data/sprites/hero/Crouch/left/crouch" + (i + 1) + "I.png"));
		}
		for (int i = 0; i < 4; i++) {
			dyingRight.add(new Image("file:data/sprites/hero/Dead/right/dead" + (i + 1) + "D.png"));
			dyingLeft.add(new Image("file:data/sprites/hero/Dead/left/dead" + (i + 1) + "I.png"));
		}
		for (int i = 0; i < 4; i++) {
			fireStandingRight.add(new Image("file:data/sprites/hero/Shoot/fireStandingRight/fire" + (i + 1) + "D.png"));
			fireStandingLeft.add(new Image("file:data/sprites/hero/Shoot/fireStandingLeft/fire" + (i + 1) + "I.png"));
		}
		for (int i = 0; i < 3; i++) {
			fireCrouchingRight
					.add(new Image("file:data/sprites/hero/Shoot/crouchFireRight/CrouchFire" + (i + 1) + "D.png"));
			fireCrouchingLeft
					.add(new Image("file:data/sprites/hero/Shoot/crouchFireLeft/CrouchFire" + (i + 1) + "I.png"));
		}
		for (int i = 0; i < 16; i++) {
			robotMoving.add(new Image("file:data/sprites/mini robot/mini-robot_" + (i + 1) + ".png"));
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

	public Image getDyingRightImage(int i) {
		return dyingRight.get(i);
	}

	public Image getDyingLeftImage(int i) {
		return dyingLeft.get(i);
	}

	public Image getFireStandingRightImage(int i) {
		return fireStandingRight.get(i);
	}

	public Image getFireStandingLeftImage(int i) {
		return fireStandingLeft.get(i);
	}

	public Image getFireCrouchingRightImage(int i) {
		return fireCrouchingRight.get(i);
	}

	public Image getFireCrouchingLeftImage(int i) {
		return fireCrouchingLeft.get(i);
	}

	public Image getRobotMoving(int i) {
		return robotMoving.get(i);
	}

	public boolean getHeroIsDead() {
		return hero.isDead();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		centerHeroX = heroImageView.getBoundsInLocal().getWidth() / 2;
		centerHeroY = heroImageView.getBoundsInLocal().getHeight() / 2;

	}

	public void setHeroCrouching(boolean b) {

		hero.setCrouching(b);

	}

	public void setHeroAimingUp(boolean b) {

		hero.setAimingUp(b);

	}

}
