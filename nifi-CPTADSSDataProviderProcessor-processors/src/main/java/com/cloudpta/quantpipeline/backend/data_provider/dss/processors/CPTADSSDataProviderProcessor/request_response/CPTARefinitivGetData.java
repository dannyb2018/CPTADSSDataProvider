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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.json.JsonObject;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public class CPTARefinitivGetData
{
    public static JsonObject getData(ComponentLog logger, ProcessContext context, List<CPTAInstrumentSymbology> symbols, List<CPTADSSField> fields, List<CPTADSSProperty> properties)
    {
        HashMap<String,List<String>> mappedFields = new HashMap<>();
        // for each type get the fields
        // Loop through the fields
        for( CPTADSSField field : fields)
        {
            // Get the message type
            String messageType = field.messageType;
            // If it is not already in the map
            List<String> fieldsForThisType = mappedFields.get(messageType);
            if(null == fieldsForThisType)
            {
                // Add it
                fieldsForThisType = new ArrayList<>();
                mappedFields.put(messageType, fieldsForThisType);
            }
            
            // Add the field
            fieldsForThisType.add(field.name);
        }
        
        List<JsonObject> responses = new ArrayList<>();
        Set<String> messagesTypesToQuery = mappedFields.keySet();
        // Go through the list of message types
        for(String currentMessageType : messagesTypesToQuery )
        {
            // Get fields for the message type
            List<String> fieldsForThisMessageType = mappedFields.get(currentMessageType);
            // Try to create an instance of that message
            try
            {
                CPTARefinitivMessage message = getMessageByType(currentMessageType);
                // For each message pass in the relevant request
                // Get the data
                JsonObject response = message.getResult
                                                      (
                                                      logger,
                                                      context, 
                                                      symbols, 
                                                      fieldsForThisMessageType,
                                                      properties
                                                      );
                // Add to list of responses
                responses.add(response);
            }
            catch(Exception E)
            {
                // Think what to do here
            }
        }
        
        // Turn reponses into a proper response
        JsonObject globalResponse = createGlobalResponseFromList(responses);
        return globalResponse;
    }
    
    protected static CPTARefinitivMessage getMessageByType
                                                         (
                                                         String messageType
                                                         ) throws InstantiationException, IllegalAccessException
    {
        // If the mapper is null
        if( null == typeToMessageClassMap )
        {
            // Create it
            typeToMessageClassMap = new HashMap<>();
            // populate it
        }

        // Get class
        Class messageClassForThisType = typeToMessageClassMap.get(messageType);
        // Create an instance of it
        CPTARefinitivMessage messageForThisType = (CPTARefinitivMessage)(messageClassForThisType.newInstance());
        
        return messageForThisType;
    }
                                                         
    protected static JsonObject createGlobalResponseFromList(List<JsonObject> responsesFromEachMessage)
    {
        
        return null;
    }
    
    private CPTARefinitivGetData()
    {
        // So we dont accidentally create this
    }
    
    static HashMap<String, Class> typeToMessageClassMap = null;
}
