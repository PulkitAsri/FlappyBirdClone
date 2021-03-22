package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;

import java.util.Random;
import java.util.zip.DeflaterOutputStream;

import javax.swing.plaf.TextUI;

public class FlappyBird extends ApplicationAdapter {
	private static final int JUMP_UP_VELOCITY = -30;

	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	BitmapFont font;
	Random random;
	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture gameOver;
	Texture tapPlayAgain;
	int gameState=0;

	float gap=450;

	int score=0;
	int scoringTube=0;

	Texture[] birds;
	int flapRenderCount=0;
	int flapState=0;
	float birdY=0;
	Circle birdCircle;

	Rectangle[] topTubeRec;
	Rectangle[] bottomTubeRec;

	float velocity=0;
	float gravity=2;

	float tubeVelocity=4;

	int numberOfTubes=4;
	float[] tubeX=new float[numberOfTubes];
	float[] tubeOffset=new float[numberOfTubes];
	float distanceBetweenTubes;


	float maxTubeOffset;


	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gameOver=new Texture("gameover.png");
		tapPlayAgain=new Texture("taptoplayagain.png");
		background=new Texture("bg.png");
		birds=new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");


		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);



		topTube=new Texture("toptube.png");
		bottomTube=new Texture("bottomtube.png");

		distanceBetweenTubes=Gdx.graphics.getWidth()*3/4;

		maxTubeOffset=Gdx.graphics.getHeight()/2-gap/2-200;
		random=new Random();


		shapeRenderer=new ShapeRenderer();
		birdCircle=new Circle();
		topTubeRec=new Rectangle[numberOfTubes];
		bottomTubeRec=new Rectangle[numberOfTubes];

		startGame();




	}
	public void startGame(){
		birdY=Gdx.graphics.getHeight()/2-birds[0].getHeight()/2;
		for (int i=0;i<numberOfTubes;i++){
			tubeOffset[i]=(random.nextFloat()-0.5f)*(maxTubeOffset);
			tubeX[i]=Gdx.graphics.getWidth()*3/2-topTube.getWidth()/2+i*distanceBetweenTubes;
			topTubeRec[i]=new Rectangle();
			bottomTubeRec[i]=new Rectangle();

		}
	}
	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());


		if(gameState==1){

			if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2){
				score++;
				Gdx.app.log("Your score",""+score);
				if (scoringTube<numberOfTubes-1) scoringTube++;
				else scoringTube=0;
			}
			if(Gdx.input.justTouched()){
				velocity=JUMP_UP_VELOCITY;
			}
			for (int i=0;i<numberOfTubes;i++){
				if(tubeX[i]<0-topTube.getWidth()){
					tubeX[i]+=numberOfTubes*distanceBetweenTubes;
					tubeOffset[i]=(random.nextFloat()-0.5f)*(maxTubeOffset);
				}
				else{
					tubeX[i]-=tubeVelocity;

				}

				batch.draw(topTube,tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i]);
				batch.draw(bottomTube,tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i]);

				topTubeRec[i]=new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
				bottomTubeRec[i]=new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());

			}

			if(birdY>0 ){
				velocity+=gravity;
				birdY-=velocity;
			}else gameState=2;
		}
		else if(gameState==0){
			if(Gdx.input.justTouched()){
				gameState=1;
			}

		}else if(gameState==2){
			batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,
					Gdx.graphics.getHeight()/2-gameOver.getHeight()/2,
					(int)(0.9*Gdx.graphics.getWidth()),
					(int)(0.16*Gdx.graphics.getHeight()));
			batch.draw(tapPlayAgain,Gdx.graphics.getWidth()/2-tapPlayAgain.getWidth()/2,
					Gdx.graphics.getHeight()/2-tapPlayAgain.getHeight()/2-gameOver.getHeight());
			if(Gdx.input.justTouched()){
				gameState=1;
				startGame();
				score=0;
				scoringTube=0;
				velocity=JUMP_UP_VELOCITY;
			}
		}

		if(flapRenderCount<5) flapRenderCount++;
		else {
			flapRenderCount=0;
			if(flapState==0) flapState=1;
			else flapState=0;
		}

		batch.draw(birds[flapState],Gdx.graphics.getWidth()/2-birds[flapState].getWidth()/2,birdY);

		font.draw(batch,""+ score,100,200);


		birdCircle.set(Gdx.graphics.getWidth()/2,birdY + birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);

//		shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

		for (int i=0;i<numberOfTubes;i++){
//			shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
//			shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle,topTubeRec[i])|| Intersector.overlaps(birdCircle,bottomTubeRec[i])){
				Gdx.app.log("COLLISION!!","occured");
				gameState=2;
			}
		}
//		shapeRenderer.end();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
