package com.habna.dev.lfg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.habna.dev.lfg.Models.Group;
import com.habna.dev.lfg.R;

import java.util.ArrayList;

/**
 * Custom list adapter for Group
 */
public class GroupListAdapter extends BaseAdapter {

  private Context mContext;
  private ArrayList<Group> groups;

  public GroupListAdapter(Context context, ArrayList<Group> groups) {
    mContext = context;
    this.groups = groups;
  }

  @Override
  public int getCount() {
    return groups.size();
  }

  @Override
  public Object getItem(int position) {
    return groups.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View view = convertView;

    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) mContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.group_list_item, null);
    }

    TextView text1 = (TextView) view.findViewById(R.id.groupItemText1);

    Group group = groups.get(position);

    text1.setText(group.getTitle());

    return view;
  }

  public void addGroup(Group g) {
    if (groups == null) {
      groups = new ArrayList<>();
    }
    groups.add(g);
  }

  public void clear() {
    groups.clear();
  }
}
