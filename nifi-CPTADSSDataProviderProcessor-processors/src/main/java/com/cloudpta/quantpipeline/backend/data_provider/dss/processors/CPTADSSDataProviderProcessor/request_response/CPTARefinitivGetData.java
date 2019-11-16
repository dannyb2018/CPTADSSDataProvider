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
import javax.json.JsonObject;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public class CPTARefinitivGetData
{
    public static JsonObject getData(ProcessContext context, List<CPTAInstrumentSymbology> symbols, List<CPTADSSField> fields, List<CPTADSSProperty> properties)
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
            fieldsForThisType.add(field.fieldName);
        }
        
        return null;
    }
    
    private CPTARefinitivGetData()
    {
        // So we dont accidentally create this
    }
    
    HashMap<String, Class> typeToMessageClassMap = new HashMap<>();
}
