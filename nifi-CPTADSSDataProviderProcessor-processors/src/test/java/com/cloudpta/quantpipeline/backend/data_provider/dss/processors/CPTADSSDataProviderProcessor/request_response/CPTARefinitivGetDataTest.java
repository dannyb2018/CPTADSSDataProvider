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

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
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
import javax.json.JsonObject;
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
        // Make one of the DSWS message type
        
        //  Make two of EOD message type
        // Make two of DSWS message type
        
        // Make one of the EOD message type and one of DSWS message type
        
        // Make one of the EOD message type and two of DSWS message type
        // Make one of the DSWS message type and two of EOD message type
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
        List<JsonObject> responsesFromEachMessage = null;
        CPTARefinitivGetData instance = null;
        String expResult = "";
//        String result = instance.createGlobalResponseFromList(responsesFromEachMessage);
    }
    
}
