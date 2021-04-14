package com.flexicore.billing.service;


import com.flexicore.billing.interfaces.PaymentMethodService;
import com.flexicore.billing.model.*;
import com.flexicore.billing.request.ContractItemFiltering;
import com.flexicore.billing.request.InvoiceFiltering;
import com.flexicore.billing.request.InvoiceItemFiltering;
import com.flexicore.billing.request.PayRequest;
import com.flexicore.billing.response.PayResponse;
import com.flexicore.billing.response.PayStatus;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
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
public class AutomaticInvoicePayer implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(AutomaticInvoicePayer.class);
    @Autowired
    private InvoiceItemService invoiceItemService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private SecurityContextBase adminSecurityContext;
    @Autowired
    private ObjectProvider<PaymentMethodService> paymentMethodServices;


    @Scheduled(cron = "0 0 0 * * ?")
    public void handleUnpaidInvoices(){
        logger.info("started handling unpaid invoices");
        InvoiceFiltering filtering = new InvoiceFiltering().setAutomatic(true).setUnpaid(true).setPageSize(50);
        List<Invoice> invoices;
        long count=0;
        List<PaymentMethodService> paymentMethodServices = this.paymentMethodServices.stream().collect(Collectors.toList());
        for (invoices=invoiceService.listAllInvoices(adminSecurityContext, filtering); !invoices.isEmpty(); invoices=invoiceService.listAllInvoices(adminSecurityContext, filtering.setCurrentPage(filtering.getCurrentPage()+1))) {
            List<Object> toMerge=new ArrayList<>();
            Map<String,List<InvoiceItem>> invoiceItemsMap=invoiceItemService.listAllInvoiceItems(adminSecurityContext,new InvoiceItemFiltering().setInvoices(invoices)).stream().collect(Collectors.groupingBy(f->f.getInvoice().getId()));

            for (Invoice invoice : invoices) {
                List<InvoiceItem> invoiceItems=invoiceItemsMap.get(invoice.getId());
                if(invoiceItems!=null&&!invoiceItems.isEmpty()){
                    Map<String, Currency> currencyMap=invoiceItems.stream().map(f->f.getContractItem().getCurrency()).collect(Collectors.toMap(f->f.getId(),f->f));
                    Map<String,List<InvoiceItem>> invoiceItemsForCurrencyMap=invoiceItems.stream().collect(Collectors.groupingBy(f->f.getContractItem().getCurrency().getId()));
                    for (Map.Entry<String, List<InvoiceItem>> invoiceByCurrencyEntry : invoiceItemsForCurrencyMap.entrySet()) {
                        Currency currency=currencyMap.get(invoiceByCurrencyEntry.getKey());
                        List<InvoiceItem> invoiceItemsForCurrency=invoiceByCurrencyEntry.getValue();
                        long sum = invoiceItemsForCurrency.stream().mapToLong(f -> f.getContractItem().getPriceListToService().getPrice()).sum();
                        PayRequest payRequest=new PayRequest()
                                .setInvoiceItem(invoiceItemsForCurrency)
                                .setInvoice(invoice)
                                .setCents(sum)
                                .setCurrency(currency)
                                .setPaymentMethod(invoice.getContract().getAutomaticPaymentMethod());
                        paymentMethodServices.stream().filter(f->f.canHandle(payRequest)).findFirst().ifPresent(f->{
                            PayResponse pay = f.pay(payRequest);
                            if(PayStatus.COMPLETE.equals(pay.getPayStatus())){
                                for (InvoiceItem invoiceItem : invoiceItemsForCurrency) {
                                    invoiceItem.setPaymentReference(pay.getTransactionId());
                                    invoiceItem.setDatePaid(OffsetDateTime.now());
                                    toMerge.add(invoiceItem);
                                }
                            }
                        });

                    }
                    count++;

                }
            }
            invoiceService.massMerge(toMerge);
        }
        logger.info("finished handling "+count+" unpaid invoices");

    }


}
