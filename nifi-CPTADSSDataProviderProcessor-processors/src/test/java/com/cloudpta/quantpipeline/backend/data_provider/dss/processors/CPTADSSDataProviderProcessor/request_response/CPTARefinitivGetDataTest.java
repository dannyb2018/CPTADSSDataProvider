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
package com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response;

import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSCompositeMessage;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSCorporateActionsMessage;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSEODMessage;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSTimeSeriesMessage;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dsws.CPTADSWSConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dsws.CPTADSWSMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.json.JsonArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Danny
 */
public class CPTARefinitivGetDataTest
{
    /**
     * Test of getInstance method, of class CPTARefinitivGetData.
     */
    @Test
    public void testGetInstance()
    {
        System.out.println("getInstance");
        CPTARefinitivGetData instance = CPTARefinitivGetData.getInstance();
        // Should have 5 message types
        // DSS EOD 
        Class messageClass = instance.typeToMessageClassMap.get(CPTADSSConstants.EOD_MESSAGE_TYPE);
        assertEquals(messageClass.getTypeName(), CPTADSSEODMessage.class.getTypeName());
        // DSS Corporate actions
        messageClass = instance.typeToMessageClassMap.get(CPTADSSConstants.CA_MESSAGE_TYPE);
        assertEquals(messageClass.getTypeName(), CPTADSSCorporateActionsMessage.class.getTypeName());
        // DSS time series
        messageClass = instance.typeToMessageClassMap.get(CPTADSSConstants.TS_MESSAGE_TYPE);
        assertEquals(messageClass.getTypeName(), CPTADSSTimeSeriesMessage.class.getTypeName());
        // DSS composite
        messageClass = instance.typeToMessageClassMap.get(CPTADSSConstants.COMPOSITE_MESSAGE_TYPE);
        assertEquals(messageClass.getTypeName(), CPTADSSCompositeMessage.class.getTypeName());
        // DSWS 
        messageClass = instance.typeToMessageClassMap.get(CPTADSWSConstants.MESSAGE_TYPE);
        assertEquals(messageClass.getTypeName(), CPTADSWSMessage.class.getTypeName());
    }


