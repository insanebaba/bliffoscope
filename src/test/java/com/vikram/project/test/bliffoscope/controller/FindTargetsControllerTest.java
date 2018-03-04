package com.vikram.project.test.bliffoscope.controller;

import com.vikram.project.test.bliffoscope.Tuple;
import com.vikram.project.test.bliffoscope.service.StorageProperties;
import com.vikram.project.test.bliffoscope.service.StorageServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FindTargetsControllerTest {

    private FindTargetsController findTargetsController = new FindTargetsController(new StorageServiceImpl(new StorageProperties()));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testFindPattern() throws IOException {
        Character[][] inputData= findTargetsController.getCharArray("....\n..+.\n..+.\n..+.\n..+.".getBytes());
        Character[][] pattern= findTargetsController.getCharArray(".+.\n.++\n...".getBytes());

        System.out.println("input:");
        System.out.println(Arrays.deepToString(inputData));
        System.out.println("pattern:");
        System.out.println(Arrays.deepToString(pattern));

        int[][] result=new int[pattern.length][pattern[0].length];
        System.out.println("result before:");
        System.out.println(Arrays.deepToString(result));


        List<Tuple<Integer, Integer>> targets = findTargetsController.findPattern(inputData, pattern,  0.67);

    }

    @Test
    public void testFind() {
        Assert.assertTrue("                12  ".replaceAll("^(?=\\\\s*\\\\S).*$","").length()>0);
    }
}

