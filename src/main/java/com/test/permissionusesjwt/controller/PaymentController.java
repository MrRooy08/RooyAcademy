package com.test.permissionusesjwt.controller;




import com.test.permissionusesjwt.enums.ApproveStatus;
import com.test.permissionusesjwt.enums.CourseStatus;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.service.EnrollmentService;
import com.test.permissionusesjwt.dto.request.EnrollmentRequest;
import com.test.permissionusesjwt.enums.OrderStatus;
import com.test.permissionusesjwt.service.VNPayService;
import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.repository.*;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class PaymentController {
    private final VNPayService vnpayService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;
    InstructorCourseRepository instructorCourseRepository;
    CourseRepository courseRepository;
    private final AuthUtils authUtils;

    @GetMapping("/create")
    @Transactional
    public ResponseEntity<?> createPayment(
            @RequestParam(required = false) String courseId,
            HttpServletRequest request) throws UnsupportedEncodingException {

        // 1. Lấy user
        String username = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Course> courses = new ArrayList<>();

        // 2. Nếu có courseId => "Mua ngay"
        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .filter(c -> c.getApproveStatus() == ApproveStatus.APPROVED
                            && c.getIsActive() == CourseStatus.PUBLIC)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            courses.add(course);
        }
        else {
            // Ngược lại => lấy từ giỏ hàng
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            if (cart.getItems().isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }
            courses = cart.getItems().stream().map(CartItem::getCourse).toList();
        }

        boolean hadBeenInstructorOfCourse = instructorCourseRepository.hasUserBeenInstructorOfCourse(courseId, user.getId());
        if(!hadBeenInstructorOfCourse) {
            Order order = Order.builder()
                    .user(user)
                    .totalAmount(courses.stream().map(course -> course.getPrice().getPrice())
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .status(OrderStatus.PENDING)
                    .paymentMethod("vnpay")
                    .build();

            for (Course course : courses) {
                OrderDetail od = OrderDetail.builder()
                        .order(order)
                        .course(course)
                        .originalPrice(course.getPrice().getPrice())
                        .discountedPrice(course.getPrice().getPrice())
                        .build();
                order.getItems().add(od);
            }

            orderRepository.save(order);

            // 4. Nếu tất cả khóa học đều miễn phí => enroll ngay
            boolean isPaidOrder = courses.stream()
                    .anyMatch(c -> c.getPrice().getPrice().compareTo(BigDecimal.ZERO) > 0);

            if (!isPaidOrder) {
                order.setStatus(OrderStatus.SUCCESS);
                orderRepository.save(order);

                for (Course course : courses) {
                    enrollmentService.enrollCourse(user, course.getId());
                }

                return ResponseEntity.ok(Map.of("status", "success", "message", "Enrolled successfully"));
            }
            // 5. Gọi VNPay nếu có khóa học phải trả phí
            String ipAddress = request.getRemoteAddr();
            String url = vnpayService.createPaymentUrl(ipAddress, order);
            return ResponseEntity.ok(Map.of("status", "pending", "url", url));
        }
        else {
            throw new AppException(ErrorCode.PREVIOUS_INSTRUCTOR);
        }
    }


    @GetMapping("/vnpay-return")
    @Transactional
    public RedirectView vnpayReturn(@RequestParam Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String orderId = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");
        String amount = params.get("vnp_Amount");
        String bankCode = params.get("vnp_BankCode");
        String cardType = params.get("vnp_CardType");
        String bankTranNo = params.get("vnp_BankTranNo");
        String payDate = params.get("vnp_PayDate");
        String terminalId = params.get("vnp_TmnCode");
        String secureHash = params.get("vnp_SecureHash");

        // 1. Xác thực chữ ký
        if (!vnpayService.validateSignature(params, secureHash)) {
            return new RedirectView("http://localhost:3000/payment/vnpay-return?status=invalid");
        }

        // 2. Lấy Order và kiểm tra idempotent
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return new RedirectView("http://localhost:3000/payment/vnpay-return?status=order_not_found");
        }
        // Nếu đã xử lý, không làm lại
        if (order.getStatus() != OrderStatus.PENDING) {
            return new RedirectView("http://localhost:3000/payment/vnpay-return?status=already_processed");
        }
        String status;
        switch (responseCode){
            case "00": // Cập nhật trạng thái đơn hàng
                order.setStatus(OrderStatus.SUCCESS);
                orderRepository.save(order);

                // Tạo Enrollment cho từng khoá học
                for (OrderDetail od : order.getItems()) {
                    try {
                        enrollmentService.enrollCourse(order.getUser(), od.getCourse().getId());
                    } catch (Exception ignored) {}
                }
                // Xoá giỏ hàng
                User user = order.getUser();
                cartRepository.findByUser(user).ifPresent(cart -> cartItemRepository.deleteAll(cart.getItems()));
                status = "success";
                break;
            case "24":
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                status = "cancelled";
                break;
            default:
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                status = "failed";
                break;
        }
        // 4. Redirect về frontend với đầy đủ dữ liệu
        String redirectUrl = String.format(
                "http://localhost:3000/payment/vnpay-return?" +
                        "status=%s&orderId=%s&transactionNo=%s&amount=%s&bankCode=%s&cardType=%s" +
                        "&bankTranNo=%s&payDate=%s&terminalId=%s&responseCode=%s",
                status,
                orderId,
                transactionNo,
                amount,
                bankCode,
                cardType,
                bankTranNo,
                payDate,
                terminalId,
                responseCode
        );

        return new RedirectView(redirectUrl);
    }





}
