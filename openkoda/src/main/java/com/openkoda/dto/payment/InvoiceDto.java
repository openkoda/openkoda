/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.dto.payment;

import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationRelatedObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceDto implements CanonicalObject, OrganizationRelatedObject {

    public String sellerCompanyName;
    public String sellerCompanyAddressLine1;
    public String sellerCompanyAddressLine2;
    public String sellerCompanyCountry;
    public String sellerCompanyTaxNo;

    public String buyerCompanyName;
    public String buyerCompanyAddressLine1;
    public String buyerCompanyAddressLine2;
    public String buyerCompanyCountry;
    public String buyerCompanyTaxNo;

    public String invoiceIdentifier;

    public String item;

    public String currency;

    public BigDecimal value;

    public BigDecimal tax;

    public LocalDateTime createdOn;

    public Long organizationId;

    public LocalDateTime getIssueDate() {
        return createdOn;
    }

    public LocalDateTime getSellDate() {
        return createdOn;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setIssueDate(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setSellDate(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getSellerCompanyName() {
        return sellerCompanyName;
    }

    public void setSellerCompanyName(String sellerCompanyName) {
        this.sellerCompanyName = sellerCompanyName;
    }

    public String getSellerCompanyTaxNo() {
        return sellerCompanyTaxNo;
    }

    public void setSellerCompanyTaxNo(String sellerCompanyTaxNo) {
        this.sellerCompanyTaxNo = sellerCompanyTaxNo;
    }

    public String getSellerCompanyAddressLine1() {
        return sellerCompanyAddressLine1;
    }

    public void setSellerCompanyAddressLine1(String sellerCompanyAddressLine1) {
        this.sellerCompanyAddressLine1 = sellerCompanyAddressLine1;
    }

    public String getSellerCompanyAddressLine2() {
        return sellerCompanyAddressLine2;
    }

    public void setSellerCompanyAddressLine2(String sellerCompanyAddressLine2) {
        this.sellerCompanyAddressLine2 = sellerCompanyAddressLine2;
    }

    public String getSellerCompanyCountry() {
        return sellerCompanyCountry;
    }

    public void setSellerCompanyCountry(String sellerCompanyCountry) {
        this.sellerCompanyCountry = sellerCompanyCountry;
    }

    public String getBuyerCompanyAddressLine1() {
        return buyerCompanyAddressLine1;
    }

    public void setBuyerCompanyAddressLine1(String buyerCompanyAddressLine1) {
        this.buyerCompanyAddressLine1 = buyerCompanyAddressLine1;
    }

    public String getBuyerCompanyAddressLine2() {
        return buyerCompanyAddressLine2;
    }

    public void setBuyerCompanyAddressLine2(String buyerCompanyAddressLine2) {
        this.buyerCompanyAddressLine2 = buyerCompanyAddressLine2;
    }

    public String getBuyerCompanyCountry() {
        return buyerCompanyCountry;
    }

    public void setBuyerCompanyCountry(String buyerCompanyCountry) {
        this.buyerCompanyCountry = buyerCompanyCountry;
    }

    public String getBuyerCompanyName() {
        return buyerCompanyName;
    }

    public void setBuyerCompanyName(String buyerCompanyName) {
        this.buyerCompanyName = buyerCompanyName;
    }

    public String getBuyerCompanyTaxNo() {
        return buyerCompanyTaxNo;
    }

    public void setBuyerCompanyTaxNo(String buyerCompanyTaxNo) {
        this.buyerCompanyTaxNo = buyerCompanyTaxNo;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public String getInvoiceIdentifier() {
        return invoiceIdentifier;
    }

    public void setInvoiceIdentifier(String invoiceIdentifier) {
        this.invoiceIdentifier = invoiceIdentifier;
    }

    @Override
    public String notificationMessage() {
        return String.format("Invoice for %s, issued: %tF. Stands for %.2f, with id: %s", buyerCompanyName, getIssueDate(), value, invoiceIdentifier);
    }
}
