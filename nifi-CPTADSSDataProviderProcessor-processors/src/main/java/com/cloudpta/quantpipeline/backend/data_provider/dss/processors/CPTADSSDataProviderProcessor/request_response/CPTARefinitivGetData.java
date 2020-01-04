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
    public static CPTARefinitivGetData getInstance()
    {
        if(null == dataGetterInstance)
        {
            dataGetterInstance = new CPTARefinitivGetData();
        }
        
        return dataGetterInstance;
    }
    
    public String getData(ComponentLog logger, ProcessContext context, List<CPTAInstrumentSymbology> symbols, List<CPTADSSField> fields, List<CPTADSSProperty> properties)
    {
        // Get the list of message types along with the fields for each message type
        HashMap<String,List<String>> mappedFields = getMappedFields(fields);
        
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
        String globalResponse = createGlobalResponseFromList(responses);
        return globalResponse;
    }
    
    protected HashMap<String,List<String>> getMappedFields(List<CPTADSSField> fields)
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

        return mappedFields;
    }
    
    protected CPTARefinitivMessage getMessageByType
                                                  (
                                                  String messageType
                                                  ) 
                                                  throws 
                                                  InstantiationException, 
                                                  IllegalAccessException
    {
        // Get class
        Class messageClassForThisType = typeToMessageClassMap.get(messageType);
        // Create an instance of it
        CPTARefinitivMessage messageForThisType = (CPTARefinitivMessage)(messageClassForThisType.newInstance());
        
        return messageForThisType;
    }
                                                         
    protected String createGlobalResponseFromList(List<JsonObject> responsesFromEachMessage)
    {
        
        return null;
    }
    
    private CPTARefinitivGetData()
    {
        // private so we dont accidentally create this externally
        
        // Set up the mapper
        typeToMessageClassMap = new HashMap<>();
        // Populate with types
        // DSS End of day
        Class eodDSSMesageClass = CPTADSSEODMessage.class;
        typeToMessageClassMap.put(CPTADSSConstants.EOD_MESSAGE_TYPE, eodDSSMesageClass);
        // DSS Corporate actions
        Class caDSSMesageClass = CPTADSSCorporateActionsMessage.class;
        typeToMessageClassMap.put(CPTADSSConstants.CA_MESSAGE_TYPE, caDSSMesageClass);
        // DSS time series
        Class tsDSSMesageClass = CPTADSSTimeSeriesMessage.class;
        typeToMessageClassMap.put(CPTADSSConstants.TS_MESSAGE_TYPE, tsDSSMesageClass);
        // DSS composite
        Class compositeDSSMesageClass = CPTADSSCompositeMessage.class;
        typeToMessageClassMap.put(CPTADSSConstants.COMPOSITE_MESSAGE_TYPE, compositeDSSMesageClass);
        // DSWS 
        Class dswsMesageClass = CPTADSWSMessage.class;
        typeToMessageClassMap.put(CPTADSWSConstants.MESSAGE_TYPE, dswsMesageClass);
    }
    
    static CPTARefinitivGetData dataGetterInstance = null;
    HashMap<String, Class> typeToMessageClassMap = null;
}
