package com.ichiroku.jerseyuserapi.model;

public class RefreshToken {
	private String signature;
	private boolean revoked;
	
	public RefreshToken() {	}
	
	public String getSignature() {
		return this.signature;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public boolean isRevoked() {
		return this.revoked;
	}
	
	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}
}
