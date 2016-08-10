package com.habna.dev.lfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.habna.dev.lfg.Models.Group;
import com.habna.dev.lfg.Models.GroupParticipant;

public class GroupActivity extends AppCompatActivity {

  public static Group group;
  public static boolean joined;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    final TextView titleText = (TextView) findViewById(R.id.groupTitleTextView);
    final TextView descText = (TextView) findViewById(R.id.groupDescTextView);
    final Button joinButton = (Button) findViewById(R.id.joinButton);
    titleText.setText(group.getTitle());
    descText.setText(group.getDescription());
    joinButton.setVisibility(joined ? View.INVISIBLE : View.VISIBLE);
    joinButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        joinGroup();
      }
    });
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
