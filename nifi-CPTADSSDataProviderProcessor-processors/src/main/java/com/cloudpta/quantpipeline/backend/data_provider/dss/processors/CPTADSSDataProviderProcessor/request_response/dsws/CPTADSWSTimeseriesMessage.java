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

import com.cloudpta.quantpipeline.backend.data_provider.processor.CPTADataProviderAPIConstants;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataFieldValue;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataProperty;
import com.cloudpta.utilites.CPTAUtilityConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Danny
 */
public class CPTADSWSTimeseriesMessage extends CPTADSWSMessage
{
    @Override
    public String getMessageType()
    {
        return CPTADSWSConstants.TIME_SERIES_MESSAGE_TYPE;
    }

    @Override
    protected JsonObjectBuilder getDataRequestObject
                                                   (
                                                   String symbolList, 
                                                   List<String> fields, 
                                                   List<CPTADataProperty> properties
                                                   )
    {
        // Get the standard data request
        JsonObjectBuilder dataRequestObjectBuilder  = super.getDataRequestObject(symbolList, fields, properties);
        // Add date portion
        
        // Add Date aka properties
        JsonObjectBuilder dataBuilder = getDateObjectBuilder(properties);
        dataRequestObjectBuilder.add
                                   (
                                   CPTADSWSConstants.DATE_FIELD, 
                                   dataBuilder
                                   );
        
        // Hand back the builder
        return dataRequestObjectBuilder;
    }
                                                   
    protected JsonObjectBuilder getDateObjectBuilder(List<CPTADataProperty> properties)
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
        dateObjectBuilder.add(CPTADSWSConstants.KIND_FIELD, CPTADSWSConstants.KIND_FIELD_DEFAULT);
        
        // Default is the last day only
        dateObjectBuilder.add(CPTADSWSConstants.END_OFFSET_FIELD, CPTADSWSConstants.END_DATE_PROPERTY_DEFAULT);
        dateObjectBuilder.add(CPTADSWSConstants.FREQUENCY_FIELD, CPTADSWSConstants.FREQUENCY_PROPERTY_DEFAULT);
        dateObjectBuilder.add(CPTADSWSConstants.START_OFFSET_FIELD, CPTADSWSConstants.START_DATE_PROPERTY_DEFAULT);
        
        // Loop through properties
        for(CPTADataProperty currentProperty : properties )
        {
            // If it is frequency
            if( 0 == currentProperty.name.compareTo(CPTADataProviderAPIConstants.CPTA_FREQUENCY_PROPERTY))
            {
                // Add Frequency
                dateObjectBuilder.add(CPTADSWSConstants.FREQUENCY_FIELD, currentProperty.value);
            }
            // If it is end offset
            else if( 0 == currentProperty.name.compareTo(CPTADataProviderAPIConstants.CPTA_END_DATE_PROPERTY))
            {
                // Add End
                dateObjectBuilder.add(CPTADSWSConstants.END_OFFSET_FIELD, currentProperty.value);
            }
            // If it is start offset
            else if( 0 == currentProperty.name.compareTo(CPTADataProviderAPIConstants.CPTA_START_DATE_PROPERTY))
            {
                // Add Start
                dateObjectBuilder.add(CPTADSWSConstants.START_OFFSET_FIELD, currentProperty.value);
            }
        }
        
        return dateObjectBuilder;
    }    
    
    protected void getDataResultDates(JsonObject dataResponseObject)
    {
        JsonArray datesAsJsonArray = dataResponseObject.getJsonArray(CPTADSWSConstants.DATES_FIELD);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CPTAUtilityConstants.CPTA_DATE_FORMAT);
        
        // Go through each of the dates to convert it to a date string
        for( int i=0; i < datesAsJsonArray.size(); i++)
        {
            String millisecondsAsString = datesAsJsonArray.getString(i);
            // Strip away everything apart from the milliseconds
            millisecondsAsString = millisecondsAsString.substring(6,19);
            long dateAsLong = Long.parseLong(millisecondsAsString);
            // Convert it into a date
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(dateAsLong);
            // Convert it into a string
            String dateAsString = simpleDateFormat.format(date.getTime());
            // Add it to the list
            dates.add(dateAsString);
        }
    }    
    
    @Override
    protected HashMap<String, List<CPTADataFieldValue>> getResultsBySymbol(JsonObject dataResponseObject)
    {
        // Parse the dates
        getDataResultDates(dataResponseObject);

        // parent already parses data values
        // And with the overrided method to add datetimestamp will have datetimestamps too
        HashMap<String, List<CPTADataFieldValue>> results = super.getResultsBySymbol(dataResponseObject);
        
        return results;
    }
    
    @Override
    protected void addDateToFieldValueIfNeeded(CPTADataFieldValue valueForThisSymbolAndField, int offset)
    {
        valueForThisSymbolAndField.date = dates.get(offset);
    }

    protected List<String> dates = new ArrayList<>();
}
