package com.habna.dev.lfg.Models;

/**
 * Represents relationship between user participant and Group.
 */
public class GroupParticipant {

  public final static int OWNER = 1;
  public final static int ADMIN = 2;
  public final static int MEMBER = 3;

  private String groupId;
  private String participant;
  private int role;

  public GroupParticipant() {

  }

  public GroupParticipant(String groupId, String participant, int role)  {
    this.groupId = groupId;
    this.participant = participant;
    this.role = role;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getParticipant() {
    return participant;
  }

  public void setParticipant(String participant) {
    this.participant = participant;
  }

  public int getRole() {
    return role;
  }

  public void setRole(int role) {
    this.role = role;
  }
}
