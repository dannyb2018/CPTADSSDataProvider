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
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTAFieldValue;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTARefinitivMessage;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public class CPTADSWSMessage extends CPTARefinitivMessage
{
    @Override
    public JsonObject getResult
                              (
                              ProcessContext context, 
                              List<CPTAInstrumentSymbology> symbols, 
                              List<String> fields, 
                              List<CPTADSSProperty> properties
                              )
    {
        String userName = context.getProperty(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY).getValue();
        String password = context.getProperty(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY).getValue();
        String baseURL = context.getProperty(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY).getValue();

        JsonObjectBuilder cptaDataResponse  = Json.createObjectBuilder();
        
        // Can only do 50 rics at a time
        // So need to keep count of how many we have done already
        int ricsOffset = 0;
        // Whilst there are more rics to query for
        while(symbols.size() > ricsOffset)
        {
            // Take out at most first 50 rics
            int upperLimit = Math.min(symbols.size(), ricsOffset + 50);
            List<CPTAInstrumentSymbology> ricsToGet = (List<CPTAInstrumentSymbology>) symbols.subList(ricsOffset, upperLimit);     
            // Update the offset to where we are
            ricsOffset = upperLimit;
            
            // Add each one
            String symbolList = "";
            for( CPTAInstrumentSymbology ric: ricsToGet)
            {
                // For Datascope to use rics needs to be of the form <RIC> 
                // Comma to make it a list of rics
                symbolList = symbolList + ",<" + ric.getID() + ">";
            }
            // Got a spare comma at the start so remove it
            symbolList = symbolList.substring(1);

            // We need to get an authorisation token first
            String authorisationToken = getToken(baseURL, userName, password);
            // Now request the data
            JsonObject dswsDataResponse = getData
                                                (
                                                baseURL, 
                                                authorisationToken, 
                                                symbolList, 
                                                fields, 
                                                properties
                                                );
            // Add it to the response
            mergeDSWSResponseWithCPTAFormat(dswsDataResponse, cptaDataResponse);
        }

        // convert to a json object
        JsonObject cptaResponseAsJson = cptaDataResponse.build();
        return cptaResponseAsJson;
    }
    
    protected String getToken(String baseURL, String userName, String password)
    {
        // Get the token request 
        String tokenRequestAsString = buildTokenRequest(userName, password);
        
        // Make the request
        Client tokenRequestClient = javax.ws.rs.client.ClientBuilder.newClient();             
        String tokenRequestURL = baseURL + CPTADSSDataProviderProcessorConstants.DSWS_GET_TOKEN;
        WebTarget tokenRequestTarget = tokenRequestClient.target(tokenRequestURL);
        // Get response
        Response tokenRequestRespinse = tokenRequestTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(tokenRequestAsString));
        // If the response is not ok then we have a problem
        // BUGBUGDB write a handler for this
        
        // If response is ok then get the token
        String tokenResponseString = tokenRequestRespinse.readEntity(String.class);
        JsonReader JsonReader = Json.createReader(new StringReader(tokenResponseString));
        JsonObject tokenResponseAsJson = JsonReader.readObject();
        String token = tokenResponseAsJson.getString(CPTADSSDataProviderProcessorConstants.DSWS_TOKEN_VALUE_FIELD, "");
        
        return token;
    }
    
    protected JsonObject getData
                               (
                               String baseURL, 
                               String authorisationToken, 
                               String symbolList, 
                               List<String> fields, 
                               List<CPTADSSProperty> properties
                               )
    {        
        // Get the data request string
        String dataRequestAsString = buildDataRequest
                                                    (
                                                    authorisationToken, 
                                                    symbolList, 
                                                    fields, 
                                                    properties
                                                    );
        // Make actual request
        Client dataRequestClient = javax.ws.rs.client.ClientBuilder.newClient(); 
        String dataRequestURL = baseURL + CPTADSSDataProviderProcessorConstants.DSWS_GET_DATA;
        WebTarget dataRequestTarget = dataRequestClient.target(dataRequestURL);
        Response dataRequestResponse = dataRequestTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(dataRequestAsString));
        // If the response is not ok then we have a problem
        // BUGBUGDB write a handle for this
        String dataRequestResponseAsString = dataRequestResponse.readEntity(String.class);
        // Parse the response
        JsonReader JsonReader = Json.createReader(new StringReader(dataRequestResponseAsString));
        JsonObject dataRequestResponseAsJson = JsonReader.readObject();
        
        // Hand the response back
        return dataRequestResponseAsJson;
    }
    
    protected String buildTokenRequest(String userName, String password)
    {
        // Start passing in the the user name and password
        JsonObjectBuilder tokenRequestBuilder  = Json.createObjectBuilder();
        // The request is user name
        tokenRequestBuilder.add
                              (
                              CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_FIELD, 
                              userName
                              );
        // Password
        tokenRequestBuilder.add
                              (
                              CPTADSSDataProviderProcessorConstants.DSWS_PASSSWORD_FIELD, 
                              password
                              );
        // And properties, which for us is always null
        tokenRequestBuilder.addNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD);
        // Turn the JsonObject into a string so we can make a request
        String tokenRequestAsString = tokenRequestBuilder.build().toString();
        
        return tokenRequestAsString;        
    }

    protected String buildDataRequest
                                    (
                                    String authorisationToken, 
                                    String symbolList, 
                                    List<String> fields, 
                                    List<CPTADSSProperty> properties
                                    )
    {
        JsonObjectBuilder dataRequestBuilder  = Json.createObjectBuilder();
        // Now we can make a request
        // So the structure of the request is as follows
        // {
        //      DataRequest: "
        //      {
        //          This is an array of the fields we want in value property
        //          DataTypes:
        //          [
        //              {
        //                  Properties: null,Value: what_ever_field_we_want
        //              }
        //          ],
        //          Format of date is from offset from today, to offset from today
        //          frequecy is interval type and Kind is number of those intervals
        //          Date:
        //          {
        //              End: to_this_date_or_offset,
        //              Frequency: D|Y|M,
        //              Kind: 1,
        //              Start: from_this_date_or_offset
        //          },
        //          Single list of instruments as setup above
        //          Instrument:
        //          {
        //              Properties: null,
        //              Value: this_is_the_symbol_list_above
        //          },
        //          Tag: null
        //      },
        //      Properties: null,
        //      TokenValue:  whatever_token_was_returned_above
        // }            
        // Start with the values at the end, token and properties
        dataRequestBuilder.add
                             (
                             CPTADSSDataProviderProcessorConstants.DSWS_TOKEN_VALUE_FIELD, 
                             authorisationToken
                             );
        dataRequestBuilder.addNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD);
        // Add data request object
        JsonObjectBuilder dataRequestObjectBuilder = getDataRequestObject(symbolList, fields, properties);
        dataRequestBuilder.add
                             (
                             CPTADSSDataProviderProcessorConstants.DSWS_DATA_REQUEST_FIELD, 
                             dataRequestObjectBuilder
                             );
        
        // Build the data request string
        String dataRequestAsString = dataRequestBuilder.build().toString();
        return dataRequestAsString;        
    }
    
    protected JsonObjectBuilder getDataRequestObject
                                                   (
                                                   String symbolList, 
                                                   List<String> fields, 
                                                   List<CPTADSSProperty> properties
                                                   )
    {
        JsonObjectBuilder dataRequestObjectBuilder  = Json.createObjectBuilder();
        // Add datatypes aka fields
        JsonArrayBuilder fieldsArrayBuilder = getFieldsArrayBuilder(fields);
        dataRequestObjectBuilder.add
                                   (
                                   CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPES_FIELD, 
                                   fieldsArrayBuilder
                                   );
        // Add Date aka properties
        JsonObjectBuilder dataBuilder = getDateObjectBuilder(properties);
        dataRequestObjectBuilder.add
                                   (
                                   CPTADSSDataProviderProcessorConstants.DSWS_DATE_FIELD, 
                                   dataBuilder
                                   );
        // Add instruments
        JsonObjectBuilder instrumentBuilder = getInstrumentObjectBuilder(symbolList);
        dataRequestObjectBuilder.add
                                   (
                                   CPTADSSDataProviderProcessorConstants.DSWS_INSTRUMENT_FIELD, 
                                   instrumentBuilder
                                   );
        // Finally add tag which is just null
        dataRequestObjectBuilder.addNull(CPTADSSDataProviderProcessorConstants.DSWS_TAG_FIELD);
        
        // Hand back the builder
        return dataRequestObjectBuilder;
    }
    
    protected JsonArrayBuilder getFieldsArrayBuilder(List<String> fields)
    {
        JsonArrayBuilder fieldsArrayBuilder = Json.createArrayBuilder();
        // Fields are an array of json object like this
        //              {
        //                  Properties: null,Value: what_ever_field_we_want
        //              }
        
        // Loop through all the fields
        for(String currentField : fields)
        {
            // Create the object
            JsonObjectBuilder currentFieldObjectBuidler = Json.createObjectBuilder();
            // Properties always null for us
            currentFieldObjectBuidler.addNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD);
            // Value is the field
            currentFieldObjectBuidler.add
                                        (
                                        CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, 
                                        currentField
                                        );
            
            // Add object to the array
            fieldsArrayBuilder.add(currentFieldObjectBuidler);
        }
        
        // Hand the fields array builder up
        return fieldsArrayBuilder;
    }
    
    protected JsonObjectBuilder getDateObjectBuilder(List<CPTADSSProperty> properties)
    {
        // BUGBUGDB should probably do some error checking to ensure the properties 
        // are correct format and values
        
        //          Format of date is from offset from today, to offset from today
        //          frequency is interval type and Kind is number of those intervals
        //          Date:
        //          {
        //              End: to_this_date_or_offset,
        //              Frequency: D|Y|M,
        //              Kind: 1,
        //              Start: from_this_date_or_offset
        //          }
        JsonObjectBuilder dateObjectBuilder = Json.createObjectBuilder();
        // For now kind is always 1
        dateObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD, CPTADSSDataProviderProcessorConstants.DSWS_KIND_FIELD_DEFAULT);
        
        // Default is the last day only
        dateObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD, CPTADSSDataProviderProcessorConstants.DSWS_END_DATE_PROPERTY_DEFAULT);
        dateObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD, CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_PROPERTY_DEFAULT);
        dateObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD, CPTADSSDataProviderProcessorConstants.DSWS_START_DATE_PROPERTY_DEFAULT);
        
        // Loop through properties
        for(CPTADSSProperty currentProperty : properties )
        {
            // If it is frequency
            if( 0 == currentProperty.name.compareTo(CPTADSSDataProviderProcessorConstants.CPTA_FREQUENCY_PROPERTY))
            {
                // Add Frequency
                dateObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_FREQUENCY_FIELD, currentProperty.value);
            }
            // If it is end offset
            else if( 0 == currentProperty.name.compareTo(CPTADSSDataProviderProcessorConstants.CPTA_END_DATE_PROPERTY))
            {
                // Add End
                dateObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_END_OFFSET_FIELD, currentProperty.value);
            }
            // If it is start offset
            else if( 0 == currentProperty.name.compareTo(CPTADSSDataProviderProcessorConstants.CPTA_START_DATE_PROPERTY))
            {
                // Add Start
                dateObjectBuilder.add(CPTADSSDataProviderProcessorConstants.DSWS_START_OFFSET_FIELD, currentProperty.value);
            }
        }
        
        return dateObjectBuilder;
    }
    
    protected JsonObjectBuilder getInstrumentObjectBuilder(String symbolList)
    {
        // Format is as follows
        //          Instrument:
        //          {
        //              Properties: null,
        //              Value: this_is_the_symbol_list_above
        //          }
        JsonObjectBuilder instrumentObjectBuilder = Json.createObjectBuilder();
        // Properties for us is always null
        instrumentObjectBuilder.addNull(CPTADSSDataProviderProcessorConstants.DSWS_PROPERTIES_FIELD);
        // Value is the list of instruments
        instrumentObjectBuilder.add
                                  (
                                  CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD, 
                                  symbolList
                                  );
        
        return instrumentObjectBuilder;
    }
    
    protected void mergeDSWSResponseWithCPTAFormat(JsonObject newDSWSData, JsonObjectBuilder existingDSWSDataInCPTAFormat)
    {
        // Response looks like
        // {
        //      DataResponse:
        //      {
        //          Dates:
        //          [
        //              Dates are in some weird datastream format
        //              We want middle bit which is the date in milliseconds
        //              \/Date(1354665600000+0000)\/
        //          ],
        //          DataTypeValues:
        //          [
        //              This is an array, with the field first then data by ric
        //              DataType: name_of_the_field_this_data_is_for,
        //              SymbolValues:
        //              [
        //                  {
        //                      This is the return type for this symbol, 12 or 10 means there is data
        //                      Type: return_type_as_int,
        //                      symbol which is format <RIC>
        //                      Symbol: <RIC>,
        //                      Value:
        //                      [
        //                          array_of_field_in_same_order_date_timestamps    
        //                      ]
        //                  }
        //              ]
        //          ]
        //      }
        // }
        
        // Drill down to Dates first
        JsonObject dataResponseObject = newDSWSData.getJsonObject("DataResponse");
        // Get dates array
        List<String> dates = getDataResultDates(dataResponseObject);
        
        // Need error list for rics, this is the rics where there is some error
        // Or no data
        List<String> errorRics = new ArrayList<>();

        // In parsing, we should have an array of field blocks
        // mapped by ric
        HashMap<String, List<CPTAFieldValue>> resultsByRic = getResultsByRic
                                                                                (
                                                                                dataResponseObject, 
                                                                                dates
                                                                                );
        // for each new ric, add the rows of data        
    }
    
    protected List<String> getDataResultDates(JsonObject dataResponseObject)
    {
        JsonArray dates = dataResponseObject.getJsonArray(CPTADSSDataProviderProcessorConstants.DSWS_DATES_FIELD);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CPTADSSDataProviderProcessorConstants.DATA_RESPONSE_DATE_FORMAT);
        ArrayList<String> datesAsString = new ArrayList<>();
        
        // Go through each of the dates to convert it to a date string
        for( int i=0; i < dates.size(); i++)
        {
            String millisecondsAsString = dates.getString(i);
            // Strip away everything apart from the milliseconds
            millisecondsAsString = millisecondsAsString.substring(7,20);
            long dateAsLong = Long.parseLong(millisecondsAsString);
            // Convert it into a date
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(dateAsLong);
            // Convert it into a string
            String dateAsString = simpleDateFormat.format(date.getTime());
            // Add it to the list
            datesAsString.add(dateAsString);
        }
        
        return datesAsString;
    }
    
    protected HashMap<String, List<CPTAFieldValue>> getResultsByRic
                                                                  (
                                                                  JsonObject dataResponseObject, 
                                                                  List<String> dates
                                                                  )
    {
        HashMap<String, List<CPTAFieldValue>> resultsByRic = new HashMap<>();        
        // Need to go through the 
        JsonArray fields = dataResponseObject.getJsonArray(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_VALUES_FIELD);
        // Loop through all the data type values
        for( int i = 0; i < fields.size(); i++)
        {
            // Get the block for this field type
            JsonObject currentFieldBlock = fields.getJsonObject(i);
            // get the field name, this is in DataType in json
            String fieldName = currentFieldBlock.getString(CPTADSSDataProviderProcessorConstants.DSWS_DATA_TYPE_FIELD);
            // Now we loop through all the rics
            JsonArray valuesForThisFieldByRic = currentFieldBlock.getJsonArray(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_VALUES_FIELD);
            for( int j = 0; j < valuesForThisFieldByRic.size(); j++ )
            {
                JsonObject valuesForThisRic = valuesForThisFieldByRic.getJsonObject(j);
                // Check if it is an error, if it is not
                int errorCode = valuesForThisRic.getInt(CPTADSSDataProviderProcessorConstants.DSWS_TYPE_FIELD);
                if( (10 == errorCode) || (12 == errorCode) )
                {
                    // Set the ric
                    String ric = valuesForThisRic.getString(CPTADSSDataProviderProcessorConstants.DSWS_SYMBOL_FIELD); 
                    ric = ric.substring(1, ric.length()-1);
                    // If there was not an entry for this ric
                    List<CPTAFieldValue> fieldValuesForThisRic = resultsByRic.get(ric);
                    if( null == fieldValuesForThisRic )
                    {
                        // add it
                        fieldValuesForThisRic = new ArrayList<>();
                        resultsByRic.put(ric, fieldValuesForThisRic);
                    }
                    
                    // Get the values
                    // This is a new value
                    CPTAFieldValue valueForThisRicAndField = new CPTAFieldValue();
                    // Set the field name
                    valueForThisRicAndField.name = fieldName;

                    // Loop through each value
                    JsonArray fieldValues = valuesForThisRic.getJsonArray(CPTADSSDataProviderProcessorConstants.DSWS_VALUE_FIELD);
                    for( int k = 0; k < fieldValues.size(); k++ )
                    {
                        JsonValue currentValue = fieldValues.get(k);
                        // If the value is not null
                        if( 0 != currentValue.getValueType().compareTo(JsonValue.ValueType.NULL))
                        {
                            // If it is a number
                            if(0 == currentValue.getValueType().compareTo(JsonValue.ValueType.NUMBER))
                            {
                                // add a value
                                valueForThisRicAndField.value = fieldValues.getJsonNumber(k).toString();
                            }
                            else
                            {
                                valueForThisRicAndField.value = fieldValues.getString(k);                                
                            }
                            // set a date                            
                            valueForThisRicAndField.date = dates.get(k);
                            fieldValuesForThisRic.add(valueForThisRicAndField);                            
                        }
                    }                    
                }
            }
        }
    
        // return the final map
        return resultsByRic;
    }
}
