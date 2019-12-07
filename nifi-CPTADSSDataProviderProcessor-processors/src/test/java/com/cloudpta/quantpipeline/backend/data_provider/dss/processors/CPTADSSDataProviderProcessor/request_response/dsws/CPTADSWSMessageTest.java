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

import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.CPTADSSDataProviderProcessorConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTADSSProperty;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTAFieldValue;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
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
    @Test
    public void testBuildDataRequest()
    {
        System.out.println("buildDataRequest");
        /* going to build this request
        {
            "DataRequest": 
            {
                "DataTypes": 
                [
                    {
                        "Properties": null,
                        "Value": a_random_string
                    }
                ],
                "Date": 
                {
                    "End": "-0D",
                    "Frequency": "D",
                    "Kind": 1,
                    "Start": "-0D"
                },
                "Instrument": 
                {
                    "Properties": null,
                    "Value": "<a_random_string>,<another_random_string>"
                },
                "Tag": null
            },
            "Properties": null,
            "TokenValue": a_random_string
        }
        */
        // token is random
        String authorisationToken = UUID.randomUUID().toString();
        // List is two random symbols
        String symbolList = "<" + UUID.randomUUID().toString() + ">,<" + UUID.randomUUID().toString() + ">";
        List<String> fields = new ArrayList<>();
        // field is one random field
        String field = UUID.randomUUID().toString();
        fields.add(field);
        // default properties
        List<CPTADSSProperty> properties = new ArrayList<>();
        CPTADSWSMessage instance = new CPTADSWSMessage();
        // Get the result
        String result = instance.buildDataRequest(authorisationToken, symbolList, fields, properties);
        assertNotNull(result);
        // Parse it
        JsonObject parsedRequest = Json.createReader(new StringReader(result)).readObject();
        assertNotNull(parsedRequest);

        // lets check properties are null
        assertTrue(parsedRequest.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        // lets check token isnt null and token is what we passed in        
        assertTrue(false == parsedRequest.isNull(CPTADSSDataProviderProcessorConstants.DSWS_TOKEN_VALUE_FIELD));
        String tokenValue = parsedRequest.getString(CPTADSSDataProviderProcessorConstants.DSWS_TOKEN_VALUE_FIELD);
        assertNotNull(tokenValue);
        assertEquals(tokenValue,authorisationToken);
        
        // next with data request object
        JsonObject dataRequest = parsedRequest.getJsonObject(CPTADSSDataProviderProcessorConstants.DSWS_DATA_REQUEST_FIELD);
        assertNotNull(dataRequest);
        
        // Tag should always be null
        assertTrue(dataRequest.isNull(CPTADSSDataProviderProcessorConstants.DSWS_TAG_FIELD));
        // Get datatypes
        JsonArray dataTypes = dataRequest.getJsonArray(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPES_FIELD);
        assertNotNull(dataTypes);
        // Has one object
        assertEquals(dataTypes.size(),1);
        JsonObject theDataType = dataTypes.getJsonObject(0);
        assertNotNull(theDataType);
        // properties of the object are null
        assertTrue(theDataType.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        // value is equal to the field we put in
        assertTrue(false == theDataType.isNull(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD));
        String value = theDataType.getString(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
        assertEquals(field, value);
        
        // Next get instrument
        JsonObject instrumentObject = dataRequest.getJsonObject(CPTADSSDataProviderProcessorConstants.DSWS_INSTRUMENT_FIELD);
        assertNotNull(instrumentObject);
        // The list should be what we passed in
        value = instrumentObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
        assertNotNull(value);
        assertEquals(symbolList, value);
        // The properties should be null
        assertTrue(instrumentObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        
        // Finally check date properties
        JsonObject dateProperties = dataRequest.getJsonObject(CPTADSSDataProviderProcessorConstants.DSWS_DATE_FIELD);
        assertNotNull(dateProperties);
        // Kind is always one
        int kind = dateProperties.getInt(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertEquals(kind, CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD_DEFAULT);
        // it is an EOD request by default
        // So end date is now and start date is now and frequency is day
        String endDate = dateProperties.getString(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertEquals(endDate, CPTADSSDataProviderProcessorConstants.DSWS_END_DATE_PROPERTY_DEFAULT);
        String startDate = dateProperties.getString(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertEquals(startDate, CPTADSSDataProviderProcessorConstants.DSWS_START_DATE_PROPERTY_DEFAULT);
        String frequency = dateProperties.getString(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertEquals(frequency, CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_PROPERTY_DEFAULT);
    }

    /**
     * Test of getFieldsArrayBuilder method, of class CPTADSWSMessage.
     */
    @Test
    public void testGetFieldsArrayBuilder()
    {
        System.out.println("getFieldsArrayBuilder");
        
        // Start with an empty list
        System.out.println("getFieldsArrayBuilder empty field list");
        List<String> inputFields = new ArrayList<>();
        CPTADSWSMessage instance = new CPTADSWSMessage();
        JsonArrayBuilder emptyFieldsArrayBuilder = instance.getFieldsArrayBuilder(inputFields);        
        // Shouldnt be null
        assertNotNull(emptyFieldsArrayBuilder);
        // Build the array
        JsonArray emptyFieldsArray = emptyFieldsArrayBuilder.build();
        // Should be empty
        assertEquals(emptyFieldsArray.size(), 0);
        
        // Then one field
        System.out.println("getFieldsArrayBuilder one field list");
        String field1 = UUID.randomUUID().toString();
        inputFields.add(field1);
        instance = new CPTADSWSMessage();
        JsonArrayBuilder oneFieldsArrayBuilder = instance.getFieldsArrayBuilder(inputFields);        
        // Shouldnt be null
        assertNotNull(oneFieldsArrayBuilder);
        // Build the array
        JsonArray oneFieldArray = oneFieldsArrayBuilder.build();
        // Should be have one field
        assertEquals(oneFieldArray.size(), 1);
        // Get that field
        JsonValue field1Converted = oneFieldArray.get(0);
        // Should a json object
        assertEquals(field1Converted.getValueType(), ValueType.OBJECT);
        // Get the object
        JsonObject field1ConvertedAsObject = field1Converted.asJsonObject();
        // Properties should be null
        assertTrue(field1ConvertedAsObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        // Value should be what we passed in as a field
        assertFalse(field1ConvertedAsObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD));
        String field1Result = field1ConvertedAsObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
        assertEquals(field1, field1Result);

        // Then three
        System.out.println("getFieldsArrayBuilder three field list");
        String field2 = UUID.randomUUID().toString();
        inputFields.add(field2);
        String field3 = UUID.randomUUID().toString();
        inputFields.add(field3);
        instance = new CPTADSWSMessage();
        JsonArrayBuilder threeFieldsArrayBuilder = instance.getFieldsArrayBuilder(inputFields);        
        // Shouldnt be null
        assertNotNull(threeFieldsArrayBuilder);
        // Build the array
        JsonArray threeFieldsArray = threeFieldsArrayBuilder.build();
        // Should be have three fields
        assertEquals(threeFieldsArray.size(), 3);
        // Get first field
        field1Converted = threeFieldsArray.get(0);
        // Should a json object
        assertEquals(field1Converted.getValueType(), ValueType.OBJECT);
        // Get the object
        field1ConvertedAsObject = field1Converted.asJsonObject();
        // Properties should be null
        assertTrue(field1ConvertedAsObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        // Value should be what we passed in as a field
        assertFalse(field1ConvertedAsObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD));
        field1Result = field1ConvertedAsObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
        assertEquals(field1, field1Result);
        // Get second field
        JsonValue field2Converted = threeFieldsArray.get(1);
        // Should a json object
        assertEquals(field2Converted.getValueType(), ValueType.OBJECT);
        // Get the object
        JsonObject field2ConvertedAsObject = field2Converted.asJsonObject();
        // Properties should be null
        assertTrue(field2ConvertedAsObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        // Value should be what we passed in as a field
        assertFalse(field2ConvertedAsObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD));
        String field2Result = field2ConvertedAsObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
        assertEquals(field2, field2Result);
        // Get third field
        JsonValue field3Converted = threeFieldsArray.get(2);
        // Should a json object
        assertEquals(field3Converted.getValueType(), ValueType.OBJECT);
        // Get the object
        JsonObject field3ConvertedAsObject = field3Converted.asJsonObject();
        // Properties should be null
        assertTrue(field3ConvertedAsObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        // Value should be what we passed in as a field
        assertFalse(field3ConvertedAsObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD));
        String field3Result = field3ConvertedAsObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
        assertEquals(field3, field3Result);
        
    }

    /**
     * Test of getDateObjectBuilder method, of class CPTADSWSMessage.
     */
    @Test
    public void testGetDateObjectBuilder()
    {
        System.out.println("getDateObjectBuilder");
        
        // Should start with default properties
        List<CPTADSSProperty> properties = new ArrayList<>();
        CPTADSWSMessage instance = new CPTADSWSMessage();        
        JsonObjectBuilder defaultDateBuilder = instance.getDateObjectBuilder(properties);
        // Then have today and 1 day before hand
        assertNotNull(defaultDateBuilder);
        JsonObject defaultDateObject = defaultDateBuilder.build();
        assertNotNull(defaultDateObject);
        // Kind always 1
        JsonValue kind = defaultDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertNotNull(kind);
        assertTrue(false == defaultDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD));        
        int kindValue = defaultDateObject.getInt(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertEquals(kindValue, CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD_DEFAULT);
        // End date check
        JsonValue endDateOffset = defaultDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertNotNull(endDateOffset);
        assertTrue(false == defaultDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD));        
        String endDateAsString = defaultDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertEquals(endDateAsString, CPTADSSDataProviderProcessorConstants.DSWS_END_DATE_PROPERTY_DEFAULT);
        // start date check
        JsonValue startDateOffset = defaultDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertNotNull(startDateOffset);
        assertTrue(false == defaultDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD));        
        String startDateAsString = defaultDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertEquals(startDateAsString, CPTADSSDataProviderProcessorConstants.DSWS_START_DATE_PROPERTY_DEFAULT);
        // Frequency check
        JsonValue frequency = defaultDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertNotNull(frequency);
        assertTrue(false == defaultDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD));        
        String frequencyAsString = defaultDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertEquals(frequencyAsString, CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_PROPERTY_DEFAULT);

        // 2 days before
        properties = new ArrayList<>();
        CPTADSSProperty newStartDateProperty = new CPTADSSProperty();
        newStartDateProperty.name = CPTADSSDataProviderProcessorConstants.CPTA_START_DATE_PROPERTY;
        newStartDateProperty.value = "-1D";
        properties.add(newStartDateProperty);
        instance = new CPTADSWSMessage();        
        JsonObjectBuilder oneDayBeforeDateBuilder = instance.getDateObjectBuilder(properties);
        // Then have today and 1 day before hand
        assertNotNull(oneDayBeforeDateBuilder);
        JsonObject oneDayBeforeDateObject = oneDayBeforeDateBuilder.build();
        assertNotNull(oneDayBeforeDateObject);
        // Kind always 1
        kind = oneDayBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertNotNull(kind);
        assertTrue(false == oneDayBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD));        
        kindValue = oneDayBeforeDateObject.getInt(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertEquals(kindValue, CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD_DEFAULT);
        // End date check
        endDateOffset = oneDayBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertNotNull(endDateOffset);
        assertTrue(false == oneDayBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD));        
        endDateAsString = oneDayBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertEquals(endDateAsString, CPTADSSDataProviderProcessorConstants.DSWS_END_DATE_PROPERTY_DEFAULT);
        // start date check
        startDateOffset = oneDayBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertNotNull(startDateOffset);
        assertTrue(false == oneDayBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD));        
        startDateAsString = oneDayBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertEquals(startDateAsString, newStartDateProperty.value);
        // Frequency check
        frequency = oneDayBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertNotNull(frequency);
        assertTrue(false == oneDayBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD));        
        frequencyAsString = oneDayBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertEquals(frequencyAsString, CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_PROPERTY_DEFAULT);

        // A week beforehand
        properties = new ArrayList<>();
        newStartDateProperty = new CPTADSSProperty();
        newStartDateProperty.name = CPTADSSDataProviderProcessorConstants.CPTA_START_DATE_PROPERTY;
        newStartDateProperty.value = "-1W";
        properties.add(newStartDateProperty);
        instance = new CPTADSWSMessage();        
        JsonObjectBuilder oneWeekBeforeDateBuilder = instance.getDateObjectBuilder(properties);
        // Then have today and 1 day before hand
        assertNotNull(oneWeekBeforeDateBuilder);
        JsonObject oneWeekBeforeDateObject = oneWeekBeforeDateBuilder.build();
        assertNotNull(oneWeekBeforeDateObject);
        // Kind always 1
        kind = oneWeekBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertNotNull(kind);
        assertTrue(false == oneWeekBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD));        
        kindValue = oneWeekBeforeDateObject.getInt(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertEquals(kindValue, CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD_DEFAULT);
        // End date check
        endDateOffset = oneWeekBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertNotNull(endDateOffset);
        assertTrue(false == oneWeekBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD));        
        endDateAsString = oneWeekBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertEquals(endDateAsString, CPTADSSDataProviderProcessorConstants.DSWS_END_DATE_PROPERTY_DEFAULT);
        // start date check
        startDateOffset = oneWeekBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertNotNull(startDateOffset);
        assertTrue(false == oneWeekBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD));        
        startDateAsString = oneWeekBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertEquals(startDateAsString, newStartDateProperty.value);
        // Frequency check
        frequency = oneWeekBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertNotNull(frequency);
        assertTrue(false == oneWeekBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD));        
        frequencyAsString = oneWeekBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertEquals(frequencyAsString, CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_PROPERTY_DEFAULT);
        
        // 1 month by weeks beforehand
        properties = new ArrayList<>();
        newStartDateProperty = new CPTADSSProperty();
        newStartDateProperty.name = CPTADSSDataProviderProcessorConstants.CPTA_START_DATE_PROPERTY;
        newStartDateProperty.value = "-1M";
        properties.add(newStartDateProperty);
        CPTADSSProperty newFrequencyProperty = new CPTADSSProperty();
        newFrequencyProperty.name = CPTADSSDataProviderProcessorConstants.CPTA_FREQUENCY_PROPERTY;
        newFrequencyProperty.value = "W";
        properties.add(newFrequencyProperty);
        instance = new CPTADSWSMessage();        
        JsonObjectBuilder oneMonthBeforeDateBuilder = instance.getDateObjectBuilder(properties);
        // Then have today and 1 day before hand
        assertNotNull(oneMonthBeforeDateBuilder);
        JsonObject oneMonthBeforeDateObject = oneMonthBeforeDateBuilder.build();
        assertNotNull(oneMonthBeforeDateObject);
        // Kind always 1
        kind = oneMonthBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertNotNull(kind);
        assertTrue(false == oneMonthBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD));        
        kindValue = oneMonthBeforeDateObject.getInt(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertEquals(kindValue, CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD_DEFAULT);
        // End date check
        endDateOffset = oneMonthBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertNotNull(endDateOffset);
        assertTrue(false == oneMonthBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD));        
        endDateAsString = oneMonthBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertEquals(endDateAsString, CPTADSSDataProviderProcessorConstants.DSWS_END_DATE_PROPERTY_DEFAULT);
        // start date check
        startDateOffset = oneMonthBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertNotNull(startDateOffset);
        assertTrue(false == oneMonthBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD));        
        startDateAsString = oneMonthBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertEquals(startDateAsString, newStartDateProperty.value);
        // Frequency check
        frequency = oneMonthBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertNotNull(frequency);
        assertTrue(false == oneMonthBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD));        
        frequencyAsString = oneMonthBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertEquals(frequencyAsString, newFrequencyProperty.value);
        
        // 1 year by months beforehand
        properties = new ArrayList<>();
        newStartDateProperty = new CPTADSSProperty();
        newStartDateProperty.name = CPTADSSDataProviderProcessorConstants.CPTA_START_DATE_PROPERTY;
        newStartDateProperty.value = "-1Y";
        properties.add(newStartDateProperty);
        newFrequencyProperty = new CPTADSSProperty();
        newFrequencyProperty.name = CPTADSSDataProviderProcessorConstants.CPTA_FREQUENCY_PROPERTY;
        newFrequencyProperty.value = "M";
        properties.add(newFrequencyProperty);
        instance = new CPTADSWSMessage();        
        JsonObjectBuilder oneYearBeforeDateBuilder = instance.getDateObjectBuilder(properties);
        // Then have today and 1 day before hand
        assertNotNull(oneYearBeforeDateBuilder);
        JsonObject oneYearBeforeDateObject = oneYearBeforeDateBuilder.build();
        assertNotNull(oneYearBeforeDateObject);
        // Kind always 1
        kind = oneYearBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertNotNull(kind);
        assertTrue(false == oneYearBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD));        
        kindValue = oneYearBeforeDateObject.getInt(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD);
        assertEquals(kindValue, CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD_DEFAULT);
        // End date check
        endDateOffset = oneYearBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertNotNull(endDateOffset);
        assertTrue(false == oneYearBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD));        
        endDateAsString = oneYearBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD);
        assertEquals(endDateAsString, CPTADSSDataProviderProcessorConstants.DSWS_END_DATE_PROPERTY_DEFAULT);
        // start date check
        startDateOffset = oneYearBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertNotNull(startDateOffset);
        assertTrue(false == oneYearBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD));        
        startDateAsString = oneYearBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD);
        assertEquals(startDateAsString, newStartDateProperty.value);
        // Frequency check
        frequency = oneYearBeforeDateObject.get(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertNotNull(frequency);
        assertTrue(false == oneYearBeforeDateObject.isNull(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD));        
        frequencyAsString = oneYearBeforeDateObject.getString(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD);
        assertEquals(frequencyAsString, newFrequencyProperty.value);
        
    }

    /**
     * Test of getInstrumentObjectBuilder method, of class CPTADSWSMessage.
     */
    @Test
    public void testGetInstrumentObjectBuilder()
    {
        System.out.println("getInstrumentObjectBuilder");
        
        // Start with no symbols
        System.out.println("getInstrumentObjectBuilder no symbols");
        String symbolList = "";
        CPTADSWSMessage instance = new CPTADSWSMessage();
        JsonObjectBuilder emptyInstrumentBuilder = instance.getInstrumentObjectBuilder(symbolList);
        assertNotNull(emptyInstrumentBuilder);
        JsonObject emptyInstrument = emptyInstrumentBuilder.build();
        // Get properties
        JsonValue properties = emptyInstrument.get(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD);
        // Should exist but be null entry 
        assertNotNull(properties);
        assertTrue(emptyInstrument.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        // Get instrument list
        String emptySymboltList = emptyInstrument.getString(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
        // should be empty
        assertNotNull(emptySymboltList);
        assertTrue(false == emptyInstrument.isNull(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD));
        assertEquals(emptySymboltList, "");

        // The random string for symbols
        System.out.println("getInstrumentObjectBuilder random symbols");
        symbolList = UUID.randomUUID().toString();
        instance = new CPTADSWSMessage();        
        JsonObjectBuilder randomInstrumentBuilder = instance.getInstrumentObjectBuilder(symbolList);
        assertNotNull(randomInstrumentBuilder);
        JsonObject randomInstrument = randomInstrumentBuilder.build();
        // Get properties
        properties = randomInstrument.get(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD);
        // Should exist but be null entry 
        assertNotNull(properties);
        assertTrue(randomInstrument.isNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD));
        // Get instrument list
        String randomSymbolList = randomInstrument.getString(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
        // should be same as the list string passed in
        assertNotNull(randomSymbolList);
        assertEquals(randomSymbolList, symbolList);
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
    @Test
    public void testGetDataResultDates()
    {
        // DS date format is \/Date(time_in_millis+0000)\/
        // \/Date(1574985600000+0000)\/
        System.out.println("getDataResultDates");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CPTADSSDataProviderProcessorConstants.DATA_RESPONSE_DATE_FORMAT);
        
        // Build basic response
        JsonArrayBuilder jsonDateArray = Json.createArrayBuilder();
        JsonObjectBuilder jsonDataResponse = Json.createObjectBuilder();
        jsonDataResponse.add("Dates", jsonDateArray);
        
        JsonObject dataResponseObject = jsonDataResponse.build();        
        CPTADSWSMessage instance = new CPTADSWSMessage();

        // Pass in a result with no dates
        List<String> emptyDateResult = instance.getDataResultDates(dataResponseObject);
        // Should be an empty array
        assertNotNull(emptyDateResult);
        assertEquals(emptyDateResult.size(), 0);
        
        // Pass a result in with 1 date
        // This will be now
        Calendar now = Calendar.getInstance();
        // Turn into milliseconds
        long nowAsMilliseconds = now.getTimeInMillis();
        String nowAsDSWSDate = "\\/Date(" + Long.toString(nowAsMilliseconds) + "+0000)\\/";
        jsonDateArray.add(nowAsDSWSDate);
        jsonDataResponse.add("Dates", jsonDateArray);
        dataResponseObject = jsonDataResponse.build();
        List<String> oneDateResult = instance.getDataResultDates(dataResponseObject);
        // Should be an empty array
        assertNotNull(oneDateResult);
        assertEquals(oneDateResult.size(), 1);
        String expectedResult = simpleDateFormat.format(now.getTime());
        assertEquals(oneDateResult.get(0), expectedResult);
        // Pass in a result with 10 dates
    }

    /**
     * Test of getResultsByRic method, of class CPTADSWSMessage.
     */
    @Test
    public void testGetResultsByRic()
    {
        
        System.out.println("getResultsByRic");

        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CPTADSSDataProviderProcessorConstants.DATA_RESPONSE_DATE_FORMAT);
        String nowDate = simpleDateFormat.format(now.getTime());
        
        // Start by handling a empty result
        JsonObjectBuilder dataResponseObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder dataTypeValuesBuilder = Json.createArrayBuilder();   
        dataResponseObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_VALUES_FIELD, dataTypeValuesBuilder);
        JsonObject dataResponseObject = dataResponseObjectBuilder.build();
        List<String> dates = new ArrayList<>();
        CPTADSWSMessage instance = new CPTADSWSMessage();
        HashMap<String, List<CPTAFieldValue>> result = instance.getResultsByRic(dataResponseObject, dates);
        // Result should be empty        
        assertTrue(result.isEmpty());

        // The date is just one, today
        dates = new ArrayList<>();
        dates.add(nowDate);

        // handle a result with one ric
        String field1 = UUID.randomUUID().toString();
        String ric1 = UUID.randomUUID().toString();
        double field1Ric1Value1 = Math.random();
        // build the symbol values
        JsonObjectBuilder ric1SymbolValueBuilder = Json.createObjectBuilder();
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 10);
        // The symbols are ric codes within brackets
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric1 + ">");
        JsonArrayBuilder ric1Field1ValuesBuilder = Json.createArrayBuilder();   
        ric1Field1ValuesBuilder.add(field1Ric1Value1);
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric1Field1ValuesBuilder);
        JsonArrayBuilder field1SymbolValuesBuilder = Json.createArrayBuilder();
        field1SymbolValuesBuilder.add(ric1SymbolValueBuilder);
        JsonObjectBuilder field1ResponseBuilder = Json.createObjectBuilder();
        // This is the field
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_FIELD, field1);
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_VALUES_FIELD, field1SymbolValuesBuilder);
        dataTypeValuesBuilder = Json.createArrayBuilder();   
        // Add this to the list of field responses
        dataTypeValuesBuilder.add(field1ResponseBuilder);
        dataResponseObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_VALUES_FIELD, dataTypeValuesBuilder);
        dataResponseObject = dataResponseObjectBuilder.build();
        instance = new CPTADSWSMessage();
        result = instance.getResultsByRic(dataResponseObject, dates);
        // Cant be empty
        assertTrue(false == result.isEmpty());
        List<CPTAFieldValue> ric1Result = result.get(ric1);
        assertNotNull(ric1Result);
        // Should be just one block
        assertEquals(ric1Result.size(), 1);
        CPTAFieldValue resultBlock11 = ric1Result.get(0);
        assertEquals(resultBlock11.name, field1);
        assertEquals(resultBlock11.date, nowDate);
        assertEquals(resultBlock11.value, Double.toString(field1Ric1Value1));
        
        // handle a result with one ric that has a datapoint and one ric that has an error
        String ric2 = UUID.randomUUID().toString();
        // build the symbol values for second ric
        JsonObjectBuilder ric2SymbolValueBuilder = Json.createObjectBuilder();
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 14);
        // The symbols are ric codes within brackets
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric2 + ">");
        JsonArrayBuilder ric2Field1ValuesBuilder = Json.createArrayBuilder();   
        ric2Field1ValuesBuilder.addNull();
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric2Field1ValuesBuilder);
        JsonArrayBuilder field1Ric2SymbolValuesBuilder = Json.createArrayBuilder();
        field1Ric2SymbolValuesBuilder.add(ric1SymbolValueBuilder);
        field1ResponseBuilder = Json.createObjectBuilder();
        // This is the first ric
        ric1SymbolValueBuilder = Json.createObjectBuilder();
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 10);
        // The symbols are ric codes within brackets
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric1 + ">");
        ric1Field1ValuesBuilder = Json.createArrayBuilder();   
        ric1Field1ValuesBuilder.add(field1Ric1Value1);
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric1Field1ValuesBuilder);
        field1SymbolValuesBuilder = Json.createArrayBuilder();
        field1SymbolValuesBuilder.add(ric1SymbolValueBuilder);
        field1SymbolValuesBuilder.add(ric2SymbolValueBuilder);
        dataTypeValuesBuilder = Json.createArrayBuilder();   
        // Now add the field response
        field1ResponseBuilder = Json.createObjectBuilder();
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_FIELD, field1);
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_VALUES_FIELD, field1SymbolValuesBuilder);
        // Add this to the list of field responses
        dataTypeValuesBuilder.add(field1ResponseBuilder);
        dataResponseObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_VALUES_FIELD, dataTypeValuesBuilder);
        dataResponseObject = dataResponseObjectBuilder.build();
        result = instance.getResultsByRic(dataResponseObject, dates);
        // Cant be empty
        assertTrue(false == result.isEmpty());
        ric1Result = result.get(ric1);
        assertNotNull(ric1Result);
        // Should be just one block
        assertEquals(ric1Result.size(), 1);
        resultBlock11 = ric1Result.get(0);
        assertEquals(resultBlock11.name, field1);
        assertEquals(resultBlock11.date, nowDate);
        assertEquals(resultBlock11.value, Double.toString(field1Ric1Value1));
        
        // handle result with one ric with an error
        // build the symbol values for second ric
        ric2SymbolValueBuilder = Json.createObjectBuilder();
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 14);
        // The symbols are ric codes within brackets
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric2 + ">");
        ric2Field1ValuesBuilder = Json.createArrayBuilder();   
        ric2Field1ValuesBuilder.addNull();
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric2Field1ValuesBuilder);
        field1Ric2SymbolValuesBuilder = Json.createArrayBuilder();
        field1Ric2SymbolValuesBuilder.add(ric1SymbolValueBuilder);
        field1ResponseBuilder = Json.createObjectBuilder();
        field1SymbolValuesBuilder = Json.createArrayBuilder();
        field1SymbolValuesBuilder.add(ric2SymbolValueBuilder);
        dataTypeValuesBuilder = Json.createArrayBuilder();   
        // Now add the field response
        field1ResponseBuilder = Json.createObjectBuilder();
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_FIELD, field1);
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_VALUES_FIELD, field1SymbolValuesBuilder);
        // Add this to the list of field responses
        dataTypeValuesBuilder.add(field1ResponseBuilder);
        dataResponseObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_VALUES_FIELD, dataTypeValuesBuilder);
        dataResponseObject = dataResponseObjectBuilder.build();
        result = instance.getResultsByRic(dataResponseObject, dates);
        // Should be empty
        assertTrue(result.isEmpty());
        
        // handle two rics with an error
        // build the symbol values for second ric
        ric2SymbolValueBuilder = Json.createObjectBuilder();
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 14);
        // The symbols are ric codes within brackets
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric2 + ">");
        ric2Field1ValuesBuilder = Json.createArrayBuilder();   
        ric2Field1ValuesBuilder.addNull();
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric2Field1ValuesBuilder);
        field1Ric2SymbolValuesBuilder = Json.createArrayBuilder();
        field1Ric2SymbolValuesBuilder.add(ric1SymbolValueBuilder);
        field1ResponseBuilder = Json.createObjectBuilder();
        // This is the first ric
        ric1SymbolValueBuilder = Json.createObjectBuilder();
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 14);
        // The symbols are ric codes within brackets
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric1 + ">");
        ric1Field1ValuesBuilder = Json.createArrayBuilder();   
        ric1Field1ValuesBuilder.addNull();
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric1Field1ValuesBuilder);
        field1SymbolValuesBuilder = Json.createArrayBuilder();
        field1SymbolValuesBuilder.add(ric1SymbolValueBuilder);
        field1SymbolValuesBuilder.add(ric2SymbolValueBuilder);
        dataTypeValuesBuilder = Json.createArrayBuilder();   
        // Now add the field response
        field1ResponseBuilder = Json.createObjectBuilder();
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_FIELD, field1);
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_VALUES_FIELD, field1SymbolValuesBuilder);
        // Add this to the list of field responses
        dataTypeValuesBuilder.add(field1ResponseBuilder);
        dataResponseObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_VALUES_FIELD, dataTypeValuesBuilder);
        dataResponseObject = dataResponseObjectBuilder.build();
        result = instance.getResultsByRic(dataResponseObject, dates);
        assertTrue(result.isEmpty());
        
        // handle two rics with a datapoint
        double field1Ric2Value1 = Math.random();
        // build the symbol values for second ric
        ric2SymbolValueBuilder = Json.createObjectBuilder();
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 10);
        // The symbols are ric codes within brackets
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric2 + ">");
        ric2Field1ValuesBuilder = Json.createArrayBuilder();   
        ric2Field1ValuesBuilder.add(field1Ric2Value1);
        ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric2Field1ValuesBuilder);
        field1Ric2SymbolValuesBuilder = Json.createArrayBuilder();
        field1Ric2SymbolValuesBuilder.add(ric1SymbolValueBuilder);
        field1ResponseBuilder = Json.createObjectBuilder();
        // This is the first ric
        ric1SymbolValueBuilder = Json.createObjectBuilder();
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 10);
        // The symbols are ric codes within brackets
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric1 + ">");
        ric1Field1ValuesBuilder = Json.createArrayBuilder();   
        ric1Field1ValuesBuilder.add(field1Ric1Value1);
        ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric1Field1ValuesBuilder);
        field1SymbolValuesBuilder = Json.createArrayBuilder();
        field1SymbolValuesBuilder.add(ric1SymbolValueBuilder);
        field1SymbolValuesBuilder.add(ric2SymbolValueBuilder);
        dataTypeValuesBuilder = Json.createArrayBuilder();   
        // Now add the field response
        field1ResponseBuilder = Json.createObjectBuilder();
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_FIELD, field1);
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_VALUES_FIELD, field1SymbolValuesBuilder);
        // Add this to the list of field responses
        dataTypeValuesBuilder.add(field1ResponseBuilder);
        dataResponseObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_VALUES_FIELD, dataTypeValuesBuilder);
        dataResponseObject = dataResponseObjectBuilder.build();
        result = instance.getResultsByRic(dataResponseObject, dates);
        assertTrue(false == result.isEmpty());
        // Should have two rics
        assertEquals(result.keySet().size(), 2);
        // Get first ric
        List<CPTAFieldValue> ric1Values = result.get(ric1);
        assertNotNull(ric1Values);
        // Only one value
        assertEquals(ric1Values.size(),1);
        // should be the set one with the set date
        assertEquals(ric1Values.get(0).name, field1);
        assertEquals(ric1Values.get(0).date, nowDate);
        assertEquals(ric1Values.get(0).value, Double.toString(field1Ric1Value1));

        // Get second ric
        List<CPTAFieldValue> ric2Values = result.get(ric2);
        assertNotNull(ric2Values);
        // Only one value
        assertEquals(ric2Values.size(),1);
        // should be the set one with the set date
        assertEquals(ric2Values.get(0).name, field1);
        assertEquals(ric2Values.get(0).date, nowDate);
        assertEquals(ric2Values.get(0).value, Double.toString(field1Ric2Value1));

        // handle two rics with two datapoints
        String field2 = UUID.randomUUID().toString();
        double field2Ric1Value1 = Math.random();
        double field2Ric2Value1 = Math.random();
        // Start with first field
        // build the symbol values for second ric
        JsonObjectBuilder field1Ric2SymbolValueBuilder = Json.createObjectBuilder();
        field1Ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 10);
        // The symbols are ric codes within brackets
        field1Ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric2 + ">");
        ric2Field1ValuesBuilder = Json.createArrayBuilder();   
        ric2Field1ValuesBuilder.add(field1Ric2Value1);
        field1Ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric2Field1ValuesBuilder);
        // This is the first ric
        JsonObjectBuilder field1Ric1SymbolValueBuilder = Json.createObjectBuilder();
        field1Ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 10);
        // The symbols are ric codes within brackets
        field1Ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric1 + ">");
        ric1Field1ValuesBuilder = Json.createArrayBuilder();   
        ric1Field1ValuesBuilder.add(field1Ric1Value1);
        field1Ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric1Field1ValuesBuilder);
        // add both values for field1
        field1SymbolValuesBuilder = Json.createArrayBuilder();
        field1SymbolValuesBuilder.add(field1Ric1SymbolValueBuilder);
        field1SymbolValuesBuilder.add(field1Ric2SymbolValueBuilder);
        // Now add the field response
        field1ResponseBuilder = Json.createObjectBuilder();
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_FIELD, field1);
        field1ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_VALUES_FIELD, field1SymbolValuesBuilder);
        // Now do same for second field
        // build the symbol values for second ric
        JsonObjectBuilder field2Ric2SymbolValueBuilder = Json.createObjectBuilder();
        field2Ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 10);
        // The symbols are ric codes within brackets
        field2Ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric2 + ">");
        JsonArrayBuilder ric2Field2ValuesBuilder = Json.createArrayBuilder();   
        ric2Field2ValuesBuilder.add(field2Ric2Value1);
        field2Ric2SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric2Field2ValuesBuilder);
        // This is the first ric
        JsonObjectBuilder field2Ric1SymbolValueBuilder = Json.createObjectBuilder();
        field2Ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD, 10);
        // The symbols are ric codes within brackets
        field2Ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD, "<" + ric1 + ">");
        JsonArrayBuilder ric1Field2ValuesBuilder = Json.createArrayBuilder();   
        ric1Field2ValuesBuilder.add(field2Ric1Value1);
        field2Ric1SymbolValueBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, ric1Field2ValuesBuilder);
        JsonArrayBuilder field2SymbolValuesBuilder = Json.createArrayBuilder();
        field2SymbolValuesBuilder.add(field2Ric1SymbolValueBuilder);
        field2SymbolValuesBuilder.add(field2Ric2SymbolValueBuilder);
        // Now add the field response
        JsonObjectBuilder field2ResponseBuilder = Json.createObjectBuilder();
        field2ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_FIELD, field2);
        field2ResponseBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_VALUES_FIELD, field2SymbolValuesBuilder);
        // Add this to the list of field responses
        dataTypeValuesBuilder = Json.createArrayBuilder();   
        dataTypeValuesBuilder.add(field1ResponseBuilder);
        dataTypeValuesBuilder.add(field2ResponseBuilder);
        dataResponseObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_VALUES_FIELD, dataTypeValuesBuilder);
        dataResponseObject = dataResponseObjectBuilder.build();
        result = instance.getResultsByRic(dataResponseObject, dates);
        // Shouldnt be empty
        assertTrue(false == result.isEmpty());
        // Should have two rics
        assertEquals(result.keySet().size(),2);
        // Get result for first ric
        ric1Values = result.get(ric1);
        // should have two fields
        assertEquals(ric1Values.size(),2);
        assertEquals(ric1Values.get(0).name, field1);
        assertEquals(ric1Values.get(0).date, nowDate);
        assertEquals(ric1Values.get(0).value, Double.toString(field1Ric1Value1));
        assertEquals(ric1Values.get(1).name, field2);
        assertEquals(ric1Values.get(1).date, nowDate);
        assertEquals(ric1Values.get(1).value, Double.toString(field2Ric1Value1));
        // Get result for second ric
        ric2Values = result.get(ric2);
        // should have two fields
        assertEquals(ric2Values.size(),2);
        assertEquals(ric2Values.get(0).name, field1);
        assertEquals(ric2Values.get(0).date, nowDate);
        assertEquals(ric2Values.get(0).value, Double.toString(field1Ric2Value1));
        assertEquals(ric2Values.get(1).name, field2);
        assertEquals(ric2Values.get(1).date, nowDate);
        assertEquals(ric2Values.get(1).value, Double.toString(field2Ric2Value1));
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
