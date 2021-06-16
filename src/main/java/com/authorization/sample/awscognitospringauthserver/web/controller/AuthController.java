package com.authorization.sample.awscognitospringauthserver.web.controller;

import com.authorization.sample.awscognitospringauthserver.service.UserService;
import com.authorization.sample.awscognitospringauthserver.service.dto.LoginDTO;
import com.authorization.sample.awscognitospringauthserver.service.dto.UserPasswordUpdateDTO;
import com.authorization.sample.awscognitospringauthserver.web.response.AuthenticatedResponse;
import com.authorization.sample.awscognitospringauthserver.web.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<BaseResponse> login(@RequestBody @Validated LoginDTO loginRequest) {
		return new ResponseEntity<>(userService.authenticate(loginRequest),HttpStatus.OK);
	}

	@PutMapping("/change-password")
	public ResponseEntity<BaseResponse> changePassword(@RequestBody @Validated UserPasswordUpdateDTO userPasswordUpdateDTO) {
		AuthenticatedResponse authenticatedResponse = userService.updateUserPassword(userPasswordUpdateDTO);

		return new ResponseEntity<>(new BaseResponse(authenticatedResponse, "Update successfully", false),HttpStatus.OK);
	}

	@DeleteMapping("/logout")
	public ResponseEntity<BaseResponse>  logout(@RequestHeader("Authorization") String bearerToken) {
		if(bearerToken != null && bearerToken.contains("Bearer "))
		{
			String accessToken = bearerToken.replace("Bearer ", "");

			userService.logout(accessToken);

			return new ResponseEntity<>(new BaseResponse(null, "Logout successfully", false),HttpStatus.OK);
		}
		return new ResponseEntity<>(new BaseResponse(null, "Header not correct"),HttpStatus.BAD_REQUEST);
	}

}
