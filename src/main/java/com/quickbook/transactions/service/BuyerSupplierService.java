package com.quickbook.transactions.service;

import com.quickbook.transactions.constants.Constants;
import com.quickbook.transactions.model.Buyer;
import com.quickbook.transactions.model.BuyerXSupplier;
import com.quickbook.transactions.model.Supplier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuyerSupplierService {

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

    private Integer compareBuyerSupplier(Buyer buyer, Supplier supplier) {
        int score = 0;

        // compare gst_in
        if (buyer.getGstin() != null && buyer.getGstin().trim() != "" && supplier.getGstin() != null) {
            if (buyer.getGstin().equals(supplier.getGstin()))
                score += 2;
            else if (buyer.getGstin().equalsIgnoreCase(supplier.getGstin()))
                score += 1;
        }

        if(buyer.getTotal() != null && supplier.getTotal() != null){
            double diff = Math.abs(buyer.getTotal() - supplier.getTotal());
            if(diff == 0d)
                score += 2;
            else if(diff <= 5d)
                score += 1;
        }

        // todo add other comparing logics

        return score;
    }

}
