package com.tinybee.common.netty.buffer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinybee.common.util.JsonUtil;
import com.tinybee.common.netty.annotation.Member;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class ByteArrayBuffer
{
	private static Logger mLogger = LoggerFactory.getLogger(ByteArrayBuffer.class);
	
	private final static boolean BIGENDIAN = true;
	private final static int BUFFERSIZE = 10 * 1024;
	
	private ByteBuf mBuffer;
	
	public ByteArrayBuffer()
	{
		this(BUFFERSIZE);
	}
	
	public ByteArrayBuffer(int capacity)
	{
		this(PooledByteBufAllocator.DEFAULT.buffer(capacity), null);
	}
	
	public ByteArrayBuffer(byte[] buffer)
	{
		this(PooledByteBufAllocator.DEFAULT.buffer(buffer.length), buffer);
	}
	
	public ByteArrayBuffer(ByteBuf buffer, byte[] src)
	{
		mBuffer = buffer;
		if (src != null)
			mBuffer.writeBytes(src);
	}
	
	public int getCapacity()
	{
		return mBuffer.capacity();
	}
	public int getSpace()
	{
		return mBuffer.writableBytes();
	}
	public int getAvailable()
	{
		return mBuffer.readableBytes();
	}

	public ByteArrayBuffer writeByteArray(byte[] value)
	{
		if (value == null)
		{
			writeInt(0);
		}
		else
		{
			writeInt(value.length);
			mBuffer.writeBytes(value);
		}
		return this;
	}

	public byte[] readByteArray()
	{
		int len = readInt();
		byte[] tmp = null;
		if (len > 0)
		{
			tmp = new byte[len];
			mBuffer.readBytes(tmp);
		}
		return tmp;
	}
	
	public ByteArrayBuffer writeByte(byte value)
	{
		mBuffer.writeByte(value);
		return this;
	}

	public byte readByte()
	{
		return mBuffer.readByte();
	}

	public ByteArrayBuffer writeShort(short value)
	{
		if (BIGENDIAN)
		{
			mBuffer.writeShort(value);
		}
		else
		{
			mBuffer.writeShortLE(value);
		}
		return this;
	}

	public short readShort()
	{
		return BIGENDIAN ? mBuffer.readShort() : mBuffer.readShortLE();
	}

	public ByteArrayBuffer writeInt(int value)
	{
		if (BIGENDIAN)
		{
			mBuffer.writeInt(value);
		}
		else
		{
			mBuffer.writeIntLE(value);
		}
		return this;
	}

	public int readInt()
	{
		return BIGENDIAN ? mBuffer.readInt() : mBuffer.readIntLE();            
	}

	public ByteArrayBuffer writeLong(long value)
	{
		if (BIGENDIAN)
		{
			mBuffer.writeLong(value);
		}
		else
		{
			mBuffer.writeLongLE(value);
		}
		return this;
	}

	public long readLong()
	{
		return BIGENDIAN ? mBuffer.readLong() : mBuffer.readLongLE(); 
	}

	public ByteArrayBuffer writeFloat(float value)
	{
		if (BIGENDIAN)
		{
			mBuffer.writeFloat(value);
		}
		else
		{
			mBuffer.writeFloatLE(value);
		}
		return this;
	}

	public float readFloat()
	{
		return BIGENDIAN ? mBuffer.readFloat() : mBuffer.readFloatLE(); 
	}

	public ByteArrayBuffer writeDouble(double value)
	{
		if (BIGENDIAN)
		{
			mBuffer.writeDouble(value);
		}
		else
		{
			mBuffer.writeDoubleLE(value);
		}
		return this;
	}

	public double readDouble()
	{
		return BIGENDIAN ? mBuffer.readDouble() : mBuffer.readDoubleLE();
	}

	public ByteArrayBuffer writeChar(char value)
	{
		mBuffer.writeChar(value);
		return this;
	}

	public char readChar()
	{
		return mBuffer.readChar();
	}

	public ByteArrayBuffer writeBool(boolean value)
	{
		mBuffer.writeBoolean(value);
		return this;
	}

	public boolean readBool()
	{
		return mBuffer.readBoolean();
	}

	public ByteArrayBuffer writeString(String value)
	{
		if (value == null)
		{
			return writeByteArray(null);
		}
		else
		{
			byte[] tmp = value.getBytes(Charset.forName("UTF-8"));
			return writeByteArray(tmp);
		}
	}

	public String readString()
	{
		try
		{
			byte[] tmp = readByteArray();
			return tmp != null ? new String(tmp, "UTF-8") : null;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public ByteArrayBuffer writeDate(Date value)
	{
		return writeLong(value.getTime());
	}
	
	public Date readDate()
	{
		return new Date(readLong());
	}
	
	private Field[] sortMember(Field[] fields)
	{
		List<Field> list = Arrays.asList(fields);
		return list.stream()
		.filter(n -> n.isAnnotationPresent(Member.class))
		.sorted((a,b) -> { 
			Member m1 = a.getAnnotation(Member.class);
	    	Member m2 = b.getAnnotation(Member.class);
	        if (m1 != null && m2 != null)
	        {
	        	return m1.order() - m2.order();
	        } 
	        else if (m1 != null && m2 == null)
	        {
	        	return -1;
	        }
	        else if (m1 == null && m2 != null)
	        {
	        	return 1;
	        }
	        return a.getName().compareTo(b.getName());
		})
		.toArray(Field[]::new);
	}
	
	private ByteArrayBuffer writeValue(Class<?> clazz, Object value) throws Throwable
	{
		if (clazz == Boolean.class || clazz == boolean.class)
			return writeBool((boolean)value);
		else if (clazz == Character.class || clazz == char.class)
			return writeChar((char)value);
		else if (clazz == Byte.class || clazz == byte.class)
			return writeByte((byte)value);
		else if (clazz == Short.class || clazz == short.class)
			return writeShort((short)value);
		else if (clazz == Integer.class || clazz == int.class)
			return writeInt((int)value);
		else if (clazz == Long.class || clazz == long.class)
			return writeLong((long)value);
		else if (clazz == Float.class || clazz == float.class)
			return writeFloat((float)value);
		else if (clazz == Double.class || clazz == double.class)
			return writeDouble((double)value);
		else if (clazz == String.class)
			return writeString((String)value);
		else if (clazz == Date.class)
			return writeDate((Date)value);
		Field[] fields = sortMember(clazz.getDeclaredFields());
		for (Field field : fields)
		{
			field.setAccessible(true);
			if (field.getType().isArray())
			{
				int len = Array.getLength(field.get(value));
				for (int i = 0; i < len; i++)
				{
					writeValue(field.getType().getComponentType(), Array.get(field.get(value), i));
				}
			}
			else
			{
				writeValue(field.getType(), field.get(value));
			}
		}
		return this;
	}
	
	private Object readValue(Class<?> clazz) throws Throwable 
	{
		if (clazz == null)
			return null;

		if (clazz == Boolean.class || clazz == boolean.class)
			return new Boolean(readBool());
		else if (clazz == Character.class || clazz == char.class)
			return new Character(readChar());
		else if (clazz == Byte.class || clazz == byte.class)
			return new Byte(readByte());
		else if (clazz == Short.class || clazz == short.class)
			return new Short(readShort());
		else if (clazz == Integer.class || clazz == int.class)
			return new Integer(readInt());
		else if (clazz == Long.class || clazz == long.class)
			return new Long(readLong());
		else if (clazz == Float.class || clazz == float.class)
			return new Float(readFloat());
		else if (clazz == Double.class || clazz == double.class)
			return new Double(readDouble());
		else if (clazz == String.class)
			return new String(readString());
		else if (clazz == Date.class)
			return readDate();
		Object value = clazz.newInstance();
		Field[] fields = sortMember(clazz.getDeclaredFields());
		for (Field field : fields)
		{
			field.setAccessible(true);
			Class<?> fieldClazz = field.getType();
			if (field.getType().isArray())
			{
				Member member = field.getAnnotation(Member.class);
				Class<?> arrayClazz = field.getType().getComponentType();
				int len = member.length();
				Object array = Array.newInstance(arrayClazz, len);
				field.set(value, array);
				for (int i = 0; i < len; i++)
				{
					Array.set(array, i, readValue(arrayClazz));
				}
			}
			else
			{
				field.set(value, readValue(fieldClazz));
			}
		}
		return value;
	}
	
	public ByteArrayBuffer writeStruct(Class<?> clazz, Object value)
	{
		try
		{
			return value != null ? writeValue(clazz, value) : this;
		}
		catch (Throwable e)
		{
			mLogger.info(e.getMessage());
		}
		return this;
	}

	public <T> T readStruct(Class<T> clazz)
	{
		try
		{
			return clazz != null ? clazz.cast(readValue(clazz)) : null;
		}
		catch (Throwable e)
		{
			mLogger.info(e.getMessage());
		}
		return null;
	}
	
	public ByteArrayBuffer writeBufToJson(Object value)
	{
		return writeString(JsonUtil.obj2JsonStr(value));
	}

	public <T> T readBufToJson(Class<T> clazz)
	{
		try
		{
			return clazz != null ? clazz.cast(JsonUtil.toObject(readString(), clazz)) : null;
		}
		catch (Exception e)
		{
			mLogger.info(e.getMessage());
		}
		return null;
	}
	
	public void compress()
	{
		//TODO:壓縮
	}

	public void decompress()
	{
		//TODO:解壓縮
	}

	public void encode(byte code)
	{
		if (code == 0)
			return;
		
		int len = mBuffer.readableBytes();
		byte[] tmp = new byte[len];
		mBuffer.readBytes(tmp);
		for (int i = 0; i < len; i++)
			tmp[i] ^= code;
		mBuffer.writeBytes(tmp);
	}

	public void decode(byte code)
	{
		if (code == 0)
			return;

		int len = mBuffer.readableBytes();
		byte[] tmp = new byte[len];
		mBuffer.readBytes(tmp);
		for (int i = 0; i < len; i++)
			tmp[i] ^= code;
		mBuffer.writeBytes(tmp);
	}
	
	public byte[] copy()
	{
		ByteBuf buffer = mBuffer.copy(mBuffer.readerIndex(), mBuffer.readableBytes());
		byte[] tmp = new byte[buffer.readableBytes()];
		buffer.readBytes(tmp);
		return tmp;
	}
}
