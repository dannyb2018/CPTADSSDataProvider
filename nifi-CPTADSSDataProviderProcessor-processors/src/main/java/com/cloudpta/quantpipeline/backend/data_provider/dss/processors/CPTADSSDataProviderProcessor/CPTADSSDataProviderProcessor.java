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

import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSCompositeMessage;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSCorporateActionsMessage;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSEODMessage;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dss.CPTADSSTimeSeriesMessage;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dsws.CPTADSWSConstants;
import com.cloudpta.quantpipeline.backend.data_provider.dss.processors.CPTADSSDataProviderProcessor.request_response.dsws.CPTADSWSMessage;
import com.cloudpta.quantpipeline.backend.data_provider.processor.CPTADataProviderProcessor;
import com.cloudpta.quantpipeline.backend.data_provider.request_response.CPTADataRetriever;
import java.util.HashMap;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.processor.util.StandardValidators;
import java.util.List;

@Tags({"DSS and Datastream data provider"})
@CapabilityDescription("Gets data from DSS and DataStream")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class CPTADSSDataProviderProcessor extends CPTADataProviderProcessor<CPTADataRetriever>
{
    @Override
    public void addProperties(List<PropertyDescriptor> thisInstanceDescriptors)
    {
        thisInstanceDescriptors.add(DSS_USER_NAME_PROPERTY);
        thisInstanceDescriptors.add(DSS_PASSWORD_PROPERTY);
        thisInstanceDescriptors.add(DSWS_USER_NAME_PROPERTY);
        thisInstanceDescriptors.add(DSWS_PASSWORD_PROPERTY);        
    }
    
    @Override
    protected void setUpDataRetriever()
    {
        HashMap<String, Class> typeToMessageClassMap = new HashMap<>();
        
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
        
        dataRetriever = CPTADataRetriever.getInstance(typeToMessageClassMap);
    }

    // For DSS 
    // this is user name
    final PropertyDescriptor DSS_USER_NAME_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSS_USER_NAME_PROPERTY_DESCRIPTION)
    .required(true)
    .sensitive(false)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // password
    final PropertyDescriptor DSS_PASSWORD_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSS_PASSWORD_PROPERTY_DESCRIPTION)
    .required(true)
    .sensitive(true)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // base url
    final PropertyDescriptor DSS_BASE_URL_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSS_BASE_URL_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSS_BASE_URL_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSS_BASE_URL_PROPERTY_DISPLAY)
    .defaultValue(CPTADSSDataProviderProcessorConstants.DSS_BASE_URL_PROPERTY_DEFAULT_VALUE)
    .required(true)
    .sensitive(false)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // For DSWS 
    // this is user name
    final PropertyDescriptor DSWS_USER_NAME_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSWS_USER_NAME_PROPERTY_DESCRIPTION)
    .required(true)
    .sensitive(false)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // password
    final PropertyDescriptor DSWS_PASSWORD_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSWS_PASSWORD_PROPERTY_DESCRIPTION)
    .required(true)
    .sensitive(true)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
    // base url
    final PropertyDescriptor DSWS_BASE_URL_PROPERTY = new PropertyDescriptor
    .Builder()
    .name(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY)
    .displayName(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY_DISPLAY)
    .description(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY_DISPLAY)
    .defaultValue(CPTADSSDataProviderProcessorConstants.DSWS_BASE_URL_PROPERTY_DEFAULT_VALUE)
    .required(true)
    .sensitive(false)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();    
}