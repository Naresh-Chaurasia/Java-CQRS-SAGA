package com.appsdeveloperblog.photoapp.api.users.ui.controllers;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.appsdeveloperblog.photoapp.api.users.ui.model.CreateUserResponseModel;
import com.appsdeveloperblog.photoapp.api.users.ui.model.UserResponseModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Enumeration;


@RestController
@RequestMapping("/users")
public class UsersController {

	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

	@Autowired
	private Environment env;

	@Autowired
	UsersService usersService;
	
	private void logRequest(HttpServletRequest request) {
	    logger.info("=== Incoming Request ===");
	    logger.info("Method: {}", request.getMethod());
	    logger.info("URL: {}", request.getRequestURL());
	    logger.info("Remote Addr: {}", request.getRemoteAddr());
	    
	    // Log all headers
	    Enumeration<String> headerNames = request.getHeaderNames();
	    while (headerNames.hasMoreElements()) {
	        String headerName = headerNames.nextElement();
	        logger.info("Header - {}: {}", headerName, request.getHeader(headerName));
	    }
	}


	@GetMapping("/status/check")
	public String status(HttpServletRequest request) {
	    logRequest(request);
	    String response = "Working on port " + env.getProperty("local.server.port") + ", token.secret = " + env.getProperty("token.secret");
	    logger.info("Status check response: {}", response);
	    return response;
	}

	@PostMapping(
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
	)
	public ResponseEntity<CreateUserResponseModel>  createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {

		System.out.println("-----------------------------UsersController/createUser--------------------------"+java.time.LocalDateTime.now());

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = usersService.createUser(userDto);

		CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);

		return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
	}


	@GetMapping(value="/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserResponseModel> getUser(
	        @PathVariable("userId") String userId,
	        @RequestHeader HttpHeaders headers,
	        HttpServletRequest request) {
	    
	    logRequest(request);
	    logger.info("Getting user with ID: {}", userId);
	    logger.info("Request headers: {}", headers);

	    try {
	        UserDto userDto = usersService.getUserByUserId(userId);
	        logger.info("Found user: {}", userDto.getUserId());
	        
	        UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);
	        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
	    } catch (Exception e) {
	        logger.error("Error getting user with ID {}: {}", userId, e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
}
