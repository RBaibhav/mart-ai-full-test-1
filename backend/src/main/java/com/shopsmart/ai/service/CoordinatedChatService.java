package com.shopsmart.ai.service;

import com.shopsmart.ai.dto.ChatResponseDto;
import com.shopsmart.ai.dto.RecommendationResponseDto;
import com.shopsmart.ai.model.AuditRecord;
import com.shopsmart.ai.tools.CustomerOrderTool;
import com.shopsmart.ai.tools.PolicyKnowledgeTool;
import com.shopsmart.knowledge.dto.KnowledgeResponseDto;
import com.shopsmart.orders.dto.OrderDetailDto;
import com.shopsmart.orders.dto.OrderSummaryDto;
import com.shopsmart.orders.dto.ShipmentEventDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CoordinatedChatService {

    private final CustomerOrderTool orderTool;
    private final PolicyKnowledgeTool policyTool;
    private final RecommendationService recommendationService;
    private final AuditService auditService;

    public CoordinatedChatService(CustomerOrderTool orderTool,
                                  PolicyKnowledgeTool policyTool,
                                  RecommendationService recommendationService,
                                  AuditService auditService) {
        this.orderTool = orderTool;
        this.policyTool = policyTool;
        this.recommendationService = recommendationService;
        this.auditService = auditService;
    }

    public ChatResponseDto processChat(String message) {
        String lower = (message != null ? message.toLowerCase() : "").trim();
        List<ChatResponseDto.ToolExecutionDto> toolActivity = new ArrayList<>();
        List<KnowledgeResponseDto.SourceCitation> sources = new ArrayList<>();

        // Intent detection: Coordinated order tracking + cancellation policy lookup
        boolean asksAboutOrder = lower.contains("where is my order") || lower.contains("order status")
            || lower.contains("track") || lower.contains("shipment") || lower.contains("delivery")
            || lower.contains("delivered") || lower.contains("shipped") || lower.contains("out for delivery");
        boolean asksAboutCancel = lower.contains("cancel") || lower.contains("cancellation");
        boolean asksAboutReturnOrPolicy = lower.contains("return") || lower.contains("policy") || lower.contains("refund");
        boolean asksAboutRecommendation = lower.contains("recommend") || lower.contains("laptop")
                || lower.contains("budget") || lower.contains("suggest") || lower.contains("buy");

        if (asksAboutOrder && asksAboutCancel) {
            // Case study specific coordinated scenario:
            // "Where is my order, and can I cancel after shipment?"
            toolActivity.add(new ChatResponseDto.ToolExecutionDto(
                    "CustomerOrderTool", "getCustomerOrders", "Retrieving simulated orders for authenticated customer"
            ));

            List<OrderSummaryDto> orders;
            try {
                orders = orderTool.getCustomerOrders();
            } catch (Exception e) {
                orders = Collections.emptyList();
            }

            toolActivity.add(new ChatResponseDto.ToolExecutionDto(
                    "PolicyKnowledgeTool", "queryPolicy", "Retrieving applicable Order Cancellation Policy from approved knowledge documents"
            ));

            KnowledgeResponseDto policyResp = policyTool.queryPolicy("order cancellation after shipment", "Policies", "GLOBAL");
            if (policyResp.getSources() != null) {
                sources.addAll(policyResp.getSources());
            }

            StringBuilder sb = new StringBuilder();
            if (!orders.isEmpty()) {
                OrderSummaryDto latestOrder = orders.get(0);
                OrderDetailDto detail = orderTool.getOrderDetail(latestOrder.getId());
                String lastEventStatus = !detail.getShipmentEvents().isEmpty()
                        ? detail.getShipmentEvents().get(detail.getShipmentEvents().size() - 1).getStatus()
                        : latestOrder.getStatus();

                sb.append("Your latest Order #").append(latestOrder.getId().toString().substring(0, 8))
                        .append(" is currently in status '").append(lastEventStatus).append("'. ");

                if (!detail.getShipmentEvents().isEmpty()) {
                    ShipmentEventDto lastEvent = detail.getShipmentEvents().get(detail.getShipmentEvents().size() - 1);
                    if (lastEvent.getEstimatedDelivery() != null) {
                        sb.append("Estimated delivery is ").append(lastEvent.getEstimatedDelivery().toLocalDate()).append(". ");
                    }
                }
            } else {
                sb.append("No active orders found on your customer account. ");
            }

            sb.append("\n\nRegarding cancellation eligibility: According to our approved ")
                    .append(!sources.isEmpty() ? sources.get(0).getDocument() : "Order Cancellation Policy")
                    .append(" (")
                    .append(!sources.isEmpty() ? sources.get(0).getSection() : "Post-Shipment Cancellation Eligibility")
                    .append("):\n")
                    .append("Once an order has SHIPPED or is OUT_FOR_DELIVERY, shipment events are in motion and the order cannot be cancelled in transit. ")
                    .append("You may receive the shipment and initiate a return request following delivery under the applicable return policy. ")
                    .append("\n\n[Note: Cancellation eligibility is strictly informational; this assistant does not execute cancellations.]");

            AuditRecord audit = auditService.recordAudit(
                    message, sb.toString(), toolActivity, sources, "SUCCESS"
            );

            return new ChatResponseDto(sb.toString(), "COORDINATED_ASSISTANCE", toolActivity, sources, null, audit.getId());

        } else if (asksAboutOrder) {
            // Pure order lookup
            toolActivity.add(new ChatResponseDto.ToolExecutionDto(
                    "CustomerOrderTool", "getCustomerOrders", "Fetching customer orders"
            ));

            List<OrderSummaryDto> orders = orderTool.getCustomerOrders();
            if (orders.isEmpty()) {
                String reply = "You currently have no orders on file.";
                AuditRecord audit = auditService.recordAudit(message, reply, toolActivity, sources, "SUCCESS");
                return new ChatResponseDto(reply, "ORDER_TRACKING", toolActivity, sources, null, audit.getId());
            }

            OrderSummaryDto order = orders.get(0);
            OrderDetailDto detail = orderTool.getOrderDetail(order.getId());
            StringBuilder sb = new StringBuilder();
            sb.append("Order #").append(order.getId().toString().substring(0, 8))
                    .append(" placed on ").append(order.getOrderDate().toLocalDate())
                    .append(" is currently ").append(order.getStatus())
                    .append(". Total: $").append(order.getTotalAmount()).append(".\nShipment Events:\n");

            for (ShipmentEventDto event : detail.getShipmentEvents()) {
                sb.append("- ").append(event.getStatus()).append(" (")
                        .append(event.getTimestamp().toLocalDate()).append(")\n");
            }

            AuditRecord audit = auditService.recordAudit(message, sb.toString(), toolActivity, sources, "SUCCESS");
            return new ChatResponseDto(sb.toString(), "ORDER_TRACKING", toolActivity, sources, null, audit.getId());

        } else if (asksAboutReturnOrPolicy) {
            // Policy question via RAG
            toolActivity.add(new ChatResponseDto.ToolExecutionDto(
                    "PolicyKnowledgeTool", "queryPolicy", "Retrieving policy knowledge chunks"
            ));

            KnowledgeResponseDto policyResp = policyTool.queryPolicy(message, null, null);
            if (policyResp.getSources() != null) {
                sources.addAll(policyResp.getSources());
            }

            AuditRecord audit = auditService.recordAudit(message, policyResp.getAnswer(), toolActivity, sources, "SUCCESS");
            return new ChatResponseDto(policyResp.getAnswer(), "POLICY_LOOKUP", toolActivity, sources, null, audit.getId());

        } else {
            // Recommendation intent
            toolActivity.add(new ChatResponseDto.ToolExecutionDto(
                    "ProductCatalogTool", "searchCatalog", "Searching catalog products based on requirements"
            ));
            toolActivity.add(new ChatResponseDto.ToolExecutionDto(
                    "WarehouseInventoryTool", "checkStockStatus", "Verifying warehouse stock availability"
            ));

            RecommendationResponseDto rec = recommendationService.getRecommendations(message);
            String reply;
            if (rec.getClarifyingQuestion() != null) {
                reply = rec.getClarifyingQuestion();
            } else if (!rec.getRecommendations().isEmpty()) {
                reply = "Based on your requirement '" + rec.getInterpretedRequirement() + "', here are top recommended catalog matches:";
            } else {
                reply = "No catalog products matched your specific requirements within budget.";
            }

            AuditRecord audit = auditService.recordAudit(message, reply, toolActivity, sources, "SUCCESS");
            return new ChatResponseDto(reply, "RECOMMENDATION", toolActivity, sources, rec, audit.getId());
        }
    }
}
