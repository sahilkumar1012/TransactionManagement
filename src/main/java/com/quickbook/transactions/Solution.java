package com.quickbook.transactions;

import com.opencsv.exceptions.CsvException;
import com.quickbook.transactions.model.Buyer;
import com.quickbook.transactions.model.BuyerXSupplier;
import com.quickbook.transactions.model.Supplier;
import com.quickbook.transactions.service.BuyerSupplierService;
import com.quickbook.transactions.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    private List<Buyer> buyers;
    private List<Supplier> suppliers;

    BuyerSupplierService buyerSupplierService = new BuyerSupplierService();
    private final Logger log = LoggerFactory.getLogger(Solution.class);

    public List<BuyerXSupplier> main() {
        try {
            readBuyerSupplierData();    // reads buyers and suppliers data from csv files
            List<BuyerXSupplier> buyersXSuppliers = buyerSupplierService.prepareRelation(buyers, suppliers);
            log.info("Final result : " + buyersXSuppliers.toString());
            return buyersXSuppliers;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void readBuyerSupplierData() throws FileNotFoundException, CsvException {
        List<String[]> buyerReadResult = FileUtil.readCSVFile("Buyer.csv");
        List<String[]> supplierReadResult = FileUtil.readCSVFile("Supplier.csv");

        buyers = prepareBuyerList(buyerReadResult);
        suppliers = prepareSupplierList(supplierReadResult);
    }

    private List<Supplier> prepareSupplierList(List<String[]> supplierReadResult) {
        Supplier supplier;
        List<Supplier> suppliers = new ArrayList<>();

        for(int i = 1; i< supplierReadResult.size(); i++){
            String array[] = supplierReadResult.get(i);
            supplier = new Supplier(array[0],array[1],array[2],array[3],array[4],array[5],array[6],array[7],
                    array[8]);
            suppliers.add(supplier);
        }
//        System.out.println(supplier);
        return suppliers;
    }


    private List<Buyer> prepareBuyerList(List<String[]> buyerReadResult) {
        Buyer buyer;
        List<Buyer> buyers = new ArrayList<>();

        for(int i = 1; i< buyerReadResult.size(); i++){
            String array[] = buyerReadResult.get(i);
            buyer = new Buyer(array[0],array[1],array[2],array[3],array[4],array[5],array[6],array[7],
                    array[8]);
            buyers.add(buyer);
        }
//        System.out.println(buyers);
        return buyers;
    }
}