    /**
     * Test of getMappedFields method, of class CPTARefinitivGetData.
     */
    @Test
    public void testGetMappedFields()
    {
        System.out.println("getMappedFields");
        
        // Start with empty list
        List<CPTADSSField> fields = new ArrayList<>();
        CPTARefinitivGetData instance = CPTARefinitivGetData.getInstance();
        HashMap<String, List<String>> result = instance.getMappedFields(fields);
        // Check it is empty
        assertTrue(result.isEmpty());
        
        // Make one of EOD message type
        fields = new ArrayList<>();
        CPTADSSField eodField1 = new CPTADSSField();
        eodField1.messageType = CPTADSSConstants.EOD_MESSAGE_TYPE;
        eodField1.name = UUID.randomUUID().toString();
        fields.add(eodField1);
        result = instance.getMappedFields(fields);
        // Shouldnt be empty
        assertTrue( false == result.isEmpty());
        // Should be just one message type
        Set<String> messageTypes = result.keySet();
        assertTrue(false == messageTypes.isEmpty());
        assertEquals(messageTypes.size(), 1);
        // It should be EOD
        assertTrue(messageTypes.contains(CPTADSSConstants.EOD_MESSAGE_TYPE));
        // should have only one field
        List<String> resultEODFields = result.get(CPTADSSConstants.EOD_MESSAGE_TYPE);
        assertTrue(false == resultEODFields.isEmpty());
        assertEquals(resultEODFields.size(), 1);
        // its name should be what was set
        String resultEODField1Name = resultEODFields.get(0);
        assertEquals(resultEODField1Name, eodField1.name);
        
        // Make one of the DSWS message type
        fields = new ArrayList<>();
        CPTADSSField dswsField1 = new CPTADSSField();
        dswsField1.messageType = CPTADSWSConstants.MESSAGE_TYPE;
        dswsField1.name = UUID.randomUUID().toString();
        fields.add(dswsField1);
        result = instance.getMappedFields(fields);
        // Shouldnt be empty
        assertTrue( false == result.isEmpty());
        // Should be just one message type
        messageTypes = result.keySet();
        assertTrue(false == messageTypes.isEmpty());
        assertEquals(messageTypes.size(), 1);
        // It should be DSWS
        assertTrue(messageTypes.contains(CPTADSWSConstants.MESSAGE_TYPE));
        // should have only one field
        List<String> resultDSWSFields = result.get(CPTADSWSConstants.MESSAGE_TYPE);
        assertTrue(false == resultDSWSFields.isEmpty());
        assertEquals(resultDSWSFields.size(), 1);
        // its name should be what was set
        String resultDSWSField1Name = resultDSWSFields.get(0);
        assertEquals(resultDSWSField1Name, dswsField1.name);
        
        //  Make two of EOD message type
        fields = new ArrayList<>();
        eodField1 = new CPTADSSField();
        eodField1.messageType = CPTADSSConstants.EOD_MESSAGE_TYPE;
        eodField1.name = UUID.randomUUID().toString();
        fields.add(eodField1);
        CPTADSSField eodField2 = new CPTADSSField();
        eodField2.messageType = CPTADSSConstants.EOD_MESSAGE_TYPE;
        eodField2.name = UUID.randomUUID().toString();
        fields.add(eodField2);
        result = instance.getMappedFields(fields);
        // Shouldnt be empty
        assertTrue( false == result.isEmpty());
        // Should be just one message type
        messageTypes = result.keySet();
        assertTrue(false == messageTypes.isEmpty());
        assertEquals(messageTypes.size(), 1);
        // It should be EOD
        assertTrue(messageTypes.contains(CPTADSSConstants.EOD_MESSAGE_TYPE));
        // should have two fields
        resultEODFields = result.get(CPTADSSConstants.EOD_MESSAGE_TYPE);
        assertTrue(false == resultEODFields.isEmpty());
        assertEquals(resultEODFields.size(),2);
        // its name should be what was set
        resultEODField1Name = resultEODFields.get(0);
        assertEquals(resultEODField1Name, eodField1.name);
        String resultEODField2Name = resultEODFields.get(1);
        assertEquals(resultEODField2Name, eodField2.name);

        // Make two of DSWS message type
        fields = new ArrayList<>();
        dswsField1 = new CPTADSSField();
        dswsField1.messageType = CPTADSWSConstants.MESSAGE_TYPE;
        dswsField1.name = UUID.randomUUID().toString();
        fields.add(dswsField1);
        CPTADSSField dswsField2 = new CPTADSSField();
        dswsField2.messageType = CPTADSWSConstants.MESSAGE_TYPE;
        dswsField2.name = UUID.randomUUID().toString();
        fields.add(dswsField2);
        result = instance.getMappedFields(fields);
        // Shouldnt be empty
        assertTrue( false == result.isEmpty());
        // Should be just one message type
        messageTypes = result.keySet();
        assertTrue(false == messageTypes.isEmpty());
        assertEquals(messageTypes.size(), 1);
        // It should be DSWS
        assertTrue(messageTypes.contains(CPTADSWSConstants.MESSAGE_TYPE));
        // should have two fields
        resultDSWSFields = result.get(CPTADSWSConstants.MESSAGE_TYPE);
        assertTrue(false == resultDSWSFields.isEmpty());
        assertEquals(resultDSWSFields.size(), 2);
        // its name should be what was set
        resultDSWSField1Name = resultDSWSFields.get(0);
        assertEquals(resultDSWSField1Name, dswsField1.name);
        String resultDSWSField2Name = resultDSWSFields.get(1);
        assertEquals(resultDSWSField2Name, dswsField2.name);
        
        // Make one of the EOD message type and one of DSWS message type
        fields = new ArrayList<>();
        eodField1 = new CPTADSSField();
        eodField1.messageType = CPTADSSConstants.EOD_MESSAGE_TYPE;
        eodField1.name = UUID.randomUUID().toString();
        fields.add(eodField1);
        dswsField1 = new CPTADSSField();
        dswsField1.messageType = CPTADSWSConstants.MESSAGE_TYPE;
        dswsField1.name = UUID.randomUUID().toString();
        fields.add(dswsField1);
        result = instance.getMappedFields(fields);
        // Shouldnt be empty
        assertTrue( false == result.isEmpty());
        // Should be two message types
        messageTypes = result.keySet();
        assertTrue(false == messageTypes.isEmpty());
        assertEquals(messageTypes.size(), 2);
        // Should include both dsws and dss eod
        assertTrue(messageTypes.contains(CPTADSWSConstants.MESSAGE_TYPE));
        assertTrue(messageTypes.contains(CPTADSSConstants.EOD_MESSAGE_TYPE));
        resultDSWSFields = result.get(CPTADSWSConstants.MESSAGE_TYPE);
        assertTrue(false == resultDSWSFields.isEmpty());
        // Should be one dsws field
        resultDSWSFields = result.get(CPTADSWSConstants.MESSAGE_TYPE);
        assertTrue(false == resultDSWSFields.isEmpty());
        assertEquals(resultDSWSFields.size(), 1);
        resultDSWSField1Name = resultDSWSFields.get(0);
        assertEquals(resultDSWSField1Name, dswsField1.name);
        // Should be one dss eod field
        resultEODFields = result.get(CPTADSSConstants.EOD_MESSAGE_TYPE);
        assertTrue(false == resultEODFields.isEmpty());
        assertEquals(resultEODFields.size(),1);
        // its name should be what was set
        resultEODField1Name = resultEODFields.get(0);
        assertEquals(resultEODField1Name, eodField1.name);
        
        // Make one of the EOD message type and two of DSWS message type
        fields = new ArrayList<>();
        eodField1 = new CPTADSSField();
        eodField1.messageType = CPTADSSConstants.EOD_MESSAGE_TYPE;
        eodField1.name = UUID.randomUUID().toString();
        fields.add(eodField1);
        dswsField1 = new CPTADSSField();
        dswsField1.messageType = CPTADSWSConstants.MESSAGE_TYPE;
        dswsField1.name = UUID.randomUUID().toString();
        fields.add(dswsField1);
        dswsField2 = new CPTADSSField();
        dswsField2.messageType = CPTADSWSConstants.MESSAGE_TYPE;
        dswsField2.name = UUID.randomUUID().toString();
        fields.add(dswsField2);
        result = instance.getMappedFields(fields);
        // Shouldnt be empty
        assertTrue( false == result.isEmpty());
        // Should be two message types
        messageTypes = result.keySet();
        assertTrue(false == messageTypes.isEmpty());
        assertEquals(messageTypes.size(), 2);
        // Should include both dsws and dss eod
        assertTrue(messageTypes.contains(CPTADSWSConstants.MESSAGE_TYPE));
        assertTrue(messageTypes.contains(CPTADSSConstants.EOD_MESSAGE_TYPE));
        resultDSWSFields = result.get(CPTADSWSConstants.MESSAGE_TYPE);
        assertTrue(false == resultDSWSFields.isEmpty());
        // Should be two dsws field
        resultDSWSFields = result.get(CPTADSWSConstants.MESSAGE_TYPE);
        assertTrue(false == resultDSWSFields.isEmpty());
        assertEquals(resultDSWSFields.size(), 2);
        resultDSWSField1Name = resultDSWSFields.get(0);
        assertEquals(resultDSWSField1Name, dswsField1.name);
        resultDSWSField2Name = resultDSWSFields.get(1);
        assertEquals(resultDSWSField2Name, dswsField2.name);
        // Should be one dss eod field
        resultEODFields = result.get(CPTADSSConstants.EOD_MESSAGE_TYPE);
        assertTrue(false == resultEODFields.isEmpty());
        assertEquals(resultEODFields.size(),1);
        // its name should be what was set
        resultEODField1Name = resultEODFields.get(0);
        assertEquals(resultEODField1Name, eodField1.name);        
    }

