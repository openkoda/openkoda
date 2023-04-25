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

/**
 * Class that represents subscription
 */
public class SubscriptionDto implements CanonicalObject, OrganizationRelatedObject {

    public String subscriptionId;
    public LocalDateTime nextBilling;
    public LocalDateTime currentBillingStart;
    public LocalDateTime currentBillingEnd;
    public BigDecimal nextAmount;
    public BigDecimal price;
    public String planName;
    public String planFullName;
    public String subscriptionStatus;
    public String currency;
    public Long organizationId;

    public SubscriptionDto(String subscriptionId,
                           LocalDateTime nextBilling,
                           LocalDateTime currentBillingStart,
                           LocalDateTime currentBillingEnd,
                           BigDecimal nextAmount,
                           BigDecimal price,
                           String planName,
                           String planFullName,
                           String subscriptionStatus,
                           String currency,
                           Long organizationId) {
        this.subscriptionId = subscriptionId;
        this.nextBilling = nextBilling;
        this.currentBillingStart = currentBillingStart;
        this.currentBillingEnd = currentBillingEnd;
        this.nextAmount = nextAmount;
        this.price = price;
        this.planName = planName;
        this.planFullName = planFullName;
        this.subscriptionStatus = subscriptionStatus;
        this.currency = currency;
        this.organizationId = organizationId;
    }

    public SubscriptionDto(Long organizationId){
        this.organizationId = organizationId;
    }

    public SubscriptionDto(Long organizationId,  String planName, String subscriptionStatus){
        this.organizationId = organizationId;
        this.planName = planName;
        this.subscriptionStatus = subscriptionStatus;
    }

    public SubscriptionDto(){
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public LocalDateTime getNextBilling() {
        return nextBilling;
    }

    public void setNextBilling(LocalDateTime nextBilling) {
        this.nextBilling = nextBilling;
    }

    public LocalDateTime getCurrentBillingStart() {
        return currentBillingStart;
    }

    public void setCurrentBillingStart(LocalDateTime currentBillingStart) {
        this.currentBillingStart = currentBillingStart;
    }

    public LocalDateTime getCurrentBillingEnd() {
        return currentBillingEnd;
    }

    public void setCurrentBillingEnd(LocalDateTime currentBillingEnd) {
        this.currentBillingEnd = currentBillingEnd;
    }

    public BigDecimal getNextAmount() {
        return nextAmount;
    }

    public void setNextAmount(BigDecimal nextAmount) {
        this.nextAmount = nextAmount;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanFullName() {
        return planFullName;
    }

    public void setPlanFullName(String planFullName) {
        this.planFullName = planFullName;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String notificationMessage() {
        return String.format("Subscription of %s plan, ends %tF. Status now is %s. Price: %.2f", planName, currentBillingEnd, subscriptionStatus, price);
    }
}
