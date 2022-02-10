package com.hectorbargues.ReachYourGoals.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioBean {

	private String login = "";
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password = "";

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
