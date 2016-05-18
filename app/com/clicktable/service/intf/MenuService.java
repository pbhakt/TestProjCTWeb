package com.clicktable.service.intf;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.model.Menu;
import com.clicktable.model.MenuCategory;
import com.clicktable.model.MenuItem;
import com.clicktable.model.MenuSubCategory;
import com.clicktable.response.BaseResponse;

@Service
public interface MenuService {

	BaseResponse addMenu(Menu menu, String header);

	BaseResponse addMenuCategory(MenuCategory menuCategory, String header);

	BaseResponse addMenuSubCategory(MenuSubCategory menuSubCategory,
			String header);

	BaseResponse addMenuItem(MenuItem menuItem, String header);

	BaseResponse updateMenu(Menu menu, String header);

	BaseResponse getMenu(Map<String, Object> stringParamMap, String header);

	BaseResponse updateMenuCategory(MenuCategory menuCategory, String header);

	BaseResponse getMenuCategory(Map<String, Object> stringParamMap,
			String header);

	BaseResponse updateMenuSubCategory(MenuSubCategory menuSubCategory,
			String header);

	BaseResponse getMenuSubCategory(Map<String, Object> stringParamMap,
			String header);

}
