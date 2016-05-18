package com.clicktable.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clicktable.dao.intf.OnboardingDao;
import com.clicktable.model.Onboarding;
import com.clicktable.util.Constants;

@Service
public class OnboardingDaoImpl extends GraphDBDao<Onboarding> implements OnboardingDao {

	public OnboardingDaoImpl() {
		super();
		this.setType(Onboarding.class);
	}
	
	@Override
	public List<Onboarding> listOfDuplicateOnboarding(String name, String email) {
		Map<String, Object> params = new java.util.HashMap<String, Object>();
		params.put(Constants.NAME, name);
		params.put(Constants.EMAIL,email);
		
		StringBuilder query = new StringBuilder("MATCH (t:`Onboarding`) where (t.email={"+Constants.EMAIL+"}) AND t.status='"+Constants.ACTIVE_STATUS+"' RETURN t limit "+params.size());
		return executeQuery(query.toString(), params);
		
	}
	

	@Override
	public List<Onboarding> listOfOtherDuplicateOnboarding(String name, String email,Long id) {
		Map<String, Object> params = new java.util.HashMap<String, Object>();
		params.put(Constants.NAME, name);
		params.put(Constants.EMAIL,email);
		params.put(Constants.ID,id);

		StringBuilder query = new StringBuilder("MATCH (t:`Onboarding`) where id(t)<>{id} and (t.restaurant_name={"+Constants.NAME+"} or t.email={"+Constants.EMAIL+"}) AND t.status='"+Constants.ACTIVE_STATUS+"' RETURN t limit "+params.size());
		return executeQuery(query.toString(), params);
		
	}
	

}
