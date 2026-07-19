package com.ecom.service;

import com.ecom.model.ProductOrder;

public interface InvoiceService {

	public byte[] generateInvoicePdf(ProductOrder order);
}
