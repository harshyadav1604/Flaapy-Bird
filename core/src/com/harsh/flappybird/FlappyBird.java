package com.harsh.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;



import java.util.Random;

import jdk.internal.org.jline.utils.Log;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture background2;

	Texture gameover;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float vel = 0;  // velocity
	Circle birdCircle;
	int score = 0;
	int highscore = 0;
	int scoringTube = 0;
	BitmapFont font;
	BitmapFont font2;
	int lifeLineCnt = 0;

	int gameState = 0;  // initially game's situation : Game is not yet started
	float gravity = 1;//2

	Texture topTube;
	Texture bottomTube;
	Texture heart;
	float gap = 800;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVel = 3;
	int tubes = 4;   // tubes variable means no of tubes
	float[] tubeX = new float[tubes]; // it contains the x-position of each tubes
	float[] tubeOffset = new float[tubes]; // it determine the value of each tube pairs by shifting it up or down which helps
	// in maintaining the gap between the tubes

	float heartX;
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	Rectangle rectangleHeart;

	Music forwardMusic;
	Sound jumpSound;
	Sound gameOverSound;
	Sound scoreSound;
	Sound lifelineSound;
	int randYDist;
	int max = 200;
	int min = -200;
	Random yDist = new Random();








	@Override
	public void create () {    // This function creates the graphics/ background of the game
		System.out.println("Create function called!!!");

		batch = new SpriteBatch();
		background = new Texture("background.png");
		background2 = new Texture("background3.png");
		gameover = new Texture("gameover.png");
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		font = new BitmapFont();
		font2 = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5);
		font2.setColor(Color.GREEN);
		font2.getData().setScale(5);

		birds = new Texture[2];
		birds[0] = new Texture("flappybirdup.png");
		birds[1] = new Texture("flappybirddown.png");


		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		heart = new Texture("heart.png");


		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 90/100;
		topTubeRectangles = new Rectangle[tubes];
		bottomTubeRectangles = new Rectangle[tubes];




		System.out.println("PMD99 : Ht of screen"+Gdx.graphics.getHeight());
		System.out.println("PMD99 : Wd of screen"+Gdx.graphics.getWidth());
		System.out.println("PMD99 : distanceBetweenTubes"+distanceBetweenTubes);
		System.out.println("PMD99 : maxTubeOffset"+maxTubeOffset);

		forwardMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/flappy_music.ogg"));
		jumpSound =  Gdx.audio.newSound(Gdx.files.internal("audio/sounds/jump.wav"));
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("audio/sounds/gameover.wav"));
		scoreSound = Gdx.audio.newSound(Gdx.files.internal("audio/sounds/points.wav"));
		lifelineSound = Gdx.audio.newSound(Gdx.files.internal("audio/sounds/lifeline.wav"));


		startGame();





	}

	public void startGame() {  // this function is called from the create method. Here it sets the bird and tubes position



		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		for (int i = 0; i < tubes; i++) {

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;


			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();



		}

		heartX = tubeX[tubes-1]+heart.getWidth()+200;
		rectangleHeart = new Rectangle();

		for(int i=0; i< tubes-1; i++){
			System.out.println("PMD99 : i = "+ i + " " + tubeOffset[i] + " "+ tubeX[i]+ " "+ topTubeRectangles[i]+ " "+ bottomTubeRectangles[i]);

		}

	}

	@Override
	public void render () {  // it is called continously when the game is open on andriod mobile phones.


		System.out.println("Render function called!!!");

		batch.begin();

		if(score>=10){
			batch.draw(background2, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		else{
			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}



		if (gameState == 1) {   // if user is still playing

			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 5) {

				score++;



				scoreSound.play();

				Gdx.app.log("Score", String.valueOf(score));

				if (scoringTube < tubes - 1) {

					scoringTube++;

				} else {

					scoringTube = 0;

				}

			}

			if (Gdx.input.justTouched()) {

				vel = -17;
				jumpSound.play();

			}

			for (int i = 0; i < tubes; i++) {

				if (tubeX[i] < - topTube.getWidth()) {

					tubeX[i] += tubes * distanceBetweenTubes + heart.getWidth() + 200;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);



				} else {

					tubeX[i] = tubeX[i] - tubeVel;

					if(i == tubes-1) {
						heartX = heartX - tubeVel;
					}
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			}

			if(Intersector.overlaps(birdCircle, rectangleHeart)){
				lifelineSound.play();

				lifeLineCnt++;

				heartX = heartX + tubeX[tubes-1]+heart.getWidth()+200;
				randYDist = yDist.nextInt(Gdx.graphics.getHeight()-heart.getHeight()-200) + 200;
			}


			batch.draw(heart, heartX, randYDist);
			rectangleHeart = new Rectangle(heartX, randYDist, heart.getWidth(), heart.getHeight());


			//heartX = heartX + tubeX[3];

			if (birdY > 0) { // if bird's Y position> 0 which means still in the air

				vel = vel + gravity;
				birdY -= vel;

			} else {

				if(lifeLineCnt > 0){
					lifeLineCnt--;
					gameState = 1;
					startGame();
					score = score;
					scoringTube = scoringTube;
					vel = vel;
				}
				else{
					gameState = 3;
					forwardMusic.stop();
					gameOverSound.play();
				}


			}

		} else if (gameState == 0) {  //if user started playing the game

			if (Gdx.input.justTouched()) {

				gameState = 1;
				forwardMusic.setLooping(true);
				forwardMusic.play();


			}

		} else if (gameState == 2) {   // it happens when the bird collides with a tube or fell down

			forwardMusic.stop();
			gameOverSound.play();
			gameState = 3;






		} else if (gameState == 3) {   // here gameover is shown and user may restart the game by tapping it



			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

			if (Gdx.input.justTouched()) {
				forwardMusic.play();
				gameState = 1;
				if(highscore<score){
					highscore = score;
					FileHandle file = Gdx.files.local("highscore.txt"); // highest score is saved in the file
					try {
						file.writeString(String.valueOf(highscore), false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				startGame();
				score = 0;
				scoringTube = 0;
				vel = 0;


			}

		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}



		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 5 - birds[flapState].getWidth() / 2, birdY);

		font.draw(batch,String.valueOf(score), 100, 200);

		font.draw(batch,"HS:"+String.valueOf(highscore), 850, 1830);



		FileHandle file1 = Gdx.files.local("highscore.txt");
		if (file1.exists()) {
			try {
				highscore = Integer.parseInt(file1.readString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}



		font2.draw(batch,"Life:"+String.valueOf(lifeLineCnt), 850, 1980);


		birdCircle.set(Gdx.graphics.getWidth() / 5, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);



		if(gameState != 3)
		{
			for (int i = 0; i < tubes; i++) {


				if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
					// the above if condition says if the bird collides with tubes then it checks the lifeline

					if(lifeLineCnt > 0){  // if lifeline exist for user then lifeline will be subtracted by 1 and he/she should keep on tapping
						lifeLineCnt--;
						gameState = 1;
						startGame();
						score = score;
						scoringTube = scoringTube;
						vel = vel;
					}
					else{
						gameState = 2;
					}



				}






			}




		}

		batch.end();


	}



	@Override
	public void pause()
	{
		System.out.println("Pause function called!!!");
	}


}