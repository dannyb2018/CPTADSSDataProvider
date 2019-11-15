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
import java.util.List;
import javax.json.JsonObject;

/**
 *
 * @author Danny
 */
public abstract class CPTARefinitivMessage
{
    public CPTARefinitivMessage(List<CPTAInstrumentSymbology> symbols, List<CPTADSSField> fields, List<CPTADSSProperty> properties)
    {
        
    }
    
    public abstract JsonObject getResult(String userName, String password);
}