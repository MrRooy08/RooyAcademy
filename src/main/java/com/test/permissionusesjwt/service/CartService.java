package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.dto.request.CartCreateRequest;
import com.test.permissionusesjwt.dto.request.CartItemRequest;
import com.test.permissionusesjwt.dto.response.CartItemResponse;
import com.test.permissionusesjwt.dto.response.CartResponse;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.enums.ApproveStatus;
import com.test.permissionusesjwt.enums.CourseStatus;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    private BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .map(ci -> ci.getDiscountedPrice() != null ? ci.getDiscountedPrice() : ci.getOriginalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    AuthUtils authUtils;

    public CartResponse create(CartCreateRequest request) {
        String username = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Tìm giỏ hàng của người dùng (nếu có)
        Cart cart = cartRepository.findByUser(user).orElse(null);
        boolean isNewCart = false;
        if (cart == null) {
            cart = Cart.builder()
                    .user(user)
                    .build();
            isNewCart = true;
        }

        // 3. Thêm các item được gửi lên
        for (CartItemRequest itemReq : request.getItems()) {
            Course course = courseRepository.findById(itemReq.getCourseId())
                    .filter(c -> c.getApproveStatus() == ApproveStatus.APPROVED
                            && c.getIsActive() == CourseStatus.PUBLIC)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

            // Nếu giỏ đã tồn tại thì kiểm tra trùng khoá học
            if (!isNewCart && cartItemRepository.existsByCartAndCourse(cart, course)) {
                throw new AppException(ErrorCode.CART_ITEM_EXISTED);
            }

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .course(course)
                    .originalPrice(itemReq.getOriginalPrice())
                    .discountedPrice(itemReq.getDiscountedPrice())
                    .discountCode(itemReq.getDiscountCode())
                    .build();
            cart.getItems().add(item);
        }

        // 4. Cập nhật tổng tiền và lưu lại
        cart.setTotalAmount(calculateTotal(cart));
        cart = cartRepository.save(cart); // cascade sẽ lưu cả items

        return buildResponse(cart);
    }


    public CartResponse removeItem(String cartId, String courseId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        String username = authUtils.getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!cart.getUser().equals(user)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        CartItem item = cartItemRepository.findByCartAndCourse(cart, course)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_EXISTED));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        cart.setTotalAmount(calculateTotal(cart));
        cart = cartRepository.save(cart);

        return buildResponse(cart);
    }

    private CartResponse buildResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream().map(ci ->
                CartItemResponse.builder()
                        .id(ci.getCourse().getId())
                        .originalPrice(ci.getOriginalPrice())
                        .discountedPrice(ci.getDiscountedPrice())
                        .discountCode(ci.getDiscountCode())
                        .build()
        ).collect(java.util.stream.Collectors.toList());

        return CartResponse.builder()
                .cartId(cart.getId())
                .totalAmount(cart.getTotalAmount())
                .items(itemResponses)
                .build();
    }

    // Expose cart mapping so other services can reuse
    public CartResponse toCartResponse(Cart cart) {
        return buildResponse(cart);
    }

    public void delete(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        // Optional: ensure only owner can delete
        String username = authUtils.getCurrentUsername();
        if (!cart.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        cartRepository.delete(cart);
    }
}
