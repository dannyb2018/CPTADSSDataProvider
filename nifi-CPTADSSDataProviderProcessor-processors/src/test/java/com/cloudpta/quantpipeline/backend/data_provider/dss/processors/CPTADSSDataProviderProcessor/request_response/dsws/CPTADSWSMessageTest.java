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
package com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dsws;

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.CPTADSSDataProviderProcessorConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTADSSProperty;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTAFieldValueBlock;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import org.apache.nifi.processor.ProcessContext;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Danny
 */
public class CPTADSWSMessageTest
{
    /**
     * Test of buildTokenRequest method, of class CPTADSWSMessage.
     */
    @Test
    public void testBuildTokenRequest()
    {
        System.out.println("buildTokenRequest");
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        CPTADSWSMessage instance = new CPTADSWSMessage();
        String generatedTokenRequestAsString = instance.buildTokenRequest(userName, password);
        // Parse the result
        JsonObject generatedTokeRequest = Json.createReader(new StringReader(generatedTokenRequestAsString)).readObject();
        // Check properties exists
        JsonValue propertiesValue = generatedTokeRequest.get(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD);
        assertNotNull(propertiesValue);        
        // Check properties is null
        assertEquals(propertiesValue.getValueType(), ValueType.NULL);
        // Check user name exists
        JsonValue userNameValue = generatedTokeRequest.get(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_FIELD);
        assertNotNull(userNameValue);        
        // Check user name matches what was put in
        assertNotEquals(userNameValue.getValueType(), ValueType.NULL);
        String generatedUserName = generatedTokeRequest.getString(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_FIELD);
        assertEquals(userName, generatedUserName);
        // Check password exists
        JsonValue passwordValue = generatedTokeRequest.get(CPTADSSDataProviderProcessorConstants.DSWS_PASSSWORD_FIELD);
        assertNotNull(passwordValue);        
        // Check password matches what was put in
        assertNotEquals(passwordValue.getValueType(), ValueType.NULL);
        String generatedPassword = generatedTokeRequest.getString(CPTADSSDataProviderProcessorConstants.DSWS_PASSSWORD_FIELD);
        assertEquals(password, generatedPassword);
    }

    /**
     * Test of buildDataRequest method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testBuildDataRequest()
    {
        System.out.println("buildDataRequest");
        String authorisationToken = "";
        String symbolList = "";
        List<String> fields = null;
        List<CPTADSSProperty> properties = null;
        CPTADSWSMessage instance = new CPTADSWSMessage();
        String expResult = "";
        String result = instance.buildDataRequest(authorisationToken, symbolList, fields, properties);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDataRequestObject method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testGetDataRequestObject()
    {
        System.out.println("getDataRequestObject");
        String symbolList = "";
        List<String> fields = null;
        List<CPTADSSProperty> properties = null;
        CPTADSWSMessage instance = new CPTADSWSMessage();
        JsonObjectBuilder expResult = null;
        JsonObjectBuilder result = instance.getDataRequestObject(symbolList, fields, properties);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFieldsArrayBuilder method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testGetFieldsArrayBuilder()
    {
        System.out.println("getFieldsArrayBuilder");
        List<String> fields = null;
        CPTADSWSMessage instance = new CPTADSWSMessage();
        JsonArrayBuilder expResult = null;
        JsonArrayBuilder result = instance.getFieldsArrayBuilder(fields);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDateObjectBuilder method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testGetDateObjectBuilder()
    {
        System.out.println("getDateObjectBuilder");
        List<CPTADSSProperty> properties = null;
        CPTADSWSMessage instance = new CPTADSWSMessage();
        JsonObjectBuilder expResult = null;
        JsonObjectBuilder result = instance.getDateObjectBuilder(properties);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstrumentObjectBuilder method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testGetInstrumentObjectBuilder()
    {
        System.out.println("getInstrumentObjectBuilder");
        String symbolList = "";
        CPTADSWSMessage instance = new CPTADSWSMessage();
        JsonObjectBuilder expResult = null;
        JsonObjectBuilder result = instance.getInstrumentObjectBuilder(symbolList);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mergeDSWSResponseWithCPTAFormat method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testMergeDSWSResponseWithCPTAFormat()
    {
        System.out.println("mergeDSWSResponseWithCPTAFormat");
        JsonObject newDSWSData = null;
        JsonObjectBuilder existingDSWSDataInCPTAFormat = null;
        CPTADSWSMessage instance = new CPTADSWSMessage();
        instance.mergeDSWSResponseWithCPTAFormat(newDSWSData, existingDSWSDataInCPTAFormat);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDataResultDates method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testGetDataResultDates()
    {
        System.out.println("getDataResultDates");
        JsonObject dataResponseObject = null;
        CPTADSWSMessage instance = new CPTADSWSMessage();
        List<String> expResult = null;
        List<String> result = instance.getDataResultDates(dataResponseObject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getResultsByRic method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testGetResultsByRic()
    {
        System.out.println("getResultsByRic");
        JsonObject dataResponseObject = null;
        List<String> dates = null;
        CPTADSWSMessage instance = new CPTADSWSMessage();
        HashMap<String, List<CPTAFieldValueBlock>> expResult = null;
        HashMap<String, List<CPTAFieldValueBlock>> result = instance.getResultsByRic(dataResponseObject, dates);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getResult method, of class CPTADSWSMessage.
     */
/*    @Test
    public void testGetResult()
    {
        System.out.println("getResult");
        ProcessContext context = null;
        List<CPTAInstrumentSymbology> symbols = null;
        List<String> fields = null;
        List<CPTADSSProperty> properties = null;
        CPTADSWSMessage instance = new CPTADSWSMessage();
        JsonObject expResult = null;
        JsonObject result = instance.getResult(context, symbols, fields, properties);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

}
