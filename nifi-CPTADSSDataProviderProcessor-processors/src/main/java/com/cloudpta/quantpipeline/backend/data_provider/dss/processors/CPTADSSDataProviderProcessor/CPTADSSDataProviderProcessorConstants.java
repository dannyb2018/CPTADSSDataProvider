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
    
    public static final String CPTA_DATE_FORMAT = "yyyy-MM-dd";
   public static final String CPTA_DATE_TIME_FORMAT = "yyyy-MM-dd";

    public static final String CPTA_START_DATE_PROPERTY = "start";
    public static final String CPTA_END_DATE_PROPERTY = "end";
    public static final String CPTA_FREQUENCY_PROPERTY = "frequency";
    public static final String CPTA_ADJUST_PRICES_PROPERTY = "adjust_prices";

    public static final String CPTA_OFFSET_PROPERTY_DAILY = "D";
    public static final String CPTA_OFFSET_PROPERTY_WEEKLY = "W";
    public static final String CPTA_OFFSET_PROPERTY_MONTHLY = "M";        
    public static final String CPTA_OFFSET_PROPERTY_YEARLY = "Y";        
    
    public static final String RICS_ARRAY_NAME = "rics";
    public static final String FIELDS_ARRAY_NAME = "fields";
    public static final String PROPERTIES_ARRAY_NAME = "properties";
    
    public static final String MESSAGE_TYPE_FIELD_NAME = "type";
    public static final String FIELD_NAME_FIELD_NAME = "name";
    public static final String PROPERTY_NAME_FIELD_NAME = "name";
    public static final String PROPERTY_VALUE_FIELD_NAME = "value";

    public static final String RIC_FIELD_NAME = "RIC";
    public static final String DATE_FIELD_NAME = "DATE";
}