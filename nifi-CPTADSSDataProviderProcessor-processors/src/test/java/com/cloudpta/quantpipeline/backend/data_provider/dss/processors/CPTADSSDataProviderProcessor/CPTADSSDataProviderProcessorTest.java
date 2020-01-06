/*

Copyright 2017-2019 Advanced Products Limited, 
dannyb@cloudpta.com
github.com/dannyb2018

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CPTADSSDataProviderProcessorTest 
{
    @Test
    public void testProcessorWithEmptyFlowFile() 
    {        
        // Mock the input file
        // If it is empty then it needs to have rics, fields and properties all empty
        String emptyRequestString = "{\""+ CPTADSSDataProviderProcessorConstants.INSTRUMENTS_ARRAY_NAME + "\":[], \""+ CPTADSSDataProviderProcessorConstants.FIELDS_ARRAY_NAME + "\":[], \""+ CPTADSSDataProviderProcessorConstants.PROPERTIES_ARRAY_NAME + "\":[]}";
        InputStream content = new ByteArrayInputStream(emptyRequestString.getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTADSSDataProviderProcessor());

        // Set user names, passwords
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY, DSS_USER_NAME);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY, DSWS_USER_NAME);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY, DSS_PASSWORD);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY, DSWS_PASSWORD);

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADSSDataProviderProcessorConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue(results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));

        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
        result.assertContentEquals("nifi rocks");       
    }

    @Test
    public void testProcessorWithWrongUserNamePassword() 
    {        
        // Mock the input file
        // If it is empty then it needs to have rics, fields and properties all empty
        String emptyRequestString = "{\""+ CPTADSSDataProviderProcessorConstants.INSTRUMENTS_ARRAY_NAME + "\":[{\"" + CPTADSSDataProviderProcessorConstants.IDENTIFIER_FIELD_NAME + "\": \"2618.TW\", \"" + CPTADSSDataProviderProcessorConstants.IDENTIFIER_TYPE_FIELD_NAME + "\": \"" + CPTADSSDataProviderProcessorConstants.IDENTIFIER_TYPE_RIC + "\"}], \""+ CPTADSSDataProviderProcessorConstants.FIELDS_ARRAY_NAME + "\":[], \""+ CPTADSSDataProviderProcessorConstants.PROPERTIES_ARRAY_NAME + "\":[]}";
        InputStream content = new ByteArrayInputStream(emptyRequestString.getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTADSSDataProviderProcessor());

        // Set user names, passwords
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY, UUID.randomUUID().toString());
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY, UUID.randomUUID().toString());
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY, UUID.randomUUID().toString());
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY, UUID.randomUUID().toString());

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADSSDataProviderProcessorConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue(results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));

        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
        result.assertContentEquals("nifi rocks");       
    }

    @Test
    public void testProcessorWithOneRequestForDSSFlowFile() 
    {
 
        // Mock the input file
        // Add two instruments
        String ric1 = "2618.TW";
        String ric2 = "MSFT.OQ";
        InputStream content = new ByteArrayInputStream("{\"hello\":\"nifi rocks\"}".getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTADSSDataProviderProcessor());

        // Set user names, passwords
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY, DSS_USER_NAME);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY, DSWS_USER_NAME);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY, DSS_PASSWORD);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY, DSWS_PASSWORD);

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADSSDataProviderProcessorConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue("1 match", results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));

        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
        result.assertContentEquals("nifi rocks");       
    }

    @Test
    public void testProcessorWithTwoRequestForDSSFlowFile() 
    {
 
        // Mock the input file
        // If it is empty then it needs to have
        InputStream content = new ByteArrayInputStream("{\"hello\":\"nifi rocks\"}".getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTADSSDataProviderProcessor());

        // Set user names, passwords
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY, DSS_USER_NAME);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY, DSWS_USER_NAME);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY, DSS_PASSWORD);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY, DSWS_PASSWORD);

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADSSDataProviderProcessorConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue("1 match", results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));

        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
        result.assertContentEquals("nifi rocks");       
    }

    @Test
    public void testProcessorWithOneRequestForDSSAndOneForDSWSFlowFile() 
    {
 
        // Mock the input file
        // If it is empty then it needs to have
        InputStream content = new ByteArrayInputStream("{\"hello\":\"nifi rocks\"}".getBytes());

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new CPTADSSDataProviderProcessor());

        // Set user names, passwords
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY, DSS_USER_NAME);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY, DSWS_USER_NAME);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY, DSS_PASSWORD);
        runner.setProperty(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY, DSWS_PASSWORD);

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do aditional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(CPTADSSDataProviderProcessorConstants.RELATIONSHIP_NAME_SUCCESS);
        assertTrue("1 match", results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));

        // Test attributes and content
//        result.assertAttributeEquals(CPTADSSDataProviderProcessor.MATCH_ATTR, "nifi rocks");
        result.assertContentEquals("nifi rocks");       
    }
    
    static String DSS_USER_NAME = "CHANGE_THIS_TO_YOUR_DSS_USER_NAME";
    static String DSS_PASSWORD = "CHANGE_THIS_TO_YOUR_DSS_PASSWORD";
    static String DSWS_USER_NAME = "CHANGE_THIS_TO_YOUR_DSWS_USER_NAME";
    static String DSWS_PASSWORD = "CHANGE_THIS_TO_YOUR_DSWS_PASSWORD";
}
