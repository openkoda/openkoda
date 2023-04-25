package com.openkoda.dto.payment;

import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationRelatedObject;

import java.math.BigDecimal;

public class PaymentProductDto implements CanonicalObject, OrganizationRelatedObject {

    public Long id;

    public BigDecimal price;

    public String itemName;

    public Long paymentId;

    public Long organizationId;

    public PaymentProductDto(Long id, BigDecimal price, String itemName, Long paymentId, Long organizationId) {
        this.id = id;
        this.price = price;
        this.itemName = itemName;
        this.paymentId = paymentId;
        this.organizationId = organizationId;
    }

    @Override
    public String notificationMessage() {
        return String.format("Org %s: Item %s price %.2f", organizationId, itemName, price);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
