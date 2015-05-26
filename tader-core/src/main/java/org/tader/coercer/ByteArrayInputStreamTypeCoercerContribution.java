package org.tader.coercer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.tader.TypeCoercerContribution;


public class ByteArrayInputStreamTypeCoercerContribution extends TypeCoercerContribution<byte[], InputStream> {
	public ByteArrayInputStreamTypeCoercerContribution() {
		super(byte[].class, InputStream.class);
	}

	@Override
	public InputStream coerce(byte[] source) {
		return new ByteArrayInputStream(source);
	}

}
