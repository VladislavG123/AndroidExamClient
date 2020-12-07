package home.vladislavg123.androidexam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(v -> {
            String username = ((EditText)findViewById(R.id.username)).getText().toString();

            if (username == null || username.length() < 1){
                username = "User-" + UUID.randomUUID().toString().subSequence(0, 8);
            }

            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);

        });
    }
}