package com.quickbook.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BuyerXSupplier {
    Buyer buyer;
    Supplier supplier;
    String relation;
}
