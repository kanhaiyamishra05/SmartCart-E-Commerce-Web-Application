package com.ecom.service.impl;

import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import com.ecom.model.ProductOrder;
import com.ecom.service.InvoiceService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Override
	public byte[] generateInvoicePdf(ProductOrder order) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Document document = new Document();
			PdfWriter.getInstance(document, out);
			document.open();

			// Font styles
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
			Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
			Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

			// Title
			Paragraph title = new Paragraph("INVOICE / BILL OF SUPPLY", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(20);
			document.add(title);

			// Order Info
			document.add(new Paragraph("Order ID: " + order.getOrderId(), boldFont));
			document.add(new Paragraph("Order Date: " + order.getOrderDate(), normalFont));
			document.add(new Paragraph("Payment Type: " + order.getPaymentType(), normalFont));
			document.add(new Paragraph("Order Status: " + order.getStatus(), normalFont));
			document.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------", normalFont));

			// Billing Address
			document.add(new Paragraph("Billing Address:", boldFont));
			if (order.getOrderAddress() != null) {
				document.add(new Paragraph(order.getOrderAddress().getFirstName() + " " + order.getOrderAddress().getLastName(), normalFont));
				document.add(new Paragraph(order.getOrderAddress().getAddress() + ", " + order.getOrderAddress().getCity(), normalFont));
				document.add(new Paragraph(order.getOrderAddress().getState() + " - " + order.getOrderAddress().getPincode(), normalFont));
				document.add(new Paragraph("Mobile: " + order.getOrderAddress().getMobileNo(), normalFont));
			}
			document.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------", normalFont));
			document.add(new Paragraph(" ", normalFont)); // spacer

			// Item Table
			PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{4, 1.5f, 2, 1.5f, 2});

			// Headers
			String[] headers = {"Product Name", "Quantity", "Price", "Discount", "Total"};
			for (String h : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
				cell.setBackgroundColor(new Color(41, 128, 185)); // Sleek blue
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPadding(6);
				table.addCell(cell);
			}

			// Add items
			table.addCell(new PdfPCell(new Phrase(order.getProduct().getTitle(), normalFont)));
			
			PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(order.getQuantity()), normalFont));
			qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(qtyCell);

			PdfPCell priceCell = new PdfPCell(new Phrase("Rs. " + order.getPrice(), normalFont));
			priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(priceCell);

			double itemDiscount = (order.getDiscount() != null) ? order.getDiscount() : 0.0;
			PdfPCell discCell = new PdfPCell(new Phrase("Rs. " + String.format("%.2f", itemDiscount), normalFont));
			discCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(discCell);

			double subtotal = order.getPrice() * order.getQuantity();
			double totalPayable = subtotal - itemDiscount;
			
			PdfPCell totCell = new PdfPCell(new Phrase("Rs. " + String.format("%.2f", totalPayable), normalFont));
			totCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(totCell);

			document.add(table);

			// Summary section
			document.add(new Paragraph(" ", normalFont));
			document.add(new Paragraph("Summary details:", boldFont));
			document.add(new Paragraph("Subtotal: Rs. " + String.format("%.2f", subtotal), normalFont));
			document.add(new Paragraph("Discount Applied: Rs. " + String.format("%.2f", itemDiscount), normalFont));
			document.add(new Paragraph("Net Amount Payable: Rs. " + String.format("%.2f", totalPayable), boldFont));
			
			document.add(new Paragraph(" ", normalFont));
			Paragraph footer = new Paragraph("Thank you for shopping with us!", boldFont);
			footer.setAlignment(Element.ALIGN_CENTER);
			document.add(footer);

			document.close();
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
}
