package com.quickbook.transactions.service;

import com.quickbook.transactions.constants.Constants;
import com.quickbook.transactions.model.Buyer;
import com.quickbook.transactions.model.BuyerXSupplier;
import com.quickbook.transactions.model.Supplier;
import com.quickbook.transactions.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BuyerSupplierService {
    private static final Logger LOG = LoggerFactory.getLogger(BuyerSupplierService.class);
    boolean[] isSupplierUsed;

    public List<BuyerXSupplier> prepareRelation(List<Buyer> buyers, List<Supplier> suppliers) {
        List<BuyerXSupplier> buyerXSupplierList = new ArrayList<>();
        isSupplierUsed = new boolean[suppliers.size()];

        for(Buyer buyer: buyers){
            Supplier candidateSupplier = null;
            Integer closestSupplierIndex = -1;
            Integer maxScore = 0;

            for(int j=0; j< suppliers.size(); j++){
                Supplier supplier = suppliers.get(j);

                Integer buyerXSupplierScore = compareBuyerSupplier(buyer, supplier);
                if(buyerXSupplierScore > maxScore){
                    maxScore = buyerXSupplierScore;
                    candidateSupplier = supplier;
                    closestSupplierIndex = j;
                }
            }

            if(maxScore > 0){           // we have the supplier for this buyer
                isSupplierUsed[closestSupplierIndex] = true;
            }

            String relation;
            if(maxScore == 0){
                relation = Constants.ONLY_BUYER;
                buyerXSupplierList.add(new BuyerXSupplier(buyer, null, relation));
            }else if(maxScore == 4) {         // TODO mind max score here
                relation = Constants.EXACT;
                buyerXSupplierList.add(new BuyerXSupplier(buyer, candidateSupplier, relation));
            }else{
                relation = Constants.PARTIAL;
                buyerXSupplierList.add(new BuyerXSupplier(buyer, candidateSupplier, relation));
            }

        }
        processUnusedSupplier(suppliers, isSupplierUsed, buyerXSupplierList);
        return buyerXSupplierList;
    }

    private void processUnusedSupplier(List<Supplier> suppliers, boolean[] isSupplierUsed, List<BuyerXSupplier> buyerXSupplierList) {
        for(int i=0; i< isSupplierUsed.length; i++){
            if(! isSupplierUsed[i]){
                buyerXSupplierList.add(new BuyerXSupplier(null, suppliers.get(i), Constants.ONLY_SELLER));
            }
        }
    }

    /**
     * Comparing buyer and seller instance
     * @param buyer
     * @param supplier
     * @return
     */
    private Integer compareBuyerSupplier(Buyer buyer, Supplier supplier) {
        LOG.info("Comparing buyer and supplier instances | " + buyer + " |  " + supplier);
        int score = 0;

        if (StringUtil.isPresent(buyer.getGstin()) && StringUtil.isPresent(supplier.getGstin())) {
            score += getScore(buyer.getGstin(), supplier.getGstin());
        }

        if(StringUtil.isPresent(buyer.getDate()) && StringUtil.isPresent(supplier.getDate())){
            boolean compare = true;
            Date bDate = null, sDate = null;
            try {
                bDate = new SimpleDateFormat("dd/MM/yyyy").parse(buyer.getDate());
            } catch (ParseException e) {
                LOG.error("Date field is invalid for " + buyer);
                compare = false;
            }
            try {
                sDate = new SimpleDateFormat("dd/MM/yyyy").parse(supplier.getDate());
            } catch (ParseException e) {
                LOG.error("Date field is invalid for " + supplier);
                compare = false;
            }

            if(compare){
                score += getScore(bDate, sDate);
            }
        }

        if(StringUtil.isPresent(buyer.getBillNo()) && StringUtil.isPresent(supplier.getBillNo())){
            Integer buyerBillNo = Integer.parseInt(buyer.getBillNo());
            Integer supplierBillNo = Integer.parseInt(supplier.getBillNo());
            score += getScore(buyerBillNo, supplierBillNo);
        }

        // gst rate doesn't seem to be a significant data in comparison

        if(StringUtil.isPresent(buyer.getTotal()) && StringUtil.isPresent(supplier.getTotal())){
            Double bTotal = Double.valueOf(buyer.getTotal());
            Double sTotal = Double.valueOf(supplier.getTotal());
            score += getScore(bTotal, sTotal);
        }
        // todo add other comparing logics

        return score;
    }

    private int getScore(Date bDate, Date sDate) {
        Long diff = Math.abs(bDate.getTime() - sDate.getTime());
        if(diff == 0){
            return 2;
        }else if(diff <= 432000){
            return 1;
        }

        return 0;
    }

    private int getScore(Integer left, Integer right) {
        int diff = Math.abs(left - right);

        if(diff == 0d)
            return 2;
        else if(diff <= 5d)     // considering 5 as threshold, can pick this from properties file or environment variable if required.
            return 1;
        return 0;
    }

    private int getScore(Double total, Double total1) {
        double diff = Math.abs(total - total1);

        if(diff == 0d)
            return 2;
        else if(diff <= 5d)     // considering 5 as threshold, can pick this from properties file or environment variable if required.
            return 1;
        return 0;
    }

    /**
     * Returning score on the basis of how similar the given two strings are
     * @param left
     * @param right
     * @return
     */
    private int getScore(String left, String right) {
        int similarityFactor;

        try{
            similarityFactor = StringUtil.findSimilarity(left, right);
        }catch(IllegalArgumentException ex){
            return 0;
        }

        int maxLen = Math.max(left.length(), right.length());       // both string are valid if code is reaching at this point.

        if(similarityFactor == 0)
            return 2;
        else if(similarityFactor <= maxLen/2)
            return 1;

        return 0;
    }

}
