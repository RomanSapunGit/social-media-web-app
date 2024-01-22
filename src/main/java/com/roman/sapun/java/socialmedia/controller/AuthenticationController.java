package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.credentials.AuthRequestDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.ResetPassDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.SignUpDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.TokenDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.exception.*;
import com.roman.sapun.java.socialmedia.service.AuthenticationService;
import io.micrometer.observation.annotation.Observed;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/account")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;

    }

    /**
     * Registers a new user.
     *
     * @param signUpDto The DTO containing the sign-up details of the user.
     * @param image     The image file uploaded by the user.
     * @param request   The HttpServletRequest object.
     * @param response  The HttpServletResponse object.
     * @return The DTO representing the registered user.
     * @throws IOException           If an error occurs while processing the image file.
     * @throws InvalidValueException If the sign-up details are invalid.
     * @throws UserNotFoundException If the user is not found.
     * @throws GeneralSecurityException If a general security-related exception occurs.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @Observed(name = "register.observed")
    @PostMapping()
    public RequestUserDTO registerUser(@ModelAttribute SignUpDTO signUpDto,
                                       @RequestPart("image") MultipartFile image,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException, InvalidValueException, UserNotFoundException, GeneralSecurityException {
        return authenticationService.register(signUpDto, image, request, response);
    }

    /**
     * Initiates the process of resetting the user's password.
     *
     * @param email The email address of the user.
     * @throws MessagingException           If an error occurs while sending the password reset email.
     * @throws UnsupportedEncodingException If the email subject encoding is not supported.
     * @throws UserNotFoundException       If the user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{email}")
    public void forgotPassword(@PathVariable String email) throws MessagingException,
            UnsupportedEncodingException, UserNotFoundException {
        authenticationService.sendEmail(email);
    }

    /**
     * Resets the user's password.
     *
     * @param token        The token associated with the password reset request.
     * @param resetPassDTO The DTO containing the new password and its confirmation.
     * @return The DTO representing the updated password information.
     * @throws TokenExpiredException      If the token has expired.
     * @throws ValuesAreNotEqualException If the new password and its confirmation do not match.
     * @throws UserNotFoundException     If the user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{token}")
    public ResetPassDTO resetPassword(@PathVariable String token, @RequestBody ResetPassDTO resetPassDTO) throws TokenExpiredException, ValuesAreNotEqualException, UserNotFoundException {
        return authenticationService.resetPassword(token, resetPassDTO);
    }

    /**
     * Logs out the user and stores the statistics to database.
     *
     * @param request The HttpServletRequest object.
     * @throws UserStatisticsNotFoundException If user statistics are not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/logout")
    public void logout(HttpServletRequest request) throws UserStatisticsNotFoundException {
        authenticationService.logout(request);
    }

    /**
     * Logs in the user with traditional (username - password) credentials.
     *
     * @param authRequestDTO The DTO containing the user's authentication details.
     * @param request        The HttpServletRequest object.
     * @param response       The HttpServletResponse object.
     * @return The DTO representing the authenticated user.
     * @throws InvalidValueException If the authentication details are invalid.
     */
    @ResponseStatus(HttpStatus.OK)
    @Observed(name = "login.observed")
    @PostMapping("/login")
    public AuthRequestDTO login(@RequestBody AuthRequestDTO authRequestDTO,
                                HttpServletRequest request,
                                HttpServletResponse response) throws InvalidValueException {
        return authenticationService.loginUser(authRequestDTO, request, response);
    }

    /**
     * Logs in the user using google jwt token.
     *
     * @param token    The Google OAuth token provided in the Authorization header.
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @return The DTO representing the authenticated user.
     * @throws UserNotFoundException    If the user is not found.
     * @throws GeneralSecurityException If a general security-related exception occurs.
     * @throws IOException              If an I/O exception occurs.
     * @throws InvalidValueException    If the provided token is invalid.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/google/login")
    public TokenDTO googleLogin(@RequestHeader("Authorization") String token,
                                HttpServletRequest request,
                                HttpServletResponse response) throws UserNotFoundException, GeneralSecurityException, IOException, InvalidValueException {
        return authenticationService.loginUser(token, request, response);
    }

    /**
     * Validates the user's session.
     *
     * @param httpSession The HttpSession object.
     * @return True if the session is valid; otherwise, false.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public boolean validateSession(HttpSession httpSession) {
        return authenticationService.validateSession(httpSession);
    }
}
