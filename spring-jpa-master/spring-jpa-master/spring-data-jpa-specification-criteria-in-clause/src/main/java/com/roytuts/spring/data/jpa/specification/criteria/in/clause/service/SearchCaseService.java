package com.roytuts.spring.data.jpa.specification.criteria.in.clause.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roytuts.spring.data.jpa.specification.criteria.in.clause.criteria.CaseSearchCriteria;
import com.roytuts.spring.data.jpa.specification.criteria.in.clause.entity.Case;
import com.roytuts.spring.data.jpa.specification.criteria.in.clause.entity.Country;
import com.roytuts.spring.data.jpa.specification.criteria.in.clause.entity.Region;
import com.roytuts.spring.data.jpa.specification.criteria.in.clause.repository.CaseRepo;

@Service
public class SearchCaseService {

	@Autowired
	private CaseRepo caseRepo;

	@Transactional
	public List<Case> searchCases(final CaseSearchCriteria searchCriteria) {
		List<Case> cases = caseRepo.findAll(CaseSearchSpecificaton.findByCriteria(searchCriteria));
		return cases;
	}

	private static class CaseSearchSpecificaton {
		private static Specification<Case> findByCriteria(final CaseSearchCriteria searchCriteria) {
			return new Specification<Case>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Predicate toPredicate(Root<Case> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> predicates = new ArrayList<>();
					if (searchCriteria.getRegionIds() != null && !searchCriteria.getRegionIds().isEmpty()) {
						Join<Case, Region> regions = root.join("region");
						predicates.add(regions.in(searchCriteria.getRegionIds()));
					}
					if (null != searchCriteria.getCountry()) {
						Join<Case, Country> country = root.join("country");
						predicates.add(cb.equal(country.get("id"), searchCriteria.getCountry()));
					}
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
			};
		}
	}
}
