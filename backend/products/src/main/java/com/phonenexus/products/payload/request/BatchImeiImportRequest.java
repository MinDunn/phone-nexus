package com.phonenexus.products.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class BatchImeiImportRequest {
    @NotNull
    private BigDecimal costPrice;

    @NotEmpty
    @Size(min = 1)
    private List<@NotBlank @Size(min = 15, max = 15) String> imeis;

    public BatchImeiImportRequest() {
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public List<String> getImeis() {
        return imeis;
    }

    public void setImeis(List<String> imeis) {
        this.imeis = imeis;
    }
}
