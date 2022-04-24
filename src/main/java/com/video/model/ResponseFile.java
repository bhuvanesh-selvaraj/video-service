package com.video.model;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

public class ResponseFile {
  @Getter @Setter private String name;
  @Getter @Setter private String id;
  @Getter @Setter private String url;
  @Getter @Setter private String type;
  @Getter @Setter private long size;
  @Getter @Setter private double durationInSec;
  @Getter @Setter private Timestamp createdAt;

  public ResponseFile(
      String name,
      String url,
      String type,
      long size,
      double durationInSec,
      String id,
      Timestamp createdAt) {
    this.name = name;
    this.id = id;
    this.url = url;
    this.type = type;
    this.size = size;
    this.durationInSec = durationInSec;
    this.createdAt = createdAt;
  }
}
