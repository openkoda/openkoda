/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

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

/**
 * to be refined
 */
public class PaymentDto implements CanonicalObject, OrganizationRelatedObject {

    public enum PaymentProvider {
        hotpay, stripe, mock, none;
    }

    public enum PaymentType {
        SINGLE_ITEM, SUBSCRIPTION, PAY_AS_YOU_GO
    }

    public enum PaymentStatus {
        NEW,
        PENDING,
        SUCCESS,
        FAILURE,
        CORRUPTED
    }

    public Long id;

    public BigDecimal totalAmount;

    public BigDecimal netAmount;

    public BigDecimal taxAmount;

    public String planId;

    public String planName;

    public String description;

    public PaymentProductDto[] products;

    public PaymentProvider provider;

    public PaymentType paymentType;

    public PaymentStatus status;

    public String currency;

    public Long organizationId;

    public Long userId;

    public String redirectUrl;

    public PaymentDto() {
    }

    public PaymentDto(BigDecimal totalAmount, String description, PaymentType paymentType, String redirectUrl) {
        this.totalAmount = totalAmount;
        this.description = description;
        this.paymentType = paymentType;
        this.redirectUrl = redirectUrl;
    }

    public PaymentDto(Long id, BigDecimal totalAmount, String description, PaymentProvider provider, PaymentType paymentType, PaymentStatus status, Long organizationId, Long userId) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.description = description;
        this.provider = provider;
        this.paymentType = paymentType;
        this.status = status;
        this.organizationId = organizationId;
        this.userId = userId;
    }

    public PaymentDto(BigDecimal totalAmount, BigDecimal netAmount, BigDecimal taxAmount, String planId, String planName, String description, PaymentStatus status, String currency, Long organizationId) {
        this.totalAmount = totalAmount;
        this.netAmount = netAmount;
        this.taxAmount = taxAmount;
        this.planId = planId;
        this.planName = planName;
        this.description = description;
        this.status = status;
        this.currency = currency;
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentProductDto[] getProducts() {
        return products;
    }

    public void setProducts(PaymentProductDto[] products) {
        this.products = products;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String notificationMessage() {
        return String.format("Org %s: payment %.2f %s for plan %s", organizationId, totalAmount, currency, planName);
    }
}
