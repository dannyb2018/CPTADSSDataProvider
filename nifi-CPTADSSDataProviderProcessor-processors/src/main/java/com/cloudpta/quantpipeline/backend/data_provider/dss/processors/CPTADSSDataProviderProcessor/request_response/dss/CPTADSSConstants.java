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

/**
 *
 * @author Danny
 */
public interface CPTADSSConstants
{
    public static final String EOD_MESSAGE_TYPE = "DSS.EOD";
    public static final String EOD_EXTRACTION_TYPE = "#ThomsonReuters.Dss.Api.Extractions.ExtractionRequests.EndOfDayPricingExtractionRequest";
    public static final String CA_MESSAGE_TYPE = "DSS.CA";
    public static final String CA_EXTRACTION_TYPE = "#ThomsonReuters.Dss.Api.Extractions.ExtractionRequests.CorporateActionsStandardExtractionRequest";
    public static final String TS_MESSAGE_TYPE = "DSS.TS";
    public static final String TS_EXTRACTION_TYPE = "#ThomsonReuters.Dss.Api.Extractions.ExtractionRequests.PriceHistoryExtractionRequest";
    public static final String COMPOSITE_MESSAGE_TYPE = "DSS.ALL";
    public static final String COMPOSITE_EXTRACTION_TYPE = "#ThomsonReuters.Dss.Api.Extractions.ExtractionRequests.CompositeExtractionRequest";

    public static final String USER_NAME_FIELD = "Username";
    public static final String PASSWORD_FIELD = "Password";
    public static final String CREDENTIALS_FIELD = "Credentials";
    public static final String SESSION_TOKEN_VALUE_FIELD = "value";
    public static final String CONDITION_FIELD = "Condition";
    public static final String START_DATE_OFFSET_FIELD = "QueryStartDate";
    public static final String END_DATE_OFFSET_FIELD = "QueryEndDate";
    public static final String ADJUSTED_PRICE_FLAG_FIELD = "AdjustedPrices";
    public static final String DATE_RANGE_TYPE_FIELD = "ReportDateRangeType";
    public static final String DATE_RANGE_TYPE_DEFAULT_FIELD = "Range";

    public static final String GET_TOKEN_URL = "/Authentication/RequestToken";
    public static final String GET_DATA_URL = "/Extractions/ExtractWithNotes";
}
