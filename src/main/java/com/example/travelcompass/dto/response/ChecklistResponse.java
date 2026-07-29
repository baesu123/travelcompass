package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChecklistResponse {

    private Long id;
    private String itemName;
    private boolean checked;

}
