package com.internalproject.user_service.grpc;

import com.internalproject.user_service.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void validateUser(ValidateUserRequest request,
                             StreamObserver<ValidateUserResponse> responseObserver) {
        boolean exists = userRepository.existsById(request.getUserId());
        ValidateUserResponse response = ValidateUserResponse.newBuilder()
                .setValid(exists)
                .setMessage(exists ? "User is valid" : "User not found")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(GetUserRequest request,
                        StreamObserver<GetUserResponse> responseObserver) {
        userRepository.findById(request.getUserId()).ifPresentOrElse(
                user -> {
                    GetUserResponse response = GetUserResponse.newBuilder()
                            .setUserId(user.getUserId())
                            .setFirstName(user.getFirstName())
                            .setLastName(user.getLastName() != null ? user.getLastName() : "")
                            .setEmailId(user.getEmail())
                            .setMobileNumber(user.getMobileNumber() != null ? user.getMobileNumber() : "")
                            .setFound(true)
                            .build();
                    responseObserver.onNext(response);
                },
                () -> {
                    GetUserResponse response = GetUserResponse.newBuilder()
                            .setFound(false).build();
                    responseObserver.onNext(response);
                }
        );
        responseObserver.onCompleted();
    }
}
