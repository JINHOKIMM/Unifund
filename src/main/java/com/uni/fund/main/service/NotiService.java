package com.uni.fund.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uni.fund.main.dao.NotiDAO;

@Service
public class NotiService {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired NotiDAO notiDAO;
	
}
