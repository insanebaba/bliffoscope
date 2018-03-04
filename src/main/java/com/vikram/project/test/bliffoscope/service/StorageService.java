package com.vikram.project.test.bliffoscope.service;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file) throws IOException;

    Stream<Path> loadAll() throws IOException;

    Path load(String filename);

    Resource loadAsResource(String filename) throws FileNotFoundException;

    void deleteAll();

}