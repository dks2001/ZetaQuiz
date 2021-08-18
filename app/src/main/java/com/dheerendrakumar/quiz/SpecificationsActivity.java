package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpecificationsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    String[] numberOfQuestions = {"Number Of Questions","5","10","15","20"};
    String[] difficultyLevel = {"Difficulty Level","easy","medium","hard"};
    String[] type = {"Set Type","Multiple Choice","True/False"};
    Spinner spinner;
    Spinner dlSpiiner;
    Spinner toq;
    Button start;
    String typeFinal="";
    String url=null;
    ArrayList<String> questionTexts = new ArrayList<>();
    ArrayList<String> correctAnswer = new ArrayList<>();
    ArrayList<ArrayList<String>> incorrectAnswers = new ArrayList<>();
    Handler handler;
    String categoryCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specifications);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = findViewById(R.id.QuestionNumberSpinner);
         dlSpiiner = findViewById(R.id.dlSpinner);
         toq = findViewById(R.id.typeSpinner);
         start = findViewById(R.id.startButton);
         handler = new Handler();

         Intent intent = getIntent();
         categoryCode = String.valueOf(intent.getIntExtra("categorycode",15));

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setY(-1000);
        imageView.animate().translationY(0).setDuration(1000).alpha(1);

        spinner.setOnItemSelectedListener(SpecificationsActivity.this);
        dlSpiiner.setOnItemSelectedListener(SpecificationsActivity.this);
        toq.setOnItemSelectedListener(SpecificationsActivity.this);

        ArrayAdapter noq = new ArrayAdapter(this, android.R.layout.simple_spinner_item,numberOfQuestions);
        noq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(noq);

        ArrayAdapter dl = new ArrayAdapter(this, android.R.layout.simple_spinner_item,difficultyLevel);
        dl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dlSpiiner.setAdapter(dl);

        ArrayAdapter typeOfQuestion = new ArrayAdapter(this, android.R.layout.simple_spinner_item,type);
        typeOfQuestion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toq.setAdapter(typeOfQuestion);


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpecificationsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String numberOfQuestions = spinner.getSelectedItem().toString();
                String difficultyLevel = dlSpiiner.getSelectedItem().toString();
                String type = toq.getSelectedItem().toString();

                if(numberOfQuestions.equals("Number Of Questions")) {
                    Toast.makeText(SpecificationsActivity.this, "please specify number of questions", Toast.LENGTH_SHORT).show();

                } else if(difficultyLevel.equals("Difficulty Level")) {
                    Toast.makeText(SpecificationsActivity.this, "please specify difficulty level", Toast.LENGTH_SHORT).show();

                } else if(type.equals("Set Type")) {
                    Toast.makeText(SpecificationsActivity.this, "please specify type", Toast.LENGTH_SHORT).show();
                } else {


                    ProgressDialog progress = new ProgressDialog(SpecificationsActivity.this);
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();

                    if (type.equals("Multiple Choice")) {
                        typeFinal = "multiple";
                    } else if (type.equals("True/False")) {
                        typeFinal = "boolean";
                    }

                    url = "https://opentdb.com/api.php?amount=" + Integer.parseInt(numberOfQuestions) + "&category=" + categoryCode + "&difficulty=" + difficultyLevel + "&type=" + typeFinal + "";

                    if (isNetworkAvailable()) {
                        getData(url);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (questionTexts.size() == 0) {
                                    Toast.makeText(SpecificationsActivity.this, "Questions not available.Please change your specifications.", Toast.LENGTH_LONG).show();
                                    progress.dismiss();
                                } else if (type.equals("Multiple Choice")) {
                                    //Toast.makeText(SpecificationsActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SpecificationsActivity.this, QuizActivity.class);
                                    intent.putStringArrayListExtra("questionTexts", questionTexts);
                                    intent.putStringArrayListExtra("correctAnswer", correctAnswer);
                                    Bundle args = new Bundle();
                                    args.putSerializable("ARRAYLIST", (Serializable) incorrectAnswers);
                                    intent.putExtra("BUNDLE", args);
                                    intent.putExtra("numberOfQuestions", numberOfQuestions);
                                    startActivity(intent);
                                    progress.dismiss();
                                } else {

                                    Toast.makeText(SpecificationsActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SpecificationsActivity.this, TrueFalseActivity.class);
                                    intent.putStringArrayListExtra("questionTexts", questionTexts);
                                    intent.putStringArrayListExtra("correctAnswer", correctAnswer);
                                    Bundle args = new Bundle();
                                    args.putSerializable("ARRAYLIST", (Serializable) incorrectAnswers);
                                    intent.putExtra("BUNDLE", args);
                                    intent.putExtra("numberOfQuestions", numberOfQuestions);
                                    startActivity(intent);
                                    progress.dismiss();

                                }
                            }
                        }, 2000);

                    } else {
                        Toast.makeText(SpecificationsActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.QuestionNumberSpinner:
                //Toast.makeText(this, numberOfQuestions[position], Toast.LENGTH_SHORT).show();
                break;
            case R.id.dlSpinner:
                //Toast.makeText(this, difficultyLevel[position], Toast.LENGTH_SHORT).show();
                break;
            case R.id.typeSpinner:
                //Toast.makeText(this, type[position], Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }




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

                       // Log.i("question",questionText);
                        //Log.i("answer",questionAnswer);

                        ArrayList<String> ica = new ArrayList<>();

                        for(int j=0;j<arr.length;j++) {
                            //Log.i("op",arr[j]);
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

}