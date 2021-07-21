package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

public class TrueFalseActivity extends AppCompatActivity {

    ArrayList<String> questions;
    ArrayList<String> correctAnswers;
    ArrayList<ArrayList<String>> incorrectAnswers;
    ArrayList<String> icAnswers;
    LinearLayout linearLayout;
    LinearLayout mainLinearLayout;
    SecureRandom secureRandom;
    int score=0;
    int numberOfQuestions=0;

    Handler handler;
    TextView textView;
    TextView scoreTextview;
    Animator animator;
    int totalScore=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false);

        textView = findViewById(R.id.Dailytruefalsequestion);
        linearLayout = findViewById(R.id.DailytruefalseLinearLayout);
        mainLinearLayout = findViewById(R.id.DailymainTFLinearLayout);
        scoreTextview = findViewById(R.id.DailytruefalseScore);
        secureRandom = new SecureRandom();
        handler = new Handler();

        Intent intent = getIntent();
        questions = intent.getStringArrayListExtra("questionTexts");
        correctAnswers = intent.getStringArrayListExtra("correctAnswer");
        Bundle args = intent.getBundleExtra("BUNDLE");
        numberOfQuestions = Integer.valueOf(intent.getStringExtra("numberOfQuestions"));
        incorrectAnswers = (ArrayList<ArrayList<String>>) args.getSerializable("ARRAYLIST");
        icAnswers = incorrectAnswers.get(0);
        icAnswers.add(correctAnswers.get(0));
        Collections.shuffle(icAnswers);

        textView.setText(questions.get(0));

         for(int j=0;j<linearLayout.getChildCount();j++) {
             Button icAns = (Button) linearLayout.getChildAt(j);
             if(icAns.getText().equals("")) {
                 icAns.setText(icAnswers.get(j));
             }
         }

         for(int i=0;i<linearLayout.getChildCount();i++) {
             Button btn = (Button) linearLayout.getChildAt(i);
             btn.setOnClickListener(btnGuessListener);
             btn.setTextSize(24);
         }

    }

    View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            Button btnGuess = ((Button) view);
            String guessValue = btnGuess.getText().toString();

            if(guessValue.equals(correctAnswers.get(0))) {
                totalScore++;
                score++;
                scoreTextview.setText(score+" / "+numberOfQuestions);
                disableAllQuizButtons(false);
                btnGuess.setBackgroundColor(Color.GREEN);
                Toast.makeText(TrueFalseActivity.this, "right", Toast.LENGTH_SHORT).show();

                questions.remove(0);
                icAnswers = new ArrayList<>();
                incorrectAnswers.remove(0);
                correctAnswers.remove(0);

                if(totalScore==numberOfQuestions) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(TrueFalseActivity.this);
                    builder.setCancelable(false);


                    builder.setMessage(String.valueOf(score) + "/" + numberOfQuestions);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.show();

                }
                else{
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateAnimalQuiz(true);
                        }
                    }, 1000);
                }
            } else {
                totalScore++;
                btnGuess.setBackgroundColor(Color.RED);
                disableAllQuizButtons(true);
                questions.remove(0);
                icAnswers = new ArrayList<>();
                incorrectAnswers.remove(0);
                correctAnswers.remove(0);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animateAnimalQuiz(true);
                    }
                }, 1000);
            }
        }
    };


    private void animateAnimalQuiz(boolean animateOutAnimalImage) {

        int xTopLeft = 0;
        int yTopLeft = 0;

        int xBottomRight = mainLinearLayout.getLeft() + mainLinearLayout.getRight();
        int yBottomRight = mainLinearLayout.getTop() + mainLinearLayout.getBottom();

        int radius = Math.max(mainLinearLayout.getWidth(),mainLinearLayout.getHeight());



        if(animateOutAnimalImage) {

            animator = ViewAnimationUtils.createCircularReveal(mainLinearLayout,xBottomRight,yBottomRight,radius,0);

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showNextQuestion();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {

            animator = ViewAnimationUtils.createCircularReveal(mainLinearLayout,xTopLeft,yTopLeft,0,radius);

        }
        animator.setDuration(700);
        animator.start();
    }

    public void showNextQuestion() {

        textView.setText(questions.get(0));
        icAnswers = incorrectAnswers.get(0);
        icAnswers.add(correctAnswers.get(0));
        Collections.shuffle(icAnswers);

        for(int i=0;i<linearLayout.getChildCount();i++) {
            Button btn = (Button) linearLayout.getChildAt(i);
            btn.setBackgroundColor(getColor(R.color.buttoncolor));
        }

        for(int j=0;j<linearLayout.getChildCount();j++) {
            Button icAns = (Button) linearLayout.getChildAt(j);
            if(!icAns.getText().equals("")) {
                icAns.setText(icAnswers.get(j));
            }
        }

        disableAllQuizButtons(true);
    }

    private void disableAllQuizButtons(boolean isEnable) {
        for(int i=0;i<linearLayout.getChildCount();i++) {
            Button btn = (Button) linearLayout.getChildAt(i);
            btn.setEnabled(isEnable);
        }
    }
}