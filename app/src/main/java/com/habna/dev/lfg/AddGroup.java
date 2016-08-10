package com.habna.dev.lfg;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.habna.dev.lfg.Models.Group;
import com.habna.dev.lfg.Models.GroupParticipant;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class AddGroup extends AppCompatActivity {

  private final String TIME_REGEX = "(1[012]|[1-9]):[0-5][0-9](\\\\s)?(?i)( AM| PM)";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_group);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    final EditText titleText = (EditText) findViewById(R.id.titleEditText);
    final EditText descText = (EditText) findViewById(R.id.descEditText);
    final EditText minParticipantsText = (EditText) findViewById(R.id.minParticipantsEditText);
    final EditText maxParticipantsText = (EditText) findViewById(R.id.maxParticipantsEditText);
    final EditText dateText = (EditText) findViewById(R.id.dateEditText);
    final EditText timeText = (EditText) findViewById(R.id.timeEditText);
    final Switch inviteOnlySwitch = (Switch) findViewById(R.id.inviteOnlySwitch);
    final Button addGroupButton = (Button) findViewById(R.id.addGroupButton);
    final Calendar now = Calendar.getInstance();
    dateText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddGroup.this, new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            dateText.setText(i1+1 + "/" + i2 + "/" + i);
          }
        },
          now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
      }
    });

    timeText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddGroup.this, new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker timePicker, int i, int i1) {
            String timeOfDay = " AM";
            if (i >= 12)  {
              timeOfDay = " PM";
              if (i != 12) {
                i -= 12;
              }
            }
            String minString = String.valueOf(i1);
            if (i1 < 10)  {
              minString = "0"+i1;
            }
            timeText.setText((i == 0 ? 12 : i) + ":" + minString + timeOfDay);
          }
        },
          now.get(Calendar.AM_PM) == 1 ? now.get(Calendar.HOUR) + 12 : now.get(Calendar.HOUR),
          now.get(Calendar.MINUTE), false);
        timePickerDialog.show();
      }
    });

    addGroupButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // validate form
        boolean formValid = true;
        if (!validateTitle(titleText.getText().toString())) {
          formValid = false;
          titleText.setError("Cannot be empty!");
        }
        if (!validateDate(dateText.getText().toString())) {
          formValid = false;
          dateText.setError("Incorrect date format!");
        }
        if (!validateTime(timeText.getText().toString())) {
          formValid = false;
          timeText.setError("Incorrect time format!");
        }
        if (formValid)  {
          String[] dateParts = dateText.getText().toString().split("/");
          String[] timeParts = timeText.getText().toString().split(":");
          int hour = timeParts[1].contains("PM") ? Integer.valueOf(timeParts[0]) + 12 : Integer.valueOf(timeParts[0]);
          int minute = Integer.valueOf(timeParts[1].split(" ")[0]);

          Group group = new Group(titleText.getText().toString(), descText.getText().toString(),
            MainActivity.backendlessUser.getEmail(),
            minParticipantsText.getText().toString().isEmpty() ? 1 : Integer.valueOf(minParticipantsText.getText().toString()),
            maxParticipantsText.getText().toString().isEmpty() ? -1 : Integer.valueOf(maxParticipantsText.getText().toString()),
            new Date(Integer.valueOf(dateParts[2]), Integer.valueOf(dateParts[0]), Integer.valueOf(dateParts[1])),
            new Time(hour, minute, 0), inviteOnlySwitch.isChecked());
          Backendless.Persistence.save(group, new AsyncCallback<Group>() {
            @Override
            public void handleResponse(Group response) {
              GroupParticipant groupParticipant = new GroupParticipant(response.getObjectId(),
                response.getOwner(), GroupParticipant.OWNER);
              Backendless.Persistence.save(groupParticipant, new AsyncCallback<GroupParticipant>() {
                @Override
                public void handleResponse(GroupParticipant response) {
                  finish();
                  startActivity(new Intent(AddGroup.this, MainActivity.class));
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                  Log.e("ERROR", fault.getMessage());
                }
              });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
              Log.e("ERROR", fault.getMessage());
            }
          });
        } else  {
          // do nothing
        }
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home)  {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private boolean validateTitle(String title) {
    return !title.isEmpty();
  }

  private boolean validateDate(String date) {
    String[] parts = date.split("/");
    if (parts.length != 3)  {
      return false;
    }
    try {
      int month = Integer.valueOf(parts[0]);
      int day = Integer.valueOf(parts[1]);
      int year = Integer.valueOf(parts[2]);
      if (month < 1 || month > 12)  {
        return false;
      }
      int maxDays = getDaysInMonth(month, isLeapYear(year));
      if (day < 1 || day > maxDays) {
        return false;
      }
      if (year < Calendar.getInstance().get(Calendar.YEAR)) {
        return false;
      }
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  private boolean validateTime(String time) {
    return time.matches(TIME_REGEX);
  }

  private int getDaysInMonth(int month, boolean leapYear) {
    switch (month)  {
      case 4:
      case 9:
      case 6:
      case 11:
        return 30;
      case 2:
        return leapYear ? 29 : 28;
      case 1:
      case 3:
      case 5:
      case 7:
      case 8:
      case 10:
      case 12:
        return 31;
    }
    return -1;
  }

  private boolean isLeapYear(int year)  {
    if (year % 4 == 0)  {
      if (year % 100 == 0)  {
        if (year % 400 == 0)  {
          return true;
        } else  {
          return false;
        }
      } else  {
        return true;
      }
    }
    return false;
  }
}
