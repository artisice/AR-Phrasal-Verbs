package com.example.phrasalverbsar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// Активити со списком глаголов

public class SecondActivity extends AppCompatActivity {

    // переменные, которые будут содержать данные о выбранном глаголе для построения модели
    public static String CurrentVerb = "";
    public static String CurrentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Phrasal verb list");

        // Объявление списка ListView, который содержит данные объекта Verbs из Firebase realtime database

        ListView VerbsList = findViewById(R.id.VerbList);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        VerbsList.setAdapter(adapter);

        // Получение данных для списка глаголов из базы данных
        // Добавление полученных элементов в список

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Verbs");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Обработка нажатия на элементы списка
        // Получение данных о глаголе из базы данных по позиции в списке (название файла модели)

        VerbsList.setOnItemClickListener((parent, v, position, id) -> {

            DatabaseReference VRef = FirebaseDatabase.getInstance().getReference().child("CurrentVerb").child(String.valueOf(position));
            DatabaseReference NRef = FirebaseDatabase.getInstance().getReference().child("CurrentName").child(String.valueOf(position));

            VRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CurrentVerb = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            NRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CurrentName = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            if (MainActivity.anchorNode != null) {
                MainActivity.anchorNode.setParent(null);
            }
            this.finish();
        });
    }

}