    /**
     * Test of getMessageByType method, of class CPTARefinitivGetData.
     */
    @Test
    public void testGetMessageByType() throws Exception
    {
        System.out.println("getMessageByType");
        // Get the instance
        CPTARefinitivGetData instance = CPTARefinitivGetData.getInstance();
        // DSS EOD 
        CPTARefinitivMessage messageClass = instance.getMessageByType(CPTADSSConstants.EOD_MESSAGE_TYPE);
        assertTrue(messageClass instanceof CPTADSSEODMessage);
        // DSS Corporate actions
        messageClass = instance.getMessageByType(CPTADSSConstants.CA_MESSAGE_TYPE);
        assertTrue(messageClass instanceof CPTADSSCorporateActionsMessage);
        // DSS time series
        messageClass = instance.getMessageByType(CPTADSSConstants.TS_MESSAGE_TYPE);
        assertTrue(messageClass instanceof CPTADSSTimeSeriesMessage);
        // DSS composite
        messageClass = instance.getMessageByType(CPTADSSConstants.COMPOSITE_MESSAGE_TYPE);
        assertTrue(messageClass instanceof CPTADSSCompositeMessage);
        // DSWS 
        messageClass = instance.getMessageByType(CPTADSWSConstants.MESSAGE_TYPE);
        assertTrue(messageClass instanceof CPTADSWSMessage);
    }

    /**
     * Test of createGlobalResponseFromList method, of class CPTARefinitivGetData.
     */
    @Test
    public void testCreateGlobalResponseFromList()
    {
        System.out.println("createGlobalResponseFromList");
        List<JsonArray> responsesFromEachMessage = new ArrayList<>();
        CPTARefinitivGetData instance = CPTARefinitivGetData.getInstance();
        String expResult = "";
        String result = instance.createGlobalResponseFromList(responsesFromEachMessage);
        System.out.println(result);
    }
    
}
