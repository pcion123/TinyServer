package com.tinybee.server.netty.server.handler.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.netty.message.Message;
import com.tinybee.server.App;
import com.tinybee.server.constant.SystemId;
import com.tinybee.server.enitity.Player;
import com.tinybee.server.module.system.User;
import com.tinybee.server.netty.server.header.Header;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class HeaderDecoderHandler extends LengthFieldBasedFrameDecoder
{
	private static Logger mLogger = LoggerFactory.getLogger(HeaderDecoderHandler.class);
	
	private final static boolean BIGENDIAN = true;
	
	private final static int MAX_FRAME_LENGTH = 1024 * 1024;
	private final static int LENGTH_FIELD_LENGTH = 4;
	private final static int LENGTH_FIELD_OFFSET = 54;
	private final static int LENGTH_ADJUSTMENT = 0;
	private final static int INITIAL_BYTES_TO_STRIP = 0;
	
	private short version;
	private byte mainNo;
	private byte subNo;
	private boolean isCompress;
	private long sessionId;
	private String token;
	private byte serialId;
	private int userId;
	private int len;
	
	public HeaderDecoderHandler()
	{
		super(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP);
	}
	
	@Override
	protected Message decode(ChannelHandlerContext ctx, ByteBuf pre) throws Exception
	{
		ByteBuf in = (ByteBuf)super.decode(ctx, pre);
		if (in == null)
		{
		    return null;
		}
		
		if (in.readableBytes() < Header.LENGTH)
		{
			return null;
		}
		
        version = BIGENDIAN ? in.readShort() : in.readShortLE();
        mainNo = in.readByte();
        subNo = in.readByte();
        isCompress = in.readBoolean();
        sessionId = BIGENDIAN ? in.readLong() : in.readLongLE();
        
        int tokenLen = BIGENDIAN ? in.readInt() : in.readIntLE();
        if (tokenLen > 0)
        {
            byte[] tmp = new byte[tokenLen];
            in.readBytes(tmp);
            token = new String(tmp, "UTF-8");
        }
        else
        {
        	token = null;
        }
        
        serialId = in.readByte();
        userId = BIGENDIAN ? in.readInt() : in.readIntLE();
        len = BIGENDIAN ? in.readInt() : in.readIntLE();

		if (in.readableBytes() < len)
		{
			return null;
		}

		ByteBuf buf = in.readBytes(len);
		byte[] buffer = new byte[buf.readableBytes()];
		buf.readBytes(buffer);
		Header header = new Header(version, mainNo, subNo, isCompress, sessionId, token, serialId, userId, len);
		Message message = new Message(header, buffer);
		return message;
	}
}
