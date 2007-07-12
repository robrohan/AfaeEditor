package com.rohanclan.afae.modes;

public class SpanRegexp extends Span {
	protected String hash_chars;

	public void accept(IVisitor visitor) {
		visitor.acceptSpanRegexp(this);
	}
	
	///////////////////////////////////////////////
	public String getHashChars() {
		return hash_chars;
	}

	public void setHashChars(String hash_chars) {
		this.hash_chars = hash_chars;
	}
}
