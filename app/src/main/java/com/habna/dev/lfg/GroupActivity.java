package com.habna.dev.lfg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.habna.dev.lfg.Models.Group;
import com.habna.dev.lfg.Models.GroupMessage;
import com.habna.dev.lfg.Models.GroupParticipant;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupActivity extends AppCompatActivity {

  public static Group group;
  public static boolean joined;

  private Set<GroupParticipant> participants;
  private ArrayAdapter<String> messagesAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    fetchParticipantInfo();
  }

  private void fetchParticipantInfo() {
    BackendlessDataQuery query = new BackendlessDataQuery();
    query.setWhereClause("groupId = '" + group.getObjectId() + "'");
    Backendless.Persistence.of(GroupParticipant.class).find(query,
      new AsyncCallback<BackendlessCollection<GroupParticipant>>() {
      @Override
      public void handleResponse(BackendlessCollection<GroupParticipant> response) {
        if (participants == null) {
          participants = new HashSet<>();
        }
        for (GroupParticipant participant : response.getCurrentPage())  {
          participants.add(participant);
        }
        fetchMessages();
      }

      @Override
      public void handleFault(BackendlessFault fault) {
        Log.e("ERROR", "While fetching participant info for group " +
          group.getObjectId() + " : " + fault.getMessage());
      }
    });
  }

  private void fetchMessages()  {
    messagesAdapter = new ArrayAdapter<>(GroupActivity.this, android.R.layout.simple_list_item_1,
      new ArrayList<String>());
    BackendlessDataQuery query = new BackendlessDataQuery();
    query.setWhereClause("groupId = '" + group.getObjectId() + "'");
    Backendless.Persistence.of(GroupMessage.class).find(query,
      new AsyncCallback<BackendlessCollection<GroupMessage>>() {
      @Override
      public void handleResponse(BackendlessCollection<GroupMessage> response) {
        for (GroupMessage groupMessage : response.getCurrentPage()) {
          messagesAdapter.add(getDisplayMessageString(groupMessage));
        }
        messagesAdapter.notifyDataSetChanged();
        loadTextViews();
      }

      @Override
      public void handleFault(BackendlessFault fault) {
        Log.e("ERROR", "While fetching group messages: " + fault.getMessage());
      }
    });
  }

  private void loadTextViews() {
    final TextView titleText = (TextView) findViewById(R.id.groupTitleTextView);
    titleText.setText(group.getTitle() + "\n");

    final TextView descText = (TextView) findViewById(R.id.groupDescTextView);
    descText.setText("Description: \n\t" + group.getDescription() + "\n");

    final TextView minText = (TextView) findViewById(R.id.groupMinTextView);
    if (group.getMinParticipants() == 0)  {
      minText.setVisibility(View.INVISIBLE);
    } else  {
      minText.setText("Min Required: " + String.valueOf(group.getMinParticipants() + "\t\t\t\t\t\t\t\t"));
    }

    final TextView maxText = (TextView) findViewById(R.id.groupMaxTextView);
    if (group.getMaxParticipants() == -1)  {
      maxText.setVisibility(View.INVISIBLE);
    } else  {
      maxText.setText("Max Required: " + String.valueOf(group.getMaxParticipants()));
    }

    final TextView totalParticipantsView = (TextView) findViewById(R.id.groupTotalParticipants);
    if (group.getMaxParticipants() != -1) {
      totalParticipantsView.setText(String.valueOf(participants.size()) + "/" +
        String.valueOf(group.getMaxParticipants()) + " participants\n");
    } else  {
      totalParticipantsView.setText(participants + " have joined!");
    }

    if (joined) {
      TextView messagesHeader = new TextView(GroupActivity.this);
      messagesHeader.setText("Messages");
      final ListView messagesText = (ListView) findViewById(R.id.groupMessagesText);
      messagesText.addHeaderView(messagesHeader);
      messagesText.setVisibility(View.VISIBLE);
      messagesText.setAdapter(messagesAdapter);
      final EditText addMessage = (EditText) findViewById(R.id.addMessageEditText);
      addMessage.setVisibility(View.VISIBLE);
      addMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
          if (EditorInfo.IME_ACTION_DONE == i)  {
            if (!TextUtils.isEmpty(addMessage.getText().toString())) {
              addMessageRec(addMessage.getText().toString());
            }
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(addMessage.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            addMessage.setText("");
            return true;
          }
          return false;
        }
      });
    }

    final Button joinButton = (Button) findViewById(R.id.joinButton);
    joinButton.setVisibility(joined ? View.INVISIBLE : View.VISIBLE);
    joinButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        joinGroup();
      }
    });
  }

  private void addMessageRec(final String message) {
    GroupMessage groupMessage = new GroupMessage(message, MainActivity.backendlessUser.getEmail(),
      group.getObjectId());
    Backendless.Persistence.save(groupMessage, new AsyncCallback<GroupMessage>() {
      @Override
      public void handleResponse(GroupMessage response) {
        messagesAdapter.add(getDisplayMessageString(response));
        messagesAdapter.notifyDataSetChanged();
      }

      @Override
      public void handleFault(BackendlessFault fault) {
        Log.e("ERROR", "While adding group message: " + fault.getMessage());
      }
    });
  }

  @NonNull
  private String getDisplayMessageString(GroupMessage response) {
    return response.getParticipant() + ": " + response.getMessage();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    if (id == R.id.action_logout) {
      MainActivity.doLogout();
      finish();
      startLoginActivity();
      return true;
    }
    if (id == android.R.id.home)  {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void joinGroup()  {
    GroupParticipant groupParticipant = new GroupParticipant(group.getObjectId(),
      MainActivity.backendlessUser.getEmail(), GroupParticipant.MEMBER);
    Backendless.Persistence.save(groupParticipant, new AsyncCallback<GroupParticipant>() {
      @Override
      public void handleResponse(GroupParticipant response) {
        Toast.makeText(GroupActivity.this, "Joined group " + group.getTitle(), Toast.LENGTH_SHORT).show();
        joined = true;
        finish();
        startActivity(new Intent(GroupActivity.this, GroupActivity.class));
      }

      @Override
      public void handleFault(BackendlessFault fault) {
        Log.e("ERROR", "During joinging group: " + fault.getMessage());
      }
    });
  }

  private void startLoginActivity() {
    Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
    startActivity(intent);
  }

}
