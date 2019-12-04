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
package com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor;

/**
 *
 * @author Danny
 */
public interface CPTADSSDataProviderProcessorConstants
{
    public static final String DSS_USER_NAME_PROPERTY = "DSS_USER_NAME";
    public static final String DSS_USER_NAME_PROPERTY_DISPLAY = "DSS User Name";
    public static final String DSS_USER_NAME_PROPERTY_DESCRIPTION = "User name for Refinitiv DSS";
    public static final String DSS_PASSWORD_PROPERTY = "";
    public static final String DSS_PASSWORD_PROPERTY_DISPLAY = "DSS Password";
    public static final String DSS_PASSWORD_PROPERTY_DESCRIPTION = "Password for Refinitiv DSS";
    public static final String DSS_BASE_URL_PROPERTY = "";    
    public static final String DSS_BASE_URL_PROPERTY_DISPLAY = "";    
    public static final String DSS_BASE_URL_PROPERTY_DESCRIPTION = "";    
    public static final String DSS_BASE_URL_PROPERTY_DEFAULT_VALUE = "";    

    public static final String DSWS_USER_NAME_PROPERTY = "DWS_USER_NAME";
    public static final String DSWS_USER_NAME_PROPERTY_DISPLAY = "Datascope User Name";
    public static final String DSWS_USER_NAME_PROPERTY_DESCRIPTION = "User name for Refinitiv Datascope";
    public static final String DSWS_PASSWORD_PROPERTY = "";
    public static final String DSWS_PASSWORD_PROPERTY_DISPLAY = "Datascope Password";
    public static final String DSWS_PASSWORD_PROPERTY_DESCRIPTION = "Password for Refinitiv Datascope";
    public static final String DSWS_BASE_URL_PROPERTY = "DSWS_BASE_URL";    
    public static final String DSWS_BASE_URL_PROPERTY_DISPLAY = "Datascope Base Url";    
    public static final String DSWS_BASE_URL_PROPERTY_DESCRIPTION = "Base Url for all Datascope requests";    
    public static final String DSWS_BASE_URL_PROPERTY_DEFAULT_VALUE = "http://product.datastream.com/DswsClient/V1/DSService.svc/rest";    
    
    public static final String DSWS_GET_TOKEN = "/GetToken";
    public static final String DSWS_GET_DATA = "/GetData";
    
    public static final String DSWS_TOKEN_VALUE_FIELD = "TokenValue";
    public static final String DSWS_USER_NAME_FIELD = "UserName";
    public static final String DSWS_PASSSWORD_FIELD = "Password";
    public static final String DSWS_PROPERTIES_FIELD = "Properties";
    public static final String DSWS_DATA_REQUEST_FIELD = "DataRequest";
    public static final String DSWS_DATA_TYPES_FIELD = "DataTypes";
    public static final String DSWS_DATE_FIELD = "Date";
    public static final String DSWS_DATES_FIELD = "Dates";
    public static final String DSWS_INSTRUMENT_FIELD = "Instrument";
    public static final String DSWS_TAG_FIELD = "Tag";
    public static final String DSWS_VALUE_FIELD = "Value";
    public static final String DSWS_END_OFFSET_FIELD = "End";
    public static final String DSWS_START_OFFSET_FIELD = "Start";
    public static final String DSWS_KIND_FIELD = "Kind";
    public static final int DSWS_KIND_FIELD_DEFAULT = 1;
    public static final String DSWS_FREQUENCY_FIELD = "Frequency";
    public static final String DSWS_DATA_TYPE_VALUES_FIELD = "DataTypeValues";
    public static final String DSWS_DATA_TYPE_FIELD = "DataType";    
    public static final String DSWS_SYMBOL_VALUES_FIELD = "SymbolValues";    
    public static final String DSWS_SYMBOL_FIELD = "Symbol";    
    public static final String DSWS_TYPE_FIELD = "Type";    
    
    public static final String DATA_RESPONSE_DATE_FORMAT = "yyyy-MM-dd";
    
    public static final String CPTA_START_DATE_PROPERTY = "start";
    public static final String CPTA_END_DATE_PROPERTY = "end";
    public static final String CPTA_FREQUENCY_PROPERTY = "frequency";
    public static final String DSWS_START_DATE_PROPERTY_DEFAULT = "-0D";
    public static final String DSWS_END_DATE_PROPERTY_DEFAULT = "-0D";
    public static final String DSWS_FREQUENCY_PROPERTY_DEFAULT = "D";
    public static final String DSWS_FREQUENCY_PROPERTY_DAILY = "D";
    public static final String DSWS_FREQUENCY_PROPERTY_WEEKLY = "W";
    public static final String DSWS_FREQUENCY_PROPERTY_MONTHLY = "M";
    
    public static final String RICS_ARRAY_NAME = "rics";
    public static final String FIELDS_ARRAY_NAME = "fields";
    public static final String PROPERTIES_ARRAY_NAME = "properties";
}
