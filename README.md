# Tính năng Bài tập tự luận với Câu hỏi và Đáp án

## Tổng quan
Tính năng này cho phép giảng viên tạo bài tập tự luận với nhiều câu hỏi, mỗi câu hỏi có một đáp án chuẩn. Học viên có thể nộp bài tập với câu trả lời chi tiết cho từng câu hỏi, và giảng viên có thể đánh giá bài tập.

## Cấu trúc dữ liệu

### 1. Assignment (Bài tập)
- `id`: ID duy nhất của bài tập
- `name`: Tên bài tập
- `description`: Mô tả bài tập
- `instructions`: Hướng dẫn làm bài
- `index`: Thứ tự bài tập trong section
- `estimatedTime`: Thời gian ước tính hoàn thành
- `section`: Section chứa bài tập
- `instructor`: Giảng viên tạo bài tập
- `questions`: Danh sách câu hỏi của bài tập

### 2. AssignmentQuestion (Câu hỏi bài tập)
- `id`: ID duy nhất của câu hỏi
- `questionContent`: Nội dung câu hỏi
- `index`: Thứ tự câu hỏi trong bài tập
- `assignment`: Bài tập chứa câu hỏi
- `answer`: Đáp án chuẩn của câu hỏi (OneToOne với AssignmentAnswer)

### 3. AssignmentAnswer (Đáp án chuẩn)
- `id`: ID duy nhất của đáp án
- `answerContent`: Nội dung đáp án chuẩn
- `question`: Câu hỏi tương ứng (OneToOne với AssignmentQuestion)

### 4. CompletedAssignment (Bài tập đã hoàn thành)
- `id`: ID duy nhất của bài tập đã hoàn thành
- `progress`: Tiến độ học tập của học viên
- `assignment`: Bài tập gốc
- `question`: Câu hỏi cụ thể (nếu nộp theo từng câu hỏi)
- `finishedAt`: Thời gian hoàn thành
- `feedback`: Nhận xét tổng quát từ giảng viên
- `status`: Trạng thái bài tập (SUBMITTED, EVALUATED, etc.)
- `submission`: Nội dung bài làm tổng quát
- `answerContent`: Nội dung câu trả lời của học viên

## API Endpoints

### Quản lý Bài tập (AssignmentController)

#### 1. Tạo bài tập mới
```
POST /assignments
Authorization: Bearer {token}
Role: INSTRUCTOR

// Tạo bài tập đơn giản
{
  "name": "Bài tập tự luận 1",
  "description": "Mô tả bài tập",
  "instructions": "Hướng dẫn làm bài",
  "index": "1",
  "estimatedTime": "60",
  "sectionId": "section-id"
}

// Tạo bài tập với câu hỏi và đáp án
{
  "name": "Bài tập tự luận 1",
  "description": "Mô tả bài tập",
  "instructions": "Hướng dẫn làm bài",
  "index": "1",
  "estimatedTime": "60",
  "sectionId": "section-id",
  "questions": [
    {
      "questionContent": "Câu hỏi 1: Giải thích về...",
      "index": 1,
      "answerContent": "Đáp án chuẩn cho câu hỏi 1"
    },
    {
      "questionContent": "Câu hỏi 2: Phân tích...",
      "index": 2,
      "answerContent": "Đáp án chuẩn cho câu hỏi 2"
    }
  ]
}
```

#### 2. Lấy thông tin bài tập
```
GET /assignments/{assignmentId}
Authorization: Bearer {token}
```

#### 3. Cập nhật bài tập
```
PUT /assignments/{assignmentId}
Authorization: Bearer {token}
Role: INSTRUCTOR

// Cập nhật thông tin cơ bản
{
  "name": "Bài tập tự luận 1 - Đã cập nhật",
  "description": "Mô tả bài tập đã cập nhật",
  "instructions": "Hướng dẫn làm bài đã cập nhật",
  "index": "2",
  "estimatedTime": "90"
}

// Cập nhật với câu hỏi và đáp án
{
  "name": "Bài tập tự luận 1 - Đã cập nhật",
  "description": "Mô tả bài tập đã cập nhật",
  "instructions": "Hướng dẫn làm bài đã cập nhật",
  "index": "2",
  "estimatedTime": "90",
  "questions": [
    {
      "id": "existing-question-id", // ID câu hỏi đã tồn tại (để cập nhật)
      "questionContent": "Câu hỏi 1: Giải thích về... - Đã cập nhật",
      "index": 1,
      "answerContent": "Đáp án chuẩn cho câu hỏi 1 - Đã cập nhật"
    },
    {
      "id": null, // null để tạo câu hỏi mới
      "questionContent": "Câu hỏi mới: Phân tích...",
      "index": 2,
      "answerContent": "Đáp án chuẩn cho câu hỏi mới"
    }
  ]
}
```

#### 4. Lấy danh sách bài tập theo section
```
GET /assignments/section/{sectionId}
Authorization: Bearer {token}
```

#### 5. Lấy danh sách bài tập theo khóa học
```
GET /assignments/course/{courseId}
Authorization: Bearer {token}
```

#### 6. Nộp bài tập
```
POST /assignments/submit
Authorization: Bearer {token}

{
  "assignmentId": "assignment-id",
  "enrollmentId": "enrollment-id",
  "submission": "Nội dung bài làm"
}
```

