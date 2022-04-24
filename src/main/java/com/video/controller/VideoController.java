package com.video.controller;

import com.video.model.ResponseFile;
import com.video.model.ResponseMessage;
import com.video.repo.FileDB;
import com.video.service.FileStorageService;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/v1")
public class VideoController {
  @Autowired private FileStorageService storageService;

  /** Not video file extension - throw error upload - File not found download - file not found */
  @PostMapping("/files")
  public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") String filePath) {
    String message = "";
    try {
      storageService.store(filePath);
      message = "Uploaded the file successfully: " + Paths.get(filePath);
      return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(message));
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      message = "File exists: " + Paths.get(filePath) + "!";
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(message));
    } catch (IOException e) {
      message = "Bad request";
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    } catch (UnsupportedOperationException e) {
      message = "Unsupported Media Type";
      return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
          .body(new ResponseMessage(message));
    }
  }

  @GetMapping("/files")
  public ResponseEntity<List<ResponseFile>> getListFiles() {
    List<ResponseFile> files =
        storageService
            .getAllFiles()
            .map(
                dbFile -> {
                  String fileDownloadUri =
                      ServletUriComponentsBuilder.fromCurrentContextPath()
                          .path("/file/")
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
    return ResponseEntity.status(HttpStatus.OK).body(files);
  }

  @GetMapping("/files/{id}")
  public ResponseEntity downloadFile(@PathVariable String id) {
    FileDB fileDB = storageService.getFile(id);
    try {
      return ResponseEntity.ok()
          .header(
              HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
          .body(fileDB.getData());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
    }
  }

  @DeleteMapping("/files/{id}")
  public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String id) {
    String message = "";
    try {
      message = "Deleted the file successfully: " + id;
      storageService.deleteFile(id);
      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    } catch (Exception e) {
      e.printStackTrace();
      message = "File not found: " + id + "!";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message));
    }
  }

  @GetMapping("/files/search")
  public ResponseEntity<List<ResponseFile>> getLaptopsByNameAndBrand(
      @RequestParam(required = false, defaultValue = "...") String name,
      @RequestParam(required = false, defaultValue = "0") long size,
      @RequestParam(required = false, defaultValue = "0") double duration,
      @RequestParam(required = false, defaultValue = "...") String type) {
    List<ResponseFile> files = storageService.searchAllFiles(name, size, duration, type);
    return ResponseEntity.status(HttpStatus.OK).body(files);
  }
}
