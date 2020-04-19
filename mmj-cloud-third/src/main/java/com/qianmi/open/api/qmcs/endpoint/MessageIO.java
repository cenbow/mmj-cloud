package com.qianmi.open.api.qmcs.endpoint;

import java.nio.ByteBuffer;

// simple protocol impl
// care about Endian
// https://github.com/wsky/RemotingProtocolParser/issues/3
public class MessageIO {
	public interface MessageEncoder {
		public void writeMessage(ByteBuffer buffer, Message message);
	}

	public interface MessageDecoder {
		public Message readMessage(ByteBuffer buffer);
	}

	// TODO codec will rewrite in v2.0, current design just for compatible
	private static final MessageDecoder messageDecoder = new MessageDecoderImpl();
	private static final MessageEncoder messageEncoder = new MessageEncoderImpl();

	public static Message readMessage(ByteBuffer buffer) {
		return messageDecoder.readMessage(buffer);
	}

	public static void writeMessage(ByteBuffer buffer, Message message) {
		messageEncoder.writeMessage(buffer, message);
	}
}