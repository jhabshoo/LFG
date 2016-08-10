package com.habna.dev.lfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.habna.dev.lfg.Models.Group;
import com.habna.dev.lfg.Models.GroupParticipant;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private final String BACKENDLESS_APP_ID = "AF5997B4-BE2B-D953-FFA4-37E3B1379700";
  private final String BACKENDLESS_SECRET_KEY = "B6DDFED9-BAFD-9AA9-FFB4-CFD3870A2D00";
  private final String BACKENDLESS_VERSION = "v1";

  public static BackendlessUser backendlessUser;
  private GroupListAdapter joinedGroupListAdapter;
  private GroupListAdapter openGroupListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, AddGroup.class);
        startActivity(intent);
      }
    });

    final TextView joinedHeader = new TextView(this);
    joinedHeader.setText("Joined Groups");
    final TextView openHeader = new TextView(this);
    openHeader.setText("Open Groups");
    final ListView joinedGroupListView = (ListView) findViewById(R.id.joinedGroupList);
    joinedGroupListView.addHeaderView(joinedHeader);
    final ListView openGroupListView = (ListView) findViewById(R.id.openGroupList);
    openGroupListView.addHeaderView(openHeader);
    joinedGroupListAdapter = new GroupListAdapter(this, new ArrayList<Group>());
    openGroupListAdapter = new GroupListAdapter(this, new ArrayList<Group>());
    joinedGroupListView.setAdapter(joinedGroupListAdapter);
    openGroupListView.setAdapter(openGroupListAdapter);

    Backendless.initApp(this, BACKENDLESS_APP_ID, BACKENDLESS_SECRET_KEY, BACKENDLESS_VERSION);
    AsyncCallback<Boolean> isValidLoginCallback = new AsyncCallback<Boolean>() {
      @Override
      public void handleResponse(Boolean response) {
        if (!response)  {
          finish();
          startLoginActivity();
        } else  {
          final String userToken = UserIdStorageFactory.instance().getStorage().get();
          if (userToken != null && !userToken.isEmpty())  {
            Backendless.UserService.findById(userToken, new AsyncCallback<BackendlessUser>() {
              @Override
              public void handleResponse(BackendlessUser response) {
                backendlessUser = response;
                loadGroups();
              }

              @Override
              public void handleFault(BackendlessFault fault) {
                Log.e("ERROR", fault.getMessage());
              }
            });
          } else  {
            finish();
            startLoginActivity();
          }
        }
      }

      @Override
      public void handleFault(BackendlessFault fault) {
        Log.e("ERROR", fault.getMessage());
      }
    };
    Backendless.UserService.isValidLogin(isValidLoginCallback);
  }

  private void loadGroups() {
    loadJoinedGroups();
    loadOpenGroups();
  }

  private void loadOpenGroups() {
    AsyncCallback<BackendlessCollection<Group>> loadGroupsCallback =
      new AsyncCallback<BackendlessCollection<Group>>() {
        @Override
        public void handleResponse(BackendlessCollection<Group> response) {
          for (Group group : response.getCurrentPage()) {
            openGroupListAdapter.addGroup(group);
          }
          openGroupListAdapter.notifyDataSetChanged();
        }

        @Override
        public void handleFault(BackendlessFault fault) {
          Log.e("ERROR", fault.getMessage());
        }
      };
    BackendlessDataQuery query = new BackendlessDataQuery();
    query.setWhereClause("owner != '" + backendlessUser.getEmail() + "' " +
      "and inviteOnly = 'F'");
    Backendless.Persistence.of(Group.class).find(query, loadGroupsCallback);
  }

  private void loadJoinedGroups() {
    AsyncCallback<BackendlessCollection<GroupParticipant>> loadGPsCallback =
      new AsyncCallback<BackendlessCollection<GroupParticipant>>() {
      @Override
      public void handleResponse(BackendlessCollection<GroupParticipant> response) {
        if (!response.getCurrentPage().isEmpty()) {
          BackendlessDataQuery groupQuery = new BackendlessDataQuery();
          StringBuilder groupWhereClause = new StringBuilder();
          groupWhereClause.append("objectId in (");
          int count = 0;
          for (GroupParticipant groupParticipant : response.getCurrentPage()) {
            groupWhereClause.append("'" + groupParticipant.getGroupId() + "'");
            if (count != response.getCurrentPage().size() - 1) {
              groupWhereClause.append(",");
            }
            count++;
          }
          groupWhereClause.append(")");
          groupQuery.setWhereClause(groupWhereClause.toString());
          Backendless.Persistence.of(Group.class).find(groupQuery, new AsyncCallback<BackendlessCollection<Group>>() {
            @Override
            public void handleResponse(BackendlessCollection<Group> response) {
              for (Group group : response.getCurrentPage()) {
                joinedGroupListAdapter.addGroup(group);
              }
              joinedGroupListAdapter.notifyDataSetChanged();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
              Log.e("Error", fault.getMessage());
            }
          });
        }
      }

      @Override
      public void handleFault(BackendlessFault fault) {
        Log.e("Error", fault.getMessage());
      }
    };


    BackendlessDataQuery gpQuery = new BackendlessDataQuery();
    gpQuery.setWhereClause("participant = '" + backendlessUser.getEmail() + "'");
    Backendless.Persistence.of(GroupParticipant.class).find(gpQuery, loadGPsCallback);
  }

  private void startLoginActivity() {
    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    if (id == R.id.action_logout) {
      doLogout();
      finish();
      startLoginActivity();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void doLogout() {
    Backendless.UserService.logout(new AsyncCallback<Void>() {
      @Override
      public void handleResponse(Void response) {
        Log.i("LOGOUT", "User logged out");
      }

      @Override
      public void handleFault(BackendlessFault fault) {
        Log.e("ERROR", fault.getMessage());
      }
    });
  }
}
