package com.example.android.translation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


public class MainActivity extends AppCompatActivity {
    private Spinner fromSpinner,toSpinner;
    private TextInputEditText sourceEat;
    private MaterialButton translateBtn;
    private TextView translatedTV;
    String[] fromLanguages = {"From","Hindi","English" };
    String[] toLanguages = {"To","English","Hindi" };
    int languageCode,fromLanguageCode,toLanguageCode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);
        sourceEat = findViewById(R.id.idEatSource);
        translateBtn = findViewById(R.id.idBtnTranslate);
        translatedTV = findViewById(R.id.idTVTranslate);
        fromSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);
            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                toLanguageCode = getLanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translatedTV.setText("");
                if (sourceEat.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode == 0) {
                    Toast.makeText(MainActivity.this, "Please Select source language", Toast.LENGTH_SHORT).show();
                } else if (toLanguageCode == 0) {
                    Toast.makeText(MainActivity.this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
                } else {
                       translateText(fromLanguageCode,toLanguageCode,sourceEat.getText().toString());
                }
            }
        });
    }
private void translateText(int fromLanguageCode,int tolanguageCode,String source)
        {
          translatedTV.setText("Downloading Model...");
          FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                  .setSourceLanguage(fromLanguageCode).setTargetLanguage(tolanguageCode).build();
          FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
          FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
          translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>()
            {
              public void onSuccess(Void unused)
              {
                 translatedTV.setText("Translating...");
                 translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                     public void onSuccess(String s) {
                         translatedTV.setText(s);
                     }
                 }).addOnFailureListener(new OnFailureListener(){
                     public void onFailure(@NonNull Exception e){
                         Toast.makeText(MainActivity.this,"Fail to translate :"+e.getMessage(),Toast.LENGTH_SHORT).show();
                     }

                 });
                 }
        }).addOnFailureListener(new OnFailureListener()
          {
              public void onFailure(@NonNull Exception e)
              {
                  Toast.makeText(MainActivity.this,"Fail to download language Model"+e.getMessage(), Toast.LENGTH_SHORT).show();
              }
          });
        }




    public int getLanguageCode(String language)
    {
        int languageCode = 0;
        switch(language)
        {
            case "English":
                languageCode= FirebaseTranslateLanguage.EN;
                break;
            case "Hindi":
                languageCode=FirebaseTranslateLanguage.HI;
                break;
            default:
                languageCode=0;

        }
        return languageCode;
    }
}