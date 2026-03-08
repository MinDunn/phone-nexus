package com.phonenexus.sales.clients;

import com.phonenexus.sales.dto.external.ProductExternalResponse;
import com.phonenexus.sales.dto.external.ProductItemExternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "products-service", url = "${app.products-service.url:http://localhost:8082}")
public interface ProductClient {

        @GetMapping("/api/v1/products/{id}")
        ProductExternalResponse getProductById(@PathVariable("id") UUID id);

        @PostMapping("/api/v1/products/variants/{variantId}/reduce-stock")
        void reduceStock(@PathVariable("variantId") UUID variantId, @RequestParam("quantity") Integer quantity,
                        @RequestHeader("X-Internal-Token") String token);

        @PostMapping("/api/v1/products/variants/{variantId}/increase-stock")
        void increaseStock(@PathVariable("variantId") UUID variantId, @RequestParam("quantity") Integer quantity,
                        @RequestHeader("X-Internal-Token") String token);

        @GetMapping("/api/v1/products/variants/{variantId}/items")
        List<ProductItemExternalResponse> getAvailableItems(
                        @PathVariable("variantId") UUID variantId,
                        @RequestHeader("X-Internal-Token") String token,
                        @RequestHeader("X-Role") String role);

        @PutMapping("/api/v1/products/items/imei/{imei}/status")
        void updateItemStatusByImei(
                        @PathVariable("imei") String imei,
                        @RequestParam("status") String status,
                        @RequestHeader("X-Internal-Token") String token);
}
