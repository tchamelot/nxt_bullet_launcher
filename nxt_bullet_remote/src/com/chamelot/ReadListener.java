package com.chamelot;

public interface ReadListener {
	void readNew(byte[] data);
	void readEOC();
}
