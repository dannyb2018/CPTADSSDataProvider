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
    public static final String DSWS_BASE_URL_PROPERTY = "";    
    public static final String DSWS_BASE_URL_PROPERTY_DISPLAY = "";    
    public static final String DSWS_BASE_URL_PROPERTY_DESCRIPTION = "";    
    public static final String DSWS_BASE_URL_PROPERTY_DEFAULT_VALUE = "";    
    
    public static final String RICS_ARRAY_NAME = "rics";
    public static final String FIELDS_ARRAY_NAME = "fields";
    public static final String PROPERTIES_ARRAY_NAME = "properties";
}
