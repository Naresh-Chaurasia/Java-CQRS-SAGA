package com.appsdeveloperblog.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.appsdeveloperblog.photoapp.api.users.ui.model.AlbumResponseModel;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/*import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;*/
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;


import com.appsdeveloperblog.photoapp.api.users.data.*;

@Service
public class UsersServiceImpl  implements UsersService {

	UsersRepository usersRepository;
	RestTemplate restTemplate;
	@Autowired
	Environment environment;

	AlbumsServiceClient albumsServiceClient;

	BCryptPasswordEncoder bCryptPasswordEncoder;

	Logger logger = LoggerFactory.getLogger(this.getClass());


//	@Autowired
//	public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RestTemplate restTelmplate)
//	{
//		this.usersRepository = usersRepository;
//		//this.environment = environment;
//		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//		this.restTemplate = restTemplate;
//	}

	@Autowired
	public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RestTemplate restTemplate, AlbumsServiceClient albumsServiceClient)
	{
		this.usersRepository = usersRepository;
		//this.environment = environment;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.restTemplate = restTemplate;
		this.albumsServiceClient = albumsServiceClient;
	}


	@Override
	public UserDto createUser(UserDto userDetails) {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------UsersServiceImpl/createUser--------------------------");


		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);


		usersRepository.save(userEntity);

		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);

		return returnValue;
	}

	//Coming from super class.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		System.out.println("-----------------------------UsersServiceImpl/loadUserByUsername--------------------------"+java.time.LocalDateTime.now());
		System.out.println("-----------------------------We are overriding inbuilt method--------------------------"+java.time.LocalDateTime.now());

		//UserEntity userEntity = usersRepository.findByEmail(username);
		UserEntity userEntity = usersRepository.findByEmail(username);

		if(userEntity == null) throw new UsernameNotFoundException(username);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {

		System.out.println("-----------------------------UsersServiceImpl/getUserDetailsByEmail--------------------------"+java.time.LocalDateTime.now());

		UserEntity userEntity = usersRepository.findByEmail(email);

		if(userEntity == null)
			throw new UsernameNotFoundException(email);


		return new ModelMapper().map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {

		UserEntity userEntity = usersRepository.findByUserId(userId);
		if(userEntity == null) throw new UsernameNotFoundException("User not found");

		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);


		// REST Start
     	// String albumsUrl = String.format(environment.getProperty("albums.url"), userId);
        // ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
        // });
        // List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
		// REST End

		// Feign Start
		List<AlbumResponseModel> albumsList = null;

		logger.info("Before calling albums Microservice");
		try{
			logger.info("--- Calling using Feing Client ---");
			albumsList = albumsServiceClient.getAlbums(userId);
		}catch (FeignException fe){
			logger.info(fe.toString());
		}
		// Feign End

		logger.info("After calling albums Microservice");

		userDto.setAlbums(albumsList);

		return userDto;



	}

}
