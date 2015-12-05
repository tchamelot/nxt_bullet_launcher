package com.chamelot.communication;

public interface ReadListener {
	void readNew(byte[] data);
	void readEOC();
}
