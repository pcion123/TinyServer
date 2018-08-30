package com.tinybee.server.netty.server.handler.encoder;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.netty.buffer.ByteArrayBuffer;
import com.tinybee.common.netty.message.Message;
import com.tinybee.common.util.StringUtil;
import com.tinybee.server.netty.server.header.Header;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class HeaderEncoderHandler extends MessageToByteEncoder<Message>
{
	private static Logger mLogger = LoggerFactory.getLogger(HeaderEncoderHandler.class);
	
	private final static boolean BIGENDIAN = true;
	
	@Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception
	{
        Header header = message.getHeader(Header.class);
        if (BIGENDIAN)
        {
            out.writeShort(header.getVersion());
            out.writeByte(header.getMainNo());
            out.writeByte(header.getSubNo());
            out.writeBoolean(header.getIsCompress());
            out.writeLong(header.getSessionId());
            
            String token = header.getToken();
            if (!StringUtil.isNullOrEmpty(token))
            {
                byte[] tmp = token.getBytes(Charset.forName("UTF-8"));
                out.writeInt(tmp.length);
                out.writeBytes(tmp);
            }
            else
            {
            	out.writeInt(0);
            }
            
            out.writeByte(header.getSerialId());
            out.writeInt(header.getUserId());
            out.writeInt(header.getLen());
        }
        else
        {
            out.writeShortLE(header.getVersion());
            out.writeByte(header.getMainNo());
            out.writeByte(header.getSubNo());
            out.writeBoolean(header.getIsCompress());
            out.writeLongLE(header.getSessionId());
            
            String token = header.getToken();
            if (!StringUtil.isNullOrEmpty(token))
            {
                byte[] tmp = token.getBytes(Charset.forName("UTF-8"));
                out.writeIntLE(tmp.length);
                out.writeBytes(tmp);
            }
            else
            {
            	out.writeIntLE(0);
            }
            
            out.writeByte(header.getSerialId());
            out.writeIntLE(header.getUserId());
            out.writeIntLE(header.getLen());
        }
        ByteArrayBuffer buffer = message.getBuffer();
        out.writeBytes(buffer.copy());
    }
}