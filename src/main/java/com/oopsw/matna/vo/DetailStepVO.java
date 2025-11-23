package com.oopsw.matna.vo;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailStepVO {
    private Integer stepOrder;
    private String content;
    private String imageUrl;
}
