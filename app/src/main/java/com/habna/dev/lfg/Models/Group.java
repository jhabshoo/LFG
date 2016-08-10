package com.habna.dev.lfg.Models;

import java.sql.Time;
import java.util.Date;

/**
 * Represents Group to be added, searched, and joined by other users
 */
public class Group {

  private String objectId;
  private String title;
  private String description;
  private String owner; //e-mail address possible
  private int minParticipants;
  private int maxParticipants;
  private Date startDate;
  private Date startTime;

  public Group() {
  }

  public Group(String title, String description, String owner, int minParticipants,
               int maxParticipants, Date startDate, Time startTime) {
    this.title = title;
    this.description = description;
    this.owner = owner;
    this.minParticipants = minParticipants;
    this.maxParticipants = maxParticipants;
    this.startDate = startDate;
    this.startTime = startTime;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public int getMinParticipants() {
    return minParticipants;
  }

  public void setMinParticipants(int minParticipants) {
    this.minParticipants = minParticipants;
  }

  public int getMaxParticipants() {
    return maxParticipants;
  }

  public void setMaxParticipants(int maxParticipants) {
    this.maxParticipants = maxParticipants;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }
}
