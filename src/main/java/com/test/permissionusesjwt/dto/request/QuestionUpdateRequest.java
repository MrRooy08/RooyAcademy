package com.test.permissionusesjwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionUpdateRequest {
    String id; // ID của câu hỏi nếu đã tồn tại, null nếu tạo mới
    String questionContent;
    int index;
    String answerContent; // Đáp án chuẩn
}
