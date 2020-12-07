package home.vladislavg123.androidexam;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.ArrayList;

import io.reactivex.CompletableSource;

public class ChatActivity extends AppCompatActivity {

    private EditText editTxt;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private String username;
    private int messagesCount;
    private HubConnection hubConnection;
    private String hubUrl = "https://bazarjok.xyz/chat";

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle arguments = getIntent().getExtras();
        username = arguments.get("username").toString();

        ((TextView)findViewById(R.id.username)).setText(username);

        messagesCount = 0;
        editTxt = (EditText) findViewById(R.id.message);
        list = (ListView) findViewById(R.id.messages);
        arrayList = new ArrayList<>();

        adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, arrayList);

        list.setAdapter(adapter);


        // SignalR hub initialisation

        hubConnection = HubConnectionBuilder.create(hubUrl).build();

        hubConnection.on("ReceiveMessage", (user, message) -> {
            ChatActivity.this.runOnUiThread(new Runnable() {@Override public void run()
            {
                arrayList.add(user + ": " + message);
                adapter.notifyDataSetChanged();
            }});
        }, String.class, String.class);

        hubConnection.on("GetAll", result -> {
            ChatActivity.this.runOnUiThread(new Runnable() {@Override public void run()
            {
                arrayList.add("Chat members: " + result);
                adapter.notifyDataSetChanged();
            }});
        }, String.class);

        hubConnection.start();


         // Button handlers

        findViewById(R.id.sendButton).setOnClickListener(view -> {
            if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                Toast.makeText(ChatActivity.this, "No connection",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            hubConnection.send("SendMessage", username, editTxt.getText().toString());
            messagesCount++;
       });

        findViewById(R.id.connectButton).setOnClickListener(v -> {
            if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                hubConnection.start();
            } else {
                Toast.makeText(ChatActivity.this, "It is already connected",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.showMembersButton).setOnClickListener(v -> {
            if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                Toast.makeText(ChatActivity.this, "No connection",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (messagesCount == 0) {
                Toast.makeText(ChatActivity.this, "You should send at list one message, to get chat members ",
                        Toast.LENGTH_LONG).show();
                return;
            }
            hubConnection.send("GetAll");

        });
    }
}