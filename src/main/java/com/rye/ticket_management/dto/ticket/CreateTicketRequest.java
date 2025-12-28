package com.rye.ticket_management.dto.ticket;

import com.rye.ticket_management.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
        @NotBlank(message = "標題不能空白")
        @Size(max = 100, message = "標題長度不能超過 100 個字元")
        String title,

        @Size(max = 500, message = "內容不能超過 500 個字元")
        String description,

        @NotNull(message = "優先級不能空白")
        TicketPriority priority
) {}
