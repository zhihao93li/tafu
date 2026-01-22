package com.tafu.bazi.controller.admin;

import com.tafu.bazi.dto.response.AdminUserResponse;
import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.User;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * AdminUserController
 *
 * <p>描述: 管理后台-用户管理 API。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AdminUserController {

  private final UserRepository userRepository;

  @GetMapping
  public ApiResponse<Page<AdminUserResponse>> list(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {

    Page<User> userPage =
        userRepository.findAll(PageRequest.of(page - 1, limit, Sort.by("createdAt").descending()));

    List<AdminUserResponse> dtos =
        userPage.getContent().stream()
            .map(
                user ->
                    AdminUserResponse.builder()
                        .id(user.getId())
                        .phone(user.getPhone())
                        .username(user.getUsername())
                        .balance(
                            user.getPointsAccount() != null
                                ? user.getPointsAccount().getBalance()
                                : 0)
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
            .collect(Collectors.toList());

    return ApiResponse.success(
        new PageImpl<>(dtos, userPage.getPageable(), userPage.getTotalElements()));
  }

  @GetMapping("/{id}")
  public ApiResponse<AdminUserResponse> get(@PathVariable String id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    AdminUserResponse dto =
        AdminUserResponse.builder()
            .id(user.getId())
            .phone(user.getPhone())
            .username(user.getUsername())
            .balance(user.getPointsAccount() != null ? user.getPointsAccount().getBalance() : 0)
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();

    return ApiResponse.success(dto);
  }
}
