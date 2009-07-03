package org.argeo.slc.web.mvc.management;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.HttpRequestHandler;

import com.springsource.json.writer.JSONWriter;
import com.springsource.json.writer.JSONWriterImpl;

public class GetCredentials implements HttpRequestHandler {

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		JSONWriter jsonWriter = new JSONWriterImpl(response.getWriter())
				.object().key("user").value(authentication.getName());
		jsonWriter.key("roles").array();
		for (GrantedAuthority ga : authentication.getAuthorities()) {
			jsonWriter.value(ga.getAuthority());
		}
		jsonWriter.endArray();
		jsonWriter.endObject();
	}

}
