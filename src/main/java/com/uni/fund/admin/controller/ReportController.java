package com.uni.fund.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.uni.fund.admin.service.ReportService;

@Controller
public class ReportController {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired ReportService reportService;
	
}
