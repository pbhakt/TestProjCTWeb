package com.clicktable.dao.intf;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clicktable.model.Onboarding;

@Service
public interface OnboardingDao extends GenericDao<Onboarding> {

	List<Onboarding> listOfDuplicateOnboarding(String name, String email);

	List<Onboarding> listOfOtherDuplicateOnboarding(String name, String email, Long id);

}