#### 7. Nộp bài tập với câu trả lời chi tiết
```
POST /assignments/submit-with-answers
Authorization: Bearer {token}

{
  "assignmentId": "assignment-id",
  "progressId": "progress-id",
  "submission": "Nội dung bài làm tổng quát",
  "answers": [
    {
      "questionId": "question-id-1",
      "answerContent": "Câu trả lời cho câu hỏi 1"
    },
    {
      "questionId": "question-id-2", 
      "answerContent": "Câu trả lời cho câu hỏi 2"
    }
  ]
}
```

#### 8. Lấy danh sách bài tập đã hoàn thành theo progress
```
GET /assignments/completed/progress/{progressId}
Authorization: Bearer {token}
```

#### 9. Lấy danh sách bài tập đã hoàn thành theo assignment (cho giảng viên)
```
GET /assignments/completed/assignment/{assignmentId}
Authorization: Bearer {token}
Role: INSTRUCTOR
```

#### 10. Đánh giá bài tập
```
POST /assignments/evaluate?progressId={progressId}&assignmentId={assignmentId}&feedback={feedback}
Authorization: Bearer {token}
Role: INSTRUCTOR
```

### Quản lý Câu hỏi (tích hợp trong AssignmentController)

#### 11. Tạo câu hỏi mới cho bài tập
```
POST /assignments/{assignmentId}/questions
Authorization: Bearer {token}
Role: INSTRUCTOR

{
  "questionContent": "Nội dung câu hỏi",
  "index": 1,
  "answerContent": "Nội dung đáp án chuẩn"
}
```

#### 12. Lấy danh sách câu hỏi theo bài tập
```
GET /assignments/{assignmentId}/questions?includeAnswer=false
Authorization: Bearer {token}
```

#### 13. Lấy thông tin câu hỏi theo ID
```
GET /assignments/questions/{questionId}?includeAnswer=false
Authorization: Bearer {token}
```

#### 14. Cập nhật câu hỏi
```
PUT /assignments/questions/{questionId}
Authorization: Bearer {token}
Role: INSTRUCTOR

{
  "questionContent": "Nội dung câu hỏi đã cập nhật",
  "index": 1,
  "answerContent": "Nội dung đáp án chuẩn đã cập nhật"
}
```

#### 15. Xóa câu hỏi
```
DELETE /assignments/questions/{questionId}
Authorization: Bearer {token}
Role: INSTRUCTOR
```

## Quyền truy cập

### Giảng viên (INSTRUCTOR)
- Tạo bài tập (có thể tạo đơn giản hoặc với câu hỏi và đáp án)
- Cập nhật bài tập (chỉ giảng viên tạo bài tập)
- Tạo, cập nhật, xóa câu hỏi và đáp án
- Xem tất cả bài tập đã nộp
- Đánh giá bài tập

### Học viên (STUDENT)
- Xem bài tập và câu hỏi (không bao gồm đáp án chuẩn)
- Nộp bài tập với câu trả lời
- Xem bài tập đã nộp

## Workflow

### 1. Giảng viên tạo bài tập
**Cách 1: Tạo bài tập hoàn chỉnh một lần**
1. Sử dụng endpoint `POST /assignments` với trường `questions`
2. Gửi thông tin bài tập cùng với danh sách câu hỏi và đáp án
3. Hệ thống tự động tạo bài tập, câu hỏi và đáp án

**Cách 2: Tạo bài tập và thêm câu hỏi riêng**
1. Tạo bài tập mới với endpoint `POST /assignments` (không có trường `questions`)
2. Thêm từng câu hỏi với endpoint `POST /assignments/{assignmentId}/questions`
3. Mỗi câu hỏi có một đáp án chuẩn

### 2. Giảng viên cập nhật bài tập
1. Sử dụng endpoint `PUT /assignments/{assignmentId}` để cập nhật thông tin bài tập
2. Có thể cập nhật thông tin cơ bản hoặc thêm/sửa/xóa câu hỏi và đáp án
3. Nếu cung cấp `id` trong `questions`, sẽ cập nhật câu hỏi đã tồn tại
4. Nếu `id` là `null`, sẽ tạo câu hỏi mới
5. Các câu hỏi không có trong danh sách cập nhật sẽ bị xóa

### 3. Học viên làm bài tập
1. Xem bài tập và danh sách câu hỏi
2. Nộp bài tập với câu trả lời cho từng câu hỏi
3. Hệ thống tạo CompletedAssignment cho mỗi câu trả lời

### 4. Giảng viên đánh giá
1. Xem danh sách bài tập đã nộp
2. Đánh giá bài tập với nhận xét tổng quát
3. Cập nhật trạng thái bài tập

### 5. Học viên xem kết quả
1. Xem bài tập đã nộp
2. Xem nhận xét từ giảng viên

## Lưu ý quan trọng

1. **Bảo mật đáp án**: Đáp án chuẩn chỉ hiển thị cho giảng viên, học viên không thể xem
2. **Trạng thái bài tập**: 
   - SUBMITTED: Đã nộp bài
   - EVALUATED: Đã đánh giá
3. **Gộp dữ liệu**: StudentAnswer đã được gộp vào CompletedAssignment để đơn giản hóa cấu trúc
4. **Quyền truy cập**: Tất cả các thao tác đều có kiểm tra quyền truy cập phù hợp
5. **Tạo bài tập**: Có 2 cách tạo bài tập:
   - Tạo hoàn chỉnh: Một API call với trường `questions` tạo cả bài tập, câu hỏi và đáp án
   - Tạo từng phần: Tạo bài tập trước (không có `questions`), sau đó thêm câu hỏi từng cái một 