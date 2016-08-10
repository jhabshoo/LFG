package com.habna.dev.lfg.Models;

/**
 * Represents relationship between user participant and Group.
 */
public class GroupParticipant {

  private String groupId;
  private String participant;

  public GroupParticipant() {

  }

  public GroupParticipant(String groupId, String participant)  {
    this.groupId = groupId;
    this.participant = participant;
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
}
