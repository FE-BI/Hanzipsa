package com.a407.back.model.service;

import com.a407.back.domain.User;
import com.a407.back.dto.NotificationListResponse;
import com.a407.back.dto.UserAccountRequest;
import com.a407.back.dto.UserAccountResponse;
import com.a407.back.dto.UserNearZipsaResponse;
import com.a407.back.dto.UserRecordsResponse;
import com.a407.back.dto.UserReservationResponse;
import java.util.List;

public interface UserService {

    Long save(User user);

    List<NotificationListResponse> findNotificationsByUserId(Long userId);

    boolean isWorkedDistinction(Long userId);

    UserNearZipsaResponse findNearZipsaList(Long userId);

    User findByUserId(Long userId);

    UserRecordsResponse findRecordsByUserId(Long userId);

    UserReservationResponse findReservationByUserId(Long userId);


    UserAccountResponse accountAdd(UserAccountRequest userAccountRequest);

    String getMaskedCardNumber(Long userId);

    void accountDelete(Long userId);
}
