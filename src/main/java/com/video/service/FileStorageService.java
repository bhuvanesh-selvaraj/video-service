package com.video.service;

import com.video.model.ResponseFile;
import com.video.repo.FileDB;
import com.video.repo.FileDBRepository;
import com.xuggle.xuggler.IContainer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class FileStorageService {
  private static final List<String> lstVideoFileTypes =
      new ArrayList<>(Arrays.asList("mp4", "avi", "wmv", "mpg", "mpeg", "mov"));
  @Autowired private FileDBRepository fileDBRepository;

  public FileDB store(String filePath) throws IOException {
    File file = new File(filePath);
    String fileName = StringUtils.cleanPath(file.getName());
    String extension = FilenameUtils.getExtension(filePath);
    if (!lstVideoFileTypes.contains(extension.toLowerCase()))
      throw new UnsupportedOperationException("Unsupported File format");
    FileDB videoFile =
        new FileDB(
            fileName,
            FilenameUtils.getExtension(filePath),
            FileUtils.readFileToByteArray(file),
            getLength(filePath),
            Files.size(Paths.get(filePath)),
            new Timestamp(System.currentTimeMillis()));
    return fileDBRepository.save(videoFile);
  }

  public FileDB getFile(String fileName) {
    return fileDBRepository.findByNameContaining(fileName);
  }

  public Stream<FileDB> getAllFiles() {
    return fileDBRepository.findAll().stream();
  }

  @Transactional
  public void deleteFile(String name) {
    fileDBRepository.deleteByName(name);
  }

  private double getLength(String path) {
    IContainer container = IContainer.make();
    container.open(path, IContainer.Type.READ, null);
    return container.getDuration() / Double.valueOf(1000000);
  }

  @Transactional(readOnly = true)
  public List<ResponseFile> searchAllFiles(String name, long size, double duration, String type) {
    return fileDBRepository
        .findByNameContainsOrSizeLessThanEqualOrDurationLessThanEqualOrTypeContaining(
            name, size, duration, type)
        .map(
            dbFile -> {
              String fileDownloadUri =
                  ServletUriComponentsBuilder.fromCurrentContextPath()
                      .path("/files/")
                      .path(dbFile.getName())
                      .toUriString();
              return new ResponseFile(
                  dbFile.getName(),
                  fileDownloadUri,
                  dbFile.getType(),
                  dbFile.getData().length,
                  dbFile.getDuration(),
                  dbFile.getId(),
                  dbFile.getCreatedAt());
            })
        .collect(Collectors.toList());
  }
}
