package com.clicktable.dao.intf;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * @author s.gupta
 *
 * @param <T>
 */
@Service
public interface GenericDao<T> {

	/**
	 * Create Object in Back-end
	 * @param t 	Entity object to be created
	 * @return 		created Entity object
	 */
	T create(T t);

	/**
	 * Find Object with given Id field
	 * @param type 	Entity Class to be searched
	 * @param id	Entity Id
	 * @return		Entity object
	 */
	//T find(Class type,Object id);
	
	/**
	 * Update Entity Object
	 * @param t		Entity object to be updated
	 * @return		Updated Entity Object
	 */
	T update(T t);
	
	/**
	 * Delete Entity Object
	 * @param t		Entity object to be deleted
	 */
	void delete(Class type,Object id);
	
	
	/**
	 * Find all Entities of a given Entity type
	 * @param type		Entity Class 
	 * @return			List of Entity Objects
	 */
	List<T> findAll(Class type);

	List<T> findByFields(Class type, Map<String, Object> params);

	T find(Object id);
	
	//T findLegacy(Object id);
	
	List<T> createMultiple(List<T> list);
	
	List<T> updateMultiple(List<String>guidList, Map<String, Object> valuesToUpdate);

	void delete(String guid);

	int getCountWithParams(Map<String, Object> params);

}
