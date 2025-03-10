package com.localservice.localservice_api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MultipleUpdateResponseDto<T> {

	List<T> updated;
	List<T> notUpdated;
	
	public MultipleUpdateResponseDto () {
		updated = new ArrayList<>();
		notUpdated = new ArrayList<>();
	}
}
