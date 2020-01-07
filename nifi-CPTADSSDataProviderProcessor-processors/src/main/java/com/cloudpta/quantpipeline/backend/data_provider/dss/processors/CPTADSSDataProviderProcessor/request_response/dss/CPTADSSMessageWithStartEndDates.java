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
package com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss;

import com.cloudpta.quantpipeline.api.instrument.symbology.CPTAInstrumentSymbology;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.CPTADSSDataProviderProcessorConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.CPTADSSProperty;
import com.cloudpta.utilites.exceptions.CPTAException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;

/**
 *
 * @author Danny
 */
public abstract class CPTADSSMessageWithStartEndDates extends CPTADSSMessage
{
    @Override
    public JsonArray getResult
                             (
                             ComponentLog logger, 
                             ProcessContext context, 
                             List<CPTAInstrumentSymbology> symbols, 
                             List<String> fields, 
                             List<CPTADSSProperty> properties
                             ) throws CPTAException
    {
        // Defaults are unadjusted prices
        // set the start and end dates
        // Defaults are end date offset of today
        endDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        // and start date of yesterday
        startDate = endDate;
        // Move back one day
        startDate.add(Calendar.DAY_OF_YEAR, -1);
        
        // Go through the properties to see if any get overriden
        for(CPTADSSProperty currentProperty: properties)
        {
            // If it is the end date
            if(0 == currentProperty.name.compareTo(CPTADSSDataProviderProcessorConstants.CPTA_END_DATE_PROPERTY))
            {
                endDate = convertPropertyOffsetToCalendar(currentProperty.value);
            }
            // If it is the start date
            else if(0 == currentProperty.name.compareTo(CPTADSSDataProviderProcessorConstants.CPTA_START_DATE_PROPERTY))
            {
                startDate = convertPropertyOffsetToCalendar(currentProperty.value);                
            }
            // If it is about adjusted prices
            else if(0 == currentProperty.name.compareTo(CPTADSSDataProviderProcessorConstants.CPTA_ADJUST_PRICES_PROPERTY))
            {
                shouldGetAdjustedPrices = true;
            }
        }
            
        return super.getResult(logger, context, symbols, fields, properties);
    }        

    @Override
    protected void addConditions(JsonObjectBuilder detailsSpecificToThisExtractionRequest)
    {
        // Set the delta
        JsonObjectBuilder conditions = Json.createObjectBuilder();
        conditions.add(CPTADSSConstants.DATE_RANGE_TYPE_FIELD, CPTADSSConstants.DATE_RANGE_TYPE_DEFAULT_FIELD);
        String startDateAsString = convertCalendarToString(startDate);
        conditions.add(CPTADSSConstants.START_DATE_OFFSET_FIELD, startDateAsString);
        String endDateAsString = convertCalendarToString(endDate);
        conditions.add(CPTADSSConstants.END_DATE_OFFSET_FIELD, endDateAsString);
        conditions.add(CPTADSSConstants.ADJUSTED_PRICE_FLAG_FIELD, shouldGetAdjustedPrices);
        // default is null, but will be overrrided for some messages
        detailsSpecificToThisExtractionRequest.add(CPTADSSConstants.CONDITION_FIELD, conditions);
    }
        
    protected String convertCalendarToString(Calendar calendarToConvert)
    {
        Date d = calendarToConvert.getTime();
        DateFormat df = new SimpleDateFormat(CPTADSSDataProviderProcessorConstants.CPTA_DATE_TIME_FORMAT);
        String dateAsString = df.format(d) + "Z";   
        
        return dateAsString;
    }    

    protected Calendar convertPropertyOffsetToCalendar(String propertyValue)
    {
        Calendar actualDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        // Default is day of year
        int offsetType = Calendar.DAY_OF_YEAR;
        // Last character is the offset amount ie days, weeks, months, years
        if(true == propertyValue.endsWith(CPTADSSDataProviderProcessorConstants.CPTA_OFFSET_PROPERTY_WEEKLY))
        {
            offsetType = Calendar.WEEK_OF_YEAR;
        }
        else if(true == propertyValue.endsWith(CPTADSSDataProviderProcessorConstants.CPTA_OFFSET_PROPERTY_MONTHLY))
        {
            offsetType = Calendar.MONTH;
        }
        else if(true == propertyValue.endsWith(CPTADSSDataProviderProcessorConstants.CPTA_OFFSET_PROPERTY_YEARLY))
        {
            offsetType = Calendar.YEAR;
        }
        
        // Bit before that is the actual offset
        String offsetAsString = propertyValue.substring(0, propertyValue.length() - 1);
        int offset = Integer.parseInt(offsetAsString);
        actualDate.add(offsetType, offset);
        
        return actualDate;
    }
    
    protected boolean shouldGetAdjustedPrices = false;
}
