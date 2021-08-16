package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class DailyQuizActivity extends AppCompatActivity {

    PieChart pieChart;
    int qn=1;
    TextView textView;
    TextView scoreTextview;
    Animator animator;
    LinearLayout linearLayout;
    LinearLayout mainLinearLayout;
    int score=0;
    int numberOfQuestions=10;
    int totalScore=0;
    TextView questionNumber;

    ArrayList<String> icAnswers;
    ArrayList<String> questionTexts = new ArrayList<>();
    ArrayList<String> correctAnswer = new ArrayList<>();
    ArrayList<ArrayList<String>> incorrectAnswers = new ArrayList<>();

    String url = "https://opentdb.com/api.php?amount=10&type=boolean";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quiz);

        textView = findViewById(R.id.Dailytruefalsequestion);
        scoreTextview = findViewById(R.id.score);
        questionNumber = findViewById(R.id.questionNumber);
        linearLayout = findViewById(R.id.DailytruefalseLinearLayout);
        mainLinearLayout = findViewById(R.id.DailymainTFLinearLayout);

        ProgressDialog progress = new ProgressDialog(DailyQuizActivity.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        if(isNetworkAvailable()) {
            getData(url);
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                questionNumber.setText("Question Number : "+qn);
                scoreTextview.setText("Score : "+0);
                icAnswers = incorrectAnswers.get(0);
                icAnswers.add(correctAnswer.get(0));
                Collections.shuffle(icAnswers);

                textView.setText(questionTexts.get(0));

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
        },5000);


    }

    View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            Button btnGuess = ((Button) view);
            String guessValue = btnGuess.getText().toString();

            if(guessValue.equals(correctAnswer.get(0))) {
                btnGuess.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.right_button,null));
                totalScore++;
                score++;
                scoreTextview.setText("Score : "+score+" / "+numberOfQuestions);
                disableAllQuizButtons(false);


                Toast.makeText(DailyQuizActivity.this, "right", Toast.LENGTH_SHORT).show();


                questionTexts.remove(0);
                icAnswers = new ArrayList<>();
                incorrectAnswers.remove(0);
                correctAnswer.remove(0);

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
                    if(btn.getText().toString().equals(correctAnswer.get(0))) {
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
                            questionTexts.remove(0);
                            icAnswers = new ArrayList<>();
                            incorrectAnswers.remove(0);
                            correctAnswer.remove(0);
                            animateAnimalQuiz(true);
                        }
                    }, 1000);
                }

            }
        }
    };

    public void getData(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray results = response.getJSONArray("results");

                    for(int i=0;i<results.length();i++)
                    {
                        JSONObject questionjsonObject = results.getJSONObject(i);
                        String questionText = questionjsonObject.getString("question");
                        String questionAnswer = questionjsonObject.getString("correct_answer");
                        String incor = questionjsonObject.getString("incorrect_answers");

                        String s = incor.replace("[","");
                        String s2 = s.replace("]","");
                        String s3 = s2.replace("\"","");
                        String[] arr = s3.split(",");

                        String str = questionText.replace("&quot;","");
                        String str2 = str.replace("&#039;s","'s");

                        questionTexts.add(str2);
                        correctAnswer.add(questionAnswer);

                        Log.i("question",questionText);
                        Log.i("answer",questionAnswer);

                        ArrayList<String> ica = new ArrayList<>();

                        for(int j=0;j<arr.length;j++) {
                            Log.i("op",arr[j]);
                            ica.add(arr[j]);
                        }
                        incorrectAnswers.add(ica);

                    }
                } catch(Exception e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

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
        textView.setText(questionTexts.get(0));
        questionNumber.setText("Question Number : "+qn);
        icAnswers = incorrectAnswers.get(0);
        icAnswers.add(correctAnswer.get(0));
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
                totalQuestionPopup.setText("Total Question : "+10);
                TextView attempted = (TextView) popupView.findViewById(R.id.attemptedPopup);
                attempted.setText("Attempted : "+10);
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
                        popupWindow.dismiss();
                        Intent intent = new Intent(DailyQuizActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        },1000);



    }
}