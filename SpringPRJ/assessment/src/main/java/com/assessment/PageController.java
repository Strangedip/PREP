package com.assessment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/home")
	public String greeting() {
		return "home";
	}

}