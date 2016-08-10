package com.habna.dev.lfg.Models;

/**
 * Represents user comment on group message board
 */
public class GroupMessage {
  private String message;
  private String participant;
  private String groupId;
  private String nickname;

  public GroupMessage() {
  }

  public GroupMessage(String message, String participant, String groupId, String nickname) {
    this.message = message;
    this.participant = participant;
    this.groupId = groupId;
    this.nickname = nickname;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getParticipant() {
    return participant;
  }

  public void setParticipant(String participant) {
    this.participant = participant;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
}
