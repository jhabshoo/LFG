package com.habna.dev.lfg.Models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents Group to be added, searched, and joined by other users
 */
public class Group {

  private String title;
  private String description;
  private String owner; //e-mail address possible
  private int minParticipants;
  private int maxParticipants;
  private Set<String> participants;
  private Date start;

  public Group() {
  }

  public Group(String title, String description, String owner, int minParticipants,
               int maxParticipants, Date start) {
    this.title = title;
    this.description = description;
    this.owner = owner;
    this.minParticipants = minParticipants;
    this.maxParticipants = maxParticipants;
    this.start = start;
    participants = new HashSet<>();
    participants.add(owner);
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

  public Set<String> getParticipants() {
    return participants;
  }

  public void setParticipants(Set<String> participants) {
    this.participants = participants;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public boolean isSatisfied()  {
    return participants.size() >= minParticipants;
  }

  public boolean isFull() {
    return participants.size() == maxParticipants;
  }
}
