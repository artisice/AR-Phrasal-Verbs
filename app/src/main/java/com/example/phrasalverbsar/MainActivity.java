package com.example.phrasalverbsar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

// Активити с отображением AR фрагмента

public class MainActivity extends AppCompatActivity {

    public static AnchorNode anchorNode;
    private static boolean ModelFlag;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ModelFlag = false;

        // Инициализация Firebase Cloud Storage

        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Объявление AR фрагмента

        ArFragment arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.arFragment);

        FloatingActionButton fab = findViewById(R.id.fabHideFab);

        // Объявление кнопки - перехода на Second Activity с ListView

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton listButton = findViewById(R.id.listButton);
        listButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
            fab.show();
        });

        // обработка нажатия Floating Action Button
        // Создание кеш файла и получение файла с облака

                fab.setOnClickListener(v -> {
                    if (!Objects.equals(SecondActivity.CurrentVerb, "")) {
                        try {
                            StorageReference modelRef = storage.getReference().child(SecondActivity.CurrentVerb);
                            File file = File.createTempFile(SecondActivity.CurrentName, "glb");

                            modelRef.getFile(file).addOnSuccessListener(taskSnapshot -> buildModel(file));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fab.hide();
                    }
                    else
                        Toast.makeText(this, "Выбери глагол", Toast.LENGTH_SHORT).show();
                });

        // Обработка нажатия на метку плоской поверхности
        // Создание якоря с моделью после рендеринга
        // Анимация модели
        // Добавление возможности крутить и масштабировать модель на сцене

        if (arFragment != null) {
            (arFragment).setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                    if (ModelFlag) {
                        if (fab.isOrWillBeHidden()) {
                            anchorNode = new AnchorNode(hitResult.createAnchor());
                            anchorNode.setParent(arFragment.getArSceneView().getScene());

                            TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
                            model.setParent(anchorNode);
                            model.setRenderable(this.renderable).animate(true).start();
                            model.select();
                            ModelFlag = false;
                        }
                    }
            });
        }

    }

    private ModelRenderable renderable;

    // Построение, рендеринг полученной модели с облака
    private void buildModel(File file) {

        ModelRenderable
                .builder()
                .setSource(this, Uri.parse(file.getPath()))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .setRegistryId(file.getPath())
                .build()
                .thenAccept(modelRenderable -> {
                    Toast.makeText(this, "Модель построена", Toast.LENGTH_SHORT).show();
                    renderable = modelRenderable;
                    ModelFlag = true;

                });
    }

}