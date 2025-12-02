package com.malaka.aat.external.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.LoginException;
import com.malaka.aat.core.exception.custom.OneIdInfoException;
import com.malaka.aat.core.exception.custom.OneIdTokenException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import com.malaka.aat.external.clients.egov.EgovClient;
import com.malaka.aat.external.clients.egov.gcp.EgovGcpResponse;
import com.malaka.aat.external.clients.one_id.OneIdClient;
import com.malaka.aat.external.clients.one_id.OneIdInfoResponse;
import com.malaka.aat.external.clients.one_id.TokenResponse;
import com.malaka.aat.external.dto.auth.LoginOneIdRequest;
import com.malaka.aat.external.dto.auth.LoginResponse;
import com.malaka.aat.external.dto.auth.UserLoginDto;
import com.malaka.aat.external.enumerators.student.Gender;
import com.malaka.aat.external.model.Passport;
import com.malaka.aat.external.model.User;
import com.malaka.aat.external.repository.UserRepository;
import com.malaka.aat.external.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    @Value("${app.file.user-images.path}")
    private String userImagesPath;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final OneIdClient oneIdClient;
    private final EgovClient egovClient;

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

    @Transactional
    public BaseResponse loginWithOneId(LoginOneIdRequest request) {
        TokenResponse token;
        try {
            token = oneIdClient.getToken(request.getCode());
        } catch (Exception e) {
            throw new OneIdTokenException();
        }

        OneIdInfoResponse oneIdInfoResponse;
        try {
            oneIdInfoResponse = oneIdClient.getOneIdInfo(token.getAccess_token());
        } catch (Exception e) {
            throw new OneIdInfoException();
        }

        createUserIfNotExists(oneIdInfoResponse.getPin());

        LoginResponse loginResponse = new LoginResponse();
        String accessToken = jwtTokenProvider.generateAccessToken(oneIdInfoResponse.getPin());
        String refreshToken = jwtTokenProvider.generateRefreshToken(oneIdInfoResponse.getPin());

        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);

        BaseResponse response = new BaseResponse();
        response.setData(loginResponse);

        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }


    @Transactional
    public void createUserIfNotExists(String pinfl) {
        Optional<User> byPinfl = userRepository.findByPinfl(pinfl);

        if (byPinfl.isPresent()) {
            return;
        }

        EgovGcpResponse info = egovClient.getInfo(pinfl);
        User user = new User();
        user.setPinfl(pinfl);
        EgovGcpResponse.EgovGcpResponseData egovGcpResponseData = info.getData().get(0);
        user.setFirstName(egovGcpResponseData.getFirstNameOz());
        user.setLastName(egovGcpResponseData.getLastNameOz());
        user.setMiddleName(egovGcpResponseData.getMiddleNameOz());
        user.setBirthDate(egovGcpResponseData.getBirthDate());
        user.setNationality(egovGcpResponseData.getNationality());

        switch (egovGcpResponseData.getSex()) {
            case "1" -> {
                user.setGender(Gender.MALE);
            }
            case "2" -> {
                user.setGender(Gender.FEMALE);
            }
            default -> {
                user.setGender(Gender.UNKNOWN);
            }
        }
        String photo = egovGcpResponseData.getPhoto();
        if (photo != null) {
            try {
                Path folderPath = Path.of(userImagesPath);

                if (Files.notExists(folderPath)) {
                    Files.createDirectories(folderPath);
                }

                byte[] bytes = Base64.getDecoder().decode(photo);
                Path filePath = folderPath.resolve(pinfl);

                Files.write(filePath, bytes);
                user.setImgPath(filePath.toString());
            } catch (Exception e) {
                throw new SystemException(e.getMessage());
            }
        }

        String currentDocument = egovGcpResponseData.getCurrentDocument();
        Optional<EgovGcpResponse.Document> documentOptional = egovGcpResponseData.getDocuments().stream().filter(e -> e.getDocument().equals(currentDocument)).findFirst();
        if (documentOptional.isPresent()) {
            EgovGcpResponse.Document document = documentOptional.get();
            Passport passport = new Passport();
            passport.setStatus(document.getStatus());
            passport.setSerNumber(document.getDocument());
            passport.setDocGivenPlace(document.getDocGivePlace());
            passport.setGivenDate(document.getDateBegin());
            passport.setExpiryDate(document.getDateEnd());
            passport.setType(document.getType());
            passport.setIsCurrent((short) 1);
            user.getPassports().forEach(e -> e.setIsDeleted((short) 1));
            user.getPassports().add(passport);
            passport.setUser(user);
        }
        userRepository.save(user);
    }
}
