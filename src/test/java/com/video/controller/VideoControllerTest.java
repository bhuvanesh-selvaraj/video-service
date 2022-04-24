package com.video.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.video.model.ResponseFile;
import com.video.repo.FileDB;
import com.video.service.FileStorageService;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class VideoControllerTest {
  @Autowired private MockMvc mock;

  @MockBean private FileStorageService storageService;

  @Test
  public void testList() throws Exception {
    List<FileDB> lst = new ArrayList<>();

    lst.add(
        new FileDB(
            "sample-avi.avi",
            "avi",
            "Any String".getBytes(),
            30,
            10,
            Timestamp.valueOf("2022-04-24 07:46:59.00000")));
    when(storageService.getAllFiles()).thenReturn(lst.stream());
    mock.perform(get("/v1/files"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(
            content()
                .string(
                    "[{\"name\":\"sample-avi.avi\",\"id\":null,\"url\":\"http://localhost/file/sample-avi.avi\",\"type\":\"avi\",\"size\":10,\"durationInSec\":30.0,\"createdAt\":\"2022-04-23T23:46:59.000+00:00\"}]"));
  }

  @Test
  public void testListEmpty() throws Exception {
    List<FileDB> lst = new ArrayList<>();

    when(storageService.getAllFiles()).thenReturn(lst.stream());
    mock.perform(get("/v1/files"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().string("[]"));
  }

  @Test
  public void testSearch() throws Exception {
    List<ResponseFile> lst = new ArrayList<>();

    lst.add(
        new ResponseFile(
            "sample-avi.avi",
            "http://localhost:8080/file/sample-avi.avi",
            "avi",
            30,
            10,
            "8def5fa5-9644-4855-94e2-0679a6c09b9f",
            Timestamp.valueOf("2022-04-24 07:46:59.00000")));
    when(storageService.searchAllFiles(anyString(), anyLong(), anyDouble(), anyString()))
        .thenReturn(lst);
    mock.perform(get("/v1/files/search"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(
            content()
                .string(
                    "[{\"name\":\"sample-avi.avi\",\"id\":\"8def5fa5-9644-4855-94e2-0679a6c09b9f\",\"url\":\"http://localhost:8080/file/sample-avi.avi\",\"type\":\"avi\",\"size\":30,\"durationInSec\":10.0,\"createdAt\":\"2022-04-23T23:46:59.000+00:00\"}]"));
  }

  @Test
  public void testUpload() throws Exception {
    FileDB fileDB = new FileDB();
    when(storageService.store(anyString())).thenReturn(fileDB);
    mock.perform(post("/v1/files").contentType(MediaType.APPLICATION_JSON)
            .content("{\"file\":\"/Users/bhuvanesh/Downloads/sample-wmv.wmv\"}")
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
  }

  @Test
  public void testUpload_filepathNotFound() throws Exception {
    FileDB fileDB = new FileDB();
    when(storageService.store(anyString())).thenReturn(fileDB);
    mock.perform(post("/v1/files")).andExpect(status().isBadRequest());
  }

  @Test
  public void testUpload_filepathEmpty() throws Exception {
    when(storageService.store(anyString())).thenThrow(UnsupportedOperationException.class);
    mock.perform(post("/v1/files").contentType(MediaType.APPLICATION_JSON)
            .content("{\"file\":\"/Users/bhuvanesh/Downloads/sample-wmv.jpg\"}")
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnsupportedMediaType());
  }

  @Test
  public void testUpload_fileDuplicate() throws Exception {
    when(storageService.store(anyString())).thenThrow(DataIntegrityViolationException.class);
    mock.perform(post("/v1/files").contentType(MediaType.APPLICATION_JSON)
            .content("{\"file\":\"/Users/bhuvanesh/Downloads/sample-wmv.wmv\"}")
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());
  }

  @Test
  public void testUpload_fileBadREq() throws Exception {
    when(storageService.store(anyString())).thenThrow(IOException.class);
    mock.perform(post("/v1/files").contentType(MediaType.APPLICATION_JSON)
            .content("{\"file\":\"\"}")
            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
  }

  @Test
  public void testGetFile() throws Exception {
    FileDB fileDB = new FileDB();
    when(storageService.getFile(anyString())).thenReturn(fileDB);
    mock.perform(get("/v1/files/{id}", "sample-avi.avi")).andExpect(status().isOk());
  }

  @Test
  public void testGetFile_Error() throws Exception {
    when(storageService.getFile(anyString())).thenThrow(RuntimeException.class);
    mock.perform(get("/v1/files/{id}", "sample-avi.avi")).andExpect(status().isNotFound());
  }

  @Test
  public void testDelFile_Error() throws Exception {
    doThrow(new RuntimeException()).when(storageService).deleteFile(anyString());
    mock.perform(delete("/v1/files/{id}", "sample-avi.avi")).andExpect(status().isNotFound());
  }

  @Test
  public void testDelFile_Ok() throws Exception {
    doNothing().when(storageService).deleteFile(anyString());
    mock.perform(delete("/v1/files/{id}", "sample-avi.avi")).andExpect(status().isOk());
  }
}
