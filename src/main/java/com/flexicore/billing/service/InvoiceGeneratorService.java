package com.flexicore.billing.service;

import com.flexicore.billing.model.Contract;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.model.Invoice;
import com.flexicore.billing.model.InvoiceItem;
import com.flexicore.billing.request.*;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.DateFilter;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Extension
public class InvoiceGeneratorService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(InvoiceGeneratorService.class);

    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private InvoiceItemService invoiceItemService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private ContractItemService contractItemService;
    @Autowired
    private SecurityContextBase adminSecurityContext;

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateInvoices(){
        logger.info("invoice generation date has arrived");
        OffsetDateTime generationStart=OffsetDateTime.now();
        List<Contract> contracts;
        ContractFiltering filtering = new ContractFiltering().setNextChargeDate(new DateFilter().setEnd(generationStart)).setPageSize(50);
        int count=0;
        for (contracts=contractService.listAllContracts(adminSecurityContext, filtering.setCurrentPage(0)); !contracts.isEmpty(); contracts=contractService.listAllContracts(adminSecurityContext, filtering.setCurrentPage(filtering.getCurrentPage()+1))) {
            List<Object> toMerge=new ArrayList<>();
            Map<String,List<ContractItem>> contractItemsMap=contractItemService.listAllContractItems(adminSecurityContext,new ContractItemFiltering().setContracts(contracts)).stream().collect(Collectors.groupingBy(f->f.getContract().getId()));
            for (Contract contract : contracts) {
                List<ContractItem> contractItems=contractItemsMap.get(contract.getId());
                if(contractItems!=null&&!contractItems.isEmpty()){
                    count++;
                    Invoice invoice=generateInvoiceNoMerge(contract,contractItems,generationStart,toMerge);
                    contract.setNextChargeDate(invoice.getInvoiceDate().plusMonths(1));
                    toMerge.add(contract);
                }
            }
            contractService.massMerge(toMerge);
        }
        logger.info("generated invoices for "+count +" contracts");

    }

    private Invoice generateInvoiceNoMerge(Contract contract, List<ContractItem> contractItems, OffsetDateTime generationStart, List<Object> toMerge) {
        Invoice invoice = invoiceService.createInvoiceNoMerge(new InvoiceCreate().setInvoiceDate(generationStart).setContract(contract).setCustomer(contract.getCustomer()), adminSecurityContext);
        toMerge.add(invoice);
        for (ContractItem contractItem : contractItems) {
            InvoiceItemCreate invoiceItemCreate = new InvoiceItemCreate()
                    .setContractItem(contractItem)
                    .setInvoice(invoice);
            InvoiceItem invoiceItem = invoiceItemService.createInvoiceItemNoMerge(invoiceItemCreate, adminSecurityContext);
            toMerge.add(invoiceItem);
        }
        return invoice;
    }


}
