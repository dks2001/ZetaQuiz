package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

public class TrueFalseActivity extends AppCompatActivity {

    PieChart pieChart;
    ArrayList<String> questions;
    ArrayList<String> correctAnswers;
    ArrayList<ArrayList<String>> incorrectAnswers;
    ArrayList<String> icAnswers;
    LinearLayout linearLayout;
    LinearLayout mainLinearLayout;
    int score=0;
    int numberOfQuestions=0;
    int qn=1;

    Handler handler;
    TextView textView;
    TextView scoreTextview;
    TextView questionNumber;
    Animator animator;
    int totalScore=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false);

        ProgressDialog progress = new ProgressDialog(TrueFalseActivity.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        handler = new Handler();
        textView = findViewById(R.id.Dailytruefalsequestion);
        linearLayout = findViewById(R.id.DailytruefalseLinearLayout);
        mainLinearLayout = findViewById(R.id.DailymainTFLinearLayout);
        scoreTextview = findViewById(R.id.score);
        questionNumber = findViewById(R.id.questionNumber);


        Intent intent = getIntent();
        questions = intent.getStringArrayListExtra("questionTexts");
        correctAnswers = intent.getStringArrayListExtra("correctAnswer");
        Bundle args = intent.getBundleExtra("BUNDLE");
        numberOfQuestions = Integer.valueOf(intent.getStringExtra("numberOfQuestions"));
        incorrectAnswers = (ArrayList<ArrayList<String>>) args.getSerializable("ARRAYLIST");


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                questionNumber.setText("Question Number : "+qn);
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
                progress.dismiss();
            }
        },3000);


    }

    View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            Button btnGuess = ((Button) view);
            String guessValue = btnGuess.getText().toString();

            if(guessValue.equals(correctAnswers.get(0))) {
                totalScore++;
                score++;
                scoreTextview.setText("Score : "+score+" / "+numberOfQuestions);
                disableAllQuizButtons(false);
                btnGuess.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.right_button,null));
                Toast.makeText(TrueFalseActivity.this, "right", Toast.LENGTH_SHORT).show();

                questions.remove(0);
                icAnswers = new ArrayList<>();
                incorrectAnswers.remove(0);
                correctAnswers.remove(0);

                if(totalScore==numberOfQuestions) {

                    showPopup(view);

                }
                else{
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateAnimalQuiz(true);
                        }
                    }, 1000);
                }
            } else {

                for(int j=0;j<linearLayout.getChildCount();j++) {
                    Button btn = (Button) linearLayout.getChildAt(j);
                    if(btn.getText().toString().equals(correctAnswers.get(0))) {
                        btn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.right_button,null));
                    }
                }

                totalScore++;
                btnGuess.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.wrong_button,null));
                disableAllQuizButtons(false);


                if(totalScore==numberOfQuestions) {
                    showPopup(view);
                } else {

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            questions.remove(0);
                            icAnswers = new ArrayList<>();
                            incorrectAnswers.remove(0);
                            correctAnswers.remove(0);
                            animateAnimalQuiz(true);
                        }
                    }, 1000);
                }
            }
        }
    };


    private void animateAnimalQuiz(boolean animateQuiz) {

        int xTopLeft = 0;
        int yTopLeft = 0;

        int xBottomRight = mainLinearLayout.getLeft() + mainLinearLayout.getRight();
        int yBottomRight = mainLinearLayout.getTop() + mainLinearLayout.getBottom();

        int radius = Math.max(mainLinearLayout.getWidth(),mainLinearLayout.getHeight());


        if(animateQuiz) {

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

        qn++;
        questionNumber.setText("Question Number : "+qn);
        textView.setText(questions.get(0));
        icAnswers = incorrectAnswers.get(0);
        icAnswers.add(correctAnswers.get(0));
        Collections.shuffle(icAnswers);

        for(int i=0;i<linearLayout.getChildCount();i++) {
            Button btn = (Button) linearLayout.getChildAt(i);
            btn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.truefalsebutton_corner,null));
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

    public void showPopup(View view) {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_window, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = false; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                TextView totalQuestionPopup = (TextView)popupView.findViewById(R.id.totalQuestionPopup);
                totalQuestionPopup.setText("Total Question : "+numberOfQuestions);
                TextView attempted = (TextView) popupView.findViewById(R.id.attemptedPopup);
                attempted.setText("Attempted : "+numberOfQuestions);
                TextView correct = (TextView) popupView.findViewById(R.id.correctpopup);
                correct.setText("Correct : "+score);
                TextView incorrect = (TextView)popupView.findViewById(R.id.incorrectpopup);
                incorrect.setText("Incorrect : "+(numberOfQuestions-score));
                TextView scoree = (TextView) popupView.findViewById(R.id.scorepopup);
                scoree.setText("Score : "+score+" / "+numberOfQuestions);

                pieChart = (PieChart)popupView.findViewById(R.id.piechart);
                pieChart.addPieSlice(
                        new PieModel(
                                "Correct",
                                score,
                                Color.parseColor("#00FF00")));
                pieChart.addPieSlice(
                        new PieModel(
                                "Incorrect",
                                numberOfQuestions-score,
                                Color.parseColor("#FF0000")));

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                pieChart.startAnimation();

                Button finish = (Button) popupView.findViewById(R.id.finishpopup);
                finish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TrueFalseActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        },1000);

    }
}