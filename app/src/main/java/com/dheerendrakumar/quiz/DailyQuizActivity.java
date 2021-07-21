package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class DailyQuizActivity extends AppCompatActivity {

    Handler handler;
    TextView textView;
    TextView scoreTextview;
    Animator animator;
    LinearLayout linearLayout;
    LinearLayout mainLinearLayout;
    int score=0;
    int numberOfQuestions=10;
    int totalScore=0;

    ArrayList<String> icAnswers;
    ArrayList<String> questionTexts = new ArrayList<>();
    ArrayList<String> correctAnswer = new ArrayList<>();
    ArrayList<ArrayList<String>> incorrectAnswers = new ArrayList<>();

    String url = "https://opentdb.com/api.php?amount=10&type=boolean";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quiz);

        handler = new Handler();
        textView = findViewById(R.id.Dailytruefalsequestion);
        scoreTextview = findViewById(R.id.DailytruefalseScore);
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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {




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
                totalScore++;
                score++;
                scoreTextview.setText(score+" / "+numberOfQuestions);
                disableAllQuizButtons(false);
                btnGuess.setBackgroundColor(Color.GREEN);
                Toast.makeText(DailyQuizActivity.this, "right", Toast.LENGTH_SHORT).show();

                questionTexts.remove(0);
                icAnswers = new ArrayList<>();
                incorrectAnswers.remove(0);
                correctAnswer.remove(0);

                if(totalScore==numberOfQuestions) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DailyQuizActivity.this);
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
                questionTexts.remove(0);
                icAnswers = new ArrayList<>();
                incorrectAnswers.remove(0);
                correctAnswer.remove(0);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animateAnimalQuiz(true);
                    }
                }, 1000);

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

        textView.setText(questionTexts.get(0));
        icAnswers = incorrectAnswers.get(0);
        icAnswers.add(correctAnswer.get(0));
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