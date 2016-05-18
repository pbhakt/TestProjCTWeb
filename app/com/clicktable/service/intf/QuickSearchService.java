

package com.clicktable.service.intf;

import java.util.Map;

import com.clicktable.response.BaseResponse;
@org.springframework.stereotype.Service
public interface QuickSearchService 
{
    BaseResponse getQuickSearchResult(Map<String, Object> stringParamMap, String header);

  
}
