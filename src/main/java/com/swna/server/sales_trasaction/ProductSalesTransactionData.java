package com.swna.server.sales_trasaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesTransactionData {
    private String barcode;
    private String date;
    private Integer count;
}
