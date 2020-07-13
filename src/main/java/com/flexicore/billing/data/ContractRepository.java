package com.flexicore.billing.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.request.ContractFiltering;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
@Extension
@Component
public class ContractRepository extends AbstractRepositoryPlugin {

	public List<Contract> getAllContracts(SecurityContext securityContext,
                                       ContractFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contract> q = cb.createQuery(Contract.class);
		Root<Contract> r = q.from(Contract.class);
		List<Predicate> preds = new ArrayList<>();
		addContractPredicates(filtering, cb, r, preds);
		QueryInformationHolder<Contract> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Contract.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public long countAllContracts(SecurityContext securityContext,
			ContractFiltering filtering) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Contract> r = q.from(Contract.class);
		List<Predicate> preds = new ArrayList<>();
		addContractPredicates(filtering, cb, r, preds);
		QueryInformationHolder<Contract> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Contract.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addContractPredicates(ContractFiltering filtering,
                                     CriteriaBuilder cb, Root<Contract> r, List<Predicate> preds) {

	}

}