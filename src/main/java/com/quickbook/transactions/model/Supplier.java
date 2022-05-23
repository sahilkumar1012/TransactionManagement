package com.quickbook.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {
    private String gstin;
    private String date;
    private String billNo;
    private String gstRate;
    private String taxableValue;
    private String igst;
    private String cgst;
    private String sgst;
    private Double total;
}
