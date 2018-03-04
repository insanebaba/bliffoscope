package com.vikram.project.test.bliffoscope.service;

import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService{

    private final Path rootLocation;

    @Autowired
    public StorageServiceImpl (StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new InvalidFileNameException(
                        "Cannot store file with relative path outside current directory "
                                , filename);
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new IOException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() throws IOException {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        }
        catch (IOException e) {
            throw new IOException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) throws FileNotFoundException {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Could not initialize storage", e);
        }
    }
}
