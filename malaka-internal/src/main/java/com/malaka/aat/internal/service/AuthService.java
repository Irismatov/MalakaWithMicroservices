package com.malaka.aat.internal.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.LoginException;
import com.malaka.aat.core.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.malaka.aat.internal.dto.auth.LoginResponse;
import com.malaka.aat.internal.dto.auth.UserLoginDto;
import com.malaka.aat.internal.security.jwt.JwtTokenProvider;


@RequiredArgsConstructor
@Service
public class AuthService {


    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    public BaseResponse login(UserLoginDto dto) {

        try {
            BaseResponse response = new BaseResponse();
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            Authentication authenticate = authenticationManager.authenticate(authenticationToken);

            LoginResponse loginResponse = new LoginResponse();
            String accessToken = jwtTokenProvider.generateAccessToken(authenticate);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authenticate);

            loginResponse.setAccessToken(accessToken);
            loginResponse.setRefreshToken(refreshToken);

            response.setData(loginResponse);
            SecurityContextHolder.getContext().setAuthentication(authenticate);


            ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
            return response;

        } catch (AuthenticationException e) {
           throw new LoginException();
        }

    }



}
