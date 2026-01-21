package com.logisticstudy.api.purchasing.web;

import com.logisticstudy.api.purchasing.app.POAppService;
import com.logisticstudy.api.purchasing.web.dto.CreatePORequest;
import com.logisticstudy.api.purchasing.web.dto.POListResponse;
import com.logisticstudy.api.purchasing.web.dto.POResponse;
import com.logisticstudy.api.shared.web.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class POController {

    private final POAppService poAppService;

    @PostMapping
    public void createPurchaseOrder(@Valid @RequestBody CreatePORequest request) {
        poAppService.createPO(request);
    }

    @PostMapping("/{id}/approve")
    public void approvePurchaseOrder(@PathVariable Long id) {
        poAppService.approvePO(id);
    }

    @PostMapping("/{id}/send")
    public void sendPurchaseOrder(@PathVariable Long id) {
        poAppService.sendPO(id);
    }

    @GetMapping("/{id}")
    public POResponse getPurchaseOrderById(@PathVariable Long id) {
        return poAppService.findPOResponseById(id);
    }

    @GetMapping
    public PageResponse<POListResponse> getPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return poAppService.findPOResponsePage(page, size);
    }

    @GetMapping("/open")
    public PageResponse<POListResponse> getOpenPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return poAppService.findOpenPOResponsePage(page, size);
    }

    @GetMapping("/overdue")
    public PageResponse<POListResponse> getOverduePurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return poAppService.findOverduePOResponsePage(page, size);
    }

    @DeleteMapping("/{id}")
    public void deletePurchaseOrderById(@PathVariable Long id) {
        poAppService.deletePOById(id);
    }
}
