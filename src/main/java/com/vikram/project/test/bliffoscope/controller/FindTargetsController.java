package com.vikram.project.test.bliffoscope.controller;

import com.vikram.project.test.bliffoscope.Tuple;
import com.vikram.project.test.bliffoscope.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FindTargetsController {

    private final StorageService storageService;

    @Autowired
    public FindTargetsController(StorageService storageService) {
        this.storageService = storageService;
    }


    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        return "index.html";
    }

    @PostMapping("/findPattern")
    @ResponseBody
    public Map<String, Object> findPatternForFiles(@RequestParam("input") MultipartFile inputDataFile,
                                                   @RequestParam("torpedo") MultipartFile torpedoFile,
                                                   @RequestParam("starship") MultipartFile starshipFile) throws IOException {
        if (inputDataFile == null || torpedoFile == null || starshipFile == null)
            throw new IllegalArgumentException("testData, torpedoFile and starshipFile needs to be provided");

        storageService.store(inputDataFile);
        storageService.store(torpedoFile);
        storageService.store(starshipFile);

        Character[][] inputData = this.getCharArray(inputDataFile.getBytes());

        Character[][] torpedo = this.getCharArray(torpedoFile.getBytes());
        Character[][] starship = this.getCharArray(starshipFile.getBytes());

        List<Tuple<Integer, Integer>> result = this.findPattern(inputData, torpedo, 0.5);
        result.addAll(this.findPattern(inputData, starship, 0.5));

        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("found", (result));
        resultMap.put("input", inputData);
        resultMap.put("torpedo", torpedo);
        resultMap.put("starship", starship);

        return resultMap;
    }


    public List<Tuple<Integer, Integer>> findPattern(Character[][] inputData, Character[][] pattern, double probability) {
        char CharacterToFind = '+';

        List<Tuple<Integer, Integer>> inputDataMarkerList = new ArrayList<>();
        List<Tuple<Integer, Integer>> patternMarkerList = new ArrayList<>();

        this.listCoordinates(inputData, inputDataMarkerList, CharacterToFind, inputData.length, inputData[0].length);
        this.listCoordinates(pattern, patternMarkerList, CharacterToFind, pattern.length, pattern[0].length);

        List<Tuple<Integer, Integer>> commonMappedValues = this.findCommonMergedCommonValues(inputDataMarkerList, patternMarkerList, probability);

        System.out.println("common values:" + commonMappedValues);
        return commonMappedValues;
    }

    private List<Tuple<Integer, Integer>> findCommonMergedCommonValues(
            List<Tuple<Integer, Integer>> inputDataMarkerList,
            List<Tuple<Integer, Integer>> patternMarkerList,
            double probability
    ) {
        Tuple<Integer, Integer> firstEntry = patternMarkerList.get(0);
        List<Tuple<Integer, Integer>> temporary = new ArrayList<>(), resultMerged = new ArrayList<>();
        int xDiff, yDiff;
        Collections.sort(inputDataMarkerList, Tuple::compare);
        Collections.sort(patternMarkerList, Tuple::compare);

        Long minimalSize = Math.round(probability * patternMarkerList.size());

        System.out.println("minimum pixels to find:" + minimalSize);
        for (Tuple<Integer, Integer> entry : inputDataMarkerList) {
            temporary = new ArrayList<>();
            xDiff = entry.x - firstEntry.x;
            yDiff = entry.y - firstEntry.y;
            int keyToCompare;
            int valueCompare;
            for (Tuple<Integer, Integer> current : patternMarkerList) {
                Tuple<Integer, Integer> temp = new Tuple<>(current.x + xDiff, current.y + yDiff);
                if (inputDataMarkerList.contains(temp))
                    temporary.add(temp);
            }

            if (temporary.size() >= minimalSize) {
                resultMerged.addAll(temporary);
            }
        }
        return resultMerged;

    }

    private void listCoordinates(
            Character[][] inputData,
            List<Tuple<Integer, Integer>> inputDataMarkerList,
            char characterToFind,
            int inputDataRowLength,
            int inputDataColumnLength
    ) {
        for (int i = 0; i < inputDataRowLength; i++) {
            for (int j = 0; j < inputDataColumnLength; j++) {
                if (inputData[i][j] == characterToFind)
                    inputDataMarkerList.add(new Tuple<>(i, j));
            }
        }
    }


    public Character[][] getCharArray(byte[] inputBytes) {
        List<String> list = new ArrayList<>();
        for (String s : new String(inputBytes).split("\n")) {
            if (!s.trim().isEmpty()) {
                list.add(s);
            }
        }
        String[] inputString = list
                .toArray(new String[0]);

        Character inputData[][] = new Character[inputString.length][];
        int i = 0;
        for (String str : inputString) {
            inputData[i] = str.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
            i++;
        }
        return inputData;
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleStorageFileNotFound(Exception exc) {
        return ResponseEntity.notFound().build();
    }


}
