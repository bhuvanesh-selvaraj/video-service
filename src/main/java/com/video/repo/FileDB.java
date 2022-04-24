package com.video.repo;

import java.sql.Timestamp;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "files")
@NoArgsConstructor
public class FileDB {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Getter
  @Setter
  private String id;

  @Column(unique = true)
  @Getter
  @Setter
  private String name;

  @Getter @Setter private String type;
  @Getter @Setter private double duration;
  @Getter @Setter private long size;
  @Lob @Getter @Setter private byte[] data;
  @Getter @Setter private Timestamp createdAt;

  public FileDB(
      String name, String type, byte[] data, double duration, long size, Timestamp createdAt) {
    this.name = name;
    this.type = type;
    this.data = data;
    this.duration = duration;
    this.size = size;
    this.createdAt = createdAt;
  }
}
