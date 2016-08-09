package com.habna.dev.lfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;

public class MainActivity extends AppCompatActivity {

  private final String BACKENDLESS_APP_ID = "AF5997B4-BE2B-D953-FFA4-37E3B1379700";
  private final String BACKENDLESS_SECRET_KEY = "B6DDFED9-BAFD-9AA9-FFB4-CFD3870A2D00";
  private final String BACKENDLESS_VERSION = "v1";

  public static BackendlessUser backendlessUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Backendless.initApp(this, BACKENDLESS_APP_ID, BACKENDLESS_SECRET_KEY, BACKENDLESS_VERSION);

    AsyncCallback<Boolean> isValidLoginCallback = new AsyncCallback<Boolean>() {
      @Override
      public void handleResponse(Boolean response) {
        System.out.println( "[ASYNC] Is login valid? - " + response );
        if (!response)  {
          finish();
          startLoginActivity();
        } else  {
          String userToken = UserIdStorageFactory.instance().getStorage().get();
          if (userToken != null && !userToken.isEmpty())  {
            Backendless.UserService.findById(userToken, new AsyncCallback<BackendlessUser>() {
              @Override
              public void handleResponse(BackendlessUser response) {
                backendlessUser = response;
              }

              @Override
              public void handleFault(BackendlessFault fault) {

              }
            });
          }
        }
      }

      @Override
      public void handleFault(BackendlessFault fault) {
        System.err.println( "Error - " + fault );
      }
    };
    Backendless.UserService.isValidLogin(isValidLoginCallback);

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

    return super.onOptionsItemSelected(item);
  }
}
