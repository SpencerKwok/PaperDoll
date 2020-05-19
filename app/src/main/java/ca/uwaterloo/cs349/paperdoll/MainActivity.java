package ca.uwaterloo.cs349.paperdoll;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make window full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Add drawing view
        ViewGroup view_group = findViewById(R.id.main_region);
        view_group.addView(new PaperDollView(this, new PaperDollModel()));
    }
}