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

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentDatabaseConstants;
import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.CPTADSSDataProviderProcessorConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTARefinitivMessage;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataFieldValue;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataProperty;
import com.cloudpta.utilites.CPTAUtilityConstants;
import com.cloudpta.utilites.exceptions.CPTAException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public abstract class CPTADSWSMessage extends CPTARefinitivMessage
{   
    protected String convertSymbol(CPTAInstrumentSymbology symbolToGet)
    {
        // For Datascope to use rics needs to be of the form <RIC> 
        // If it is a ric, need a <> around it
        if( 0 == symbolToGet.getIDSource().compareTo(CPTAInstrumentDatabaseConstants.ID_SOURCE_RIC))
        {
            return "<" + symbolToGet.getID() + ">";
        }
        else
        // if it is datastrea just as is
        {
            return symbolToGet.getID();
        }
    }
    
    @Override
    public void getResult
                        (
                        ComponentLog logger, 
                        ProcessContext context, 
                        JsonArrayBuilder responses,
                        List<CPTAInstrumentSymbology> symbols, 
                        List<String> fields, 
                        List<CPTADataProperty> properties
                        ) throws CPTAException
    {
        String userName = context.getProperty(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY).getValue();
        String password = context.getProperty(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY).getValue();
        String baseURL = context.getProperty(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY).getValue();

        // Can only do 50 symbols at a time
        // So need to keep count of how many we have done already
        int symbolsOffset = 0;
        // Whilst there are more rics to query for
        while(symbols.size() > symbolsOffset)
        {
            // Take out at most first 50 rics
            int upperLimit = Math.min(symbols.size(), symbolsOffset + 50);
            List<CPTAInstrumentSymbology> symbolsToGet = (List<CPTAInstrumentSymbology>) symbols.subList(symbolsOffset, upperLimit);     
            // Update the offset to where we are
            symbolsOffset = upperLimit;
            
            // Add each one
            String symbolList = "";
            for( CPTAInstrumentSymbology symbol: symbolsToGet)
            {
                // Comma to make it a list of symbols
                symbolList = symbolList + "," + convertSymbol(symbol);
            }
            // Got a spare comma at the start so remove it
            symbolList = symbolList.substring(1);

            // We need to get an authorisation token first
            String authorisationToken = getToken(baseURL, userName, password);
            // Now request the data
            getData
                  (
                  baseURL, 
                  authorisationToken, 
                  responses,
                  symbolList, 
                  fields, 
                  properties
                  );
        }
    }
    
    protected String getToken(String baseURL, String userName, String password)
    {
        // Get the token request 
        String tokenRequestAsString = buildTokenRequest(userName, password);
        
        // Make the request
        Client tokenRequestClient = javax.ws.rs.client.ClientBuilder.newClient();             
        String tokenRequestURL = baseURL + CPTADSWSConstants.GET_TOKEN;
        WebTarget tokenRequestTarget = tokenRequestClient.target(tokenRequestURL);
        // Get response
        Response tokenRequestRespinse = tokenRequestTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(tokenRequestAsString));
        // If the response is not ok then we have a problem
        // BUGBUGDB write a handler for this
        
        // If response is ok then get the token
        String tokenResponseString = tokenRequestRespinse.readEntity(String.class);
        JsonReader JsonReader = Json.createReader(new StringReader(tokenResponseString));
        JsonObject tokenResponseAsJson = JsonReader.readObject();
        String token = tokenResponseAsJson.getString(CPTADSWSConstants.TOKEN_VALUE_FIELD, "");
        
        return token;
    }
    
    protected void getData
                         (
                         String baseURL, 
                         String authorisationToken, 
                         JsonArrayBuilder responses,
                         String symbolList, 
                         List<String> fields, 
                         List<CPTADataProperty> properties
                         ) throws CPTAException
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
        String dataRequestURL = baseURL + CPTADSWSConstants.GET_DATA;
        WebTarget dataRequestTarget = dataRequestClient.target(dataRequestURL);
        Response dataRequestResponse = dataRequestTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(dataRequestAsString));
        // If the response is not ok then we have a problem
        // BUGBUGDB write a handle for this
        String dataRequestResponseAsString = dataRequestResponse.readEntity(String.class);
        // Parse the response
        JsonReader JsonReader = Json.createReader(new StringReader(dataRequestResponseAsString));
        JsonObject dataRequestResponseAsJson = JsonReader.readObject();
        
        // Add it to the response
        mergeDSWSResponseWithCPTAFormat(dataRequestResponseAsJson, responses);
        
    }
    
    protected String buildTokenRequest(String userName, String password)
    {
        // Start passing in the the user name and password
        JsonObjectBuilder tokenRequestBuilder  = Json.createObjectBuilder();
        // The request is user name
        tokenRequestBuilder.add
                              (
                              CPTADSWSConstants.USER_NAME_FIELD, 
                              userName
                              );
        // Password
        tokenRequestBuilder.add
                              (
                              CPTADSWSConstants.PASSSWORD_FIELD, 
                              password
                              );
        // And properties, which for us is always null
        tokenRequestBuilder.addNull(CPTADSWSConstants.PROPERTIES_FIELD);
        // Turn the JsonObject into a string so we can make a request
        String tokenRequestAsString = tokenRequestBuilder.build().toString();
        
        return tokenRequestAsString;        
    }

    protected String buildDataRequest
                                    (
                                    String authorisationToken, 
                                    String symbolList, 
                                    List<String> fields, 
                                    List<CPTADataProperty> properties
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
        //          If we are a timeseries
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
                             CPTADSWSConstants.TOKEN_VALUE_FIELD, 
                             authorisationToken
                             );
        dataRequestBuilder.addNull(CPTADSWSConstants.PROPERTIES_FIELD);
        // Add data request object
        JsonObjectBuilder dataRequestObjectBuilder = getDataRequestObject(symbolList, fields, properties);
        dataRequestBuilder.add
                             (
                             CPTADSWSConstants.DATA_REQUEST_FIELD, 
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
                                                   List<CPTADataProperty> properties
                                                   )
    {
        JsonObjectBuilder dataRequestObjectBuilder  = Json.createObjectBuilder();
        // Add datatypes aka fields
        JsonArrayBuilder fieldsArrayBuilder = getFieldsArrayBuilder(fields);
        dataRequestObjectBuilder.add
                                   (
                                   CPTADSWSConstants.DATA_TYPES_FIELD, 
                                   fieldsArrayBuilder
                                   );
        // Add instruments
        JsonObjectBuilder instrumentBuilder = getInstrumentObjectBuilder(symbolList);
        dataRequestObjectBuilder.add
                                   (
                                   CPTADSWSConstants.INSTRUMENT_FIELD, 
                                   instrumentBuilder
                                   );
        // Finally add tag which is just null
        dataRequestObjectBuilder.addNull(CPTADSWSConstants.TAG_FIELD);
        
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
            currentFieldObjectBuidler.addNull(CPTADSWSConstants.PROPERTIES_FIELD);
            // Value is the field
            currentFieldObjectBuidler.add
                                        (
                                        CPTADSWSConstants.VALUE_FIELD, 
                                        currentField
                                        );
            
            // Add object to the array
            fieldsArrayBuilder.add(currentFieldObjectBuidler);
        }
        
        // Hand the fields array builder up
        return fieldsArrayBuilder;
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
        instrumentObjectBuilder.addNull(CPTADSWSConstants.PROPERTIES_FIELD);
        // Value is the list of instruments
        instrumentObjectBuilder.add
                                  (
                                  CPTADSWSConstants.VALUE_FIELD, 
                                  symbolList
                                  );
        
        return instrumentObjectBuilder;
    }
    
    protected void mergeDSWSResponseWithCPTAFormat(JsonObject newDSWSData, JsonArrayBuilder existingDataInCPTAFormat)
    {
        // Response looks like
        // {
        //      DataResponse:
        //      {
        //          Not all responses have dates
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

        // Get the results, in non timeseries messages will just be data
        // timeseries message will override this and add dates
        HashMap<String, List<CPTADataFieldValue>> resultsBySymbol = getResultsBySymbol(dataResponseObject);
        addDSWSRowsToExistingResult(resultsBySymbol, existingDataInCPTAFormat);
    }
    
    protected HashMap<String, List<CPTADataFieldValue>> getResultsBySymbol(JsonObject dataResponseObject)
    {
        // Default is not to have any dates so just a data block
        // Get datatypes block
        JsonArray dataTypeValues = dataResponseObject.getJsonArray(CPTADSWSConstants.DATA_TYPE_VALUES_FIELD);
        HashMap<String, List<CPTADataFieldValue>> resultsWithoutDateTimestamp = parseDataTypeValues(dataTypeValues);
        return resultsWithoutDateTimestamp;
    }
    
    protected HashMap<String, List<CPTADataFieldValue>> parseDataTypeValues(JsonArray dataTypeValues)
    {
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
        
        HashMap<String, List<CPTADataFieldValue>> resultsBySymbol = new HashMap<>();        
        // Need to go through the 
        // Loop through all the data type values
        for( int i = 0; i < dataTypeValues.size(); i++)
        {
            // Get the block for this field type
            JsonObject currentFieldBlock = dataTypeValues.getJsonObject(i);
            // get the field name, this is in DataType in json
            String fieldName = currentFieldBlock.getString(CPTADSWSConstants.DATA_TYPE_FIELD);
            // Now we loop through all the rics
            JsonArray valuesForThisFieldBySymbol = currentFieldBlock.getJsonArray(CPTADSWSConstants.SYMBOL_VALUES_FIELD);
            for( int j = 0; j < valuesForThisFieldBySymbol.size(); j++ )
            {
                JsonObject valuesForThisSymbol = valuesForThisFieldBySymbol.getJsonObject(j);
                // Check if it is an error, if it is not
                int errorCode = valuesForThisSymbol.getInt(CPTADSWSConstants.TYPE_FIELD);
                if( (10 == errorCode) || (12 == errorCode) )
                {
                    // Set the symbol
                    String symbol = valuesForThisSymbol.getString(CPTADSWSConstants.SYMBOL_FIELD); 
                    symbol = symbol.substring(1, symbol.length()-1);
                    // If there was not an entry for this ric
                    List<CPTADataFieldValue> fieldValuesForThisSymbol = resultsBySymbol.get(symbol);
                    if( null == fieldValuesForThisSymbol )
                    {
                        // add it
                        fieldValuesForThisSymbol = new ArrayList<>();
                        resultsBySymbol.put(symbol, fieldValuesForThisSymbol);
                    }
                    
                    // Get the values
                    // This is a new value
                    CPTADataFieldValue valueForThisRicAndField = new CPTADataFieldValue();
                    // Set the field name
                    valueForThisRicAndField.name = fieldName;

                    // Loop through each value
                    JsonArray fieldValues = valuesForThisSymbol.getJsonArray(CPTADSWSConstants.VALUE_FIELD);
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
                            
                            // Add date if exists
                            addDateToFieldValueIfNeeded(valueForThisRicAndField, k);
                            // add to list
                            fieldValuesForThisSymbol.add(valueForThisRicAndField);                            
                        }
                    }                    
                }
            }
        }
    
        // return the final map
        return resultsBySymbol;
    }
    
    protected void addDateToFieldValueIfNeeded(CPTADataFieldValue valueForThisRicAndField, int k)
    {
        // Do nothing here
        // The timeseries message will override this to add dates
    }
    
    protected void addDSWSRowsToExistingResult
                                             (
                                             HashMap<String, List<CPTADataFieldValue>> resultsBySymbol, 
                                             JsonArrayBuilder existingDataInCPTAFormat
                                             )
    {
        // Default for this is that there are no dates
        // So for each symbol
        // Get the 
        // for each ric, we add a row of data for each date
        Set<String> ricsWithResults = resultsBySymbol.keySet();
        for( String currentRic: ricsWithResults)
        {
            // For each date there is going to be a json object with all the
            // data for that ric on that date
            HashMap<String, JsonObjectBuilder> rowsForThisSymbol = new HashMap<>();
            List<CPTADataFieldValue> valuesForThisRic = resultsBySymbol.get(currentRic);
            for(CPTADataFieldValue currentValue : valuesForThisRic)
            {
                // Get the row for this date                
                JsonObjectBuilder rowForThisDate = rowsForThisSymbol.get(currentValue.date);
                // If this is the first date
                if( null == rowForThisDate )
                {
                    // Create a row
                    rowForThisDate = Json.createObjectBuilder();
                    // For consistency with DSS
                    // Need identifier in this format
                    // "IdentifierType":"Ric","Identifier":"2618.TW"
                    // Add the ric
                    rowForThisDate.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_FIELD_NAME, currentRic);
                    // Add that the identifier is a RIC
                    rowForThisDate.add(CPTAInstrumentDatabaseConstants.INSTRUMENT_ID_SOURCE_FIELD_NAME, CPTAInstrumentDatabaseConstants.ID_SOURCE_RIC);
                    // Add the date
                    rowForThisDate.add(CPTAUtilityConstants.DATE_FIELD_NAME, currentValue.date);
                    // Add to rows
                    rowsForThisSymbol.put(currentValue.date, rowForThisDate);
                }
                // Add the field name and its value
                rowForThisDate.add(currentValue.name, currentValue.value);
            }
            
            // add the rows to the result
            Collection<JsonObjectBuilder> rowsToAdd = rowsForThisSymbol.values();
            for(JsonObjectBuilder currentRow : rowsToAdd)
            {
                existingDataInCPTAFormat.add(0, currentRow);
            }
        }        
    }        
}
