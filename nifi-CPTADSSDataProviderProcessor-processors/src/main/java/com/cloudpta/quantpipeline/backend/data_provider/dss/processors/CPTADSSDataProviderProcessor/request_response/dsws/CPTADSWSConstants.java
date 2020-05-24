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

/**
 *
 * @author Danny
 */
public interface CPTADSWSConstants
{
    public static final String NO_TIME_SERIES_MESSAGE_TYPE = "DSWS.NO_TS";
    public static final String TIME_SERIES_MESSAGE_TYPE = "DSWS.TS";
    
    public static final String GET_TOKEN = "/GetToken";
    public static final String GET_DATA = "/GetData";
    
    public static final String TOKEN_VALUE_FIELD = "TokenValue";
    public static final String USER_NAME_FIELD = "UserName";
    public static final String PASSSWORD_FIELD = "Password";
    public static final String PROPERTIES_FIELD = "Properties";
    public static final String DATA_REQUEST_FIELD = "DataRequest";
    public static final String DATA_TYPES_FIELD = "DataTypes";
    public static final String DATE_FIELD = "Date";
    public static final String DATES_FIELD = "Dates";
    public static final String INSTRUMENT_FIELD = "Instrument";
    public static final String TAG_FIELD = "Tag";
    public static final String VALUE_FIELD = "Value";
    public static final String END_OFFSET_FIELD = "End";
    public static final String START_OFFSET_FIELD = "Start";
    public static final String KIND_FIELD = "Kind";
    public static final int KIND_FIELD_DEFAULT = 1;
    public static final String FREQUENCY_FIELD = "Frequency";
    public static final String DATA_TYPE_VALUES_FIELD = "DataTypeValues";
    public static final String DATA_TYPE_FIELD = "DataType";    
    public static final String SYMBOL_VALUES_FIELD = "SymbolValues";    
    public static final String SYMBOL_FIELD = "Symbol";    
    public static final String TYPE_FIELD = "Type";    
    
    
    public static final String START_DATE_PROPERTY_DEFAULT = "-0D";
    public static final String END_DATE_PROPERTY_DEFAULT = "-0D";
    public static final String FREQUENCY_PROPERTY_DEFAULT = "D";
    public static final String FREQUENCY_PROPERTY_DAILY = "D";
    public static final String FREQUENCY_PROPERTY_WEEKLY = "W";
    public static final String FREQUENCY_PROPERTY_MONTHLY = "M";        
}
