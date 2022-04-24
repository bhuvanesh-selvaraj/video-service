package com.video.repo;

import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {
  Long deleteByName(String name);

  FileDB findByNameContaining(String name);

  Stream<FileDB> findByNameContainsOrSizeLessThanEqualOrDurationLessThanEqualOrTypeContaining(
      String name, long size, double duration, String type);
}
