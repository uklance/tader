package org.tader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.tader.jdbc.TypeCoercerContribution;

public class ByteArrayInputStreamTypeCoercerContribution extends TypeCoercerContribution<byte[], InputStream> {
	public ByteArrayInputStreamTypeCoercerContribution() {
		super(byte[].class, InputStream.class);
	}

	@Override
	public InputStream coerce(byte[] source) {
		return new ByteArrayInputStream(source);
	}

}