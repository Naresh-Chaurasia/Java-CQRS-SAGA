package com.appsdeveloperblog.photoapp.api.users.data;

import java.util.ArrayList;
import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.appsdeveloperblog.photoapp.api.users.ui.model.AlbumResponseModel;



//@FeignClient(name = "albums-ws", fallbackFactory = AlbumsFallbackFactory.class)
@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {

	@GetMapping("/users/{id}/albums")
	@CircuitBreaker(name="albums-ws",fallbackMethod = "getAlbumsFallBack")
	public List<AlbumResponseModel> getAlbums(@PathVariable String id);

	default  List<AlbumResponseModel> getAlbumsFallBack(String id, Throwable exception){
		System.out.println("Param = " + id + "Exce = " + exception.toString());
		return  new ArrayList<>();
	}
}

